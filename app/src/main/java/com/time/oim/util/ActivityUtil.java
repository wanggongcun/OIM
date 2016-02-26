package com.time.oim.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.time.oim.R;
import com.time.oim.model.LoginConfig;
import com.time.oim.service.IMChatService;
import com.time.oim.service.IMConstactService;
import com.time.oim.service.IMSysMsgService;
import com.time.oim.service.ReconnectService;

public class ActivityUtil {

	public static void startService(Context context) {
		
		Intent chatServer = new Intent(context, IMChatService.class);
		context.startService(chatServer);
		Intent ConstactServer = new Intent(context, IMConstactService.class);
		context.startService(ConstactServer);
		Intent SysMsgServer = new Intent(context, IMSysMsgService.class);
		context.startService(SysMsgServer);
		Intent reconnectServer = new Intent(context, IMSysMsgService.class);
		context.startService(reconnectServer);
	}
	
	public static void stopService(Context context) {
		
		Intent chatServer = new Intent(context, IMChatService.class);
		context.stopService(chatServer);
		Intent ConstactServer = new Intent(context, IMConstactService.class);
		context.stopService(ConstactServer);
		Intent SysMsgServer = new Intent(context, IMSysMsgService.class);
		context.stopService(SysMsgServer);
		Intent reconnectService = new Intent(context,ReconnectService.class);
		context.stopService(reconnectService);
	}
	
	public static boolean isRunningApp(Context context, String packageName) {
        boolean isAppRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(1);
        for (RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(packageName) && info.baseActivity.getPackageName().equals(packageName)) {
                isAppRunning = true;
                // find it, break
                break;
            }
        }
        return isAppRunning;
    }
	
	public static boolean hasInternetConnected(Context context) {
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
	
	public static void isExit(Context context) {
		// TODO Auto-generated method stub
		final Context c = context;
		new AlertDialog.Builder(context).setTitle("确定退出吗?")
		.setNeutralButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				stopService(c);
//				xmppApplication.exit();
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).show();
	}
	
	public static void showToast(Context context, String text, int longint) {
		// TODO Auto-generated method stub
		Toast.makeText(context, text, longint).show();
	}

	public static void showToast(Context context, String text) {
		// TODO Auto-generated method stub
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	
	public static void saveLoginConfig(Context context, LoginConfig loginConfig) {
		// TODO Auto-generated method stub
		SharedPreferences preferences = context.getSharedPreferences(Constant.LOGIN_SET, 0);
		
		preferences.edit()
				.putString(Constant.XMPP_HOST, loginConfig.getXmppHost()).commit();
		preferences.edit()
				.putInt(Constant.XMPP_PORT, loginConfig.getXmppPort()).commit();
		preferences.edit()
				.putString(Constant.XMPP_SEIVICE_NAME,loginConfig.getXmppServiceName()).commit();
		preferences.edit()
				.putString(Constant.USERNAME, SecretUtil.AESencrypt(loginConfig.getUsername())).commit();
		preferences.edit()
				.putString(Constant.PASSWORD, SecretUtil.AESencrypt(loginConfig.getPassword())).commit();
		preferences.edit()
				.putBoolean(Constant.IS_AUTOLOGIN, loginConfig.isAutoLogin()).commit();
		preferences.edit()
				.putBoolean(Constant.IS_NOVISIBLE, loginConfig.isNovisible()).commit();
		preferences.edit()
				.putBoolean(Constant.IS_REMEMBER, loginConfig.isRemember()).commit();
		preferences.edit()
				.putBoolean(Constant.IS_ONLINE, loginConfig.isOnline()).commit();
		preferences.edit()
				.putBoolean(Constant.IS_FIRSTSTART, loginConfig.isFirstStart()).commit();
		
//		preferences.edit()
//				.putString(Constant.PHONE_NUM, loginConfig.getPhoneNum()).commit();
//		preferences.edit()
//				.putString(Constant.BIRTHDAT, loginConfig.getBirthday()).commit();
//		preferences.edit()
//				.putString(Constant.EMAIL, loginConfig.getEmail()).commit();
	}
	
//	public static void saveSharedPreferences(Context context,String key,String value){
//		SharedPreferences preferences = context.getSharedPreferences(Constant.LOGIN_SET, 0);
//		preferences.edit()
//			.putString(key, value).commit();
//	}
//
//	public static String getSharedPreferences(Context context,String key){
//		SharedPreferences preferences = context.getSharedPreferences(Constant.LOGIN_SET, 0);
//		return preferences.getString(key, "");
//	}
	
	public static void saveSharedPreferences(Context context,String key,String value){
		
		SharedPreferences preferences = context.getSharedPreferences(Constant.LOGIN_SET, 0);
		preferences.edit()
			.putString(key, SecretUtil.AESencrypt(value)).commit();
	}
	
	public static String getSharedPreferences(Context context,String key){
		SharedPreferences preferences = context.getSharedPreferences(Constant.LOGIN_SET, 0);
		if(preferences.getString(key, "") == "")
			return "";
		return SecretUtil.AESdecrypt(preferences.getString(key, ""));
	}
	
	public static LoginConfig getLoginConfig(Context context) {
		// TODO Auto-generated method stub
		SharedPreferences preferences = context.getSharedPreferences(Constant.LOGIN_SET, 0);
		
		LoginConfig loginConfig = new LoginConfig();
		loginConfig.setXmppHost(preferences.getString(Constant.XMPP_HOST,
				context.getResources().getString(R.string.xmpp_host)));
		loginConfig.setXmppPort(preferences.getInt(Constant.XMPP_PORT,
				context.getResources().getInteger(R.integer.xmpp_port)));
		loginConfig.setUsername(SecretUtil.AESdecrypt(preferences.getString(Constant.USERNAME, null)));
		loginConfig.setPassword(SecretUtil.AESdecrypt(preferences.getString(Constant.PASSWORD, null)));
		loginConfig.setXmppServiceName(preferences.getString(Constant.XMPP_SEIVICE_NAME,
				context.getResources().getString(R.string.xmpp_service_name)));
		loginConfig.setAutoLogin(preferences.getBoolean(Constant.IS_AUTOLOGIN,
				context.getResources().getBoolean(R.bool.is_autologin)));
		loginConfig.setNovisible(preferences.getBoolean(Constant.IS_NOVISIBLE,
				context.getResources().getBoolean(R.bool.is_novisible)));
		loginConfig.setRemember(preferences.getBoolean(Constant.IS_REMEMBER,
				context.getResources().getBoolean(R.bool.is_remember)));
		loginConfig.setFirstStart(preferences.getBoolean(
				Constant.IS_FIRSTSTART, true));
		
		loginConfig.setPhoneNum(preferences.getString(Constant.PHONE_NUM,""));
		loginConfig.setBirthday(preferences.getString(Constant.BIRTHDAT,""));
		loginConfig.setEmail(preferences.getString(Constant.EMAIL,""));
		return loginConfig;
	}
}
