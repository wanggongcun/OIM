package com.time.oim.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.webkit.WebChromeClient.CustomViewCallback;

import com.time.oim.http.DataTrans;
import com.time.oim.model.Comment;
import com.time.oim.model.Msg;
import com.time.oim.model.Notice;
import com.time.oim.model.Poi;
import com.time.oim.model.Shuoshuo;
import com.time.oim.model.User;
import com.time.oim.util.DatetimeUtil;
import com.time.oim.util.StringUtil;

public class SQLiteTemplate {
	
	private DBManager dBManager;
	private boolean isTransaction = false;
	private SQLiteDatabase dataBase = null;
	
	private SQLiteTemplate() {
		
	}

	private SQLiteTemplate(DBManager dBManager, boolean isTransaction) {
		this.dBManager = dBManager;
		this.isTransaction = isTransaction;
	}
	
	public static SQLiteTemplate getInstance(DBManager dBManager,
			boolean isTransaction) {
		return new SQLiteTemplate(dBManager, isTransaction);
	}
	//msg
	public long saveMsg(Msg msg, String table){
		ContentValues contentValues = new ContentValues();
		if (StringUtil.notEmpty(msg.getDatetime())) {
			contentValues.put("msg_time", StringUtil.doEmpty(msg.getDatetime()));
		}
		if (StringUtil.notEmpty(msg.getMsg())) {
			contentValues.put("msg_content",
					StringUtil.doEmpty(msg.getMsg()));
		}
		if (StringUtil.notEmpty(msg.getTo())) {
			contentValues.put("msg_to", StringUtil.doEmpty(msg.getTo().split("@")[0]));
		}
		if (StringUtil.notEmpty(msg.getUsername())) {
			contentValues.put("msg_from",
					StringUtil.doEmpty(msg.getUsername().split("@")[0]));
		}
		contentValues.put("msg_type", msg.getType());
		contentValues.put("msg_save", 0);
		contentValues.put("msg_inorout", msg.getInorOut());
		return insert(table, contentValues);
	}
	
	public void saveMsg(String id,int isSave){
		String sql = "update oim_msg set msg_save='"+String.valueOf(isSave)+"' where msg_id='" + id + "'";
		execSQL(sql);
	}

	
	public void deleteMsg(String id){
		String sql = "delete from oim_msg where msg_id='" + id + "'";
		execSQL(sql);
	}
	
	public void updateMsg(String id,int status){
		String sql = "update oim_msg set msg_inorout='"+String.valueOf(status)+
				"' where msg_id='" + id + "'";
		execSQL(sql);
	}
	public void clearMsgs(){
		String sql = "delete from oim_msg where msg_save='0'";
		execSQL(sql);
	}
	
	public List<Msg> getOldMsgs(String from, String to){
		List<Msg> msgs = new ArrayList<Msg>();
		Cursor cursor = null;
		Cursor cursor2 = null;
		String sql= "select * from oim_msg where msg_from=? and msg_to=?";
		String[] selectionArgs = {from,to};
		String[] selectionArgs2 = {to,from};
		
		try {
			dataBase = null;
			dataBase = dBManager.openDatabase();
			cursor = dataBase.rawQuery(sql, selectionArgs);
			while (cursor.moveToNext()) {
				Msg msg = new Msg();
				msg.setId(String.valueOf(cursor.getInt(cursor.getColumnIndex("msg_id"))));
				msg.setDatetime(cursor.getString(cursor.getColumnIndex("msg_time")));
				msg.setMsg(cursor.getString(cursor.getColumnIndex("msg_content")));
				msg.setUsername(cursor.getString(cursor.getColumnIndex("msg_from")));
				msg.setTo(cursor.getString(cursor.getColumnIndex("msg_to")));
				msg.setType(cursor.getInt(cursor.getColumnIndex("msg_type")));
				msg.setInorOut(cursor.getInt(cursor.getColumnIndex("msg_inorout")));
				msg.setSave(cursor.getInt(cursor.getColumnIndex("msg_save")));
				msgs.add(msg);
			}
			cursor2 = dataBase.rawQuery(sql, selectionArgs2);
			while (cursor2.moveToNext()) {
				Msg msg = new Msg();
				msg.setId(String.valueOf(cursor2.getInt(cursor2.getColumnIndex("msg_id"))));
				msg.setDatetime(cursor2.getString(cursor2.getColumnIndex("msg_time")));
				msg.setMsg(cursor2.getString(cursor2.getColumnIndex("msg_content")));
				msg.setUsername(cursor2.getString(cursor2.getColumnIndex("msg_from")));
				msg.setTo(cursor2.getString(cursor2.getColumnIndex("msg_to")));
				msg.setType(cursor2.getInt(cursor2.getColumnIndex("msg_type")));
				msg.setInorOut(cursor2.getInt(cursor2.getColumnIndex("msg_inorout")));
				msg.setSave(cursor2.getInt(cursor2.getColumnIndex("msg_save")));
				msgs.add(msg);
			}
		} finally {
			cursor.close();
			dataBase.close();
		}
		Collections.sort(msgs, new Comparator<Msg>() {

			@Override
			public int compare(Msg lhs, Msg rhs) {
				// TODO Auto-generated method stub
				Date d1 = DatetimeUtil.str2Date(lhs.getDatetime());
				Date d2 = DatetimeUtil.str2Date(rhs.getDatetime());
				if (d2.before(d1)) {  
                    return 1;  
                }  
                return -1;
			}
		});
		
		return msgs;
	}
	
	public List<Msg> getUnReadMsgs(String from, String to){
		List<Msg> msgs = new ArrayList<Msg>();
		Cursor cursor = null;
		String sql= "select * from oim_msg_unread where msg_from=? and msg_to=?";
		String sql1 = "delete from oim_msg_unread where msg_from='"+from+"' and msg_to='"+to+"'";
		String[] selectionArgs = {from,to};
		
		try {
			dataBase = null;
			dataBase = dBManager.openDatabase();
			cursor = dataBase.rawQuery(sql, selectionArgs);
			while (cursor.moveToNext()) {
				Msg msg = new Msg();
				msg.setId(String.valueOf(cursor.getInt(cursor.getColumnIndex("msg_id"))));
				msg.setDatetime(cursor.getString(cursor.getColumnIndex("msg_time")));
				msg.setMsg(cursor.getString(cursor.getColumnIndex("msg_content")));
				msg.setUsername(cursor.getString(cursor.getColumnIndex("msg_from")));
				msg.setTo(cursor.getString(cursor.getColumnIndex("msg_to")));
				msg.setType(cursor.getInt(cursor.getColumnIndex("msg_type")));
				msg.setInorOut(cursor.getInt(cursor.getColumnIndex("msg_inorout")));
				msgs.add(msg);
			}
			
			dataBase.execSQL(sql1);
		} finally {
			cursor.close();
			dataBase.close();
		}
		Collections.sort(msgs, new Comparator<Msg>() {

			@Override
			public int compare(Msg lhs, Msg rhs) {
				// TODO Auto-generated method stub
				Date d1 = DatetimeUtil.str2Date(lhs.getDatetime());
				Date d2 = DatetimeUtil.str2Date(rhs.getDatetime());
				if (d2.before(d1)) {  
                    return 1;  
                }  
                return -1;
			}
		});
		return msgs;
	}
	
	public List<Msg> getUnReadMsgs(){
		List<Msg> msgs = new ArrayList<Msg>();
		Cursor cursor = null;
		String sql= "select * from oim_msg_unread";
		
		try {
			dataBase = null;
			dataBase = dBManager.openDatabase();
			cursor = dataBase.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				Msg msg = new Msg();
				msg.setId(String.valueOf(cursor.getInt(cursor.getColumnIndex("msg_id"))));
				msg.setDatetime(cursor.getString(cursor.getColumnIndex("msg_time")));
				msg.setMsg(cursor.getString(cursor.getColumnIndex("msg_content")));
				msg.setUsername(cursor.getString(cursor.getColumnIndex("msg_from")));
				msg.setTo(cursor.getString(cursor.getColumnIndex("msg_to")));
				msg.setType(cursor.getInt(cursor.getColumnIndex("msg_type")));
				msg.setInorOut(cursor.getInt(cursor.getColumnIndex("msg_inorout")));
				msgs.add(msg);
				
			}
			
		} finally {
			cursor.close();
			dataBase.close();
		}
		Collections.sort(msgs, new Comparator<Msg>() {

			@Override
			public int compare(Msg lhs, Msg rhs) {
				// TODO Auto-generated method stub
				Date d1 = DatetimeUtil.str2Date(lhs.getDatetime());
				Date d2 = DatetimeUtil.str2Date(rhs.getDatetime());
				if (d2.before(d1)) {  
                    return 1;  
                }  
                return -1;
			}
		});
		return msgs;
	}
	public void clearUnSaveMsg(){
		String sql = "delete from oim_msg where msg_save='0'";
		execSQL(sql);
	}
	
	public void clearMsg(){
		String time = DatetimeUtil.dayago(-7);
		String sql = "delete from oim_msg where msg_time<'"+time+"' and msg_save='0'";
		execSQL(sql);
	}
	
	//notice
	public long saveNotice(Notice notice){
		ContentValues contentValues = new ContentValues();
		if (StringUtil.notEmpty(notice.getTitle())) {
			contentValues.put("notice_title", StringUtil.doEmpty(notice.getTitle()));
		}
		if (StringUtil.notEmpty(notice.getContent())) {
			contentValues.put("notice_content",
					StringUtil.doEmpty(notice.getContent()));
		}
		if (StringUtil.notEmpty(notice.getTo())) {
			contentValues.put("notice_to", StringUtil.doEmpty(notice.getTo().split("@")[0]));
		}
		if (StringUtil.notEmpty(notice.getFrom())) {
			contentValues.put("notice_from",
					StringUtil.doEmpty(notice.getFrom()));
		}
		if (StringUtil.notEmpty(notice.getNoticeTime())) {
			contentValues.put("notice_time",
					StringUtil.doEmpty(notice.getNoticeTime()));
		}
		contentValues.put("notice_type", notice.getNoticeType());
		contentValues.put("notice_status", notice.getStatus());
		return insert("oim_notice", contentValues);
	}
	
	public List<Notice> getUnReadNotice(String to){
		List<Notice> notices = new ArrayList<Notice>();
		Cursor cursor = null;
		String sql = "select * from oim_notice where notice_to=? and notice_status='0'";
		String [] selectionArgs = {to};
		try{
			dataBase = dBManager.openDatabase();
			cursor = dataBase.rawQuery(sql, selectionArgs);
			while(cursor.moveToNext()){
				Notice notice = new Notice();
				notice.setId(String.valueOf(cursor.getInt(cursor.getColumnIndex("notice_id"))));
				notice.setFrom(cursor.getString(cursor.getColumnIndex("notice_from")));
				notice.setTo(cursor.getString(cursor.getColumnIndex("notice_to")));
				notice.setContent(cursor.getString(cursor.getColumnIndex("notice_content")));
				notice.setNoticeTime(cursor.getString(cursor.getColumnIndex("notice_time")));
				notice.setTitle(cursor.getString(cursor.getColumnIndex("notice_title")));
				notice.setNoticeType(cursor.getInt(cursor.getColumnIndex("notice_type")));
				notice.setStatus(cursor.getInt(cursor.getColumnIndex("notice_status")));
			}
		}catch(Exception e){
			
		}finally{
			cursor.close();
			dataBase.close();
		}
		
		
		return notices;
	}
	
	public List<Notice> getUnReadConstactNotice(String to){
		List<Notice> notices = new ArrayList<Notice>();
		Cursor cursor = null;
		String sql = "select * from oim_notice where notice_to=? and notice_status='0' and notice_type='1'";
		String [] selectionArgs = {to};
		try{
			dataBase = dBManager.openDatabase();
			cursor = dataBase.rawQuery(sql, selectionArgs);
			while(cursor.moveToNext()){
				Notice notice = new Notice();
				notice.setId(String.valueOf(cursor.getInt(cursor.getColumnIndex("notice_id"))));
				notice.setFrom(cursor.getString(cursor.getColumnIndex("notice_from")));
				notice.setTo(cursor.getString(cursor.getColumnIndex("notice_to")));
				notice.setContent(cursor.getString(cursor.getColumnIndex("notice_content")));
				notice.setNoticeTime(cursor.getString(cursor.getColumnIndex("notice_time")));
				notice.setTitle(cursor.getString(cursor.getColumnIndex("notice_title")));
				notice.setNoticeType(cursor.getInt(cursor.getColumnIndex("notice_type")));
				notice.setStatus(cursor.getInt(cursor.getColumnIndex("notice_status")));
				notices.add(notice);
			}
		}catch(Exception e){
			
		}finally{
			cursor.close();
			dataBase.close();
		}
		
		
		return notices;
	}
	
	public List<Notice> getConstactNotice(String to){
		List<Notice> notices = new ArrayList<Notice>();
		Cursor cursor = null;
		String sql = "select * from oim_notice where notice_to=? and notice_type='1'";
		String [] selectionArgs = {to};
		try{
			dataBase = dBManager.openDatabase();
			cursor = dataBase.rawQuery(sql, selectionArgs);
			while(cursor.moveToNext()){
				Notice notice = new Notice();
				notice.setId(String.valueOf(cursor.getInt(cursor.getColumnIndex("notice_id"))));
				notice.setFrom(cursor.getString(cursor.getColumnIndex("notice_from")));
				notice.setTo(cursor.getString(cursor.getColumnIndex("notice_to")));
				notice.setContent(cursor.getString(cursor.getColumnIndex("notice_content")));
				notice.setNoticeTime(cursor.getString(cursor.getColumnIndex("notice_time")));
				notice.setTitle(cursor.getString(cursor.getColumnIndex("notice_title")));
				notice.setNoticeType(cursor.getInt(cursor.getColumnIndex("notice_type")));
				notice.setStatus(cursor.getInt(cursor.getColumnIndex("notice_status")));
				notices.add(notice);
			}
		}catch(Exception e){
			
		}finally{
			cursor.close();
			dataBase.close();
		}
		
		
		return notices;
	}
	
	public int getUnReadConstactNoticeNum(String to){
		List<Notice> notices = new ArrayList<Notice>();
		Cursor cursor = null;
		String sql = "select * from oim_notice where notice_to=? and notice_status='0' and notice_type='1'";
		String [] selectionArgs = {to};
		try{
			dataBase = dBManager.openDatabase();
			cursor = dataBase.rawQuery(sql, selectionArgs);
			while(cursor.moveToNext()){
				Notice notice = new Notice();
				notice.setId(String.valueOf(cursor.getInt(cursor.getColumnIndex("notice_id"))));
				notice.setFrom(cursor.getString(cursor.getColumnIndex("notice_from")));
				notice.setTo(cursor.getString(cursor.getColumnIndex("notice_to")));
				notice.setContent(cursor.getString(cursor.getColumnIndex("notice_content")));
				notice.setNoticeTime(cursor.getString(cursor.getColumnIndex("notice_time")));
				notice.setTitle(cursor.getString(cursor.getColumnIndex("notice_title")));
				notice.setNoticeType(cursor.getInt(cursor.getColumnIndex("notice_type")));
				notice.setStatus(cursor.getInt(cursor.getColumnIndex("notice_status")));
				notices.add(notice);
			}
		}catch(Exception e){
			
		}finally{
			cursor.close();
			dataBase.close();
		}
		
		
		return notices.size();
	}
	//contacter
	public long saveContacter(String owner,String name, String jid, String time){
		Cursor cursor = null;
		String sql = "select * from oim_contacter where contacter_owner=? and contacter_name=?";
		String [] selectionArgs = {owner,name};
		try{
			dataBase = dBManager.openDatabase();
			cursor = dataBase.rawQuery(sql, selectionArgs);
			while(cursor.moveToNext()){
				return cursor.getInt(cursor.getColumnIndex("contacter_id"));
			}
		}catch(Exception e){
			
		}finally{
			cursor.close();
			dataBase.close();
		}
		ContentValues contentValues = new ContentValues();
		
		contentValues.put("contacter_owner", owner);
		contentValues.put("contacter_name", name);
		contentValues.put("contacter_jid", jid);
		contentValues.put("contacter_lasttime", time);
		
		return insert("oim_contacter",contentValues);
	}
	
	public long saveContacter(String owner, User user){
		Cursor cursor = null;
		String sql = "select * from oim_contacter where contacter_owner=? and contacter_name=?";
		String [] selectionArgs = {owner,user.getName()};
		try{
			dataBase = dBManager.openDatabase();
			cursor = dataBase.rawQuery(sql, selectionArgs);
			while(cursor.moveToNext()){
				user.setLastTime(cursor.getString(cursor.getColumnIndex("contacter_lasttime")));
				return 0;
			}
		}catch(Exception e){
			
		}finally{
			cursor.close();
			dataBase.close();
		}
		ContentValues contentValues = new ContentValues();
		
		contentValues.put("contacter_owner", owner);
		contentValues.put("contacter_name", user.getName());
		contentValues.put("contacter_jid", user.getJID());
		contentValues.put("contacter_lasttime", user.getLastTime());
		
		return insert("oim_contacter",contentValues);
	}
	
	public void deleteContacter(String owner,String name){
		String sql = "delete from oim_contacter where contacter_owner='"+owner+"' and contacter_name='"+name+"'";
		execSQL(sql);
	}
	
	public void clearContacter(){
		String sql = "delete from oim_contacter";
		execSQL(sql);
	}
	
	public void updateContacter(String owner,String name,String time){
		String sql = "";
		if(time == "" || time.trim() == "" || time == null || time.trim() ==null){
			sql = "update oim_contacter set contacter_lasttime='" + DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss() + 
					"' where contacter_owner='" + owner + "' and contacter_name='" + name + "'";
			
		}else{
			sql = "update oim_contacter set contacter_lasttime='" + time + 
					"' where contacter_owner='" + owner + "' and contacter_name='" + name + "'";
		}
		
		execSQL(sql);
	}
	
	public List<User> getContacters(String owner){
		List<User> users = new ArrayList<User>();
		Cursor cursor = null;
		String sql = "select * from oim_contacter where contacter_owner=?";
		String [] selectionArgs = {owner};
		try{
			dataBase = dBManager.openDatabase();
			cursor = dataBase.rawQuery(sql, selectionArgs);
			while(cursor.moveToNext()){
				User user = new User();
				user.setName(cursor.getString(cursor.getColumnIndex("contacter_name")));
				user.setJID(cursor.getString(cursor.getColumnIndex("contacter_jid")));
				user.setLastTime(cursor.getString(cursor.getColumnIndex("contacter_lasttime")));
				users.add(user);
			}
		}catch(Exception e){
			
		}finally{
			cursor.close();
			dataBase.close();
		}
		
		return users;
	}
	
	public HashMap<String,User> getHashMapContacters(String owner){
		HashMap<String,User> users = new HashMap<String, User>();
		Cursor cursor = null;
		String sql = "select * from oim_contacter where contacter_owner=?";
		String [] selectionArgs = {owner};
		try{
			dataBase = dBManager.openDatabase();
			cursor = dataBase.rawQuery(sql, selectionArgs);
			while(cursor.moveToNext()){
				User user = new User();
				user.setName(cursor.getString(cursor.getColumnIndex("contacter_name")));
				user.setJID(cursor.getString(cursor.getColumnIndex("contacter_jid")));
				user.setLastTime(cursor.getString(cursor.getColumnIndex("contacter_lasttime")));
				String owners = cursor.getString(cursor.getColumnIndex("contacter_owner"));
				if(!users.containsKey(user.getName()))
					users.put(user.getName(), user);
			}
		}catch(Exception e){
			
		}finally{
			cursor.close();
			dataBase.close();
		}
		
		return users;
	}
	
	//poi
	public long savePoi(Poi poi){
		ContentValues contentValues = new ContentValues();
		if (StringUtil.notEmpty(poi.getTime())) {
			contentValues.put("poi_time", StringUtil.doEmpty(poi.getTime()));
		}
		if (StringUtil.notEmpty(poi.getLat())) {
			contentValues.put("poi_latitude",
					String.valueOf(poi.getLat()));
		}
		if (StringUtil.notEmpty(poi.getLon())) {
			contentValues.put("poi_longitude",
					String.valueOf(poi.getLon()));
		}
		if (StringUtil.notEmpty(poi.getDese())) {
			contentValues.put("poi_describe", StringUtil.doEmpty(poi.getDese()));
		}
		if (StringUtil.notEmpty(poi.getPath())) {
			contentValues.put("poi_imgpath", StringUtil.doEmpty(poi.getPath()));
		}
		if (StringUtil.notEmpty(poi.getWhos())) {
			contentValues.put("poi_whos", StringUtil.doEmpty(poi.getWhos()));
		}
		
		return insert("oim_notes", contentValues);
	}
	
	public Poi getPoi(String id,String whos){
		Poi poi = new Poi();
		Cursor cursor = null;
		String sql = "select * from oim_notes where poi_id=? and poi_whos=?";
		String [] selectionArgs = {id,whos};
		try{
			dataBase = dBManager.openDatabase();
			cursor = dataBase.rawQuery(sql, selectionArgs);
			while(cursor.moveToNext()){
				Notice notice = new Notice();
				notice.setId(String.valueOf(cursor.getInt(cursor.getColumnIndex("notice_id"))));
				notice.setFrom(cursor.getString(cursor.getColumnIndex("notice_from")));
				notice.setTo(cursor.getString(cursor.getColumnIndex("notice_to")));
				notice.setContent(cursor.getString(cursor.getColumnIndex("notice_content")));
				notice.setNoticeTime(cursor.getString(cursor.getColumnIndex("notice_time")));
				notice.setTitle(cursor.getString(cursor.getColumnIndex("notice_title")));
				notice.setNoticeType(cursor.getInt(cursor.getColumnIndex("notice_type")));
				notice.setStatus(cursor.getInt(cursor.getColumnIndex("notice_status")));
				
				poi.setId(cursor.getInt(cursor.getColumnIndex("poi_id")));
				poi.setTime(cursor.getString(cursor.getColumnIndex("poi_time")));
				poi.setDes(cursor.getString(cursor.getColumnIndex("poi_describe")));
				poi.setPath(cursor.getString(cursor.getColumnIndex("poi_imgpath")));
				poi.setLoca(cursor.getDouble(cursor.getColumnIndex("poi_latitude")), 
						cursor.getDouble(cursor.getColumnIndex("poi_longitude")));
				poi.setWhos(cursor.getString(cursor.getColumnIndex("poi_whos")));
			}
		}catch(Exception e){
			
		}finally{
			cursor.close();
			dataBase.close();
		}
		return poi;
	}
	
	public List<Poi> getPois(String whos){
		List<Poi> pois = new ArrayList<Poi>();
		Cursor cursor = null;
		String sql = "select * from oim_notes where poi_whos=?";
		String [] selectionArgs = {whos};
		try{
			dataBase = dBManager.openDatabase();
			cursor = dataBase.rawQuery(sql, selectionArgs);
			while(cursor.moveToNext()){
				
				Poi poi = new Poi();
				poi.setId(cursor.getInt(cursor.getColumnIndex("poi_id")));
				poi.setTime(cursor.getString(cursor.getColumnIndex("poi_time")));
				poi.setDes(cursor.getString(cursor.getColumnIndex("poi_describe")));
				poi.setPath(cursor.getString(cursor.getColumnIndex("poi_imgpath")));
				poi.setLoca(cursor.getDouble(cursor.getColumnIndex("poi_latitude")), 
						cursor.getDouble(cursor.getColumnIndex("poi_longitude")));
				poi.setWhos(cursor.getString(cursor.getColumnIndex("poi_whos")));
				pois.add(poi);
			}
		}catch(Exception e){
			e.getMessage();
		}finally{
			cursor.close();
			dataBase.close();
		}
		return pois;
	}
	
	//shuoshuo
 	public void saveShuoshuo(Shuoshuo shuoshuo){
		deleteShuoshuo(shuoshuo.getShuoshuoID());
		
		ContentValues contentValues = new ContentValues();
		
		contentValues.put("shuoshuo_id", shuoshuo.getShuoshuoID());
		contentValues.put("shuoshuo_name", shuoshuo.getUsername());
		contentValues.put("shuoshuo_time", shuoshuo.getTime());
		contentValues.put("shuoshuo_lasttime", shuoshuo.getLastTime());
		contentValues.put("shuoshuo_content", shuoshuo.getContent());
		contentValues.put("shuoshuo_path", shuoshuo.getImageURL());
		contentValues.put("shuoshuo_isread", shuoshuo.isReadImage());
		contentValues.put("shuoshuo_type", shuoshuo.hasImage());
		
		insert("oim_shuoshuo",contentValues);
		
		List<Comment> cs = new ArrayList<Comment>();
		cs = shuoshuo.getComments();
		for(int i=0;i<cs.size();i++){
			contentValues = null;
			contentValues = new ContentValues();
			contentValues.put("shuoshuo_id", cs.get(i).getShuoshuoID());
			contentValues.put("comment_id", cs.get(i).getCommentID());
			contentValues.put("comment_name", cs.get(i).getFrom());
			contentValues.put("comment_time", cs.get(i).getTime());
			contentValues.put("comment_content", cs.get(i).getContent());
			
			insert("oim_comment",contentValues);
		}
	}
	
	public void deleteShuoshuo(String id){
		String sql = "delete from oim_shuoshuo where shuoshuo_id='"+id+"'";
		String sql1 = "delete from oim_comment where shuoshuo_id='"+id+"'";
		execSQL(sql);
		execSQL(sql1);
	}
	
	public List<Shuoshuo> getShuoshuos(){
		List<Shuoshuo> shuoshuos = new ArrayList<Shuoshuo>();
		Cursor cursor = null;
		String sql = "select * from oim_shuoshuo";
		try{
			dataBase = dBManager.openDatabase();
			cursor = dataBase.rawQuery(sql, null);
			while(cursor.moveToNext()){
				Shuoshuo s = new Shuoshuo();
				s.setShuoshuoID(cursor.getString(cursor.getColumnIndex("shuoshuo_id")));
				s.setContent(cursor.getString(cursor.getColumnIndex("shuoshuo_content")));
				s.setUsername(cursor.getString(cursor.getColumnIndex("shuoshuo_name")));
				s.setTime(cursor.getString(cursor.getColumnIndex("shuoshuo_time")));
				s.setLastTime(cursor.getString(cursor.getColumnIndex("shuoshuo_lasttime")));
				s.setHasImage(cursor.getInt(cursor.getColumnIndex("shuoshuo_type")));
				s.setIsReadImage(cursor.getInt(cursor.getColumnIndex("shuoshuo_isread")));
				s.setImageURL(cursor.getString(cursor.getColumnIndex("shuoshuo_path")));
				
				s.addComments(getComments(cursor.getString(cursor.getColumnIndex("shuoshuo_id"))));
				shuoshuos.add(s);
			}
		}catch(Exception e){
			
		}finally{
			cursor.close();
			dataBase.close();
		}
		return shuoshuos;
	}
	
	public List<Comment> getComments(String id){
		List<Comment> comments = new ArrayList<Comment>();
		Cursor cursor = null;
		String sql = "select * from oim_comment where shuoshuo_id=?";
		String[] selectionArgs = {id};
		try{
			dataBase = dBManager.openDatabase();
			cursor = dataBase.rawQuery(sql, selectionArgs);
			while(cursor.moveToNext()){
				Comment c = new Comment();
				c.setContent(cursor.getString(cursor.getColumnIndex("comment_content")));
				c.setShuoshuoID(cursor.getString(cursor.getColumnIndex("comment_id")));
				c.setFrom(cursor.getString(cursor.getColumnIndex("comment_name")));
				c.setTime(cursor.getString(cursor.getColumnIndex("comment_time")));
				comments.add(c);
			}
		}catch(Exception e){
			
		}finally{
			cursor.close();
			dataBase.close();
		}
		return comments;
	}
	
	public void refresh(){
		Cursor cursor = null;
		String sql = "select * from oim_shuoshuo";
		try{
			dataBase = dBManager.openDatabase();
			cursor = dataBase.rawQuery(sql, null);
			while(cursor.moveToNext()){
				Date date1 = DatetimeUtil.str2Date(cursor.getString(cursor.getColumnIndex("shuoshuo_lasttime")));
				Date date2 = DatetimeUtil.str2Date(DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss());
				int day = (int) ((date2.getTime()-date1.getTime())/(1000*60*60*24));
				if(day >= 1){
					deleteShuoshuo(cursor.getString(cursor.getColumnIndex("shuoshuo_id")));
				}
			}
		}catch(Exception e){
			
		}finally{
			cursor.close();
			dataBase.close();
		}
	}
	
	public void execSQL(String sql) {
		try {
			dataBase = dBManager.openDatabase();
			dataBase.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!isTransaction) {
				closeDatabase(null);
			}
		}
	}
	
	public long insert(String table, ContentValues content) {
		try {
			dataBase = dBManager.openDatabase();
			// insert方法第一参数：数据库表名，第二个参数如果CONTENT为空时则向表中插入一个NULL,第三个参数为插入的内容
			return dataBase.insert(table, null, content);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!isTransaction) {
				closeDatabase(null);
			}
		}
		return 0;
	}
	
	public int updateById(String table, String table_id, String id,ContentValues values) {
		try {
			dataBase = dBManager.openDatabase();
			
			return dataBase.update(table, values, table_id + "=?",
					new String[] { id });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!isTransaction) {
				closeDatabase(null);
			}
		}
		return 0;
	}
	
	public int update(String table, ContentValues values, String whereClause,
			String[] whereArgs) {
		try {
			dataBase = dBManager.openDatabase();
			return dataBase.update(table, values, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!isTransaction) {
				closeDatabase(null);
			}
		}
		return 0;
	}
	
	public void deleteByIds(String table, Object... primaryKeys) {
		try {
			if (primaryKeys.length > 0) {
				StringBuilder sb = new StringBuilder();
				for (@SuppressWarnings("unused")
				Object id : primaryKeys) {
					sb.append("?").append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
				dataBase = dBManager.openDatabase();
//				dataBase.execSQL("delete from " + table + " where "
//						+ mPrimaryKey + " in(" + sb + ")",
//						(Object[]) primaryKeys);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!isTransaction) {
				closeDatabase(null);
			}
		}
	}
	
	public int deleteByField(String table, String field, String value) {
		try {
			dataBase = dBManager.openDatabase();
			return dataBase.delete(table, field + "=?", new String[] { value });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!isTransaction) {
				closeDatabase(null);
			}
		}
		return 0;
	}
	
	public int deleteById(String table, String id, String value) {
		try {
			dataBase = dBManager.openDatabase();
			return deleteByField(table, id ,value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!isTransaction) {
				closeDatabase(null);
			}
		}
		return 0;
	}
	
	public Boolean isExistsById(String table, String id, String value) {
		try {
			dataBase = dBManager.openDatabase();
			return isExistsByField(table, id, value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!isTransaction) {
				closeDatabase(null);
			}
		}
		return null;
	}
	
	public Boolean isExistsByField(String table, String field, String value) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM ").append(table).append(" WHERE ")
				.append(field).append(" =?");
		try {
			dataBase = dBManager.openDatabase();
			return isExistsBySQL(sql.toString(), new String[] { value });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!isTransaction) {
				closeDatabase(null);
			}
		}
		return null;
	}
	
	public Boolean isExistsBySQL(String sql, String[] selectionArgs) {
		Cursor cursor = null;
		try {
			dataBase = dBManager.openDatabase();
			cursor = dataBase.rawQuery(sql, selectionArgs);
			if (cursor.moveToFirst()) {
				return (cursor.getInt(0) > 0);
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!isTransaction) {
				closeDatabase(cursor);
			}
		}
		return null;
	}
	
	public void closeDatabase(Cursor cursor) {
		if (null != dataBase) {
			dataBase.close();
		}
		if (null != cursor) {
			cursor.close();
		}
	}
}
