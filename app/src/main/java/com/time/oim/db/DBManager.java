package com.time.oim.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.time.oim.http.DataTrans;
import com.time.oim.model.Comment;
import com.time.oim.model.Msg;
import com.time.oim.model.Notice;
import com.time.oim.model.Poi;
import com.time.oim.model.Shuoshuo;
import com.time.oim.model.User;
import com.time.oim.util.DatetimeUtil;
import com.time.oim.util.StringUtil;

public class DBManager {
	private int version = 1;
	private String databaseName = "oimdb.db";

	private Context mContext = null;

	private static DBManager dBManager = null;
	/**
	 * 构造函数
	 * 
	 * @param mContext
	 */
	public DBManager(Context mContext) {
		super();
		this.mContext = mContext;

	}

	public static DBManager getInstance(Context mContext) {
		if (null == dBManager) {
			dBManager = new DBManager(mContext);
		}
		return dBManager;
	}


	public void closeDatabase(SQLiteDatabase dataBase, Cursor cursor) {
		if (null != dataBase) {
			dataBase.close();
		}
		if (null != cursor) {
			cursor.close();
		}
	}

	
	public SQLiteDatabase openDatabase() {
		return getDatabaseHelper().getWritableDatabase();
	}

	
	public DBHelper getDatabaseHelper() {
		return new DBHelper(mContext);
	}
	
	public long saveMsg(Msg msg,String table){
		
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);

		updateContacter(msg.getTo().split("@")[0], msg.getUsername().split("@")[0], DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss());
		return st.saveMsg(msg,table);
	}
	
	public long saveMyMsg(Msg msg,String table){
		
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);

		updateContacter(msg.getUsername().split("@")[0], msg.getTo().split("@")[0], DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss());
		return st.saveMsg(msg,table);
	}

	public void saveMsg(String id,int isSave){
		
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		st.saveMsg(id,isSave);
	}
	
	public void deleteMsg(String id){
		
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		st.deleteMsg(id);
	}
	
	public void updateMsg(String id,int status){
		
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		st.updateMsg(id,status);
	}
	
	public void clearMsgs(){
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		st.clearMsg();
	}
	
	public List<Msg> getOldMsgs(String from, String to){
		List<Msg> msgs = new ArrayList<Msg>();
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		msgs = st.getOldMsgs(from.split("@")[0], to.split("@")[0]);
		
		return msgs;
	}
	
	public List<Msg> getUnReadMsgs(String from, String to){
		List<Msg> msgs = new ArrayList<Msg>();
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		msgs = st.getUnReadMsgs(from.split("@")[0], to.split("@")[0]);
		
		return msgs;
	}
	
	public List<Msg> getUnReadMsgs(){
		List<Msg> msgs = new ArrayList<Msg>();
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		msgs = st.getUnReadMsgs();
		
		return msgs;
	}
	
	public long saveNotice(Notice notice){
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		updateContacter(notice.getTo().split("@")[0], notice.getFrom().split("@")[0], DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss());
		return st.saveNotice(notice);
	}
	
	public List<Notice> getUnReadNotice(String to){
		
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		
		return st.getUnReadNotice(to);
	}
	//poi
	public long savePoi(Poi poi){
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		
		return st.savePoi(poi);
	}
	public Poi getPoi(String id,String whos){
		
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		
		return st.getPoi(id,whos);
	}
	public List<Poi> getPois(String whos){
		
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		
		return st.getPois(whos);
	}
	
	public List<Notice> getUnReadConstactNotice(String to){
		
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		
		return st.getUnReadConstactNotice(to);
	}
	
	public List<Notice> getConstactNotice(String to){
		
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		
		return st.getConstactNotice(to);
	}
	
	public int getUnReadConstactNoticeNum(String to){
		
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		
		return st.getUnReadConstactNoticeNum(to);
	}
	
	public void updateNotice(String id){
		String sql = "update oim_notice set notice_status='1' where notice_id='"+id+"'";
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		st.execSQL(sql);
	}
	
	public void deleteNotice(String id){
		String sql = "delete from oim_notice where notice_id='"+id+"'";
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		st.execSQL(sql);
	}
	
	public void clearNotice(){
		
	}
	
	public long saveContacter(String owner,String contacter_name, String jid, String time){
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		
		return st.saveContacter(owner, contacter_name, jid, time);
	}
	
	public long saveContacter(String owner, User user){
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		
		return st.saveContacter(owner, user);
	}
	
	public void deleteContacter(String owner,String name){
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		
		st.deleteContacter(owner, name);
	}
	
	public void updateLocalContacter(String owner,final String name,final String time){
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		
		st.updateContacter(owner, name, time);
	}
	
	public void updateContacter(String owner,final String name,final String time){
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		
		st.updateContacter(owner, name, time);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(time != "" && time.trim()!="")
					DataTrans.getInstance(mContext).refreshContacter(name, time);
			}
		}).start();
	}
	
	public void clearContacter(){
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		
		st.clearContacter();
	}
	
	public List<User> getContacters(String owner){
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		
		return st.getContacters(owner);
	} 
	
	
	public HashMap<String,User> getHashMapContacters(String owner){
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		
		return st.getHashMapContacters(owner);
	}
	
	//shuoshuo
	public void saveShuoshuo(Shuoshuo shuoshuo){
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		
		st.saveShuoshuo(shuoshuo);
	}
	
	public void deleteShuoshuo(String id){
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		
		st.deleteShuoshuo(id);
	}
	
	public List<Shuoshuo> getShuoshuos(){
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		
		return st.getShuoshuos();
	}
	
	public void refreshshuoshuo(){
		SQLiteTemplate st = SQLiteTemplate.getInstance(dBManager, false);
		
		st.refresh();
	}
}
