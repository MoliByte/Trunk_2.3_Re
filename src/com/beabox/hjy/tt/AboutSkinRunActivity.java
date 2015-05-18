package com.beabox.hjy.tt;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.umeng.message.PushAgent;

@SuppressWarnings("serial")
public class AboutSkinRunActivity extends BaseActivity  {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
		PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_skinrnu);
		findViewById(R.id.backBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void setupView() {
	}

	@Override
	public void addListener() {
		
	}

	@Override
	public void sendMessageHandler(int messageCode) {
		
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		
	}
	
	@Override
    protected String getActivityName() {
        return "关于SkinRun";
    }

}
