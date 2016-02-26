package com.time.oim.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.EditText;

import com.time.oim.R;

public class searchEditText extends EditText{
	
	private Drawable mSearchDrawable;
	private Drawable mClearDrawable;
	private Context context;
	
	private SearchListener searchListener;
	
	public searchEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		init();
//		this.setGravity(Gravity.LEFT);
	}
	
	private void init() { 
		this.setText("");
    	//获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
		mSearchDrawable= getCompoundDrawables()[0];
    	mClearDrawable = getCompoundDrawables()[2];
    	if (mSearchDrawable == null) { 
        	mSearchDrawable = getResources() 
                    .getDrawable(R.drawable.ic_search_black_24dp); 
        } 
    	mSearchDrawable.setBounds(0, 0, mSearchDrawable.getIntrinsicWidth(), mSearchDrawable.getIntrinsicHeight()); 
        if (mClearDrawable == null) { 
        	mClearDrawable = getResources() 
                    .getDrawable(R.drawable.emotionstore_progresscancelbtn); 
        } 
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight()); 
        setSearchIconVisible(true);
        setClearIconVisible(false); 
        
    } 
	public void setSearchIconVisible(boolean visible) { 
        Drawable left = visible ? mSearchDrawable : null; 
        setCompoundDrawables(left ,getCompoundDrawables()[1],
        		getCompoundDrawables()[2], getCompoundDrawables()[3]); 
    } 
	public void setClearIconVisible(boolean visible) { 
	        Drawable right = visible ? mClearDrawable : null; 
	        setCompoundDrawables(getCompoundDrawables()[0], 
	                getCompoundDrawables()[1], right, getCompoundDrawables()[3]); 
	} 
	
	
	
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		// TODO Auto-generated method stub
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
//		if(focused){
//			this.setGravity(Gravity.LEFT);
//		}else{
//			this.setGravity(Gravity.CENTER);
//		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (getCompoundDrawables()[2] != null) { 
            if (event.getAction() == MotionEvent.ACTION_UP) { 
            	boolean touchable = event.getX() > (getWidth() 
                        - getPaddingRight() - mClearDrawable.getIntrinsicWidth()) 
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) { 
                    this.setText(""); 
                } 
            } 
        } 
		if (getCompoundDrawables()[0] != null) { 
            if (event.getAction() == MotionEvent.ACTION_UP) { 
            	boolean touchable = event.getX() > (getPaddingRight()) 
                        && (event.getX() < ((mSearchDrawable.getIntrinsicWidth() + getPaddingRight())));
                if (touchable) { 
//                    Toast.makeText(context, getText().toString(), Toast.LENGTH_SHORT).show();
                	if(searchListener != null)
                		searchListener.search();
                } 
            } 
        } 
		return super.onTouchEvent(event);
	}



	
	public void setSearchListener(SearchListener searchListener){
		this.searchListener = searchListener;
	}

	public interface SearchListener{
		public void search();
	}

}
