package com.time.oim.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.time.oim.util.ImageUtil;
import com.time.oim.view.MyImageView.MyAsyncTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.VideoView;

public class MyVideoView extends VideoView {
	
	private Activity activity;
	private List<Paint> mPaints = new ArrayList<Paint>();
	private Paint mPaint = null;
	private List<Path> paths = new ArrayList<Path>();
	private Path path = null;
	private Map<String, Bitmap> texts = new HashMap<String, Bitmap>();
	private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	private Bitmap mBitmap;
	private MyAsyncTask myAsyncTask;// 异步动画
	private boolean canDraw = false;

	private int start_Top=-1,start_Right=-1,start_Bottom=-1,start_Left=-1;//初始位置
	private int screen_W,screen_H;//屏幕宽高
	private int oldX = 0;
	private int oldY = 0;
	private int tx = 0;
	private int ty = 0;
	private int img_position = 0;
	
	public MyVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void clear(){
		mPaints.clear();
		paths.clear();
		texts.clear();
	}
	
	public void setBitmap(Bitmap bitmap){
		mBitmap = bitmap;
		this.bitmaps.clear();
		this.bitmaps.add(bitmap);
		this.bitmaps.add(ImageUtil.toGrayscale(bitmap));
		this.bitmaps.add(ImageUtil.toBannerFilter(bitmap));
		this.bitmaps.add(ImageUtil.toBlackWhiteFilter(bitmap));
		this.bitmaps.add(ImageUtil.toRadialDisFilter(bitmap));
	}
	
	public void setActivity(Activity a){
		this.activity = a;
	}
	
	public void setScreenWH(int w,int h){
		this.screen_W = w;
		this.screen_H = h;
	}
	
	public void setDraw(boolean draw){
		this.canDraw = draw;
	}
	
	public void setPaint(Paint paint){
		mPaint = null;
		mPaint = new Paint();
		
		mPaint.setColor(paint.getColor());
		mPaint.setStyle(Style.STROKE); 
        mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(5);
	}
	
	public int doback(){
		if(paths.size()>0){
			int num = paths.size()-1;
			paths.remove(num);
			mPaints.remove(num);
			if(texts.containsKey(String.valueOf(num))){
				texts.remove(String.valueOf(num));
			}
			invalidate();
		}
		return paths.size();
	}

	public void drawBitmap(Bitmap bm){
		path = null;
		path = new Path();
		paths.add(path);
		mPaints.add(mPaint);
		int textloca = paths.size()-1;
		texts.put(String.valueOf(textloca), bm);
		invalidate();
		
	}
	
	@Override
	public void setVideoPath(String path) {
		// TODO Auto-generated method stub
		super.setVideoPath(path);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		if(start_Top == -1){
			start_Top = this.getTop();
			start_Bottom = this.getBottom();
			start_Left = this.getLeft();
			start_Right = this.getRight();
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		
		super.onDraw(canvas);
//		canvas.drawBitmap(mBitmap, 0, 0, mPaint);
		int path_len = paths.size();
		for(int i=0;i<path_len;i++){
			if(texts.containsKey(String.valueOf(i))){
				
				try {
					
					canvas.drawBitmap(texts.get(String.valueOf(i)), 0, 0, mPaint);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}else{
				canvas.drawPath(paths.get(i), mPaints.get(i));
			}
			
		}
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		getParent().requestDisallowInterceptTouchEvent(true);
		if(canDraw){
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				onTouchDown(event);
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				onPointerDown(event);
				break;
			case MotionEvent.ACTION_MOVE:
				onTouchMove(event);
				
				break;
			case MotionEvent.ACTION_UP:
				onTouchUp(event);
				break;
			case MotionEvent.ACTION_POINTER_UP:
				
				break;
			}
		}else{
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				oldX = (int) event.getX();
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				
				break;
			case MotionEvent.ACTION_MOVE:
				tx = (int) event.getX();
				if(Math.abs(tx-oldX)>50){
					int distance_w = this.getWidth() * Math.abs(tx-oldX)/screen_W;
					int distance_h = this.getHeight() * Math.abs(tx-oldX)/screen_H;
					
//					this.setFrame(start_Left+distance_w/4, start_Top+distance_h/4, start_Right-distance_w/4, start_Bottom-distance_h/4);
					this.setLeft(start_Left+distance_w/4);
					this.setTop(start_Top+distance_h/4);
					this.setRight(start_Right-distance_w/4);
					this.setBottom(start_Bottom-distance_h/4);
				}
				break;
			case MotionEvent.ACTION_UP:
				if(Math.abs(tx-oldX)>50){
					if(tx>oldX){
						try {
							if(img_position == 4){
								mBitmap = bitmaps.get(img_position);
								img_position = 0;
							}else{
								mBitmap = bitmaps.get(img_position);
								img_position++;
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
						
					}else{
						mBitmap = bitmaps.get(0);
					}
					doScaleAnim();
				}
				break;
			case MotionEvent.ACTION_POINTER_UP:
				
				break;
			}
		}
		
//		return super.onTouchEvent(event);
		return true;
	}

	private void onTouchDown(MotionEvent event){
		path = null;
		path = new Path();
		paths.add(path);
		mPaints.add(mPaint);
		int x = (int)event.getX();
		int y = (int)event.getY();
		paths.get(paths.size()-1).moveTo(x, y);
	}
	
	private void onPointerDown(MotionEvent event){
		
	}

	private void onTouchMove(MotionEvent event){
		if(path == null)
			return;
		int x = (int)event.getX();
		int y = (int)event.getY();
		paths.get(paths.size()-1).lineTo(x, y);
		invalidate();
		paths.get(paths.size()-1).moveTo(x, y);
		int path_len;
		path_len = paths.size();
		int path_l;
	}
	
	private void onTouchUp(MotionEvent event){
		if(path == null)
			return;
		int x = (int)event.getX();
		int y = (int)event.getY();
		paths.get(paths.size()-1).lineTo(x, y);
		invalidate();
	}
	
	public void drawpaths(){
		Canvas canvas = this.getHolder().lockCanvas();
		if(canvas==null)
			return;
		int path_len = paths.size();
		for(int i=0;i<path_len;i++){
			canvas.drawPath(paths.get(i), mPaints.get(i));
		}
		canvas.drawText("this is a text", 100, 100, mPaint);
		this.getHolder().unlockCanvasAndPost(canvas);
		
	}
	
	
	/***
	 * 缩放动画处理
	 */
	public void doScaleAnim() {
		myAsyncTask = new MyAsyncTask(screen_W, this.getWidth(),
				this.getHeight());
		myAsyncTask.setLTRB(this.getLeft(), this.getTop(), this.getRight(),
				this.getBottom());
		myAsyncTask.execute();
	}

	/***
	 * 回缩动画執行
	 */
	class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
		private int screen_W, current_Width, current_Height;

		private int left, top, right, bottom;

		private float scale_WH;// 宽高的比例

		/** 当前的位置属性 **/
		public void setLTRB(int left, int top, int right, int bottom) {
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}

		private float STEP = 8f;// 步伐

		private float step_H, step_V;// 水平步伐，垂直步伐

		public MyAsyncTask(int screen_W, int current_Width, int current_Height) {
			super();
			this.screen_W = screen_W;
			this.current_Width = current_Width;
			this.current_Height = current_Height;
			scale_WH = (float) current_Height / current_Width;
			step_H = STEP;
			step_V = scale_WH * STEP;
		}

		@Override
		protected Void doInBackground(Void... params) {

			while (current_Width <= screen_W) {

				left -= step_H;
				top -= step_V;
				right += step_H;
				bottom += step_V;

				current_Width += step_H;

				left = Math.max(left, start_Left);
				top = Math.max(top, start_Top);
				right = Math.min(right, start_Right);
				bottom = Math.min(bottom, start_Bottom);
				onProgressUpdate(new Integer[] { left, top, right, bottom });
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(final Integer... values) {
			super.onProgressUpdate(values);
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
//					setFrame(values[0], values[1], values[2], values[3]);
					setLeft(values[0]);
					setTop(values[1]);
					setRight(values[2]);
					setBottom(values[3]);
					
				}
			});

		}

	}
}
