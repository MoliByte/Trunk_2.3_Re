package com.skinrun.trunk.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.app.base.entity.LastTimeTestRecordEntity;
import com.app.base.entity.TestPostEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.MyApplication;
import com.app.service.PostSkinScoreService;
import com.baidu.mobstat.StatService;
import com.base.app.utils.DBService;
import com.base.app.utils.KVOEvents;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.beabox.hjy.tt.PurchaseDeviceActivity;
import com.beabox.hjy.tt.R;
import com.beabox.hjy.tt.TakePhotoActivity;
import com.beabox.hjy.tt.TestInstructActivity;
import com.beabox.hjy.tt.main.skintest.component.ComparePopupWindow;
import com.beabox.hjy.tt.main.skintest.component.ComparePopupWindow.ConfirmListener;
import com.beabox.hjy.tt.main.skintest.component.KVO.Observer;
import com.skinrun.trunk.facial.mask.test.PhotoType;
import com.skinrun.trunk.main.TestRecordPoster.IdPassInterface;
import com.skinrun.trunk.test.popuwindow.HintPopuWindow;
import com.skinrun.trunk.test.popuwindow.HintPopuWindow.HintListener;
import com.skinrun.trunk.test.popuwindow.SateMenuPopuWindow;
import com.skinrun.trunk.test.popuwindow.SateMenuPopuWindow.OnexpandListener;
import com.skinrun.trunk.test.popuwindow.TeachPopuWindow;
import com.skinrun.trunk.test.popuwindow.TeachPopuWindow.OnTeachListener;
import com.triggertrap.seekarc.SeekArc;
import com.umeng.analytics.MobclickAgent;


public class TestingSimpleFrag extends Fragment implements OnItemClickListener,
		OnClickListener, HttpAysnResultInterface, Observer, IdPassInterface {
	private ArrayList<Float> currentData;
	private ArrayList<Double> standardData;
	private MyAdapter myAdapter;
	private SeekArc showNumCir, seekArcOut, seekArc2, seekArcOut2, seekArc3,
			seekArcout3;
	private TextView standard, currentNum, currentIconName, skinScore,
			StandardSkinScoreText, hint, tvcategory, tvLastTime,
			textViewSelectFace;
	private ImageView currentIcon, selectFace, showStandardSkinScore,
			imageViewTrumpet;
	private LayoutParams currentParam;
	private android.widget.RelativeLayout.LayoutParams standardParam;
	private android.widget.FrameLayout.LayoutParams standardSkinScoreParam;
	private int width, whole, broder;
	private int clickTemp = 0;
	// 当前用户信息
	private UserEntity userInfo;
	// 标准数据提供者
	private StandardProvider standardProvider;
	// 测试部位标识
	private int partFlag = -1;
	// 判断使用次数
	private UseTimeProvider useTimeProvider;
	// 使用次数
	private int useTime = -1;
	private static int testTime = 0;
	private static final String TAG = "TestingSimpleFrag";

	private ComparePopupWindow comparePopupWindow;
	private HintPopuWindow hintPopuWindow;
	private TeachPopuWindow teachPopuWindow;
	private SateMenuPopuWindow sateMenuPopuWindow;

	private String skinComment;
	private boolean isVisble=true;
	
	private int TestId;
	
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initPopuWindow();
		prepare();
		// 注册观察者

		MyApplication.getInstance().getKvo().registerObserver(KVOEvents.TEST_RESULT, this);

		MyApplication.getInstance().getKvo().registerObserver(KVOEvents.TEST_PART, this);

		MyApplication.getInstance().getKvo().registerObserver(KVOEvents.USER_LOGININ, this);

		MyApplication.getInstance().getKvo().registerObserver(KVOEvents.DEVICE_IN, this);

	}

	private void initPopuWindow() {
		// 比较颜值

		comparePopupWindow = new ComparePopupWindow();
		comparePopupWindow.setConfirmListener(new ConfirmListener() {

			@Override
			public void onConfirm(ComparePopupWindow comparePopupWindow) {
				try{
					// 进入拍照页面
					Intent intent = new Intent(getActivity(),
							TakePhotoActivity.class);
					//float waterValues = 0, oilValues = 0, flexibleValues = 0;
					Bundle bundle = new Bundle();
					bundle.putInt("testPreDiff",
							TestTakePhotoValueUtils.testPreDiff);
					bundle.putInt("testScore", TestTakePhotoValueUtils.testScore);
					bundle.putString("shareTitle", "分享的主题");
					bundle.putString("shareContent", "分享的内容");
					bundle.putString("shareLinkUrl", "分享的链接");
					
					bundle.putFloat("waterValues", waterValues);
					bundle.putFloat("oilValues", oilValues);
					bundle.putFloat("flexibleValues", flexibleValues);
					bundle.putInt("partFlag", partFlag);
					bundle.putInt("TestId", TestId);
					
					Log.e(TAG, "============partFlag:"+partFlag);
					
					bundle.putInt("photoType", PhotoType.SKIN_PHOTO);
					intent.putExtras(bundle);

					startActivity(intent);
					getActivity().overridePendingTransition(
							R.anim.activity_enter_from_right,
							R.anim.activity_exit_to_left);
					
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					comparePopupWindow.dismiss();
				}
			}

			@Override
			public void onClosed() {
				comparePopupWindow.dismiss();
			}
		});
		// 前三次的提示
		hintPopuWindow = new HintPopuWindow();
		hintPopuWindow.setHintListener(new HintListener() {

			@Override
			public void onGoToBuy() {
				Intent intent = new Intent(getActivity(),
						PurchaseDeviceActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(
						R.anim.activity_enter_from_right,
						R.anim.activity_exit_to_left);
			}

			@Override
			public void onClose() {
				hintPopuWindow.dismiss();
			}
		});

		// 教会用法提示

		teachPopuWindow = new TeachPopuWindow();
		teachPopuWindow.setOnTeachListener(new OnTeachListener() {

			@Override
			public void onClose() {
				teachPopuWindow.dismiss();

			}
		});
		// 选择测试部位

		sateMenuPopuWindow = new SateMenuPopuWindow();
		sateMenuPopuWindow.setOnexpandListener(new OnexpandListener() {

			@Override
			public void onClickSide(int part) {
				partFlag = part;
				Log.e(TAG, "==========选择测试部位：" + part);
				setSelectFaceImage(part);
				sateMenuPopuWindow.dismiss();

				MyApplication.getInstance().getKvo()
						.fire(KVOEvents.TEST_PART, part);
			}

			@Override
			public void onClickCenter() {
				sateMenuPopuWindow.dismiss();
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.testing_main_fragment, null);
		view.findViewById(R.id.civIntroduce).setOnClickListener(this);
		initView(inflater, view);
		// 初始化上次记录
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				initLastRecod();
			}
		}, 500);
		return view;
	}

	@SuppressWarnings("deprecation")
	private void prepare() {
		useTimeProvider = new UseTimeProvider(getActivity()
				.getApplicationContext());
		if (useTime == -1) {
			useTime = useTimeProvider.getUseTime();
		}
		standardProvider = new StandardProvider(getActivity());

		currentData = new ArrayList<Float>();
		standardData = new ArrayList<Double>();
		standardData.add(0d);
		standardData.add(0d);
		standardData.add(0d);
		currentData.add(0f);
		currentData.add(0f);
		currentData.add(0f);
		// 获取屏幕宽高
		WindowManager wm = getActivity().getWindowManager();
		width = wm.getDefaultDisplay().getWidth();
		broder = dpToPx(getActivity(), 20);
		whole = width - 2 * broder;

	}

	// 上传颜值
	private void postSkinScore() {
		PostSkinScoreService service = new PostSkinScoreService(getActivity(),
				HttpTagConstantUtils.POST_TEST_RECORD, this);
		service.doPostSkinScore(userInfo.getToken(), getTestPart());
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	/*
	 * 保存最近一次的测试记录
	 */

	private void savaLastTestRecord(float waterValue, float oilValue,
			float flexibleValue) {
		LastTimeTestRecordEntity lastEntity = new LastTimeTestRecordEntity();
		lastEntity.setFlexible(flexibleValue);
		lastEntity.setOil(oilValue);
		lastEntity.setStandardOil((float) (standardData.get(1).doubleValue()));
		lastEntity.setStandardWater((float) (standardData.get(0).doubleValue()));

		long m = System.currentTimeMillis();

		lastEntity.setTime(m + "");
		lastEntity.setToken(userInfo.getToken());
		if (partFlag <= 3) {
			lastEntity.setTestPart(TestingPartUtils.FACEPART);
		} else {
			lastEntity.setTestPart(TestingPartUtils.HANDPART);
		}

		lastEntity.setArea(getTestPart());
		Log.e(TAG, "===========保存测试记录");
		lastEntity.setWater(waterValue);
		lastEntity.setCurrentSkinScore(getCurrentSkinScore(waterValue, oilValue));
		lastEntity.setStandardSkinScore((int) standardData.get(2).doubleValue());
		lastEntity.setSkinComment(skinComment);

		LastTestRecordSaver.saveRecord(lastEntity);
	}

	// 获取当前数据
	private void updataCurrentData(float waterValue, float oilValue,
			float flexibleValue) {
		currentData.clear();
		currentData.add(waterValue);
		currentData.add(oilValue);
		currentData.add(flexibleValue);
		myAdapter.notifyDataSetChanged();
	}

	// 上传或缓存测试记录
	private void saveAndPostRecord(float waterValue, float oilValue,
			float flexibleValue) {
		// 获取手机设备，系统信息类
		DeviceInfoProvider deviceInfoProvider = new DeviceInfoProvider();

		// 上传数据或者保存到本地
		TestPostEntity testPostEntity = new TestPostEntity();
		testPostEntity.setModule("skin_test");
		testPostEntity.setArea(getTestPart());
		testPostEntity.setWater((int) (waterValue) * 100);
		testPostEntity.setOil((int) (oilValue) * 100);
		testPostEntity.setElasticity((int) (flexibleValue * 100));
		testPostEntity.setLat(standardProvider.getWeatherEntity().getLat());
		testPostEntity.setLng(standardProvider.getWeatherEntity().getLng());
		testPostEntity.setTemperature(standardProvider.getWeatherEntity()
				.getTemperature());
		testPostEntity.setHumidity(standardProvider.getWeatherEntity()
				.getHumidity());
		testPostEntity.setUltraviolet(standardProvider.getWeatherEntity()
				.getUv());
		testPostEntity.setTest_time(getCurrentTime());
		testPostEntity.setCountry(standardProvider.getWeatherEntity()
				.getCountry());
		testPostEntity.setRegion(standardProvider.getWeatherEntity()
				.getRegion());
		testPostEntity.setSt_water((int) standardData.get(0).doubleValue());
		testPostEntity.setSt_oil((int) standardData.get(1).doubleValue());
		testPostEntity.setOs("android");
		testPostEntity.setOs_version(deviceInfoProvider.getOsVersion());
		testPostEntity.setApp_version(deviceInfoProvider
				.getAppVersion(getActivity()));
		testPostEntity.setDevice_info(deviceInfoProvider.getDeviceModel());
		testPostEntity.setAppid("android");
		try{
			testPostEntity.setSkin_type(Integer.parseInt(userInfo.getSkinType()));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		

		TestRecordPoster poster = new TestRecordPoster(getActivity(),
				HttpTagConstantUtils.POST_TEST_RECORD, userInfo,this);
		poster.postTestRecord(testPostEntity);
		

	}

	// 计算当前颜值
	private int getCurrentSkinScore(float waterValue, float oilValue) {
		return (int) (waterValue * 0.861 + oilValue * 0.75);
	}

	// 测试部位
	private String getTestPart() {
		switch (partFlag) {
		case TestingPartUtils.TFace:
			return "T";
		case TestingPartUtils.IceFace:
			return "E";
		case TestingPartUtils.UFace:
			return "U";
		case TestingPartUtils.OnHand:
			return "B";
		case TestingPartUtils.InHand:
			return "F";
		default:
			return null;
		}
	}

	private void setSelectFaceImage(int part) {
		switch (part) {
		case TestingPartUtils.TFace:
			selectFace.setImageResource(R.drawable.testing_part1);
			textViewSelectFace.setText("T区");
			break;
		case TestingPartUtils.IceFace:
			selectFace.setImageResource(R.drawable.testing_part2);
			textViewSelectFace.setText("眼周");
			break;
		case TestingPartUtils.UFace:
			selectFace.setImageResource(R.drawable.testing_part3);
			textViewSelectFace.setText("U区");
			break;
		case TestingPartUtils.OnHand:
			selectFace.setImageResource(R.drawable.testing_part5);
			textViewSelectFace.setText("手背");
			break;
		case TestingPartUtils.InHand:
			selectFace.setImageResource(R.drawable.testing_part4);
			textViewSelectFace.setText("手心");
			break;
		}
	}

	private void setSelectFaceImage(String area) {
		if (area.equals("U")) {
			selectFace.setImageResource(R.drawable.testing_part3);
			partFlag = TestingPartUtils.UFace;
			textViewSelectFace.setText("U区");
		} else if (area.equals("T")) {
			selectFace.setImageResource(R.drawable.testing_part1);
			partFlag = TestingPartUtils.TFace;
			textViewSelectFace.setText("T区");
		} else if (area.equals("E")) {
			selectFace.setImageResource(R.drawable.testing_part2);
			partFlag = TestingPartUtils.IceFace;
			textViewSelectFace.setText("眼周");
		} else if (area.equals("B")) {
			partFlag = TestingPartUtils.OnHand;
			selectFace.setImageResource(R.drawable.testing_part5);
			textViewSelectFace.setText("手背");
		} else if (area.equals("F")) {
			partFlag = TestingPartUtils.InHand;
			selectFace.setImageResource(R.drawable.testing_part4);
			textViewSelectFace.setText("手心");
		}

	}

	/*
	 * 获取时间
	 */
	@SuppressLint("SimpleDateFormat")
	private String getLastTime(long time) {
		Date date = new Date(time);
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		String timestring = f.format(date);
		return timestring;
	}

	// 得到用户上次测试记录
	private void initLastRecod() {

		LastTimeTestRecordEntity lr = null;
		if (userInfo != null) {
			lr = LastTestRecordSaver.getRecord(userInfo.getToken(),
					TestingPartUtils.FACEPART);

			if (lr == null) {
				lr = LastTestRecordSaver.getRecord(userInfo.getToken(),
						TestingPartUtils.HANDPART);
			}

			if (lr != null) {

				currentData.clear();
				currentData.add(lr.getWater());
				currentData.add(lr.getOil());
				currentData.add(lr.getFlexible());

				standardData.clear();
				standardData.add((double) (lr.getStandardWater()));
				standardData.add((double) (lr.getStandardOil()));
				setSeekBarProgressAndText(lr.getStandardSkinScore(),
						lr.getCurrentSkinScore());
				try {
					hint.setText(lr.getSkinComment());
					imageViewTrumpet.setVisibility(View.VISIBLE);
					
					setSelectFaceImage(lr.getArea());
					
					//tvLastTime.setText(translateTestTime(getLastTime())));
					long t=Long.parseLong(lr.getTime());
					
					tvLastTime.setText(ShowTimeUtil.getShowTime(t));
				}catch(Exception e){
					
				} 
			} else {
				try {
					setSeekBarProgressAndText(-10, -10);
					imageViewTrumpet.setVisibility(View.GONE);
					currentData.clear();
					currentData.add(0f);
					currentData.add(0f);
					currentData.add(0f);
					tvLastTime.setText("");
					standardData.clear();
					standardData.add((double) (0));
					standardData.add((double) (0));
					textViewSelectFace.setText("选择部位");
					selectFace.setImageResource(R.drawable.testing_select_face);
					hint.setText("");
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
		}
		try{
			myAdapter.notifyDataSetChanged();
			updata();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	// Post颜值的返回
	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		switch((Integer)tag){
		case HttpTagConstantUtils.POST_TEST_RECORD:
			
			
			break;
		}
	}

	@SuppressLint("SimpleDateFormat")
	private String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		Log.e(TAG, "=======系统当前时间：" + sdf.format(curDate));
		return sdf.format(curDate);
	}

	@Override
	public void onResume() {
		super.onResume();
		isVisble=true;
		MobclickAgent.onResume(getActivity());
		StatService.onResume(getActivity());

		
	}
	@Override
	public void onPause() {
		super.onPause();
		isVisble=false;
		MobclickAgent.onPause(getActivity());
		StatService.onPause(getActivity());
	}

	private void initView(LayoutInflater inflater, View view) {
		// 提示ImageView
		ImageView indicator = (ImageView) view
				.findViewById(R.id.imageView_testing_introduce);
		indicator.setOnClickListener(this);

		standard = (TextView) view.findViewById(R.id.textViewStandard);
		standard.setTextColor(getResources().getColor(
				R.color.test_water_num_standard));
		currentNum = (TextView) view.findViewById(R.id.seekArcProgress);
		currentNum
				.setTextColor(getResources().getColor(R.color.test_water_num));

		currentIcon = (ImageView) view.findViewById(R.id.imageView_show);
		showStandardSkinScore = (ImageView) view
				.findViewById(R.id.imageViewIndicate);
		currentIconName = (TextView) view.findViewById(R.id.textView_show);
		hint = (TextView) view.findViewById(R.id.textViewHint);
		imageViewTrumpet = (ImageView) view.findViewById(R.id.imageViewTrumpet);
		hint.setText("");
		tvLastTime = (TextView) view.findViewById(R.id.tvLastTime);
		tvcategory = (TextView) view.findViewById(R.id.tvcategory);

		showNumCir = (SeekArc) view.findViewById(R.id.seekArc);
		seekArcOut = (SeekArc) view.findViewById(R.id.seekArcOut);
		seekArc2 = (SeekArc) view.findViewById(R.id.seekArc2);
		seekArcOut2 = (SeekArc) view.findViewById(R.id.seekArcOut2);
		seekArc3 = (SeekArc) view.findViewById(R.id.seekArc3);
		seekArcout3 = (SeekArc) view.findViewById(R.id.seekArcOut3);

		showNumCir.setArcWidth(5);
		showNumCir.setProgressWidth(5);
		seekArc2.setArcWidth(5);
		seekArc2.setProgressWidth(5);
		seekArc3.setArcWidth(5);
		seekArc3.setProgressWidth(5);

		GridView gridView = (GridView) view.findViewById(R.id.gridViewTesting);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		selectFace = (ImageView) view.findViewById(R.id.imageViewSelectFace);
		selectFace.setOnClickListener(this);
		textViewSelectFace = (TextView) view
				.findViewById(R.id.textViewSelectFace);
		myAdapter = new MyAdapter();
		gridView.setAdapter(myAdapter);
		gridView.setOnItemClickListener(this);
		skinScore = (TextView) view.findViewById(R.id.textViewSkinScore);
		StandardSkinScoreText = (TextView) view
				.findViewById(R.id.textViewStandardSkinScore);
		// 布局参数
		currentParam = (LayoutParams) skinScore.getLayoutParams();
		standardParam = (android.widget.RelativeLayout.LayoutParams) StandardSkinScoreText
				.getLayoutParams();
		standardSkinScoreParam = (android.widget.FrameLayout.LayoutParams) showStandardSkinScore
				.getLayoutParams();
		// 三次以内出现提示
		if (useTimeProvider.getPopuWindowShowTime() <= 3) {

			try {
				hintPopuWindow.show(getActivity());
			} catch (Exception e) {

			}

		}
		userInfo = DBService.getUserEntity();

		setSeekBarProgressAndText(50, 60);
	}

	class MyAdapter extends BaseAdapter {
		// 标识选择的Item
		public void setSeclection(int position) {
			clickTemp = position;
		}

		@Override
		public int getCount() {
			return currentData.size();
		}

		@Override
		public Object getItem(int arg0) {
			return currentData.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return R.id.textViewTestingItem;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			MyHolder holder = null;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.testing_gridview_item, null);
				holder = new MyHolder();
				holder.tv = (TextView) convertView
						.findViewById(R.id.textViewTestingItem);
				holder.iv = (ImageView) convertView
						.findViewById(R.id.imageViewTestingItem);
				convertView.setTag(holder);
			} else {
				holder = (MyHolder) convertView.getTag();
			}

			if (arg0 == 2) {
				holder.tv
						.setText(String.format("%1.1f", currentData.get(arg0)));
			} else {
				holder.tv.setText(String.format("%1.1f", currentData.get(arg0))
						+ "%");
			}

			// 选中之后的判断
			switch (arg0) {
			case 0:
				if (0 == clickTemp) {
					holder.iv.setImageResource(R.drawable.water_select);
					holder.tv.setTextColor(getResources().getColor(
							R.color.test_water_num));
					currentIconName.setText("水份");
					currentIconName.setTextColor(getResources().getColor(
							R.color.test_water_text));
					currentIcon.setImageResource(R.drawable.water_small_icon);

					break;
				}
				holder.tv.setTextColor(getResources().getColor(
						R.color.test_gray));
				holder.iv.setImageResource(R.drawable.water_noselect);
				break;
			case 1:
				if (1 == clickTemp) {
					holder.tv.setTextColor(getResources().getColor(
							R.color.test_oil_num));
					currentIconName.setText("油份");
					currentIconName.setTextColor(getResources().getColor(
							R.color.test_oil_text));
					currentIcon.setImageResource(R.drawable.oil_small_icon);
					holder.iv.setImageResource(R.drawable.oil_select);
					break;
				}
				holder.tv.setTextColor(getResources().getColor(
						R.color.test_gray));
				holder.iv.setImageResource(R.drawable.oil_noselect);
				break;
			case 2:
				if (2 == clickTemp) {
					currentIconName.setText("弹性");
					holder.tv.setTextColor(getResources().getColor(
							R.color.test_flex_num));
					currentIcon
							.setImageResource(R.drawable.flexible_small_icon);
					currentIconName.setTextColor(getResources().getColor(
							R.color.test_flex_text));
					holder.iv.setImageResource(R.drawable.flexible_select);
					break;
				}
				holder.tv.setTextColor(getResources().getColor(
						R.color.test_gray));
				holder.iv.setImageResource(R.drawable.flexible_noselect);
				break;
			}
			return convertView;
		}
	}

	/**
	 * 
	 * holder
	 */
	class MyHolder {
		TextView tv;
		ImageView iv;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		clickTemp = arg2;
		myAdapter.notifyDataSetChanged();
		updata();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		// 提示的图标点击事件
		case R.id.imageView_testing_introduce:
			try {
				teachPopuWindow.show(getActivity());
			} catch (Exception e) {

			}

			break;
		case R.id.imageViewSelectFace:
			try {
				sateMenuPopuWindow.show(getActivity());
			} catch (Exception e) {

			}

			break;

		case R.id.civIntroduce:
			Intent intent = new Intent(getActivity(),
					TestInstructActivity.class);
			intent.putExtra("CONTENT_TYPE", 1);
			startActivity(intent);
			getActivity().overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);

			break;

		}
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	private static int dpToPx(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	Handler handler = new Handler();

	// 设置圆弧的进度
	private void setCirProgress(final int num, final SeekArc seekArc) {

		seekArc.setProgress(0);

		new Thread(new Runnable() {
			public void run() {

				for (int i = 0; i <= num; i++) {
					final int progress = i;
					if (i < num * 2 / 3) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					} else {
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					handler.post(new Runnable() {

						@Override
						public void run() {
							seekArc.setProgress(progress);
						}
					});
				}
			}
		}).start();
	}

	// 设置标准颜值图标进度以及肌肤评价
	private void setSeekBarProgressAndText(final int num, final int curScore) {
		try {
			standardSkinScoreParam.leftMargin = (int) (whole * num / 100 - showStandardSkinScore
					.getWidth() / 2);
			showStandardSkinScore.setLayoutParams(standardSkinScoreParam);
			skinScore.setText(curScore + "");
			currentParam.leftMargin = (int) (whole * curScore / 100);
			standardParam.leftMargin = (int) (whole * num / 100 - StandardSkinScoreText
					.getWidth() / 2);
			skinScore.setLayoutParams(currentParam);
			StandardSkinScoreText.setLayoutParams(standardParam);
			// 位移，渐变动画
			Animation translateAnim = AnimationUtils.loadAnimation(
					getActivity(), R.anim.test_translate_anim);
			// StandardSkinScoreText.setVisibility(View.VISIBLE);
			// showStandardSkinScore.setVisibility(View.VISIBLE);
			// StandardSkinScoreText.startAnimation(translateAnim);
			// showStandardSkinScore.startAnimation(translateAnim);
			skinScore.startAnimation(translateAnim);

		} catch (Exception e) {

		}
	}

	// 更新页面
	private void updata() {
		// 圆环显示

		if (clickTemp == 2) {
			currentNum.setText(String.format("%1.1f",
					currentData.get(clickTemp)));
			standard.setText("");
			standard.setVisibility(View.INVISIBLE);
			showNumCir.setVisibility(View.INVISIBLE);
			seekArcOut.setVisibility(View.INVISIBLE);
			seekArc2.setVisibility(View.INVISIBLE);
			seekArcOut2.setVisibility(View.INVISIBLE);
			seekArc3.setVisibility(View.VISIBLE);
			seekArcout3.setVisibility(View.VISIBLE);
			setCirProgress(100, seekArc3);
			currentNum.setTextColor(getResources().getColor(
					R.color.test_flex_num));
		} else {
			currentNum.setText(String.format("%1.1f",
					currentData.get(clickTemp))
					+ "%");
			switch (clickTemp) {
			case 0:
				showNumCir.setVisibility(View.VISIBLE);
				seekArcOut.setVisibility(View.VISIBLE);
				seekArc2.setVisibility(View.INVISIBLE);
				seekArcOut2.setVisibility(View.INVISIBLE);
				seekArc3.setVisibility(View.INVISIBLE);
				seekArcout3.setVisibility(View.INVISIBLE);
				currentNum.setTextColor(getResources().getColor(
						R.color.test_water_num));
				standard.setTextColor(getResources().getColor(
						R.color.test_water_num_standard));
				break;
			case 1:
				showNumCir.setVisibility(View.INVISIBLE);
				seekArcOut.setVisibility(View.INVISIBLE);
				seekArc2.setVisibility(View.VISIBLE);
				seekArcOut2.setVisibility(View.VISIBLE);
				seekArc3.setVisibility(View.INVISIBLE);
				seekArcout3.setVisibility(View.INVISIBLE);
				currentNum.setTextColor(getResources().getColor(
						R.color.test_oil_num));
				standard.setTextColor(getResources().getColor(
						R.color.test_oil_num_standard));
				break;
			}
			standard.setVisibility(View.VISIBLE);
			standard.setText("标准"
					+ String.format("%1.1f", standardData.get(clickTemp)) + "%");
			int cirNum = 0;
			if ((float) currentData.get(clickTemp) >= (standardData
					.get(clickTemp))) {
				cirNum = 100;
			} else if ((float) currentData.get(clickTemp) >= (standardData
					.get(clickTemp)) * 0.9) {
				cirNum = 100 * 5 / 6;
			} else {
				cirNum = 100 * 2 / 3;
			}
			switch (clickTemp) {
			case 0:
				setCirProgress(cirNum, showNumCir);
				break;
			case 1:
				setCirProgress(cirNum, seekArc2);
				break;
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (comparePopupWindow != null) {
			comparePopupWindow.destory();
		}
		// comparePopupWindow = null;

		if (hintPopuWindow != null) {
			hintPopuWindow.destory();
		}
		// hintPopuWindow = null;

		if (teachPopuWindow != null) {
			teachPopuWindow.destroy();
		}
		// teachPopuWindow = null;

		if (sateMenuPopuWindow != null) {
			sateMenuPopuWindow.destroy();
		}
		// sateMenuPopuWindow = null;

		MyApplication.getInstance().getKvo()
				.removeObserver(KVOEvents.USER_LOGININ, this);

		MyApplication.getInstance().getKvo()
				.removeObserver(KVOEvents.TEST_RESULT, this);
		MyApplication.getInstance().getKvo()
				.removeObserver(KVOEvents.TEST_PART, this);
		MyApplication.getInstance().getKvo()
				.removeObserver(KVOEvents.DEVICE_IN, this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		//isVisble=false;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		//isVisble=true;
	}
	
	
	float waterValues = 0, oilValues = 0, flexibleValues = 0;
	// 接收测试数据
	@Override
	public void onEvent(String event, Object... args) {

		
		if (event.equals(KVOEvents.TEST_RESULT)) {
			if(!isVisble){
				return;
			}
			
			
			
			// 单次登录测试次数
			testTime++;
			tvLastTime.setText(ShowTimeUtil.getShowTime(System
					.currentTimeMillis()));
			waterValues = (Float) (args[0]);
			oilValues = (Float) args[1];
			flexibleValues = (Float) args[2];
			imageViewTrumpet.setVisibility(View.VISIBLE);
			// 判断是不是第一次测试，如果不是第一次则必须先选择测试部位
			if (partFlag == -1) {
				if (testTime == 1 && useTime == 1) {
					// 更新当前数据
					updataCurrentData(waterValues, oilValues, flexibleValues);

					// 获取标准值
					standardData = standardProvider.getStandardValue(userInfo);
					// 设置当前颜值与标准颜值
					setSeekBarProgressAndText((int) standardData.get(2)
							.doubleValue(),
							getCurrentSkinScore(waterValues, oilValues));
					// 更新页面
					updata();
					// 更新肌肤评价
					// 更新肌肤评价
					if(partFlag<=3){
						skinComment = standardProvider.getSkinComment(userInfo,
								getCurrentSkinScore(waterValues, oilValues));
					}else{
						skinComment=standardProvider.getHandComment(userInfo, getCurrentSkinScore(waterValues, oilValues));
					}
					hint.setText(skinComment);

				} else {
					final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder
							.getInstance(getActivity());

					dialogBuilder
							.withTitle(null)
							.withMessage(null)
							.withMessageColor("#FFFFFFFF")
							.isCancelableOnTouchOutside(false)
							.withDuration(200)
							.withEffect(Effectstype.Fadein)
							.withButton1Text("我知道了")
							.setCustomView(
									R.layout.dialog_hint_select_testpart,
									getActivity())
							.setButton1Click(new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									dialogBuilder.dismiss();
								}
							}).show();
				}

			} else {
				// 更新当前数据
				updataCurrentData(waterValues, oilValues, flexibleValues);
				// 获取标准值
				standardData = standardProvider.getStandardValue(userInfo);
				// 设置当前颜值与标准颜值

				setSeekBarProgressAndText((int) standardData.get(2)
						.doubleValue(),
						getCurrentSkinScore(waterValues, oilValues));
				// 更新页面
				updata();
				// 上传数据
				saveAndPostRecord(waterValues, oilValues, flexibleValues);

				// 更新肌肤评价
				if(partFlag<=3){
					skinComment = standardProvider.getSkinComment(userInfo,
							getCurrentSkinScore(waterValues, oilValues));
				}else{
					skinComment=standardProvider.getHandComment(userInfo, getCurrentSkinScore(waterValues, oilValues));
				}
				hint.setText(skinComment);

				LastTimeTestRecordEntity lr = null;
				if (partFlag <= 3) {
					lr = LastTestRecordSaver.getRecord(userInfo.getToken(),
							TestingPartUtils.FACEPART);
				} else {
					lr = LastTestRecordSaver.getRecord(userInfo.getToken(),
							TestingPartUtils.HANDPART);
				}

				if (lr != null) {
					TestTakePhotoValueUtils.testPreDiff = getCurrentSkinScore(
							waterValues, oilValues) - lr.getCurrentSkinScore();

				} else {
					TestTakePhotoValueUtils.testPreDiff = 0;
				}
				TestTakePhotoValueUtils.testScore = getCurrentSkinScore(
						waterValues, oilValues);

				// 保存最近一次测试记录到本地
				savaLastTestRecord(waterValues, oilValues, flexibleValues);

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						comparePopupWindow.show(getActivity(),
								TestTakePhotoValueUtils.testPreDiff,
								TestTakePhotoValueUtils.testScore);
					}
				}, 3000);
			}

		} else if (event.equals(KVOEvents.USER_LOGININ)) {
			userInfo = DBService.getUserEntity();
			if (userInfo != null) {
				initLastRecod();
			}

		} else if (event.equals(KVOEvents.DEVICE_IN)) {
			int tag = (Integer) args[0];
			if (tag == 0) {
				tvcategory.setText("请连接肌肤管家");
			} else {
				tvcategory.setText("启动开机按钮，将测试头紧贴皮肤开始测试");
			}

		}
	}

	@Override
	public void getId(int id) {
		TestId=id;
		// 上传颜值
		postSkinScore();
	}
}
