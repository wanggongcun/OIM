package com.time.oim.view;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyImageButton extends LinearLayout {
	 // ----------------private attribute----------------------------- 
	  private ImageView mButtonImage = null; 
	  private TextView mButtonText = null;
	
	public MyImageButton(Context context) { 
	    super(context); 
	 
	    mButtonImage = new ImageView(context); 
	    mButtonText = new TextView(context); 
	 
	    mButtonImage.setPadding(0, 0, 0, 0); 
	 
	    setTextColor(0xFF000000); 
	    mButtonText.setPadding(0, 0, 0, 0); 
	 
	    //设置本布局的属性 
	    setClickable(true);  //可点击 
	    setFocusable(true);  //可聚焦 
	    setBackgroundResource(android.R.drawable.btn_default);  //布局才用普通按钮的背景 
	    setOrientation(LinearLayout.HORIZONTAL);  //垂直布局 
	    setGravity(Gravity.CENTER);
	    //首先添加Image，然后才添加Text 
	    //添加顺序将会影响布局效果 
	    addView(mButtonImage); 
	    addView(mButtonText); 
	  } 
	 
	  // ----------------public method----------------------------- 
	  /* 
	   * setImageResource方法 
	   */ 
	  public void setImageResource(int resId) { 
	    mButtonImage.setImageResource(resId); 
	  } 
	 
	  /* 
	   * setText方法 
	   */ 
	  public void setText(int resId) { 
	    mButtonText.setText(resId); 
	  } 
	 
	  public void setTextSize(int size) { 
		    mButtonText.setTextSize(size); 
		  } 
	  
	  public void setText(CharSequence buttonText) { 
	    mButtonText.setText(buttonText); 
	  } 
	 
	  /* 
	   * setTextColor方法 
	   */ 
	  public void setTextColor(int color) { 
	    mButtonText.setTextColor(color); 
	  } 
	 
	 
}
