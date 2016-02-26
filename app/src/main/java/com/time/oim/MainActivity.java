package com.time.oim;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import com.time.oim.db.DBHelper;
import com.time.oim.db.DBManager;
import com.time.oim.manager.ContacterManager;
import com.time.oim.manager.FontManager;
import com.time.oim.manager.XmppConnectionManager;
import com.time.oim.model.LoginConfig;
import com.time.oim.model.Notice;
import com.time.oim.model.User;
import com.time.oim.util.ActivityUtil;
import com.time.oim.util.Constant;
import com.time.oim.view.MyViewPager;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity{

	public MyViewPager mViewPager;  
    private FragmentPagerAdapter mAdapter;  
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private CameraActivity cameraActivity= null;
    private FriendListActivity friendListActivity = null;
    private ChatListActivity chatListActivity = null;
    private StoryActivity storyActivity = null;
    private SettingActivity settingActivity= null;
    private Receiver receiver = null;
    
    private MediaPlayer mPlayer;
    private String username;
    private String password;
    
    private final static int HANDLER_AUTO_LOGIN_SUCCESS = 2;
    private final static int HANDLER_AUTO_LOGIN_FAILED = 3;
    private final static int REQUEST_TO_LOGIN = 1;
	private static final int RESULT_LOGIN_SUCCESS_TO_MAIN = 11;//登录
    
    boolean ischat = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initView();
		if(getIntent().hasExtra("request_str")){
			String request_str = getIntent().getStringExtra("request_str");
			if(request_str != null){
				if(request_str.equals("chat_to_one")){
					ischat = true;
				}
			}

		}else{
			isLogin();
		}
		
		mPlayer  = MediaPlayer.create(MainActivity.this,R.raw.fangye);
		
		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mFragments.size();
			}
			
			@Override
			public Fragment getItem(int arg0) {
				// TODO Auto-generated method stub
				return mFragments.get(arg0);
			}
		};
		
		mViewPager.setAdapter(mAdapter);
		mViewPager.setCurrentItem(1);
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
//				mViewPager.setDrag(!cameraActivity.canDraw);
				playmusic();
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
//				mViewPager.setDrag(!cameraActivity.canDraw);
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
//				mViewPager.setDrag(!cameraActivity.canDraw);
				
			}
		});
		
		NotificationManager notificationManager = (NotificationManager) this   
                .getSystemService(NOTIFICATION_SERVICE);   
        notificationManager.cancel(0);
        
        DBManager.getInstance(MainActivity.this).clearMsgs();
	}
	
	private void initView()  
    {   
		
		mViewPager = (MyViewPager) findViewById(R.id.page);
		mViewPager.setBackgroundColor(Color.TRANSPARENT);
		
		cameraActivity = new CameraActivity();
		friendListActivity = new FriendListActivity();
		chatListActivity = new ChatListActivity();
		storyActivity = new StoryActivity();
//		settingActivity = new SettingActivity();
//		mFragments.add(chatListActivity);
		mFragments.add(friendListActivity);
		mFragments.add(cameraActivity);
		mFragments.add(storyActivity);
//		mFragments.add(settingActivity);
		
    }  
	
	private void isLogin(){
		
		username = ActivityUtil.getSharedPreferences(MainActivity.this, Constant.USERNAME);
		password = ActivityUtil.getSharedPreferences(MainActivity.this, Constant.PASSWORD);
		if(username == "" || password == "" || username == null || password == null){
			Intent intent = new Intent(MainActivity.this, LoginActivity.class);
			startActivityForResult(intent, REQUEST_TO_LOGIN);
		}else{
			if(!ActivityUtil.hasInternetConnected(MainActivity.this)){
				ActivityUtil.showToast(MainActivity.this,"无网络连接!");
			}else
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Message msg = new Message();
						try {
							XMPPConnection connection = null;
							connection = XmppConnectionManager.getInstance().getConnection();
							if(connection == null){
								msg.what = HANDLER_AUTO_LOGIN_FAILED;
							}else{
								if(!connection.isConnected()){
									connection.connect();
//									connection.login(username, password);
								}
//								if(connection.is)
								if(!connection.isAuthenticated()){
									connection.login(username, password);
								}

								msg.what = HANDLER_AUTO_LOGIN_SUCCESS;
							}
							
						} catch (XMPPException e) {
							 //TODO Auto-generated catch block
							msg.what = HANDLER_AUTO_LOGIN_FAILED;
						}
						
						
						handler.sendMessage(msg);
					}
				}).start();
				
			
		}
		
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if(arg1== RESULT_LOGIN_SUCCESS_TO_MAIN){
			
			ActivityUtil.saveSharedPreferences(MainActivity.this, Constant.PHONE_NUM, 
					XmppConnectionManager.getInstance().getUserVCard("").getPhoneHome("oim.phonenum"));
			ActivityUtil.saveSharedPreferences(MainActivity.this, Constant.EMAIL, 
					XmppConnectionManager.getInstance().getUserVCard("").getEmailHome());
			ActivityUtil.saveSharedPreferences(MainActivity.this, Constant.BIRTHDAT, 
					XmppConnectionManager.getInstance().getUserVCard("").getField("oim.birthday"));
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		unregisterReceiver(receiver);
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		receiver = new Receiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("fragment_change");
		filter.addAction("oim_chat_to_one");

		registerReceiver(receiver, filter);
		
		NotificationManager notificationManager = (NotificationManager) this   
                .getSystemService(NOTIFICATION_SERVICE);   
        notificationManager.cancel(0);
        
	} 
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try{
			if(!ischat){
				ActivityUtil.stopService(MainActivity.this);
				XMPPConnection connection = null;
				connection = XmppConnectionManager.getInstance().getConnection();
				connection.disconnect();
			}
		}catch(Exception e){
			
		}

	}



	private class Receiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			User user = intent.getParcelableExtra(User.userKey);
			Notice notice = (Notice) intent.getSerializableExtra("notice");
			if(action.equals("fragment_change")){
				String to = intent.getStringExtra("fragment_change");
//				mViewPager.arrowScroll(Integer.valueOf(to));
				mViewPager.setCurrentItem(Integer.valueOf(to));
			}else if(action.equals("oim_chat_to_one")){
				Toast.makeText(MainActivity.this, action+intent.getStringExtra("msg_path"), Toast.LENGTH_SHORT).show();
				String path = intent.getStringExtra("msg_path");
				Intent it = new Intent(MainActivity.this,ChatListActivity.class);
				it.putExtra("msg_type", 1);
				it.putExtra("msg_path", path);
				MainActivity.this.setResult(1, it);
				
			}
		}
		
	}
	
	private void playmusic(){
		
		try {
			mPlayer.stop();
			mPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM);
			mPlayer.setVolume(AudioManager.STREAM_MUSIC, AudioManager.STREAM_MUSIC);
			
			mPlayer.prepare();
			mPlayer.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case HANDLER_AUTO_LOGIN_SUCCESS:
				ActivityUtil.startService(MainActivity.this);
				ContacterManager.init(MainActivity.this,XmppConnectionManager.getInstance().getConnection());
				ActivityUtil.showToast(MainActivity.this,"连接成功!");
				break;
			case HANDLER_AUTO_LOGIN_FAILED:
				
				ActivityUtil.showToast(MainActivity.this,"服务器连接失败!");
				break;
			}
		}
	};
	
	
	
}
