package com.time.oim.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {

	private boolean canDrag= true;
	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void setDrag(boolean can){
		canDrag = can;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		getParent().requestDisallowInterceptTouchEvent(true);
		if(canDrag){
			return super.onInterceptTouchEvent(arg0);
		}else{
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		getParent().requestDisallowInterceptTouchEvent(true);
		if(canDrag){
			return super.onTouchEvent(arg0);
		}else{
			return false;
		}
		
	}
	

}
