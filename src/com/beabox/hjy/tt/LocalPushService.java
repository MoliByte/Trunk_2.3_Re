package com.beabox.hjy.tt;

import java.util.Timer;
import java.util.TimerTask;
import com.app.base.entity.FacialTestEntity;
import com.app.base.entity.ProductEntity;
import com.app.base.entity.UserEntity;
import com.base.app.utils.EasyLog;
import com.base.app.utils.GetUserInfoUtil;
import com.base.supertoasts.util.AppToast;
import com.skinrun.trunk.facial.mask.test.FacialDuration;
import com.skinrun.trunk.facial.mask.test.FacialmaskTestActivity;
import com.skinrun.trunk.facial.mask.test.SaveFacialResultUtil;
import com.skinrun.trunk.facial.mask.test.SaveSelectedProductUtil;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.IBinder;

public class LocalPushService extends Service {
	private final Timer timer = new Timer();
	private TimerTask task;
	private Handler handler;
	 //获取到通知管理器  
    NotificationManager mNotificationManager;
    FacialTestEntity facialTestEntity;
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mNotificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		handler=new Handler();
		
		task=new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					
					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						try{
							//AppToast.toastMsg(LocalPushService.this, "不死服务").show();
							UserEntity userEntity=GetUserInfoUtil.getUserEntity();
							if(userEntity==null||userEntity.getToken()==null){
								//AppToast.toastMsg(LocalPushService.this, "Push2222").show();
								return;
							}
							facialTestEntity=SaveFacialResultUtil.getRecord(userEntity.getToken());
							if(facialTestEntity==null){
								//AppToast.toastMsg(LocalPushService.this, "FacialTestEntity为空").show();
								return;
							}
							
							if(facialTestEntity!=null){
								if(facialTestEntity.getTime1()==null||facialTestEntity.getTime1().equals("")){
									//AppToast.toastMsg(LocalPushService.this, "Push3333").show();
									return;
								}
								long d=System.currentTimeMillis()-Long.parseLong(facialTestEntity.getTime1());
								//Log.e("LocalPushService", "============距离上次测试时间间隔："+d);
								if(d<FacialDuration.MianMoing||d>=FacialDuration.YanShiBuShui){
									//AppToast.toastMsg(LocalPushService.this, "Push4444").show();
									return;
								}
								
								Notification baseNF=new Notification();
		                        //设置通知在状态栏显示的图标
		                        baseNF.icon = R.drawable.app_icon;
		                        //通知时在状态栏显示的内容
		                        baseNF.tickerText = "面膜测试";
		                        //通知被点击后，自动消失
		                        baseNF.flags |= Notification.FLAG_AUTO_CANCEL;
		                        //点击'Clear'时，不清除该通知(QQ的通知无法清除，就是用的这个)
		                        baseNF.flags |= Notification.FLAG_NO_CLEAR;
		                        //通知的默认参数 DEFAULT_SOUND, DEFAULT_VIBRATE, DEFAULT_LIGHTS.
		                        //如果要全部采用默认值, 用 DEFAULT_ALL. 
		                        //此处采用默认声音
		                        baseNF.defaults |= Notification.DEFAULT_ALL;
		                        //第二个参数 ：下拉状态栏时显示的消息标题 expanded message title
		                        //第三个参数：下拉状态栏时显示的消息内容 expanded message text
		                        //第四个参数：点击该通知时执行页面跳转
		                        Intent intent=new Intent(getApplicationContext(),FacialmaskTestActivity.class);
		                        //用户所选面膜
		                        ProductEntity selectedProduct=SaveSelectedProductUtil.getSelectProduct(userEntity.getToken());
		                        if(selectedProduct==null){
		                        	return;
		                        }
		                        intent.putExtra("REPLY", 0);
		                        
		                        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,intent,0);
								
		                        SharedPreferences sharedPreferences=getSharedPreferences("LOCAL_PUSH", Context.MODE_WORLD_WRITEABLE);
		                        boolean push1=sharedPreferences.getBoolean("push1", false);
		                        boolean push2=sharedPreferences.getBoolean("push2", false);
		                        boolean push3=sharedPreferences.getBoolean("push3", false);
		                        Editor editor=sharedPreferences.edit();
		                        
								if(d<FacialDuration.JiShiBuShui){
									if(!push1){
										 baseNF.setLatestEventInfo(LocalPushService.this.getApplicationContext(), "面膜测试提示",
					                        		"小主，您有正在进行中的面膜测试已经进入即时补水阶段了哦~", pendingIntent);
										 
										 editor.putBoolean("push1", true);
										 
										 mNotificationManager.notify(0x001, baseNF);
									}
									
								}else if(d<FacialDuration.ChiXuBuShui){
									if(!push2){
										 baseNF.setLatestEventInfo(LocalPushService.this.getApplicationContext(), "面膜测试提示",
					                        		"小主，您有正在进行中的面膜测试已经进入持续补水阶段了哦~", pendingIntent);
										 //AppToast.toastMsg(LocalPushService.this, "Push22：").show();
										 editor.putBoolean("push2", true);
										 mNotificationManager.notify(0x002, baseNF);
									}
									
								}else if(d<FacialDuration.YanShiBuShui){
									if(!push3){
										 baseNF.setLatestEventInfo(LocalPushService.this.getApplicationContext(), "面膜测试提示",
					                        		"小主，您有正在进行中的面膜测试已经进入长效补水阶段了哦~", pendingIntent);
										 //AppToast.toastMsg(LocalPushService.this, "Push33：").show();
										 editor.putBoolean("push3", true);
										 mNotificationManager.notify(0x003, baseNF);
									}
								}
								editor.commit();
							}
							
						}catch(Exception e){
							e.printStackTrace();
							EasyLog.e(e.toString());
						}
					}
				});
			}
		};
		
		timer.schedule(task, 2000, 10*1000);
	}
}
