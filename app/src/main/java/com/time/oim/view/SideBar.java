package com.time.oim.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class SideBar extends View {
	// 触摸事件
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	// 26个字母
	public static String[] b = { "D","1", "2", "3", "4", "5", "6", "7", "W", "1",
			"2", "3", "4", "M" , "#"};
	public static String[] bb = { "0","1", "2", "3", "4", "5", "6", "7", "8", "8",
		"15", "22", "29", "31" , "61"};
	private int choose = -1;// 选中
	private Paint paint = new Paint();
	private TextView mTextDialog;
	public void setTextView(TextView mTextDialog) {
		this.mTextDialog = mTextDialog;
	}
	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}
	
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		getParent().requestDisallowInterceptTouchEvent(true);
		int y = (int) event.getY();
		int oldChoose = choose;
		OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		int c = (int) (y * b.length/ getHeight());
		switch(event.getAction()){
		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c) {
				if (c >= 0 && c < b.length) {
					if (listener != null) {
						listener.onTouchingLetterChanged(bb[c]);
					}
					if (mTextDialog != null) {
						mTextDialog.setText(b[c]);
						mTextDialog.setVisibility(View.VISIBLE);
					}
					choose = c;
//					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			choose = -1;//
//			invalidate();
			if (mTextDialog != null) {
				mTextDialog.setVisibility(View.INVISIBLE);
			}
			break;
		}
		return true;
//		return super.onTouchEvent(event);
	}
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent event) {
//		// TODO Auto-generated method stub
//		final int action = event.getAction();
//		final float y = event.getY();// 点击y坐标
//		final int oldChoose = choose;
//		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
//		final int c = (int) (y / getHeight() * b.length);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.
//
//		switch (action) {
//		case MotionEvent.ACTION_UP:
////			setBackgroundDrawable(new ColorDrawable(0x00000000));
//			choose = -1;//
//			invalidate();
//			if (mTextDialog != null) {
//				mTextDialog.setVisibility(View.INVISIBLE);
//			}
//			break;
//
//		default:
////			setBackgroundDrawable(new ColorDrawable(0x00FFFFFF));
//			if (oldChoose != c) {
//				if (c >= 0 && c < b.length) {
//					if (listener != null) {
//						listener.onTouchingLetterChanged(bb[c]);
//					}
//					if (mTextDialog != null) {
//						mTextDialog.setText(b[c]);
//						mTextDialog.setVisibility(View.VISIBLE);
//					}
//					choose = c;
//					invalidate();
//				}
//			}
//
//			break;
//		}
//		return true;
//	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		// 获取焦点改变背景颜色.
		int height = getHeight();// 获取对应高度
		int width = getWidth(); // 获取对应宽度
		int singleHeight = height / b.length;// 获取每一个字母的高度

		for (int i = 0; i < b.length; i++) {
			paint.setColor(Color.parseColor("#34AADC"));
//			paint.setTypeface(Typeface.DEFAULT_BOLD);
//			paint.setAntiAlias(true);
			paint.setTextSize(30);
			// 选中的状态
			if (i == choose) {
				paint.setColor(Color.parseColor("#FFFFFF"));
				paint.setFakeBoldText(true);
			}
			// x坐标等于中间-字符串宽度的一半.
			float xPos = width / 2 - paint.measureText(b[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(b[i], xPos, yPos, paint);
			if(i > 0){
				canvas.drawText(".", width / 2 - paint.measureText(".") / 2, 
						singleHeight * i + singleHeight - paint.getFontMetrics().bottom + paint.getFontMetrics().top, paint);
			}
//			paint.reset();// 重置画笔
		}
	}

	/**
	 * 向外公开的方法
	 * 
	 * @param onTouchingLetterChangedListener
	 */
	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	/**
	 * 接口
	 * 
	 * @author coder
	 * 
	 */
	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

}
