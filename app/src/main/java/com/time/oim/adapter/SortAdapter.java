package com.time.oim.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.time.oim.R;
import com.time.oim.model.User;
import com.time.oim.util.DatetimeUtil;

public class SortAdapter extends BaseAdapter implements SectionIndexer{
	
	private List<User> users = null;
	private Context context=null;
	private String now = DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss();
	
	public SortAdapter(Context mContext, List<User> list) {
		this.context = mContext;
		this.users = new ArrayList<User>();
		this.users = list;
	}
	
	public void setnow(){
		now = DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss();
	}
	
	public void updateListView(List<User> list){
		now = DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss();
		this.users = new ArrayList<User>();
		this.users = list;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return users.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return users.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		final User user = users.get(position);
		
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.fri_item, null);
			viewHolder.tv_catalog = (TextView) view.findViewById(R.id.tv_catalog);
			viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
			viewHolder.tv_time = (TextView) view.findViewById(R.id.tv_time);
			viewHolder.tv_num = (TextView) view.findViewById(R.id.tv_num);
			viewHolder.msglayout = (RelativeLayout) view.findViewById(R.id.msglayout);
			
			
			view.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		viewHolder.tv_name.setText(user.getJID().split("@")[0]);
		
		int minute = (int) ((System.currentTimeMillis())/(1000*60)-user.getLastTime_second()/60);
		viewHolder.tv_time.setText(getWhen(minute));
		
		if(user.getUnReadMsg()>0){
			viewHolder.tv_num.setText(String.valueOf(user.getUnReadMsg()));
//			viewHolder.tv_catalog.setText("你收到"+String.valueOf(user.getUnReadMsg())+"条消息");
			viewHolder.msglayout.setVisibility(View.VISIBLE);
		}else{
			viewHolder.msglayout.setVisibility(View.GONE);
		}
		
//		int section = getSectionForPosition(position);
//		
//		if(position == getPositionForSection(section)){
//			viewHolder.tv_catalog.setVisibility(View.VISIBLE);
//			viewHolder.tv_catalog.setText(getIndexDay(section));
//		}else{
//			viewHolder.tv_catalog.setVisibility(View.GONE);
//		}
		
		
		return view;
	}

	@Override
	public int getPositionForSection(int arg0) {
		// TODO Auto-generated method stub
		int size =users.size();
		for(int i=0;i<size;i++){
//			Date date1 = DatetimeUtil.str2Date(users.get(i).getLastTime());
//			Date date2 = DatetimeUtil.str2Date(now);
//			int day1 = (int) ((date2.getTime()-date1.getTime())/(1000*60*60*24));
//			int d = (int) (System.currentTimeMillis()/(1000*60*60*24));
//			int d2 = (int) (users.get(i).getLastTime_second()/(1000*60*60*24));
			int day = (int) ((System.currentTimeMillis())/(1000*60*60*24)-users.get(i).getLastTime_second());
			if(day == arg0){
				return i;
			}else if(day > arg0){
				return i-1;
			}
		}
		return size-1;
	}

	@Override
	public int getSectionForPosition(int arg0) {
		// TODO Auto-generated method stub
//		Date date1 = DatetimeUtil.str2Date(users.get(arg0).getLastTime());
//		Date date2 = DatetimeUtil.str2Date(DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss());
//		int day = (int) ((date2.getTime()-date1.getTime())/(1000*60*60*24));
		int day = (int) ((System.currentTimeMillis())/(1000*60*60*24)-users.get(arg0).getLastTime_second());
		return day;
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}
	
	final static class ViewHolder {
		TextView tv_catalog;
		TextView tv_name;
		TextView tv_time;
		TextView tv_num;
		RelativeLayout msglayout;
	}

	private String getIndexDay(int day){
		String[] b = { "最近","一天", "两天", "三天", "四天", "五天", "六天", "七天", "一周",
				"两周", "三周", "四周", "一个月" , "无"};
		if(day<=7){
			return b[day];
		}else if(day>7 && day <31){
			return b[7 + day/7];
		}else{
			return b[12];
		}
	}
	
	private String getWhen(int minute){
		String[] m = {"分钟前","小时前","一天前", "两天前", "三天前", "四天前", "五天前", "六天前", "七天前", "一周前",
				"两周前", "三周前", "四周前", "一个月前" , "无"};
		
		if(minute<60){
			return String.valueOf(minute) + m[0];
		}else if(minute<1440){
			return String.valueOf(minute/60) + m[1];
		}else if(minute<10080){
			return m[(int)(minute/1440) + 1];
		}else if(minute<40320){
			return m[(int)(minute/10080) + 8];
		}else if(minute<43200){
			return m[13];
		}else{
			return m[14];
		}
		
	}
	
}
