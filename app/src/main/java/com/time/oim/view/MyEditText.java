package com.time.oim.view;

import android.app.Activity;
import android.content.Context;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class MyEditText extends EditText {

	private Activity activity= null;
	private MyTextView tv_text = null;
	
	public MyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void setActivity(Activity a){
		this.activity = a;
	}
	public void setTextView(MyTextView tv){
		tv_text = tv;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_ENTER){

			InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(this.getWindowToken(), InputMethodManager.RESULT_SHOWN);
			
			tv_text.setText(this.getText().toString());
			this.setText("");
			this.setVisibility(View.GONE);
			tv_text.drag();
		}else if(keyCode == KeyEvent.KEYCODE_BACK){
			this.setVisibility(View.GONE);
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_CLEAR){
			this.setVisibility(View.GONE);
		}
		return false;
	}

	
	
	

}
