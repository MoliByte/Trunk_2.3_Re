package com.skinrun.trunk.facial.mask.test;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.base.entity.CompleteProductEntity;
import com.app.base.entity.FacialComparaEntity;
import com.app.base.entity.FacialTestEntity;
import com.app.base.entity.ProductEntity;
import com.app.base.entity.UpLoadImagePathEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.MyApplication;
import com.app.service.GetCompleteProductService;
import com.app.service.PostFacialTestRecordService;
import com.base.app.utils.DBService;
import com.base.app.utils.SavaUpLoadImagePathUtil;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.beabox.hjy.tt.R;
import com.skinrun.trunk.main.AudioProcess;
import com.triggertrap.seekarc.SeekArc;

public class FacialmaskTestActivity extends Activity implements OnClickListener, HttpAysnResultInterface {

	private String TAG = "FacialmaskTestActivity";
	
	private TextView tvFacialName, textViewCurrentState1,
			textViewCurrentState2, textViewTestStep, textViewFacialResult,
			tvFacialTestStep1, tvFacialResult1, tvFacialTestStep2,
			tvFacialResult2, tvFacialTestStep3, tvFacialResult3,
			tvFacialTestStep4, tvFacialResult4,tvFacialTestStepTime1,tvFacialTestStepTime2,tvFacialTestStepTime3,tvFacialTestStepTime4;

	private View viewResult1, viewResult2, viewResult3, viewResult4;

	private ImageView ivRaiseIcon;
	
	private ProductEntity selectedProduct;
	
	private SeekArc seekArc;
	
	private FacialComparaEntity facialComparaEntity;
	
	private boolean isSave=true;
	
	
	
	Timer m_timer1; // 定时器
	Handler m_handler;
	int mnTestCount = 0;

	private SoundPool mSoundPool;
	int mnSoundID = 0;
	int mnCurrentVolume; // 当前竟量
	
	boolean mbLinked = false; // 是否连接设备
	//AudioRecord audioRecord; // 播放声音
	AudioProcess audioProcess = MyApplication.getInsProcess();
	HeadsetPlugReceiver headsetPlugReceiver = null;

	private Handler handler = new Handler();
	private int waterNum;
	private float waterNum1,waterNum2,waterNum3,waterNum4;
	private long startTime, currentTime,testTime2,testTime3,testTime4;
	
	private PostFacialTestRecordService postFacialTestRecordService;
	
	private boolean isFinish=false;
	//测试id
	private int sid,reply;
	
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if (startTime == 0) {
				startTime = System.currentTimeMillis();
			}

			currentTime = System.currentTimeMillis();

			long duration = (currentTime - startTime) / 1000;
			int hour = (int) Math.abs(duration / 3600);
			int minute = (int) Math.abs(duration % 3600 / 60);
			int second = (int) Math.abs(duration % 3600 % 60);

			String hourStr = hour >= 10 ? hour + "" : "0" + hour;
			String minuteStr = minute >= 10 ? minute + "" : "0" + minute;
			String secondStr = second >= 10 ? second + "" : "0" + second;

			if (hour == 0) {
				textViewFacialResult.setText(minuteStr + ":" + secondStr);
			} else {
				textViewFacialResult.setText(hourStr + ":" + minuteStr + ":"
						+ secondStr);
			}
			handler.postDelayed(this, 1000);

		}
	};
	
	
	//初始化上次测试记录
	private void initLastTest(){
		UserEntity userEntity=DBService.getUserEntity();
		if(userEntity!=null){
			FacialTestEntity facialTestEntity=SaveFacialResultUtil.getRecord(userEntity.getToken());
			if(facialTestEntity!=null){
				try{
					startTime=Long.parseLong(facialTestEntity.getTime1());
					testTime2=Long.parseLong(facialTestEntity.getTime2());
					testTime3=Long.parseLong(facialTestEntity.getTime3());
					testTime4=Long.parseLong(facialTestEntity.getTime4());
					waterNum1=facialTestEntity.getWater1();
					waterNum2=facialTestEntity.getWater2();
					waterNum3=facialTestEntity.getWater3();
					waterNum4=facialTestEntity.getWater4();
					
					 int blue=getResources().getColor(R.color.facial_blue);
					
					if(waterNum1>0){
						tvFacialTestStep1.setTextColor(blue);
						tvFacialResult1.setTextColor(blue);
						viewResult1.setBackgroundColor(blue);
						tvFacialTestStepTime1.setTextColor(blue);
						tvFacialResult1.setText(String.format("%1.1f", waterNum1)+"%");
						textViewTestStep.setText("面膜时间");
						textViewFacialResult.setText(String.format("%1.1f", waterNum1)+"%");
					}
					
					if(waterNum2>0){
						tvFacialTestStep2.setTextColor(blue);
						tvFacialResult2.setTextColor(blue);
						viewResult2.setBackgroundColor(blue);
						tvFacialTestStepTime2.setTextColor(blue);
						tvFacialResult2.setText(String.format("%1.1f", waterNum2)+"%");
						textViewTestStep.setText("即时补水");
						textViewFacialResult.setText(String.format("%1.1f", waterNum2)+"%");
					}
					
					if(waterNum3>0){
						tvFacialTestStep3.setTextColor(blue);
						tvFacialResult3.setTextColor(blue);
						tvFacialTestStepTime3.setTextColor(blue);
						viewResult3.setBackgroundColor(blue);
						tvFacialResult3.setText(String.format("%1.1f", waterNum3)+"%");
						textViewTestStep.setText("持续补水");
						textViewFacialResult.setText(String.format("%1.1f", waterNum3)+"%");
					}
					
					if(waterNum4>0){
						tvFacialTestStep4.setTextColor(blue);
						tvFacialResult4.setTextColor(blue);
						viewResult4.setBackgroundColor(blue);
						tvFacialTestStepTime4.setTextColor(blue);
						tvFacialResult4.setText(String.format("%1.1f", waterNum4)+"%");
						textViewTestStep.setText("长效补水");
						textViewFacialResult.setText(String.format("%1.1f", waterNum4)+"%");
					}
					
					handler.postDelayed(runnable, 1000);
					
					
				}catch(Exception e){
					
				}
			}
		}
	}
	
	
	//保存测试记录
	private void savaRecord(){
		UserEntity userEntity=DBService.getUserEntity();
		if(userEntity!=null&&!userEntity.getToken().equals("")){
			FacialTestEntity facialTestEntity=getFacialTestEntity();
			if(facialTestEntity!=null){
				//保存
				SaveFacialResultUtil.save(facialTestEntity);
			}else{
				SaveFacialResultUtil.delete(userEntity.getToken());
			}
		}
	}
	
	private FacialTestEntity getFacialTestEntity(){
		UserEntity userEntity=DBService.getUserEntity();
		if(userEntity!=null&&!userEntity.getToken().equals("")){
			FacialTestEntity facialTestEntity=new FacialTestEntity();
			facialTestEntity.setToken(userEntity.getToken());
			facialTestEntity.setTime1(startTime+"");
			facialTestEntity.setTime2(testTime2+"");
			facialTestEntity.setTime3(testTime3+"");
			facialTestEntity.setTime4(testTime4+"");
			facialTestEntity.setWater1(waterNum1);
			facialTestEntity.setWater2(waterNum2);
			facialTestEntity.setWater3(waterNum3);
			facialTestEntity.setWater4(waterNum4);
			
			if(waterNum1<=0){
				return null;
			}else{
				return facialTestEntity;
			}
		}
		return null;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		
		reply=getIntent().getIntExtra("REPLY", 0);
		
		
		postFacialTestRecordService=new PostFacialTestRecordService(this, HttpTagConstantUtils.UPLOAD_FACIAL_TESTNUM, this);
		setContentView(R.layout.facialmasktest);
		initView();
		
		
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(headsetPlugReceiver);
		m_timer1.cancel();
		audioProcess.stop();
		resetVolume(); // 恢复音量
		mSoundPool.release();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		init();
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}

	@Override
	protected void onStop() {
		super.onStop();
		handler.removeCallbacks(runnable);
		savaRecord();
	}
	@Override
	protected void onStart() {
		super.onStart();
		initLastTest();
	}
	private void init() {
		mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		mnSoundID = mSoundPool.load(this, R.raw.send_data, 1);
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mnCurrentVolume = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		registerHeadsetPlugReceiver();

		// 定时器
		TimerTask task = new TimerTask() {
			public void run() {
				Message message = new Message();
				message.what = 1;
				m_handler.sendMessage(message);
			}
		};
		m_timer1 = new Timer(true);
		m_timer1.schedule(task, 1000, 1000); // 1秒后执行，每秒一次

		m_handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 3: {
					Bundle bundle = msg.getData();
					int nCount = bundle.getInt("Count");
					int nBit[] = bundle.getIntArray("Bit");
					String strText = "";
					for (int i = 0; i < nCount; i++) {
						if (i % 4 == 0)
							strText += " ";
						strText += String.format("%d", nBit[i]);
					}
					break;
				}
				case 1:
					boolean bLinked = audioProcess.isLinked();
					if(bLinked != mbLinked) {
						mbLinked = bLinked;
						if(mbLinked) {
							setMaxVolume();
						}
						else {
							resetVolume();
						}
						Log.i("timer", "状态改变 00001"); 
						//btnTest.setEnabled(mbLinked);
					}
					break;
				case 2:
					Bundle bundle = msg.getData();
					int nTestEm = bundle.getInt("Em");
					Log.i("Em", String.format("����ֵ: %d", nTestEm));
					// float fMoist = bundle.getFloat("Moist");
					// float fOil = bundle.getFloat("Oil");
					// float fSoft = bundle.getFloat("Soft");
					// tvMoist.setText(String.format("%1.2f", fMoist));
					// tvOil.setText(String.format("%1.2f", fOil));
					// tvSoft.setText(String.format("%1.2f", fSoft));

					// 2014217
					float fWater = bundle.getFloat("Water");
					float fOil = bundle.getFloat("Oil");
					float fTX = bundle.getFloat("TX");
					float fWater_Oil = bundle.getFloat("Water_Oil");
					waterNum = (int) fWater;
					Log.e(TAG, "==========每次测试的水值：" + waterNum);
					// seekArc.setProgress(0);
					
					if(fWater>99){
						final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(FacialmaskTestActivity.this);

						dialogBuilder.withTitle(null).withMessage(null).withMessageColor("#FFFFFFFF")
								.isCancelableOnTouchOutside(false).withDuration(200).withEffect(Effectstype.Fadein)
								.withButton1Text("我知道了").setCustomView(R.layout.facial_test_error,FacialmaskTestActivity.this)
								.setButton1Click(new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										dialogBuilder.dismiss();
									}
								}).show();
						return;
					}
					
					judgeDuration(fWater);
					
					
					try{
						if(facialComparaEntity!=null){
							sid=facialComparaEntity.getSid();
						}
						
						UpLoadImagePathEntity u=SavaUpLoadImagePathUtil.get(DBService.getUserEntity().getToken());
						String imageUrl="";
						if(u!=null){
							imageUrl=u.getImageUrl();
						}
						postFacialTestRecordService.postRecord(DBService.getUserEntity().getToken(), fWater, 0,
								selectedProduct.getProductId(), reply, sid,imageUrl);
					}catch(Exception e){
						
					}
					
					handler.removeCallbacks(runnable);
					
					
					Log.e(TAG, "===========handler");
					handler.postDelayed(runnable, 5000);

					break;
				}

				super.handleMessage(msg);
			}
		};
	}
	
	private void isSaveValue(final TextView tv,final TextView tv2,final float value,final int step){
		final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(FacialmaskTestActivity.this);
		
		dialogBuilder.withTitle(null).withMessage(null).withMessageColor("#FFFFFFFF")
				.isCancelableOnTouchOutside(false).withDuration(200).withEffect(Effectstype.Fadein)
				.withButton1Text("不保存").setCustomView(R.layout.issavevalue,FacialmaskTestActivity.this)
				.setButton1Click(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						dialogBuilder.dismiss();
					}
				}).withButton2Text("保存").setButton2Click(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						tv.setText(String.format("%1.1f", value)+"%");
						tv2.setText(String.format("%1.1f", value)+"%");
						dialogBuilder.dismiss();
						setSeekProgress(waterNum);
						switch(step){
						case FacialDuration.M1:
							waterNum1=value;
							
							break;
						case FacialDuration.M2:
							waterNum2=value;
							
							break;	
						case FacialDuration.M3:
							waterNum2=value;
							
							break;
						case FacialDuration.M4:
							waterNum4=value;
							
							break;
						}
					}
				}).show();
				
	}
	
	private void judgeDuration(float waterValue){
		int blue=getResources().getColor(R.color.facial_blue);
		if(startTime==0){
			startTime=System.currentTimeMillis()+5*1000;
			
			@SuppressWarnings("deprecation")
			SharedPreferences SharedPreferences=getSharedPreferences("LOCAL_PUSH", Context.MODE_WORLD_WRITEABLE);
			Editor editor=SharedPreferences.edit();
			editor.putBoolean("push1", false);
			editor.putBoolean("push2", false);
			editor.putBoolean("push3", false);
			editor.commit();
			
			if(waterNum1==0){
				waterNum1=waterValue;
			}
			setSeekProgress(waterNum);
			tvFacialTestStep1.setTextColor(blue);
			tvFacialResult1.setTextColor(blue);
			tvFacialTestStepTime1.setTextColor(blue);
			viewResult1.setBackgroundColor(blue);
			tvFacialResult1.setText(String.format("%1.1f", waterNum1)+"%");
			textViewTestStep.setText("面膜时间");
			textViewFacialResult.setText(String.format("%1.1f", waterNum1)+"%");
			return;
		}
		long now=System.currentTimeMillis();
		
		
		long duration=now-startTime;
		
		if(duration<=FacialDuration.MianMoing){
			if(waterNum1==0){
				waterNum1=waterValue;
				setSeekProgress(waterNum);
				tvFacialTestStep1.setTextColor(blue);
				tvFacialResult1.setTextColor(blue);
				tvFacialTestStepTime1.setTextColor(blue);
				viewResult1.setBackgroundColor(blue);
				tvFacialResult1.setText(String.format("%1.1f", waterNum1)+"%");
				textViewTestStep.setText("面膜时间");
				textViewFacialResult.setText(String.format("%1.1f", waterNum1)+"%");
			}else{
				isSaveValue(textViewFacialResult,tvFacialResult1, waterValue, FacialDuration.M1);
			}
			
			
			
		}else if(duration<FacialDuration.JiShiBuShui&&duration>FacialDuration.MianMoing){
			if(waterNum2==0){
				waterNum2=waterValue;
				testTime2=System.currentTimeMillis();
				tvFacialTestStep2.setTextColor(blue);
				tvFacialResult2.setTextColor(blue);
				viewResult2.setBackgroundColor(blue);
				tvFacialTestStepTime2.setTextColor(blue);
				tvFacialResult2.setText(String.format("%1.1f", waterNum2)+"%");
				textViewTestStep.setText("即时补水");
				textViewFacialResult.setText(String.format("%1.1f", waterNum2)+"%");
				setSeekProgress(waterNum);
			}else{
				isSaveValue(textViewFacialResult,tvFacialResult2, waterValue,  FacialDuration.M2);
			}
			
			
			
			
		}else if(duration<FacialDuration.ChiXuBuShui){
			if(waterNum3==0){
				waterNum3=waterValue;
				testTime3=System.currentTimeMillis();
				tvFacialTestStep3.setTextColor(blue);
				tvFacialResult3.setTextColor(blue);
				tvFacialTestStepTime3.setTextColor(blue);
				viewResult3.setBackgroundColor(blue);
				tvFacialResult3.setText(String.format("%1.1f", waterNum3)+"%");
				textViewTestStep.setText("持续补水");
				textViewFacialResult.setText(String.format("%1.1f", waterNum3)+"%");
				setSeekProgress(waterNum);
			}else{
				isSaveValue(textViewFacialResult,tvFacialResult3, waterValue,  FacialDuration.M3);
			}
			
			
		}else if(duration<FacialDuration.YanShiBuShui){
			if(waterNum4==0){
				waterNum4=waterValue;
				testTime4=System.currentTimeMillis();
				tvFacialTestStep4.setTextColor(blue);
				tvFacialResult4.setTextColor(blue);
				tvFacialTestStepTime4.setTextColor(blue);
				viewResult4.setBackgroundColor(blue);
				tvFacialResult4.setText(String.format("%1.1f", waterNum4)+"%");
				textViewTestStep.setText("长效补水");
				textViewFacialResult.setText(String.format("%1.1f", waterNum4)+"%");
				setSeekProgress(waterNum);
			}else{
				isSaveValue(textViewFacialResult, tvFacialResult4,waterValue,  FacialDuration.M4);
			}
		}
	}
	
	
	private void initData() {
		
		audioProcess.setHandle(m_handler);
		audioProcess.start();
	}

	@Override
	public void finish() {
		super.finish();
		if(!isFinish){
			overridePendingTransition(R.anim.activity_enter_from_left,
					R.anim.activity_exit_to_right);
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonFacialEndTest:
			
			if(waterNum1==0||startTime==0){
				//AppToast.toastMsg(this, "小主，测试之后才能看结果哦~").show();
				//return;
			}
			
			
			FacialTestEntity facialTestEntity=getFacialTestEntity();
			
			Intent intent = new Intent(this, FacialmaskResultActivity.class);
			if(facialTestEntity!=null){
				intent.putExtra("FacialTestEntity", facialTestEntity);
			}
			
			
			if(facialComparaEntity!=null){
				intent.putExtra("facialComparaEntity", facialComparaEntity);
			}
			
			startActivity(intent);
			overridePendingTransition(R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			isFinish=true;
			finish();
			break;
		case R.id.backBtnFacialMaskTest:
			finish();

			break;
		}
	}

	private void initView() {
		findViewById(R.id.buttonFacialEndTest).setOnClickListener(this);
		findViewById(R.id.backBtnFacialMaskTest).setOnClickListener(this);

		tvFacialName = (TextView) findViewById(R.id.tvFacialName);
		textViewCurrentState1 = (TextView) findViewById(R.id.textViewCurrentState1);
		textViewCurrentState2 = (TextView) findViewById(R.id.textViewCurrentState2);
		textViewTestStep = (TextView) findViewById(R.id.textViewTestStep);
		textViewFacialResult = (TextView) findViewById(R.id.textViewFacialResult);
		tvFacialTestStep1 = (TextView) findViewById(R.id.tvFacialTestStep1);
		tvFacialTestStep2 = (TextView) findViewById(R.id.tvFacialTestStep2);
		tvFacialTestStep3 = (TextView) findViewById(R.id.tvFacialTestStep3);
		tvFacialTestStep4 = (TextView) findViewById(R.id.tvFacialTestStep4);
		
		tvFacialTestStepTime1=(TextView)findViewById(R.id.tvFacialTestStepTime1);
		tvFacialTestStepTime2=(TextView)findViewById(R.id.tvFacialTestStepTime2);
		tvFacialTestStepTime3=(TextView)findViewById(R.id.tvFacialTestStepTime3);
		tvFacialTestStepTime4=(TextView)findViewById(R.id.tvFacialTestStepTime4);

		tvFacialResult1 = (TextView) findViewById(R.id.tvFacialResult1);
		tvFacialResult2 = (TextView) findViewById(R.id.tvFacialResult2);
		tvFacialResult3 = (TextView) findViewById(R.id.tvFacialResult3);
		tvFacialResult4 = (TextView) findViewById(R.id.tvFacialResult4);

		viewResult1 = findViewById(R.id.viewResult1);
		viewResult2 = findViewById(R.id.viewResult2);
		viewResult3 = findViewById(R.id.viewResult3);
		viewResult4 = findViewById(R.id.viewResult4);

		ivRaiseIcon = (ImageView) findViewById(R.id.ivRaiseIcon);
		seekArc = (SeekArc) findViewById(R.id.seekArcFacial);
		seekArc.setArcWidth(5);
		seekArc.setProgressWidth(5);
		
		UserEntity user=DBService.getUserEntity();
		if(user!=null){
			selectedProduct=SaveSelectedProductUtil.getSelectProduct(user.getToken());
			facialComparaEntity=SaveComparaDtaUtil.getRecord(user.getToken());
		}
		
		
		if(selectedProduct!=null){
			tvFacialName.setText(selectedProduct.getPro_brand_name());
			if(user!=null){
				new GetCompleteProductService(this, HttpTagConstantUtils.GET_COMPLETE_PRODUCT, this).doGet(user.getToken(), selectedProduct.getProductId());
			}
		}
	}

	// 设置最大音
	private void setMaxVolume() {
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mnCurrentVolume = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
	}

	// 恢复音量
	private void resetVolume() {
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				mnCurrentVolume, 0);

	}

	// 播放wav
	private boolean playWav() {
		mSoundPool.play(mnSoundID, 1, 1, 0, 0, 1);
		return true;
	}

	private void headsetPlugStateChanged() {
		if (mbLinked) {
			setMaxVolume();
			audioProcess.resume();
		} else {
			resetVolume();
			audioProcess.pause();
		}
	}

	Handler han = new Handler();

	private void setSeekProgress(final int progress) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i <= progress; i++) {
					final int index = i;
					try {
						Thread.sleep(15);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					han.post(new Runnable() {

						@Override
						public void run() {
							seekArc.setProgress(index);
						}
					});
				}
			}
		}).start();
	}

	// 注册广播
	private void registerHeadsetPlugReceiver() {
		headsetPlugReceiver = new HeadsetPlugReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.HEADSET_PLUG");
		registerReceiver(headsetPlugReceiver, intentFilter);
	}

	// 接收广播监听耳机插拔事件
	public class HeadsetPlugReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.hasExtra("state")) {
				if (intent.getIntExtra("state", 0) == 0) {
					mbLinked = false;
					headsetPlugStateChanged();
				} else if (intent.getIntExtra("state", 0) == 1) {
					mbLinked = true;
					headsetPlugStateChanged();
				}
			}
		}
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		
		int t=(Integer)tag;
		switch(t){
		case HttpTagConstantUtils.UPLOAD_FACIAL_TESTNUM:
			try{
				facialComparaEntity=(FacialComparaEntity)result;
				if(facialComparaEntity!=null&&DBService.getUserEntity()!=null){
					facialComparaEntity.setToken(DBService.getUserEntity().getToken());
				}
				SaveComparaDtaUtil.save(facialComparaEntity);
				
			}catch(Exception e){
				
			}
			
			break;
		case HttpTagConstantUtils.GET_COMPLETE_PRODUCT:
			try{
				CompleteProductEntity completeProductEntity=(CompleteProductEntity)result;
				if(completeProductEntity!=null){
					String r=completeProductEntity.getJoinnum();
					textViewCurrentState1.setText("已经有"+r+"人测试");
				}
			}catch(Exception e){
				
			}
			
		}
	}
}
