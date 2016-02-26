package com.time.oim;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.time.oim.db.DBManager;
import com.time.oim.fragment.AddContactActivity;
import com.time.oim.fragment.AddNameActivity;
import com.time.oim.fragment.AddRecommendActivity;
import com.time.oim.manager.ContacterManager;
import com.time.oim.manager.XmppConnectionManager;
import com.time.oim.model.Notice;
import com.time.oim.util.ActivityUtil;
import com.time.oim.util.Constant;
import com.time.oim.view.PagerSlidingTabStrip;
import com.time.oim.view.PagerSlidingTabStrip.IconTabProvider;
import com.time.oim.view.clearEditText;
import com.time.oim.view.searchEditText;

public class AddFriendsActivity extends FragmentActivity {

	private PagerSlidingTabStrip tabs;
	private ViewPager pagers;
	private ImageButton ibt_back;
	private ImageButton ibt_share;
	
	private searchEditText et_search;
	private RelativeLayout rl_viewpager;
	private ListView lv_search_result;
	
	private List<String> searchUsers;
	private SearchAdapter searchAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
	private AddNameActivity addNameActivity;
	private AddContactActivity addContactActivity;
//	private AddRecommendActivity addRecommendActivity;
	private AddPagerAdapter adapter;
	private DisplayMetrics dm;
	
	private final static int NOTIFYDATASETCHANGED = 1;
	private final static int SEND_ADD_SUCCESS = 2;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case NOTIFYDATASETCHANGED:
				searchAdapter.notifyDataSetChanged();
				break;
			case SEND_ADD_SUCCESS:
				
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_friends);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pagers = (ViewPager) findViewById(R.id.pager);
		adapter = new AddPagerAdapter(getSupportFragmentManager());
//		adapter = new AddPagerAdapter();
		searchAdapter = new SearchAdapter();
		searchUsers = new ArrayList<String>();
		
		addNameActivity = new AddNameActivity();
		addContactActivity = new AddContactActivity();
//		addRecommendActivity = new AddRecommendActivity();
		mFragments.add(addNameActivity);
		mFragments.add(addContactActivity);
//		mFragments.add(addRecommendActivity);
		pagers.setAdapter(adapter);

		tabs.setViewPager(pagers);
		init();
		setTabsValue();
		pagers.requestFocus();
		rl_viewpager.requestFocus();
	}
	
	private void init(){
		ibt_back = (ImageButton) findViewById(R.id.ibt_back);
		ibt_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.go_zoom_in, R.anim.go_zoom_out);
			}
		});
		ibt_share = (ImageButton)findViewById(R.id.ibt_share);
		ibt_share.setOnClickListener(new View.OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent shareIntent = new Intent();
		        shareIntent.setAction(Intent.ACTION_SEND);
		        shareIntent.setType("text/plain");
		        shareIntent.putExtra(Intent.EXTRA_TEXT,  "我在玩一个软件叫" 
		        		+" 图知 "+"，我的名字是   " 
		        		+ ActivityUtil.getSharedPreferences(AddFriendsActivity.this, Constant.USERNAME) 
		        		+ " ,加我一起玩吧。" + " 下载地址：http://115.28.52.47:8080/OnefileServers/version/OIM_1_0.apk");
		        
		        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		        startActivity(Intent.createChooser(shareIntent, "分享到"));
			}
		});
		et_search = (searchEditText) findViewById(R.id.et_search);
		et_search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s=="" || s==null || s.length()<1){
//					isSearch = false;
//					addNameAdapter.notifyDataSetChanged();
//					et_search.setClearIconVisible(false);
//					et_search.setSearchIconVisible(false);
					setSearch(false);
					
				}else{
//					et_search.setClearIconVisible(true);
//					et_search.setSearchIconVisible(true);
					setSearch(true);
					final String searchText = et_search.getText().toString();
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Message msg = new Message();
							if(!(searchText == "" || searchText.trim() == "")){
								searchUsers = ContacterManager.serachUser(searchText);
								msg.what=1;
							}
							handler.sendMessage(msg);
						}
					}).start();
					
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
		
		rl_viewpager = (RelativeLayout)findViewById(R.id.rl_viewpager);
		pagers.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				rl_viewpager.setFocusable(true);
				rl_viewpager.requestFocus();
				return false;
			}
		});
		pagers.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub

//				Toast.makeText(AddFriendsActivity.this, String.valueOf(arg0), Toast.LENGTH_SHORT).show();
				if(arg0 == 1){
					adapter.setSelectAccount(false);
				}else{
					adapter.setSelectAccount(true);
				}
				tabs.notifyDataSetChanged();
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		lv_search_result = (ListView)findViewById(R.id.lv_search_result);
		lv_search_result.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				lv_search_result.setFocusable(true);
				lv_search_result.requestFocus();
				return false;
			}
		});
		lv_search_result.setAdapter(searchAdapter);
		searchAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.go_zoom_in, R.anim.go_zoom_out);
	}

	private class SearchAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return searchUsers.size();
			
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return searchUsers.get(position);
			
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
				view = LayoutInflater.from(AddFriendsActivity.this).inflate(R.layout.add_name_item, null);
				viewHolder.tv_add_name = (TextView) view.findViewById(R.id.tv_add_name);
				viewHolder.tv_add_id = (TextView) view.findViewById(R.id.tv_add_id);
				viewHolder.bt_add_accept = (TextView) view.findViewById(R.id.bt_add_accept);
				viewHolder.bt_refuse = (TextView) view.findViewById(R.id.bt_refuse);
				view.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) view.getTag();
			}
			String searchName = searchUsers.get(pos);
			
				if(searchName.split(" ").length>1){
					viewHolder.tv_add_name.setText(searchName.split(" ")[0]);
					viewHolder.tv_add_id.setText("快聊: "+searchName.split(" ")[1]);
				}else{
					viewHolder.tv_add_name.setText(searchName);
					viewHolder.tv_add_id.setText("快聊: "+searchName);
				}
			if(!ContacterManager.hasContacter(searchName.split(" ")[0])){
				viewHolder.bt_add_accept.setText("添加");
				viewHolder.bt_add_accept.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						try {
							if(searchUsers.get(pos).split(" ").length>1){
								ContacterManager.addUser(searchUsers.get(pos).split(" ")[0], searchUsers.get(pos).split(" ")[1], new String[] { "friends" });
							}else{
								ContacterManager.addUser(searchUsers.get(pos), searchUsers.get(pos), new String[] { "friends" });
							}
							
//							v.setVisibility(View.GONE);
							Toast.makeText(AddFriendsActivity.this, "请求已发送", Toast.LENGTH_SHORT).show();
							searchAdapter.notifyDataSetChanged();
						} catch (Exception e) {
							// TODO Auto-generated catch block
	//						e.printStackTrace();
							Toast.makeText(AddFriendsActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}else{
				viewHolder.bt_add_accept.setVisibility(View.GONE);
			}
			viewHolder.bt_refuse.setVisibility(View.GONE);
				
			
			return view;
		}
	}
	
	final class ViewHolder{
		TextView tv_add_name;
		TextView tv_add_id;
		TextView bt_add_accept;
		TextView bt_refuse;
	}
	
	private void setSearch(boolean search){
		if(search){
			rl_viewpager.setVisibility(View.GONE);
			lv_search_result.setVisibility(View.VISIBLE);
		}else{
			rl_viewpager.setVisibility(View.VISIBLE);
			lv_search_result.setVisibility(View.GONE);
			rl_viewpager.requestFocus();
		}
		
	}
	
	private void setTabsValue() {
		// 设置Tab是自动填充满屏幕的
		tabs.setShouldExpand(true);
		// 设置Tab的分割线是透明的
		tabs.setDividerColor(Color.rgb(217, 217, 217));
//		// 设置Tab底部线的高度
//		tabs.setUnderlineHeight((int) TypedValue.applyDimension(
//				TypedValue.COMPLEX_UNIT_DIP, 1, dm));
//		// 设置Tab Indicator的高度
//		tabs.setIndicatorHeight((int) TypedValue.applyDimension(
//				TypedValue.COMPLEX_UNIT_DIP, 4, dm));
//		// 设置Tab标题文字的大小
//		tabs.setTextSize((int) TypedValue.applyDimension(
//				TypedValue.COMPLEX_UNIT_SP, 16, dm));
//		// 设置Tab Indicator的颜色
//		tabs.setIndicatorColor(Color.parseColor("#45c01a"));
//		// 设置选中Tab文字的颜色 (这是我自定义的一个方法)
//		tabs.setSelectedTextColor(Color.parseColor("#45c01a"));
		// 取消点击Tab时的背景色
		tabs.setTabBackground(0);
		
		
	}
	
	public class AddPagerAdapter extends FragmentPagerAdapter implements IconTabProvider {

		public AddPagerAdapter(FragmentManager fm) {
			super(fm);
		}
//		public AddPagerAdapter() {
//			super();
//		}
		private final int[] ICONS = { R.drawable.ic_account_circle_black_24dp, R.drawable.ic_book_black_24dp1,
				R.drawable.ic_account_circle_black_24dp1, R.drawable.ic_book_black_24dp };
		private final String[] titles = { "新的好友", "通讯录" };
		private boolean selectaccount = true;
		public void setSelectAccount(boolean yes){
			selectaccount = yes;
			adapter.notifyDataSetChanged();
		}
		
		
		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Fragment getItem(int position) {

			return mFragments.get(position);
		}

		@Override
		public int getPageIconResId(int position) {
			// TODO Auto-generated method stub
			if(selectaccount){
				return ICONS[position];
			}else{
				return ICONS[position+2];
			}
		}

		@Override
		public String getPageIconTextResId(int position) {
			// TODO Auto-generated method stub
			return titles[position];
		}

		
	}
}
