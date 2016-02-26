package com.time.oim.map;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.time.oim.R;
import com.time.oim.SendPicActivity;
import com.time.oim.db.DBManager;
import com.time.oim.manager.ContacterManager;
import com.time.oim.manager.XmppConnectionManager;
import com.time.oim.model.Msg;
import com.time.oim.model.Poi;
import com.time.oim.model.User;
import com.time.oim.util.ActivityUtil;
import com.time.oim.util.Constant;
import com.time.oim.util.DatetimeUtil;
import com.time.oim.util.SaveFileUtil;
import com.time.oim.util.SecretUtil;
import com.time.oim.view.clearEditText;

public class BaseMapActivity extends Activity {

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	
	private boolean isFirstLoc = true;
	
	private clearEditText et_search_poi;
	private Button bt_saveScreen;
	private Button bt_draw;
	private Button bt_find;
	private Button bt_send;
	private Button bt_clear;
	private Button bt_poi;
	private ImageButton bt_back;
	
	//搜索
	private PoiSearch mPoiSearch = null;
	private SuggestionSearch mSuggestionSearch = null;
	private PoiSearchListener poiSearchListener = null;
	private MyPoiOverlay searchPoiOverlay = null;
	private Overlay overlay = null;
	private int load_Index = 0;
	//绘图
	private boolean isDraw = false;
	private List<LatLng> points = null;
	private String saveScreenPath = null;
	private LatLng location = null;
	
	//协同编辑
	private String myname;
	private String to = "";
	private List<Msg> msgs;
	private ChatReceiver receiver;
	private Chat chat = null;
	private boolean isColla = false;
	
	private InfoWindow mInfoWindow;
	
	private final static int REQUEST_TO_SEND_PIC = 20;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_base_map);
		
		mMapView = (MapView)findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		receiver = new ChatReceiver();
		msgs = new ArrayList<Msg>();
		myname = ActivityUtil.getSharedPreferences(BaseMapActivity.this, Constant.USERNAME);
		initmap();
		initview();
		
		if(getIntent().hasExtra("to")){
			isColla = true;
			to = getIntent().getStringExtra("to").toString();
			XMPPConnection connection = null;
			connection = XmppConnectionManager.getInstance().getConnection();
			if(connection == null){
				Toast.makeText(BaseMapActivity.this, "无法连接服务器", Toast.LENGTH_SHORT).show();
			}else{
				chat = XmppConnectionManager.getInstance().getConnection().getChatManager().createChat(to, null);
			}
			
		}
		if(isColla){
			bt_draw.setText("协同");
			bt_find.setVisibility(View.GONE);
		}else{
			bt_draw.setText("开始");
			bt_find.setVisibility(View.VISIBLE);
		}
		bt_poi.setVisibility(View.VISIBLE);
	}
	
	private void initview(){
		et_search_poi = (clearEditText)findViewById(R.id.et_search_poi);
		et_search_poi.setSearchIconVisible(true);
		et_search_poi.setClearIconVisible(true);
		et_search_poi.setSearchListener(new clearEditText.SearchListener() {
			
			@Override
			public void search() {
				// TODO Auto-generated method stub
				InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				im.hideSoftInputFromWindow(et_search_poi.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				
				if(et_search_poi.getText().toString().trim() == ""){
					return;
				}
				PoiNearbySearchOption po= new PoiNearbySearchOption();
				po.keyword(et_search_poi.getText().toString());
		        po.location(new LatLng(mBaiduMap.getLocationData().latitude, mBaiduMap.getLocationData().longitude));
		        po.radius(5 * 1000);
		        po.pageNum(load_Index);
		        load_Index++;
		        boolean result = mPoiSearch.searchNearby(po);
//				if(searchPoiOverlay == null || searchPoiOverlay.getOverlayOptions().size()<10){
//					load_Index = 0;
//					po= new PoiNearbySearchOption();
//					po.keyword(et_search_poi.getText().toString());
//			        po.location(new LatLng(mBaiduMap.getLocationData().latitude, mBaiduMap.getLocationData().longitude));
//			        po.radius(5 * 1000);
//			        po.pageNum(load_Index);
//			        load_Index++;
//				}
				
			}
		});
		et_search_poi.setClearListener(new clearEditText.ClearListener() {
			
			@Override
			public void clear() {
				// TODO Auto-generated method stub
				InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				im.hideSoftInputFromWindow(et_search_poi.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				searchPoiOverlay.removeFromMap();
			}
		});
		et_search_poi.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				load_Index = 0;
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	
		bt_saveScreen = (Button) findViewById(R.id.bt_saveScreen);
		bt_saveScreen.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(BaseMapActivity.this, "正在截取地图", Toast.LENGTH_SHORT).show();
				mBaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
					
					@Override
					public void onSnapshotReady(Bitmap arg0) {
						// TODO Auto-generated method stub
						try{
							saveScreenPath = SaveFileUtil.savebm(arg0);
							bt_send.setVisibility(View.VISIBLE);
							Toast.makeText(BaseMapActivity.this, "截图保存在："+saveScreenPath, Toast.LENGTH_SHORT).show();
						}catch(Exception ex){
							Toast.makeText(BaseMapActivity.this, "截图失败", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});
		
		bt_draw = (Button) findViewById(R.id.bt_draw);
		
		bt_draw.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isDraw){
					isDraw = false;
					if(isColla){
						bt_draw.setText("协同");
					}else{
						bt_draw.setText("开始");
					}
					mBaiduMap.getUiSettings().setAllGesturesEnabled(true);
				}else{
					isDraw = true;
					bt_draw.setText("正在");
					mBaiduMap.getUiSettings().setAllGesturesEnabled(false);
				}
			}
		});
	
		bt_find = (Button)findViewById(R.id.bt_find);
		bt_find.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						List<User> users = new ArrayList<User>();
						users = ContacterManager.getContacterList(BaseMapActivity.this);
						String time = DatetimeUtil.date2Str(Calendar.getInstance(),Constant.MS_FORMART);
						for(int i=0;i<users.size();i++){
							Chat chat = XmppConnectionManager.getInstance().getConnection().getChatManager().
									createChat(users.get(i).getJID(), null);
							
							Msg mess = new Msg(myname, users.get(i).getJID(), "访问了对方位置", Msg.MSG_LBS_NEARBY, DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss(), Msg.MSG_OUT);
//							int msg_id = (int)DBManager.getInstance(BaseMapActivity.this).saveMsg(mess,"oim_msg");
							
							org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
							message.setProperty(Msg.KEY_TIME, time);
							message.setProperty(Msg.KEY_TYPE, Msg.MSG_LBS_NEARBY);
							message.setBody("request@" + String.valueOf(mBaiduMap.getLocationData().latitude) + "@"
									+ String.valueOf(mBaiduMap.getLocationData().longitude));
							message.setTo(users.get(i).getJID());
							try {
								if(chat==null){
								}else{
									chat.sendMessage(message);
									
								}
									
							} catch (XMPPException e) {
								// TODO Auto-generated catch block
//								e.printStackTrace();
							}
						}
					}
				}).start();
			}
		});
		
		bt_send = (Button)findViewById(R.id.bt_send);
		bt_send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(saveScreenPath != "" && saveScreenPath != null){
					new AlertDialog.Builder(BaseMapActivity.this)
					.setTitle("选项")
					.setMessage("选择你要的操作")
					.setPositiveButton("分享", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
							Uri imageUri = Uri.fromFile(new File(saveScreenPath));
					        Intent shareIntent = new Intent();
					        shareIntent.setAction(Intent.ACTION_SEND);
					        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
					        shareIntent.setType("image/*");
					        startActivity(Intent.createChooser(shareIntent, "分享到"));
						}
					})
					.setNeutralButton("发送", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
							Intent it = new Intent(BaseMapActivity.this, SendPicActivity.class);
							it.putExtra("msg_path", saveScreenPath);
							startActivityForResult(it, REQUEST_TO_SEND_PIC);
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					})
					.setCancelable(true)
					.create().show();
					
				}
			}
		});
		
		bt_clear = (Button)findViewById(R.id.bt_clear);
		bt_clear.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mBaiduMap.clear();
				if(location != null){
					OverlayOptions marker = new MarkerOptions().position(location).
							icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).zIndex(9).draggable(true).title(to.split("@")[0]);

					if(overlay == null){
						overlay = mBaiduMap.addOverlay(marker);
					}else{
						overlay.remove();
						overlay = mBaiduMap.addOverlay(marker);
					}
				}
			}
		});
		
		bt_poi = (Button)findViewById(R.id.bt_poi);
		bt_poi.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				List<Poi> pois = new ArrayList<Poi>();
				pois = DBManager.getInstance(BaseMapActivity.this).getPois(myname);
				for(int i=0;i<pois.size();i++){
					
					OverlayOptions marker = new MarkerOptions().position(new LatLng(pois.get(i).getLat(),pois.get(i).getLon())).
							icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).zIndex(9).draggable(true).title("path@"+pois.get(i).getPath());
					mBaiduMap.addOverlay(marker);
					
				}
				
				if(mBaiduMap.isMyLocationEnabled()){
					mBaiduMap.setMyLocationEnabled(false);
				}else{
					mBaiduMap.setMyLocationEnabled(true);
				}
			}
		});
		
		bt_back = (ImageButton)findViewById(R.id.bt_back);
		bt_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
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
		poiSearchListener = new PoiSearchListener(BaseMapActivity.this, mMapView);
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(poiSearchListener);
		searchPoiOverlay = new MyPoiOverlay(mBaiduMap);
		searchPoiOverlay.setPoiSearch(BaseMapActivity.this,mPoiSearch,mMapView);
		poiSearchListener.setOverlay(searchPoiOverlay);
//		mSuggestionSearch = SuggestionSearch.newInstance();
//		mSuggestionSearch.setOnGetSuggestionResultListener(poiSearchListener);
		
		mapevent();
	}

	private void mapevent(){
		
		points = new ArrayList<LatLng>();
	
		mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
			
			@Override
			public void onTouch(MotionEvent ev) {
				// TODO Auto-generated method stub
				if(isDraw){
					switch (ev.getAction()) {
					case MotionEvent.ACTION_DOWN:
						points.clear();
//						List<LatLng> points = new ArrayList<LatLng>();
						Point pt = new Point((int)ev.getX(), (int)ev.getY());
						LatLng ll = mBaiduMap.getProjection().fromScreenLocation(pt);
						points.add(ll);
						break;
					case MotionEvent.ACTION_MOVE:
						Point pt2 = new Point((int)ev.getX(), (int)ev.getY());
						LatLng ll2 = mBaiduMap.getProjection().fromScreenLocation(pt2);
						points.add(ll2);
//						ooPolyline.points(points);
						break;
					case MotionEvent.ACTION_UP:
						Point pt3 = new Point((int)ev.getX(), (int)ev.getY());
						LatLng ll3 = mBaiduMap.getProjection().fromScreenLocation(pt3);
						points.add(ll3);
//						ooPolyline.points(points);		
						
						OverlayOptions ooPolyline = new PolylineOptions().width(10)
								.color(0xAAFF0000).points(points);
						mBaiduMap.addOverlay(ooPolyline);
						String draw = "draw";
						for(int i=0;i<points.size();i++){
							draw += "@" + String.valueOf(points.get(i).latitude) + "@" + String.valueOf(points.get(i).longitude);
						}

						if(to != ""){
							sendmsg(draw, Msg.MSG_LBS_DRAW);
						}
						break;
					case MotionEvent.ACTION_CANCEL:
						
						break;
					default:
						break;
					}
				}
			}
		});

		mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker arg0) {
				// TODO Auto-generated method stub
				String title = arg0.getTitle();
				OnInfoWindowClickListener listener = null;
				listener = new OnInfoWindowClickListener() {
					public void onInfoWindowClick() {
						
						mMapView.getMap().hideInfoWindow();
					}
				};
				LatLng ll =arg0.getPosition();
				if(title.split("@")[0].equals("loca")){
					Button button = new Button(BaseMapActivity.this);
					button.setText(title.split("@")[1]);
					button.setTextSize(12);
					button.setBackgroundColor(Color.WHITE);
					mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47, listener);
				}else if(title.split("@")[0].equals("path")){
					ImageView iv = new ImageView(BaseMapActivity.this);
					iv.setImageURI(Uri.fromFile(new File(title.split("@")[1])));
					iv.setMaxHeight(100);
					iv.setMaxWidth(100);
					iv.setScaleType(ScaleType.FIT_CENTER);
					iv.setBackgroundColor(Color.WHITE);
					mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(iv), ll, -47, listener);
				}
				mMapView.getMap().showInfoWindow(mInfoWindow);
				
				return true;
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
			
//			if (isFirstLoc) {
//				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				
				mBaiduMap.animateMapStatus(u);
				
//			}
			if(to != ""){
				sendmsg("location" + "@" + String.valueOf(location.getLatitude()) + "@" + String.valueOf(location.getLongitude()), Msg.MSG_LBS_DRAW);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	
	private void sendmsg(final String msg,final int msg_type){
		if(!ActivityUtil.hasInternetConnected(BaseMapActivity.this)){
			Toast.makeText(BaseMapActivity.this, "无网络连接", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Msg mess = new Msg(myname, to, msg, msg_type, DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss(), Msg.MSG_SENDING);
		msgs.add(mess);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				android.os.Message handlermsg = new android.os.Message();
				
				String time = DatetimeUtil.date2Str(Calendar.getInstance(),
						Constant.MS_FORMART);
				Message message = new Message();
				message.setProperty(Msg.KEY_TIME, time);
				message.setProperty(Msg.KEY_TYPE, msg_type);
				message.setBody(msg);
				message.setTo(to);
				
				Bundle bundle = new Bundle();
				handlermsg.setData(bundle);
				try {
					if(chat==null){
						
					}else{
						chat.sendMessage(message);
						
					}
						
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					
				}finally{
					
				}
				
			}
		}).start();
		
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
		unregisterReceiver(receiver);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMapView.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.NEW_MESSAGE_ACTION);
		filter.addAction(Constant.ACTION_RECONNECT_STATE);
		registerReceiver(receiver, filter);
	}
	
	private class ChatReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (Constant.NEW_MESSAGE_ACTION.equals(action)) {
				
				Msg msg = intent.getParcelableExtra(Msg.IMMESSAGE_KEY);
				String draw = msg.getMsg();
				if(draw == ""){
					return;
				}
				String[] pts = draw.split("@");
				if(msg.getUsername().split("@")[0].equals(to.split("@")[0])){
					if(pts[0].toString().equals("draw")){//协同编辑
						points.clear();
						for(int i=1;i<=(pts.length-1)/2;i++){
							LatLng ll3 = new LatLng(Double.valueOf(pts[2*i-1]), Double.valueOf(pts[2*i]));
							points.add(ll3);
						}
						
						OverlayOptions ooPolyline = new PolylineOptions().width(10)
								.color(0xAA00FF00).points(points);
						mBaiduMap.addOverlay(ooPolyline);
					}else if(pts[0].toString().equals("location")){//接收协同编辑方位置
						location = new LatLng(Double.valueOf(pts[1]), Double.valueOf(pts[2]));
						OverlayOptions marker = new MarkerOptions().position(location).
								icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).zIndex(9).draggable(true).title("loca@"+to.split("@")[0]);
//						locationPoiOverlay = new PoiOverlay(mBaiduMap);
//						locationPoiOverlay.removeFromMap();
						if(overlay == null){
							overlay = mBaiduMap.addOverlay(marker);
							
							sendmsg("location" + "@" + String.valueOf(mBaiduMap.getLocationData().latitude) + "@"
									+ String.valueOf(mBaiduMap.getLocationData().longitude), Msg.MSG_LBS_DRAW);
						}else{
							overlay.remove();
							overlay = mBaiduMap.addOverlay(marker);
						}
						
					}
					
				}
				
				if(pts[0].toString().equals("request")){//查找附近的人返回结果
					Msg mess = new Msg(myname, pts[3], "访问了对方位置", Msg.MSG_LBS_NEARBY, DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss(), Msg.MSG_OUT);
					int msg_id = (int)DBManager.getInstance(BaseMapActivity.this).saveMsg(mess,"oim_msg");
					
					LatLng ll3 = new LatLng(Double.valueOf(pts[1]), Double.valueOf(pts[2]));
					OverlayOptions marker = new MarkerOptions().position(ll3).
							icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).zIndex(9).draggable(true).title("loca@"+pts[3]);
					mBaiduMap.addOverlay(marker);
				}
			}
		
		}
	}
	
}
