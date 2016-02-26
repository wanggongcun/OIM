package com.time.oim;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.time.oim.manager.FontManager;
import com.time.oim.util.ActivityUtil;
import com.time.oim.util.Constant;

public class AccountActivity extends Activity {

	private ImageButton ibt_back;
	private TextView tv_account;
	private TextView tv_myname;
	private TextView tv_myphone;
	private TextView tv_birthday;
	private TextView tv_email;
	private Switch sw_notify;
	private ImageView iv_password;
	private ImageView iv_about;
	private Button bt_login;
	
	private String username;
	private String phonenum;
	private String id;
	private String mybirthday;
	private String myemail;
	private SharedPreferences preferences;

	private static final int RESULT_LOGIN_SUCCESS_TO_MAIN = 11;//
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_account);
		
		preferences = getSharedPreferences(Constant.LOGIN_SET, 0);
		
		username = ActivityUtil.getSharedPreferences(AccountActivity.this,Constant.USERNAME);
		id = username;
		phonenum = ActivityUtil.getSharedPreferences(AccountActivity.this,Constant.PHONE_NUM);
		mybirthday = ActivityUtil.getSharedPreferences(AccountActivity.this,Constant.BIRTHDAT);
		myemail = ActivityUtil.getSharedPreferences(AccountActivity.this,Constant.EMAIL);
		
		ibt_back = (ImageButton)findViewById(R.id.ibt_back);
		ibt_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.go_in_bottom, R.anim.go_out_top);
			}
		});
		
		tv_account = (TextView) findViewById(R.id.tv_account);
		tv_account.setText(username);
		
		tv_myname = (TextView) findViewById(R.id.tv_myname);
		tv_myname.setText(id);
		tv_myname.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Toast.makeText(AccountActivity.this, "手机号码", Toast.LENGTH_SHORT).show();
//				VCard vCard = new VCard();
//				vCard.setPhoneHome("oim.phonenum", "15273153715");
//				XmppConnectionManager.getInstance().saveUserVCard(vCard);
				Intent it = new Intent(AccountActivity.this,ModifyNumActivity.class);
				it.putExtra("modify_type", "name");
				startActivityForResult(it, 1);
			}
		});
		
		tv_myphone = (TextView) findViewById(R.id.tv_myphone);
		tv_myphone.setText(phonenum);
		tv_myphone.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Toast.makeText(AccountActivity.this, "手机号码", Toast.LENGTH_SHORT).show();
//				VCard vCard = new VCard();
//				vCard.setPhoneHome("oim.phonenum", "15273153715");
//				XmppConnectionManager.getInstance().saveUserVCard(vCard);
				Intent it = new Intent(AccountActivity.this,ModifyNumActivity.class);
				it.putExtra("modify_type", "phone");
				startActivityForResult(it, 1);
			}
		});
		
		tv_birthday = (TextView) findViewById(R.id.tv_birthday);
		tv_birthday.setText(mybirthday);
		tv_birthday.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(AccountActivity.this, "生日", Toast.LENGTH_SHORT).show();
//				VCard vCard = new VCard();
//				vCard.setPhoneHome("oim.phonenum", "15273153715");
//				XmppConnectionManager.getInstance().saveUserVCard(vCard);
				Intent it = new Intent(AccountActivity.this,ModifyNumActivity.class);
				it.putExtra("modify_type", "birthday");
				startActivityForResult(it, 1);
			}
		});
		
		tv_email = (TextView) findViewById(R.id.tv_email);
		tv_email.setText(myemail);
		tv_email.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Toast.makeText(AccountActivity.this, "邮箱", Toast.LENGTH_SHORT).show();
//				VCard vCard = new VCard();
//				vCard.setPhoneHome("oim.phonenum", "15273153715");
//				XmppConnectionManager.getInstance().saveUserVCard(vCard);
				Intent it = new Intent(AccountActivity.this,ModifyNumActivity.class);
				it.putExtra("modify_type", "email");
				startActivityForResult(it, 1);
			}
		});
		
		sw_notify = (Switch) findViewById(R.id.sw_notify);
		if(ActivityUtil.getSharedPreferences(AccountActivity.this, Constant.OIM_ACTION).equals("1")){
			sw_notify.setChecked(true);
		}
		sw_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					Toast.makeText(AccountActivity.this, "打开", Toast.LENGTH_SHORT).show();
					ActivityUtil.saveSharedPreferences(AccountActivity.this,Constant.OIM_ACTION, "1");
				}else{
					Toast.makeText(AccountActivity.this, "关闭", Toast.LENGTH_SHORT).show();
					ActivityUtil.saveSharedPreferences(AccountActivity.this,Constant.OIM_ACTION, "0");
				}
			}
		});
		
		iv_password = (ImageView) findViewById(R.id.iv_password);
		iv_password.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(AccountActivity.this, ModifyPwdActivity.class);
				startActivity(it);
			}
		});
		
		iv_about = (ImageView)findViewById(R.id.iv_about);
		iv_about.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(AccountActivity.this,AboutActivity.class);
				startActivityForResult(it, 1);
			}
		});
		
		bt_login = (Button)findViewById(R.id.bt_login);
		bt_login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(AccountActivity.this,LoginActivity.class);
				it.putExtra("relogin", true);
				startActivityForResult(it, 1);
//				finish();
			}
		});
		
//		FontManager.changeFonts((ViewGroup)findViewById(R.id.toplayout).getParent(), AccountActivity.this);
	
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.go_in_bottom, R.anim.go_out_top);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == 1){
			username = preferences.getString(Constant.USERNAME, "");
			id = username;
			phonenum = preferences.getString(Constant.PHONE_NUM, "");
			mybirthday = preferences.getString(Constant.BIRTHDAT, "");
			myemail = preferences.getString(Constant.EMAIL, "");
			tv_account.setText(username);
			tv_myname.setText(id);
			tv_myphone.setText(phonenum);
			tv_birthday.setText(mybirthday);
			tv_email.setText(myemail);
		}else if(resultCode == RESULT_LOGIN_SUCCESS_TO_MAIN){
//			Intent intent = new Intent(AccountActivity.this,MainActivity.class);
			setResult(RESULT_LOGIN_SUCCESS_TO_MAIN);
			finish();
		}
	}
}
