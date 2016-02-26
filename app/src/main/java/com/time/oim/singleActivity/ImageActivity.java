package com.time.oim.singleActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.time.oim.R;
import com.time.oim.view.DragImageView;

public class ImageActivity extends Activity {

	private DragImageView dragImageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image);
		WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        dragImageView = (DragImageView)findViewById(R.id.dragImageView);
		String image_path = getIntent().getStringExtra("image_path");
		Bitmap bitmap = BitmapFactory.decodeFile(image_path);
		dragImageView.setImageBitmap(bitmap);
		dragImageView.setmActivity(ImageActivity.this);
		Rect frame = new Rect();
		getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(frame);
		int state_height = frame.top;
		dragImageView.setScreen_H(display.getHeight()-state_height);
		dragImageView.setScreen_W(display.getWidth());
		
		dragImageView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					finish();
					break;

				default:
					break;
				}
				return false;
			}
		});
	}
}
