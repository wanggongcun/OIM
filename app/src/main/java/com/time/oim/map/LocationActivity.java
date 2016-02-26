package com.time.oim.map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.time.oim.ChatListActivity;
import com.time.oim.R;
import com.time.oim.R.drawable;
import com.time.oim.R.id;
import com.time.oim.R.layout;

public class LocationActivity extends Activity implements OnGetGeoCoderResultListener{

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	//搜索相关
	private GeoCoder mSearch = null;
	
	private boolean isFirstLoc = true;
	private boolean isMyLocation = true;
	private boolean isShowLocation = false;
	private String showlocation = null;
	private double lat;
	private double lng;
	
	private TextView tv_location;
	private Button bt_back;
	private Button bt_send;
	
	public static final int RESULT_MSG_SEND_MAP = 11;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_location);
		
		if(getIntent().hasExtra("ismylocation")){
			String ism = getIntent().getStringExtra("ismylocation");
			if(ism.equals("true")){
				isMyLocation = true;
			}else{
				isMyLocation = false;
			}
		}
		
		
		mMapView = (MapView)findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		initmap();
		initview();
		
		if(getIntent().hasExtra("showlocation")){
			showlocation = getIntent().getStringExtra("showlocation");
			isShowLocation = true;
			bt_send.setVisibility(View.GONE);
		}
	}
	
	private void initmap(){
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		
		MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(15);
		mBaiduMap.animateMapStatus(u);
		mCurrentMode = LocationMode.NORMAL;
		// 修改为自定义marker
//		mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, null));
		//搜索
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		
		mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
			
			@Override
			public void onTouch(MotionEvent arg0) {
				// TODO Auto-generated method stub
				if(!isMyLocation){
					mBaiduMap.clear();
					DisplayMetrics dm = new DisplayMetrics();        
			        getWindowManager().getDefaultDisplay().getMetrics(dm);        
			        int widthPixels = dm.widthPixels;        
			        int heightPixels = dm.heightPixels; 
			        // 计算屏幕宽度和高度        
			        Point pt = new Point(widthPixels/2, heightPixels/2);
					LatLng ll = mBaiduMap.getProjection().fromScreenLocation(pt);
					OverlayOptions marker = new MarkerOptions().position(ll).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).zIndex(9).draggable(true);
					mBaiduMap.addOverlay(marker);
					
					mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
					
				}
			}
		});
		
		
	}
	
	private void initview(){
		
		tv_location = (TextView)findViewById(R.id.tv_location);
		bt_back = (Button)findViewById(R.id.bt_back);
		bt_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		bt_send = (Button)findViewById(R.id.bt_send);
		bt_send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isShowLocation){
					finish();
				}else{
					Intent it = new Intent(LocationActivity.this,ChatListActivity.class);
					String location = tv_location.getText().toString() + "@" + String.valueOf(lat) + "@" + String.valueOf(lng);
					if(isMyLocation){
						it.putExtra("location","我在" + tv_location.getText().toString() + "@" + String.valueOf(lat) + "@" + String.valueOf(lng));
					}else{
						it.putExtra("location", tv_location.getText().toString() + "@" + String.valueOf(lat) + "@" + String.valueOf(lng));
					}
					setResult(RESULT_MSG_SEND_MAP, it);
					finish();
				}
			}
		});
		
	}
	
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				if(isShowLocation && showlocation.split("@").length>2){
					LatLng latlng = new LatLng(Double.valueOf(showlocation.split("@")[1]), Double.valueOf(showlocation.split("@")[2]));
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latlng);
					mBaiduMap.animateMapStatus(u);
					OverlayOptions marker = new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).zIndex(9).draggable(true);
					mBaiduMap.addOverlay(marker);
				}else{
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
					mBaiduMap.animateMapStatus(u);
					
				}
				
				if(!isMyLocation){
					OverlayOptions marker = new MarkerOptions().position(ll).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).zIndex(9).draggable(true);
					mBaiduMap.addOverlay(marker);
				}
				mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
				lat = ll.latitude;
				lng = ll.longitude;
			}
	
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMapView.onResume();
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(LocationActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		
		String strInfo = String.format("纬度：%f 经度：%f",
				result.getLocation().latitude, result.getLocation().longitude);
		Toast.makeText(LocationActivity.this, strInfo, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(LocationActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
//		Toast.makeText(LocationActivity.this, result.getAddress(),
//				Toast.LENGTH_LONG).show();
		tv_location.setText(result.getAddress());
		lat = result.getLocation().latitude;
		lng = result.getLocation().longitude;
	}

	
}
