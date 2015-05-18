package com.beabox.hjy.tt;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.app.base.entity.UserEntity;
import com.app.base.init.ACache;
import com.app.base.init.MyApplication;
import com.app.custom.view.CustomVideoView;
import com.base.app.utils.DBService;
import com.umeng.message.PushAgent;

public class StartActivity extends Activity implements OnClickListener {
	private CustomVideoView videoView;
	private TextView tvLook,tvLogin,tvRegister;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
		 //无title    
		requestWindowFeature(Window.FEATURE_NO_TITLE);    
        //全屏    
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);  
		
		
		setContentView(R.layout.start_activity);
		if(isLogin()){
			Intent intent=new Intent(this, SkinRunMainActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_enter_from_right, R.anim.activity_exit_to_left);
		}else{
			videoView=(CustomVideoView)findViewById(R.id.videoViewStart);
			tvLook=(TextView)findViewById(R.id.tvLook);
			tvRegister=(TextView)findViewById(R.id.tvRegister);
			tvLogin=(TextView)findViewById(R.id.tvLogin);
			
			tvLogin.setOnClickListener(this);
			tvLook.setOnClickListener(this);
			tvRegister.setOnClickListener(this);
			playVideo();
		}
	}
	
	
	private boolean isLogin(){
		if (!"".equals(ACache.get(this).getAsString("Token"))
				&& !"".equals(ACache.get(this).getAsString("uid"))) {
			return true;
		} else{
			UserEntity user=DBService.getUserEntity();
			if(user!=null&&user.getToken()!=null&&!user.getToken().equals("")){
				return true;
			}else{
				return false;
			}
		}
		
	}
	
	private void playVideo(){
		//videoView.setVideoURI(Uri.parse("android.resource://com.telecom.activities/"+R.raw.yanwo));
		videoView.setVideoPath("android.resource://" + getPackageName() + "/"+R.raw.movice);
		videoView.start();    
		videoView.requestFocus(); 
		videoView.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer m) {
				 m.start();  
	             m.setLooping(true);  
			}
		});
		
		videoView.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tvLogin:
			Intent intent=new Intent(this, LoginActivity.class);
			intent.putExtra("FROM", 1);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_enter_from_right, R.anim.activity_exit_to_left);
			finish();
			break;
		case R.id.tvRegister:
			Intent i=new Intent(this,  RegisterActivity_1.class);
			i.putExtra("FROM", 1);
			startActivity(i);
			overridePendingTransition(R.anim.activity_enter_from_right, R.anim.activity_exit_to_left);
			finish();
			break;
		case R.id.tvLook:
			Intent in=new Intent(this, SkinRunMainActivity.class);
			startActivity(in);
			overridePendingTransition(R.anim.activity_enter_from_right, R.anim.activity_exit_to_left);
			finish();
			break;
		}
	}
}
