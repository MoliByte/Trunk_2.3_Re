package com.beabox.hjy.tt;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.avoscloud.chat.base.ChatMsgAdapter;
import com.umeng.message.PushAgent;

/**
 * Created by lzw on 14-9-21.
 */
public class ImageBrowerActivity extends BaseActivity {
	String url;
	String path;
	PhotoView imageView;
	
	ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
		PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		//getWindow().setBackgroundDrawable(dw);
		setContentView(R.layout.chat_image_brower_layout);
		imageView = (PhotoView) findViewById(R.id.imageView);
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		path = intent.getStringExtra("path");
		ChatMsgAdapter.displayImageByUri(imageView, path, url);
		imageView.setOnPhotoTapListener(new PhotoTapListener());
//		imageView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				finish();
//				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//			}
//		});
	}
	 private class PhotoTapListener implements OnPhotoTapListener {

	        @Override
	        public void onPhotoTap(View view, float x, float y) {
	            float xPercentage = x * 100f;
	            float yPercentage = y * 100f;
	            finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

	        }
	    }
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setupView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMessageHandler(int messageCode) {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getActivityName() {
		// TODO Auto-generated method stub
		return null;
	}
}
