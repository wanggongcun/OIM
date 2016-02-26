package com.time.oim.adapter;

import java.util.List;


import com.time.oim.R;
import com.time.oim.model.Shuoshuo;
import com.time.oim.util.DatetimeUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShuoshuoAdapter extends BaseAdapter{

	private List<Shuoshuo> shuoshuos = null;
	private Context context=null;
	private String now = DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss();
	
	public ShuoshuoAdapter(Context mContext, List<Shuoshuo> list) {
		this.context = mContext;
		this.shuoshuos = list;
	}
	
	public void setnow(){
		now = DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss();
	}
	
	public void updateListView(List<Shuoshuo> list){
		now = DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss();
		this.shuoshuos = list;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return shuoshuos.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return shuoshuos.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder viewHolder = null;
		Shuoshuo shuoshuo = new Shuoshuo();
		shuoshuo = shuoshuos.get(position);
		if(view == null){
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.shuoshuo_item, null);
			viewHolder.iv_shuoshuo = (ImageView) view.findViewById(R.id.iv_shuoshuo);
			viewHolder.tv_shuoshuo_time = (TextView) view.findViewById(R.id.tv_shuoshuo_time);
			viewHolder.tv_shuoshuo_name = (TextView) view.findViewById(R.id.tv_shuoshuo_name);
			viewHolder.tv_comment_time = (TextView) view.findViewById(R.id.tv_comment_time);
			
			view.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) view.getTag();
		}
		Options options = new Options();
		
		
		if(shuoshuo.hasImage() == 1){
			Bitmap bm = BitmapFactory.decodeResource(context.getResources(), Integer.valueOf(shuoshuo.getImageURL()));
			viewHolder.iv_shuoshuo.setImageBitmap(bm);
		}else if(shuoshuo.hasImage() == 2){
			options.inJustDecodeBounds = true;  
			Bitmap bm = BitmapFactory.decodeFile(shuoshuo.getImageURL(),options);
			options.inJustDecodeBounds = false;
			int h = options.outHeight;
			int be = h/400 + 1;
			options.inSampleSize = be;
			bm = BitmapFactory.decodeFile(shuoshuo.getImageURL(),options);
			viewHolder.iv_shuoshuo.setImageBitmap(bm);
		}else{
			Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_small);
			viewHolder.iv_shuoshuo.setImageBitmap(bm);
		}
		
		
		viewHolder.tv_shuoshuo_time.setText(shuoshuo.getTime());
		viewHolder.tv_shuoshuo_name.setText(shuoshuo.getUsername());
		viewHolder.tv_comment_time.setText(shuoshuo.getContent());
		
		return view;
	}

	final static class ViewHolder {
		ImageView iv_shuoshuo;
		TextView tv_shuoshuo_time;
		TextView tv_shuoshuo_name;
		TextView tv_comment_time;
	}
}
