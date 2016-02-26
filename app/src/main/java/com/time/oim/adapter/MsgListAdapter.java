package com.time.oim.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.time.oim.ChatListActivity;
import com.time.oim.R;
import com.time.oim.db.DBManager;
import com.time.oim.http.FileTrans;
import com.time.oim.manager.FontManager;
import com.time.oim.manager.XmppConnectionManager;
import com.time.oim.map.LocationActivity;
import com.time.oim.model.Msg;
import com.time.oim.singleActivity.ImageActivity;
import com.time.oim.util.Constant;
import com.time.oim.util.DatetimeUtil;
import com.time.oim.util.SecretUtil;

public class MsgListAdapter extends BaseAdapter{

	private Context mContext;
	private List<Msg> msgs;
	
	private static final int STATUS_SENDING = 3;
	private static final int STATUS_SENDED = 1;
	private static final int STATUS_SEND_FAIL = 2;
	long mStartTime = 0;
	long mEndTime = 0;
	private RelativeLayout rl_iv;
	private ImageView iv_iv;
	
	public MsgListAdapter(Context context){
		this.mContext = context;
		msgs = new ArrayList<Msg>();
	}
	
	public void setIv(RelativeLayout rl,ImageView iv){
		rl_iv = rl;
		iv_iv = iv;
	}
	public void setIvgone(){
		rl_iv.setVisibility(View.GONE);
	}
	
	public void updateMsgs(List<Msg> ms){
		msgs = null;
		msgs = new ArrayList<Msg>();
		this.msgs = ms;
	}
	
	public Msg changeStatus(String msg_id,int status){
		for(int i=msgs.size()-1;i>=0;i--){
			if(msgs.get(i).getId().equals(msg_id)){
				msgs.get(i).setInorOut(status);
				DBManager.getInstance(mContext).updateMsg(msg_id, status);
				notifyDataSetChanged();
				return msgs.get(i);
			}
		}
		return null;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return msgs.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return msgs.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("ResourceAsColor")
	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		final Msg msg = msgs.get(position);
		
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.msg_item, null);
			viewHolder.tv_who = (TextView) view.findViewById(R.id.tv_who);
			viewHolder.tv_time = (TextView) view.findViewById(R.id.tv_time);
			viewHolder.view_color = (View) view.findViewById(R.id.view_color);
			viewHolder.tv_msg = (TextView) view.findViewById(R.id.tv_msg);
			viewHolder.iv_msg = (ImageView) view.findViewById(R.id.iv_msg);
			viewHolder.tv_status = (TextView) view.findViewById(R.id.tv_status);
			viewHolder.pb_status = (ProgressBar) view.findViewById(R.id.pb_status);
			viewHolder.rl_item = (RelativeLayout) view.findViewById(R.id.rl_item);
			viewHolder.tv_date = (TextView) view.findViewById(R.id.tv_date);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		Date date = DatetimeUtil.str2Date(msg.getDatetime());
		viewHolder.tv_who.setText(msg.getUsername().toString());
		viewHolder.tv_time.setText(msg.getDatetime().split(" ")[1]);
		
		if(msg.getType() == Msg.MSG_TEXT || msg.getType() == Msg.MSG_LBS_NEARBY){
			viewHolder.tv_msg.setVisibility(View.VISIBLE);
			viewHolder.iv_msg.setVisibility(View.GONE);
			viewHolder.tv_msg.setText(msg.getMsg());
		}else if(msg.getType()== Msg.MSG_LBS){
			viewHolder.tv_msg.setVisibility(View.VISIBLE);
			viewHolder.iv_msg.setVisibility(View.GONE);
			
			viewHolder.tv_msg.setText("【位置】"+msg.getMsg().split("@")[0]);
			
		}else if(msg.getType() == Msg.MSG_LBS_SHARE){
			viewHolder.tv_msg.setVisibility(View.VISIBLE);
			viewHolder.iv_msg.setVisibility(View.GONE);
			viewHolder.tv_msg.setText("【位置共享】"+msg.getMsg().split("@")[0]);
		}else if(msg.getType() == Msg.MSG_PIC){
		
		
			viewHolder.tv_msg.setVisibility(View.VISIBLE);
			viewHolder.iv_msg.setVisibility(View.GONE);
			
			File imgfile = new File(msg.getMsg());
			viewHolder.tv_msg.setText("【图片】长按信息查看图片。");
			if(imgfile.exists()){
				final Bitmap bm = BitmapFactory.decodeFile(msg.getMsg());
				viewHolder.tv_msg.setOnTouchListener(new View.OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							mStartTime = SystemClock.currentThreadTimeMillis();
							break;
						case MotionEvent.ACTION_MOVE:
							mEndTime = SystemClock.currentThreadTimeMillis();
							if(Math.abs(mEndTime-mStartTime)>50){
								iv_iv.setImageBitmap(bm);
								rl_iv.setVisibility(View.VISIBLE);
								iv_iv.requestFocus();
							}
							break;
						case MotionEvent.ACTION_CANCEL:
							rl_iv.setVisibility(View.GONE);
							break;
						case MotionEvent.ACTION_UP:
							rl_iv.setVisibility(View.GONE);
							break;
						default:
							break;
						}
						
						return true;
					}
				});
			}else{
				viewHolder.tv_msg.setOnTouchListener(new View.OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							break;
						case MotionEvent.ACTION_MOVE:
							if(Math.abs(mEndTime-mStartTime)>50){
								new AlertDialog.Builder(mContext).setTitle("提示").setMessage("找不到图片")
								.setPositiveButton("queren", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										dialog.cancel();
									}
								}).create().show();
							}
							break;
						case MotionEvent.ACTION_UP:
							rl_iv.setVisibility(View.GONE);
							break;
						default:
							break;
						}
						return true;
					}
				});
			}
			if(msg.getInorOut() ==Msg.MSG_IN){
				viewHolder.tv_msg.setTextColor(Color.rgb(52,170,220));
			}else{
				viewHolder.tv_msg.setTextColor(Color.rgb(255, 40, 105));
			}
			
		}else if(msg.getType() == Msg.MSG_BIAOQING){
			viewHolder.iv_msg.setVisibility(View.VISIBLE);
			viewHolder.tv_msg.setVisibility(View.GONE);
			int resid = mContext.getResources().getIdentifier(msg.getMsg(), "drawable", mContext.getPackageName());
			viewHolder.iv_msg.setImageResource(resid);
		}
		
		if(msg.getInorOut() ==Msg.MSG_IN){
			viewHolder.tv_who.setTextColor(Color.rgb(52,170,220));
		}else{
			viewHolder.tv_who.setTextColor(Color.rgb(255, 40, 105));
		}
		if(msg.getInorOut() == Msg.MSG_FAIL){
			viewHolder.tv_status.setVisibility(View.VISIBLE);
			viewHolder.pb_status.setVisibility(View.GONE);
			viewHolder.tv_status.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Message handlermsg = new Message();
							Bundle bundle = new Bundle();
							bundle.putString("msg_id", String.valueOf(msg.getId()));
							handlermsg.setData(bundle);
							XMPPConnection connection = null;
							connection = XmppConnectionManager.getInstance().getConnection();
							if(connection == null){
								handlermsg.what = STATUS_SEND_FAIL;
							}else{
								handlermsg.what = STATUS_SENDING;
							}
							
							mHandler.sendMessage(handlermsg);
						}
					}).start();
					
				}
			});
		}else if(msg.getInorOut()== Msg.MSG_SENDING){
			viewHolder.tv_status.setVisibility(View.GONE);
			viewHolder.pb_status.setVisibility(View.VISIBLE);
		}else{
			viewHolder.tv_status.setVisibility(View.GONE);
			viewHolder.pb_status.setVisibility(View.GONE);
		}
		
		if(msg.msg_save == 1){
			viewHolder.rl_item.setBackgroundColor(Color.rgb(229,229,234));
//			view.setBackgroundColor(Color.rgb(229,229,234));
		}else{
			viewHolder.rl_item.setBackgroundColor(Color.rgb(255,255,255));
//			view.setBackgroundColor(Color.rgb(255,255,255));
		}
		if(!(position>0 && msg.comparedate(msgs.get(position-1)))){
			viewHolder.tv_date.setVisibility(View.VISIBLE);
			String datastr = msg.getDatetime().trim().split(" ")[0];
			viewHolder.tv_date.setText(String.valueOf(Integer.valueOf(datastr.split("-")[1])) + "月"+String.valueOf(Integer.valueOf(datastr.split("-")[2])) + "日");
		}else{
			viewHolder.tv_date.setVisibility(View.GONE);
		}

		return view;
	}
	
	final static class ViewHolder {
		TextView tv_who;
		TextView tv_time;
		View view_color;
		TextView tv_msg;
		ImageView iv_msg;
		
		TextView tv_status;
		ProgressBar pb_status;
		
		RelativeLayout rl_item;
		TextView tv_date;
	}
	
	@SuppressWarnings("unused")
	private void sendmsg(final Msg msg){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message handlermsg = new Message();
				String to;
				XMPPConnection connection2 = null;
				connection2 = XmppConnectionManager.getInstance().getConnection();
				if(connection2 == null){
					to = msg.getTo().split("@")[0]+"@"+XmppConnectionManager.servername;
				}else{
					to = msg.getTo().split("@")[0]+"@"+XmppConnectionManager.getInstance().getConnection().getServiceName();
				}
				String time = DatetimeUtil.date2Str(Calendar.getInstance(),
						Constant.MS_FORMART);
				org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
				message.setProperty(Msg.KEY_TIME, time);
				message.setBody(msg.getMsg());
				message.setTo(to);
				
				Bundle bundle = new Bundle();
				bundle.putString("msg_id", String.valueOf(msg.getId()));
				handlermsg.setData(bundle);
				try {
					XMPPConnection connection = null;
					connection = XmppConnectionManager.getInstance().getConnection();
					if(connection == null){
						handlermsg.what = STATUS_SEND_FAIL;
					}else{
						connection.getChatManager().createChat(to, null).sendMessage(message);
						handlermsg.what = STATUS_SENDED;
					}
					
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					handlermsg.what = STATUS_SEND_FAIL;
				}
				
				mHandler.sendMessage(handlermsg);
			}
		}).start();
	}
	
	private void sendimg(final Msg msg){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub

				String result = FileTrans.getInstance(mContext).uploadFile(new File(msg.getMsg()));
				android.os.Message handlermsg = new android.os.Message();
				String to;
				XMPPConnection connection2 = null;
				connection2 = XmppConnectionManager.getInstance().getConnection();
				if(connection2 == null){
					to = msg.getTo().split("@")[0]+"@"+XmppConnectionManager.servername;
				}else{
					to = msg.getTo().split("@")[0]+"@"+XmppConnectionManager.getInstance().getConnection().getServiceName();
				}
				Bundle bundle = new Bundle();
				bundle.putString("msg_id", String.valueOf(msg.getId()));
				handlermsg.setData(bundle);
				if(result == null || result == ""){
					handlermsg.what = STATUS_SEND_FAIL;
				}else{
					String time = DatetimeUtil.date2Str(Calendar.getInstance(),
							Constant.MS_FORMART);
					JSONObject resobj = null;
					try {
						resobj = new JSONObject(result);
					
						if(resobj.has("code") && resobj.getString("code").equals("0")){
							JSONObject jj = new JSONObject(resobj.getString("data"));
							String url =jj.getString("url");
							org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
							message.setProperty(Msg.KEY_TIME, time);
							message.setProperty(Msg.KEY_TYPE, Msg.MSG_PIC);
							message.setBody(url);
							message.setTo(to);
							
							try {
								XMPPConnection connection = null;
								connection = XmppConnectionManager.getInstance().getConnection();
								
								connection.getChatManager().createChat(to, null).sendMessage(message);
								handlermsg.what = STATUS_SENDED;
								
							} catch (XMPPException e) {
								// TODO Auto-generated catch block
								handlermsg.what = STATUS_SEND_FAIL;
							}
						}else{
							handlermsg.what = STATUS_SEND_FAIL;
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						handlermsg.what = STATUS_SEND_FAIL;
					}
				}
				mHandler.sendMessage(handlermsg);
			}
		}).start();
	}

	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			String msg_id;
			switch (msg.what) {
			case STATUS_SENDING:
				msg_id = msg.getData().getString("msg_id");
				Msg sendmsg=changeStatus(msg_id, STATUS_SENDING);
				if(sendmsg!=null){
					if(sendmsg.getType() == Msg.MSG_TEXT){
						sendmsg(sendmsg);
					}else if(sendmsg.getType() == Msg.MSG_PIC){
						sendimg(sendmsg);
					}
				}
				Toast.makeText(mContext, "正在发送", Toast.LENGTH_SHORT).show();
				break;
			case STATUS_SENDED:
				msg_id = msg.getData().getString("msg_id");
				changeStatus(msg_id, STATUS_SENDED);
				Toast.makeText(mContext, "发送成功", Toast.LENGTH_SHORT).show();
				break;
			case STATUS_SEND_FAIL:
//				msg_id = msg.getData().getString("msg_id");
//				changeStatus(msg_id, STATUS_SEND_FAIL);
				msg_id = msg.getData().getString("msg_id");
				changeStatus(msg_id, STATUS_SEND_FAIL);
				Toast.makeText(mContext, "发送失败", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
		
	};
	
}


