package com.time.oim.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;


public class Shuoshuo implements Parcelable, Comparable<Shuoshuo>{
	private String username;
	private String time;
	private String lasttime;
	private String content;
	private String image_content;
	private String image_url;
	private int hasimage; // 0无    1image  2video
	private int isreadimage; //0fasle 1true
	private List<Comment> comments;
	private String shuoshuoID;
	
	public Shuoshuo(){
		this.username = "";
		this.time = "";
		this.lasttime = "";
		this.content = "";
		this.image_content = "";
		this.image_url = "";
		this.hasimage = 0;
		this.isreadimage = 0;
		this.comments = new ArrayList<Comment>();
		this.shuoshuoID = "";
	}
	
	public Shuoshuo(Shuoshuo s){
		this.username = s.getUsername();
		this.time = s.getTime();
		this.lasttime = s.getLastTime();
		this.content = s.getContent();
		this.image_content = s.getImage();
		this.image_url = s.getImageURL();
		this.hasimage = s.hasImage();
		this.isreadimage = s.isReadImage();
		this.comments = s.getComments();
		this.shuoshuoID = s.getShuoshuoID();
	}
	
	public Shuoshuo(String username,String time,String lasttime,String content,
			String image_content,String image_url,int hasimage,int isreadimage,
			List<Comment> comments,String shuoshuoid){
		this.username = username;
		this.time = time;
		this.lasttime = lasttime;
		this.content = content;
		this.image_content = image_content;
		this.image_url = image_url;
		this.hasimage = hasimage;
		this.isreadimage = isreadimage;
		this.comments = comments;
		this.shuoshuoID = shuoshuoid;
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	
	public String getUsername(){
		return this.username;
	}
	
	public void setTime(String time){
		this.time = time;
	}
	
	public String getTime(){
		return this.time;
	}
	
	public void setLastTime(String time){
		this.lasttime = time;
	}
	
	public String getLastTime(){
		return this.lasttime;
	}
	
	public void setContent(String content){
		this.content = content;
	}
	
	public String getContent(){
		return this.content;
	}
	
	public void setImage(String image){
		this.image_content = image;
	}
	
	public String getImage(){
		return this.image_content;
	}
	
	public void setImageURL(String url){
		this.image_url = url;
	}
	
	public String getImageURL(){
		return this.image_url;
	}
	
	public void setHasImage(int hasImage){
		this.hasimage = hasImage;
	}
	
	public int hasImage(){
		return this.hasimage;
	}
	
	public void setIsReadImage(int isReadImage){
		this.isreadimage = isReadImage;
	}
	
	public int isReadImage(){
		return this.isreadimage;
	}
	
	public void setShuoshuoID(String id){
		this.shuoshuoID = id;
	}
	
	public String getShuoshuoID(){
		return this.shuoshuoID;
	}
	
	public List<Comment> getComments(){
		if(comments == null)
			comments = new ArrayList<Comment>();
		return this.comments;
	}
	
	public void addComments(List<Comment> comments){
		if(comments == null)
			comments = new ArrayList<Comment>();
		this.comments.addAll(comments);
	}
	
	public void addComments(String comments){
		
	}
	
	public void addComment(Comment comment){
		this.comments.add(comment);
	}
	
	public void addComment(String comment){
//		Gson gson = new Gson();
//		Comment c = new Comment();
//		c = gson.fromJson(comment, Comment.class);
//		
//		this.comments.add(c);
	}
	
	public void deleteComment(String id){
		for(Comment c : comments){
			if(c.getCommentID() == id){
				comments.remove(c);
				break;
			}
		}
	}
	
	public void clearComment(){
		this.comments.clear();
	}
	
	public void sortComment(){
		Collections.sort(comments, new Comparator<Comment>() {

			@Override
			public int compare(Comment lhs, Comment rhs) {
				// TODO Auto-generated method stub
				
				return lhs.getTime().compareTo(rhs.getTime());
			}
		});
	}

	@Override
	public int compareTo(Shuoshuo arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
		arg0.writeString(username);
		arg0.writeString(time);
		arg0.writeString(lasttime);
		arg0.writeString(content);
		arg0.writeString(image_content);
		arg0.writeString(image_url);
		arg0.writeInt(hasimage);
		arg0.writeInt(isreadimage);
		arg0.writeParcelableArray(null, 0);
		arg0.writeString(shuoshuoID);
	}
}
