package com.time.oim.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.time.oim.model.Notice;
import com.time.oim.util.Constant;

import android.content.Context;
import android.content.SharedPreferences;

public class NoticeManager {
	private static NoticeManager noticeManager = null;
//	private static List<Notice> listNotice = null;
	private static Map<String,Notice> listNotice = null;
	private static int id = 0;
	
	private NoticeManager(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.LOGIN_SET, Context.MODE_PRIVATE);
//		listNotice = new ArrayList<Notice>();
		listNotice = new HashMap<String, Notice>();
	}
	
	public static NoticeManager getInstance(Context context) {

		if (noticeManager == null) {
			noticeManager = new NoticeManager(context);
		}

		return noticeManager;
	}

	public int saveNotice(Notice notice){
		id++;
		notice.setId(String.valueOf(id));
		listNotice.put(String.valueOf(id), notice);
//		listNotice.add(notice);
		return id;
	}
	
	public void removeNotice(String id){
		listNotice.remove(id);
	}
	
	public List<Notice> getUnReadNoticeList(){
		List<Notice> list = new ArrayList<Notice>();
		Iterator<String> it = listNotice.keySet().iterator();
		while(it.hasNext()){
			String key =(String) it.next();
			Notice noti = listNotice.get(key);
			list.add(noti);
		}
		return list;
	}
	
	public List<Notice> getUnReadNoticeList(int type){
		List<Notice> listnotice= null;
		
		return listnotice;
	}
	
	public Integer getUnReadNoticeCount(){
		
		return listNotice.size();
	}
	
	public Integer getUnReadNoticeCount(int type){
		
		return 0;
	}
	
}
