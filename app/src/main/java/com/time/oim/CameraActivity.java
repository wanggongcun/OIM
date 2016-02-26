package com.time.oim;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.time.oim.db.DBManager;
import com.time.oim.manager.XmppApplication;
import com.time.oim.model.Poi;
import com.time.oim.service.IMChatService;
import com.time.oim.util.ActivityUtil;
import com.time.oim.util.CameraUtil;
import com.time.oim.util.Constant;
import com.time.oim.util.DatetimeUtil;
import com.time.oim.util.SaveFileUtil;
import com.time.oim.view.ColorPickerView;
import com.time.oim.view.MyImageView;
import com.time.oim.view.MyTextView;

public class CameraActivity extends Fragment {
	
	private Camera camera = null;
	private Parameters parameters = null;
	private MediaRecorder mediarecorder = null;
	public String path = "";
	private String tempath = null;
	private boolean isvideo = false;
	public boolean canDraw = false;
	private int orientation = 90;
	private SensorManager sm;
	private Sensor sensor;
	private SensorListener listener = new SensorListener();
	private Paint mPaint;
	private Bitmap mBitmap;
	private Bitmap savebm;
	
	private float oldX = 0.0f;
	private float oldY = 0.0f;
	private float tx = 0.0f;
	private float ty = 0.0f;
	
	private int mStartTime = 0;
	private int mEndTime = 0;
	
	private Display display;
	private Rect realSize;
	private RelativeLayout rl_top = null;
	private FrameLayout frameLayout = null;
	private SurfaceView surfaceView = null;
	private MyImageView dragImageView = null;
	private MyTextView tv_text = null;
	private FrameLayout textLayout = null;
	private EditText et_text = null;
	private FrameLayout fl_text = null;
	private View colorLayout = null;
	private View colorLayout2 = null;
	private ColorPickerView colorPickerView = null;
	private View cameraLayout = null;
	private ImageButton ibt_takephoto = null;
	private ImageButton bt_frontsnap = null;
	private ImageButton bt_flash = null;
	private ImageButton bt_setting = null;
	private ImageButton bt_friendlist = null;
	private ImageButton bt_friendstory = null;
	private ViewStub ok_viewStub = null;
	private View okLayout = null;
	private ImageButton bt_ok = null;
	private ImageButton bt_cancle = null;
	private ImageView bt_draw = null;
	private ImageButton bt_doback = null;
	private ImageButton bt_oktext = null;
	private ImageButton bt_save = null;
	private ImageButton bt_share = null;
	private int cameraPosition = 0;
	private int img_position = 0;
	public static boolean ischat = false;
	private String myname;
	private List<String> names;
	int state_height;
	
	private ProgressDialog pd = null;
	
	private final static int HANDLER_INIT_VIEW = 0;
	private final static int REQUEST_TO_SETTING = 10;
	private final static int REQUEST_TO_SEND_PIC = 20;
	private final static int REQUEST_LOCAL_PIC = 100;
	private final static int RESULT_SEND_PIC_SUCCESS = 21;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		WindowManager windowManager = getActivity().getWindowManager();
        display = windowManager.getDefaultDisplay();
        realSize = new Rect();
        display.getRectSize(realSize);
		camera = CameraUtil.getCameraInstance();
		Rect frame = new Rect();
		getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		state_height = frame.top;
		init();
		rl_top = (RelativeLayout) getView().findViewById(R.id.toplayout);
		
		frameLayout = (FrameLayout) getView().findViewById(R.id.frameLayout);
		surfaceView = (SurfaceView) getView().findViewById(R.id.surfaceView);
		surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceView.getHolder().setKeepScreenOn(true);// 屏幕常亮
		
		surfaceView.getHolder().addCallback(new CameraPreview());// 为SurfaceView的句柄添加一个回调函数
		surfaceView.setFocusable(true);
		
		sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sm.registerListener(listener, sensor,SensorManager.SENSOR_DELAY_NORMAL);
		
        mPaint = new Paint();
		mPaint.setColor(Color.BLUE);
		mPaint.setStyle(Style.STROKE); 
        mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(5);
		myname = ActivityUtil.getSharedPreferences(getActivity(), Constant.USERNAME);
		String request_str = getActivity().getIntent().getStringExtra("request_str");
		if(request_str != null)
			if(request_str.equals("chat_to_one")){
				ischat = true;
			}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.activity_camera, container, false);
	}
	
	private void init(){
		
		new Timer().schedule(new TimerTask() {  
			  
            @Override  
            public void run() {  
            	Message msg = new Message();
            	msg.what = HANDLER_INIT_VIEW;
                handler.sendMessage(msg);  
            }  
        }, 1000);// 延迟1秒,然后加载 
		cameraLayout = getView().findViewById(R.id.cameralayout);
		//照相
		ibt_takephoto = (ImageButton) getView().findViewById(R.id.ibt_takephoto);
		ibt_takephoto.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					mStartTime = (int)SystemClock.currentThreadTimeMillis();
					break;
				case MotionEvent.ACTION_MOVE:
					
					break;
				case MotionEvent.ACTION_UP:
	                cameraLayout.setVisibility(ViewGroup.GONE);
					if(isvideo){
						
	            	}else{
//	            		initCamera();
	            		surfaceView.destroyDrawingCache();
	                    camera.takePicture(null, null, new MyPictureCallback());
	                    try{
	                    	ok_viewStub.inflate();
	                    	okinit();
	                    }catch(Exception ex){
	                    	
	                    }
	            	}
					mStartTime = mEndTime;
					break;
				}
				mEndTime = (int)SystemClock.currentThreadTimeMillis();
				
				return false;
			}
		});
		//设置页
		bt_setting = (ImageButton)getView().findViewById(R.id.bt_setting);
		bt_setting.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(getActivity(), AccountActivity.class);
				startActivityForResult(it, REQUEST_TO_SETTING);
				getActivity().overridePendingTransition(R.anim.go_in_top, R.anim.go_out_bottom);
			}
		});
		//闪光灯
		bt_flash = (ImageButton) getView().findViewById(R.id.bt_flash);
		bt_flash.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				parameters = null;
				parameters = camera.getParameters();
				String flashmode = parameters.getFlashMode();
				if(flashmode.equals(Parameters.FLASH_MODE_ON)){
					parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
					bt_flash.setBackgroundResource(R.drawable.ic_flash_off_black_48dp);
				}else if(flashmode.equals(Parameters.FLASH_MODE_OFF)){
					parameters.setFlashMode(Parameters.FLASH_MODE_ON);
					bt_flash.setBackgroundResource(R.drawable.ic_flash_on_black_48dp);
				}else{
					parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
					bt_flash.setBackgroundResource(R.drawable.ic_flash_off_black_48dp);
				}
				camera.setParameters(parameters);
				camera.startPreview();
			}
		});
		//前后切换
		bt_frontsnap = (ImageButton) getView().findViewById(R.id.bt_frontsnap);
		if(Camera.getNumberOfCameras()<2){
			bt_frontsnap.setVisibility(View.GONE);
		}else{
			bt_frontsnap.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(camera == null)
						return;
					if(cameraPosition == 0){
						cameraPosition++;
					}else{
						cameraPosition--;
					}
					
					try {
						releaseCamera();
					
						camera = Camera.open(cameraPosition);
//						initCamera();
						camera.setPreviewDisplay(surfaceView.getHolder());
						initCamera();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		bt_friendstory = (ImageButton) getView().findViewById(R.id.bt_friendstory);
		bt_friendstory.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, REQUEST_LOCAL_PIC);
			}
		});
 		
		ok_viewStub = (ViewStub) getView().findViewById(R.id.ok_stub);
		colorLayout = getView().findViewById(R.id.colorLayout);
		colorLayout2 = getView().findViewById(R.id.colorLayout2);
		colorPickerView = (ColorPickerView) getView().findViewById(R.id.colorPickerView);
		colorLayout2.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				getView().getParent().requestDisallowInterceptTouchEvent(true);
				int colors[] = new int[8];   
				colors[0] = 0xFF000000;  
		        colors[1] = 0xFFFF0000;  
		        colors[2] = 0xFFFFFF00; 
		        colors[3] = 0xFF00FF00; 
		        colors[4] = 0xFF00FFFF;  
		        colors[5] = 0xFF0000FF;  
		        colors[6] = 0xFFFF00FF;  
		        colors[7] = 0xFFFFFFFF;
		        
				float y = event.getY()/colorPickerView.getHeight();
				if(y<0.14){
					mPaint.setColor(Color.rgb((int) (255*y/0.14), 0, 0));
				}else if(y<0.28){
					mPaint.setColor(Color.rgb(255,(int) (255*(y-0.14)/0.14), 0));
				}else if(y<0.42){
					mPaint.setColor(Color.rgb(255-(int) (255*(y-0.28)/0.14),255, 0));
				}else if(y<0.56){
					mPaint.setColor(Color.rgb(0,255, (int) (255*(y-0.42)/0.14)));
				}else if(y<0.7){
					mPaint.setColor(Color.rgb(0,255-(int) (255*(y-0.56)/0.14),255));
				}else if(y<0.84){
					mPaint.setColor(Color.rgb((int) (255*(y-0.7)/0.14),0,255));
				}else if(y<0.98){
					mPaint.setColor(Color.rgb(255,(int) (255*(y-0.84)/0.14),255));
				}else{
					mPaint.setColor(Color.rgb(255,255, 255));
				}
				dragImageView.setPaint(mPaint);
				bt_draw.setBackgroundColor(mPaint.getColor());
				
				tv_text.setTextColor(mPaint.getColor());
				et_text.setTextColor(mPaint.getColor());
				return true;
			}
		});
	}
	
	private void okinit(){
		
		
		okLayout = getView().findViewById(R.id.okLayout);
		bt_save = (ImageButton) getView().findViewById(R.id.bt_save);
		bt_save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isvideo){
					
				}else{
					final CharSequence[] items = { "保存到本机", "保存到地图" };
					new AlertDialog.Builder(getActivity()).setTitle("选项")
					.setItems(items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {
								dragImageView.setDrawingCacheEnabled(true);
								dragImageView.invalidate();
								path = SaveFileUtil.savebm(dragImageView.getDrawingCache());
								Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
							} else {
								dragImageView.setDrawingCacheEnabled(true);
								dragImageView.invalidate();
								path = SaveFileUtil.savemapbm(dragImageView.getDrawingCache());
								Poi poi = new Poi();
								poi.setTime(DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss());
								poi.setWhos(myname);
								poi.setPath(path);
								poi.setLoca(XmppApplication.getLoca().latitude, XmppApplication.getLoca().longitude);
								if(XmppApplication.getLoca().latitude < 10 || XmppApplication.getLoca().latitude > 70){
									Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_SHORT).show();
									return;
								}
								long id =DBManager.getInstance(getActivity()).savePoi(poi);
								if(id>=0){
									Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_SHORT).show();
								}
								
							}
						}
					}).create().show();
					
				}
			}
		});
		bt_ok = (ImageButton) getView().findViewById(R.id.bt_ok);
		bt_ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dragImageView.setDrawingCacheEnabled(true);
				dragImageView.invalidate();
				Bitmap bbb = dragImageView.getDrawingCache();
				String sendbm = SaveFileUtil.savetempbm(bbb);
				if(ischat){
					
					Intent intent = new Intent(getActivity(), ChatListActivity.class);
					intent.putExtra("msg_type","1");
					intent.putExtra("msg_path", sendbm);
					
					getActivity().setResult(1, intent);
					getActivity().finish();
				}else{
					
					Intent it = new Intent(getActivity(), SendPicActivity.class);
					it.putExtra("msg_path", sendbm);
					startActivityForResult(it, REQUEST_TO_SEND_PIC);
				}
				isvideo = false;
            	
			}
		});
		bt_cancle = (ImageButton) getView().findViewById(R.id.bt_cancle);
		bt_cancle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isvideo = false;
//				sm.unregisterListener(listener);
				surfaceView.setVisibility(View.VISIBLE);
            	camera.startPreview();
	    		okLayout.setVisibility(ViewGroup.GONE);
	            cameraLayout.setVisibility(ViewGroup.VISIBLE);
				colorLayout.setVisibility(View.GONE);//gone
				dragImageView.clear();
				dragImageView.setVisibility(View.GONE);
				tv_text.setText("");
				bt_oktext.setVisibility(View.GONE);
				bt_doback.setVisibility(View.VISIBLE);
			}
		});
		bt_draw = (ImageView) getView().findViewById(R.id.bt_draw);
		bt_draw.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(canDraw){
					canDraw = false;
//					bt_draw.setText("文字");
					dragImageView.setDraw(canDraw);
					bt_draw.setBackgroundColor(0x00000000);
				}else{
					canDraw = true;
//					bt_draw.setText("涂鸦");
					dragImageView.setDraw(canDraw);
					bt_draw.setBackgroundColor(mPaint.getColor());
				}
				
			}
		});
		bt_doback = (ImageButton) getView().findViewById(R.id.bt_doback);
		bt_doback.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(isvideo){
					
				}else{
					int path_num = dragImageView.doback();
//					Toast.makeText(getActivity(), String.valueOf(path_num), Toast.LENGTH_SHORT).show();
				}
			}
		});
		bt_oktext = (ImageButton) getView().findViewById(R.id.bt_oktext);
		bt_oktext.setOnClickListener(new View.OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!tv_text.getText().toString().equals("")){
					try {

						textLayout = null;
						textLayout = (FrameLayout) getView().findViewById(R.id.textLayout);
						textLayout.setDrawingCacheEnabled(true);
						textLayout.destroyDrawingCache();
						if(isvideo){
							
						}else{
							dragImageView.setDrawingCacheEnabled(true);
							textLayout.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 
									MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
							textLayout.layout(0, 0, dragImageView.getMeasuredWidth(), dragImageView.getMeasuredHeight());
							textLayout.buildDrawingCache();
							Bitmap bm = null;
//							bm = Bitmap.createBitmap(dragImageView.getDrawingCache());
							bm = textLayout.getDrawingCache();
							Bitmap bbb = Bitmap.createBitmap(bm);
							dragImageView.drawBitmap(bbb);
							
						}
						tv_text.init();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
				}
				tv_text.setText("");
				bt_oktext.setVisibility(View.GONE);
				bt_doback.setVisibility(View.VISIBLE);
			}
		});
		
		bt_share = (ImageButton)getView().findViewById(R.id.bt_share);
		bt_share.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dragImageView.setDrawingCacheEnabled(true);
				dragImageView.invalidate();
				path = SaveFileUtil.savetempbm(dragImageView.getDrawingCache());
				
				final View shareview = LayoutInflater.from(getActivity()).inflate(R.layout.share, null);
				new AlertDialog.Builder(getActivity())
				.setTitle("选项")
				.setView(shareview)
				.setPositiveButton("分享", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
						EditText et_sharetext = (EditText)shareview.findViewById(R.id.et_sharetext);
						String sharetext = et_sharetext.getText().toString();
						Uri imageUri = Uri.fromFile(new File(path));
				        Intent shareIntent = new Intent();
				        shareIntent.setAction(Intent.ACTION_SEND);
				        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
				        if(sharetext != ""){
				        	shareIntent.putExtra(Intent.EXTRA_SUBJECT, sharetext);
				        	shareIntent.putExtra(Intent.EXTRA_TEXT, sharetext);  
				        }
				        shareIntent.setType("image/*");
				        startActivity(Intent.createChooser(shareIntent, "分享到"));
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				})
				.setCancelable(true)
				.create().show();
				
				
				
			}
		});
	}
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
//			pd.dismiss();
			switch(msg.what){
			case HANDLER_INIT_VIEW:
				dragImageView = (MyImageView) getView().findViewById(R.id.dragImageView);
				dragImageView.setOnTouchListener(new View.OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						tx = event.getX();
						switch(event.getAction()){
						case MotionEvent.ACTION_DOWN:
							if(canDraw){
								okLayout.setVisibility(View.GONE);
								colorLayout.setVisibility(View.GONE);//gone
							}else{
								oldX = event.getX();
							}
							break;
						case MotionEvent.ACTION_MOVE:
							if(tx-oldX>100 && canDraw==false){
								
							}else if(tx-oldX<-100 && canDraw==false){
								
							}
							break;
						case MotionEvent.ACTION_UP:
							if(canDraw){
								okLayout.setVisibility(View.VISIBLE);
								colorLayout.setVisibility(View.VISIBLE);
							}else{
								tx = event.getX();
								if(Math.abs(tx-oldX)<50){
									if(tv_text.getText().toString().equals("")){

										tv_text.setVisibility(View.VISIBLE);
										tv_text.InputShow();
									}
								}else{

								}
							}
							break;
						}

						return false;
					}
				});
				
				fl_text = (FrameLayout) getView().findViewById(R.id.fl_text);
				fl_text.setOnTouchListener(new View.OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						fl_text.getParent().requestDisallowInterceptTouchEvent(true);
						return false;
					}
				});
				fl_text.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(!et_text.getText().toString().trim().equals("")){
							Toast.makeText(getActivity(), et_text.getText().toString().trim(), Toast.LENGTH_SHORT).show();
							bt_oktext.setVisibility(View.VISIBLE);
							bt_doback.setVisibility(View.GONE);
						}
						tv_text.setText(et_text.getText().toString());
						et_text.setText("");
						fl_text.setVisibility(View.GONE);
						tv_text.drag();
						InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
						im.hideSoftInputFromWindow(getView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					}
				});
				et_text = (EditText) getView().findViewById(R.id.et_text);
				et_text.setOnTouchListener(new View.OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						et_text.getParent().getParent().requestDisallowInterceptTouchEvent(true);
						return false;
					}
				});
				et_text.setOnKeyListener(new View.OnKeyListener() {
					
					@Override
					public boolean onKey(View v, int keyCode, KeyEvent event) {
						// TODO Auto-generated method stub

						if(keyCode == KeyEvent.KEYCODE_ENTER){
							bt_oktext.setVisibility(View.VISIBLE);
							bt_doback.setVisibility(View.GONE);
							tv_text.setText(et_text.getText().toString());
							et_text.setText("");
							fl_text.setVisibility(View.GONE);
							tv_text.drag();
							InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
							im.hideSoftInputFromWindow(getView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
						}else if(keyCode == KeyEvent.KEYCODE_BACK){
							tv_text.setText(et_text.getText().toString());
							et_text.setText("");
							if(!tv_text.getText().toString().equals("")){
								bt_oktext.setVisibility(View.VISIBLE);
								bt_doback.setVisibility(View.GONE);
							}else{
								bt_oktext.setVisibility(View.GONE);
								bt_doback.setVisibility(View.VISIBLE);
							}
							fl_text.setVisibility(View.GONE);
							tv_text.drag();
							return true;
						}else if(keyCode == KeyEvent.KEYCODE_CLEAR){
							fl_text.setVisibility(View.GONE);
						}
						if(tv_text.getText().toString().equals("")){

							bt_oktext.setVisibility(View.GONE);
							bt_doback.setVisibility(View.VISIBLE);
							tv_text.setVisibility(View.GONE);
						}
						return false;
					}
				});
				tv_text = (MyTextView) getView().findViewById(R.id.tv_text);
				LayoutParams params = tv_text.getLayoutParams();
				params.width = (int) (tv_text.getTextSize() * tv_text.getText().length());
				tv_text.setLayoutParams(params);
				tv_text.setActivity(getActivity());
				tv_text.setEditText(fl_text,et_text);
				tv_text.setScreenWH(display.getWidth(), display.getHeight());
				
				try{
                	ok_viewStub.inflate();
                	okinit();
                }catch(Exception ex){
                	
                }
			default:
				
				break;
			}
		}
		
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_LOCAL_PIC){
			Uri vUri = data.getData();
			// 将图片内容解析成字节数组
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = getActivity().managedQuery(vUri, proj, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			path = cursor.getString(column_index);
			mBitmap = SaveFileUtil.getxtsldraw(getActivity(), path);
			
			cameraLayout.setVisibility(ViewGroup.GONE);
			dragImageView.setImageBitmap(mBitmap);
            dragImageView.setCamera(false);
	        dragImageView.setScreenWH(display.getWidth(), display.getHeight()-state_height);
	        dragImageView.setActivity(getActivity());
            dragImageView.setPaint(mPaint);
			dragImageView.setVisibility(View.VISIBLE);

			colorLayout.setVisibility(View.VISIBLE);
            okLayout.setVisibility(View.VISIBLE);
            canDraw = false;
            
    		Toast.makeText(getActivity(), "照相完成", Toast.LENGTH_SHORT).show();
    		new Thread(new Runnable() {
			
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
		                surfaceView.destroyDrawingCache();
						surfaceView.setVisibility(View.GONE);
		                dragImageView.setBitmap(mBitmap);
						
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}).start();
		}else if(requestCode == REQUEST_TO_SEND_PIC && resultCode == RESULT_SEND_PIC_SUCCESS){
			surfaceView.setVisibility(View.VISIBLE);
//        	camera.startPreview();
    		okLayout.setVisibility(ViewGroup.GONE);
            cameraLayout.setVisibility(ViewGroup.VISIBLE);
			colorLayout.setVisibility(View.GONE);//gone
			dragImageView.clear();
			dragImageView.setVisibility(View.GONE);
			tv_text.setText("");
			bt_oktext.setVisibility(View.GONE);
			bt_doback.setVisibility(View.VISIBLE);
		}else if(requestCode == 20 && resultCode == 22){
			
		}
	}

	@SuppressLint("NewApi")
	private void initCamera(){
			camera.stopPreview();
			parameters = null;
			parameters = camera.getParameters();
			parameters.setPictureFormat(PixelFormat.JPEG);
			parameters.set("orientation", "portrait");
			parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
			
			if(cameraPosition == 0){
				parameters.setRotation(orientation);
//				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			}else{
				parameters.setRotation((orientation+270)%360);
			}
			List<Size> sizes = parameters.getSupportedPreviewSizes();
			Size optimalSize = getOptimalPreviewSize(sizes, display.getHeight()-state_height, display.getWidth());
			parameters.setPreviewSize(optimalSize.width,optimalSize.height);
			List<Size> sizes2 = parameters.getSupportedPictureSizes();
			Size optimalSize2 = getOptimalPreviewSize(sizes2, display.getHeight()-state_height, display.getWidth());
			parameters.setPictureSize(optimalSize2.width, optimalSize2.height);
			surfaceView.setLayoutParams(new FrameLayout.LayoutParams(display.getWidth(),
					display.getWidth()*parameters.getPreviewSize().width/parameters.getPreviewSize().height));

//			Toast.makeText(getActivity(), String.valueOf(parameters.getPreviewSize().width) + " + "+String.valueOf(parameters.getPictureSize().width)
//					 + " + " + String.valueOf(parameters.getPreviewSize().height) + "+"+String.valueOf(parameters.getPictureSize().height), Toast.LENGTH_SHORT).show();
			camera.setParameters(parameters);
			
			camera.setDisplayOrientation(SaveFileUtil.getPreviewDegree(getActivity())); 
			camera.startPreview();
		
	}
	
	public class CameraPreview implements SurfaceHolder.Callback{
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			if(holder.getSurface() == null){
				return;
			}
			try{
				camera.stopPreview();
			}catch(Exception e){
				
			}
			
			try{
				camera.setPreviewDisplay(holder);
				initCamera();
			}catch(Exception e){
				
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			try{

				camera.setPreviewDisplay(holder);
				camera.startPreview();
			}catch(Exception e){
				
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private final class MyPictureCallback implements PictureCallback{

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			try {  
				
                int width = display.getWidth();
                int height = display.getHeight()-state_height;
                int sampleSize = 1;
                
                Options opt = new Options();
                opt.inJustDecodeBounds = true;
                opt.inPreferQualityOverSpeed = false;
                
                BitmapFactory.decodeByteArray(data, 0, data.length, opt);
                while(opt.outHeight/sampleSize > height || opt.outWidth/sampleSize > width){
                	sampleSize *= 2;
                }
                
                opt.inJustDecodeBounds = true;
                opt.inSampleSize = sampleSize;
                opt.inPreferQualityOverSpeed = false;
                opt.inJustDecodeBounds = false;
                Matrix ma = new Matrix();
                
                if(cameraPosition == 1){
                	Bitmap bb = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
                	ma.postRotate(270);
                	mBitmap = Bitmap.createBitmap(bb,0,0,bb.getWidth(),bb.getHeight(),ma,true);
    				Canvas ca = new Canvas(mBitmap);
    				ca.drawBitmap(bb, ma, mPaint);
                }else{
                	mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
                }
                
                dragImageView.setImageBitmap(mBitmap);
                dragImageView.setCamera(true);
		        dragImageView.setScreenWH(display.getWidth(), display.getHeight()-state_height);
		        dragImageView.setActivity(getActivity());
                dragImageView.setPaint(mPaint);
				dragImageView.setVisibility(View.VISIBLE);

				colorLayout.setVisibility(View.VISIBLE);
                okLayout.setVisibility(ViewGroup.VISIBLE);
                canDraw = false;
                
        		Toast.makeText(getActivity(), "照相完成", Toast.LENGTH_SHORT).show();
               new Thread(new Runnable() {
				
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
			                surfaceView.destroyDrawingCache();
							surfaceView.setVisibility(View.GONE);
			                dragImageView.setBitmap(mBitmap);
							
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}).start();
            } catch (Exception e) {  
                e.printStackTrace();  
            } 
		}
		
	}
	
	
	private void releaseMediaRecorder(){
        if (mediarecorder != null) {
            mediarecorder.reset();   // clear recorder configuration
            mediarecorder.release(); // release the recorder object
            mediarecorder = null;
            camera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (camera != null){
        	camera.cancelAutoFocus();
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }

    public class SensorListener implements SensorEventListener{

		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			float ax = event.values[0];
			float ay = event.values[1]; 
			double g = Math.sqrt(ax * ax + ay * ay); 
	        double cos = ay / g; 
	        if (cos > 1) { 
	            cos = 1; 
	        } else if (cos < -1) { 
	            cos = -1; 
	        } 
	        double rad = Math.acos(cos); 
	        if (ax < 0) { 
	            rad = 2 * Math.PI - rad; 
	        } 
	        
	        int uiRot = getActivity().getWindowManager().getDefaultDisplay().getRotation(); 
	        double uiRad = Math.PI / 2 * uiRot; 
	        rad -= uiRad; 
	        if(rad >= Math.PI*7/4 || rad < Math.PI/4){
	        	orientation = 90;
	        }else if(rad >= Math.PI/4 && rad < Math.PI*3/4){
	        	orientation = 0;
	        }else if(rad >= Math.PI*3/4 && rad < Math.PI*5/4){
	        	orientation = 270;
	        }else{
	        	orientation = 180;
	        }
		}
		
	}

    @Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		releaseMediaRecorder();
		releaseCamera();
		sm.unregisterListener(listener);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		sm.registerListener(listener, sensor,SensorManager.SENSOR_DELAY_NORMAL);
		if(camera == null)
			camera = Camera.open();
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		  final double ASPECT_TOLERANCE = 0.2;
		  double targetRatio = (double) w / h;
		  if (sizes == null)
			  return null;
		  Size ss = null;
		  List<Size> optimalSize = new ArrayList<Camera.Size>();
		  double minDiff = Double.MAX_VALUE;

		  int targetHeight = h;

		  // Try to find an size match aspect ratio and size
		  for (Size size : sizes) {
			  double ratio = (double) size.width / size.height;
			  if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				  continue;
			  optimalSize.add(size);
//			  if (Math.abs(size.height - targetHeight) < minDiff) {
//				  optimalSize.add(size);
//				  minDiff = Math.abs(size.height - targetHeight);
//			  }
		  }

		  if(optimalSize.size()==0){
			  minDiff = Double.MAX_VALUE;
			  for (Size size : sizes) {
				  double ratio = (double) size.width / size.height;
				  	optimalSize.clear();
				  if (Math.abs(ratio - targetRatio) < minDiff) {
					  optimalSize.add(size);
					  minDiff = Math.abs(size.height - targetHeight);
				  }
			  }
		  }
		  // Cannot find the one match the aspect ratio, ignore the requirement
		  if (optimalSize.size()>1) {
		   minDiff = Double.MAX_VALUE;
		   for (Size size : optimalSize) {
		    if (Math.abs(size.height - targetHeight) < minDiff) {
		     ss = size;
		     minDiff = Math.abs(size.height - targetHeight);
		    }
		   }
		  }else{
			  ss = optimalSize.get(0);
		  }
		  return ss;
		 }
	
	protected DisplayMetrics getScreenWH() {
        DisplayMetrics dMetrics = new DisplayMetrics();
        dMetrics = this.getResources().getDisplayMetrics();
        return dMetrics;
     }
}
