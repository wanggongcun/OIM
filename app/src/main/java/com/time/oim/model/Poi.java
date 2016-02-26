package com.time.oim.model;

public class Poi {

	private int poi_id;
	private String poi_time;
	private double poi_latitude;
	private double poi_longitude;
	private String poi_describe;
	private String poi_imgpath;
	private String poi_whos;
	
	public Poi(){
		poi_id = -1;
		poi_time = "";
		poi_latitude = 0;
		poi_longitude = 0;
		poi_describe = "";
		poi_imgpath = "";
		poi_whos = "";
	}
	
	public Poi(int id,String time,double lat,double lon,String des,String path,String whos){
		poi_id = id;
		poi_time = time;
		poi_latitude = lat;
		poi_longitude = lon;
		poi_describe = des;
		poi_imgpath = path;
		poi_whos = whos;
	}
	
	public Poi(String time,double lat,double lon,String des,String path,String whos){
		poi_id = -1;
		poi_time = time;
		poi_latitude = lat;
		poi_longitude = lon;
		poi_describe = des;
		poi_imgpath = path;
		poi_whos = whos;
	}
	
	public void setId(int id){
		poi_id = id;
	}
	
	public int getId(){
		return poi_id;
	}
	
	public void setTime(String time){
		poi_time = time;
	}
	
	public String getTime(){
		return poi_time;
	}
	
	public void setDes(String des){
		poi_describe = des;
	}
	
	public String getDese(){
		return poi_describe;
	}
	
	public void setPath(String path){
		poi_imgpath = path;
	}
	
	public String getPath(){
		return poi_imgpath;
	}
	
	public void setWhos(String whos){
		poi_whos = whos;
	}
	
	public String getWhos(){
		return poi_whos;
	}
	
	public void setLoca(double lat,double lon){
		poi_latitude = lat;
		poi_longitude = lon;
	}
	
	public double getLon(){
		return poi_longitude;
	}
	
	public double getLat(){
		return poi_latitude;
	}
}
