package com.time.oim.manager;

import java.util.ArrayList;
import java.util.List;

import com.time.oim.db.DBManager;
import com.time.oim.model.Msg;
import com.time.oim.util.Constant;

import android.content.Context;
import android.content.SharedPreferences;

public class MsgManager {
	private static MsgManager msgManager= null;
	private static List<Msg> msgList = new ArrayList<Msg>();
	private static Context context = null;
	
	public MsgManager(Context c){
		SharedPreferences sharedPre = c.getSharedPreferences(
				Constant.LOGIN_SET, Context.MODE_PRIVATE);
		context = c;
	}
	
	public static MsgManager getInstance(Context c){
		if(msgManager == null){
			msgManager = new MsgManager(c);
		}
		return msgManager;
	}

	public void saveMsg(Msg msg){
		msgList.add(msg);
	}
	
	public static List<Msg> getallMSgs(){
		return msgList;
	}
	
	public List<Msg> getUnReadMsgs(DBManager dbManager){
		msgList = null;
		msgList = new ArrayList<Msg>();
		msgList = dbManager.getUnReadMsgs();
		
		
		return msgList;
	}
	
	public List<Msg> getUnReadMsgsWithclear(String from, String to,DBManager dbManager){
	
		List<Msg> msgs= new ArrayList<Msg>();
		msgList = null;
		msgList = new ArrayList<Msg>();
		msgList = dbManager.getUnReadMsgs(from, to);
		for(int i=0;i<msgList.size();i++){
			if(msgList.get(i).getUsername().split("@")[0].equals(from.split("@")[0])){
				int msg_id = (int)dbManager.saveMsg(msgList.get(i),"oim_msg");
				msgList.get(i).setId(String.valueOf(msg_id));
				msgs.add(msgList.get(i));
				msgList.remove(i);
				i--;
			}
		}
		
		return msgs;
	}
	
	public void deleteone(String Jid){
		int location = 0;
		for(Msg msg : msgList){
			if(msg.getUsername() == Jid){
				msgList.remove(location);
				location--;
			}
			location++;
		}
	}
	
	public void deleteall(){
		msgList.clear();
	}
}
