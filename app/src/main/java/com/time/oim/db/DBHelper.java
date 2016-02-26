package com.time.oim.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

	private static final String db_name = "oimdb.db";
	private static final int version = 1;
	
	public DBHelper(Context context) {
		super(context, db_name, null, version);
		// TODO Auto-generated constructor stub
	}
	
	public DBHelper(Context context, String name, int version) {
		super(context, db_name, null, version);
		// TODO Auto-generated constructor stub
	}
	
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		String sql_msg = "create table oim_msg(msg_id integer primary key,"
				+ "msg_from varchat(32),"
				+ "msg_to varchar(32),"
				+ "msg_content TEXT,"
				+ "msg_type integer,"
				+ "msg_time varchar(32),"
				+ "msg_inorout integer default '0',"
				+ "msg_save integer default '0')";
		String sql_msg_unread = "create table oim_msg_unread(msg_id integer primary key,"
				+ "msg_from varchat(32),"
				+ "msg_to varchar(32),"
				+ "msg_content TEXT,"
				+ "msg_type integer,"
				+ "msg_time varchar(32),"
				+ "msg_inorout integer default '0',"
				+ "msg_save integer default '0')";

		String sql_notice = "create table oim_notice(notice_id integer primary key,"
				+ "notice_from varchar(32),"
				+ "notice_to varchar(32),"
				+ "notice_title TEXT,"
				+ "notice_content TEXT,"
				+ "notice_status integer default '0',"
				+ "notice_time varchar(16),"
				+ "notice_type integer default '0')";
		
		String sql_contacter = "create table oim_contacter(contacter_id integer primary key,"
				+ "contacter_jid varchar(64),"
				+ "contacter_name varchar(32),"
				+ "contacter_lasttime varchar(16),"
				+ "contacter_owner varchar(32))";
		
		String sql_shuoshuo = "create table oim_shuoshuo(shuoshuo_id integer primary key,"
				+ "shuoshuo_name varchar(32),"
				+ "shuoshuo_time varchar(16),"
				+ "shuoshuo_lasttime varchar(16),"
				+ "shuoshuo_content TEXT,"
				+ "shuoshuo_path varchar(64),"
				+ "shuoshuo_isread integer default '0',"
				+ "shuoshuo_type integer default '0')";
		String sql_comment = "create table oim_comment(comment_id integer primary key,"
				+ "shuoshuo_id integer,"
				+ "comment_name varchar(32),"
				+ "comment_time varchar(16),"
				+ "comment_content TEXT)";
		String sql_poi = "create table oim_notes(poi_id integer primary key,"
				+ "poi_whos varchar(32),"
				+ "poi_time varchar(32),"
				+ "poi_latitude double,"
				+ "poi_longitude double,"
				+ "poi_imgpath TEXT,"
				+ "poi_describe TEXT)";
		db.execSQL(sql_msg);
		db.execSQL(sql_msg_unread);
		db.execSQL(sql_notice);
		db.execSQL(sql_contacter);
		
		db.execSQL(sql_shuoshuo);
		db.execSQL(sql_comment);
		
		db.execSQL(sql_poi);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
