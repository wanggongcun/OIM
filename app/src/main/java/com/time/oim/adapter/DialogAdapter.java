package com.time.oim.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.time.oim.R;
import com.time.oim.R.color;
import com.time.oim.model.User;

public class DialogAdapter extends BaseAdapter{

	private List<User> users = null;
	private Context context = null;
	private Map<String, User> select_users = null;
	
	private TextView select_all;
	private TextView select_num;
	private EditText select_name;
	
	public DialogAdapter(Context mContext, List<User> list) {
		this.context = mContext;
		select_users = new HashMap<String, User>();
		this.users = new ArrayList<User>();
		this.users = list;

	}
	
	public void setView(TextView _all,TextView _num,EditText _name){
		this.select_all = _all;
		this.select_num = _num;
		this.select_name = _name;
	}
	
	public void updateView(){
		if(select_all!=null && select_name!=null && select_num!=null){
			for(int j=0;j<users.size();j++){
				if(select_users.containsKey(users.get(j).getName().toString())){
					select_all.setText("取消");
				}else{
					select_all.setText("全选");
					break;
				}
			}
			
			select_num.setText(String.valueOf(select_users.size()));
			if(select_users.size()>0){
				String namessss = "";
				for(String key : select_users.keySet()){
					namessss +="【" + select_users.get(key).getName().toString() + "】 ";
				}
				select_name.setText(namessss);
			}else{
				select_name.setText("");
			}
		}
	}
	
	public void updateListView(List<User> list){
		this.users = new ArrayList<User>();
		this.users = list;

		notifyDataSetChanged();
		updateView();
	}
	
	public void selectall(){
		for(int i=0;i<users.size();i++){
			if(!select_users.containsKey(users.get(i).getName().toString())){
				select_users.put(users.get(i).getName().toString(),users.get(i));
			}
		}
		
		notifyDataSetChanged();
		updateView();
	}
	
	public void selectnone(){
		for(int i=0;i<users.size();i++){
			if(select_users.containsKey(users.get(i).getName().toString())){
				select_users.remove(users.get(i).getName().toString());
			}
		}
		
		notifyDataSetChanged();
		updateView();
	}
	
	public List<String> getSelect(){
		if(select_users == null){
			select_users = new HashMap<String, User>();
		}
		List<String> names = new ArrayList<String>();
		for(String key : select_users.keySet()){
			names.add(key);
		}
		return names; 
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return users.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return users.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		final int pos = position;
		if(convertView==null){
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.dialog_item_view, null);
			viewHolder.tv_dialog_name = (TextView) convertView.findViewById(R.id.tv_dialog_name);
			viewHolder.cb_dialog_select = (CheckBox) convertView.findViewById(R.id.cb_dialog_select);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.tv_dialog_name.setText(users.get(position).getName());
		
		viewHolder.cb_dialog_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				
				if(isChecked){
					if(!select_users.containsKey(users.get(pos).getName().toString())){
						select_users.put(users.get(pos).getName().toString(),users.get(pos));
					}
				}else{
					if(select_users.containsKey(users.get(pos).getName().toString())){
						select_users.remove(users.get(pos).getName().toString());
					}
				}
				notifyDataSetChanged();
				updateView();
			}
		});
		if(select_users.containsKey(users.get(pos).getName().toString())){
			viewHolder.cb_dialog_select.setChecked(true);
			viewHolder.tv_dialog_name.setTextColor(Color.rgb(52, 170, 220));
		}else{
			viewHolder.cb_dialog_select.setChecked(false);
			viewHolder.tv_dialog_name.setTextColor(Color.BLACK);
		}
		
		return convertView;
	}

	final static class ViewHolder {
		TextView tv_dialog_name;
		CheckBox cb_dialog_select;
	}
}
