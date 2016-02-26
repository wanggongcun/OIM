package com.time.oim.view;

import com.time.oim.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Scroller;

public class HideListView extends ListView{

	private Context mContext;
	private View itemView;
	private View timeView;
	private Scroller scroller;
	private int mDownX;
	private int mDownY;
	private int mMoveX;
	private int mMoveY;
	
	public HideListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
//		mContext = context;
//		scroller = new Scroller(context);
	}

	public void allViewMove(int where){
		int startpos = this.getFirstVisiblePosition();
		int endpos = this.getLastVisiblePosition();
		for(int i=startpos;i<=endpos;i++){
			View view = getChildAt(i-getFirstVisiblePosition()).findViewById(R.id.rl_item);
			view.setX(where);
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			
			mDownX = (int)ev.getX();
			mDownY = (int)ev.getY();
			int slidePosition = pointToPosition(mDownX, mDownY);
			if(slidePosition == AdapterView.INVALID_POSITION){
				return super.dispatchTouchEvent(ev);
			}
			itemView = getChildAt(slidePosition-getFirstVisiblePosition()).findViewById(R.id.rl_item);
//			timeView = getChildAt(slidePosition-getFirstVisiblePosition()).findViewById(R.id.tv_time);
			break;
		case MotionEvent.ACTION_MOVE:
			
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return super.dispatchTouchEvent(ev);
	}


	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.requestFocus();
		int action = ev.getAction();
		
			switch(action){
			case MotionEvent.ACTION_DOWN:
				mDownX = (int)ev.getX();
				
				break;
			case MotionEvent.ACTION_MOVE:
				mMoveX = (int)ev.getX();
				if(mMoveX-mDownX>50 && mMoveX-mDownX<=200){
//					itemView.setX(mMoveX-mDownX);
					allViewMove(mMoveX-mDownX);
				}else if(mMoveX-mDownX>200){
//					itemView.setX(150);
					allViewMove(150);
				}
				

				break;
			case MotionEvent.ACTION_UP:
//				itemView.setX(0);
				allViewMove(0);
				break;
			case MotionEvent.ACTION_CANCEL:
//				itemView.setX(0);
				allViewMove(0);
				
				break;
			}
//		return true;
		
		return super.onTouchEvent(ev);
	}

	
}
