package com.time.oim.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;
import com.time.oim.db.DBManager;
import com.time.oim.manager.ShuoshuoManager;
import com.time.oim.model.Comment;
import com.time.oim.model.Shuoshuo;
import com.time.oim.util.ActivityUtil;
import com.time.oim.util.Constant;
import com.time.oim.util.RSAUtils;
import com.time.oim.util.SecretUtil;

public class DataTrans {

	private Context mContext;
//	private String action_url = "http://www.blcnjk.1866.co:8888/";
	private String action_url = "http://115.28.52.47/";
	private static DataTrans dataTrans;
	
	private String putMessageURL = action_url + "putMessage";
	private String userLoginURL = action_url + "userLogin";
	private String userRegisterURL = action_url + "userRegister";
	private String deleteMessageURL = action_url + "deleteMessage";
	private String getMessageListURL = action_url + "getMessageList";
	private String upLoadImageURL = action_url + "OnefileServers/OneServers";
	private String upLoadVideoURL = action_url + "uploadVideo";
	private String commentMessageURL = action_url + "commentMessage";
	private String deleteCommentURL = action_url + "deleteComment";
	private String refreshContacterURL = action_url + "refreshContacter";
	private String getVersionURL = "http://115.28.52.47/OnefileServers/GetVersion";
	
	
	public DataTrans(Context context){
		this.mContext = context;
	}
	
	public static DataTrans getInstance(Context context){
		if(dataTrans == null){
			dataTrans = new DataTrans(context);
		}
		return dataTrans;
	}
	
	public boolean userRegister(String username,String password){
		
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost mpost = new HttpPost(userRegisterURL);
		mpost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("userName", username));
		pairs.add(new BasicNameValuePair("password", password));
		try {
			mpost.setEntity(new UrlEncodedFormEntity(pairs,HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpResponse response = null;
		try {
			response = client.execute(mpost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(response == null)
			return false;
		if(response.getStatusLine().getStatusCode() == 200){
			HttpEntity entity = response.getEntity();
			if(entity != null){
				try {
					String strresponse = EntityUtils.toString(entity);
					JSONObject resobj = new JSONObject(strresponse);
					if(resobj.getString("code").equals("0")){
						if(resobj.has("cookies")){
							String cookies = resobj.getString("cookies");
							saveCookies(cookies);
						}
						return true;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					return false;
				} catch (IOException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					return false;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					return false;
				}
				
			}
		}
		
		return false;
	}
	
	public boolean userLogin(String userName,String password){
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost mpost = new HttpPost(userLoginURL);
		mpost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
//		mpost.setHeader("cookies", getCookies());
		List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("userName", userName));
		pairs.add(new BasicNameValuePair("password", password));
		try {
			mpost.setEntity(new UrlEncodedFormEntity(pairs,HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpResponse response = null;
		try {
			response = client.execute(mpost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(response.getStatusLine().getStatusCode() == 200){
			HttpEntity entity = response.getEntity();
			if(entity != null){
				try {
					String strresponse = EntityUtils.toString(entity);
					JSONObject resobj = new JSONObject(strresponse);
					if(resobj.getString("code").equals("0")){
						JSONArray resdata = new JSONArray(resobj.getString("data"));
						for(int i=0;i<resdata.length();i++){
							JSONObject jo = new JSONObject(resdata.get(i).toString());
							if(jo.has("userName") && jo.has("ts") && jo.getString("userName")!="")
								DBManager.getInstance(mContext).updateLocalContacter(userName, jo.getString("userName"), jo.getString("ts"));
						}
						if(resobj.has("cookies")){
							String cookies = resobj.getString("cookies");
							saveCookies(cookies);
						}
						return true;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					return false;
				} catch (IOException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					return false;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					return false;
				}
				
			}
		}
		
		return false;
	}
	
	public void refreshContacter(String userName,String timeS){
//		DefaultHttpClient client = new DefaultHttpClient();
//		HttpPost mpost = new HttpPost(refreshContacterURL);
//		mpost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
//		mpost.setHeader("cookies", getCookies());
//		List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
//		pairs.add(new BasicNameValuePair("id", userName));
//		pairs.add(new BasicNameValuePair("ts", timeS));
//		
//		try {
//			mpost.setEntity(new UrlEncodedFormEntity(pairs,HTTP.UTF_8));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		HttpResponse response = null;
//		try {
//			response = client.execute(mpost);
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		if(response == null){
//			return;
//		}
//		if(response.getStatusLine().getStatusCode() == 200){
//			HttpEntity entity = response.getEntity();
//			if(entity != null){
//				try {
//					String strresponse = EntityUtils.toString(entity);
//					JSONObject resobj = new JSONObject(strresponse);
//					
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
////					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
////					e.printStackTrace();
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
////					e.printStackTrace();
//				}
//				
//			}
//		}
		
	}
	
	public String getVersion(){
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost mpost = new HttpPost(getVersionURL);
		mpost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		mpost.setHeader("cookies", getCookies());
//		List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
//		
//		pairs.add(new BasicNameValuePair("", ""));
//		try {
//			mpost.setEntity(new UrlEncodedFormEntity(pairs,HTTP.UTF_8));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		HttpResponse response = null;
		try {
			response = client.execute(mpost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return null;
		}
		
		if(response.getStatusLine().getStatusCode() == 200){
			HttpEntity entity = response.getEntity();
			if(entity != null){
				try {
					String strresponse = EntityUtils.toString(entity);
					JSONObject resobj = new JSONObject(strresponse);
					if(resobj.getString("code").equals("0")){
						return resobj.getString("data");
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					return null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					return null;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					return null;
				}
				
			}
		}
		
		return null;
		
	}
	
 	public ImageObj uploadImage(String path){
		ImageObj io = new ImageObj();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost mpost = new HttpPost(upLoadImageURL);
//		mpost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		mpost.setHeader("Content-Type", "application/multipart/form-data; charset=utf-8");
		mpost.setHeader("cookies", getCookies());
		MultipartEntity mulEntity = new MultipartEntity();
		File file = new File(path);
		mulEntity.addPart("content", new FileBody(file));
		
		mpost.setEntity(mulEntity);	
		
		HttpResponse response = null;
		try {
			response = client.execute(mpost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(response.getStatusLine().getStatusCode() == 200){
			HttpEntity entity = response.getEntity();
			if(entity != null){
				try {
					String strresponse = EntityUtils.toString(entity);
					JSONObject resobj = new JSONObject(strresponse);
					if(resobj.getString("code").equals("0")){
						JSONObject resdata = new JSONObject(resobj.getString("data"));
						io.id = resdata.getString("id");
						io.uid = resdata.getString("uid");
						io.url = resdata.getString("url");
					}else{
						io = null;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					io = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					io = null;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					io = null;
				}
				
			}else{
				io = null;
			}
		}else{
			io = null;
		}
		return io;
	}
	
	public String sendShuoshuo(Shuoshuo shuoshuo){
		ImageObj img = null;
		if(shuoshuo.hasImage() == 1){
			img = uploadImage(shuoshuo.getImageURL());
			if(img == null){
				return null;
			}
		}
		
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost mpost = new HttpPost(putMessageURL);
		mpost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		mpost.setHeader("cookies", getCookies());
		List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("content", shuoshuo.getContent()));
		pairs.add(new BasicNameValuePair("ImageID", img.id));
		
		try {
			mpost.setEntity(new UrlEncodedFormEntity(pairs,HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpResponse response = null;
		try {
			response = client.execute(mpost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(response.getStatusLine().getStatusCode() == 200){
			HttpEntity entity = response.getEntity();
			if(entity != null){
				try {
					String strresponse = EntityUtils.toString(entity);
					JSONObject resobj = new JSONObject(strresponse);
					if(resobj.getString("code").equals("0")){
						JSONObject resdata = new JSONObject(resobj.getString("data"));
						Shuoshuo s = new Shuoshuo(shuoshuo);
						s.setShuoshuoID(resdata.getString("id"));
						s.setTime(resdata.getString("timeStamps"));
						ShuoshuoManager.getInstance(mContext).addShuoshuo(s);
						
						return resdata.getString("id");
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		return null;
	}
	
	public String sendComment(Comment comment){
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost mpost = new HttpPost(commentMessageURL);
		mpost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		mpost.setHeader("cookies", getCookies());
		JSONObject reqobj = new JSONObject();
		JSONObject reqdata = new JSONObject();
		try {
			
			reqdata.put("content", comment.getContent());
			reqdata.put("shuoshuoid", comment.getShuoshuoID());
			
			reqobj.put("data", reqdata);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("post", reqobj.toString()));
		try {
			mpost.setEntity(new UrlEncodedFormEntity(pairs,HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpResponse response = null;
		try {
			response = client.execute(mpost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(response.getStatusLine().getStatusCode() == 200){
			HttpEntity entity = response.getEntity();
			if(entity != null){
				try {
					String strresponse = EntityUtils.toString(entity);
					JSONObject resobj = new JSONObject(strresponse);
					if(resobj.getString("code").equals("0")){
						JSONObject resdata = new JSONObject(resobj.getString("data"));
						Shuoshuo s = new Shuoshuo(ShuoshuoManager.getInstance(mContext).getShuoshuo(comment.getShuoshuoID()));
						ShuoshuoManager.getInstance(mContext).deleteShuoshuo(comment.getShuoshuoID());
						ShuoshuoManager.getInstance(mContext).addShuoshuo(s);
						return resdata.getString("commentid");
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		return null;
	}
	
	public boolean deleteShuoshuo(String id){
		
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost mpost = new HttpPost(deleteMessageURL);
		mpost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		
		JSONObject reqobj = new JSONObject();
		JSONObject reqdata = new JSONObject();
		try {
			reqdata.put("messageID", id);
			reqobj.put("data", reqdata);
			reqobj.put("cookies", getCookies());
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("post", reqobj.toString()));
		try {
			mpost.setEntity(new UrlEncodedFormEntity(pairs,HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpResponse response = null;
		try {
			response = client.execute(mpost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(response.getStatusLine().getStatusCode() == 200){
			HttpEntity entity = response.getEntity();
			if(entity != null){
				try {
					String strresponse = EntityUtils.toString(entity);
					JSONObject resobj = new JSONObject(strresponse);
					if(resobj.getString("code").equals("0")){
//						JSONObject resdata = new JSONObject(resobj.getString("data"));
						return true;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		return false;
	}
	
	public List<Shuoshuo> getShuoshuos(){
		
		List<Shuoshuo> shuoshuos = new ArrayList<Shuoshuo>();
		
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost mpost = new HttpPost(getMessageListURL);
		mpost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		
		JSONObject reqobj = new JSONObject();
		JSONObject reqdata = new JSONObject();
		try {
			reqdata.put("type", "all");
//			reqdata.put("begin", "all");
//			reqdata.put("end", "all");
			
			reqobj.put("cookies", getCookies());
			reqobj.put("data", reqdata);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("post", reqobj.toString()));
		try {
			mpost.setEntity(new UrlEncodedFormEntity(pairs,HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpResponse response = null;
		try {
			response = client.execute(mpost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(response.getStatusLine().getStatusCode() == 200){
			HttpEntity entity = response.getEntity();
			if(entity != null){
				try {
					String strresponse = EntityUtils.toString(entity);
					JSONObject resobj = new JSONObject(strresponse);
					if(resobj.getString("code").equals("0")){
						JSONArray ja = new JSONArray(resobj.getString("data"));
						for(int i=0;i<ja.length();i++){
							Shuoshuo shuoshuo = new Shuoshuo();
							Gson gson = new Gson();
							shuoshuo = gson.fromJson(ja.get(i).toString(), Shuoshuo.class);
							shuoshuos.add(shuoshuo);
						}
						ShuoshuoManager.getInstance(mContext).refreshShuoshuos();
						ShuoshuoManager.getInstance(mContext).addShuoshuos(shuoshuos);
						return shuoshuos;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		return null;
	}
	
	private String userObj(String id,String userName,String timeStamps){
		JSONObject obj = new JSONObject();
		try {
			obj.put("id", id);
			obj.put("userName", userName);
			obj.put("timeStamps", timeStamps);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj.toString();
		
	}
	
	private String ImageObj(String id,String uid,String timeStamps,String name,String url){
		JSONObject obj = new JSONObject();
		try {
			obj.put("id", id);
			obj.put("uid", uid);
			obj.put("timeStamps", timeStamps);
			obj.put("name", name);
			obj.put("url", url);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj.toString();
		
	}
	
	public class ImageObj{
		String id;
		String uid;
		String url;
	}
	
	private String VideoObj(String id,String uid,String timeStamps,String name,String url){
		JSONObject obj = new JSONObject();
		try {
			obj.put("id", id);
			obj.put("uid", uid);
			obj.put("timeStamps", timeStamps);
			obj.put("name", name);
			obj.put("url", url);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj.toString();
		
	}
	
	private String MessageObj(String id,String imageObj,String videoObj,
			String userObj,String content,String timeStamps){
		JSONObject obj = new JSONObject();
		try {
			obj.put("id", id);
			obj.put("imageObj", imageObj);
			obj.put("videoObj", videoObj);
			obj.put("userObj", userObj);
			obj.put("content", content);
			obj.put("timeStamps", timeStamps);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj.toString();
		
	}
	
	@SuppressWarnings("resource")
	private void saveCookies(String cookies){
		File fileHolder = new File(Environment.getExternalStorageDirectory() + "/aone/cookies/");
		if(!fileHolder.exists()){
			fileHolder.mkdirs();
		}
		File cookiesFile = new File(fileHolder, "cookies.txt");
		
		try {
			FileOutputStream out = new FileOutputStream(cookiesFile, false);
			out.write(cookies.getBytes("UTF-8"));
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public String getCookies(){
		JSONObject jodata = new JSONObject();
		
		try {
			jodata.put("userName", ActivityUtil.getSharedPreferences(mContext, Constant.USERNAME));
			jodata.put("token", ActivityUtil.getSharedPreferences(mContext, Constant.PASSWORD));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return null;
		}
//		String cookies = "userName=wgc;token=123456";
		String da = SecretUtil.RSAencrypt(jodata.toString());
		return SecretUtil.RSAencrypt(jodata.toString());
		
//		File fileHolder = new File(Environment.getExternalStorageDirectory() + "/aone/cookies/","cookies.txt");
//		if(!fileHolder.exists()){
//			
//			return null;
//		}
//		
//		try {
//			FileInputStream in = new FileInputStream(fileHolder);
//			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//			byte[] buffer = new byte[1024];  
//	        int len = 0;  
//	        // 将内容读到buffer中，读到末尾为-1  
//	        while ((len = in.read(buffer)) != -1)  
//	        {  
//	            // 本例子将每次读到字节数组(buffer变量)内容写到内存缓冲区中，起到保存每次内容的作用  
//	            outStream.write(buffer, 0, len);  
//	        } 
//	        byte[] data = outStream.toByteArray();  
//	        in.close(); 
//	        outStream.close();
//	        String result = new String(data); 
//	        return result;
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
	}
	
}
