package com.time.oim.service;

import java.io.IOException;
import java.util.Calendar;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Packet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.time.oim.MainActivity;
import com.time.oim.R;
import com.time.oim.manager.XmppApplication;
import com.time.oim.manager.XmppConnectionManager;
import com.time.oim.model.Notice;
import com.time.oim.util.Constant;
import com.time.oim.util.DatetimeUtil;

public class IMSysMsgService extends Service {
	private Context context;
	PacketCollector myCollector = null;
	/* 声明对象变量 */
	private NotificationManager myNotiManager;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		initSysMsgService();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
	
		return super.onStartCommand(intent, flags, startId);
	}

	private void initSysMsgService(){
		XMPPConnection con = XmppConnectionManager.getInstance().getConnection();
		con.addPacketListener(pListener, new MessageTypeFilter(
				Message.Type.normal));
	}
	
	PacketListener pListener = new PacketListener() {

		@Override
		public void processPacket(Packet packetz) {
			Message message = (Message) packetz;

			if (message.getType() == Type.normal) {

//				NoticeManager noticeManager = NoticeManager
//						.getInstance(context);
				Notice notice = new Notice();
				// playSound(1, 0); //播放音效

				notice.setTitle("系统消息");
				notice.setNoticeType(Notice.SYS_MSG);
				notice.setFrom(packetz.getFrom());
				notice.setContent(message.getBody());
				notice.setNoticeTime(DatetimeUtil.date2Str(Calendar.getInstance(),
						Constant.MS_FORMART));
				notice.setFrom(packetz.getFrom());
				notice.setTo(packetz.getTo());
				notice.setStatus(Notice.UNREAD);

//				long noticeId = noticeManager.saveNotice(notice);
//				if (noticeId != -1) {
					Intent intent = new Intent();
					intent.setAction(Constant.ACTION_SYS_MSG);
//					notice.setId(String.valueOf(noticeId));
					intent.putExtra("notice", notice);
					sendBroadcast(intent);
					setNotiType(R.drawable.ic_launcher, Constant.SYS_MSG_DIS,
							message.getBody(), MainActivity.class);
					MediaPlayer mPlayer  = MediaPlayer.create(XmppApplication.xmppapplication.getApplicationContext(),R.raw.msn );
					try {
						if (mPlayer != null) {
							mPlayer.stop();
						}
						mPlayer.prepare();
						mPlayer.start();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
//			}
		}
	};
	
	private void setNotiType(int iconId, String contentTitle,
			String contentText, Class activity) {
		/*
		 * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
		Intent notifyIntent = new Intent(this, activity);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		/* 创建PendingIntent作为设置递延运行的Activity */
		PendingIntent appIntent = PendingIntent.getActivity(this, 0,
				notifyIntent, 0);

		/* 创建Notication，并设置相关参数 */
		Notification myNoti = new Notification();
		/* 设置statusbar显示的icon */
		myNoti.icon = iconId;
		/* 设置statusbar显示的文字信息 */
		myNoti.tickerText = contentTitle;
		/* 设置notification发生时同时发出默认声音 */
		myNoti.defaults = Notification.DEFAULT_SOUND;
		/* 设置Notification留言条的参数 */
		myNoti.setLatestEventInfo(this, contentTitle, contentText, appIntent);
		/* 送出Notification */
		myNotiManager.notify(0, myNoti);
	}
}
