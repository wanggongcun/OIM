package com.time.oim.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MyTextView extends TextView {

	private Activity activity;
	private EditText et_text;
	private FrameLayout fl_text;
	private int startx;
	private int starty;
	private int endx;
	private int endy;
	private int inx;
	private int iny;
	
	private int screenHeight;
	private int screenWidth;
	
	private static final int NONE = 0;  
    private static final int DRAG = 1;  
    private static final int ZOOM = 2;  
    int mode = NONE;
    float oldRotation = 0;
    float oldDist = 1f;
    float dist= 1f;
    
    int starttime = -1;
    int endtime = -1;
    int uptime = -1;
    
    PointF pointf = null;
    
	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		getParent().requestDisallowInterceptTouchEvent(true);
		LayoutParams params = this.getLayoutParams();
		params.width = (int) (this.getTextSize() * this.getText().length()+20);
		this.setLayoutParams(params);
		
		inx = (int) event.getX();
		iny = (int) event.getY();
		endx = (int) event.getRawX();
		endy = (int) event.getRawY();
		uptime = -1;
		endtime = (int)System.currentTimeMillis();
		switch(event.getAction() & MotionEvent.ACTION_MASK){
		case MotionEvent.ACTION_DOWN:
			mode = DRAG;
			startx = (int) event.getRawX();
			starty = (int) event.getRawY();
			if(starttime != -1){
				if(endtime-starttime<500){
					InputShow();
					starttime = -1;
				    endtime = -1;
				}else{
					starttime = endtime;
				}
			}else{
				starttime = (int)System.currentTimeMillis();
			}
			
			break;
		case MotionEvent.ACTION_MOVE:
			onTouchMove(event);
			break;
		case MotionEvent.ACTION_UP:
			mode = NONE;
			uptime = (int)System.currentTimeMillis();
			if(uptime-starttime>200){
				
				
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mode = ZOOM;
			oldRotation = rotation(event);
			oldDist = spacing(event);
			dist = spacing(event);
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		}

//		return false;
		return super.onTouchEvent(event);
	}

	private void onTouchMove(MotionEvent event){
		if(mode == DRAG){
			int xx = (int) (this.getX()+endx-startx);
			int yy = (int) (this.getY()+endy-starty);
			if(xx<10-this.getTextSize()*this.getText().length()){
				xx = (int) (10-this.getTextSize()*this.getText().length());
			}else if(xx > screenWidth-10){
				xx = screenWidth-10;
			}
			if(yy<10-this.getTextSize()){
				yy = (int) (10-this.getTextSize());
			}else if(yy > screenHeight - 10){
				yy = screenHeight- 10;
			}
			setXY(xx,yy);
			startx = endx;starty = endy;
		}else if(mode == ZOOM){
			float rotation = rotation(event) - oldRotation;
			
			this.setRotation(this.getRotation() + rotation);
			
			dist = spacing(event);
			float ss = px2dip(this.getContext(), this.getTextSize());
			if((ss + (dist - oldDist)/2)>10){
				this.setTextSize(ss + (dist - oldDist)/2);
			}else{
				this.setTextSize(10);
			}
			
			oldDist= dist;
			
			
		}else{
			
		}
	}
	
	public void init(){
		this.setXY(screenWidth/2, screenHeight/2);
		this.setRotation(0f);
		this.setTextSize(dip2px(this.getContext(), 40));
	}
	
	public void drag(){
		LayoutParams params = this.getLayoutParams();
		params.width = (int) (this.getTextSize() * this.getText().length());
		
		this.setLayoutParams(params);
		
	}
	
	public void InputShow(){
//		this.setRotation(0);
		fl_text.setVisibility(View.VISIBLE);
//		et_text.setVisibility(View.VISIBLE);
		et_text.setText(this.getText().toString());
		et_text.setSelection(et_text.getText().length());
		InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		im.showSoftInput(this.et_text, InputMethodManager.RESULT_UNCHANGED_SHOWN);
		et_text.requestFocus();
	}
	
	// 触碰两点间距离  
    private float spacing(MotionEvent event) {  
        float x = event.getX(0) - event.getX(1);  
        float y = event.getY(0) - event.getY(1);  
        return FloatMath.sqrt(x * x + y * y);  
    }  
      
    // 取手势中心点  
    private void midPoint(PointF point, MotionEvent event) {  
        float x = event.getX(0) + event.getX(1);  
        float y = event.getY(0) + event.getY(1);  
        point.set(x / 2, y / 2);  
    }  
  
    // 取旋转角度  
    private float rotation(MotionEvent event) {  
        double delta_x = (event.getX(0) - event.getX(1));  
        double delta_y = (event.getY(0) - event.getY(1));  
        double radians = Math.atan2(delta_y, delta_x);  
        return (float) Math.toDegrees(radians);  
    }
	
	public void setSize(float size){
		this.setTextSize(size);
	}
	
	public void setColor(int color){
		this.setTextColor(color);
	}
	
	public void setRotation(int rotation){
		this.setRotation(rotation);
	}
	
	public void setXY(float x,float y){
		this.setX(x);
		this.setY(y);
	}
	
	public void setActivity(Activity a){
		this.activity = a;
	}
	
	public void setEditText(FrameLayout fl,EditText et){
		this.fl_text = fl;
		this.et_text = et;
	}
	
	public void setScreenWH(int width,int height){
		this.screenHeight = height;
		this.screenWidth = width;
	}
	
	public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    } 
    
    float oldx;
    float oldy;
    float oldr;
    public void TextViewtoEditText(){
    	oldx = this.getX();
    	oldy = this.getY();
    	oldr = this.getRotation();
    	AnimationSet set = new AnimationSet(true);
    	
//    	RotateAnimation rotate = new RotateAnimation(
//    			0f, -this.getRotation(), 1f, 0f);
//    	rotate.setDuration(1000);
//    	set.addAnimation(rotate);
    	
//    	TranslateAnimation translate = new TranslateAnimation(
//    			0f,this.et_text.getX()-this.getX(),0f,this.et_text.getY()-this.getY());
    	TranslateAnimation translate = new TranslateAnimation(
    			this.et_text.getX(),this.getX(),this.et_text.getY(),this.getY());
    	translate.setDuration(2000);
    	set.addAnimation(translate);
    	set.setFillAfter(true);
    	set.setFillBefore(false);
    	set.setAnimationListener(new MyAnimationListerer(this,this.et_text,true));
    	this.startAnimation(set);
    }
    
    public void EditTexttoTextVIew(){
    	AnimationSet set = new AnimationSet(true);
//    	RotateAnimation rotate = new RotateAnimation(
//    			0, oldr, 1f, 0f);
//    	rotate.setDuration(1000);
//    	set.addAnimation(rotate);
    	
    	TranslateAnimation translate = new TranslateAnimation(
    			oldx, this.et_text.getX(), oldy, this.et_text.getY());
    	translate.setDuration(2000);
    	set.addAnimation(translate);
    	set.setFillAfter(true);
    	this.startAnimation(set);
    }
    
    public class MyAnimationListerer implements Animation.AnimationListener{
    	
    	private TextView tv_text;
    	private EditText et_text;
    	private boolean fromto;
    	public MyAnimationListerer(TextView tv,EditText et,boolean fromto){
    		this.tv_text = tv;
    		this.et_text = et;
    		this.fromto = fromto;
    	}
    	
		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			if(fromto){
				InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
				im.showSoftInput(this.et_text, 0);
				et_text.setVisibility(View.VISIBLE);
				et_text.setSelection(et_text.getText().length());
//				tv_text.setX(et_text.getX());
//				tv_text.setY(et_text.getY());
			}else{
				
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

			
			
		}
    	
    }
}
