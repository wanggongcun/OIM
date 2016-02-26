package com.time.oim.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatetimeUtil {
	public final static String yyyy = "yyyy";
	public final static String yyyy_MM_dd = "yyyy-MM-dd";
	public final static String yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";
	public final static String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
	public final static String yyyy_MM_dd_HH_mm_ss_SSS = "yyyy-MM-dd HH:mm:ss SSS";
	public final static String MM_dd_HH_mm_ss = "MM-dd  HH:mm:ss";
	public final static String FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static String now(String pattern) {
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		return df.format(Calendar.getInstance().getTime());
	}
	
	public static String dayago(int num) {
		SimpleDateFormat df = new SimpleDateFormat(yyyy_MM_dd_HH_mm_ss);
		Calendar ca = Calendar.getInstance();//得到一个Calendar的实例 
		ca.setTime(new Date()); //设置时间为当前时间 
		ca.add(Calendar.DATE, num); //年份减1 
		return df.format(ca.getTime());
	}
	
	public static boolean howlong(String datetime){
		
		Date date = new Date(datetime);
		Calendar ca = Calendar.getInstance();//得到一个Calendar的实例 
		ca.setTime(new Date()); //设置时间为当前时间 
		ca.add(Calendar.DATE, -31);
		Date monthage =ca.getTime();
		
		if(monthage.after(date)){
			return false;
		}else{
			return true;
		}
	}
	
	public static String now_yyyy() {
		return now(yyyy);
	}

	public static String now_yyyy_MM_dd() {
		return now(yyyy_MM_dd);
	}

	public static String now_yyyy_MM_dd_HH_mm_ss() {
		return now(yyyy_MM_dd_HH_mm_ss);
	}

	public static String now_yyyy_MM_dd_HH_mm_ss_SSS() {
		return now(yyyy_MM_dd_HH_mm_ss_SSS);
	}
	
	public static String now_MM_dd_HH_mm_ss() {
		return now(MM_dd_HH_mm_ss);
	}
	
	public static Date str2Date(String str) {
		return str2Date(str, null);
	}

	public static Date str2Date(String str, String format) {
		if (str == null || str.length() == 0) {
			return null;
		}
		if (format == null || format.length() == 0) {
			format = FORMAT;
		}
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			date = sdf.parse(str);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;

	}

	public static Calendar str2Calendar(String str) {
		return str2Calendar(str, null);

	}

	public static Calendar str2Calendar(String str, String format) {

		Date date = str2Date(str, format);
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		return c;

	}

	public static String date2Str(Calendar c) {// yyyy-MM-dd HH:mm:ss
		return date2Str(c, null);
	}

	public static String date2Str(Calendar c, String format) {
		if (c == null) {
			return null;
		}
		return date2Str(c.getTime(), format);
	}

	public static String date2Str(Date d) {// yyyy-MM-dd HH:mm:ss
		return date2Str(d, null);
	}

	public static String date2Str(Date d, String format) {// yyyy-MM-dd HH:mm:ss
		if (d == null) {
			return null;
		}
		if (format == null || format.length() == 0) {
			format = FORMAT;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String s = sdf.format(d);
		return s;
	}

	public static String getCurDateStr() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-"
				+ c.get(Calendar.DAY_OF_MONTH) + "-"
				+ c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE)
				+ ":" + c.get(Calendar.SECOND);
	}

	/**
	 * 获得当前日期的字符串格式
	 * 
	 * @param format
	 * @return
	 */
	public static String getCurDateStr(String format) {
		Calendar c = Calendar.getInstance();
		return date2Str(c, format);
	}

	// 格式到秒
	public static String getMillon(long time) {

		return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(time);

	}

	// 格式到天
	public static String getDay(long time) {

		return new SimpleDateFormat("yyyy-MM-dd").format(time);

	}

	// 格式到毫秒
	public static String getSMillon(long time) {

		return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(time);

	}
}
