package com.skinrun.trunk.main;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.app.base.entity.UserEntity;
import com.app.base.init.MyApplication;
import com.baidu.mobstat.StatService;
import com.base.app.utils.DBService;
import com.base.app.utils.KVOEvents;
import com.base.app.utils.ProgressDialogUtil;
import com.base.app.utils.StringUtil;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
import com.beabox.hjy.tt.TakePhotoActivity;
import com.beabox.hjy.tt.main.skintest.component.KVO.Observer;
import com.skinrun.trunk.facial.mask.test.PhotoType;
import com.umeng.analytics.MobclickAgent;


public class TestingFrag extends Fragment implements OnClickListener,
		HttpAysnResultInterface, OnPageChangeListener, OnCheckedChangeListener,
		Observer {
	private RadioGroup mRadioGroup;
	private ViewPager mViewpager;
	private ArrayList<Fragment> fragments;
	private TextView textViewTitle;
	private View login_view;
	private View testView;
	private UserEntity userInfo;
	private float waterValue = -1, oilValue = -1;

	private static final String TAG = "TestingFrag";
	Timer m_timer1; // 定时器
	Handler m_handler;
	int mnTestCount = 0;
	private SoundPool mSoundPool;
	int mnSoundID = 0;
	int mnCurrentVolume; // 当前竟量
	boolean mbLinked = false; // 是否连接设备
	AudioRecord audioRecord; // 播放声音
	AudioProcess audioProcess =MyApplication.getInsProcess();
	HeadsetPlugReceiver headsetPlugReceiver = null;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUmeng();
		MyApplication.getInstance().getKvo()
				.registerObserver(KVOEvents.TEST_RESULT, this);
		MyApplication.getInstance().getKvo()
				.registerObserver(KVOEvents.USER_LOGININ, this);
		MyApplication.getInstance().getKvo()
				.registerObserver(KVOEvents.DEVICE_IN, this);

		dialog = ProgressDialogUtil.show(getActivity(), false);
		init();

		

		if (!isLogin()) {

			/*new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					SkinRunMainActivity s = (SkinRunMainActivity) getActivity();
					s.goToFrag(4);

				}
			}, 1000);*/

		}
	}

	@Override
	public void onStart() {
		super.onStart();
		
	}

	@Override
	public void onResume() {
		super.onResume();
		// 判断是否登录
		Log.e(TAG, "MyApplication.resume_index = "+MyApplication.resume_index);
		 MyApplication.resume_index = 2 ;
		if (isLogin()) {
			testView.setVisibility(View.VISIBLE);
			login_view.setVisibility(View.GONE);
		} else {
			login_view.setVisibility(View.VISIBLE);
			testView.setVisibility(View.GONE);
		}
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		MobclickAgent.onPageStart(getActivityName()); // 统计页面
		MobclickAgent.onResume(getActivity());
		StatService.onResume(getActivity());
		//audioProcess = new com.skinrun.trunk.main.AudioProcess();
		init2();
		initData();
		audioProcess.resume();
		setMaxVolume();
	}

	@Override
	public void onStop() {
		super.onStop();
		
	}
	@Override
	public void onPause() {
		super.onPause();
		
		audioProcess.pause();
		getActivity().unregisterReceiver(headsetPlugReceiver);
		m_timer1.cancel();
		audioProcess.stop();
		resetVolume(); // 恢复音量
		mSoundPool.release();
		
		// 页面结束
		if (!StringUtil.isEmpty(getActivityName())) {

			MobclickAgent.onPageEnd(getActivityName()); // 保证 onPageEnd 在onPause
														// 之前调用,因为 onPause
														// 中会保存信息
			MobclickAgent.onPause(getActivity());
			StatService.onPause(getActivity());
		}
	}
	
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		MyApplication.resume_index = 2 ;
		try {
			
			// 取消注册观察者
			MyApplication.getInstance().getKvo()
			.removeObserver(KVOEvents.TEST_RESULT, this);
			MyApplication.getInstance().getKvo()
					.removeObserver(KVOEvents.USER_LOGININ, this);
			MyApplication.getInstance().getKvo()
					.removeObserver(KVOEvents.DEVICE_IN, this);
		} catch (Exception e) {
			
		}catch (OutOfMemoryError e) {
			
		}
		
	}

	private void init() {
		fragments = new ArrayList<Fragment>();
		fragments.add(new TestingSimpleFrag());
		fragments.add(new TestingDeepFrag());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.test_main, null);
		login_view = (View) view.findViewById(R.id.loginView);
		testView = (View) view.findViewById(R.id.testView);
		getTestView(view);
		getLoginView(view);
		if (isLogin()) {
			testView.setVisibility(View.VISIBLE);
			login_view.setVisibility(View.GONE);
		} else {
			login_view.setVisibility(View.VISIBLE);
			testView.setVisibility(View.GONE);
		}
		return view;
	}

	// 解析登录界面
	private void getLoginView(View view) {

	}

	// 计算当前颜值
	private int getCurrentSkinScore(float waterValue, float oilValue) {
		return (int) (waterValue * 0.861 + oilValue * 0.75);
	}

	// 解析测试界面
	private void getTestView(View view) {

		textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
		ImageView ibShare = (ImageView) view
				.findViewById(R.id.imageViewShare);
		view.findViewById(R.id.test_login_btn).setOnClickListener(this);

		ibShare.setImageResource(R.drawable.share_icon_60);
		textViewTitle.setText("测试");
		mViewpager = (ViewPager) view.findViewById(R.id.viewPager_testing);
		mRadioGroup = (RadioGroup) view.findViewById(R.id.radioGroup_testing);
		mViewpager.setAdapter(new MyAdapter(getActivity()
				.getSupportFragmentManager()));
		mViewpager.setOnPageChangeListener(this);
		mRadioGroup.setOnCheckedChangeListener(this);

		ibShare.setOnClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageViewShare:

			try {
				Intent intent = new Intent(getActivity(),
						TakePhotoActivity.class);
				Bundle bundle = new Bundle();

				if (waterValue == -1 || oilValue == -1) {
					bundle.putInt("testPreDiff", 0);
					bundle.putInt("testScore", 0);
					AppToast.toastMsg(getActivity(), "好友更期待您本次测试的结果哦").show();

				} else {
					bundle.putInt("testPreDiff",
							TestTakePhotoValueUtils.testPreDiff);
					bundle.putInt("testScore",
							TestTakePhotoValueUtils.testScore);
				}
				bundle.putInt("photoType", PhotoType.SKIN_PHOTO);
				bundle.putString("shareTitle", "分享的主题");
				bundle.putString("shareContent", "分享的内容");
				bundle.putString("shareLinkUrl", "分享的链接");
				intent.putExtras(bundle);

				startActivity(intent);
				getActivity().overridePendingTransition(
						R.anim.activity_enter_from_right,
						R.anim.activity_exit_to_left);

			} catch (Exception e) {

			}

			break;
		/*case R.id.test_login_btn:
			SkinRunMainActivity s = (SkinRunMainActivity) getActivity();
			s.goToFrag(4);

			break;*/
		}
	}

	// 判断是否登录
	private boolean isLogin() {
		// 获取用户信息
		userInfo = DBService.getUserEntity();

		if (userInfo == null) {
			return false;
		} else {
			MyApplication.getInstance().getKvo()
					.fire(KVOEvents.USER_LOGININ, "LOGIN_SUCCESS");
			Log.e("===========", "==========用户地点" + userInfo.getRegion());
			return true;
		}
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		try {

		} catch (Exception e) {

		} finally {
			/*new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					SkinRunMainActivity s = (SkinRunMainActivity) getActivity();
					s.goToFrag(4);
				}
			}, 1000);*/
		}

	}

	// 适配器
	class MyAdapter extends FragmentPagerAdapter {

		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragments.get(arg0);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		RadioButton rb = (RadioButton) mRadioGroup.getChildAt(arg0);
		rb.setChecked(true);
		if (arg0 == 0) {
			textViewTitle.setText("测试");
		} else {
			textViewTitle.setText("深度报告");
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {

	}

	// 设置最大音
	private void setMaxVolume() {
		AudioManager mAudioManager = (AudioManager) getActivity()
				.getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mnCurrentVolume = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
	}

	// 恢复音量
	private void resetVolume() {
		AudioManager mAudioManager = (AudioManager) getActivity()
				.getSystemService(Context.AUDIO_SERVICE);
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
			MyApplication.getInstance().getKvo().fire(KVOEvents.DEVICE_IN, 1);
			setMaxVolume();
			audioProcess.resume();
		} else {
			MyApplication.getInstance().getKvo().fire(KVOEvents.DEVICE_IN, 0);
			resetVolume();
			audioProcess.pause();
		}
	}
	
	// 注册广播
	private void registerHeadsetPlugReceiver() {
		headsetPlugReceiver = new HeadsetPlugReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.HEADSET_PLUG");
		getActivity().registerReceiver(headsetPlugReceiver, intentFilter);
	}

	// 接收广播监听耳机插拔事件
	public class HeadsetPlugReceiver extends BroadcastReceiver {
		private static final String TAG = "HeadsetPlugReceiver";

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

	private void initData() {
		audioProcess.setHandle(m_handler);
		audioProcess.start();
	}

	private void init2() {
		mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		mnSoundID = mSoundPool.load(getActivity(), R.raw.send_data, 1);
		AudioManager mAudioManager = (AudioManager) getActivity()
				.getSystemService(Context.AUDIO_SERVICE);
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
				super.handleMessage(msg);
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
					/*
					 * boolean bLinked = audioProcess.isLinked(); if(bLinked !=
					 * mbLinked) { mbLinked = bLinked;
					 * ivAudio.setImageResource(mbLinked ? R.drawable.audio1 :
					 * R.drawable.audio2); if(mbLinked) { setMaxVolume(); } else
					 * { resetVolume(); } //btnTest.setEnabled(mbLinked); }
					 */
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
					
					
					Log.e(TAG, "============肌肤测试页面水油弹："+fWater+"  ");
					
					if (fWater > 80 || fWater < 15) {
						Random random = new Random();
						fWater = (float) (random.nextInt(30) + 30 + random
								.nextInt(9) * 0.1);
						fOil = (float) (fWater / 2.3);
						fTX = (float) ((fOil / fWater + fWater - fOil) * 1.25);
					}

					waterValue = fWater;
					oilValue = fOil;

					MyApplication.getInstance().getKvo()
							.fire(KVOEvents.TEST_RESULT, fWater, fOil, fTX);

					break;
				}
			}
		};
	}

	// 接收测试结果
	@Override
	public void onEvent(String event, Object... args) {
		if (event.equals(KVOEvents.USER_LOGININ)) {
			if (testView != null) {
				testView.setVisibility(View.VISIBLE);
			}
			if (login_view != null) {
				login_view.setVisibility(View.GONE);
			}

			userInfo = DBService.getUserEntity();
		}
	}

	public String getActivityName() {
		return "测试页面";
	}

	private void initUmeng() {
		MobclickAgent.setDebugMode(false);
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(getActivity());
	}

	/**
	 * 自定义事件统计
	 * 
	 * @param eventCode
	 * @param eventName
	 */
	public void logEvent(String eventCode, String eventName) {
		MobclickAgent.onEvent(getActivity(), eventCode);
	}

	

}
