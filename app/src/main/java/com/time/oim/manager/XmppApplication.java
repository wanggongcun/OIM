package com.time.oim.manager;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.time.oim.MainActivity;
import com.time.oim.util.ActivityUtil;
import com.time.oim.util.Constant;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class XmppApplication extends Application{
	public static XmppApplication xmppapplication;
	public static String user;
	public static SharedPreferences preferences;
//	public static final String XMPP_UP_MESSAGE_ACTION = "com.tarena.xmpp.chat.up.message.action";
//
// 	public static ConcurrentHashMap<String, OneChatMags>AllFriendsMessageMapData;
 	
	public static String oim_version = "v1.0";
	public static String update_url;
	
 	private List<Activity> activityList = new LinkedList<Activity>();
 	// 定位相关
 	LocationClient mLocClient;
 	public MyLocationListenner myListener = new MyLocationListenner();
 	private static double lat = 0;
 	private static double lng = 0;
 	
 	public static LatLng getLoca(){
 		LatLng latlng = null;
 		if(lat!=0 || lng!=0){
 			latlng = new LatLng(lat, lng);
 		}
 		
 		return latlng;
 	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		xmppapplication = this;
		preferences = getSharedPreferences(Constant.LOGIN_SET, 0);
		user = preferences.getString(Constant.USERNAME, "");
		ActivityUtil.saveSharedPreferences(XmppApplication.this, Constant.OIM_VERSION, oim_version);
//		AllFriendsMessageMapData = new ConcurrentHashMap<String, OneChatMags>();
//		//新消息条数
//		sharedPreferences = getApplicationContext().getSharedPreferences("newMsgCount",
//				Context.MODE_PRIVATE);
		
		SDKInitializer.initialize(getApplicationContext()); 
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}
	
	public String getVersion(){
		return oim_version;
	}
	
//	public void onDestroy() {
//		// TODO Auto-generated method stub
//		try{
//			ActivityUtil.stopService(XmppApplication.this);
//		}catch(Exception e){
//			
//		}
//
//	}

		// 添加Activity到容器中
		public void addActivity(Activity activity) {
			activityList.add(activity);
		}

		// 遍历所有Activity并finish
		public void exit() {
			XmppConnectionManager.getInstance().getConnection().disconnect();
			for (Activity activity : activityList) {
				activity.finish();
			}
		}
		
		public class MyLocationListenner implements BDLocationListener {

			@Override
			public void onReceiveLocation(BDLocation location) {
				// map view 销毁后不在处理新接收的位置
				if (location == null)
					return;
				MyLocationData locData = new MyLocationData.Builder()
						.accuracy(location.getRadius())
						// 此处设置开发者获取到的方向信息，顺时针0-360
						.direction(100).latitude(location.getLatitude())
						.longitude(location.getLongitude()).build();
				lat = locData.latitude;
				lng = locData.longitude;
			}

			public void onReceivePoi(BDLocation poiLocation) {
			}
		}


}
