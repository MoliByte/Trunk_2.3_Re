package com.beabox.hjy.tt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.app.base.entity.RegionsEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.ACache;
import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.custom.view.DateTimePickDialogUtil;
import com.app.service.AvatarUploadTaskService;
import com.avos.avoscloud.LogUtil.log;
import com.avoscloud.chat.base.ChatMsgAdapter;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.Logger;
import com.avoscloud.chat.util.PathUtils;
import com.avoscloud.chat.util.PhotoUtils;
import com.avoscloud.chat.util.Utils;
import com.base.app.utils.DBService;
import com.base.app.utils.FileUtil;
import com.base.app.utils.HomeTag;
import com.base.app.utils.KVOEvents;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.main.skintest.component.KVO.Observer;
import com.idongler.image.ImageUtil;
import com.idongler.widgets.CircleImageView;
import com.imagechooser.ui.SelectorImageMainActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.skinrun.trunk.facial.mask.test.FacialmaskEntryActivity;
import com.skinrun.wheel.adapter.AbstractWheelTextAdapter;
import com.skinrun.wheel.city.cascade.model.CityModel;
import com.skinrun.wheel.city.cascade.model.DistrictModel;
import com.skinrun.wheel.city.cascade.model.ProvinceModel;
import com.skinrun.wheel.city.cascade.service.XmlParserHandler;
import com.skinrun.wheel.city.widget.OnWheelChangedListener;
import com.skinrun.wheel.city.widget.WheelViewCity;
import com.skinrun.wheel.city.widget.adapters.ArrayWheelAdapter;
import com.skinrun.wheel.view.OnWheelScrollListener;
import com.skinrun.wheel.view.WheelView;
import com.umeng.message.PushAgent;

/**
 * 修改个人信息页面
 * 
 * @author zhup
 * 
 */
public class UpdateBaseInfoActivity extends BaseActivity implements
		OnWheelChangedListener, Observer {
	private final static String TAG = "UpdateBaseInfoActivity";
	ImageView backBtn;
	CircleImageView avatarImg;
	TextView nickname;
	TextView birthday;
	TextView sex;
	TextView skin_type;
	TextView region;
	// 点击修改昵称
	View update_nick_name;

	// 生日滚轮
	private View birthdayView;// birthday wheel
	private WheelView startYearView, startMonthView, startDayView;
	private String[] years, months, days;
	private String startYear = "", startMonth = "", startDay = "";
	private Dialog startDialog, regionDialog, skinTypeDialog, sexDialog,selectPhotoDialog;
	private List<String> startYearList, startMonthList, startDayList;

	private YearAdapter startYearAdapter;
	private MonthAdapter startMonthAdapter;
	private DayAdapter startDayAdapter;
	private final int MIN_YEAR = 1970;
	private int max_Year = 2100;
	private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
	private SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
	private SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
	private int startYearIndex, startMonthIndex, startDayIndex;
	private Resources res;
	private Button button, startDone, startCancel;

	// 区域滚轮
	private View regionView;// regions wheel
	private View skinTypeView;// regions wheel
	private View sexView;// regions wheel

	private WheelViewCity mViewProvince;
	private WheelViewCity mViewCity;
	private WheelViewCity mViewDistrict;

	private WheelViewCity mViewSkinType;
	private WheelViewCity mViewSex;

	private Button cityDone, cityCancel;
	private Button skinDone, skinCancel;
	private Button sexDone, sexCancel;
	private String region_code;
	// 拍照图片上传
	Activity activity;
	FileUtil fileUtil;
	UserEntity entity;

	private File file;
	private final static int REQUEST_CODE = 2;// take a picture successfully!
	Bitmap photo;
	String pictureDir = "";// 图片存储路径

	Uri aUri;
	String saveDir = "";
	static String upload_path = "";
	private PopupWindow popupWindow1;
	private PopupWindow popupWindowCity;

	final static int widthHeight = 120;// 裁剪大小
	final static int thumb = 120;// 缩略图大小

	NiftyDialogBuilder dialogUploadImage;
	private View birthday_layout;
	private View regionView_layout;
	private View sex_layout;
	private View skin_type_layout;

	private String initEndDateTime = "";
	String token = "" ;
	ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
	
	//来源，肌肤测试还是面膜测试
	private String TestType="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
    	
    	MyApplication.getInstance().getKvo().registerObserver(KVOEvents.ACTION_AFTER_INFO, this);
    	TestType=getIntent().getStringExtra("ACTION_TYPE");
    	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_base_info);
		activity = this;
		fileUtil = new FileUtil(this);
		entity = DBService.getUserEntity() ;//DBService.getEntityByToken(getToken());
		if (entity != null) {
			initEndDateTime = entity.getBirthday();
			region_code = entity.getRegion() ;//aCache.getAsString("region");
		}
		token = ACache.get(UpdateBaseInfoActivity.this).getAsString("Token");
		Log.e(TAG, "token = "+token);
		setupView();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyApplication.getInstance().getKvo().removeObserver(KVOEvents.ACTION_AFTER_INFO, this);
	}

	@Override
	public void setupView() {
		avatarImg = (CircleImageView) findViewById(R.id.avatarImg);
		backBtn = (ImageView) findViewById(R.id.backBtn);
		nickname = (TextView) findViewById(R.id.nick_name);
		birthday = (TextView) findViewById(R.id.birthday);
		skin_type = (TextView) findViewById(R.id.skin_type);
		region = (TextView) findViewById(R.id.region);
		sex = (TextView) findViewById(R.id.sex);
		update_nick_name = findViewById(R.id.update_nick_name);
		birthday_layout = findViewById(R.id.birthday_layout);
		regionView_layout = findViewById(R.id.regionView_layout);
		skin_type_layout = findViewById(R.id.skin_type_layout);
		sex_layout = findViewById(R.id.sex_layout);

		birthdayView = LayoutInflater.from(this).inflate(R.layout.wheel_view,
				null);

		startYearView = (WheelView) birthdayView.findViewById(R.id.year);
		startMonthView = (WheelView) birthdayView.findViewById(R.id.month);
		startDayView = (WheelView) birthdayView.findViewById(R.id.day);
		startDone = (Button) birthdayView.findViewById(R.id.done);
		startCancel = (Button) birthdayView
				.findViewById(R.id.whell_view_textview);

		if(entity != null){
			nickname.setText("" + entity.getNiakname());
			birthday.setText("" + entity.getBirthday());
			skin_type.setText(""
					+ HttpTagConstantUtils.getSkinTypeByKey(entity.getSkinType()));
			//region.setText("" + entity.getRegion());
			String gender = aCache.getAsString("gender");
			sex.setText("1".equals(gender) ? "男" : "女");
			String avatar = entity.getAvatar();
			Log.e(TAG, "UpdateBaseInfo ------->" + avatar);
			
			UserService.imageLoader.displayImage(
					avatar + "?time=" + System.currentTimeMillis(), avatarImg,
					PhotoUtils.myPicImageOptions);
			
		}else{
			UserService.imageLoader.displayImage(
					"", avatarImg,
					PhotoUtils.myPicImageOptions);
		}
		// KJBitmap.create().display(avatarImg, avatar);

		avatarImg.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		update_nick_name.setOnClickListener(this);
		skin_type_layout.setOnClickListener(this);
		sex_layout.setOnClickListener(this);
		regionView_layout.setOnClickListener(this);
		findViewById(R.id.comfirmBtn).setOnClickListener(this);

		Runnable task = new Runnable() {
			@Override
			public void run() {
				// 初始化生日数据
				initDatePicker();
				// initRegionData();
				initProvinceDatas();
			}
		};
		new Handler().post(task);
		// setUpData();
	}

	private void initDatePicker() {
		res = getResources();
		months = res.getStringArray(R.array.months);
		days = res.getStringArray(R.array.days_31);
		final Date date = new Date();
		String year = yearFormat.format(date);
		max_Year = Integer.parseInt(year);
		startYearList = new ArrayList<String>();
		for (int i = MIN_YEAR; i <= max_Year; i++) {
			startYearList.add(i + "");
		}

		startMonthList = Arrays.asList(months);
		startDayList = Arrays.asList(days);

		startYearAdapter = new YearAdapter(this, startYearList);
		startMonthAdapter = new MonthAdapter(this, startMonthList);
		startDayAdapter = new DayAdapter(this, startDayList);
		startYearView.setViewAdapter(startYearAdapter);
		startMonthView.setViewAdapter(startMonthAdapter);
		startDayView.setViewAdapter(startDayAdapter);

		startDialog = new Dialog(this);
		startDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		startDialog.setContentView(birthdayView);
		Window startWindow = startDialog.getWindow();
		startWindow.setGravity(Gravity.BOTTOM);
		startWindow.setLayout(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		startWindow.setWindowAnimations(R.style.view_animation);
		startWindow.setBackgroundDrawable(dw);
		startDialog.setCanceledOnTouchOutside(false);
		try {
			startYear = birthday.getText().toString().split("-")[0];
			startMonth = birthday.getText().toString().split("-")[1];
			startDay = birthday.getText().toString().split("-")[2];
		} catch (Exception e) {
		}

		startCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (startDialog != null && startDialog.isShowing()) {
					startDialog.dismiss();
				}
			}
		});

		startYearView.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				startYearIndex = wheel.getCurrentItem();
				String year = (String) startYearAdapter
						.getItemText(startYearIndex);
				String month = (String) startMonthAdapter
						.getItemText(startMonthIndex);
				if (Integer.parseInt(month) == 2) {
					if (isLeapYear(year)) {
						// 29 闰年2月29天
						if (startDayAdapter.list.size() != 29) {
							startDayList = Arrays.asList(res
									.getStringArray(R.array.days_29));
							startDayAdapter = new DayAdapter(
									UpdateBaseInfoActivity.this, startDayList);
							startDayView.setViewAdapter(startDayAdapter);
							if (startDayIndex > 28) {
								startDayView.setCurrentItem(0);
								startDayIndex = 0;
							} else {
								startDayView.setCurrentItem(startDayIndex);
							}
						}
					} else {
						// 28 非闰年2月28天
						if (startDayAdapter.list.size() != 28) {
							startDayList = Arrays.asList(res
									.getStringArray(R.array.days_28));
							startDayAdapter = new DayAdapter(
									UpdateBaseInfoActivity.this, startDayList);
							startDayView.setViewAdapter(startDayAdapter);
							if (startDayIndex > 27) {
								startDayView.setCurrentItem(0);
								startDayIndex = 0;
							} else {
								startDayView.setCurrentItem(startDayIndex);
							}
						}
					}
				}
			}
		});
		startMonthView.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				startMonthIndex = wheel.getCurrentItem();
				String year = (String) startYearAdapter
						.getItemText(startYearIndex);
				String month = (String) startMonthAdapter
						.getItemText(startMonthIndex);
				int i = Integer.parseInt(month);
				if (i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10
						|| i == 12) {
					// 31
					if (startDayAdapter.list.size() != 31) {
						startDayList = Arrays.asList(res
								.getStringArray(R.array.days_31));
						startDayAdapter = new DayAdapter(
								UpdateBaseInfoActivity.this, startDayList);
						startDayView.setViewAdapter(startDayAdapter);
						startDayView.setCurrentItem(startDayIndex);
					}
				} else if (i == 2) {
					if (isLeapYear(year)) {
						if (startDayAdapter.list.size() != 29) {
							startDayList = Arrays.asList(res
									.getStringArray(R.array.days_29));
							startDayAdapter = new DayAdapter(
									UpdateBaseInfoActivity.this, startDayList);
							startDayView.setViewAdapter(startDayAdapter);
							if (startDayIndex > 28) {
								startDayView.setCurrentItem(0);
								startDayIndex = 0;
							} else {
								startDayView.setCurrentItem(startDayIndex);
							}
						}
					} else {
						// 28
						if (startDayAdapter.list.size() != 28) {
							startDayList = Arrays.asList(res
									.getStringArray(R.array.days_28));
							startDayAdapter = new DayAdapter(
									UpdateBaseInfoActivity.this, startDayList);
							startDayView.setViewAdapter(startDayAdapter);
							if (startDayIndex > 27) {
								startDayView.setCurrentItem(0);
								startDayIndex = 0;
							} else {
								startDayView.setCurrentItem(startDayIndex);
							}
						}
					}
				} else {
					// 30
					if (startDayAdapter.list.size() != 30) {
						startDayList = Arrays.asList(res
								.getStringArray(R.array.days_30));
						startDayAdapter = new DayAdapter(
								UpdateBaseInfoActivity.this, startDayList);
						startDayView.setViewAdapter(startDayAdapter);
						if (startDayIndex > 29) {
							startDayView.setCurrentItem(0);
							startDayIndex = 0;
						} else {
							startDayView.setCurrentItem(startDayIndex);
						}
					}
				}
			}
		});
		startDayView.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				startDayIndex = wheel.getCurrentItem();
			}
		});

		birthday_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if ("".equals(startYear)) {
					startYear = max_Year + "";
					startMonth = monthFormat.format(date);
					startDay = dayFormat.format(date);
				}
				startYearIndex = startYearList.indexOf(startYear);
				startMonthIndex = startMonthList.indexOf(startMonth);
				startDayIndex = startDayList.indexOf(startDay);
				if (startYearIndex == -1) {
					startYearIndex = 0;
				}
				if (startMonthIndex == -1) {
					startMonthIndex = 0;
				}
				if (startDayIndex == -1) {
					startDayIndex = 0;
				}
				startYearView.setCurrentItem(startYearIndex);
				startMonthView.setCurrentItem(startMonthIndex);
				startDayView.setCurrentItem(startDayIndex);
				startDialog.show();
			}
		});

		startDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startDialog.dismiss();
				startYear = (String) startYearAdapter
						.getItemText(startYearIndex);
				startMonth = (String) startMonthAdapter
						.getItemText(startMonthIndex);
				startDay = (String) startDayAdapter.getItemText(startDayIndex);
				birthday.setText(startYear + "-" + startMonth + "-" + startDay);
			}
		});

	}

	@Override
	public void addListener() {

	}

	AlertDialog cityCacadeDialog;

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.avatarImg:
			select();
			break;
		case R.id.my_message_layout:// 我的消息

			break;
		case R.id.update_nick_name:
			intent = new Intent(this, SaveNickNameActivity.class);
			startActivity(intent);
			overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		case R.id.backBtn:
			finish();
			
			break;
		case R.id.comfirmBtn:
			dialogUploadImage = NiftyDialogBuilder.getInstance(this,
					R.layout.dialog_login_layout);
			final View text = LayoutInflater.from(this).inflate(
					R.layout.dialog_login_view, null);
			TextView t = (TextView) text.findViewById(R.id.loading_text);
			t.setText("正在保存....");
			dialogUploadImage.withTitle(null).withMessage(null)
			.withEffect(Effectstype.Fadein).withDuration(100)
			.isCancelableOnTouchOutside(false)
			.setCustomView(text, this.getApplicationContext()).show();
			
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					String current_skin = skin_type.getText().toString();
					for (int i = 0; i < HttpTagConstantUtils.SKIN_ARRAY.length; i++) {
						if (HttpTagConstantUtils.SKIN_ARRAY[i]
								.equals(current_skin)) {
							skin_upload = (i + 1) + "";
						}
					}
					
					String current_sex = sex.getText().toString();
					for (int i = 0; i < sexType.length; i++) {
						if (current_sex.equals(sexType[i])) {
							sex_upload = (i + 1) + "";
						}
					}
					String provinceCode_ = MyApplication.mProvinceMap.get(mCurrentProviceName) ;
					String cityCode_ = MyApplication.mProvinceMap.get(mCurrentCityName) ;
					//String districtCode_ = MyApplication.mProvinceMap.get(mCurrentDistrictName) ;
					
					Log.e(TAG, "provinceCode_ = "+provinceCode_);
					Log.e(TAG, "cityCode_ = "+cityCode_);
					
					for(String code : mZipcodeDatasMap.keySet()){
						if(mZipcodeDatasMap.get(code).equals(mCurrentDistrictName)
								&& code.substring(0, 2).equals(provinceCode_.substring(0, 2))){
							provinceCode_ = code ;
						}
					}
					
					//Log.e(TAG, "districtCode_ = "+districtCode_);
					/*String provinceCode = MyApplication.mProvinceMap.get(mCurrentDistrictName)  ;
					provinceCode_ = provinceCode ;
					for(String keyString : MyApplication.mProvinceMap_.keySet()){
						//String code = MyApplication.mProvinceMap_.get(keyString);
						if (MyApplication.mProvinceMap_.get(keyString).equals(
								mCurrentDistrictName)
								&& cityCode_.substring(0, 4).equals(keyString.substring(0, 4))) {
							provinceCode_ = keyString;
						}
					}*/
					Log.e(TAG, "final provinceCode_ = "+provinceCode_);
					new com.app.service.UpdateUserInfoService(
							getApplicationContext(),
							HttpTagConstantUtils.UPDATE_USER_INFO,
							UpdateBaseInfoActivity.this).doUserInfoUpdate(
							aCache.getAsString("Token"), "",
							provinceCode_+ "",
							skin_upload, sex_upload, birthday.getText() + "");
				}
			});

			break;
		case R.id.skin_type_layout:
			try {
				// 皮肤类型
				skinTypeView = LayoutInflater.from(this).inflate(
						R.layout.wheel_view_skin_type, null);
				mViewSkinType = (WheelViewCity) skinTypeView
						.findViewById(R.id.skin_type);
				skinDone = (Button) skinTypeView.findViewById(R.id.skinDone);
				skinCancel = (Button) skinTypeView
						.findViewById(R.id.skinCancel);

				// 添加change事件
				mViewSkinType.addChangingListener(this);

				setSkinTypeData();
				skinTypeDialog = new Dialog(this);
				skinTypeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				skinTypeDialog.setContentView(skinTypeView);
				Window regionWindow = skinTypeDialog.getWindow();
				regionWindow.setGravity(Gravity.BOTTOM);
				regionWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				regionWindow.setWindowAnimations(R.style.view_animation);
				regionWindow.setBackgroundDrawable(dw);
				skinTypeDialog.setCanceledOnTouchOutside(false);

				skinDone.setOnClickListener(this);
				skinCancel.setOnClickListener(this);
				skinTypeDialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.sex_layout:
			try {
				// 性别
				sexView = LayoutInflater.from(this).inflate(
						R.layout.wheel_view_sex, null);
				mViewSex = (WheelViewCity) sexView.findViewById(R.id.sex_type);

				sexDone = (Button) sexView.findViewById(R.id.sexDone);
				sexCancel = (Button) sexView.findViewById(R.id.sexCancel);

				// 添加change事件
				mViewSex.addChangingListener(this);

				setSexData();
				sexDialog = new Dialog(this);
				sexDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				sexDialog.setContentView(sexView);
				Window regionWindow = sexDialog.getWindow();
				regionWindow.setGravity(Gravity.BOTTOM);
				regionWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				regionWindow.setWindowAnimations(R.style.view_animation);
				regionWindow.setBackgroundDrawable(dw);
				sexDialog.setCanceledOnTouchOutside(false);

				sexDone.setOnClickListener(this);
				sexCancel.setOnClickListener(this);
				sexDialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.skinDone:
			if (null != skinTypeDialog && skinTypeDialog.isShowing()) {
				skinTypeDialog.dismiss();
			}
			skin_type.setText(""
					+ HttpTagConstantUtils.SKIN_ARRAY[mViewSkinType
							.getCurrentItem()]);
			// AppToast.toastMsgCenter(getApplicationContext(),
			// ""+mViewSkinType.getCurrentItem(), 2000).show();
			break;
		case R.id.skinCancel:
			if (null != skinTypeDialog && skinTypeDialog.isShowing()) {
				skinTypeDialog.dismiss();
			}
			break;
		case R.id.sexDone:
			if (null != sexDialog && sexDialog.isShowing()) {
				sexDialog.dismiss();
			}
			sex.setText("" + sexType[mViewSex.getCurrentItem()]);
			// AppToast.toastMsgCenter(getApplicationContext(),
			// ""+mViewSex.getCurrentItem(), 2000).show();
			break;
		case R.id.sexCancel:
			if (null != sexDialog && sexDialog.isShowing()) {
				sexDialog.dismiss();
			}
			break;
		case R.id.birthday_layout:
			DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
					this, aCache.getAsString("birthday"));
			dateTimePicKDialog.dateTimePicKDialog2(birthday);
			break;
		case R.id.regionView_layout:
			try {
				// 省
				regionView = LayoutInflater.from(this).inflate(
						R.layout.wheel_view_region, null);
				mViewProvince = (WheelViewCity) regionView
						.findViewById(R.id.province);
				mViewCity = (WheelViewCity) regionView.findViewById(R.id.city);
				mViewDistrict = (WheelViewCity) regionView
						.findViewById(R.id.county);

				cityDone = (Button) regionView.findViewById(R.id.cityDone);
				cityCancel = (Button) regionView.findViewById(R.id.cityCancel);

				// 添加change事件
				mViewProvince.addChangingListener(this);
				// 添加change事件
				mViewCity.addChangingListener(this);
				// 添加change事件
				mViewDistrict.addChangingListener(this);
				setUpData();
				regionDialog = new Dialog(this);
				regionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				regionDialog.setContentView(regionView);
				Window regionWindow = regionDialog.getWindow();
				regionWindow.setGravity(Gravity.BOTTOM);
				regionWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				regionWindow.setWindowAnimations(R.style.view_animation);
				regionWindow.setBackgroundDrawable(dw);
				regionDialog.setCanceledOnTouchOutside(false);

				cityCancel.setOnClickListener(this);
				cityDone.setOnClickListener(this);
				regionDialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case R.id.btn_register_photo1:
			if (selectPhotoDialog != null) {
				selectPhotoDialog.dismiss();
				selectPhotoDialog = null;
			}
			destoryBimap();
			String state = Environment.getExternalStorageState();
			uploadImage(state);

			break;
		case R.id.btn_register_photo2:
			if (selectPhotoDialog != null) {
				selectPhotoDialog.dismiss();
				selectPhotoDialog = null;
			}
			// 相册选择
//			intent = new Intent(
//					Intent.ACTION_PICK,
//					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//			startActivityForResult(intent, 0);
			
			//自定义
			intent = new Intent(this,SelectorImageMainActivity.class);
			startActivityForResult(intent, SELECT_FROM_LOCAL);
			overridePendingTransition(R.anim.activity_enter_from_right, R.anim.activity_exit_to_left);
			
			break;
		case R.id.btn_register_photo3:
			if (selectPhotoDialog != null) {
				selectPhotoDialog.dismiss();
				selectPhotoDialog = null;
			}
			break;
		case R.id.cityDone:
			if (popupWindowCity != null) {
				popupWindowCity.dismiss();
				popupWindowCity = null;
			}
			if (regionDialog != null) {
				regionDialog.dismiss();
				regionDialog = null;
			}
			// region.setText("" + mCurrentProviceName.substring(0,2) +
			// mCurrentCityName+ mCurrentDistrictName);
			/*region.setText("" + mCurrentProviceName.substring(0, 2)
					+ mCurrentCityName.substring(0, 2));*/

			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[mViewDistrict
					.getCurrentItem()];		
			if(
					"北京".equals(mCurrentCityName)
					|| "上海".equals(mCurrentCityName)
					|| "重庆".equals(mCurrentCityName)
					|| "天津".equals(mCurrentCityName)
					|| "北京市".equals(mCurrentCityName)
					|| "上海市".equals(mCurrentCityName)
					|| "重庆市".equals(mCurrentCityName)
					|| "县".equals(mCurrentCityName)
					|| null == mCurrentCityName
					|| "".equals(mCurrentCityName)
					){
				this.region.setText("" + mCurrentProviceName + mCurrentDistrictName.replaceAll("bj", ""));
				
			}else if(
					"香港".equals(mCurrentCityName)
					||"台湾".equals(mCurrentCityName)
					){
				this.region.setText("" + mCurrentCityName + mCurrentDistrictName);
			}else if("澳门".equals(mCurrentCityName)){
				this.region.setText("" + mCurrentProviceName);
			}else{
				this.region.setText("" + mCurrentProviceName
							 + (mCurrentCityName != null ? mCurrentCityName
															.replaceAll("bj", "")
															.replaceAll("sjz", "")
															.replaceAll("xt", "")
															.replaceAll("zjk", "")
															: mCurrentCityName));
			}
			
			
			//this.region.setText(""+ ACache.get(this).getAsString("regionNames"));
			//this.region.setText(""+mCurrentProviceName+ mCurrentCityName + mCurrentDistrictName);
			break;
		case R.id.cityCancel:
			if (popupWindowCity != null) {
				popupWindowCity.dismiss();
				popupWindowCity = null;
			}

			if (regionDialog != null) {
				regionDialog.dismiss();
				regionDialog = null;
			}
			break;

		default:
			break;
		}
	}

	private void select() {
		try {
			View view = View.inflate(this, R.layout.activity_select_photo, null);
			Button btnPhoto;
			Button btnImage;
			Button btnDismis;
			
			btnPhoto = (Button) view.findViewById(R.id.btn_register_photo1);
			btnImage = (Button) view.findViewById(R.id.btn_register_photo2);
			btnDismis = (Button) view.findViewById(R.id.btn_register_photo3);
			btnPhoto.setOnClickListener(this);
			btnImage.setOnClickListener(this);
			btnDismis.setOnClickListener(this);
			
			selectPhotoDialog = new Dialog(this);
			
			selectPhotoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			selectPhotoDialog.setContentView(view);
			Window regionWindow = selectPhotoDialog.getWindow();
			regionWindow.setGravity(Gravity.BOTTOM);
			regionWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			regionWindow.setWindowAnimations(R.style.view_animation);
			regionWindow.setBackgroundDrawable(dw);
			selectPhotoDialog.setCanceledOnTouchOutside(false);
		
			selectPhotoDialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		/*try {
			if (popupWindow1 == null) {
				popupWindow1 = new PopupWindow(view,
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				popupWindow1.showAtLocation(view, Gravity.BOTTOM
						| Gravity.CENTER, 0, 0);
			}

			btnPhoto = (Button) view.findViewById(R.id.btn_register_photo1);
			btnImage = (Button) view.findViewById(R.id.btn_register_photo2);
			btnDismis = (Button) view.findViewById(R.id.btn_register_photo3);
			btnPhoto.setOnClickListener(this);
			btnImage.setOnClickListener(this);
			btnDismis.setOnClickListener(this);

		} catch (Exception e) {
			e.printStackTrace();
		}*/

	}

	/**
	 * 以最省内存的方式读取本地资源的图片 或者SDCard中的图片
	 * 
	 * @param imagePath
	 *            图片在SDCard中的路径
	 * @return
	 */
	public static Bitmap getSDCardImg(String imagePath) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;

		opt.inSampleSize = 2;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		opt.inJustDecodeBounds = false;
		// 获取资源图片
		return BitmapFactory.decodeFile(imagePath, opt);
	}

	private void destoryBimap() {
		if (photo != null && !photo.isRecycled()) {
			photo.recycle();
			photo = null;
		}
	}

	// 获取图片方向
	public static int getExifOrientation(String filepath) {
		int degree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(filepath);

		} catch (IOException ex) {
			Log.e("test", "cannot read exif", ex);
		}
		if (exif != null) {
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, -1);

			if (orientation != -1) {
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				}
			}
		}
		return degree;
	}

	public void uploadImage(String state) {
		if (state.equals(Environment.MEDIA_MOUNTED)) {
//			saveDir = Environment.getExternalStorageDirectory() + "/temple";
//			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//			SimpleDateFormat timeStampFormat = new SimpleDateFormat(
//					"yyyy_MM_dd_HH_mm_ss");
//			String filename = timeStampFormat.format(new Date());
//			ContentValues values = new ContentValues();
//			values.put(MediaStore.Images.Media.TITLE, filename);
//			aUri = getContentResolver().insert(
//					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//			intent.putExtra(MediaStore.EXTRA_OUTPUT, aUri);
//
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivityForResult(intent, REQUEST_CODE);
			selectPicFromCamera();
		} else {
			AppToast.toastMsgCenter(getApplicationContext(), "没有SDK卡", 2000).show();
			// saveDir = getApplicationContext().getCacheDir() + "/temple";
		}

		// Toast.makeText(this, "没有SD卡!", Toast.LENGTH_LONG).show();
		// messageDialogUtil.showWrongMessage("没有SD卡!");
		// AppToast.toastMsgCenter(getApplicationContext(), "没有SDK卡", 2000).show();
	}

	// 也不知道有没有用
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i("UserInfoActivity", "onConfigurationChanged");
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// Log.i("UserInfoActivity", "横屏");
			Configuration o = newConfig;
			o.orientation = Configuration.ORIENTATION_PORTRAIT;
			newConfig.setTo(o);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			// Log.i("UserInfoActivity", "竖屏");
		}
		super.onConfigurationChanged(newConfig);
	}
	
	private String localCameraPath = PathUtils.getTmpPath();
	private static final int TAKE_CAMERA_REQUEST = 2;
	/**
	 * 照相获取图片
	 *******************************/
	public void selectPicFromCamera() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri imageUri = Uri.fromFile(new File(localCameraPath));
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, TAKE_CAMERA_REQUEST);
	}
	public final static int SELECT_FROM_LOCAL = 20 ;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SELECT_FROM_LOCAL && resultCode == Activity.RESULT_OK) {
			Uri uri = data.getData();
			Log.e(TAG, "uri = " + uri);
			try {
				if (data != null) {
					String path = "" ;
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						Cursor cursor = getContentResolver().query(selectedImage, null, null,
								null, null);
						if (cursor != null) {
							cursor.moveToFirst();
							int columnIndex = cursor.getColumnIndex("_data");
							String picturePath = cursor.getString(columnIndex);
							cursor.close();
							cursor = null;

							if (picturePath == null || picturePath.equals("null")) {
								AppToast.toastMsgCenter(getApplicationContext(), "找不到图片").show();
								return;
							}
							path = picturePath ;
							Log.e(TAG, "picturePath = " + picturePath);
							
						} else {
							File file = new File(selectedImage.getPath());
							if (!file.exists()) {
								AppToast.toastMsgCenter(getApplicationContext(), "找不到图片").show();
								return;

							}
							
							Log.e(TAG, "file.getAbsolutePath() = " + file.getAbsolutePath());
							
							path = file.getAbsolutePath() ;
						}
					}
					
					if(!"".equals(path)){
						upload_path = path;
						// avatarImg.setImageBitmap(getSDCardImg(path));// 显示本地图片
						Log.e("photo---------path:", path);
						Bitmap photo = null ;
						try {
							photo = PhotoUtils.getImageThumbnail(path, 200, 200) ;//getSDCardImg(path);
							avatarImg.setImageBitmap(photo);// 显示本地图片
						} catch (Exception e) {
							// TODO: handle exception
						} catch (OutOfMemoryError e) {
							// TODO: handle exception
						}
						if (photo == null) {
							AppToast.toastMsgCenter(getApplicationContext(), "上传图片失败").show();
							return;
						}
							
						String fileName = System.currentTimeMillis() + ".j";
						// 图片先压缩
						Bitmap min = ImageUtil.createScaleBitmap(photo, thumb);
						final String localUrl = fileUtil.savaBitmap(fileName, min,
								85);

						photo.recycle();
						min.recycle();
						

						dialogUploadImage = NiftyDialogBuilder.getInstance(this,
								R.layout.dialog_login_layout);
						final View text = LayoutInflater.from(this).inflate(
								R.layout.dialog_login_view, null);
						dialogUploadImage.withTitle(null).withMessage(null)
								.withEffect(Effectstype.Fadein).withDuration(100)
								.isCancelableOnTouchOutside(false)
								.setCustomView(text, this.getApplicationContext())
								.show();

						final String img = new FileUtil(getApplicationContext())
								.getBitmapBase64(localUrl);
						Log.e(TAG, "base_code_64_img >>>>" + img);
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								new AvatarUploadTaskService(
										getApplicationContext(),
										HttpTagConstantUtils.USER_PIC_UPDATE,
										UpdateBaseInfoActivity.this)
										.doAvatarUpload(
												aCache.getAsString("Token"), img);
							}
						}, 500);
					}
				}
				/**
				String[] pojo = { MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(uri, pojo, null, null, null);
				if (cursor != null) {
					ContentResolver cr = getContentResolver();
					int colunm_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					String path = cursor.getString(colunm_index);
					upload_path = path;
					// avatarImg.setImageBitmap(getSDCardImg(path));// 显示本地图片
					Log.e("photo---------path:", path);
					Bitmap photo = getSDCardImg(path);
					avatarImg.setImageBitmap(photo);// 显示本地图片
					if (photo == null) {
						return;
					}
						
					String fileName = System.currentTimeMillis() + ".j";
					// 图片先压缩
					Bitmap min = ImageUtil.createScaleBitmap(photo, thumb);
					final String localUrl = fileUtil.savaBitmap(fileName, min,
							85);

					photo.recycle();
					min.recycle();
					

					dialogUploadImage = NiftyDialogBuilder.getInstance(this,
							R.layout.dialog_login_layout);
					final View text = LayoutInflater.from(this).inflate(
							R.layout.dialog_login_view, null);
					dialogUploadImage.withTitle(null).withMessage(null)
							.withEffect(Effectstype.Fadein).withDuration(100)
							.isCancelableOnTouchOutside(false)
							.setCustomView(text, this.getApplicationContext())
							.show();

					final String img = new FileUtil(getApplicationContext())
							.getBitmapBase64(localUrl);
					Log.e(TAG, "base_code_64_img >>>>" + img);
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							new AvatarUploadTaskService(
									getApplicationContext(),
									HttpTagConstantUtils.USER_PIC_UPDATE,
									UpdateBaseInfoActivity.this)
									.doAvatarUpload(
											aCache.getAsString("Token"), img);
						}
					}, 500);
				}
				
				***/

			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		} else if (/*REQUEST_CODE*/TAKE_CAMERA_REQUEST == requestCode
				&& resultCode == Activity.RESULT_OK) {
			/*Uri uri = null;
			if (data != null) {
				uri = data.getData();
			} else {
				uri = aUri;
			}*/
//			String[] proj = { MediaStore.Images.Media._ID, MediaColumns.DATA,
//					MediaColumns.SIZE };
//			if(aUri == null ){
//				return ;
//			}
//			Cursor actualimagecursor = managedQuery(aUri, proj, null, null,null);
//			int actual_image_column_index = actualimagecursor
//					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//			actualimagecursor.moveToFirst();
//
//			Bitmap bitmap = null;
//			if (actualimagecursor.moveToFirst()) {
//				String realPath = actualimagecursor.getString(actualimagecursor
//						.getColumnIndex(MediaColumns.DATA));// 数据库
//				try {
//					if (realPath != null) {
//						// 刷新
//						Intent scanIntent = new Intent(
//								Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//						scanIntent.setData(Uri.fromFile(new File(realPath)));
//						getApplicationContext().sendBroadcast(scanIntent);
//
//						bitmap = getSDCardImg(realPath);// 为什么返回的为空
//						Matrix matrix = new Matrix();
//						if (null != bitmap) {
//							float postscaler = 500f / bitmap.getWidth();// 500f/bitmap.getHeight();
//							if (postscaler < 1) {
//								matrix.postScale(postscaler, postscaler);
//							}
//
//							bitmap = Bitmap.createBitmap(bitmap, 0, 0,
//									bitmap.getWidth(), bitmap.getHeight(),
//									matrix, true);
//
//							int angle = getExifOrientation(realPath);
//							if (angle != 0) { // 如果照片出现了 旋转 那么 就更改旋转度数
//								matrix = new Matrix();
//								matrix.postRotate(angle);
//								bitmap = Bitmap.createBitmap(bitmap, 0, 0,
//										bitmap.getWidth(), bitmap.getHeight(),
//										matrix, true);// 从新生成图片
//
//								FileOutputStream out = new FileOutputStream(
//										new File(realPath));
//								bitmap.compress(Bitmap.CompressFormat.JPEG,
//										100, out);
//								out.flush();
//								out.close();
//							}
//						}
//						// 如果没有回收
//						if (bitmap.isRecycled() == false) {
//							bitmap.recycle();
//						}
//
//					}
//					String img_path = actualimagecursor
//							.getString(actual_image_column_index);
//					File file = new File(img_path);
//					String path = file.getAbsolutePath();
//					// Log.e(TAG, "" + path);
//					upload_path = path;
//
//					Bitmap photo = getSDCardImg(path);
//					avatarImg.setImageBitmap(photo);// 显示本地图片
//					// UrlImageViewHelper.setUrlDrawable(user_photo, path,
//					// R.drawable.user_default);
//					if (photo == null) {
//						return;
//					}
//					String fileName = System.currentTimeMillis() + ".j";
//					// 图片先压缩
//					Bitmap min = ImageUtil.createScaleBitmap(photo, thumb);
//					final String localUrl = fileUtil.savaBitmap(fileName, min,
//							85);
//
//					photo.recycle();
//					min.recycle();
//					// 上传图片
//					final String img = fileUtil.getBitmapBase64(localUrl);
//					
//					dialogUploadImage = NiftyDialogBuilder.getInstance(this,
//							R.layout.dialog_login_layout);
//					final View text = LayoutInflater.from(this).inflate(
//							R.layout.dialog_login_view, null);
//					dialogUploadImage.withTitle(null).withMessage(null)
//							.withEffect(Effectstype.Fadein).withDuration(100)
//							.isCancelableOnTouchOutside(false)
//							.setCustomView(text, this.getApplicationContext())
//							.show();
//
//					Log.e(TAG, "base_code_64_img >>>>" + img);
//					new Handler().postDelayed(new Runnable() {
//
//						@Override
//						public void run() {
//							new AvatarUploadTaskService(getApplicationContext(),
//									HttpTagConstantUtils.USER_PIC_UPDATE, UpdateBaseInfoActivity.this)
//									.doAvatarUpload(aCache.getAsString("Token"), img);
//						}
//					}, 500);
//
//					
//				} catch (OutOfMemoryError e) {
//					if (bitmap != null) {
//						bitmap.recycle();
//						bitmap = null;
//					}
//				} catch (Exception e) {
//					if (bitmap != null) {
//						bitmap.recycle();
//						bitmap = null;
//					}
//				}

//			}
			
			try {
				upload_path = localCameraPath;
				Log.e(TAG, "localCameraPath = " + localCameraPath);
				//sendImageByPath(localCameraPath);
				final String objectId = Utils.uuid();
				final String newPath = PathUtils.getChatFilePath(objectId);
				//PhotoUtils.compressImage(localCameraPath, newPath);
				
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(localCameraPath, options);
					int inSampleSize = 1;
					int maxSize = 3000;
					Logger.d("outWidth=" + options.outWidth + " outHeight="
							+ options.outHeight);
					if (options.outWidth > maxSize || options.outHeight > maxSize) {
						int widthScale = (int) Math.ceil(options.outWidth * 1.0 / maxSize);
						int heightScale = (int) Math
								.ceil(options.outHeight * 1.0 / maxSize);
						inSampleSize = Math.max(widthScale, heightScale);
					}
					Logger.d("inSampleSize=" + inSampleSize);
					options.inJustDecodeBounds = false;
					options.inSampleSize = inSampleSize;
					Bitmap bitmap = BitmapFactory.decodeFile(localCameraPath, options);
					avatarImg.setImageBitmap(bitmap);
					//旋转图片
					Matrix matrix = new Matrix();
					int angle = PhotoUtils.readPictureDegree(localCameraPath);
					if (angle != 0) { // 如果照片出现了 旋转 那么 就更改旋转度数
						matrix = new Matrix();
						matrix.postRotate(angle);
						bitmap = Bitmap.createBitmap(bitmap, 0, 0,
								bitmap.getWidth(), bitmap.getHeight(),
								matrix, true);// 从新生成图片

						FileOutputStream out = new FileOutputStream(
								new File(localCameraPath));
						bitmap.compress(Bitmap.CompressFormat.JPEG,
								100, out);
						out.flush();
						out.close();
					}
					
					int w = bitmap.getWidth();
					int h = bitmap.getHeight();
					int newW = w;
					int newH = h;
					if (w > maxSize || h > maxSize) {
						if (w > h) {
							newW = maxSize;
							newH = (int) (newW * h * 1.0 / w);
						} else {
							newH = maxSize;
							newW = (int) (newH * w * 1.0 / h);
						}
					}
					
					Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, newW, newH, false);
					// recycle(bitmap);
					Logger.d("bitmap width=" + newBitmap.getWidth() + " h="
							+ newBitmap.getHeight());

					FileOutputStream outputStream = null;
					try {
						outputStream = new FileOutputStream(newPath);
						newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} finally {
						try {
							if (outputStream != null) {
								outputStream.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
					recycle(newBitmap);
					recycle(bitmap);
				//UserService.imageLoader.displayImage(localCameraPath, avatarImg, PhotoUtils.myPicImageOptions);
				// 上传图片
				final String img = fileUtil.getBitmapBase64(newPath);
				
				dialogUploadImage = NiftyDialogBuilder.getInstance(this,
						R.layout.dialog_login_layout);
				final View text = LayoutInflater.from(this).inflate(
						R.layout.dialog_login_view, null);
				dialogUploadImage.withTitle(null).withMessage(null)
						.withEffect(Effectstype.Fadein).withDuration(100)
						.isCancelableOnTouchOutside(false)
						.setCustomView(text, this.getApplicationContext())
						.show();

				Log.e(TAG, "base_code_64_img >>>>" + img);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						new AvatarUploadTaskService(getApplicationContext(),
								HttpTagConstantUtils.USER_PIC_UPDATE, UpdateBaseInfoActivity.this)
								.doAvatarUpload(aCache.getAsString("Token"), img);
					}
				}, 500);
				
			} catch (Exception e) {
				Log.e(TAG, "TAKE_CAMERA_REQUEST ERROR = "+e.toString());
			} catch(OutOfMemoryError error){
				
			}
		}

		//super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 回收垃圾 recycle
	 * 
	 * @throws
	 */
	public static void recycle(Bitmap bitmap) {
		// 先判断是否已经回收
		if (bitmap != null && !bitmap.isRecycled()) {
			// 回收并且置为null
			bitmap.recycle();
			bitmap = null;
		}
		System.gc();
	}

	@Override
	protected void onResume() {
		super.onResume();
		nickname.setText("" + aCache.getAsString("nickname"));
//		String avatar = aCache.getAsString("avatar");
//		Log.e(TAG, "UpdateBaseInfo ------->" + avatar);
//		UserService.imageLoader.displayImage(
//				avatar + "?time=" + System.currentTimeMillis(), avatarImg,
//				PhotoUtils.myPicImageOptions);
	}
	
	public static void displayImageByUri(ImageView imageView, String localPath,
			String url) {
		File file = new File(localPath);
		ImageLoader imageLoader = UserService.imageLoader;
		if (file.exists()) {
			imageLoader.displayImage("file://" + localPath, imageView,
					PhotoUtils.myPicImageOptions);
		} else {
			imageLoader.displayImage(url, imageView,
					PhotoUtils.myPicImageOptions);
		}
	}

	@Override
	public void dataCallBack(final Object tag, int statusCode, Object result) {
		try {
			if ((Integer) (tag) == HttpTagConstantUtils.USER_PIC_UPDATE) {
				if (isSuccess(statusCode)) {
					//avatarImg.setBackgroundDrawable(null);
					//avatarImg.setImageBitmap(getSDCardImg(upload_path));// 显示本地图片
					//UserService.imageLoader.displayImage(""+HttpClientUtils.USER_IMAGE_URL+StringUtil.getPathByUid(ACache.get(getApplicationContext()).getAsString("uid")), avatarImg, PhotoUtils.myPicImageOptions);
					String avatar = aCache.getAsString("avatar");
					Log.e(TAG, "UpdateBaseInfo ------->" + avatar);
					/*UserService.imageLoader.displayImage(
							avatar + "?time=" + System.currentTimeMillis(), avatarImg,
							PhotoUtils.myPicImageOptions);*/
					
					displayImageByUri(avatarImg, upload_path, avatar + "?time=" + System.currentTimeMillis());
					AppToast.toastMsgCenter(this, "上传图片成功").show();
				} else {
					AppToast.toastMsgCenter(this, getResources().getString(R.string.ERROR_404)).show();
				}

			}else if ((Integer) (tag) == HttpTagConstantUtils.UPDATE_USER_INFO) {
				if (isSuccess(statusCode)) {
					// AppToast.toastMsgCenter(this, "保存成功!", 2000).show();
					if (dialogUploadImage != null) {
						dialogUploadImage.dismiss();
					} else {
						//AppToast.toastMsgCenter(this, "" + getResources().getString(R.string.ERROR_404)).show();
					}
					if(TestType.equals(HomeTag.FACE_MARK)){
						gotoActivity(FacialmaskEntryActivity.class);
						
					}else if(TestType.equals(HomeTag.SKIN_TEST)){
						MyApplication.getInstance().getKvo().fire(KVOEvents.ACTION_AFTER_INFO, 2);
						
					}
					
					
					finish();
				} else {
					if (dialogUploadImage != null) {
						dialogUploadImage.dismiss();
					}
					//finish();
					AppToast.toastMsgCenter(this, getResources().getString(R.string.ERROR_404)).show();
				}
			}
		} catch (Exception e) {
			//AppToast.toastMsgCenter(this, "保存失败!", 2000).show();
			finish();
		} finally {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (dialogUploadImage != null) {
						dialogUploadImage.dismiss();
					}
				}
			}, 1000);
		}
	}

	@Override
	public void sendMessageHandler(int messageCode) {

	}

	/**
	 * 判断是否是闰年
	 * */
	public static boolean isLeapYear(String str) {
		int year = Integer.parseInt(str);
		return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
	}

	private class ProvinceAdapter extends AbstractWheelTextAdapter {
		public List<String> list;

		protected ProvinceAdapter(Context context, List<String> list) {
			super(context, R.layout.wheel_view_layout, NO_RESOURCE);
			this.list = list;
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			TextView textCity = (TextView) view.findViewById(R.id.textView);
			textCity.setText(list.get(index));
			return view;
		}

		public int getItemsCount() {
			return list.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return list.get(index);
		}
	}

	private class YearAdapter extends AbstractWheelTextAdapter {
		/**
		 * Constructor
		 */
		public List<String> list;

		protected YearAdapter(Context context, List<String> list) {
			super(context, R.layout.wheel_view_layout, NO_RESOURCE);
			this.list = list;
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);

			TextView textCity = (TextView) view.findViewById(R.id.textView);
			textCity.setText(list.get(index));
			return view;
		}

		public int getItemsCount() {
			return list.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return list.get(index);
		}
	}

	private class MonthAdapter extends AbstractWheelTextAdapter {
		/**
		 * Constructor
		 */
		public List<String> list;

		protected MonthAdapter(Context context, List<String> list) {
			super(context, R.layout.wheel_view_layout, NO_RESOURCE);
			this.list = list;
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);

			TextView textCity = (TextView) view.findViewById(R.id.textView);
			textCity.setText(list.get(index));
			return view;
		}

		public int getItemsCount() {
			return list.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return list.get(index);
		}
	}

	private class DayAdapter extends AbstractWheelTextAdapter {
		/**
		 * Constructor
		 */
		public List<String> list;

		protected DayAdapter(Context context, List<String> list) {
			super(context, R.layout.wheel_view_layout, NO_RESOURCE);
			this.list = list;
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);

			TextView textCity = (TextView) view.findViewById(R.id.textView);
			textCity.setText(list.get(index));
			return view;
		}

		public int getItemsCount() {
			return list.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return list.get(index);
		}
	}

	//protected Map<String, String> mProvinceMap = new HashMap<String, String>();
	protected Map<String, String> mCityMap = new HashMap<String, String>();
	protected Map<String, String> mDistrictMap = new HashMap<String, String>();

	List<RegionsEntity> provinceList = new ArrayList<RegionsEntity>();
	List<RegionsEntity> cityList = new ArrayList<RegionsEntity>();
	List<RegionsEntity> districtList = new ArrayList<RegionsEntity>();

	private void setUpData() {
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(this,
				mProvinceDatas));
		int proindex = 0;
		for (int i = 0; i < mProvinceDatas.length; i++) {
			if (mProvinceDatas[i].equals(mCurrentProviceName)) {
				proindex = i;
			}
		}
		mViewProvince.setCurrentItem(proindex);

		// 设置可见条目数量
		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);
		mViewDistrict.setVisibleItems(7);
		updateCities();
		updateAreas();
	}

	String[] skinType = null;

	private void setSkinTypeData() {
		try {
			String[] skinType = HttpTagConstantUtils.SKIN_ARRAY;// new
																// String[skiyTypeArr.length()];
			int currentIndex = Integer
					.valueOf(aCache.getAsString("skin_type") == null
							|| "".equals(aCache.getAsString("skin_type")) ? "0"
							: aCache.getAsString("skin_type"));

			mViewSkinType.setViewAdapter(new ArrayWheelAdapter<String>(this,
					skinType));
			mViewSkinType.setCurrentItem(currentIndex - 1);
		} catch (Exception e) {

		}

	}

	String[] sexType = new String[] { "男", "女" };

	private void setSexData() {
		mViewSex.setViewAdapter(new ArrayWheelAdapter<String>(this, sexType));
		int sexindex = 0;
		for (int i = 0; i < sexType.length; i++) {
			if (sexType[i].equals(sex.getText().toString())) {
				sexindex = i;
			}
		}
		mViewSex.setCurrentItem(sexindex);
	}

	public static final String ENCODING = "UTF-8";

	// 从assets 文件夹中获取文件并读取数据
	public String getFromAssets(String fileName) {
		String result = "";
		try {
			InputStream in = getResources().getAssets().open(fileName);
			// 获取文件的字节数
			int lenght = in.available();
			// 创建byte数组
			byte[] buffer = new byte[lenght];
			// 将文件中的数据读到byte数组中
			in.read(buffer);
			result = EncodingUtils.getString(buffer, ENCODING);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public void onChanged_2(com.skinrun.wheel.city.widget.WheelViewCity wheel,
			int oldValue, int newValue) {
		if (wheel == mViewProvince) {
			updateCities();
		} else if (wheel == mViewCity) {
			updateAreas();
		} else if (wheel == mViewDistrict) {
			mCurrentDistrictName = mDistrictMap.get(mCurrentCityName);
			// mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
		}
	}

	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas_2() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = cityList.get(pCurrent).getName();// mCitisDatasMap.get(mCurrentProviceName)[pCurrent];

		String mCurrentCityCode = mCityMap.get(mCurrentCityName)
				.substring(0, 4);// 当前市的代码

		List<String> areaList = new ArrayList<String>();
		for (String key : mDistrictMap.keySet()) {
			String code = mDistrictMap.get(key);
			if (code.startsWith(mCurrentCityCode)) {
				areaList.add(key);
			}
		}

		String[] areas = new String[areaList.size()];
		for (int i = 0; i < areaList.size(); i++) {
			areas[i] = areaList.get(i);
		}
		// String[] areas = mDistrictDatasMap.get(mCurrentCityName);
		mViewDistrict
				.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
		mViewDistrict.setCurrentItem(0);
	}


	// /////////////////////////////
	/**
	 * 所有省
	 */
	protected String[] mProvinceDatas;
	/**
	 * key - 省 value - 市
	 */
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	/**
	 * key - 市 values - 区
	 */
	protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

	/**
	 * key - 区 values - 邮编
	 */
	protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

	/**
	 * 当前省的名称
	 */
	protected String mCurrentProviceName;
	/**
	 * 当前市的名称
	 */
	protected String mCurrentCityName;
	/**
	 * 当前区的名称
	 */
	protected String mCurrentDistrictName = "";

	/**
	 * 当前区的邮政编码
	 */
	protected String mCurrentZipCode = "";

	/**
	 * 解析省市区的XML数据
	 */

	protected void initProvinceDatas() {
		List<ProvinceModel> provinceList = null;
		AssetManager asset = getAssets();
		try {
			InputStream input = asset.open("province_data.xml");
			// 创建一个解析xml的工厂对象
			SAXParserFactory spf = SAXParserFactory.newInstance();
			// 解析xml
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler();
			parser.parse(input, handler);
			input.close();
			// 获取解析出来的数据
			provinceList = handler.getDataList();
			// */
			mProvinceDatas = new String[provinceList.size()];
			for (int i = 0; i < provinceList.size(); i++) {
				// 遍历所有省的数据
				mProvinceDatas[i] = provinceList.get(i).getName();
				List<CityModel> cityList = provinceList.get(i).getCityList();
				String[] cityNames = new String[cityList.size()];
				for (int j = 0; j < cityList.size(); j++) {
					// 遍历省下面的所有市的数据
					cityNames[j] = cityList.get(j).getName();
					List<DistrictModel> districtList = cityList.get(j)
							.getDistrictList();
					String[] distrinctNameArray = new String[districtList
							.size()];
					DistrictModel[] distrinctArray = new DistrictModel[districtList
							.size()];
					for (int k = 0; k < districtList.size(); k++) {
						// 遍历市下面所有区/县的数据
						DistrictModel districtModel = new DistrictModel(
								districtList.get(k).getName(), districtList
										.get(k).getZipcode());
						// 区/县对于的邮编，保存到mZipcodeDatasMap
						mZipcodeDatasMap.put(districtList.get(k).getZipcode(),districtList.get(k).getName());
						distrinctArray[k] = districtModel;
						distrinctNameArray[k] = districtModel.getName();
					}
					// 市-区/县的数据，保存到mDistrictDatasMap
					mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
				}
				// 省-市的数据，保存到mCitisDatasMap
				mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
			}
			if(MyApplication.mProvinceMap.size()<=0){
				JSONArray cityData = new JSONArray(getFromAssets("regions"));
				JSONObject obj = null;
	
				for (int i = 0; i < cityData.length(); i++) {
					obj = (JSONObject) cityData.get(i);
					MyApplication.mProvinceMap.put(obj.optString("name"), obj.optString("code"));
					obj = null;
				}
				cityData = null;
			}
			
			//System.out.println(mZipcodeDatasMap);
			
			//邮编zhup
			/*for(String key : mZipcodeDatasMap.keySet()){
				
			}*/
			/**
			String region = region_code == null
					|| "".equals(region_code) ? "110101"
					: region_code.length() < 6 ? "110101"
							: region_code;
			Log.e(TAG, "region code = " + region);
			for (String key : MyApplication.mProvinceMap.keySet()) {
				String code = MyApplication.mProvinceMap.get(key);
				
				if (code.equals(region.substring(0, 2) + "0000")) {
					mCurrentProviceName = key;
				}
				if (code.equals(region.substring(0, 4) + "00")) {
					mCurrentCityName = key;
				}
				if (code.equals(region)) {
					mCurrentDistrictName = key;
				}
			}
			 **/
			mCurrentProviceName = ACache.get(this).getAsString("province");
			mCurrentCityName = ACache.get(this).getAsString("city");
			mCurrentDistrictName = ACache.get(this).getAsString("district");
			//mCurrentDistrictName = mZipcodeDatasMap.get(region);
			
			if(
					"北京".equals(mCurrentCityName)
					|| "上海".equals(mCurrentCityName)
					|| "重庆".equals(mCurrentCityName)
					|| "天津".equals(mCurrentCityName)
					|| "北京市".equals(mCurrentCityName)
					|| "上海市".equals(mCurrentCityName)
					|| "重庆市".equals(mCurrentCityName)
					|| "天津市".equals(mCurrentCityName)
					|| "县".equals(mCurrentCityName)
					|| null == mCurrentCityName
					|| "".equals(mCurrentCityName)
					){
				this.region.setText("" + mCurrentProviceName + mCurrentDistrictName.replaceAll("bj", ""));
				
			}else if(
					"香港".equals(mCurrentCityName)
					||"台湾".equals(mCurrentCityName)
					){
				this.region.setText("" + mCurrentCityName + mCurrentDistrictName);
			}else if("澳门".equals(mCurrentCityName)){
				this.region.setText("" + mCurrentProviceName);
			}else{
				this.region.setText("" + mCurrentProviceName
							 + (mCurrentCityName != null ? mCurrentCityName
															.replaceAll("bj", "")
															.replaceAll("sjz", "")
															.replaceAll("xt", "")
															.replaceAll("zjk", "")
															: mCurrentCityName) );
			}
			
			Log.e(TAG, "mCurrentProviceName-->"+mCurrentProviceName);
			Log.e(TAG, "mCurrentCityName-->"+mCurrentCityName);
			Log.e(TAG, "mCurrentDistrictName-->"+mCurrentDistrictName);
			
			//this.region.setText(""+ mCurrentProviceName+ mCurrentCityName + mCurrentDistrictName);
			
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mDistrictDatasMap.get(mCurrentCityName);
		
		
		if (areas == null) {
			areas = new String[] { "" };
		}
		for (String string : areas) {
			
		}
		mViewDistrict
				.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));

		// String[] dis = mDistrictDatasMap.get(mCurrentCityName);
		int dindex = 0;
		for (int i = 0; i < areas.length; i++) {
			if (areas[i].equals(mCurrentDistrictName)) {
				dindex = i;
			}
		}
		mViewDistrict.setCurrentItem(dindex);
		// mViewDistrict.setCurrentItem(0);
	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
		String[] city = mCitisDatasMap.get(mCurrentProviceName);
		int cityindex = 0;
		for (int i = 0; i < city.length; i++) {
			if (city[i].equals(mCurrentCityName)) {
				cityindex = i;
			}
		}
		mViewCity.setCurrentItem(cityindex);
		updateAreas();
	}

	@Override
	public void onChanged(WheelViewCity wheel, int oldValue, int newValue) {
		if (wheel == mViewProvince) {
			updateCities();
			// mCurrentDistrictName =
			// mDistrictDatasMap.get(mCurrentCityName)[0];
		} else if (wheel == mViewCity) {
			// mCurrentDistrictName =
			// mDistrictDatasMap.get(mCurrentCityName)[0];
			updateAreas();
		} else if (wheel == mViewDistrict) {
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
			//mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
			//log.e(TAG, "mCurrentZipCode = "+ mCurrentZipCode);
			log.e(TAG, "mCurrentDistrictName = "+ mCurrentDistrictName);
		}
	}

	String skin_upload = "1";// 默认1
	String sex_upload = "1";// 默认男-1

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/*if (keyCode == KeyEvent.KEYCODE_BACK) {
			dialogUploadImage = NiftyDialogBuilder.getInstance(this,
					R.layout.dialog_login_layout);
			final View text = LayoutInflater.from(this).inflate(
					R.layout.dialog_login_view, null);
			TextView t = (TextView) text.findViewById(R.id.loading_text);
			t.setText("正在保存....");
			// dialogUploadImage.withTitle(null).withMessage(null)
			// .withEffect(Effectstype.Fadein).withDuration(100)
			// .isCancelableOnTouchOutside(false)
			// .setCustomView(text, this.getApplicationContext()).show();
			String current_skin = skin_type.getText().toString();
			for (int i = 0; i < HttpTagConstantUtils.SKIN_ARRAY.length; i++) {
				if (HttpTagConstantUtils.SKIN_ARRAY[i]
						.equals(current_skin)) {
					skin_upload = (i + 1) + "";
				}
			}
			String current_sex = sex.getText().toString();
			for (int i = 0; i < sexType.length; i++) {
				if (current_sex.equals(sexType[i])) {
					sex_upload = (i + 1) + "";
				}
			}
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					String provinceCode_ = MyApplication.mProvinceMap.get(mCurrentProviceName) ;
					String cityCode_ = MyApplication.mProvinceMap.get(mCurrentCityName) ;
					String districtCode_ = MyApplication.mProvinceMap.get(mCurrentDistrictName) ;
					
					Log.e(TAG, "provinceCode_ = "+provinceCode_);
					Log.e(TAG, "cityCode_ = "+cityCode_);
					Log.e(TAG, "districtCode_ = "+districtCode_);
					
					for(String code : mZipcodeDatasMap.keySet()){
						if(mZipcodeDatasMap.get(code).equals(mCurrentDistrictName)
								&& code.substring(0, 2).equals(provinceCode_.substring(0, 2))){
							provinceCode_ = code ;
						}
					}
					
					String provinceCode = MyApplication.mProvinceMap.get(mCurrentDistrictName)  ;
					provinceCode_ = provinceCode ;
					
					
					for(String keyString : MyApplication.mProvinceMap_.keySet()){
						if (MyApplication.mProvinceMap_.get(keyString).equals(
								mCurrentDistrictName)
								&& cityCode_.substring(0, 4).equals(keyString.substring(0, 4))) {
							provinceCode_ = keyString;
						}
					}
					Log.e(TAG, "final provinceCode_ = "+provinceCode_);
					new com.app.service.UpdateUserInfoService(
							getApplicationContext(),
							HttpTagConstantUtils.UPDATE_USER_INFO,
							UpdateBaseInfoActivity.this).doUserInfoUpdate(
							ACache.get(UpdateBaseInfoActivity.this).getAsString("Token"), "",
							provinceCode_ + "",
							skin_upload, sex_upload, birthday.getText() + "");
					try {
						Thread.sleep(200);
						finish();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			});
			return true;
		}*/
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected String getActivityName() {
		return "修改个人信息";
	}

	@Override
	public void onEvent(String event, Object... args) {
		// TODO Auto-generated method stub
		
	}
}