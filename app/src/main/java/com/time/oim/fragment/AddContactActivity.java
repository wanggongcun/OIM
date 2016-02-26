package com.time.oim.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.support.v4.app.Fragment;
import android.telephony.gsm.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.time.oim.AddFriendsActivity;
import com.time.oim.R;
import com.time.oim.model.Contact;
import com.time.oim.util.ActivityUtil;
import com.time.oim.util.Constant;
import com.time.oim.view.clearEditText;

public class AddContactActivity extends Fragment {

	private List<Contact> contacts;
	private static final String[] PHONES_PROJECTION = new String[] {     
        Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
	
	private clearEditText et_search;
	private ListView lv_contacts;
	private ContactAdapter contactAdapter;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case 1:
//				ArrayList<Contact> filtercontacts = new ArrayList<Contact>();
//				filtercontacts = msg.getData().getParcelableArrayList("contacts");
				contactAdapter.notifyDataSetChanged();
				break;
			case 2:
				contactAdapter.notifyDataSetChanged();
				break;
			}
		}
	};
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		contacts = new ArrayList<Contact>();
		getPhotoContacts();
		
		lv_contacts = (ListView) getView().findViewById(R.id.lv_contacts);
		contactAdapter = new ContactAdapter();
		lv_contacts.setAdapter(contactAdapter);
		contactAdapter.notifyDataSetChanged();
		
		et_search = (clearEditText)getActivity().findViewById(R.id.et_search_contact);
		et_search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

				final String searchText = et_search.getText().toString();
				if(s=="" || s==null || s.length()<1){
//					isSearch = false;
//					addNameAdapter.notifyDataSetChanged();
//					et_search.setClearIconVisible(false);
//					et_search.setSearchIconVisible(false);
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Message msg = new Message();
							if(!(searchText == "" || searchText.trim() == "")){
								getPhotoContacts();
								msg.what=1;
							}
							handler.sendMessage(msg);
						}
					}).start();
				}else{
//					et_search.setClearIconVisible(true);
//					et_search.setSearchIconVisible(true);
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Message msg = new Message();
							if(!(searchText == "" || searchText.trim() == "")){
								filterData(searchText);
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.activity_add_contact, container, false);
	}
	
	private class ContactAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return contacts.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return contacts.get(position);
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
			final Contact contact = contacts.get(position);
			if (view == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(getActivity()).inflate(R.layout.contact_item, null);
				viewHolder.tv_contactName = (TextView) view.findViewById(R.id.tv_contactName);
				viewHolder.tv_phoneNum = (TextView) view.findViewById(R.id.tv_phoneNum);
				viewHolder.bt_contact = (TextView) view.findViewById(R.id.bt_contact);
				
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			
			viewHolder.tv_contactName.setText(contact.getContactName());
			viewHolder.tv_phoneNum.setText(contact.getPhotoNum());
			viewHolder.bt_contact.setText("邀请");
			viewHolder.bt_contact.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Uri smsToUri = Uri.parse("smsto:" + contact.getPhotoNum());
					Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
					intent.putExtra("sms_body", "我在玩一个软件叫" 
				        		+" 图知 "+"，我的名字是   " 
				        		+ ActivityUtil.getSharedPreferences(getActivity(), Constant.USERNAME) 
				        		+ " ,加我一起玩吧。 " + "下载地址： http://115.28.52.47:8080/OnefileServers/version/OIM_1_0.apk");
					startActivity(intent);
//					try{
//						SmsManager smsManager = SmsManager.getDefault();
//						smsManager.sendTextMessage(contact.getPhotoNum(), null, 
//										"我在玩一个软件叫" 
//						        		+" 图知 "+"，我的名字是   " 
//						        		+ ActivityUtil.getSharedPreferences(getActivity(), Constant.USERNAME) 
//						        		+ " ,加我一起玩吧" + " http://115.28.52.47:8080/OnefileServers/version/OIM_1_0.apk", null, null);
//						Toast.makeText(getActivity(), "邀请成功", Toast.LENGTH_SHORT).show();
//					}catch(Exception ex){
//						Toast.makeText(getActivity(), "邀请失败", Toast.LENGTH_SHORT).show();
//					}
				}
			});
			
			return view;
		}
		
	}
	
	final static class ViewHolder {
		TextView tv_contactName;
		TextView tv_phoneNum;
		TextView bt_contact;
	}
	
	private void filterData(final String filterStr){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ArrayList<Contact> filterContacts = new ArrayList<Contact>();

				Message msg = new Message();
				if(TextUtils.isEmpty(filterStr)){
//					filterContacts = (ArrayList<Contact>) contacts;
					getPhotoContacts();
				}else{
					filterContacts.clear();
					for(Contact contact : contacts){
						String name = contact.getContactName();
						if(name.indexOf(filterStr.toString()) != -1){
							filterContacts.add(contact);
						}
					}
					contacts.clear();
					contacts.addAll(filterContacts);
				}

				msg.what = 1;
				handler.sendMessage(msg);
			}
		}).start();
		
	}
	
	private void getPhotoContacts(){
		ContentResolver resolver = getActivity().getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
		if(phoneCursor != null){
			contacts.clear();
			while(phoneCursor.moveToNext()){
				String phoneNum= phoneCursor.getString(1);
				if(TextUtils.isEmpty(phoneNum)){
					continue;
				}
				String contactName = phoneCursor.getString(0);
				long contactId = phoneCursor.getLong(3);
				long photoId = phoneCursor.getLong(2);
				
				contacts.add(new Contact(phoneNum, contactName, contactId, photoId));
			}
		}
	}
}
