package com.beabox.hjy.tt;

import com.base.supertoasts.util.AppToast;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PushBroadCastReceiver extends BroadcastReceiver {
	boolean isServiceRunning = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_TIME_TICK)||Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
			// 检查Service状态
			ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			AppToast.toastMsg(context, "守护广播").show();
			Log.e("PushBroadCastReceiver", "==========守护广播");
			
			for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
				if ("com.beabox.hjy.tt.LocalPushService".equals(service.service.getClassName())) {
					isServiceRunning = true;
				}
			}
			if (!isServiceRunning) {
				Intent i = new Intent(context, LocalPushService.class);
				context.startService(i);
			}
		}
	}
}
