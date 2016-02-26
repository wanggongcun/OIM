package com.time.oim.view;

import com.time.oim.R;

import android.R.dimen;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Scroller;

public class SlideListView extends ListView {

	private Context context;
	private int slidePosition;
	private int downX;
	private int downY;
	private int screenW;
	private View itemView;
	
	private Scroller scroller;
	private VelocityTracker velocityTracker;
	private boolean isFlash = true;
	public boolean isSlide = false;
	private int mMoveY;
	private int mDownY;
	private int mTouchSlop;
	public enum MoveDirection{
		RIGHT,LEFT;
	}
	private MoveDirection moveDirection;
	private MoveListener moveListener;
	
	private LayoutInflater mInflater; 
    private LinearLayout mHeaderLinearLayout = null;
	   
    private int mHeaderHeight;
	
	public SlideListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		screenW = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
		scroller = new Scroller(context);
		mTouchSlop = ViewConfiguration.getWindowTouchSlop();
		
		mInflater = LayoutInflater.from(context);
		mHeaderLinearLayout = (LinearLayout) mInflater.inflate(R.layout.delete_bt_item, null);
		addHeaderView(mHeaderLinearLayout);
		measureView(mHeaderLinearLayout);
		mHeaderHeight = mHeaderLinearLayout.getMeasuredHeight();
		mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getLeft(), -mHeaderHeight, mHeaderLinearLayout.getRight(), 0);    
        setSelection(1);
	}
	
	public void setIsSlide(boolean slide){
		isSlide = slide;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub

		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			
			addVelocityTracker(ev);
			if(!scroller.isFinished()){
				return super.dispatchTouchEvent(ev); 
			}
			downX = (int)ev.getX();
			downY = (int)ev.getY();
			slidePosition = pointToPosition(downX, downY);
			if(slidePosition == AdapterView.INVALID_POSITION){
				return super.dispatchTouchEvent(ev);
			}
			itemView = getChildAt(slidePosition-getFirstVisiblePosition()).findViewById(R.id.rl_item);
			break;
		case MotionEvent.ACTION_MOVE:
			if(Math.abs(getScrollVelocity())>600 
					|| (Math.abs(ev.getX()-downX)>mTouchSlop && Math.abs(ev.getY()-downY)<mTouchSlop )){
//				isSlide = true;

			}
			break;
		case MotionEvent.ACTION_UP:
			recycleVelocityTracker();
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
		if(isSlide && slidePosition!=AdapterView.INVALID_POSITION){
			addVelocityTracker(ev);
			int x = (int)ev.getX();
			switch(action){
			case MotionEvent.ACTION_DOWN:
				itemView.setBackgroundColor(Color.rgb(200, 200, 200));
				getParent().requestDisallowInterceptTouchEvent(true);
				mDownY = (int)ev.getY();
				
				break;
			case MotionEvent.ACTION_MOVE:
				mMoveY = (int)ev.getY();
				
				int deltaX = downX - x;
				if(deltaX>0){
					if(deltaX>screenW/5)
						getParent().requestDisallowInterceptTouchEvent(false);
					
				}else{
					getParent().requestDisallowInterceptTouchEvent(true);
					itemView.scrollTo(deltaX, 0);
				}

				break;
			case MotionEvent.ACTION_UP:
				
				itemView.setBackgroundColor(Color.rgb(255, 255, 255));
				getParent().requestDisallowInterceptTouchEvent(true);
				this.setPadding(this.getPaddingLeft(), 0, this.getPaddingRight(), this.getPaddingBottom());
				mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getLeft(), -mHeaderHeight, mHeaderLinearLayout.getPaddingRight(), 0);
				int velocityX = getScrollVelocity();
				if(velocityX>600){
					scrollRight();
					recycleVelocityTracker();
					isSlide = false;
				}else if(velocityX<-600){

				}else{
					scrollByDistanceX();
					recycleVelocityTracker();
					isSlide = false;
				}
				this.setFocusable(false);
				break;
			case MotionEvent.ACTION_CANCEL:
				itemView.setBackgroundColor(Color.rgb(255, 255, 255));
				break;
			}
			return true;
		}else{
			itemView.scrollTo(0, 0);
			switch(action){
			case MotionEvent.ACTION_DOWN:
				itemView.setBackgroundColor(Color.rgb(200, 200, 200));
				getParent().requestDisallowInterceptTouchEvent(false);
				mDownY = (int)ev.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				mMoveY = (int)ev.getY();
				if(Math.abs(mMoveY - mDownY)<mTouchSlop){
					break;
				}
				if(mHeaderLinearLayout.getBottom()>=0 && mHeaderLinearLayout.getBottom()<mHeaderHeight){
					mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(),
                            (int)(mMoveY - mDownY) - mHeaderHeight,
                            mHeaderLinearLayout.getPaddingRight(),
                            mHeaderLinearLayout.getPaddingBottom());
				}else if(mHeaderLinearLayout.getBottom()>=mHeaderHeight){
					mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(), (int)((mMoveY - mDownY)/3), 
							mHeaderLinearLayout.getPaddingRight(), mHeaderLinearLayout.getPaddingBottom());  
					
				}
				if((int)(mMoveY - mDownY)<0){ 
					this.setPadding(this.getPaddingLeft(), (int)(mMoveY - mDownY), this.getPaddingRight(), 0);
				}
				break;
			case MotionEvent.ACTION_UP:
				itemView.setBackgroundColor(Color.rgb(255, 255, 255));
				getParent().requestDisallowInterceptTouchEvent(true);
				this.setPadding(this.getPaddingLeft(), 0, this.getPaddingRight(), this.getPaddingBottom());
				mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getLeft(), -mHeaderHeight, mHeaderLinearLayout.getPaddingRight(), 0);
				this.setFocusable(false);
				break;
			case MotionEvent.ACTION_CANCEL:
				itemView.setBackgroundColor(Color.rgb(255, 255, 255));
				break;
			}
		}
		return super.onTouchEvent(ev);
	}
	
	
	
	@Override
	public void setAdapter(ListAdapter adapter) {
		// TODO Auto-generated method stub
		super.setAdapter(adapter);
		setSelection(1);
	}

	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		super.computeScroll();
		if(scroller.computeScrollOffset()){
			itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());
			postInvalidate();
			
			if(scroller.isFinished()){
				if(moveListener == null){
					return;
				}
				itemView.scrollTo(0, 0);
				moveListener.moveItem(moveDirection, slidePosition);
			}
		}
	}
	

	private void scrollRight(){
		moveDirection = MoveDirection.RIGHT;
		int delta = screenW + itemView.getScrollX();
		scroller.startScroll(itemView.getScrollX(), 0, -delta, 0, Math.abs(delta));
		postInvalidate();
	}
	
	private void scrollLeft(){
		moveDirection = MoveDirection.LEFT;
		int delta = screenW - itemView.getScrollX();
		scroller.startScroll(itemView.getScrollX(), 0, delta, 0, Math.abs(delta));
		postInvalidate();
	}
	
	private void scrollByDistanceX(){
		if(itemView.getScrollX() >= screenW/3){
//			scrollLeft();
		}else if(itemView.getScrollX() <= -screenW/3){
			scrollRight();
		}else{
			itemView.scrollTo(0, 0);
		}
	}

	private void addVelocityTracker(MotionEvent event){
		if(velocityTracker == null){
			velocityTracker = VelocityTracker.obtain();
		}
		velocityTracker.addMovement(event);
	}
	
	private void recycleVelocityTracker(){
		if(velocityTracker != null){
			velocityTracker.recycle();
			velocityTracker= null;
		}
	}
	
	private int getScrollVelocity(){
		velocityTracker.computeCurrentVelocity(10);
		int velocity = (int) velocityTracker.getXVelocity();
		return velocity;
	}
	
	private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    } 
	
	public void setMoveListener(MoveListener moveListener){
		this.moveListener = moveListener;
	}

	public interface MoveListener{
		public void moveItem(MoveDirection direction, int position);
	}
	

}
