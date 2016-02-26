package com.time.oim.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import com.time.oim.util.SaveFileUtil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class FileTrans {
	private Context mContext;
	private String action_uploadimgurl = "http://115.28.52.47/OnefileServers/OneServers";
	private String action_downloadimgurl = "";
	private String filePath;
	private String id="a";
	
	private static FileTrans filaTrans;
	
	
	public FileTrans(Context context){
		this.mContext = context;
	}
	
	public static FileTrans getInstance(Context context){
		if(filaTrans == null){
			filaTrans = new FileTrans(context);
		}
		return filaTrans;
	}
	
	public void setPro(ProgressDialog p){
//		this.pd = p;
	}
	
	public void setUploadUrl(String url){
		this.action_uploadimgurl = url;
	}
	
	public String getUploadUrl(){
		return action_uploadimgurl;
	}
	
	public void setDownloadUrl(String url){
		this.action_downloadimgurl = url;
	}
	
	public String getDownloadUrl(){
		return action_downloadimgurl;
	}
	
	public void setFilePath(String path){
		this.filePath = path;
	}
	
	public String getFilePath(){
		return filePath;
	}
	
	public String upload(String path){
		FileUploadTask task = new FileUploadTask();
		task.execute(path);
		
		return id;
	}
	
	public boolean download(String url){
		FileDownloadTask task = new FileDownloadTask();
		task.execute(url);
		return false;
	}
	
	private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10*1000;   //超时时间
    private static final String CHARSET = "utf-8"; //设置编码
    public static final String SUCCESS = "1";  
    public static final String FAILURE = "0";
	public String uploadFile(File file){
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成  
        String PREFIX = "--", LINE_END = "\r\n";  
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型   
        try {  
            URL url = new URL(action_uploadimgurl);  
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
            conn.setReadTimeout(TIME_OUT);  
            conn.setConnectTimeout(TIME_OUT);  
            conn.setDoInput(true); // 允许输入流  
            conn.setDoOutput(true); // 允许输出流  
            conn.setUseCaches(false); // 不允许使用缓存  
            conn.setRequestMethod("POST"); // 请求方式  
            conn.setRequestProperty("Charset", CHARSET); // 设置编码  
            conn.setRequestProperty("connection", "keep-alive");  
            conn.setRequestProperty("cookies", DataTrans.getInstance(mContext).getCookies());
//            conn.setRequestProperty("Cookie", DataTrans.getInstance(mContext).getCookies());
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="  
                    + BOUNDARY); 
            conn.connect();
            if (file != null) {  
                /** 
                 * 当文件不为空，把文件包装并且上传 
                 */  
                OutputStream outputSteam = conn.getOutputStream();  
  
                DataOutputStream dos = new DataOutputStream(outputSteam);  
                StringBuffer sb = new StringBuffer();  
                sb.append(PREFIX);  
                sb.append(BOUNDARY);  
                sb.append(LINE_END);  
                /** 
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件 
                 * filename是文件的名字，包含后缀名的 比如:abc.png 
                 */  
                String fulegetname = file.getName();
                sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""  
                        + file.getName() + "\"" + LINE_END);  
                sb.append("Content-Type: application/octet-stream; charset="  
                        + CHARSET + LINE_END);  
                sb.append(LINE_END);  
                dos.write(sb.toString().getBytes());  
                InputStream is = new FileInputStream(file);  
                byte[] bytes = new byte[1024];  
                int len = 0;  
                while ((len = is.read(bytes)) != -1) {  
                    dos.write(bytes, 0, len);  
                }  
                is.close();  
                dos.write(LINE_END.getBytes());  
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)  
                        .getBytes();  
                dos.write(end_data);  
                dos.flush();  
                /** 
                 * 获取响应码 200=成功 当响应成功，获取响应的流 
                 */  
                int res = conn.getResponseCode();  
                if (res == 200) {  
//                	showDialog(conn.getResponseMessage());
                	BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb2 = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb2.append(line);
                    }
                    br.close();
                    return sb2.toString();  
                }  
            }  
        } catch (MalformedURLException e) {  
//            e.printStackTrace();  
        	showDialog(e.getMessage());
        } catch (IOException e) {  
//            e.printStackTrace(); 
        	showDialog(e.getMessage());
        }   
		return null;
	}
	
	public String downloadFile(String url){
		URL imgURL = null;
		try {
			imgURL = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imgURL.openConnection();
			conn.setDoInput(true);
//			conn.setRequestProperty("cookies", DataTrans.getInstance(mContext).getCookies());
			conn.connect();
			InputStream input = conn.getInputStream();
			int length = conn.getContentLength();
			if(length != -1){
				byte[] imgData = new byte[length];
				byte[] buffer = new byte[1024];
				int readlen = 0;
				int destpos = 0;
				while((readlen = input.read(buffer)) > 0){
					System.arraycopy(buffer, 0, imgData, destpos, readlen);
					destpos += readlen;
				}
				Bitmap bm = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);

				return SaveFileUtil.savetempbm(bm);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String downloadVersionFile(String url){
		URL versionURL = null;
		try {
			versionURL = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) versionURL.openConnection();
			conn.setDoInput(true);
//			conn.setRequestProperty("cookies", DataTrans.getInstance(mContext).getCookies());
			conn.connect();
			InputStream input = conn.getInputStream();
			File fileFolder = new File(Environment.getExternalStorageDirectory()  
	                + "/aone/version/");  
			String versionFileName = url.substring(url.lastIndexOf("/")+1, url.length());
	        if (!fileFolder.exists()) { // 如果目录不存在，则创建一个 
	            fileFolder.mkdir();  
	        } 
	        File file = new File(fileFolder,versionFileName);
			int length = conn.getContentLength();
			OutputStream os = new FileOutputStream(file);
			if(length != -1){
//				byte[] imgData = new byte[length];
//				byte[] buffer = new byte[1024];
//				int readlen = 0;
//				int destpos = 0;
//				while((readlen = input.read(buffer)) > 0){
//					System.arraycopy(buffer, 0, imgData, destpos, readlen);
//					destpos += readlen;
//				}
//				Bitmap bm = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
//
//				return SaveFileUtil.savebm(bm);
				byte[] Data = new byte[length];
				byte[] buffer = new byte[1024];
				int readlen = 0;
				int destpos = 0;
				while((readlen = input.read(buffer)) > 0){
					os.write(buffer, 0, readlen);
//					System.arraycopy(buffer, 0, imgData, destpos, readlen);
//					destpos += readlen;
				}
			}
			return file.getPath();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private void showDialog(String mess) {
		new AlertDialog.Builder(mContext).setTitle("Message")
				.setMessage(mess)
				.setNegativeButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			
			}
		}).show();
	}
	
//	private ProgressDialog pd = null;
	private class FileUploadTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			
			String filePath = params[0];
			String issuccess = uploadFile(new File(filePath));
			
			return issuccess;
			
		}

		@Override
		protected void onPreExecute() {
//			pd = new ProgressDialog(mContext);
//			pd.setMessage("正在提交,请稍候...");
//			pd.show();
		}

		@Override
		protected void onPostExecute(String result) {
//			pd.dismiss();
			Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
		}

	}
	
	private class FileDownloadTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String requesturl = params[0];
//			downloadFile(requesturl,filePath);
			String issuccess = downloadFile(requesturl);

			return issuccess;
			
		}

		@Override
		protected void onPreExecute() {
//			pd = new ProgressDialog(mContext);
//			pd.setMessage("正在下载,请稍候...");
//			pd.show();
		}

		@Override
		protected void onPostExecute(String result) {
//			pd.dismiss();
			Toast.makeText(mContext, "download success" + result, Toast.LENGTH_LONG).show();
		}

	}
	
}
