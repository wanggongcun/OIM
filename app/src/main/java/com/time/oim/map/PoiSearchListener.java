package com.time.oim.map;

import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;

public class PoiSearchListener implements
		OnGetPoiSearchResultListener
//		, OnGetSuggestionResultListener
		{

	private MapView mMapView;
	private Context mContext;
	private BaiduMap mBaiduMap;
	private PoiOverlay overlay;
	
	public PoiSearchListener(Context context,MapView mapView){
		this.mContext =context;
		this.mMapView = mapView;
		mBaiduMap = mapView.getMap();
		
	}
	
	public void setOverlay(PoiOverlay o){
		overlay = o;
	}
	
	public void clearOverlay(){
		overlay.removeFromMap();
	}
	
//	@Override
//	public void onGetSuggestionResult(SuggestionResult res) {
//		// TODO Auto-generated method stub
//		if (res == null || res.getAllSuggestions() == null) {
//			return;
//		}
//	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		// TODO Auto-generated method stub
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(mContext, "抱歉，未找到结果", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(mContext, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
			.show();
		}
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		// TODO Auto-generated method stub
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			mBaiduMap.clear();
			Toast.makeText(mContext, "没有了", Toast.LENGTH_LONG).show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
//			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(mContext, strInfo, Toast.LENGTH_LONG)
					.show();
		}
		
		
	}
	
	

}
