package com.time.oim;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.time.oim.adapter.ExpressionAdapter;
import com.time.oim.adapter.ExpressionPagerAdapter;
import com.time.oim.adapter.MsgListAdapter;
import com.time.oim.db.DBManager;
import com.time.oim.http.FileTrans;
import com.time.oim.manager.ContacterManager;
import com.time.oim.manager.MsgManager;
import com.time.oim.manager.XmppConnectionManager;
import com.time.oim.map.BaseMapActivity;
import com.time.oim.map.LocationActivity;
import com.time.oim.model.Msg;
import com.time.oim.util.ActivityUtil;
import com.time.oim.util.Constant;
import com.time.oim.util.DatetimeUtil;
import com.time.oim.util.SecretUtil;
import com.time.oim.view.HideListView;
import com.time.oim.view.PasteEditText;

@SuppressLint("HandlerLeak")
public class ChatListActivity extends Activity {

	private ImageButton ibt_back;
	private ImageButton ibt_add;
	private TextView tv_to_chat_name;
	private HideListView lv_msg;
	private EditText et_msg;
	private Button bt_send;
	private Button bt_camera;
	private RelativeLayout rl_imageview;
	private ImageView iv_imageview;
	private Button bt_biaoqing;
	private LinearLayout ll_biaoqing;
	private ViewPager expressionViewpager;
	
	private String myname;
	private String to;
	private List<Msg> msgs;
	private MsgManager msgManager;
	private DBManager dbManager;
	private Chat chat = null;
	private MsgListAdapter msgListAdapter;
	private ChatReceiver receiver;
	
	private boolean isText = false;
	public static final int MSG_SENDIND = 3;
	public static final int MSG_SENDED = 1;
	public static final int MSG_SEND_FAIL = 2;
	public static final int CHAT_CONNECT = 4;
	public static final int CHAT_DISCONNECT = 5;
	public static final int REQUEST_MAP = 10;
	public static final int RESULT_MSG_SEND_MAP = 11;
	
	private int mMoveX;
	private int mDownX;
	private String path;
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(android.os.Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			String msg_id;
			String msg_content;
			switch(msg.what){
			case MSG_SENDIND:
				
				break;
			case MSG_SEND_FAIL:
				msg_id = msg.getData().getString("msg_id");
				msgListAdapter.changeStatus(msg_id, MSG_SEND_FAIL);
//				Toast.makeText(ChatListActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
				break;
			case MSG_SENDED:
				msg_id = msg.getData().getString("msg_id");
				msgListAdapter.changeStatus(msg_id, MSG_SENDED);
//				Toast.makeText(ChatListActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
				break;
			case CHAT_CONNECT:
				chat = XmppConnectionManager.getInstance().getConnection().getChatManager().createChat(to, null);
//				msg_content = msg.getData().getString("msg_content"); 
//				sendmsg(msg_content,Msg.MSG_TEXT);
				break;
			case CHAT_DISCONNECT:
				chat = null;
//				msg_content = msg.getData().getString("msg_content"); 
//				sendmsg(msg_content,Msg.MSG_TEXT);
				break;
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat_list);
		if(getIntent().hasExtra("oim_chat_name"));
			to = getIntent().getStringExtra("oim_chat_name");
		if(to=="" && to.trim()==""){
			return;
		}
		
		myname = ActivityUtil.getSharedPreferences(ChatListActivity.this, Constant.USERNAME);
		if(myname=="" && myname.trim()==""){
			return;
		}
			
		msgManager = MsgManager.getInstance(ChatListActivity.this);
		
		
		dbManager = DBManager.getInstance(ChatListActivity.this);
		msgs = new ArrayList<Msg>();
		msgs.addAll(dbManager.getOldMsgs(to, myname));
		msgs.addAll(msgManager.getUnReadMsgsWithclear(to,myname,dbManager));
		
		rl_imageview = (RelativeLayout)findViewById(R.id.rl_iamgeview);
		iv_imageview = (ImageView)findViewById(R.id.iv_imageview);
		
		
		msgListAdapter = new MsgListAdapter(ChatListActivity.this);
		msgListAdapter.setIv(rl_imageview, iv_imageview);
		receiver = new ChatReceiver();
		init();
		msgListAdapter.updateMsgs(msgs);
		msgListAdapter.notifyDataSetChanged();
		initlist();
		connect("oim");
		
	}

	private void init(){
		ibt_back = (ImageButton)findViewById(R.id.ibt_back);
		ibt_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.go_in_right, R.anim.go_out_left);
			}
		});
		ibt_add= (ImageButton) findViewById(R.id.ibt_add);
		tv_to_chat_name = (TextView)findViewById(R.id.tv_to_chat_name);
		tv_to_chat_name.setText(to.split("@")[0]);
		lv_msg = (HideListView) findViewById(R.id.lv_msg);
		lv_msg.setAdapter(msgListAdapter);
		msgListAdapter.notifyDataSetChanged();
		lv_msg.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
//				InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//				im.hideSoftInputFromWindow(et_msg.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				hideKeyboard();
				ll_biaoqing.setVisibility(View.GONE);
//				final CharSequence[] items = { "保存消息", "删除消息" };
				if(msgs.get(arg2).getInorOut() == Msg.MSG_SENDING)
					msgListAdapter.changeStatus(msgs.get(arg2).getId(), Msg.MSG_FAIL);
				if(msgs.get(arg2).getType() == Msg.MSG_TEXT){
					if(msgs.get(arg2).getSave() == Msg.MSG_NOSAVE){
						DBManager.getInstance(ChatListActivity.this).saveMsg(msgs.get(arg2).getId(),Msg.MSG_SAVE);
						msgs.get(arg2).setSave(Msg.MSG_SAVE);
					}else{
						DBManager.getInstance(ChatListActivity.this).saveMsg(msgs.get(arg2).getId(),Msg.MSG_NOSAVE);
						msgs.get(arg2).setSave(Msg.MSG_NOSAVE);
					}
					msgListAdapter.updateMsgs(msgs);
					msgListAdapter.notifyDataSetChanged();
				}else if(msgs.get(arg2).getType() == Msg.MSG_LBS){
					if(msgs.get(arg2).getMsg().split("@").length < 3){
						Toast.makeText(ChatListActivity.this, "无法查看位置", Toast.LENGTH_SHORT).show();
					}else{
						Intent it = new Intent(ChatListActivity.this, LocationActivity.class);
						it.putExtra("showlocation", msgs.get(arg2).getMsg());
						startActivity(it);
					}
				}else if(msgs.get(arg2).getType() == Msg.MSG_LBS_SHARE){
					Intent it = new Intent(ChatListActivity.this, BaseMapActivity.class);
					it.putExtra("to", to);
					startActivityForResult(it, REQUEST_MAP);
				}
			}
			
		});
		
		et_msg = (EditText) findViewById(R.id.et_msg);
		et_msg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ll_biaoqing.setVisibility(View.GONE);
			}
		});
		et_msg.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s=="" || s==null || s.length() == 0){
					isText = false;
					bt_send.setText("位置");
				}else{
					isText = true;
					bt_send.setText("发送");
				}
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
		bt_send = (Button) findViewById(R.id.bt_send);
		bt_send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ll_biaoqing.setVisibility(View.GONE);
				if(isText){
					if(et_msg.getText().toString()!=""){
//						connect(et_msg.getText().toString());
						if(et_msg.getText().toString().length()>0){
							sendmsg(et_msg.getText().toString(),Msg.MSG_TEXT);
							et_msg.setText("");
						}
					}
				}else{
					final CharSequence[] items = { "发送我的位置", "发送位置","发起地图编辑" };
					new AlertDialog.Builder(ChatListActivity.this).setTitle("选项")
							.setItems(items, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									if (which == 0) {
										Intent it = new Intent(ChatListActivity.this, LocationActivity.class);
										it.putExtra("ismylocation", "true");
										startActivityForResult(it, REQUEST_MAP);
									} else if(which == 1){
										Intent it = new Intent(ChatListActivity.this, LocationActivity.class);
										it.putExtra("ismylocation", "false");
										startActivityForResult(it, REQUEST_MAP);
									} else if(which == 2){
										sendmsg("向你发起", Msg.MSG_LBS_SHARE);
										Intent it = new Intent(ChatListActivity.this, BaseMapActivity.class);
										it.putExtra("to", to);
										startActivityForResult(it, REQUEST_MAP);
									}
								}
							}).create().show();
				}
				
			}
		});
		
		bt_camera = (Button) findViewById(R.id.bt_camera);
		bt_camera.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ll_biaoqing.setVisibility(View.GONE);
				Intent it = new Intent(ChatListActivity.this, MainActivity.class);
				it.putExtra("request_str", "chat_to_one");
				startActivityForResult(it, 1);
			}
		});
		
		ll_biaoqing = (LinearLayout)findViewById(R.id.ll_biaoqing);
		bt_biaoqing = (Button)findViewById(R.id.bt_biaoqing);
		bt_biaoqing.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(ll_biaoqing.getVisibility() == View.VISIBLE){
					ll_biaoqing.setVisibility(View.GONE);
				}else{
					hideKeyboard();
					ll_biaoqing.setVisibility(View.VISIBLE);
				}
			}
		});
		expressionViewpager = (ViewPager) findViewById(R.id.vPager);
		List<View> views = new ArrayList<View>();
		View gv1 = getGridChildView(1);
		View gv2 = getGridChildView(2);
		views.add(gv1);
		views.add(gv2);
		expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(et_msg.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//		return super.onTouchEvent(event);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 1){
			if(data.getStringExtra("msg_type") == "1" || data.getStringExtra("msg_type").equals("1")){
				path = data.getStringExtra("msg_path");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
				}
				sendimg();

			}else{
//				sendmsg("找不到图片",);
				Toast.makeText(ChatListActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
			}
			
		}else if(resultCode == RESULT_MSG_SEND_MAP){
			String msg_path = data.getStringExtra("location");
			sendmsg(msg_path,Msg.MSG_LBS);
		}
	}

	private void connect(final String msg_content){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				android.os.Message msg = new android.os.Message();
				XMPPConnection connection = null;
				connection = XmppConnectionManager.getInstance().getConnection();
				if(connection == null){
					msg.what = CHAT_DISCONNECT;
				}else{
					msg.what = CHAT_CONNECT;
				}
				Bundle bb = new Bundle();
				bb.putString("msg_content", msg_content);
				msg.setData(bb);
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	private void sendmsg(final String msg,final int msg_type){
		Msg mess = new Msg(myname, to, msg, msg_type, DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss(), Msg.MSG_SENDING);
		final int msg_id = (int)dbManager.saveMyMsg(mess,"oim_msg");
		mess.setId(String.valueOf(msg_id));
		msgs.add(mess);
		msgListAdapter.updateMsgs(msgs);
		msgListAdapter.notifyDataSetChanged();
		initlist();
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
				bundle.putString("msg_id", String.valueOf(msg_id));
				handlermsg.setData(bundle);
				try {
					if(chat==null){
						handlermsg.what = MSG_SEND_FAIL;
					}else{
						chat=null;
						chat = XmppConnectionManager.getInstance().getConnection().getChatManager().createChat(to, null);
						chat.sendMessage(message);
						handlermsg.what = MSG_SENDED;
					}
						
				} catch (Exception e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					handlermsg.what = MSG_SEND_FAIL;
				}finally{
					handler.sendMessage(handlermsg);
				}
				
			}
		}).start();
		
	}
	
	private void sendimg(){
		Msg mess = new Msg(myname, to, path, Msg.MSG_PIC, DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss(), Msg.MSG_SENDING);
		final int msg_id = (int)dbManager.saveMsg(mess,"oim_msg");
		mess.setId(String.valueOf(msg_id));
		msgs.add(mess);
		msgListAdapter.updateMsgs(msgs);
		msgListAdapter.notifyDataSetChanged();
		initlist();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub

				String result = FileTrans.getInstance(ChatListActivity.this).uploadFile(new File(path));
				android.os.Message handlermsg = new android.os.Message();
				
				if(result == null || result == ""){
					handlermsg.what = MSG_SEND_FAIL;
				}else{
					String time = DatetimeUtil.date2Str(Calendar.getInstance(),
							Constant.MS_FORMART);
					JSONObject resobj = null;
					try {
						resobj = new JSONObject(result);
					
						if(resobj.has("code") && resobj.getString("code").equals("0")){
							JSONObject jj = new JSONObject(resobj.getString("data"));
							String url = jj.getString("url");
							Message message = new Message();
							message.setProperty(Msg.KEY_TIME, time);
							message.setProperty(Msg.KEY_TYPE, Msg.MSG_PIC);
							message.setBody(url);
							message.setTo(to);
							
							Bundle bundle = new Bundle();
							bundle.putString("msg_id", String.valueOf(msg_id));
							handlermsg.setData(bundle);
							try {
								if(chat==null){
									handlermsg.what = MSG_SEND_FAIL;
								}else{
//									chat=null;
//									chat = XmppConnectionManager.getInstance().getConnection().getChatManager().createChat(to, null);
									Chat chatimg = XmppConnectionManager.getInstance().getConnection().getChatManager().
											createChat(to, null);
									chatimg.sendMessage(message);
									handlermsg.what = MSG_SENDED;
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								handlermsg.what = MSG_SEND_FAIL;
							}
						}else{
							handlermsg.what = MSG_SEND_FAIL;
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						handlermsg.what = MSG_SEND_FAIL;
					}
				}
				handler.sendMessage(handlermsg);
			}
		}).start();
	}

	private void initlist(){
		int count = lv_msg.getCount();
		if(count>0)
			lv_msg.setSelection(count-1);
	}
	
	private View getGridChildView(int i) {
		View view = View.inflate(this, R.layout.expression_gridview, null);
		GridView gv = (GridView) view.findViewById(R.id.gv_biaoqing);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = getExpressionRes(35).subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(getExpressionRes(35).subList(20, getExpressionRes(35).size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					if(filename!="delete_expression"){
						sendmsg(filename, Msg.MSG_BIAOQING);
					}
				} catch (Exception e) {
					
				}

			}
		});
		return view;
	}
	
	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;

			reslist.add(filename);

		}
		return reslist;

	}
	
	private class ChatReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (Constant.NEW_MESSAGE_ACTION.equals(action)) {
				msgs.addAll(msgManager.getUnReadMsgsWithclear(to,myname,dbManager));
				msgListAdapter.notifyDataSetChanged();
				initlist();
//				Msg msg = intent.getParcelableExtra(Msg.IMMESSAGE_KEY);
//				int noticeId = intent.getIntExtra(Constant.NOTICE_ID, 0);
//				ActivityUtil.showToast(ChatListActivity.this,String.valueOf(noticeId));
//				if(msg.getUsername().split("@")[0].equals(to.split("@")[0])){
//					MsgManager.getInstance(context).getMsgsWithclear(to);
//					NoticeManager.getInstance(context).removeNotice(String.valueOf(noticeId));
////					notificationManager.cancel(noticeId);
////					msglist.add(msg);
//					msgs.add(msg);
//					msgListAdapter.notifyDataSetChanged();
//				}
			}
		}
		
	}
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.go_in_right, R.anim.go_out_left);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		IntentFilter filter = new IntentFilter();

		filter.addAction(Constant.NEW_MESSAGE_ACTION);

		filter.addAction(Constant.ACTION_RECONNECT_STATE);
		registerReceiver(receiver, filter);
		super.onResume();
	}
	
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				 ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
				 	hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
}
