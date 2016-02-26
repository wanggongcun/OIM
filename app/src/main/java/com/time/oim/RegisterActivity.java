package com.time.oim;

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.time.oim.http.DataTrans;
import com.time.oim.manager.ContacterManager;
import com.time.oim.manager.XmppConnectionManager;
import com.time.oim.model.LoginConfig;
import com.time.oim.util.ActivityUtil;
import com.time.oim.util.CameraUtil;
import com.time.oim.util.Constant;
import com.time.oim.util.SaveFileUtil;

public class RegisterActivity extends Activity {
	private Camera camera = null;
	private SurfaceView surfaceView;
	private RelativeLayout rl_content;

	private LinearLayout ll_step1;
	private EditText et_username;
	private Button bt_next1;
	
	private LinearLayout ll_step2;
	private EditText et_password;
	private Button bt_next2;
	
	private LinearLayout ll_step2_5;
	private EditText et_firstname;
	private EditText et_secondname;
	private Button bt_next2_5;
	
	private LinearLayout ll_step3;
	private EditText et_email;
	private Button bt_next3;
	
	private LinearLayout ll_step4;
	private DatePicker dp_birthday;
	private Button bt_next4;
	
	private LinearLayout ll_step5;
	private EditText et_phone;
	private Button bt_next5;
	
	private LinearLayout ll_step6;
	private TextView tv_tologin;
	private RelativeLayout rl_bottom;
	private LoginConfig loginConfig;
	
	private String phonenum = "";
	private String email = "";
	private String birthday = "";
	private static final int LOGIN_SUCCESS = 1;//登录成功
	private static final int LOGIN_FAILED = 2;//登录失败
	private static final int REGISTER_SUCCESS = 3;//注册成功
	private static final int REGISTER_EXIST = 4;//用户已存在
	private static final int REGISTER_FAILED = 5;//注册失败
	private static final int REGISTER_FAILED_CONNECT = 6;//没有结果
	private static final int LOGIN_FAILED_HTTP = 7;//登录http失败
	private static final int REGISTER_FAILED_HTTP = 8;//注册http失败
	private static final int RESULT_LOGIN_SUCCESS_TO_MAIN = 11;//
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
			switch (msg.what) {
			case LOGIN_SUCCESS:
				ActivityUtil.startService(RegisterActivity.this);
				ActivityUtil.saveLoginConfig(RegisterActivity.this,loginConfig);

				if(phonenum!="" || email!="" || birthday!=""){
					try{
						VCard vCard = new VCard();
						if(phonenum!=""){
							vCard.setPhoneHome("oim.phonenum", phonenum);
						}
						if(email!=""){
							vCard.setEmailHome(email);
						}
						if(birthday!=""){
							vCard.setField("oim.birthday", birthday);
						}
						XmppConnectionManager.getInstance().saveUserVCard(vCard);
	
					}catch(Exception ex){
						
					}
				}
				
				ContacterManager.init(RegisterActivity.this,XmppConnectionManager.getInstance().getConnection());

//				Toast.makeText(RegisterActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
				Intent it = new Intent(RegisterActivity.this,MainActivity.class);
				setResult(RESULT_LOGIN_SUCCESS_TO_MAIN,it);
				finish();
				break;
			case LOGIN_FAILED:
				
				break;
			case REGISTER_SUCCESS:

				Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
				
				if(phonenum!="")
					intent.putExtra("oim.phonenum", phonenum);
				if(email!="")
					intent.putExtra("oim.email", email);
				if(birthday!="")
					intent.putExtra("oim.birthday", birthday);
				intent.putExtra("username", et_username.getText().toString());
				intent.putExtra("password", et_password.getText().toString());
//				setResult(REGISTER_SUCCESS,intent);
//				finish();
				try{
					Thread.sleep(2000);
				}catch(Exception ex){
					
				}
				login();
				break;
			case REGISTER_EXIST:
				setResult(REGISTER_EXIST,intent);
				finish();
				break;
			case REGISTER_FAILED:
				setResult(REGISTER_FAILED,intent);
				finish();
				break;
			case REGISTER_FAILED_CONNECT:
				setResult(REGISTER_FAILED_CONNECT,intent);
				finish();
				break;
			case LOGIN_FAILED_HTTP:
				setResult(LOGIN_FAILED_HTTP,intent);
				finish();
				break;
			case REGISTER_FAILED_HTTP:
				setResult(REGISTER_FAILED_HTTP,intent);
				finish();
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		loginConfig = ActivityUtil.getLoginConfig(RegisterActivity.this);
		camera = CameraUtil.getCameraInstance();
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceView.getHolder().setKeepScreenOn(true);// 屏幕常亮
		surfaceView.getHolder().addCallback(new CameraPreview());// 为SurfaceView的句柄添加一个回调函数
		surfaceView.setFocusable(true);
		
		ll_step1 = (LinearLayout)findViewById(R.id.ll_step1);
		et_username = (EditText)findViewById(R.id.et_username);
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
		bt_next1 = (Button)findViewById(R.id.bt_next1);
		bt_next1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(et_username.getText().toString().trim() == ""){
					Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
				}else{
					ll_step1.setVisibility(View.GONE);
					ll_step2.setVisibility(View.VISIBLE);
				}
				
			}
		});
		
		ll_step2 = (LinearLayout)findViewById(R.id.ll_step2);
		et_password = (EditText)findViewById(R.id.et_password);
		bt_next2 = (Button)findViewById(R.id.bt_next2);
		bt_next2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(et_password.getText().toString().trim() == ""){
					Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
				}else{
					ll_step2.setVisibility(View.GONE);
					ll_step3.setVisibility(View.VISIBLE);
				}
			}
		});
		
		ll_step2_5 = (LinearLayout)findViewById(R.id.ll_step2_5);
		et_firstname = (EditText)findViewById(R.id.et_firstname);
		et_secondname = (EditText)findViewById(R.id.et_secondname);
		bt_next2_5 = (Button)findViewById(R.id.bt_next2_5);
		bt_next2_5.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(et_firstname.getText().toString().trim() == "" || et_secondname.getText().toString().trim() == ""){
					Toast.makeText(RegisterActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
				}else{
					ll_step2_5.setVisibility(View.GONE);
					ll_step3.setVisibility(View.VISIBLE);
				}
			}
		});
		
		ll_step3 = (LinearLayout)findViewById(R.id.ll_step3);
		et_email = (EditText)findViewById(R.id.et_email);
		bt_next3 = (Button)findViewById(R.id.bt_next3);
		bt_next3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(et_email.getText().toString().trim() ==""){
					Toast.makeText(RegisterActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
				}else{
					ll_step3.setVisibility(View.GONE);
					ll_step4.setVisibility(View.VISIBLE);
				}
			}
		});
		
		ll_step4 = (LinearLayout)findViewById(R.id.ll_step4);
		et_phone = (EditText)findViewById(R.id.et_phone);
		bt_next4 = (Button)findViewById(R.id.bt_next4);
		bt_next4.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(et_phone.getText().toString().trim() ==""){
					Toast.makeText(RegisterActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
				}else{
					ll_step4.setVisibility(View.GONE);
					ll_step5.setVisibility(View.VISIBLE);
				}
			}
		});
		
		ll_step5 = (LinearLayout)findViewById(R.id.ll_step5);
		dp_birthday = (DatePicker)findViewById(R.id.dp_birthday);
		bt_next5 = (Button)findViewById(R.id.bt_next5);
		bt_next5.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ll_step5.setVisibility(View.GONE);
				ll_step6.setVisibility(View.VISIBLE);
				rl_bottom.setVisibility(View.GONE);
				final IQ result = null;
				final String accounts = et_username.getText().toString();
				final String pw = et_password.getText().toString();
				final String em = et_email.getText().toString();
				final String nicheng = et_firstname.getText().toString() + " " + et_secondname.getText().toString();
				phonenum = et_phone.getText().toString().trim();
				email = em;
				birthday = dp_birthday.getYear() + "-" + (dp_birthday.getMonth()+1) + "-" + dp_birthday.getDayOfMonth() + "";
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub

						Message ms = new Message();
//						boolean result = DataTrans.getInstance(RegisterActivity.this).userRegister(accounts,pw);
//						if(!result){
//							ms.what = REGISTER_FAILED_HTTP;
//							handler.sendMessage(ms);
//						}else{
							try {
								XMPPConnection connection = null;
								connection = XmppConnectionManager.getInstance().getConnection();
								if(connection == null){
									ms.what=REGISTER_FAILED;
									handler.sendMessage(ms);
									return;
								}
	//							connection.connect();
								if(connection.isConnected()){
									connection.disconnect();
									connection.connect();
								}
								connection.login("wgc", "123456");
								regist(connection,accounts, pw, em, accounts);
								if(connection.isConnected()){
									connection.disconnect();
									connection.connect();
								}
							} catch (XMPPException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
//						}
					}
				}).start();
			}
		});
		
		ll_step6 = (LinearLayout)findViewById(R.id.ll_step6);
		rl_bottom = (RelativeLayout)findViewById(R.id.rl_bottom);
		tv_tologin = (TextView)findViewById(R.id.tv_tologin);
		tv_tologin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	private void login(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				if(login(et_username.getText().toString(),et_password.getText().toString())){
//					loginConfig.setUsername(et_username.getText().toString());
//					loginConfig.setPassword(et_password.getText().toString());
					ActivityUtil.saveSharedPreferences(RegisterActivity.this, Constant.USERNAME, et_username.getText().toString());
					ActivityUtil.saveSharedPreferences(RegisterActivity.this, Constant.PASSWORD, et_password.getText().toString());
//					boolean result = DataTrans.getInstance(RegisterActivity.this).userLogin(et_username.getText().toString(),
//							et_password.getText().toString());
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
//			connection.connect();
			if(connection.isConnected()){
				connection.disconnect();
				connection.connect();
			}
			connection.login(username, password); 
			
			
		} catch (XMPPException e) {
			 //TODO Auto-generated catch block
			return false;
		}
		return true;
	}
	
	private boolean register(String username,String password,String email){
		
		XMPPConnection connection = XmppConnectionManager.getInstance().getConnection();
		try {
			connection.getAccountManager().createAccount(username, password);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public IQ regist(XMPPConnection connection,String accounts,String pwd,String em,String nicheng){
		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
//			reg.setTo(XmppConnectionManager.getInstance().getConnection().getServiceName());
		
		reg.setUsername(accounts);
		reg.setPassword(pwd);
		reg.addAttribute("name", nicheng);
		reg.addAttribute("email", em);
		
		
		PacketFilter filter = new AndFilter(new PacketIDFilter(
		                                reg.getPacketID()), new PacketTypeFilter(
		                                IQ.class));
		PacketCollector collector = connection.createPacketCollector(filter);
		connection.sendPacket(reg);

		IQ result = (IQ) collector.nextResult(SmackConfiguration
		                                .getPacketReplyTimeout());
		                        // Stop queuing results
		collector.cancel();// 停止请求results（是否成功的结果）
		Message msg = new Message();
		if (result == null) {  
			msg.what = REGISTER_FAILED_CONNECT;
//			Toast.makeText(RegisterActivity.this, "服务器没有返回结果", Toast.LENGTH_SHORT).show();  
		}  
		else if (result.getType() == IQ.Type.ERROR) {  
			if(result.getError().toString().equalsIgnoreCase("conflict(409)")){ 
				msg.what = REGISTER_EXIST;
//					Toast.makeText(RegisterActivity.this, "这个账号已经存在", Toast.LENGTH_SHORT).show();  
			}else{  
				msg.what = REGISTER_FAILED;
//					Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
			}
		}else if(result.getType() == IQ.Type.RESULT){  
			msg.what = REGISTER_SUCCESS;
//					setToText(R.id.username, registerUserName.getText().toString());  
//					setToText(R.id.password, registerPassword.getText().toString());  
//				Toast.makeText(RegisterActivity.this, "恭喜你注册成功", Toast.LENGTH_SHORT).show();  
		}

		handler.sendMessage(msg);
		return result;
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
		camera.setDisplayOrientation(SaveFileUtil.getPreviewDegree(RegisterActivity.this)); 
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
