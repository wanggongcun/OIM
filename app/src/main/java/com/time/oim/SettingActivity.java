package com.time.oim;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.time.oim.http.DataTrans;
import com.time.oim.http.FileTrans;
import com.time.oim.manager.ContacterManager;
import com.time.oim.manager.XmppApplication;
import com.time.oim.util.ActivityUtil;
import com.time.oim.util.Constant;

public class SettingActivity extends Fragment {

	private ImageButton ibt_camera;
	private TextView tv_accountsafe;
	private Switch sw_notify;
	private TextView tv_privacy;
	private TextView tv_update;
	private Button bt_login;
	
	private static final int UPDATE_LAST = 1;//
	private static final int UPDATE_NONE = 2;//
	private static final int UPDATE_OK = 3;//
	private static final int UPDATE_CANCLE = 4;//
	
	private Toast toast = null;
	private AlertDialog alertDialog = null;
//	ProgressDialog pd = null;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
//			pd.dismiss();
			switch(msg.what){
			case UPDATE_LAST :
				String version = msg.getData().getString("version");
				final String url = msg.getData().getString("url");
				new AlertDialog.Builder(getActivity())
							.setTitle("最新版本 " + version)
							.setMessage("是否马上更新")
							.setCancelable(true)
							.setPositiveButton("更新", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									new Thread(new Runnable() {
										
										@Override
										public void run() {
											// TODO Auto-generated method stub
											Message msg = new Message();
											try{
												String path = FileTrans.getInstance(getActivity()).downloadVersionFile(url);
												if(path == null){
													msg.what = UPDATE_CANCLE;
												}else{
													Bundle bd = new Bundle();
													bd.putString("path", path);
													msg.setData(bd);
													msg.what = UPDATE_OK;
												}
											}catch (Exception e) {
												// TODO: handle exception
												msg.what = UPDATE_CANCLE;
											}
											handler.sendMessage(msg);

										}
									}).start();
								}
							})
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									dialog.cancel();
								}
							}).create().show();
							
				break;
			case UPDATE_NONE :
					Toast.makeText(getActivity(), "无更新", Toast.LENGTH_SHORT).show();
					
				break;
			case UPDATE_OK :
				
				String path = msg.getData().getString("path");
				Intent intent = new Intent();    
		        // 设置目标应用安装包路径    
		        intent.setDataAndType(Uri.fromFile(new File(path)),    
		                "application/vnd.android.package-archive");    
		        startActivity(intent); 
				break;
			case UPDATE_CANCLE :
				Toast.makeText(getActivity(),"更新失败", Toast.LENGTH_SHORT).show();
				break;
			default:
				
				break;
			}
		}
		
	};
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
//		pd = new ProgressDialog(getActivity());
//		pd.setTitle("正在检查更新");
//		pd.setMessage("请稍后。。。");
		
		ibt_camera = (ImageButton) getView().findViewById(R.id.ibt_camera);
		ibt_camera.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent("fragment_change");
				intent.putExtra("fragment_change","1");
				getActivity().sendBroadcast(intent);
			}
		});
		
		tv_accountsafe = (TextView)getView().findViewById(R.id.tv_accountsafe);
		tv_accountsafe.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(getActivity(), AccountActivity.class);
				startActivity(it);
			}
		});
		
		
		sw_notify = (Switch) getView().findViewById(R.id.sw_notify);
		sw_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					Toast.makeText(getActivity(), "打开", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(getActivity(), "关闭", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		tv_privacy = (TextView)getView().findViewById(R.id.tv_privacy);
		tv_privacy.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "隐私保护", Toast.LENGTH_SHORT).show();
			}
		});
		
		tv_update = (TextView) getActivity().findViewById(R.id.tv_update);
		tv_update.setText(((XmppApplication)getActivity().getApplication()).getVersion() + " >");
		tv_update.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				pd.show();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String result = DataTrans.getInstance(getActivity()).getVersion();
						Message msg = new Message();
						if(result!=null){
							try {
								JSONObject jo = new JSONObject(result);
								if(jo.getString("version") == XmppApplication.oim_version || jo.getString("version").equals(XmppApplication.oim_version)){
									msg.what = UPDATE_NONE;
								}else{
									Bundle bd = new Bundle();
									bd.putString("version", jo.getString("version"));
									bd.putString("url", jo.getString("url"));
									msg.setData(bd);
									msg.what = UPDATE_LAST;
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
//								e.printStackTrace();
								msg.what = UPDATE_NONE;
							}
						}else{
							msg.what = UPDATE_NONE;
						}
						handler.sendMessage(msg);
					}
				}).start();
			}
		});
		
		bt_login = (Button) getView().findViewById(R.id.bt_login);
		bt_login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(getActivity(),LoginActivity.class);
				it.putExtra("relogin", true);
				startActivityForResult(it, 1);
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.activity_setting, container, false);
	}

}
