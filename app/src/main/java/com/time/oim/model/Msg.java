package com.time.oim.model;

import java.util.Date;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.time.oim.util.Constant;
import com.time.oim.util.DatetimeUtil;

public class Msg implements Parcelable, Comparable<Msg>{
	private String msg_id;
	private String username;
	private String to;
	private String msg;
	private int msg_type;
	private String sendDatetime;
	private int InorOut;
	public int msg_save;
	public static final String IMMESSAGE_KEY = "immessage.key";
	public static final String KEY_TIME = "immessage.time";
	public static final String KEY_TYPE = "immessage.type";
	public static final int MSG_TEXT = 0;
	public static final int MSG_PIC = 1;
	public static final int MSG_LBS = 2;
	public static final int MSG_LBS_SHARE = 3;
	public static final int MSG_LBS_DRAW = 4;
	public static final int MSG_LBS_NEARBY = 5;
	public static final int MSG_BIAOQING = 6;
	public static final int MSG_IN = 0;
	public static final int MSG_OUT = 1;
	public static final int MSG_FAIL = 2;
	public static final int MSG_SENDING = 3;
	public static final int MSG_SAVE = 1;
	public static final int MSG_NOSAVE = 0;
	
	private Bitmap bm = null;
	
	public Msg(){
		msg_id = "";
		username = "";
		to = "";
		msg = "";
		msg_type = 0;//0文字，1图片，2位置
		sendDatetime = "";
		InorOut = 0; //0=in,1=out,2fail to send,3sending
		msg_save = 0;//0 不保存 1保存
	}
	
	public Msg(String username, String to,String msg,int type,String datetime,int inout){
//		super();
		this.msg_id = "";
		this.username = username;
		this.to = to;
		this.msg = msg;
		this.msg_type = type;
		this.sendDatetime = datetime;
		this.InorOut = inout;
		msg_save = 0;//0 不保存 1保存
	}
	
	public void setId(String id){
		this.msg_id = id;
	}
	
	public String getId(){
		return this.msg_id;
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	
	public String getUsername(){
		return this.username;
	}
	
	public void setTo(String to){
		this.to = to;
	}
	
	public String getTo(){
		return this.to;
	}
	
	public void setMsg(String Msg){
		this.msg = Msg;
	}
	
	public String getMsg(){
		return this.msg;
	}
	
	public void setDatetime(String datetime){
		this.sendDatetime = datetime;
	}
	
	public String getDatetime(){
		return this.sendDatetime;
	}
	
	public void setType(int type){
		this.msg_type = type;
	}
	
	public int getType(){
		return this.msg_type;
	}
	
	public void setInorOut(int inout){
		this.InorOut = inout;
	}
	
	public int getInorOut(){
		return this.InorOut;
	}
	public void setSave(int inout){
		this.msg_save = inout;
	}
	
	public int getSave(){
		return this.msg_save;
	}
	
	public Bitmap getImage(){
		
		return this.bm;
	}
	
	public void setImage(Bitmap bitmap){
		this.bm = bitmap;
	}
	
	public static final Parcelable.Creator<Msg> CREATOR = new Creator<Msg>() {
		
		@Override
		public Msg[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Msg[size];
		}
		
		@Override
		public Msg createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			Msg msg = new Msg();
			msg.setUsername(source.readString());
			msg.setMsg(source.readString());
			msg.setType(source.readInt());
			msg.setDatetime(source.readString());
			msg.setInorOut(source.readInt());
			return msg;
		}
	};

	public boolean comparedate(Msg msg){
		if (null == this.getDatetime() || null == msg.getDatetime()) {
			return true;
		}
		
		if((this.getDatetime().split(" ")[0] == msg.getDatetime().split(" ")[0]) ||  
				(this.getDatetime().split(" ")[0].equals(msg.getDatetime().split(" ")[0])))
			return true;
		return false;
	}
	
	@Override
	public int compareTo(Msg oth) {
		// TODO Auto-generated method stub
		if (null == this.getDatetime() || null == oth.getDatetime()) {
			return 0;
		}
		String format = null;
		String time1 = "";
		String time2 = "";
		if (this.getDatetime().length() == oth.getDatetime().length()
				&& this.getDatetime().length() == 23) {
			time1 = this.getDatetime();
			time2 = oth.getDatetime();
			format = Constant.MS_FORMART;
		} else {
			time1 = this.getDatetime().substring(0, 19);
			time2 = oth.getDatetime().substring(0, 19);
		}
		Date da1 = DatetimeUtil.str2Date(time1, format);
		Date da2 = DatetimeUtil.str2Date(time2, format);
		if (da1.before(da2)) {
			return -1;
		}
		if (da2.before(da1)) {
			return 1;
		}

		return 0;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		// TODO Auto-generated method stub
		dest.writeString(username);
		dest.writeString(msg);
		dest.writeInt(msg_type);
		dest.writeString(sendDatetime);
		dest.writeInt(InorOut);
	}
	
}
