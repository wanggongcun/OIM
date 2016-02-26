package com.time.oim;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.time.oim.manager.XmppApplication;
import com.time.oim.model.LoginConfig;
import com.time.oim.service.IMChatService;
import com.time.oim.service.IMConstactService;
import com.time.oim.service.IMSysMsgService;
import com.time.oim.service.ReconnectService;
import com.time.oim.util.Constant;

public class ActivitySupport extends FragmentActivity implements IActivitySupport{

	protected Context context = null;
	protected SharedPreferences preferences;
	protected XmppApplication xmppApplication;
	protected ProgressDialog pg = null;
	protected NotificationManager notificationManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		preferences = getSharedPreferences(Constant.LOGIN_SET, 0);
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		pg = new ProgressDialog(context);
		xmppApplication = (XmppApplication) getApplication();
		xmppApplication.addActivity(this);
	}

	@Override
	public XmppApplication getXmppApplication() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stopService() {
		// TODO Auto-generated method stub
		// 聊天服务
		Intent chatServer = new Intent(context, IMChatService.class);
		context.stopService(chatServer);
		Intent ConstactServer = new Intent(context, IMConstactService.class);
		context.stopService(ConstactServer);
		Intent SysMsgServer = new Intent(context, IMSysMsgService.class);
		context.stopService(SysMsgServer);
		Intent reconnectService = new Intent(context,ReconnectService.class);
		context.stopService(reconnectService);
	}

	@Override
	public void startService() {
		// TODO Auto-generated method stub
		// 聊天服务
		Intent chatServer = new Intent(context, IMChatService.class);
		context.startService(chatServer);
		Intent ConstactServer = new Intent(context, IMConstactService.class);
		context.startService(ConstactServer);
		Intent SysMsgServer = new Intent(context, IMSysMsgService.class);
		context.startService(SysMsgServer);
		Intent reconnectServer = new Intent(context, IMSysMsgService.class);
		context.startService(reconnectServer);
	}

	@Override
	public boolean validateInternet() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasInternetConnected() {
		// TODO Auto-generated method stub
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE);
		if (manager != null) {
			NetworkInfo network = manager.getActiveNetworkInfo();
			if (network != null && network.isConnectedOrConnecting()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void isExit() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(context).setTitle("确定退出吗?")
		.setNeutralButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				stopService();
				xmppApplication.exit();
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).show();
	}

	@Override
	public boolean hasLocationGPS() {
		// TODO Auto-generated method stub
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			new AlertDialog.Builder(context)
					.setTitle(R.string.prompt)
					.setMessage("请检查内存卡")
					.setPositiveButton(R.string.menu_settings,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									Intent intent = new Intent(
											Settings.ACTION_SETTINGS);
									context.startActivity(intent);
								}
							})
					.setNegativeButton("退出",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									xmppApplication.exit();
								}
							}).create().show();
		}
		return false;
	}

	@Override
	public boolean hasLocationNetWork() {
		// TODO Auto-generated method stub
		LocationManager manager = (LocationManager) context
				.getSystemService(context.LOCATION_SERVICE);
		if (manager
				.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void checkMemoryCard() {
		// TODO Auto-generated method stub
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			new AlertDialog.Builder(context)
					.setTitle(R.string.prompt)
					.setMessage("请检查内存卡")
					.setPositiveButton(R.string.menu_settings,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									Intent intent = new Intent(
											Settings.ACTION_SETTINGS);
									context.startActivity(intent);
								}
							})
					.setNegativeButton("退出",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									xmppApplication.exit();
								}
							}).create().show();
		}
	}

	@Override
	public void showToast(String text, int longint) {
		// TODO Auto-generated method stub
		Toast.makeText(context, text, longint).show();
	}

	@Override
	public void showToast(String text) {
		// TODO Auto-generated method stub
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public ProgressDialog getProgressDialog() {
		// TODO Auto-generated method stub
		return pg;
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return context;
	}

	@Override
	public SharedPreferences getLoginUserSharedPre() {
		// TODO Auto-generated method stub
		return preferences;
	}

	@Override
	public boolean getUserOnlineState() {
		// TODO Auto-generated method stub
		return preferences.getBoolean(Constant.IS_ONLINE, true);
	}

	@Override
	public void setUserOnlineState(boolean isOnline) {
		// TODO Auto-generated method stub
		preferences.edit().putBoolean(Constant.IS_ONLINE, isOnline).commit();
	}

	@Override
	public void setNotiType(int iconId, String contentTitle,
			String contentText, Class activity, String from) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveLoginConfig(LoginConfig loginConfig) {
		// TODO Auto-generated method stub
		preferences.edit()
				.putString(Constant.XMPP_HOST, loginConfig.getXmppHost())
				.commit();
		preferences.edit()
				.putInt(Constant.XMPP_PORT, loginConfig.getXmppPort()).commit();
		preferences.edit()
				.putString(Constant.XMPP_SEIVICE_NAME,
						loginConfig.getXmppServiceName()).commit();
		preferences.edit()
				.putString(Constant.USERNAME, loginConfig.getUsername())
				.commit();
		preferences.edit()
				.putString(Constant.PASSWORD, loginConfig.getPassword())
				.commit();
		preferences.edit()
				.putBoolean(Constant.IS_AUTOLOGIN, loginConfig.isAutoLogin())
				.commit();
		preferences.edit()
				.putBoolean(Constant.IS_NOVISIBLE, loginConfig.isNovisible())
				.commit();
		preferences.edit()
				.putBoolean(Constant.IS_REMEMBER, loginConfig.isRemember())
				.commit();
		preferences.edit()
				.putBoolean(Constant.IS_ONLINE, loginConfig.isOnline())
				.commit();
		preferences.edit()
				.putBoolean(Constant.IS_FIRSTSTART, loginConfig.isFirstStart())
				.commit();
	}

	@Override
	public LoginConfig getLoginConfig() {
		// TODO Auto-generated method stub
		LoginConfig loginConfig = new LoginConfig();
		String a = preferences.getString(Constant.XMPP_HOST, null);
		String b = getResources().getString(R.string.xmpp_host);
		loginConfig.setXmppHost(preferences.getString(Constant.XMPP_HOST,
				getResources().getString(R.string.xmpp_host)));
		loginConfig.setXmppPort(preferences.getInt(Constant.XMPP_PORT,
				getResources().getInteger(R.integer.xmpp_port)));
		loginConfig.setUsername(preferences.getString(Constant.USERNAME, null));
		loginConfig.setPassword(preferences.getString(Constant.PASSWORD, null));
		loginConfig.setXmppServiceName(preferences.getString(
				Constant.XMPP_SEIVICE_NAME,
				getResources().getString(R.string.xmpp_service_name)));
		loginConfig.setAutoLogin(preferences.getBoolean(Constant.IS_AUTOLOGIN,
				getResources().getBoolean(R.bool.is_autologin)));
		loginConfig.setNovisible(preferences.getBoolean(Constant.IS_NOVISIBLE,
				getResources().getBoolean(R.bool.is_novisible)));
		loginConfig.setRemember(preferences.getBoolean(Constant.IS_REMEMBER,
				getResources().getBoolean(R.bool.is_remember)));
		loginConfig.setFirstStart(preferences.getBoolean(
				Constant.IS_FIRSTSTART, true));
		return loginConfig;
	}

}
