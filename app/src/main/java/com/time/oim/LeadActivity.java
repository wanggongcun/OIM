package com.time.oim;

import com.time.oim.singleActivity.LoadingActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class LeadActivity extends Activity {
	private Button bt_in;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_lead);
		
		bt_in = (Button) findViewById(R.id.bt_in);
		bt_in.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(LeadActivity.this, MainActivity.class));
        	
	            finish();
	            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		});
		
	}
}
