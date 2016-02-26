package com.time.oim;

import com.time.oim.manager.XmppConnectionManager;
import com.time.oim.util.ActivityUtil;
import com.time.oim.util.Constant;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class ModifyPwdActivity extends Activity {

	private ImageButton ibt_back;
	private EditText et_oldpwd;
	private EditText et_newpwd;
	private Button bt_modify;
	
	private String oldpwd;
	private String newpwd;
	private SharedPreferences preferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_modify_pwd);
		
		preferences = getSharedPreferences(Constant.LOGIN_SET, 0);
		oldpwd= preferences.getString(Constant.PASSWORD, "");
		oldpwd = ActivityUtil.getSharedPreferences(ModifyPwdActivity.this,Constant.PASSWORD);
		ibt_back = (ImageButton)findViewById(R.id.ibt_back);
		ibt_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		et_oldpwd = (EditText) findViewById(R.id.et_oldpwd);
		et_newpwd = (EditText) findViewById(R.id.et_newpwd);
		
		bt_modify = (Button) findViewById(R.id.bt_modify_pwd);
		bt_modify.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(et_oldpwd.getText().toString().trim() == "" || et_newpwd.getText().toString().trim() == ""){
					Toast.makeText(ModifyPwdActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
				}else{
					if(oldpwd!=et_oldpwd.getText().toString().trim()){
						Toast.makeText(ModifyPwdActivity.this, "旧密码错误", Toast.LENGTH_SHORT).show();
					}else{
						try{
							XmppConnectionManager.getInstance().changePassword(et_newpwd.getText().toString().trim());
							
							if(preferences.edit().putString(Constant.PASSWORD, et_newpwd.getText().toString().trim()).commit()){
								Toast.makeText(ModifyPwdActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
								finish();
							}else{
								Toast.makeText(ModifyPwdActivity.this, "密码修改出错", Toast.LENGTH_SHORT).show();
							}
							
						}catch(Exception ex){
							Toast.makeText(ModifyPwdActivity.this, "密码修改失败", Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		});
	}
}
