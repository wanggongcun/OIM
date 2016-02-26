package com.time.oim;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.time.oim.util.SaveFileUtil;

public class ShuoshuoActivity extends Activity {

	private static final int SELECT_PICTURE = 1;
	private static final int SELECT_CAMER = 2;
//	private ImageButton ibt_camera;
//	private ImageButton ibt_add;
	private TextView tv_cancle;
	private TextView tv_ok;
	private EditText et_shuoshuo;
	private ImageView img_pic;
	private LinearLayout ll_picparent;
	
	private Bitmap bmp;
	private String path;
	private String shuoshuo_text;
	private boolean hasImage = false;
	private boolean hasVideo = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_shuoshuo);
		
		init();
	}
	
	private void init(){
		tv_cancle = (TextView) findViewById(R.id.tv_cancle);
		tv_cancle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				finish();
			}
		});
		tv_ok = (TextView) findViewById(R.id.tv_ok);
		tv_ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				shuoshuo_text = et_shuoshuo.getText().toString();
				Intent it = new Intent(ShuoshuoActivity.this,MainActivity.class);
				it.putExtra("sendshuoshuo", true);
				it.putExtra("hasImage", hasImage?2:0);
				it.putExtra("hasVideo", hasVideo);
				it.putExtra("path", path);
				it.putExtra("content", shuoshuo_text);
				setResult(1, it);
				finish();
			}
		});
		et_shuoshuo = (EditText) findViewById(R.id.et_shuoshuo);
		img_pic = (ImageView) findViewById(R.id.img_pic);
		ll_picparent = (LinearLayout) findViewById(R.id.ll_picparent);
		ll_picparent.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectimg();
			}
		});
	}
	
	private void selectimg() {
		final CharSequence[] items = { "拍照上传", "从相册选择" };
		new AlertDialog.Builder(this).setTitle("选择图片来源")
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == SELECT_PICTURE) {
							toGetLocalImage();
						} else {
//							toGetCameraImage();
//							Toast.makeText(ShuoshuoActivity.this, "", Toast.LENGTH_SHORT).show();
							Intent it = new Intent(ShuoshuoActivity.this,MainActivity.class);
							it.putExtra("request_str", "chat_to_one");
							startActivityForResult(it, 1);
						}
					}
				}).create().show();
	}
	
	public void toGetLocalImage() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, SELECT_PICTURE);

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case SELECT_PICTURE:
				Uri vUri = data.getData();
				// 将图片内容解析成字节数组
				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(vUri, proj, null, null, null);
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				path = cursor.getString(column_index);
				Bitmap bm = SaveFileUtil.getxtsldraw(ShuoshuoActivity.this, path);
				hasImage = true;
//				path = Until.creatfile(ShuoshuoActivity.this, bm, "usermodify");
//				if(null!=bm&&!"".equals(bm)){
//					imgList.add(bm);
//				}
				img_pic.setImageBitmap(bm);
				img_pic.setVisibility(View.VISIBLE);
				ll_picparent.setVisibility(View.INVISIBLE);
				
				break;
			case SELECT_CAMER:
				
				break;
			default:
				break;
			}

		}else if(resultCode == 1 && data.hasExtra("msg_path")){
			String path = data.getStringExtra("msg_path");
			bmp = BitmapFactory.decodeFile(path);
			img_pic.setImageBitmap(bmp);
			img_pic.setVisibility(View.VISIBLE);
			ll_picparent.setVisibility(View.INVISIBLE);
		}
	}
	
	
}
