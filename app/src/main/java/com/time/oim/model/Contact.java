package com.time.oim.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
	private String phoneNum;
	private String contactName;
	private long contactId;
	private long photoId;
	
	public Contact(){
		this.phoneNum = "";
		this.contactName = "";
		this.contactId = -1;
		this.photoId = -1;
	}
	
	public Contact(String photoNum,String contactName,long contactId,long photoId){
		this.phoneNum = photoNum;
		this.contactName = contactName;
		this.contactId = contactId;
		this.photoId = photoId;
	}
	
	public void setPhotoNum(String photoNum){
		this.phoneNum = photoNum;
	}
	
	public String getPhotoNum(){
		return phoneNum;
	}
	
	public void setContactName(String contactName){
		this.contactName = contactName;
	}
	
	public String getContactName(){
		return contactName;
	}
	
	public void setContactId(long contactId){
		this.contactId = contactId;
	}
	
	public long getContactId(){
		return contactId;
	}
	
	public void setPhotoId(long photoId){
		this.photoId = photoId;
	}
	
	public long getPhotoId(){
		return photoId;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		// TODO Auto-generated method stub
		dest.writeString(phoneNum);
		dest.writeString(contactName);
		dest.writeLong(contactId);
		dest.writeLong(photoId);
	}
}
