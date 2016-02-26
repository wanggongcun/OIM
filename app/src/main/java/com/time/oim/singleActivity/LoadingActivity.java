package com.time.oim.singleActivity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.time.oim.LeadActivity;
import com.time.oim.MainActivity;
import com.time.oim.R;
import com.time.oim.util.Constant;
import com.time.oim.view.DragImageView;

public class LoadingActivity extends Activity {
	private DragImageView dragImageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_loading);
		
		dragImageView = (DragImageView)findViewById(R.id.dragImageView);
		new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
            	if(getSharedPreferences(Constant.LOGIN_SET, 0).getBoolean(Constant.IS_FIRSTSTART, true) == true){
            		getSharedPreferences(Constant.LOGIN_SET, 0).edit().putBoolean(Constant.IS_FIRSTSTART, false).commit();
            		startActivity(new Intent(LoadingActivity.this, LeadActivity.class));
            	}else{
            		startActivity(new Intent(LoadingActivity.this, MainActivity.class));
            	}
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, 1000);
	}
}
