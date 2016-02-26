package com.time.oim.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable, Comparable<Comment>{
	private String from;
	private String to;
	private String time;
	private String content;
	private String shuoshuoid;
	private String commentid;
	private boolean zan;
	
	public Comment(){
		this.from = null;
		this.to = null;
		this.time = null;
		this.content = null;
		this.shuoshuoid = null;
		this.commentid = null;
		this.zan = false;
	}
	
	public Comment(String from, String to, String time, String content, String shuoshuoid,
				String commentid, boolean zan){
		this.from = from;
		this.to = to;
		this.time = time;
		this.content = content;
		this.shuoshuoid = shuoshuoid;
		this.commentid = commentid;
		this.zan = zan;
	}
	
	public void setFrom(String from){
		this.from = from;
	}

	public String getFrom(){
		return this.from;
	}
	
	public void setTo(String to){
		this.to = to;
	}
	
	public String getTo(){
		return this.to;
	}
	
	public void setTime(String time){
		this.time = time;
	}
	
	public String getTime(){
		return this.time;
	}
	
	public void setContent(String content){
		this.content = content;
	}
	
	public String getContent(){
		return this.content;
	}
	
	public void setShuoshuoID(String id){
		this.shuoshuoid = id;
	}
	
	public String getShuoshuoID(){
		return this.shuoshuoid;
	}
	
	public void setCommentID(String id){
		this.commentid = id;
	}
	
	public String  getCommentID(){
		return this.commentid;
	}
	
	public void setZan(boolean zan){
		this.zan = zan;
	}
	
	public boolean getZan(){
		return this.zan;
	}

	@Override
	public int compareTo(Comment another) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
}
