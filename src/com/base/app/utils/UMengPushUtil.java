package com.base.app.utils;

import org.json.JSONException;

import com.base.service.impl.HttpClientUtils;
import com.umeng.message.PushAgent;
import com.umeng.message.proguard.k.e;

import android.content.Context;
import android.util.Log;

public class UMengPushUtil {
	//加别名
	public void addAlice(Context context,String uid){
		final PushAgent mPushAgent = PushAgent.getInstance(context);
		mPushAgent.enable();
		final String aliceValue=HttpClientUtils.md5(uid+"SKINRUN");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					mPushAgent.addAlias(aliceValue, "UID");
					Log.e("UMengPushUtil", "==========友盟推送别名："+aliceValue);
				} catch (e e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
