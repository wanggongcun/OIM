package com.time.oim.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;
import com.time.oim.db.DBManager;
import com.time.oim.model.Shuoshuo;


public class ShuoshuoManager {
	private static List<Shuoshuo> shuoshuos = new ArrayList<Shuoshuo>();
	private static ShuoshuoManager shuoshuoManager = null;
	
	private Context mContext;
	
	public ShuoshuoManager(Context context){
		this.mContext = context;
	}
	
	public static ShuoshuoManager getInstance(Context context){
		if(shuoshuoManager == null){
			shuoshuoManager = new ShuoshuoManager(context);
		}
		return shuoshuoManager;
	}
	
	public ShuoshuoManager(List<Shuoshuo> ss){
		shuoshuos = ss;
	}
	
	public void addShuoshuos(List<Shuoshuo> ss){
		for(int i=0;i<ss.size();i++){
			addShuoshuo(ss.get(i));
		}
	}
	
	public void addShuoshuos(String shuoshuos){
		
	}
	
	public void addShuoshuo(Shuoshuo shuoshuo){
		deleteShuoshuo(shuoshuo.getShuoshuoID());
		shuoshuos.add(shuoshuo);
		DBManager.getInstance(mContext).saveShuoshuo(shuoshuo);
		
	}
	
	public void addShuoshuo(String shuoshuo){
		Shuoshuo s = new Shuoshuo();
		Gson gson = new Gson();
		s = gson.fromJson(shuoshuo, Shuoshuo.class);
		shuoshuos.add(s);
		deleteShuoshuo(s.getShuoshuoID());
		DBManager.getInstance(mContext).saveShuoshuo(s);
	}
	
	public List<Shuoshuo> getShuoshuos(){
		if(shuoshuos == null){
			shuoshuos = new ArrayList<Shuoshuo>();
		}
		shuoshuos = DBManager.getInstance(mContext).getShuoshuos();
		return shuoshuos;
	}
	
	public List<Shuoshuo> getMyShuoshuos(String name){
		if(shuoshuos == null){
			shuoshuos = new ArrayList<Shuoshuo>();
			shuoshuos= DBManager.getInstance(mContext).getShuoshuos();
		}
		List<Shuoshuo> myShuoshuos = new ArrayList<Shuoshuo>();
		for(Shuoshuo s : shuoshuos){
			if(s.getUsername() == name){
				myShuoshuos.add(s);
			}
		}
		return myShuoshuos;
	}
	
	public Shuoshuo getShuoshuo(String id){
		if(shuoshuos == null){
			shuoshuos = new ArrayList<Shuoshuo>();
			shuoshuos= DBManager.getInstance(mContext).getShuoshuos();
		}
		for(Shuoshuo s : shuoshuos){
			if(s.getShuoshuoID() == id){
				
				return s;
			}
		}
		return null;
	}
	
	public void deleteShuoshuo(String id){
		for(Shuoshuo s : shuoshuos){
			if(s.getShuoshuoID() == id){
				shuoshuos.remove(s);
				break;
			}
		}
		DBManager.getInstance(mContext).deleteShuoshuo(id);
	}
	
	public void refreshShuoshuos(){
		DBManager.getInstance(mContext).refreshshuoshuo();
		if(shuoshuos == null){
			shuoshuos = new ArrayList<Shuoshuo>();
		}
		shuoshuos= DBManager.getInstance(mContext).getShuoshuos();
	}
}
