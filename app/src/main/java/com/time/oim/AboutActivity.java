package com.time.oim;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.time.oim.http.DataTrans;
import com.time.oim.http.FileTrans;
import com.time.oim.manager.XmppApplication;

public class AboutActivity extends Activity {

	private ImageButton ibt_back;
	private TextView tv_update;
	private TextView tv_intro;
	private TextView tv_suggest;
	
	private static final int UPDATE_LAST = 1;//
	private static final int UPDATE_NONE = 2;//
	private static final int UPDATE_OK = 3;//
	private static final int UPDATE_CANCLE = 4;//
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
				new AlertDialog.Builder(AboutActivity.this)
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
												String path = FileTrans.getInstance(AboutActivity.this).downloadVersionFile(url);
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
					Toast.makeText(AboutActivity.this, "无更新", Toast.LENGTH_SHORT).show();
					
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
				Toast.makeText(AboutActivity.this,"更新失败", Toast.LENGTH_SHORT).show();
				break;
			default:
				
				break;
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_about);
		
		ibt_back = (ImageButton)findViewById(R.id.ibt_back);
		ibt_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		tv_update = (TextView)findViewById(R.id.tv_update);
		tv_update.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String result = DataTrans.getInstance(AboutActivity.this).getVersion();
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
		
		tv_intro = (TextView)findViewById(R.id.tv_intro);
		tv_suggest = (TextView)findViewById(R.id.tv_suggest);
	}
}
