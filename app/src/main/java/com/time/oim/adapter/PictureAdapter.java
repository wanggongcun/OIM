package com.time.oim.adapter;

import java.util.ArrayList;
import java.util.List;

import com.time.oim.R;
import com.time.oim.util.ImageUtil;
import com.time.oim.view.MyViewPager;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PictureAdapter extends PagerAdapter {
	
	private Context context = null;
	private List<ImageView> imgList;
	private Bitmap bitmap = null;
	
	public void setBitmap(Bitmap bm){
		this.bitmap =Bitmap.createBitmap(bm.getWidth(),bm.getHeight(),bm.getConfig());

	}
	
	public void setPaths(){
		
		imgList = new ArrayList<ImageView>();
		ImageView iv0 = new ImageView(context);
//		iv0.setBackgroundColor(0x330000FF);
		iv0.setBackgroundResource(R.drawable.camera);
		iv0.setDrawingCacheEnabled(true);
		bitmap = iv0.getDrawingCache();
		iv0.setDrawingCacheEnabled(false);
		iv0.setImageBitmap(ImageUtil.toGrayscale(bitmap));
		imgList.add(iv0);
		
		ImageView iv1 = new ImageView(context);
		iv1.setBackgroundColor(0x00FFFFFF);
		imgList.add(iv1);
		
		ImageView iv5 = new ImageView(context);
		iv5.setBackgroundColor(0x330000FF);
		iv5.setImageBitmap(ImageUtil.toGrayscale(bitmap));
		imgList.add(iv5);
		
		ImageView iv6 = new ImageView(context);
		iv6.setBackgroundColor(0x33FFFFFF);
		imgList.add(iv6);
	}
	
	public void setContext(Context con){
		this.context = con;
	}
	

	
	public PictureAdapter(Context con) {
		// TODO Auto-generated constructor stub
		this.context = con;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return imgList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
//		super.destroyItem(container, position, object);
		((MyViewPager)container).removeView(imgList.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		((MyViewPager)container).addView(imgList.get(position));
		return imgList.get(position);
	}

	
}
