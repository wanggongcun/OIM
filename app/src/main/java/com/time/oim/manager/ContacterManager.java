package com.time.oim.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.content.Context;
import android.content.SharedPreferences;

import com.time.oim.db.DBManager;
import com.time.oim.model.Msg;
import com.time.oim.model.User;
import com.time.oim.util.Constant;
import com.time.oim.util.DatetimeUtil;
import com.time.oim.util.StringUtil;


public class ContacterManager {
	public static Map<String,User> contacters = null;

	public static void init(Context context, Connection connection){
		contacters = null;
		contacters = new HashMap<String, User>();
		if(!connection.isConnected()){
			return;
		}
//		DBManager.getInstance(context).clearContacter();
		SharedPreferences preferences = context.getSharedPreferences(Constant.LOGIN_SET, 0);
		String myname = preferences.getString(Constant.USERNAME, "");
//		DBManager.getInstance(context).clearContacter();
		for(RosterEntry entry : connection.getRoster().getEntries()){
			String name = "";
			if (entry.getName() == null) {
				name = StringUtil.getUserNameByJid(entry.getUser());
			} else {
				name = entry.getName();
			}
			
			User user = transEntryToUser(entry, connection.getRoster());
			contacters.put(name, user);
			DBManager.getInstance(context).saveContacter(myname, user);
		}
	}
	
	public static void deleteContacts(Context context){
		if(contacters == null)
			return;
		String myname = context.getSharedPreferences(Constant.LOGIN_SET, 0).getString(Constant.USERNAME, "");
		for(String key : contacters.keySet()){
			User user = contacters.get(key);
			if(!DatetimeUtil.howlong(user.getLastTime())){
				try {
					deleteUser(user.getJID());
					DBManager.getInstance(context).deleteContacter(myname, user.getJID().split("@")[0]);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					DBManager.getInstance(context).deleteContacter(myname, user.getJID().split("@")[0]);
				}
			}
		}
		
	}
	
	public static void destroy(){
		contacters = null;
	}
	
	public static List<User> getContacterList(Context context){

		SharedPreferences preferences = context.getSharedPreferences(Constant.LOGIN_SET, 0);
		String myname = preferences.getString(Constant.USERNAME, "");
//		return DBManager.getInstance(context).getContacters(myname);
		contacters = null;
		contacters = new HashMap<String, User>();
		contacters = DBManager.getInstance(context).getHashMapContacters(myname);
		updateMsgs(context);
		if (contacters == null){
			contacters = new HashMap<String, User>();
		}
		List<User> list = new ArrayList<User>();
		for(String key : contacters.keySet()){
			list.add(contacters.get(key));
		}
		return list;
	}
	
	public static boolean hasContacter(String name){
		if(contacters.containsKey(name)){
			return true;
		}
		return false;
	}
	
	 public static User transEntryToUser(RosterEntry entry,Roster roster){
		User user = new User();
		if (entry.getName() == null) {
			user.setName(StringUtil.getUserNameByJid(entry.getUser()));
		} else {
			user.setName(entry.getName());
		}
		Presence presence = roster.getPresence(entry.getUser());
		user.setFrom(presence.getFrom());
		user.setStatus(presence.getStatus());
		user.setSize(entry.getGroups().size());
		user.setAvailable(presence.isAvailable());
		user.setType(entry.getType());
		user.setJID(entry.getUser());
		user.setLastTime(DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss());
		
		return user;
	}
	
	public static void setNickname(User user, String nickname,
			XMPPConnection connection) {
		RosterEntry entry = connection.getRoster().getEntry(user.getJID());

		entry.setName(nickname);
	}
	
	public static User getNickname(String Jid, XMPPConnection connection) {
		Roster roster = connection.getRoster();
		for (RosterEntry entry : roster.getEntries()) {
			String params = entry.getUser();
			if (params.split("/")[0].equals(Jid)) {
				return transEntryToUser(entry, roster);
			}
		}
		return null;

	}
	
	public static void deleteUser(String userJid) throws XMPPException {

		Roster roster = XmppConnectionManager.getInstance().getConnection()
				.getRoster();
		RosterEntry entry = roster.getEntry(userJid);
		XmppConnectionManager.getInstance().getConnection().getRoster()
				.removeEntry(entry);

	}
	
	public static User getByUserJid(String userJId, XMPPConnection connection) {
		Roster roster = connection.getRoster();
		RosterEntry entry = connection.getRoster().getEntry(userJId);
		if (null == entry) {
			return null;
		}
		User user = new User();
		if (entry.getName() == null) {
			user.setName(StringUtil.getUserNameByJid(entry.getUser()));
		} else {
			user.setName(entry.getName());
		}
		user.setJID(entry.getUser());
		System.out.println(entry.getUser());
		Presence presence = roster.getPresence(entry.getUser());
		user.setFrom(presence.getFrom());
		user.setStatus(presence.getStatus());
		user.setSize(entry.getGroups().size());
		user.setAvailable(presence.isAvailable());
		user.setType(entry.getType());
		return user;

	}
	
	public static void addUser(String userJid,String nickname,String[] groups) throws XMPPException{
		
		XmppConnectionManager.getInstance().getConnection().getRoster().createEntry(userJid +
				"@" + XmppConnectionManager.getInstance().getConnection().getServiceName(), nickname, groups);
	
	}
	
	public static void add(User user){
		if (contacters == null){
			contacters = new HashMap<String, User>();
		}
		contacters.put(user.getName(), user);
		
	}
	
	public static List<String> serachUser(String name){
		List<String> searchUsers = new ArrayList<String>();
		Connection connection= null;
		connection = XmppConnectionManager.getInstance().getConnection();
		if(connection == null)
			return searchUsers;
		UserSearchManager search = new UserSearchManager(connection);
		try {
			String serverName = XmppConnectionManager.getInstance().getConnection().getServiceName();
			Form searchForm = search.getSearchForm("search." + serverName);
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("search", name);
			ReportedData data = search.getSearchResults(answerForm, "search." + serverName);
			Iterator<Row> it = data.getRows();
			Row row = null;
			String ansS = "";
			while(it.hasNext()){
				row = it.next();
				ansS = row.getValues("username").next().toString() + " " + row.getValues("name").next().toString();
				
				searchUsers.add(ansS);
			}
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return searchUsers;
	}
	
	public static List<User> getContacterListBySort(){
		if (contacters == null){
			contacters = new HashMap<String, User>();
//			throw new RuntimeException("contacters is null");
		}
		List<User> list = new ArrayList<User>();
		for(String key : contacters.keySet()){
			list.add(contacters.get(key));
		}
		Collections.sort(list, new Comparator<User>() {

			@Override
			public int compare(User lhs, User rhs) {
				// TODO Auto-generated method stub
				Date d1 = DatetimeUtil.str2Date(lhs.getLastTime());
				Date d2 = DatetimeUtil.str2Date(rhs.getLastTime());
				if (d1.before(d2)) {  
                    return 1;  
                }  
                return -1;
			}
		});
		return list;
	}
	
	public static void updateMsgs(Context context){
		if(contacters == null){
			contacters = new HashMap<String, User>();
		}
		List<Msg> msgs = new ArrayList<Msg>();
		msgs = MsgManager.getInstance(context).getUnReadMsgs(DBManager.getInstance(context));
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
		for(String key:contacters.keySet()){
			contacters.get(key).setUnReadMsg(0);
		}
		
		for(int i=0;i<msgs.size();i++){
			String name = msgs.get(i).getUsername().split("@")[0];
			if(contacters.containsKey(name)){
				contacters.get(name).setLastTime(msgs.get(i).getDatetime());
				contacters.get(name).addMsg();
			}
		}
		
	}
	
	public static void updateMsg(Msg msg){
		
		String name = msg.getUsername().split("@")[0];
		if(contacters.containsKey(name)){
			contacters.get(name).setLastTime(msg.getDatetime());
			contacters.get(name).addMsg();
		
		}
	}
	
	public static void clearMsg(String name){
		if(contacters.containsKey(name))
			contacters.get(name).setUnReadMsg(0);
	}
}
