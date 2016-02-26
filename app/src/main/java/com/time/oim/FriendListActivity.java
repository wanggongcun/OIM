package com.time.oim;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.jivesoftware.smack.XMPPException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.time.oim.adapter.SortAdapter;
import com.time.oim.db.DBManager;
import com.time.oim.http.FileTrans;
import com.time.oim.manager.ContacterManager;
import com.time.oim.manager.XmppConnectionManager;
import com.time.oim.map.BaseMapActivity;
import com.time.oim.model.Msg;
import com.time.oim.model.Notice;
import com.time.oim.model.User;
import com.time.oim.util.ActivityUtil;
import com.time.oim.util.Constant;
import com.time.oim.util.DatetimeUtil;
import com.time.oim.view.SideBar;
import com.time.oim.view.SlideListView;
import com.time.oim.view.SlideListView.MoveDirection;
import com.time.oim.view.clearEditText;

public class FriendListActivity extends Fragment {
	private SlideListView fri_listview ;
	private SideBar sideBar;
	private clearEditText et_search;
	private TextView tv_dialog;
	private TextView tv_unreadconstactnum;
	private ImageButton ibt_camera;
	private ImageButton ibt_add;
	
	private SortAdapter sortAdapter;
	private List<User> users;
	private ContacterReceiver receiver;
	private String myname;
	private int unReadConstactNoticeNum = 0;
	
	private final static int HANDLER_UPDATE_ADAPTER = 0;
	private final static int HANDLER_UPDATE_FILTER_ADAPTER = 1;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
//			pd.dismiss();
			switch(msg.what){
			case HANDLER_UPDATE_ADAPTER :
				sortAdapter.updateListView(users);
			case HANDLER_UPDATE_FILTER_ADAPTER :
				try{
					ArrayList<User> filterusers = new ArrayList<User>();
					filterusers = msg.getData().getParcelableArrayList("users");
					sortAdapter.updateListView(filterusers);
				}catch(Exception ex){
					sortAdapter.updateListView(users);
				}
			default:
				
				break;
			}
		}
		
	};
 
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		users = new ArrayList<User>();
		myname = ActivityUtil.getSharedPreferences(getActivity(), Constant.USERNAME);
		unReadConstactNoticeNum = DBManager.getInstance(getActivity()).getUnReadConstactNoticeNum(myname);
		
		receiver = new ContacterReceiver();
		init();
		refreshList();
		fri_listview.requestFocus();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.activity_friend_list, container, false);
	}
	
	
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		getActivity().unregisterReceiver(receiver);
		super.onPause();
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		IntentFilter filter = new IntentFilter();

		filter.addAction(Constant.ROSTER_ADDED);
		filter.addAction(Constant.ROSTER_DELETED);
		filter.addAction(Constant.ROSTER_PRESENCE_CHANGED);
		filter.addAction(Constant.ROSTER_UPDATED);
		filter.addAction(Constant.ROSTER_SUBSCRIPTION);
		// 好友请求
		filter.addAction(Constant.NEW_MESSAGE_ACTION);
		filter.addAction(Constant.ACTION_SYS_MSG);

		filter.addAction(Constant.ACTION_RECONNECT_STATE);
		getActivity().registerReceiver(receiver, filter);
		super.onResume();
		
		refreshList();
	}

	private void refreshList() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				users = ContacterManager.getContacterList(getActivity());
				if(users == null){
					users = new ArrayList<User>();
				}
				Collections.sort(users, new Comparator<User>() {

					@Override
					public int compare(User lhs, User rhs) {
						// TODO Auto-generated method stub
						if(lhs.getLastTime_second()<rhs.getLastTime_second()){
							return 1;
						}
						return -1;
					}
				});
				Message msg = new Message();
				msg.what = HANDLER_UPDATE_ADAPTER;
				handler.sendMessage(msg);
			}
		}).start();
		

	}
	
	private void init(){
		fri_listview = (SlideListView) getView().findViewById(R.id.fri_listview);
		sortAdapter = new SortAdapter(getView().getContext(), users);
		fri_listview.setAdapter(sortAdapter);
//		fri_listview.setMoveListener(new SlideListView.MoveListener() {
//			
//			@Override
//			public void moveItem(MoveDirection direction, int pos) {
//				// TODO Auto-generated method stub
//				int position = pos-1;
//				switch(direction){
//				case RIGHT:
//					Toast.makeText(getActivity(), "向右  "+ users.get(position).getName(), Toast.LENGTH_SHORT).show();
//					ContacterManager.clearMsg(users.get(position).getName());
//					users.get(position).setUnReadMsg(0);
//					sortAdapter.updateListView(users);
//					Intent it = new Intent(getView().getContext(), ChatListActivity.class);
//					it.putExtra("oim_chat_name", users.get(position).getJID());
//					startActivity(it);
//					getActivity().overridePendingTransition(R.anim.go_in_left, R.anim.go_out_right);
//					break;
//				case LEFT:
//					Toast.makeText(getActivity(), "向左  "+ position, Toast.LENGTH_SHORT).show();
//					break;
//				default:
//					
//					break;
//				}
//			}
//		});
		fri_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				int position = arg2-1;
				ContacterManager.clearMsg(users.get(position).getName());
				users.get(position).setUnReadMsg(0);
				sortAdapter.updateListView(users);
				Intent it = new Intent(getView().getContext(), ChatListActivity.class);
				it.putExtra("oim_chat_name", users.get(position).getJID());
				startActivity(it);
				getActivity().overridePendingTransition(R.anim.go_in_left, R.anim.go_out_right);
			}
		});
		fri_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if(fri_listview.isSlide)
					return false;
				final CharSequence[] items = { "删除联系人", "发送消息" };
				final int position = arg2-1;
				new AlertDialog.Builder(getActivity()).setTitle("选项")
						.setItems(items, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								if (which == 0) {
									deleteContacter(users.get(position).getJID());
								} else {
									ContacterManager.clearMsg(users.get(position).getName());
									users.get(position).setUnReadMsg(0);
									sortAdapter.updateListView(users);
									Intent it = new Intent(getView().getContext(), ChatListActivity.class);
									it.putExtra("oim_chat_name", users.get(position).getJID());
									startActivity(it);
									getActivity().overridePendingTransition(R.anim.go_in_left, R.anim.go_out_right);
								}
							}
						}).create().show();
				return false;
			}
			
		});
	
		
		sideBar = (SideBar) getView().findViewById(R.id.sideBar);
		sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
			
			@Override
			public void onTouchingLetterChanged(String s) {
				// TODO Auto-generated method stub
				int position = sortAdapter.getPositionForSection(Integer.valueOf(s));
				if(position != -1){
					fri_listview.setSelection(position);
				}
			}
		});
		et_search = (clearEditText) getView().findViewById(R.id.et_search);
		et_search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s=="" || s==null){
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
		tv_dialog = (TextView) getView().findViewById(R.id.dialog);
		sideBar.setTextView(tv_dialog);
		
		ibt_camera = (ImageButton) getView().findViewById(R.id.ibt_camera);
		ibt_camera.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent("fragment_change");
//				intent.putExtra("fragment_change","1");
//				getActivity().sendBroadcast(intent);
				Intent it = new Intent(getActivity(), BaseMapActivity.class);
				startActivityForResult(it, 1);
			}
		});
		ibt_add = (ImageButton) getView().findViewById(R.id.ibt_add);
		ibt_add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(getActivity(), AddFriendsActivity.class);
				startActivity(it);
				getActivity().overridePendingTransition(R.anim.go_zoom_in, R.anim.go_zoom_out);
			}
		});
		tv_unreadconstactnum = (TextView) getView().findViewById(R.id.tv_unreadconstactnum);
		if(unReadConstactNoticeNum>0){
//			tv_unreadconstactnum.setText(String.valueOf(unReadConstactNoticeNum));
			tv_unreadconstactnum.setVisibility(View.VISIBLE);
		}else{
			tv_unreadconstactnum.setVisibility(View.INVISIBLE);
		}
		
	}
	
	private void filterData(final String filterStr){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ArrayList<User> filterUsers = new ArrayList<User>();
				
				if(TextUtils.isEmpty(filterStr)){
					filterUsers = (ArrayList<User>) users;
				}else{
					filterUsers.clear();
					for(User user : users){
						String name = user.getName();
						if(name.indexOf(filterStr.toString()) != -1){
							filterUsers.add(user);
						}
					}
				}
				Bundle bd = new Bundle();
				bd.putParcelableArrayList("users", filterUsers);
				Message msg = new Message();
				msg.setData(bd);
				msg.what = HANDLER_UPDATE_FILTER_ADAPTER;
				
				handler.sendMessage(msg);
			}
		}).start();
		
	}
	
	private class ContacterReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (Constant.ROSTER_ADDED.equals(action)) {
				ContacterManager.init(getActivity(),XmppConnectionManager.getInstance().getConnection());
				addUserReceive();
			}else if (Constant.ROSTER_DELETED.equals(action)) {
				ContacterManager.init(getActivity(),XmppConnectionManager.getInstance().getConnection());
				deleteUserReceive();
			}else if (Constant.ROSTER_PRESENCE_CHANGED.equals(action)) {
				ContacterManager.init(getActivity(),XmppConnectionManager.getInstance().getConnection());
//				changePresenceReceive(user);
				updateUserReceive();
			}else if (Constant.ROSTER_UPDATED.equals(action)) {
				ContacterManager.init(getActivity(),XmppConnectionManager.getInstance().getConnection());
				updateUserReceive();
			}else if (Constant.ROSTER_SUBSCRIPTION.equals(action)) {
				ContacterManager.init(getActivity(),XmppConnectionManager.getInstance().getConnection());
//				subscripUserReceive(intent.getStringExtra(Constant.ROSTER_SUB_FROM));
				subscripUserReceive();
			} else if (Constant.NEW_MESSAGE_ACTION.equals(action)) {
//				 intent.putExtra("noticeId", noticeId);
//				String noticeId = intent.getStringExtra("noticeId");
				Msg msg = (Msg)intent.getParcelableExtra(Msg.IMMESSAGE_KEY);
				msgReceive(msg);
//				Toast.makeText(getActivity(), action, Toast.LENGTH_SHORT).show();
			} else if (Constant.ACTION_RECONNECT_STATE.equals(action)) {
				boolean isSuccess = intent.getBooleanExtra(
						Constant.RECONNECT_STATE, true);
//				handReConnect(isSuccess);
			}

		}
	}
	
	private void deleteContacter(final String jid){
		new AlertDialog.Builder(getActivity()).setTitle("删除联系人")
				.setMessage("确认删除联系人:" + jid.split("@")[0])
				.setNegativeButton("取消", null)
				.setPositiveButton("删除", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						try {
							ContacterManager.deleteUser(jid);
							DBManager.getInstance(getActivity()).deleteContacter(myname, jid.split("@")[0]);
						} catch (Exception e) {
							// TODO Auto-generated catch block
//							e.printStackTrace();
							DBManager.getInstance(getActivity()).deleteContacter(myname, jid.split("@")[0]);
						}
					}
				}).show();
	}
	
	private void setNickName(String name){
		
	}
	
	private void setNickname(final User user) {
		final EditText name_input = new EditText(getActivity());
		name_input.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		name_input.setHint("输入昵称");
		new AlertDialog.Builder(getActivity()).setTitle("修改昵称").setView(name_input)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String name = name_input.getText().toString();
						if (!"".equals(name))
							setNickname(user, name);
					}
				}).setNegativeButton("取消", null).show();
	}
	
	private void setNickname(User user, String nickname) {
		
		ContacterManager.setNickname(user, nickname, XmppConnectionManager
				.getInstance().getConnection());
	}
	
	protected void addUserReceive() {
		refreshList();
	}

	protected void deleteUserReceive() {
		
		refreshList();
	}

	protected void updateUserReceive() {
		refreshList();
	}

	protected void subscripUserReceive() {
//		Notice notice = new Notice();
//		notice.setFrom(subFrom);
//		notice.setNoticeType(Notice.CHAT_MSG);
		unReadConstactNoticeNum = DBManager.getInstance(getActivity()).getUnReadConstactNoticeNum(myname);
		if(unReadConstactNoticeNum>0){
//			tv_unreadconstactnum.setText(String.valueOf(unReadConstactNoticeNum));
			tv_unreadconstactnum.setVisibility(View.VISIBLE);
		}else{
			tv_unreadconstactnum.setVisibility(View.INVISIBLE);
		}
	}
	
	protected void msgReceive(Msg msg) {
//		ContacterManager.updateMsg(msg);
		refreshList();
	}
}
