package com.time.oim.model;

import org.jivesoftware.smack.packet.RosterPacket;

import com.time.oim.util.DatetimeUtil;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * intent可以携带传递Parcel数据，需要实现三个方法 . 1、describeContents()返回0就可以.
 * 2、将需要的数据写入Parcel中，框架调用这个方法传递数据. 3、重写外部类反序列化该类时调用的方法.
 * 
 * @author wangdan
 * 
 */
public class User implements Parcelable{
	/**
	 * 将user保存在intent中时的key
	 */
	public static final String userKey = "lovesong_user";

	private String name;
	private String JID;
	private static RosterPacket.ItemType type;
	private String status;
	private String from;
	private String groupName;
	private String lastTime;
	private long lasttime_second;
	private int unreadmsg = 0;
	/**
	 * 用户状态对应的图片
	 */
	private int imgId;
	/**
	 * group的size
	 */
	private int size;
	private boolean available;

	public User(){
		name = "";
		JID="";
		lastTime = DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss();
		unreadmsg = 0;
	}
	public User(String name,String jid,String lasttime,int msg){
		this.name = name;
		this.JID = jid;
		this.lastTime = lasttime;
		this.lasttime_second = (int)DatetimeUtil.str2Date(lasttime).getTime();
		this.unreadmsg = msg;
	}
	
	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}
	
	public int getUnReadMsg() {
		return unreadmsg;
	}

	public void setUnReadMsg(int msg) {
		this.unreadmsg = msg;
	}
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	public String getLastTime() {
		return lastTime;
	}
	
	public long getLastTime_second() {
		return lasttime_second;
	}
	public void setLastTime(String time) {
		long lt = DatetimeUtil.str2Date(time).getTime()/(1000);
		this.lasttime_second = (int) lt;
		this.lastTime = time;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJID() {
		return JID;
	}

	public void setJID(String jID) {
		JID = jID;
	}

	public RosterPacket.ItemType getType() {
		return type;
	}

	@SuppressWarnings("static-access")
	public void setType(RosterPacket.ItemType type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(JID);
		dest.writeString(name);
		dest.writeString(from);
		dest.writeString(status);
		dest.writeInt(available ? 1 : 0);
	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

		@Override
		public User createFromParcel(Parcel source) {
			User u = new User();
			u.JID = source.readString();
			u.name = source.readString();
			u.from = source.readString();
			u.status = source.readString();
			u.available = source.readInt() == 1 ? true : false;
			return u;
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}

	};

	public User clone() {
		User user = new User();
		user.setAvailable(User.this.available);
		user.setFrom(User.this.from);
		user.setGroupName(User.this.groupName);
		user.setImgId(User.this.imgId);
		user.setJID(User.this.JID);
		user.setName(User.this.name);
		user.setSize(User.this.size);
		user.setStatus(User.this.status);
		return user;
	}
	
	public void addMsg(){
		this.unreadmsg++;
	}

}
