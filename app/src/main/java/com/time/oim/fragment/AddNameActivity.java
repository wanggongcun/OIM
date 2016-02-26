package com.time.oim.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.time.oim.R;
import com.time.oim.db.DBManager;
import com.time.oim.manager.ContacterManager;
import com.time.oim.manager.XmppConnectionManager;
import com.time.oim.model.Msg;
import com.time.oim.model.Notice;
import com.time.oim.model.User;
import com.time.oim.util.ActivityUtil;
import com.time.oim.util.Constant;
import com.time.oim.util.DatetimeUtil;
import com.time.oim.view.clearEditText;

public class AddNameActivity extends Fragment {

	private ListView lv_add_name;
	private TextView tv_none;
	
	private String myname;
	private List<Notice> notices;
	private AddNameAdapter addNameAdapter = null;
	private ContacterReceiver receiver;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		myname = ActivityUtil.getSharedPreferences(getActivity(), Constant.USERNAME);
		notices = new ArrayList<Notice>();
		notices = DBManager.getInstance(getActivity()).getConstactNotice(myname);
		receiver = new ContacterReceiver();
		init();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.activity_add_name, container, false);
	}
	
	private void init(){
		
		
		lv_add_name = (ListView) getView().findViewById(R.id.lv_add_name);
		addNameAdapter = new AddNameAdapter();
		lv_add_name.setAdapter(addNameAdapter);
		addNameAdapter.notifyDataSetChanged();
		
		tv_none = (TextView) getView().findViewById(R.id.tv_none);
 		if(notices.size()==0){
			tv_none.setVisibility(View.VISIBLE);
		}else{
			tv_none.setVisibility(View.GONE);
		}
	}
	
	private class AddNameAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			
			return notices.size();
			
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return notices.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			final int pos = position;
			if(view == null){
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(getActivity()).inflate(R.layout.add_name_item, null);
				viewHolder.tv_add_name = (TextView) view.findViewById(R.id.tv_add_name);
				viewHolder.bt_add_accept = (TextView) view.findViewById(R.id.bt_add_accept);
				viewHolder.bt_refuse = (TextView) view.findViewById(R.id.bt_refuse);
				view.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) view.getTag();
			}
			
				Notice notice = notices.get(position);
				viewHolder.tv_add_name.setText(notice.getContent());
				if(ContacterManager.hasContacter(notice.getTo().split("@")[0]) || notice.getStatus()==1){
					viewHolder.bt_add_accept.setText("已通过");
				}else{
					viewHolder.bt_add_accept.setText("同意");
					viewHolder.bt_add_accept.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							XMPPConnection connection = null;
							try{
								connection = XmppConnectionManager.getInstance().getConnection();
							
								if(connection!=null && connection.isConnected() && connection.isSendPresence()){
									Presence presence = new Presence(Presence.Type.subscribed);
									presence.setTo(notices.get(pos).getFrom());
									XmppConnectionManager.getInstance().getConnection().sendPacket(presence);
									DBManager.getInstance(getActivity()).updateNotice(notices.get(pos).getId());
	//								notices = DBManager.getInstance(getActivity()).getUnReadConstactNotice(myname);
									addNameAdapter.notifyDataSetChanged();
									Toast.makeText(getActivity(), "添加成功", Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(getActivity(), "添加失败，未连接", Toast.LENGTH_SHORT).show();
								}
							}catch(Exception ex){
								Toast.makeText(getActivity(), "添加失败，未连接", Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
				viewHolder.bt_refuse.setVisibility(View.GONE);
//				viewHolder.bt_refuse.setVisibility(View.VISIBLE);
//				viewHolder.bt_refuse.setText("忽略");
//				viewHolder.bt_refuse.setOnClickListener(new View.OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						DBManager.getInstance(getActivity()).deleteNotice(notices.get(pos).getId());
//						notices = DBManager.getInstance(getActivity()).getUnReadConstactNotice(myname);
//						addNameAdapter.notifyDataSetChanged();
//					}
//				});
			
			
			return view;
		}
	}
	
	final class ViewHolder{
		TextView tv_add_name;
		TextView bt_add_accept;
		TextView bt_refuse;
	}
	
	private class ContacterReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if (Constant.ROSTER_ADDED.equals(action)) {
				refreshList();
			}else if (Constant.ROSTER_DELETED.equals(action)) {
				refreshList();
			}else if (Constant.ROSTER_PRESENCE_CHANGED.equals(action)) {
				refreshList();
			}else if (Constant.ROSTER_UPDATED.equals(action)) {
				refreshList();
			}else if (Constant.ROSTER_SUBSCRIPTION.equals(action)) {
//				subscripUserReceive(intent.getStringExtra(Constant.ROSTER_SUB_FROM));
				refreshList();
			} else if (Constant.NEW_MESSAGE_ACTION.equals(action)) {
				refreshList();
			} else if (Constant.ACTION_RECONNECT_STATE.equals(action)) {
				boolean isSuccess = intent.getBooleanExtra(
						Constant.RECONNECT_STATE, true);
//				handReConnect(isSuccess);
			}

		}
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
		
		
	}

	private void refreshList() {
		ContacterManager.init(getActivity(),XmppConnectionManager.getInstance().getConnection());
		ContacterManager.updateMsgs(getActivity());
	}
	
}
