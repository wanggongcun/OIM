package com.time.oim;


import java.util.List;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smackx.packet.VCard;

import com.time.oim.R;
import com.time.oim.R.id;
import com.time.oim.R.layout;
import com.time.oim.http.DataTrans;
import com.time.oim.manager.ContacterManager;
import com.time.oim.manager.XmppConnectionManager;
import com.time.oim.model.LoginConfig;
import com.time.oim.util.ActivityUtil;
import com.time.oim.util.CameraUtil;
import com.time.oim.util.Constant;
import com.time.oim.util.SaveFileUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private Camera camera = null;
	private SurfaceView surfaceView;
	private RelativeLayout rl_content;
	private EditText et_username = null;
	private EditText et_password = null;
	
	private Button bt_login = null;
	private TextView bt_register = null;
	
	private LoginConfig loginConfig;
	
	private static final int LOGIN_SUCCESS = 1;//登录成功
	private static final int LOGIN_FAILED = 2;//登录失败
	private static final int REGISTER_SUCCESS = 3;//注册成功
	private static final int REGISTER_EXIST = 4;//用户已存在
	private static final int REGISTER_FAILED = 5;//注册失败
	private static final int REGISTER_FAILED_CONNECT = 6;//没有结果
	private static final int LOGIN_FAILED_HTTP = 7;//登录http失败
	private static final int REGISTER_FAILED_HTTP = 8;//注册http失败
	
	private static final int REQUEST_TO_REGISTER = 12;
	private static final int RESULT_LOGIN_SUCCESS_TO_MAIN = 11;//
	
	private boolean isregister = false;
	private String phonenum = "";
	private String email = "";
	private String birthday = "";
	
	private ProgressDialog pd = null;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			pd.dismiss();
			switch (msg.what) {
			case LOGIN_SUCCESS:
				ActivityUtil.startService(LoginActivity.this);
				ActivityUtil.saveLoginConfig(LoginActivity.this,loginConfig);

				if(phonenum!="" || email!="" || birthday!=""){
					try{
						SharedPreferences preferences = getSharedPreferences(Constant.LOGIN_SET, 0);
						VCard vCard = new VCard();
						if(phonenum!=""){
							vCard.setPhoneHome("oim.phonenum", phonenum);
	//						preferences.edit().putString(Constant.PHONE_NUM, arg2.getStringExtra("oim.phonenum")).commit();
						}
						if(email!=""){
							vCard.setEmailHome(email);
	//						preferences.edit().putString(Constant.EMAIL, arg2.getStringExtra("oim.email")).commit();
						}
						if(birthday!=""){
							vCard.setField("oim.birthday", birthday);
	//						preferences.edit().putString(Constant.BIRTHDAT, arg2.getStringExtra("oim.birthday")).commit();
						}
						XmppConnectionManager.getInstance().saveUserVCard(vCard);
	
					}catch(Exception ex){
						
					}
				}
				
				ContacterManager.init(LoginActivity.this,XmppConnectionManager.getInstance().getConnection());

				setResult(RESULT_LOGIN_SUCCESS_TO_MAIN);
				finish();
				break;
			case LOGIN_FAILED:
				ActivityUtil.showToast(LoginActivity.this,"登录失败!");
				break;
			case REGISTER_SUCCESS:
				ActivityUtil.showToast(LoginActivity.this,"注册成功!");
				ActivityUtil.saveLoginConfig(LoginActivity.this,loginConfig);
				isregister = true;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				login();
				break;
			case REGISTER_EXIST:
				ActivityUtil.showToast(LoginActivity.this,"注册失败，账户已存在!");
				break;
			case REGISTER_FAILED:
				ActivityUtil.showToast(LoginActivity.this,"注册失败!");
				break;
			case REGISTER_FAILED_CONNECT:
				ActivityUtil.showToast(LoginActivity.this,"失败，无法连接服务器!");
				break;
			case LOGIN_FAILED_HTTP:
				ActivityUtil.showToast(LoginActivity.this,"失败，无法连接http服务器!");
				break;
			case REGISTER_FAILED_HTTP:
				
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		phonenum = "";
		birthday = "";
		email = "";
		init();
		if(!getIntent().hasExtra("relogin")){
			if(loginConfig.getUsername()!="" && loginConfig.getPassword()!="" &&
					loginConfig.getUsername()!=null && loginConfig.getPassword()!=null){
				login();
			}
		}
		rl_content = (RelativeLayout)findViewById(R.id.rl_content);
		rl_content.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				im.hideSoftInputFromWindow(rl_content.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				return true;
			}
		});
	}
	
	private void init(){

		camera = CameraUtil.getCameraInstance();
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceView.getHolder().setKeepScreenOn(true);// 屏幕常亮
		
		surfaceView.getHolder().addCallback(new CameraPreview());// 为SurfaceView的句柄添加一个回调函数
		surfaceView.setFocusable(true);
//		initCamera();
		loginConfig = ActivityUtil.getLoginConfig(LoginActivity.this);
		et_username = (EditText) findViewById(R.id.username);
		et_password = (EditText) findViewById(R.id.password);
		
		bt_login = (Button) findViewById(R.id.login);
		bt_register = (TextView) findViewById(R.id.register);
		
		et_username.setText(loginConfig.getUsername());
		et_password.setText(loginConfig.getPassword());
		
		pd = new ProgressDialog(this);
		pd.setTitle("提示");
		pd.setMessage("请稍后...");
		bt_login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				login();
			}
		});
		
		bt_register.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(LoginActivity.this,RegisterActivity.class);
				startActivityForResult(it, REQUEST_TO_REGISTER);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_TO_REGISTER){
			switch (resultCode) {
			case LOGIN_SUCCESS:
				setResult(RESULT_LOGIN_SUCCESS_TO_MAIN);
				finish();
				break;
			case LOGIN_FAILED:
				
				break;
			case REGISTER_SUCCESS:
				ActivityUtil.showToast(LoginActivity.this,"注册成功!");
				ActivityUtil.saveLoginConfig(LoginActivity.this,loginConfig);
				isregister = true;
				if(data.hasExtra("oim.phonenum"))
					phonenum = data.getStringExtra("oim.phonenum");
				if(data.hasExtra("oim.email"))
					email = data.getStringExtra("oim.email");
				if(data.hasExtra("oim.birthday"))
					birthday = data.getStringExtra("oim.birthday");
				
				et_username.setText(data.getStringExtra("username"));
				et_password.setText(data.getStringExtra("password"));

				ActivityUtil.saveSharedPreferences(LoginActivity.this, Constant.USERNAME, data.getStringExtra("username"));
				ActivityUtil.saveSharedPreferences(LoginActivity.this, Constant.PASSWORD, data.getStringExtra("password"));
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				login();
				break;
			case REGISTER_EXIST:
				ActivityUtil.showToast(LoginActivity.this,"注册失败，账户已存在!");
				break;
			case REGISTER_FAILED:
				ActivityUtil.showToast(LoginActivity.this,"注册失败!");
				break;
			case REGISTER_FAILED_CONNECT:
				ActivityUtil.showToast(LoginActivity.this,"失败，无法连接服务器!");
				break;
			case LOGIN_FAILED_HTTP:
				ActivityUtil.showToast(LoginActivity.this,"失败，无法连接http服务器!");
				break;
			case REGISTER_FAILED_HTTP:
				ActivityUtil.showToast(LoginActivity.this,"失败，无法连接http服务器!");
				break;
			case RESULT_LOGIN_SUCCESS_TO_MAIN:

				setResult(RESULT_LOGIN_SUCCESS_TO_MAIN);
				finish();
				break;
			}
		}
	}

	private void login(){
		
		pd.show();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				if(login(et_username.getText().toString(),et_password.getText().toString())){
					loginConfig.setUsername(et_username.getText().toString());
					loginConfig.setPassword(et_password.getText().toString());
					ActivityUtil.saveLoginConfig(LoginActivity.this,loginConfig);
//					ActivityUtil.saveSharedPreferences(LoginActivity.this, Constant.USERNAME, et_username.getText().toString());
//					ActivityUtil.saveSharedPreferences(LoginActivity.this, Constant.PASSWORD, et_password.getText().toString());
//					boolean result = DataTrans.getInstance(LoginActivity.this).userLogin(et_username.getText().toString(),
//							et_password.getText().toString());
////					boolean result = DataTrans.getInstance(LoginActivity.this).userLogin("test1", "4");
					if(true)
						msg.what = LOGIN_SUCCESS;
					else
						msg.what = LOGIN_FAILED_HTTP;
				}else{
					msg.what = LOGIN_FAILED;
				}
				
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	private boolean login(String username,String password){
		try {
			XMPPConnection connection = null;
			connection = XmppConnectionManager.getInstance().getConnection();
			if(connection == null){
				return false;
			}
			connection.connect();
			if(connection.isConnected()){
				connection.disconnect();
				connection.connect();
			}
			connection.login(username, password); 
			
			ActivityUtil.saveSharedPreferences(LoginActivity.this, Constant.USERNAME, username);
			ActivityUtil.saveSharedPreferences(LoginActivity.this, Constant.PASSWORD, password);
		} catch (XMPPException e) {
			 //TODO Auto-generated catch block
			return false;
		}
		return true;
	}
	
	private void initCamera(){
		
		camera.stopPreview();
		Parameters parameters = null;
		parameters = camera.getParameters();
		parameters.setPictureFormat(PixelFormat.JPEG);
		parameters.setRotation(270);
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
		parameters.set("orientation", "portrait");
		parameters.setFlashMode(Parameters.FLASH_MODE_OFF);

		camera.setParameters(parameters);
		camera.setDisplayOrientation(SaveFileUtil.getPreviewDegree(LoginActivity.this)); 
		camera.startPreview();
			
		
	}
	
	public class CameraPreview implements SurfaceHolder.Callback{
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			if(holder.getSurface() == null){
				return;
			}
			try{
				camera.stopPreview();
			}catch(Exception e){
				
			}
			
			try{
				camera.setPreviewDisplay(holder);
				initCamera();
			}catch(Exception e){
				
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			try{

				camera.setPreviewDisplay(holder);
				camera.startPreview();
			}catch(Exception e){
				
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	 private void releaseCamera(){
	        if (camera != null){
	        	camera.cancelAutoFocus();
	            camera.release();        // release the camera for other applications
	            camera = null;
	        }
	    }

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		releaseCamera();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if(camera == null)
			camera = Camera.open();
	}
	
}
