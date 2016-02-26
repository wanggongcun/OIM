package com.time.oim.map;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiSearch;

public class MyPoiOverlay extends PoiOverlay{

	private Context mContext = null;
	private MapView mMapView;
	private PoiSearch mPoiSearch = null;
	private PopupWindow pop;

	private InfoWindow mInfoWindow;
	public MyPoiOverlay(BaiduMap arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public void setPoiSearch(Context context,PoiSearch search,MapView mapView){
		this.mContext = context;
		this.mPoiSearch = search;
		mMapView = mapView;
	}
	
	@Override
	public boolean onPoiClick(int arg0) {
		// TODO Auto-generated method stub
		
		PoiInfo poi = getPoiResult().getAllPoi().get(arg0);
		 if (poi.hasCaterDetails) {
//			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
//					.poiUid(poi.uid));
			Toast.makeText(mContext, poi.address + " "+poi.name, Toast.LENGTH_SHORT).show();
//			View popview = LayoutInflater.from(mContext).inflate(R.layout.poipop, null);
//		    TextView testText = (TextView)popview.findViewById(R.id.poinote);  
//		    testText.setText(poi.address + " "+poi.name);
//		    pop = new PopupWindow(popview, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		    
			Button button = new Button(mContext);
			button.setText(poi.address + "\n"+poi.name);
			button.setTextSize(12);
			button.setBackgroundColor(Color.WHITE);
			OnInfoWindowClickListener listener = null;
			listener = new OnInfoWindowClickListener() {
				public void onInfoWindowClick() {
					
					mMapView.getMap().hideInfoWindow();
				}
			};
			LatLng ll = poi.location;
			mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47, listener);
			mMapView.getMap().showInfoWindow(mInfoWindow);
		 }else{
			 
		 }
		return true;
//		return super.onPoiClick(arg0);
	}

//	@Override
//	protected boolean onTap(int i) {
//		
//		return super.onTap(i);
//	}
	

}
