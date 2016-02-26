package com.time.oim;

import org.jivesoftware.smackx.packet.VCard;

import com.time.oim.manager.XmppConnectionManager;
import com.time.oim.util.ActivityUtil;
import com.time.oim.util.Constant;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyNumActivity extends Activity {

	private ImageButton ibt_back;
	private TextView tv_title;
	
	private LinearLayout ll_name;
	private EditText et_name;
	private Button bt_modify_name;
	private LinearLayout ll_phone;
	private EditText et_phonenum;
	private Button bt_modify_phone;
	private LinearLayout ll_email;
	private EditText et_email;
	private Button bt_modify_email;
	private LinearLayout ll_birthday;
	private DatePicker dp_birthday;
	private Button bt_modify_birthday;
	
	private String phonenum;
	private String email;
	private String birthday;
	private SharedPreferences preferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_modify_num);

//		preferences = getSharedPreferences(Constant.LOGIN_SET, 0);
//		phonenum = preferences.getString(Constant.PHONE_NUM, "");
//		email = preferences.getString(Constant.EMAIL, "");
//		birthday = preferences.getString(Constant.BIRTHDAT, ""); 
		phonenum = ActivityUtil.getSharedPreferences(ModifyNumActivity.this,Constant.PHONE_NUM);
		birthday = ActivityUtil.getSharedPreferences(ModifyNumActivity.this,Constant.BIRTHDAT);
		email = ActivityUtil.getSharedPreferences(ModifyNumActivity.this,Constant.EMAIL);
		
		ibt_back = (ImageButton)findViewById(R.id.ibt_back);
		ibt_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		tv_title = (TextView)findViewById(R.id.tv_title);
		
		ll_name = (LinearLayout)findViewById(R.id.ll_name);
		et_name = (EditText)findViewById(R.id.et_name);
		bt_modify_name = (Button) findViewById(R.id.bt_modify_name);
		
		ll_phone = (LinearLayout)findViewById(R.id.ll_phone);
		et_phonenum = (EditText)findViewById(R.id.et_phonenum);
		et_phonenum.setText(phonenum);
		bt_modify_phone = (Button) findViewById(R.id.bt_modify_phone);
		bt_modify_phone.setOnClickListener(listener);
		
		ll_email = (LinearLayout)findViewById(R.id.ll_email);
		et_email = (EditText)findViewById(R.id.et_email);
		et_email.setText(email);
		bt_modify_email = (Button) findViewById(R.id.bt_modify_email);
		bt_modify_email.setOnClickListener(listener);
		
		ll_birthday = (LinearLayout)findViewById(R.id.ll_birthday);
		dp_birthday = (DatePicker)findViewById(R.id.dp_birthday);
		if(birthday!=""){
			if(birthday.split("-").length>2){
				dp_birthday.init(Integer.valueOf(birthday.split("-")[0]), Integer.valueOf(birthday.split("-")[1]),
						Integer.valueOf(birthday.split("-")[2]), null);
			}
		}
		bt_modify_birthday = (Button) findViewById(R.id.bt_modify_birthday);
		bt_modify_birthday.setOnClickListener(listener);
		
		
		if(getIntent().hasExtra("modify_type")){
			if(getIntent().getStringExtra("modify_type").equals("phone")){
				tv_title.setText("修改手机号");
				ll_name.setVisibility(View.GONE);
				ll_phone.setVisibility(View.VISIBLE);
				ll_birthday.setVisibility(View.GONE);
				ll_email.setVisibility(View.GONE);
			}else if(getIntent().getStringExtra("modify_type").equals("birthday")){
				tv_title.setText("修改生日");
				ll_name.setVisibility(View.GONE);
				ll_phone.setVisibility(View.GONE);
				ll_birthday.setVisibility(View.VISIBLE);
				ll_email.setVisibility(View.GONE);
			}else if(getIntent().getStringExtra("modify_type").equals("name")){
				tv_title.setText("修改姓名");
				ll_name.setVisibility(View.VISIBLE);
				ll_phone.setVisibility(View.GONE);
				ll_birthday.setVisibility(View.GONE);
				ll_email.setVisibility(View.GONE);
			}else{
				tv_title.setText("修改邮箱");
				ll_name.setVisibility(View.GONE);
				ll_phone.setVisibility(View.GONE);
				ll_birthday.setVisibility(View.GONE);
				ll_email.setVisibility(View.VISIBLE);
			}
		}
	}
	
	OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try{
				VCard vCard = new VCard();
				
//				if(et_phonenum.getText().toString().trim() != ""){
					birthday = dp_birthday.getYear() + "-" + (dp_birthday.getMonth()+1) + "-" + dp_birthday.getDayOfMonth();
					vCard.setPhoneHome("oim.birthday", birthday);
//				}
				
				if(et_phonenum.getText().toString().trim() != ""){
					
					vCard.setPhoneHome("oim.phonenum", et_phonenum.getText().toString().trim());
				}
				
				if(et_email.getText().toString().trim() != ""){
					vCard.setEmailHome(et_email.getText().toString());
				}

				XmppConnectionManager.getInstance().saveUserVCard(vCard);
				preferences.edit().putString(Constant.PHONE_NUM, et_phonenum.getText().toString().trim()).commit();
				preferences.edit().putString(Constant.EMAIL, et_email.getText().toString().trim()).commit();
				preferences.edit().putString(Constant.BIRTHDAT, birthday).commit();

				Toast.makeText(ModifyNumActivity.this, "修改完成", Toast.LENGTH_SHORT).show();
				setResult(1);
				finish();
			}catch(Exception ex){
				Toast.makeText(ModifyNumActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
			}
		}
	};
}
