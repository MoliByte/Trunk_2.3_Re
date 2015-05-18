package com.beabox.hjy.tt;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.skinrun.trunk.adapter.SplashPagerAdapter;
import com.umeng.message.PushAgent;

@SuppressWarnings("serial")
public class SkinRunSplashActivity extends BaseActivity implements
		SplashPagerAdapter.SplashPagerAdapterListener {
	//升级到新版本，用户数据保留
	public static final String SPLASH_SHOWN_PREFERENCE = "splash_has_shown_prefer";
    public static final String SPLASH_SHOWN_KEY = "splash_has_shown_key";
    
    private final static int SKIP_TIME = 2000 ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		//loginServer();
		setupView();
		//注册
		MyApplication.getInstance().informedUser();
	}

	

	private boolean splashHasShown() {
		SharedPreferences sharedPreferences = MyApplication.getInstance()
				.getSharedPreferences(SPLASH_SHOWN_PREFERENCE, MODE_PRIVATE);
		return sharedPreferences.getBoolean(getSplashShownKey(), Boolean.FALSE);
	}

	private void setSplashShown() {
		SharedPreferences sharedPreferences = MyApplication.getInstance()
				.getSharedPreferences(SPLASH_SHOWN_PREFERENCE, MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(getSplashShownKey(), Boolean.TRUE);
		editor.commit();
	}
	
	private String getSplashShownKey() {
        return SPLASH_SHOWN_KEY + MyApplication.getInstance().getAppVersionCode();
    }

	@Override
	public void startExperience() {
		//loginServer();
		Intent intent = new Intent(SkinRunSplashActivity.this, SkinRunMainActivity.class);
        startActivity(intent);
        //overridePendingTransition(R.anim.activity_exit_to_left, R.anim.activity_enter_from_right);
       
        
		/*skipActivity(SkinRunSplashActivity.this, SkinRunMainActivity.class);*/
		this.finish();
		 overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
	}

	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void setupView() {
		/*String startup_switch = getResources().getString(R.string.startup_switch);
		if("startup_skinrun_on".equals(startup_switch)){
			startActivity(new Intent(this,StartActivity.class));
			finish();
			 overridePendingTransition(
						R.anim.activity_enter_from_right,
						R.anim.activity_exit_to_left);
		}else{*/
			// 如果已经查看splash了，则直接跳转
			if (!splashHasShown()) {
				setSplashShown();
				ViewPager viewPager = (ViewPager) findViewById(R.id.viewPaper);
				ViewGroup pointGroup = (ViewGroup) findViewById(R.id.pointGroup);
				SplashPagerAdapter splashPagerAdapter = new SplashPagerAdapter(
						this, pointGroup, this);
				viewPager.setAdapter(splashPagerAdapter);
				viewPager.setOnPageChangeListener(splashPagerAdapter);
			} else {
				Runnable task = new Runnable() {
					@Override
					public void run() {
						startExperience();
					}
				};
				new Handler().postDelayed(task, SKIP_TIME);
			}
		//}

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
		return "启动画面";
	}

}
