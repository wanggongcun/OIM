package com.time.oim.service;

import java.util.Calendar;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.time.oim.MainActivity;
import com.time.oim.R;
import com.time.oim.db.DBManager;
import com.time.oim.http.FileTrans;
import com.time.oim.manager.XmppApplication;
import com.time.oim.manager.XmppConnectionManager;
import com.time.oim.model.Msg;
import com.time.oim.util.ActivityUtil;
import com.time.oim.util.Constant;
import com.time.oim.util.DatetimeUtil;
import com.time.oim.util.SecretUtil;

public class IMChatService extends Service {
	
	private Context context;
	private NotificationManager notificationManager;
	
//	// 定位相关
//	LocationClient mLocClient;
//	public MyLocationListenner myListener = new MyLocationListenner();
//	private double lat = 0;
//	private double lng = 0;
//	
//	public LatLng getLoca(){
//		LatLng latlng = null;
//		if(lat!=0 || lng!=0){
//			latlng = new LatLng(lat, lng);
//		}
//		
//		return latlng;
//	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
//		initChatManager();
		context = this;
		
//		// 定位初始化
//		mLocClient = new LocationClient(this);
//		mLocClient.registerLocationListener(myListener);
//		LocationClientOption option = new LocationClientOption();
//		option.setOpenGps(true);// 打开gps
//		option.setCoorType("bd09ll"); // 设置坐标类型
//		option.setScanSpan(1000);
//		mLocClient.setLocOption(option);
//		mLocClient.start();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		initChatManager();
//		return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private void initChatManager() {
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		XmppConnectionManager.getInstance().getConnection().addPacketListener(pListener, new MessageTypeFilter(
				Message.Type.chat));
//		Msg msg1 = new Msg("wgc1", "ok", 0, DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss(), 0);
//		OneChatMags.MessageList.add(msg1);
		
	}
	
	PacketListener pListener = new PacketListener() {

		@Override
		public void processPacket(Packet packet) {
			// TODO Auto-generated method stub
			Message nowMessage = (Message) packet;
			Log.i("come", nowMessage.getFrom() + " to" + nowMessage.getTo() + " :" + nowMessage.getBody());
			if(nowMessage.getFrom().equals(XmppConnectionManager.servername)){
				
			}
			String body = nowMessage.getBody();
			int msg_type = Msg.MSG_TEXT;
			if(nowMessage.getPropertyNames().contains(Msg.KEY_TYPE)){
				msg_type = (int)nowMessage.getProperty(Msg.KEY_TYPE);
			}
			Msg msg = null;
			if(msg_type == Msg.MSG_PIC){
				String path = FileTrans.getInstance(IMChatService.this).downloadFile(body);
				msg = new Msg(nowMessage.getFrom().split("@")[0],nowMessage.getTo().split("@")[0], path, 
						msg_type, DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss().toString(), Msg.MSG_IN);
				DBManager.getInstance(IMChatService.this).saveMsg(msg, "oim_msg_unread");
			}else if(msg_type == Msg.MSG_TEXT || msg_type == Msg.MSG_LBS || msg_type == Msg.MSG_BIAOQING){
				msg = new Msg(nowMessage.getFrom().split("@")[0],nowMessage.getTo().split("@")[0], body, 
						msg_type, DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss().toString(), Msg.MSG_IN);
				DBManager.getInstance(IMChatService.this).saveMsg(msg, "oim_msg_unread");
			}else if(msg_type == Msg.MSG_LBS_SHARE){
				msg = new Msg(nowMessage.getFrom().split("@")[0],nowMessage.getTo().split("@")[0], body, 
						msg_type, DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss().toString(), Msg.MSG_IN);
				DBManager.getInstance(IMChatService.this).saveMsg(msg, "oim_msg_unread");
			}else if(msg_type == Msg.MSG_LBS_DRAW){
				msg = new Msg(nowMessage.getFrom().split("@")[0],nowMessage.getTo().split("@")[0], body, 
						msg_type, DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss().toString(), Msg.MSG_IN);
			}else if(msg_type == Msg.MSG_LBS_NEARBY){
//				msg = new Msg(nowMessage.getFrom().split("@")[0],nowMessage.getTo().split("@")[0], body, 
//						msg_type, DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss().toString(), Msg.MSG_IN);
				try{
					LatLng location1 = new LatLng(Double.valueOf(body.split("@")[1]), Double.valueOf(body.split("@")[2]));
//					LatLng location2 = new LatLng(lat, lng);
					LatLng location2 = new LatLng(XmppApplication.getLoca().latitude, XmppApplication.getLoca().longitude);
					Double distance = DistanceUtil.getDistance(location1, location2);
					if(distance<1000){
						Chat chat = XmppConnectionManager.getInstance().getConnection().getChatManager().createChat(nowMessage.getFrom(), null);
						String time = DatetimeUtil.date2Str(Calendar.getInstance(),Constant.MS_FORMART);
						org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
						message.setProperty(Msg.KEY_TIME, time);
						message.setProperty(Msg.KEY_TYPE, Msg.MSG_LBS_DRAW);
						message.setBody("request@" + String.valueOf(XmppApplication.getLoca().latitude) + "@"+ String.valueOf(XmppApplication.getLoca().longitude) + "@" 
									+ XmppConnectionManager.getInstance().getConnection().getUser());
						message.setTo(nowMessage.getFrom());
						try {
							if(chat==null){
								
							}else{
								chat.sendMessage(message);
								msg = new Msg(nowMessage.getFrom().split("@")[0],nowMessage.getTo().split("@")[0], "访问了你的位置", 
										msg_type, DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss().toString(), Msg.MSG_IN);
								DBManager.getInstance(IMChatService.this).saveMsg(msg, "oim_msg_unread");
							}
								
						} catch (XMPPException e) {
							// TODO Auto-generated catch block
//							e.printStackTrace();
						}
					}
				}catch(Exception ex){ 
					
				}
			}else{
				msg = new Msg(nowMessage.getFrom().split("@")[0],nowMessage.getTo().split("@")[0], body, 
						Msg.MSG_TEXT, DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss().toString(), Msg.MSG_IN);
				DBManager.getInstance(IMChatService.this).saveMsg(msg, "oim_msg_unread");
			}
			
//			MsgManager.getInstance(context).saveMsg(msg);
			// 生成通知
//			NoticeManager noticeManager = NoticeManager
//					.getInstance(context);
//			Notice notice = new Notice();
//			notice.setTitle("会话信息");
//			notice.setNoticeType(Notice.CHAT_MSG);
//			notice.setContent(nowMessage.getBody());
//			notice.setFrom(nowMessage.getFrom());
//			notice.setStatus(Notice.UNREAD);
//			notice.setNoticeTime(DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss());

			int noticeId = -1;
//			noticeId = noticeManager.saveNotice(notice);
//			if(noticeId != -1){
//				XmppApplication.xmppapplication.sendBroadcast(new Intent(Constant.NEW_MESSAGE_ACTION));
				// 播放声音
//				MediaPlayer mPlayer  = MediaPlayer.create(context,R.raw.msn );
//				try {
//					if (mPlayer != null) {
//						mPlayer.stop();
//					}
//					mPlayer.prepare();
//					mPlayer.start();
//				} catch (IllegalStateException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			String action = ActivityUtil.getSharedPreferences(context, Constant.OIM_ACTION);
			boolean isrun = ActivityUtil.isRunningApp(context, "com.time.oim");
			if(action.equals("1") && !isrun){
				setNotiType(R.drawable.logo,
						getResources().getString(R.string.new_message),
						"你有新消息", MainActivity.class, nowMessage.getFrom(),0);
			}
				
				Intent intent = new Intent(Constant.NEW_MESSAGE_ACTION);
				intent.putExtra(Msg.IMMESSAGE_KEY, msg);
				intent.putExtra(Constant.NOTICE_ID, noticeId);
				sendBroadcast(intent);
//			}
			
		}

	};
	private void setNotiType(int iconId, String contentTitle,
			String contentText, Class activity, String from,int noticeId) {

		/*
		 * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
		Intent notifyIntent = new Intent(this, activity);
		notifyIntent.putExtra("to", from);
		// notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		/* 创建PendingIntent作为设置递延运行的Activity */
		PendingIntent appIntent = PendingIntent.getActivity(this, 0,
				notifyIntent, 0);

		/* 创建Notication，并设置相关参数 */
		Notification myNoti = new Notification();
		// 点击自动消失
		myNoti.flags = Notification.FLAG_AUTO_CANCEL;
		/* 设置statusbar显示的icon */
		myNoti.icon = iconId;
		/* 设置statusbar显示的文字信息 */
		myNoti.tickerText = contentTitle;
		/* 设置notification发生时同时发出默认声音 */
		myNoti.defaults = Notification.DEFAULT_SOUND;
		/* 设置Notification留言条的参数 */
		myNoti.setLatestEventInfo(this, contentTitle, contentText, appIntent);
		/* 送出Notification */
		notificationManager.notify(noticeId, myNoti);
	}
	
//	public class MyLocationListenner implements BDLocationListener {
//
//		@Override
//		public void onReceiveLocation(BDLocation location) {
//			// map view 销毁后不在处理新接收的位置
//			if (location == null)
//				return;
//			MyLocationData locData = new MyLocationData.Builder()
//					.accuracy(location.getRadius())
//					// 此处设置开发者获取到的方向信息，顺时针0-360
//					.direction(100).latitude(location.getLatitude())
//					.longitude(location.getLongitude()).build();
//			lat = locData.latitude;
//			lng = locData.longitude;
//		}
//
//		public void onReceivePoi(BDLocation poiLocation) {
//		}
//	}
}
