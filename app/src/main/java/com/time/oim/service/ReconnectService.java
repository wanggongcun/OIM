package com.time.oim.service;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.time.oim.manager.XmppConnectionManager;
import com.time.oim.util.Constant;

public class ReconnectService extends Service{
	private Context context;
	private ConnectivityManager connectivityManager;
	private NetworkInfo networkInfo;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		context = this;
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(reconnectReceiver, intentFilter);
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(reconnectReceiver);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	BroadcastReceiver reconnectReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
				connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				XMPPConnection connection = XmppConnectionManager.getInstance().getConnection();
				networkInfo = connectivityManager.getActiveNetworkInfo();
				if(networkInfo != null && networkInfo.isAvailable()){
					if(connection.isConnected()){
						reConnect(connection);
					}else{
						sendInentAndPre(Constant.RECONNECT_STATE_SUCCESS);
						Toast.makeText(context, "用户已上线!", Toast.LENGTH_LONG)
						.show();
					}
				}else{
					sendInentAndPre(Constant.RECONNECT_STATE_FAIL);
					Toast.makeText(context, "网络断开,用户已离线!", Toast.LENGTH_LONG)
							.show();
				}
			}
		}
	};
	
	public void reConnect(XMPPConnection connection) {
		try {
			connection.connect();
			if (connection.isConnected()) {
				Presence presence = new Presence(Presence.Type.available);
				connection.sendPacket(presence);
				Toast.makeText(context, "用户已上线!", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(context, "断开连接!", Toast.LENGTH_LONG).show();
			}
		} catch (XMPPException e) {
			Log.e("ERROR", "XMPP连接失败!", e);
			reConnect(connection);
		}
	}

	private void sendInentAndPre(boolean isSuccess) {
		Intent intent = new Intent();
		SharedPreferences preference = getSharedPreferences(Constant.LOGIN_SET,
				0);
		// 保存在线连接信息
		preference.edit().putBoolean(Constant.IS_ONLINE, isSuccess).commit();
		intent.setAction(Constant.ACTION_RECONNECT_STATE);
		intent.putExtra(Constant.RECONNECT_STATE, isSuccess);
		sendBroadcast(intent);
	}
}
