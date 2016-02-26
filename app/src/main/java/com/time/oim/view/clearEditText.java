package com.time.oim.view;

import com.time.oim.R;
import com.time.oim.view.SlideListView.MoveDirection;
import com.time.oim.view.SlideListView.MoveListener;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.Toast;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;

public class clearEditText extends EditText{
	
	private Drawable mSearchDrawable;
	private Drawable mClearDrawable;
	private Context context;
	
	private SearchListener searchListener;
	private ClearListener clearListener;
	
	public clearEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		init();
	}
	
	private void init() { 
		this.setText("");
    	//获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
		mSearchDrawable= getCompoundDrawables()[0];
    	mClearDrawable = getCompoundDrawables()[2];
    	if (mSearchDrawable == null) { 
        	mSearchDrawable = getResources() 
                    .getDrawable(R.drawable.search_bar_icon_normal); 
        } 
    	mSearchDrawable.setBounds(0, 0, mSearchDrawable.getIntrinsicWidth(), mSearchDrawable.getIntrinsicHeight()); 
        if (mClearDrawable == null) { 
        	mClearDrawable = getResources() 
                    .getDrawable(R.drawable.emotionstore_progresscancelbtn); 
        } 
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight()); 
        setSearchIconVisible(false);
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
		if(focused){
			this.setGravity(Gravity.LEFT);
//			setClearIconVisible(true);
		}else{
			this.setGravity(Gravity.CENTER);
//			setClearIconVisible(false);
		}
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
                    if(clearListener != null){
                    	clearListener.clear();
                    }
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


	public void setClearListener(ClearListener clearListener){
		this.clearListener = clearListener;
	}

	public interface ClearListener{
		public void clear();
	}
	
	public void setSearchListener(SearchListener searchListener){
		this.searchListener = searchListener;
	}

	public interface SearchListener{
		public void search();
	}

}
