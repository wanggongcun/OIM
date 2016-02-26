package com.time.oim.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SVDraw extends SurfaceView implements SurfaceHolder.Callback {

	private Bitmap bmp;  
    private String imgPath = "";  
    protected SurfaceHolder sh; // 专门用于控制surfaceView的  
    private int width;  
    private int height;
    private int imgwidth;  
    private int imgheight;
    private float scale = 0.0f;
    private Paint paint;
    
    
	public SVDraw(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		sh = getHolder();  
        sh.addCallback(this);  
        sh.setFormat(PixelFormat.TRANSPARENT); // 设置为透明  
        setZOrderOnTop(true);// 设置为顶端  
        scale = context.getResources().getDisplayMetrics().density;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w,
			int h) {
		// TODO Auto-generated method stub
		width = w;  
        height = h;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	private float oldx = 0.0f;
	private float oldy = 0.0f;
	private float newx = 0.0f;
	private float newy = 0.0f;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			oldx = event.getX();
			oldy = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			newx = event.getX();
			newy = event.getY();
			Canvas canvas = sh.lockCanvas();
	        canvas.drawColor(Color.TRANSPARENT);// 这里是绘制背景  
	        Paint p = new Paint(); // 笔触  
	        p.setAntiAlias(true); // 反锯齿  
	        p.setColor(Color.RED);  
	        p.setStyle(Style.STROKE);  
	        canvas.drawLine(oldx, oldy, newx, newy, p); 
	        sh.unlockCanvasAndPost(canvas);
	        
	        Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),bmp.getConfig());
			Canvas ca = new Canvas(bitmap);
			ca.drawColor(Color.TRANSPARENT);
	        ca.drawBitmap(bmp, 0, 0, p);
	        ca.drawLine(oldx*viewtoimg(), oldy*viewtoimg(), newx*viewtoimg(), newy*viewtoimg(), p);
	        bmp = bitmap;
	        
	        oldx = newx;
	        oldy = newy;
			break;
		}
		
		return super.onTouchEvent(event);
	}

	
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.draw(canvas);
	}

	private float viewtoimg(){
		return (float)imgwidth/width;
	}
	
	void clearDraw() {  
		  
        Canvas canvas = sh.lockCanvas();  
        canvas.drawColor(Color.BLUE);// 清除画布  
        sh.unlockCanvasAndPost(canvas);  
    }  
  
    /** 
     * 绘制 
     */  
    public void doDraw() {  
    	imgwidth = bmp.getWidth();
    	imgheight = bmp.getHeight();
    	int den = bmp.getDensity();
        if (bmp != null) {  
            Canvas canvas = sh.lockCanvas();  
            int dens = canvas.getDensity();
            canvas.drawColor(Color.TRANSPARENT);// 这里是绘制背景  
            Paint p = new Paint(); // 笔触  
            p.setAntiAlias(true); // 反锯齿  
            p.setColor(Color.RED);  
//            p.setStyle(Style.STROKE);  
//            canvas.drawBitmap(bmp, 0, 0, p);  
            canvas.drawLine(width / 2 - 100, 0, width / 2 - 100, height, p);  
            canvas.drawLine(width / 2 + 100, 0, width / 2 + 100, height, p);  
             
            // 提交绘制  
            sh.unlockCanvasAndPost(canvas);
            
            Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),bmp.getConfig());
			Canvas ca = new Canvas(bitmap);
			ca.drawColor(Color.TRANSPARENT);
	        ca.drawBitmap(bmp, 0, 0, p);
	        ca.drawLine(width / 2 - 100, 0, width / 2 - 100, height, p);  
            ca.drawLine(width / 2 + 100, 0, width / 2 + 100, height, p);
            
	        bmp = bitmap;
        }  
  
    }  
  
    public void drawLine() {  
  
    	Canvas canvas = sh.lockCanvas();  
    	  
        canvas.drawColor(Color.TRANSPARENT);// 这里是绘制背景  
        Paint p = new Paint(); // 笔触  
        p.setAntiAlias(true); // 反锯齿  
        p.setColor(Color.RED);  
        p.setStyle(Style.STROKE);  
        canvas.drawLine(width / 2 - 100, 0, width / 2 - 100, height, p);  
        canvas.drawLine(width / 2 + 100, 0, width / 2 + 100, height, p);  
        // 提交绘制  
        sh.unlockCanvasAndPost(canvas);  
        
        Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),bmp.getConfig());
		Canvas ca = new Canvas(bitmap);
		ca.drawColor(Color.TRANSPARENT);
        ca.drawBitmap(bmp, 0, 0, p);
        canvas.drawLine(width / 2 - 100, 0, width / 2 - 100, height, p);  
        canvas.drawLine(width / 2 + 100, 0, width / 2 + 100, height, p); 
        bmp = bitmap;
    }  
    
    
    public void drowLine(float ox,float oy,float x,float y,Paint mpaint){
    	this.oldx = ox;
    	this.oldy = oy;
    	this.newx = x;
    	this.newy = y;
    	this.paint = mpaint;
    	
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Canvas canvas = sh.lockCanvas();  
//		        canvas.drawColor(Color.TRANSPARENT);
		        canvas.drawLine(oldx, oldy, newx, newy, paint); 
		        sh.unlockCanvasAndPost(canvas);
			}
		}).start();
    	 
        
        Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),bmp.getConfig());
		Canvas ca = new Canvas(bitmap);
		ca.drawColor(Color.TRANSPARENT);
        ca.drawBitmap(bmp, 0, 0, mpaint);
        ca.drawLine(oldx*viewtoimg(), oldy*viewtoimg(), x*viewtoimg(), y*viewtoimg(), mpaint);
        bmp = bitmap;
        
    }
  
    public String getImgPath() {  
        return imgPath;  
    }  
  
    public void setImgPath(String imgPath) {  
        this.imgPath = imgPath;  
        // 根据路径载入目标图像  
        bmp = BitmapFactory.decodeFile(imgPath);  
        if (bmp != null) {  
            Canvas canvas = sh.lockCanvas();  
            canvas.drawColor(Color.TRANSPARENT);// 这里是绘制背景  
            Paint p = new Paint(); // 笔触  
            p.setAntiAlias(true); // 反锯齿  
            p.setColor(Color.RED);  
//            p.setStyle(Style.STROKE);  
            canvas.drawBitmap(bmp, 0, 0, p); 
            sh.unlockCanvasAndPost(canvas);  
        }
    }
    
	public void setImg(Bitmap bitmap){
    	bmp = bitmap;
    	if (bmp != null) {  
            Canvas canvas = sh.lockCanvas();  
            canvas.drawColor(Color.TRANSPARENT);// 这里是绘制背景  
            Paint p = new Paint(); // 笔触  
            p.setAntiAlias(true); // 反锯齿  
            p.setColor(Color.RED);  
//            p.setStyle(Style.STROKE);  
            canvas.drawBitmap(bmp, 0, 0, p); 
            sh.unlockCanvasAndPost(canvas);  
            
        }
    }
    
    public Bitmap getImg(){
    	return this.bmp;
    }

    
}
