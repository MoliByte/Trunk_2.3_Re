package com.avoscloud.chat.service;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.IBinder;

import com.avos.avoscloud.LogUtil.log;
import com.avos.avoscloud.PushService;

public class CustomedPushService extends PushService {
	public static final String TAG = "CustomedPushService";
	@Override
	public void onDestroy() {
		super.onDestroy();
		log.e(TAG, "........onDestroy");
	}

	@Override
	public IBinder onBind(Intent intent) {
		log.e(TAG, "........onBind");
		return super.onBind(intent);
		
	}

	@Override
	public void onCreate() {
		super.onCreate();
		log.e(TAG, "........onCreate");
	}

	@Override
	@TargetApi(5)
	public int onStartCommand(Intent intent,
            int flags,
            int startId) {
		log.e(TAG, "flags" + flags);
		log.e(TAG, "intent" + intent.getAction());
		log.e(TAG, "startId" + startId);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	@TargetApi(14)
	public void onTaskRemoved(Intent rootIntent) {
		log.e(TAG, "intent" + rootIntent.getAction());
		super.onTaskRemoved(rootIntent);
	}
	
}
