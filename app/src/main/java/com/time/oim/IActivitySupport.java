package com.time.oim;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import com.time.oim.manager.XmppApplication;
import com.time.oim.model.LoginConfig;

/**
 * Activity帮助支持类接口.
 * 
 * @author shimiso
 */
public interface IActivitySupport {
	/**
	 * 
	 * 获取EimApplication.
	 * 
	 * @author shimiso
	 * @update 2012-7-6 上午9:05:51
	 */
	public abstract XmppApplication getXmppApplication();

	/**
	 * 
	 * 终止服务.
	 */
	public abstract void stopService();

	/**
	 * 
	 * 开启服务.
	 */
	public abstract void startService();

	/**
	 * 
	 * 校验网络-如果没有网络就弹出设置,并返回true.
	 * 
	 */
	public abstract boolean validateInternet();

	/**
	 * 
	 * 校验网络-如果没有网络就返回true.
	 * 
	 */
	public abstract boolean hasInternetConnected();

	/**
	 * 
	 * 退出应用.
	 * 
	 */
	public abstract void isExit();

	/**
	 * 
	 * 判断GPS是否已经开启.
	 * 
	 */
	public abstract boolean hasLocationGPS();

	/**
	 * 
	 * 判断基站是否已经开启.
	 * 
	 */
	public abstract boolean hasLocationNetWork();

	/**
	 * 
	 * 检查内存卡.
	 * 
	 */
	public abstract void checkMemoryCard();

	/**
	 * 
	 * 显示toast.
	 * 
	 * @param text
	 *            内容
	 * @param longint
	 *            内容显示多长时间
	 */
	public abstract void showToast(String text, int longint);

	/**
	 * 
	 * 短时间显示toast.
	 * 
	 */
	public abstract void showToast(String text);

	/**
	 * 
	 * 获取进度条.
	 * 
	 */
	public abstract ProgressDialog getProgressDialog();

	/**
	 * 
	 * 返回当前Activity上下文.
	 * 
	 */
	public abstract Context getContext();

	/**
	 * 
	 * 获取当前登录用户的SharedPreferences配置.
	 * 
	 */
	public SharedPreferences getLoginUserSharedPre();

	/**
	 * 
	 * 保存用户配置.
	 * 
	 */
	public void saveLoginConfig(LoginConfig loginConfig);

	/**
	 * 
	 * 获取用户配置.
	 * 
	 */
	public LoginConfig getLoginConfig();

	/**
	 * 
	 * 用户是否在线（当前网络是否重连成功）
	 * 
	 */
	public boolean getUserOnlineState();

	/**
	 * 设置用户在线状态 true 在线 false 不在线
	 * 
	 * @param isOnline
	 */
	public void setUserOnlineState(boolean isOnline);

	/**
	 * 
	 * 发出Notification的method.
	 * 
	 * @param iconId
	 *            图标
	 * @param contentTitle
	 *            标题
	 * @param contentText
	 *            你内容
	 */
	public void setNotiType(int iconId, String contentTitle,
			String contentText, Class activity, String from);
}
