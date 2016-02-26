package com.time.oim;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.time.oim.adapter.ShuoshuoAdapter;
import com.time.oim.http.DataTrans;
import com.time.oim.manager.ShuoshuoManager;
import com.time.oim.model.Shuoshuo;
import com.time.oim.util.ActivityUtil;
import com.time.oim.util.Constant;
import com.time.oim.util.DatetimeUtil;
import com.time.oim.view.MyListView;
import com.time.oim.view.RefreshListView;

public class StoryActivity extends Fragment {

	private ImageButton ibt_add;
	private RefreshListView lv_shuoshuo;
	private ShuoshuoAdapter shuoshuoAdapter;
	private List<Shuoshuo> shuoshuos;
	
	private static final int SEND_SHUOSHUO = 1;
	private static final int SELECT_CAMER = 2;
	
	private static final int USERLOGIN_SUCCESS = 0;
	private static final int USERLOGIN_FAIL= 1;
	private static final int PUTMESSAGE_SUCCESS = 2;
	private static final int PUTMESSAGE_FAIL= 3;
	private String myname;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		SharedPreferences preferences = getActivity().getSharedPreferences(Constant.LOGIN_SET, 0);
//		myname = preferences.getString(Constant.USERNAME, "");
		myname = ActivityUtil.getSharedPreferences(getActivity(), Constant.USERNAME);
		shuoshuos = new ArrayList<Shuoshuo>();
		shuoshuos = ShuoshuoManager.getInstance(getActivity()).getShuoshuos();
		
		Shuoshuo shuoshuo = new Shuoshuo();
		shuoshuo.setUsername(myname);
		shuoshuo.setContent("我是要成为海贼王的男人   --路飞");
		shuoshuo.setHasImage(1);
		shuoshuo.setTime(DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss());
		shuoshuo.setImageURL(String.valueOf(R.drawable.onepeace));
		shuoshuos.add(shuoshuo);
		
		Shuoshuo shuoshuo2 = new Shuoshuo();
		shuoshuo2.setUsername(myname);
		shuoshuo2.setContent("我是一只孤独的猫猫猫");
		shuoshuo2.setHasImage(1);
		shuoshuo2.setTime(DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss());
		shuoshuo2.setImageURL(String.valueOf(R.drawable.cat));
		shuoshuos.add(shuoshuo2);
		
		Shuoshuo shuoshuo3 = new Shuoshuo();
		shuoshuo3.setUsername(myname);
		shuoshuo3.setContent("很帅的画面，赞！！！");
		shuoshuo3.setHasImage(1);
		shuoshuo3.setTime(DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss());
		shuoshuo3.setImageURL(String.valueOf(R.drawable.fight));
		shuoshuos.add(shuoshuo3);
		
		Shuoshuo shuoshuo4 = new Shuoshuo();
		shuoshuo4.setUsername(myname);
		shuoshuo4.setContent("我们是有梦想的一群人");
		shuoshuo4.setHasImage(1);
		shuoshuo4.setTime(DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss());
		shuoshuo4.setImageURL(String.valueOf(R.drawable.nakama));
		shuoshuos.add(shuoshuo4);
		
		shuoshuos.add(shuoshuo);
		shuoshuos.add(shuoshuo2);
		shuoshuos.add(shuoshuo3);
		shuoshuos.add(shuoshuo4);
		
		shuoshuoAdapter = new ShuoshuoAdapter(getActivity(), shuoshuos);
		init();
		shuoshuoAdapter.notifyDataSetChanged();
//		lv_shuoshuo.setSelection(1);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.activity_story, container, false);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == SEND_SHUOSHUO){
			if(data.getBooleanExtra("sendshuoshuo",false)){
				int hasImage = data.getIntExtra("hasImage",0);
				String path = data.getStringExtra("path");
//				Bitmap bm = SaveFileUtil.getxtsldraw(getActivity(), path);
				Shuoshuo shuoshuo = new Shuoshuo();
				shuoshuo.setUsername(myname);
				shuoshuo.setContent(data.getStringExtra("content"));
				shuoshuo.setHasImage(hasImage);
				shuoshuo.setTime(DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss());
				shuoshuo.setImageURL(path);
				
//				putMessage(shuoshuo);
				shuoshuos.add(0, shuoshuo);
				shuoshuoAdapter.updateListView(shuoshuos);
			}
			
		}
	}

	private void init(){

		ibt_add = (ImageButton) getView().findViewById(R.id.ibt_add);
		ibt_add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), ShuoshuoActivity.class);
				startActivityForResult(intent, 1);
				
			}
		});
		
		lv_shuoshuo = (RefreshListView) getView().findViewById(R.id.lv_shuoshuo);
		lv_shuoshuo.setAdapter(shuoshuoAdapter);
		lv_shuoshuo.setOnRefreshListener(new RefreshListView.RefreshListener() {
			
			@Override
			public Object refreshing() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void refreshed(Object obj) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void more() {
				// TODO Auto-generated method stub
				
			}
		});
//		lv_shuoshuo.setonRefreshListener(new MyListView.OnRefreshListener() {
//			
//			@Override
//			public void onRefresh() {
//				// TODO Auto-generated method stub
//				try {  
//                    Thread.sleep(2000);  
//                } catch (Exception e) {  
//                    e.printStackTrace();  
//                }  
//			}
//		});
	}
	
	private void putMessage(final Shuoshuo shuoshuo){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				String id = DataTrans.getInstance(getActivity()).sendShuoshuo(shuoshuo);
				if(id==null || id=="" || id.equals("")){
					msg.what = PUTMESSAGE_FAIL;
				}else{
					msg.what = PUTMESSAGE_SUCCESS;
				}
				mHandler.sendMessage(msg);
			}
		}).start();
	}
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case USERLOGIN_SUCCESS:
				Toast.makeText(getActivity(), "login success", Toast.LENGTH_SHORT).show();
				break;
			case USERLOGIN_FAIL:

				Toast.makeText(getActivity(), "login fail", Toast.LENGTH_SHORT).show();
				break;
			case PUTMESSAGE_SUCCESS:
				Shuoshuo shuoshuo = new Shuoshuo();
				shuoshuo = msg.getData().getParcelable("shuoshuo");
				
				shuoshuos.add(shuoshuo);
				shuoshuoAdapter.updateListView(shuoshuos);
				shuoshuoAdapter.notifyDataSetChanged();
				Toast.makeText(getActivity(), "put message success", Toast.LENGTH_SHORT).show();
				break;
			case PUTMESSAGE_FAIL:
				Toast.makeText(getActivity(), "put message fail", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
		
	};

}
