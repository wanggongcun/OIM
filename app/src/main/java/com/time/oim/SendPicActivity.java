package com.time.oim;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONException;
import org.json.JSONObject;

import com.time.oim.adapter.DialogAdapter;
import com.time.oim.db.DBManager;
import com.time.oim.http.FileTrans;
import com.time.oim.manager.ContacterManager;
import com.time.oim.manager.XmppConnectionManager;
import com.time.oim.model.Msg;
import com.time.oim.model.User;
import com.time.oim.util.ActivityUtil;
import com.time.oim.util.Constant;
import com.time.oim.util.DatetimeUtil;
import com.time.oim.util.SecretUtil;
import com.time.oim.view.MyEditText;
import com.time.oim.view.clearEditText;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SendPicActivity extends Activity {

	private ImageButton ibt_back;
	private TextView tv_select_all;
	private clearEditText et_search;
	private ListView lv_contact;
	private TextView tv_select_ok;
	
	private TextView tv_select_num;
	private EditText et_select_name;
	
	private DialogAdapter dialogAdapter;
	private ProgressDialog pd = null;

	private List<String> selected_names;
	private List<User> users;
	private String sendbm = "";
	private String myname;
	
	private final static int SEND_PIC_SUCCESS = 1;
	private final static int SEND_PIC＿FAILED = 2;
	
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			pd.dismiss();
			switch (msg.what) {
			case SEND_PIC_SUCCESS:
				Toast.makeText(SendPicActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
				setResult(21);
				finish();
				
				break;
			case SEND_PIC＿FAILED:
				Toast.makeText(SendPicActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
				setResult(22);
				finish();
				break;
			
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_send_pic);
		
		if(getIntent().hasExtra("msg_path")){
			sendbm = getIntent().getStringExtra("msg_path");
		}else{
			Toast.makeText(SendPicActivity.this, "发生错误", Toast.LENGTH_SHORT).show();
			finish();
		}
		users = new ArrayList<User>();
		users = ContacterManager.getContacterList(SendPicActivity.this);
		myname = ActivityUtil.getSharedPreferences(SendPicActivity.this,Constant.PHONE_NUM);
		
		pd = new ProgressDialog(this);
		pd.setTitle("请稍后");
		pd.setMessage("正在发送图片");
		
		
		ibt_back = (ImageButton)findViewById(R.id.ibt_back);
		ibt_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		tv_select_all = (TextView) findViewById(R.id.tv_select_all);
		tv_select_all.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(tv_select_all.getText().equals("全选")){
					dialogAdapter.selectall();
					tv_select_all.setText("取消");
				}else{
					dialogAdapter.selectnone();
					tv_select_all.setText("全选");
				}
				
				
			}
		});
		et_search = (clearEditText) findViewById(R.id.et_search);
		et_search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(count == 0){
					et_search.setClearIconVisible(false);
				}else{
					et_search.setClearIconVisible(true);
				}
				filterData(s.toString());
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
		
		tv_select_num = (TextView) findViewById(R.id.tv_select_num);
		et_select_name = (EditText) findViewById(R.id.et_select_name);
		
		tv_select_ok = (TextView) findViewById(R.id.tv_select_ok);
		tv_select_ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selected_names = new ArrayList<String>();
				selected_names = dialogAdapter.getSelect();
					pd.show();
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							String result = FileTrans.getInstance(SendPicActivity.this).uploadFile(new File(sendbm));
							JSONObject resobj;
							Message msg = new Message();
							if(result == null || result == ""){
								msg.what = SEND_PIC＿FAILED;
							}else{
								String time = DatetimeUtil.date2Str(Calendar.getInstance(),Constant.MS_FORMART);
								try {
									resobj = new JSONObject(result);
									if(resobj.has("code") && resobj.getString("code").equals("0")){
										JSONObject jj = new JSONObject(resobj.getString("data"));
										String url =jj.getString("url");
										
										for(int i=0;i<selected_names.size();i++){
											Chat chat = XmppConnectionManager.getInstance().getConnection().getChatManager().
													createChat(ContacterManager.contacters.get(selected_names.get(i)).getJID(), null);
											Msg mess = new Msg(myname, selected_names.get(i), sendbm, Msg.MSG_PIC, DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss(), Msg.MSG_OUT);
											int msg_id = (int)DBManager.getInstance(SendPicActivity.this).saveMsg(mess,"oim_msg");
											
											org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
											message.setProperty(Msg.KEY_TIME, time);
											message.setProperty(Msg.KEY_TYPE, Msg.MSG_PIC);
											message.setBody(url);
											message.setTo(ContacterManager.contacters.get(selected_names.get(i)).getJID());
											try {
												if(chat==null){
													msg.what = SEND_PIC＿FAILED;
												}else{
													chat.sendMessage(message);
													
												}
													
											} catch (XMPPException e) {
												// TODO Auto-generated catch block
//												e.printStackTrace();
												msg.what = SEND_PIC＿FAILED;
											}
										}
										msg.what = SEND_PIC_SUCCESS;
									}else{
										msg.what = SEND_PIC＿FAILED;
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
		//							e.printStackTrace();
									msg.what = SEND_PIC＿FAILED;
								}
							}
							handler.sendMessage(msg);
						}
					}).start();
				}
			
		});
		
		
		
		lv_contact = (ListView)findViewById(R.id.lv_contact);
		dialogAdapter = new DialogAdapter(SendPicActivity.this, ContacterManager.getContacterList(SendPicActivity.this));
		lv_contact.setAdapter(dialogAdapter);
		lv_contact.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				lv_contact.setFocusable(true);
				lv_contact.requestFocus();
				return false;
			}
		});
		dialogAdapter.setView(tv_select_all, tv_select_num, et_select_name);
		dialogAdapter.notifyDataSetChanged();
	}
	
	private void filterData(String filterStr){
		List<User> filterUsers = new ArrayList<User>();
		
		if(TextUtils.isEmpty(filterStr)){
			filterUsers = users;
		}else{
			filterUsers.clear();
			for(User user : users){
				String name = user.getName();
				if(name.indexOf(filterStr.toString()) != -1){
					filterUsers.add(user);
				}
			}
		}
		
		dialogAdapter.updateListView(filterUsers);
	}
}
