package com.skinrun.trunk.main;

import java.sql.Date;
import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.app.base.entity.ComparaDataEntity;
import com.app.base.entity.ComparaTestResultEntity;
import com.app.base.entity.GetSkinScoreEntity;
import com.app.base.entity.LastTestDataEntity;
import com.app.base.entity.LastTimeTestRecordEntity;
import com.app.base.entity.Point;
import com.app.base.entity.SkinScoreEntity;
import com.app.base.entity.SkinScoreSeriesEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.MyApplication;
import com.app.custom.view.TestChartView;
import com.app.service.GetComparaDataService;
import com.app.service.GetLastTestDateService;
import com.app.service.GetSkinScoreRecordService;
import com.app.service.GetSportAdviceService;
import com.avos.avoscloud.LogUtil.log;
import com.baidu.mobstat.StatService;
import com.base.app.utils.DBService;
import com.base.app.utils.KVOEvents;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
import com.beabox.hjy.tt.main.skintest.component.KVO.Observer;
import com.skinrun.trunk.test.popuwindow.DeepAnalyseHintPopuwindow;
import com.skinrun.trunk.test.popuwindow.DeepAnalyseHintPopuwindow.onCloseListener;
import com.umeng.analytics.MobclickAgent;

public class TestingDeepFrag extends Fragment implements OnClickListener,
		HttpAysnResultInterface, Observer, onCloseListener {
	private TextView textViewLastData, textViewAge, textViewQualty,
			textViewState, textViewTestPart, textViewResult1, textViewResult2,
			textViewResult3, textViewResult4, textViewAdvice, tvSkinTestDate,
			tvFoodAdvice,tvSportAdvice;
	private RadioGroup radioGroupDeep;
	private ImageView imageViewTestPart, imageViewChartBackground,
			imageViewChartLast, imageViewChartNext;
	private GridView gridViewDeep_ShowData;
	private int width;
	private LayoutParams imageParam;
	private LayoutParams chartParam;
	private TestChartView testingChart;
	private GridViewAdapter mAdapter;
	private int testPosition = 1;
	private int partFlag = 1;
	private UserEntity user;
	private ArrayList<Integer> data;
	private float waterValue, oilValue, flexibleValue;
	private DeepTestDataProvider deepTestDataProvider;
	private GetLastTestDateService getLastTestDateService;
	private int faceAgeFactor = -999, handAgeFactor = -999;
	private String faceSuggestion = "", handSuggestion = "";

	private LastTimeTestRecordEntity lrFace, lrHand;

	private boolean faceFlag = false, handFlag = false;

	private static int NoAgeFactor = -100;

	private static int NOVALUE = -1000;
	
	private DeepAnalyseHintPopuwindow hintPopuwindow;

	// 记录时间
	private long currentHandTime = System.currentTimeMillis(),
			currentFaceTime = System.currentTimeMillis();

	private String TAG = "TestingDeepFrag";

	private NiftyDialogBuilder dialogUploadImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().getKvo()
				.registerObserver(KVOEvents.TEST_RESULT, this);
		MyApplication.getInstance().getKvo()
				.registerObserver(KVOEvents.TEST_PART, this);
		MyApplication.getInstance().getKvo()
				.registerObserver(KVOEvents.USER_LOGININ, this);
		initDialog();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		width = MyApplication.screenSize.x;
		View view =null;
		try{
			view = inflater.inflate(R.layout.testing_deep_new, null);

			initView(inflater, view);
		}catch(Exception e){
			e.printStackTrace();
		}
		

		return view;
	}

	private void initDialog() {
		dialogUploadImage = NiftyDialogBuilder.getInstance(getActivity(),
				R.layout.dialog_login_layout);
		final View text = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_login_view, null);
		TextView t = (TextView) text.findViewById(R.id.loading_text);
		t.setText("");
		dialogUploadImage.withTitle(null).withMessage(null)
				.withEffect(Effectstype.Fadein).withDuration(100)
				.isCancelableOnTouchOutside(false)
				.setCustomView(text, getActivity().getApplicationContext());
	}

	private void initView(LayoutInflater inflater, View view) {
		// 初始化控件
		textViewLastData = (TextView) view.findViewById(R.id.textViewLastData);
		textViewAge = (TextView) view.findViewById(R.id.textViewAge);
		textViewQualty = (TextView) view.findViewById(R.id.textViewQualty);
		textViewState = (TextView) view.findViewById(R.id.textViewState);
		radioGroupDeep = (RadioGroup) view.findViewById(R.id.radioGroupDeep);
		imageViewTestPart = (ImageView) view.findViewById(R.id.imageViewTestPart);
		textViewTestPart = (TextView) view.findViewById(R.id.textViewTestPart);
		gridViewDeep_ShowData = (GridView) view.findViewById(R.id.gridViewDeep_ShowData);
		gridViewDeep_ShowData.setSelector(new ColorDrawable(Color.TRANSPARENT));

		imageViewChartBackground = (ImageView) view.findViewById(R.id.imageViewChartBackground);
		imageViewChartLast = (ImageView) view.findViewById(R.id.imageViewChartLast);
		imageViewChartNext = (ImageView) view.findViewById(R.id.imageViewChartNext);
		testingChart = (TestChartView) view.findViewById(R.id.testingChart);

		textViewResult1 = (TextView) view.findViewById(R.id.textViewResult1);
		textViewResult2 = (TextView) view.findViewById(R.id.textViewResult2);
		textViewResult3 = (TextView) view.findViewById(R.id.textViewResult3);
		textViewResult4 = (TextView) view.findViewById(R.id.textViewResult4);
		textViewAdvice = (TextView) view.findViewById(R.id.textViewAdvice);
		tvSkinTestDate = (TextView) view.findViewById(R.id.tvSkinTestDate);
		
		tvFoodAdvice=(TextView)view.findViewById(R.id.tvFoodAdvice);
		tvSportAdvice=(TextView)view.findViewById(R.id.tvSportAdvice);

		view.findViewById(R.id.viewComparaLastWeek).setOnClickListener(this);
		view.findViewById(R.id.tvSkinTestDate).setOnClickListener(this);
		
		hintPopuwindow=new DeepAnalyseHintPopuwindow(this);
		
		user = DBService.getUserEntity();

		if (user != null) {
			Log.e(TAG, "============第三方登陆用户信息：" + user.toString());
			deepTestDataProvider = new DeepTestDataProvider(user);
		}
		// 设置布局位置
		setLayoutParam();
		// 设置监听
		setListener();
		// 为GridView设置适配器
		setAdapter();
		// 设置折线图数据
		// 初始化上次数据
		if (user != null) {
			requestLastData(user.getToken(), TestingPartUtils.FACEPART, true);
			setAgeAndSkinState(user);
			
		}
		
	}
	
	

	private void setSkinTime(long time) {
		switch (testPosition) {
		case 1:
			tvSkinTestDate.setText(ShowTimeUtil.getLocalShowTime(time - 6 * 24
					* 60 * 60 * 1000)
					+ "-" +

					ShowTimeUtil.getLocalShowTime(time) + "脸部颜值趋势图");

			break;
		case 2:

			tvSkinTestDate.setText(ShowTimeUtil.getLocalShowTime(time - 6 * 24
					* 60 * 60 * 1000)
					+ "-" + ShowTimeUtil.getLocalShowTime(time) + "手部数值趋势图");

			break;
		}
	}

	/*
	 * 获取时间
	 */
	@SuppressLint("SimpleDateFormat")
	private String getLastTime(long time) {
		Date date = new Date(time);
		java.text.SimpleDateFormat f = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		String timestring = f.format(date);
		return timestring;
	}

	/*
	 * 设置肌肤年龄和肤质
	 */
	private void setAgeAndSkinState(UserEntity userEntity) {
		try{
			userEntity=DBService.getUserEntity();
			int age = UserAgeUtil.getUserAge(userEntity);
			if (age <= 0) {
				textViewAge.setText("年龄:未知");
			} else {
				textViewAge.setText("年龄:" + UserAgeUtil.getUserAge(userEntity)
						+ "岁");
			}
			textViewQualty.setText("" + HttpTagConstantUtils.getSkinTypeByKey(userEntity.getSkinType()));
			log.e(TAG, "===========skinType:"+userEntity.getSkinType());
			log.e(TAG, "===========skinType:"+HttpTagConstantUtils.getSkinTypeByKey(userEntity.getSkinType()));
			//textViewQualty.setText("肤质:" + deepTestDataProvider.getSkinType());
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/*
	 * 设置肌肤状况和肌肤年龄
	 */

	private void setSAS(int age, int ageFactor, String suggestion) {
		textViewResult1.setText(ageFactor + age + "");
		if (ageFactor == NoAgeFactor) {
			textViewResult1.setText("");
			textViewResult2.setText("");
			textViewResult3.setText("");
			textViewResult4.setText("");

		} else if (ageFactor < 0) {
			textViewResult2.setText("岁 比实际年龄小");
			textViewResult3.setText(-ageFactor + "");
			textViewResult4.setText("岁");
		} else if (ageFactor == 0) {
			textViewResult2.setText("岁 和实际年龄相同");
			textViewResult3.setText("");
			textViewResult4.setText("");
		} else {
			textViewResult2.setText("岁 比实际年龄大");
			textViewResult3.setText(ageFactor + "");
			textViewResult4.setText("岁");
		}

		if (ageFactor == NoAgeFactor) {
			textViewState.setText("肌肤状况:");
			textViewAdvice.setText("");
		} else {
			textViewState.setText("肌肤状况:"
					+ deepTestDataProvider.getSkinState(ageFactor));
			textViewAdvice.setText(suggestion);
		}
	}

	/*
	 * 请求对比数据
	 */

	private void requestComparaData(String area) {
		GetComparaDataService service = new GetComparaDataService(
				getActivity(), HttpTagConstantUtils.GET_COMPARA_DATA, this);
		if (user != null) {
			service.doGetComparaData(user.getToken(), area);
		}
	}

	/*
	 * 请求最近测试数据
	 */

	private void requestLastData(String token, String part, boolean isFirst) {

		if (part.equals(TestingPartUtils.FACEPART)) {
			lrFace = LastTestRecordSaver.getRecord(token,
					TestingPartUtils.FACEPART);
			if (lrFace != null) {
				faceFlag = true;

				if (faceAgeFactor != -999 && !faceSuggestion.equals("")) {
					setSAS(UserAgeUtil.getUserAge(user), faceAgeFactor,
							handSuggestion);
				}

				data.set(0, translateNum(lrFace.getWater()));
				data.set(3, translateNum(lrFace.getOil()));
				data.set(6, translateNum(lrFace.getFlexible()));

				waterValue = data.get(0);
				oilValue = data.get(3);
				flexibleValue = data.get(6);

				textViewLastData.setText("最近测试日期:"
						+ ShowTimeUtil.getLocalShowTime(Long.parseLong(lrFace
								.getTime())));

				try {
					currentFaceTime = Long.parseLong(lrFace.getTime());
					setSkinTime(currentFaceTime);
					// 请求颜值
					requestSkinScore(getLastTime(currentFaceTime));

				} catch (Exception e) {

				}

				// 请求对比数据
				if (WeekUtil.getWeek(
						getLastTime(Long.parseLong(lrFace.getTime())))
						.equals(WeekUtil.getWeek(getLastTime(System
								.currentTimeMillis())))) {
					// 请求测试数据结束请求对比数据
					requestComparaData(lrFace.getArea());

					Log.e(TAG, "==========请求测试数据的部位：" + lrFace.getArea());
				} else {
					data.set(1, NOVALUE);
					data.set(2, NOVALUE);
					data.set(4, NOVALUE);
					data.set(5, NOVALUE);
					data.set(7, NOVALUE);
					data.set(8, NOVALUE);
					mAdapter.notifyDataSetChanged();
				}
			} else {
				if (getLastTestDateService == null) {
					getLastTestDateService = new GetLastTestDateService(
							getActivity(),
							HttpTagConstantUtils.GET_LAST_TEST_DATA, this);
				}
				getLastTestDateService.doGetLastTestDate(token);
			}

		} else if (part.equals(TestingPartUtils.HANDPART)) {
			lrHand = LastTestRecordSaver.getRecord(token,
					TestingPartUtils.HANDPART);
			if (lrHand != null) {
				handFlag = true;

				if (handAgeFactor != -999 && !handSuggestion.equals("")) {
					setSAS(UserAgeUtil.getUserAge(user), handAgeFactor,
							handSuggestion);
				}

				data.set(0, translateNum(lrHand.getWater()));
				data.set(3, translateNum(lrHand.getOil()));
				data.set(6, translateNum(lrHand.getFlexible()));

				waterValue = data.get(0);
				oilValue = data.get(3);
				flexibleValue = data.get(6);

				try {
					currentHandTime = Long.parseLong(lrHand.getTime());
					setSkinTime(currentHandTime);
					// 请求颜值
					requestSkinScore(getLastTime(currentHandTime));
				} catch (Exception e) {

				}

				textViewLastData.setText("最近测试日期:"
						+ ShowTimeUtil.getLocalShowTime(Long.parseLong(lrHand
								.getTime())));

				// 请求对比数据
				if (WeekUtil.getWeek(
						getLastTime(Long.parseLong(lrHand.getTime())))
						.equals(WeekUtil.getWeek(getLastTime(System
								.currentTimeMillis())))) {
					// 请求测试数据结束请求对比数据

					requestComparaData(lrHand.getArea());
				} else {
					data.set(1, NOVALUE);
					data.set(2, NOVALUE);
					data.set(4, NOVALUE);
					data.set(5, NOVALUE);
					data.set(7, NOVALUE);
					data.set(8, NOVALUE);
					mAdapter.notifyDataSetChanged();
				}
			} else {
				if (getLastTestDateService == null) {
					getLastTestDateService = new GetLastTestDateService(
							getActivity(),
							HttpTagConstantUtils.GET_LAST_TEST_DATA, this);
				}
				getLastTestDateService.doGetLastTestDate(token);
			}

		}
	}

	private int translateNum(float num) {
		int a = (int) num;
		if (num > a + 0.5) {
			return a + 1;
		}
		return a;
	}

	/*
	 * (non-Javadoc)请求数据回调
	 * 
	 * @see
	 * com.base.service.impl.HttpAysnResultInterface#dataCallBack(java.lang.
	 * Object, int, java.lang.Object)
	 */
	int index=0;
	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {

		switch ((Integer) tag) {
		
		case HttpTagConstantUtils.GET_SPORT_ADVICE:
			try{
				JSONObject jsonObject=new JSONObject(result.toString());
				int code=jsonObject.getInt("code");
				if(code==200){
					JSONObject data=jsonObject.getJSONObject("data");
					tvFoodAdvice.setText(data.getString("foodProposal"));
					tvSportAdvice.setText(data.getString("sportsProposal"));
				}
				
			}catch(Exception e){
				
			}
			
			break;
		
		
		case HttpTagConstantUtils.GET_COMPARA_DATA:
			ComparaTestResultEntity comparaTestResultEntity = (ComparaTestResultEntity) result;
			// 如果返回无数据，设数据值为-1
			if (comparaTestResultEntity != null) {
				ComparaDataEntity weekCompara = comparaTestResultEntity
						.getCompare_2_lastweek();
				if (weekCompara != null && weekCompara.getHas_value() == 1) {
					data.set(1,
							countPersent(waterValue, weekCompara.getWater()));
					data.set(4, countPersent(oilValue, weekCompara.getOil()));
					data.set(
							7,
							countPersent(flexibleValue,
									weekCompara.getElasticity()));

				} else {
					data.set(1, NOVALUE);
					data.set(4, NOVALUE);
					data.set(7, NOVALUE);
				}

				ComparaDataEntity sameAgeCompara = comparaTestResultEntity
						.getSame_age();

				if (sameAgeCompara != null
						&& sameAgeCompara.getHas_value() == 1) {
					data.set(2,
							countPersent(waterValue, sameAgeCompara.getWater()));
					data.set(5, countPersent(oilValue, sameAgeCompara.getOil()));
					data.set(
							8,
							countPersent(flexibleValue,
									sameAgeCompara.getElasticity()));

				} else {
					data.set(2, NOVALUE);
					data.set(5, NOVALUE);
					data.set(8, NOVALUE);
				}
			} else {
				data.set(1, NOVALUE);
				data.set(4, NOVALUE);
				data.set(7, NOVALUE);
				data.set(2, NOVALUE);
				data.set(5, NOVALUE);
				data.set(8, NOVALUE);
			}

			mAdapter.notifyDataSetChanged();
			break;
		case HttpTagConstantUtils.GET_LAST_TEST_DATA:
			LastTestDataEntity lastTestDataEntity = (LastTestDataEntity) result;

			switch (testPosition) {
			case 1:
				if (lastTestDataEntity != null) {
					if (lastTestDataEntity.getFace_lastday().equals(
							"1970-01-01")
							|| lastTestDataEntity.getFace_lastday().equals("0")) {
						faceFlag = false;
						data.set(0, 0);
						data.set(3, 0);
						data.set(6, 0);
						textViewLastData.setText("最近没有测试记录哦");
						setSAS(0, NoAgeFactor, "");
						data.set(1, NOVALUE);
						data.set(2, NOVALUE);
						data.set(4, NOVALUE);
						data.set(5, NOVALUE);
						data.set(7, NOVALUE);
						data.set(8, NOVALUE);
						mAdapter.notifyDataSetChanged();

						setSkinTime(System.currentTimeMillis());

					} else {
						faceFlag = true;

						if (faceAgeFactor != -999 && !faceSuggestion.equals("")) {
							setSAS(UserAgeUtil.getUserAge(user), faceAgeFactor,
									faceSuggestion);
						}

						currentFaceTime = WeekUtil
								.getTimemili(lastTestDataEntity
										.getFace_lastday());
						setSkinTime(currentFaceTime);

						requestSkinScore(getLastTime(currentFaceTime));

						data.set(0, lastTestDataEntity.getFace_water());
						data.set(3, lastTestDataEntity.getFace_oil());
						data.set(6, lastTestDataEntity.getFace_elasticity());

						waterValue = lastTestDataEntity.getFace_water();
						oilValue = lastTestDataEntity.getFace_oil();
						flexibleValue = lastTestDataEntity.getFace_elasticity();
						textViewLastData.setText("最近测试日期:"
								+ ShowTimeUtil.getShowTime(lastTestDataEntity
										.getFace_lastday()));

						if (WeekUtil.getWeek(
								lastTestDataEntity.getFace_lastday()).equals(
								WeekUtil.getWeek(getLastTime(System
										.currentTimeMillis())))) {
							// 请求测试数据结束请求对比数据
							requestComparaData(lastTestDataEntity
									.getFace_area());
						} else {
							data.set(1, NOVALUE);
							data.set(2, NOVALUE);
							data.set(4, NOVALUE);
							data.set(5, NOVALUE);
							data.set(7, NOVALUE);
							data.set(8, NOVALUE);
							mAdapter.notifyDataSetChanged();
						}
					}
				}
				break;

			case 2:
				if (lastTestDataEntity != null) {

					if (lastTestDataEntity.getHand_lastday().equals("0")
							|| lastTestDataEntity.getHand_lastday().equals(
									"1970-01-01")) {
						handFlag = false;
						data.set(0, 0);
						data.set(3, 0);
						data.set(6, 0);
						textViewLastData.setText("最近没有测试记录哦");
						setSAS(0, NoAgeFactor, "");
						textViewState.setText("肌肤状况:");

						data.set(1, NOVALUE);
						data.set(2, NOVALUE);
						data.set(4, NOVALUE);
						data.set(5, NOVALUE);
						data.set(7, NOVALUE);
						data.set(8, NOVALUE);

						mAdapter.notifyDataSetChanged();
						setSkinTime(System.currentTimeMillis());

					} else {

						handFlag = true;

						if (handAgeFactor != -999 && !handSuggestion.equals("")) {
							setSAS(UserAgeUtil.getUserAge(user), handAgeFactor,
									handSuggestion);
						}

						currentHandTime = WeekUtil
								.getTimemili(lastTestDataEntity
										.getHand_lastday());
						setSkinTime(currentHandTime);
						requestSkinScore(getLastTime(currentHandTime));

						data.set(0, lastTestDataEntity.getHand_water());
						data.set(3, lastTestDataEntity.getHand_oil());
						data.set(6, lastTestDataEntity.getHand_elasticity());
						waterValue = lastTestDataEntity.getHand_water();
						oilValue = lastTestDataEntity.getHand_oil();
						flexibleValue = lastTestDataEntity.getHand_elasticity();
						textViewLastData.setText("最近测试日期:"
								+ ShowTimeUtil.getShowTime(lastTestDataEntity
										.getHand_lastday()));

						if (WeekUtil.getWeek(
								lastTestDataEntity.getHand_lastday()).equals(
								WeekUtil.getWeek(getLastTime(System
										.currentTimeMillis())))) {

							// 请求测试数据结束请求对比数据
							requestComparaData(lastTestDataEntity
									.getHand_area());
						} else {
							data.set(1, NOVALUE);
							data.set(2, NOVALUE);
							data.set(4, NOVALUE);
							data.set(5, NOVALUE);
							data.set(7, NOVALUE);
							data.set(8, NOVALUE);
							mAdapter.notifyDataSetChanged();
						}
					}

				}
				break;
			}

			break;
		case HttpTagConstantUtils.GET_SKIN_SCORE:
			GetSkinScoreEntity getSkinScoreEntity = (GetSkinScoreEntity) result;
			if (getSkinScoreEntity != null) {

				setSkinScoreCallBackResult(getSkinScoreEntity);
				String suggestion = getSkinScoreEntity.getSuggestion();
				double age_factor = getSkinScoreEntity.getAge_factor();
				Log.e(TAG, "========深度分析====颜值系数：" + age_factor);

				// 保存建议和年龄系数

				if (suggestion != null) {
					switch (testPosition) {
					case 1:
						if (faceSuggestion.equals("")) {
							faceSuggestion = suggestion;

						}
						break;
					case 2:
						if (handSuggestion.equals("")) {
							handSuggestion = suggestion;

						}
						break;
					}
				}

				if (age_factor == -999) {
					switch (testPosition) {
					case 1:
						if (faceAgeFactor == -999) {
							if (user != null) {
								if (lrFace != null) {
									faceAgeFactor = new StandardProvider(
											getActivity()).getAgeFactor(user,
											lrFace.getCurrentSkinScore());
								} else {
									faceAgeFactor = 0;
								}
								if (faceFlag) {

									if (faceAgeFactor != -999
											&& faceSuggestion != null) {
										setSAS(UserAgeUtil.getUserAge(user),
												faceAgeFactor, faceSuggestion);
									}
								}
							}

						}

						break;
					case 2:
						if (handAgeFactor == -999) {
							if (user != null) {
								if (lrHand != null) {
									handAgeFactor = new StandardProvider(
											getActivity()).getAgeFactor(user,
											lrHand.getCurrentSkinScore());
								} else {
									handAgeFactor = 0;
								}

								if (handFlag) {

									if (handAgeFactor != -999
											&& handSuggestion != null) {
										setSAS(UserAgeUtil.getUserAge(user),
												handAgeFactor, handSuggestion);
									}
								}

							}
						}
					}
					break;

				} else {
					if (age_factor >= 5) {
						age_factor = 5;
					}
					if (age_factor <= -5) {
						age_factor = -5;
					}

					switch (testPosition) {
					case 1:
						if (faceAgeFactor == -999) {
							faceAgeFactor = (int) age_factor;
							if (user != null) {

								if (faceFlag) {
									if (faceAgeFactor != -999
											&& faceSuggestion != null) {
										setSAS(UserAgeUtil.getUserAge(user),
												faceAgeFactor, faceSuggestion);
									}
								}

							}
						}

						break;
					case 2:
						if (handAgeFactor == -999) {
							handAgeFactor = (int) age_factor;

							if (user != null) {
								if (faceFlag) {
									if (handAgeFactor != -999
											&& handSuggestion != null) {
										setSAS(UserAgeUtil.getUserAge(user),
												handAgeFactor, handSuggestion);
									}
								}

							}
						}

						break;
					}

				}
			} else {
				switch (testPosition) {
				case 1:
					faceAgeFactor = 0;
					if (faceFlag) {
						if (faceAgeFactor != -999 && faceSuggestion != null) {
							setSAS(UserAgeUtil.getUserAge(user), faceAgeFactor,
									faceSuggestion);
						}
					}

					break;
				case 2:
					handAgeFactor = 0;
					if (handFlag) {
						if (handAgeFactor != -999 && handSuggestion != null) {
							setSAS(UserAgeUtil.getUserAge(user), handAgeFactor,
									handSuggestion);
						}
					}

					break;
				}

				setCharData(null);
			}
			
			GetSportAdviceService getSportAdviceService=new GetSportAdviceService(getActivity(), HttpTagConstantUtils.GET_SPORT_ADVICE, this);
			try{
				if(faceAgeFactor>5||faceAgeFactor<-5){
					getSportAdviceService.doGetAdvice(DBService.getUserEntity().getToken(), "0");
				}else{
					getSportAdviceService.doGetAdvice(DBService.getUserEntity().getToken(), faceAgeFactor+"");
				}
				
			}catch(Exception e){
				
			}
			
			
			
			break;
		}
		dismiss();
	}

	// 请求颜值和年龄系数
	private void requestSkinScore(String lastday) {
		GetSkinScoreRecordService service = new GetSkinScoreRecordService(
				getActivity(), HttpTagConstantUtils.GET_SKIN_SCORE, this);
		service.doGetData(user.getToken(), testPosition + "",
				user.getSkinType(), lastday);
	}

	private void dismiss() {
		if (dialogUploadImage != null && dialogUploadImage.isShowing()) {
			dialogUploadImage.dismiss();
		}
	}

	// 显示颜色和年龄系数返回结果

	private void setSkinScoreCallBackResult(
			GetSkinScoreEntity getSkinScoreEntity) {
		SkinScoreSeriesEntity skinScoreSeriesEntity = getSkinScoreEntity
				.getSeries();
		if (skinScoreSeriesEntity != null) {
			ArrayList<SkinScoreEntity> u_last7days = skinScoreSeriesEntity
					.getU_last7days();
			if (u_last7days != null && u_last7days.size() > 0) {
				setCharData(u_last7days);
			} else {
				setCharData(null);
			}

		} else {
			setCharData(null);
		}
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

	// 计算比较百分比
	private int countPersent(double a1, double a2) {
		if (a2 == 0) {
			return 1;
		}
		return (int) ((a1 - a2));
	}

	// 设置折线图数据
	private void setCharData(ArrayList<SkinScoreEntity> skins) {
		if (skins == null || skins.size() <= 0) {
			
			testingChart.setPoints(null);
			testingChart.invalidate();
			return;
		}
		ArrayList<Point> points = new ArrayList<Point>();
		for (int i = 0; i < skins.size(); i++) {
			if (skins.get(i).getSkinScore() < 100) {
				if (skins.get(i).getSkinScore() > 0) {
					points.add(getPoint(i + 1, (int) skins.get(i)
							.getSkinScore()));
				}

			} else {
				if ((int) skins.get(i).getSkinScore() / 100 != 0) {
					points.add(getPoint(i + 1, (int) skins.get(i)
							.getSkinScore() / 100));
				}
			}

		}

		testingChart.setPoints(points);
		testingChart.invalidate();
	}

	// 确定坐标点的位置
	private Point getPoint(int date, int skinscore) {
		if (skinscore < 10) {
			skinscore = 10;
		}
		if (skinscore > 100) {
			skinscore = 100;
		}

		return new Point(chartParam.width / 7 * date - chartParam.width / 14,
				chartParam.height - chartParam.height / 90 * (skinscore - 8));
	}

	// 设置GridView适配器以及初始数据
	private void setAdapter() {
		mAdapter = new GridViewAdapter(null);
		gridViewDeep_ShowData.setAdapter(mAdapter);

		// 测试数据
		data = new ArrayList<Integer>();
		data.add(0);
		data.add(NOVALUE);
		data.add(NOVALUE);
		data.add(0);
		data.add(NOVALUE);
		data.add(NOVALUE);
		data.add(0);
		data.add(NOVALUE);
		data.add(NOVALUE);
		mAdapter.setData(data);
	}

	// 设置监听
	private void setListener() {
		imageViewChartLast.setOnClickListener(this);
		imageViewChartNext.setOnClickListener(this);
		radioGroupDeep
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup arg0, int arg1) {
						switch (arg1) {
						case R.id.radioFace:
							dialogUploadImage.show();

							imageViewTestPart
									.setImageResource(R.drawable.testingdeepicon);
							textViewTestPart.setText("脸部");
							testPosition = 1;
							requestSkinScore(getLastTime(currentFaceTime));
							// 请求最近测试数据
							requestLastData(user.getToken(),
									TestingPartUtils.FACEPART, false);

							tvSkinTestDate.setText(ShowTimeUtil
									.getLocalShowTime(currentFaceTime - 6 * 24
											* 60 * 60 * 1000)
									+ "-"
									+ ShowTimeUtil
											.getLocalShowTime(currentFaceTime)
									+ "脸部颜值趋势图");
							Log.e(TAG, "=======faceSuggestion:"
									+ faceSuggestion + "faceAgeFactor:"
									+ faceAgeFactor);
							if (faceFlag) {

								if (faceAgeFactor != -999
										&& faceSuggestion != null) {

									setSAS(UserAgeUtil.getUserAge(user),
											faceAgeFactor, faceSuggestion);
								}
							}
							break;
						case R.id.radioHand:
							dialogUploadImage.show();

							imageViewTestPart
									.setImageResource(R.drawable.deephand);
							textViewTestPart.setText("手部");
							testPosition = 2;
							requestSkinScore(getLastTime(currentHandTime));
							// 请求最近测试数据
							requestLastData(user.getToken(),
									TestingPartUtils.HANDPART, false);
							tvSkinTestDate.setText(ShowTimeUtil
									.getLocalShowTime(currentHandTime - 6 * 24
											* 60 * 60 * 1000)
									+ "-"
									+ ShowTimeUtil
											.getLocalShowTime(currentHandTime)
									+ "手部数值趋势图");
							Log.e(TAG, "=======handSuggestion:"
									+ handSuggestion + "handAgeFactor:"
									+ handAgeFactor);
							if (handFlag) {
								if (handAgeFactor != -999
										&& handSuggestion != null) {
									setSAS(UserAgeUtil.getUserAge(user),
											handAgeFactor, handSuggestion);
								}
							}

							break;
						}
					}
				});
	}

	// 设置布局参数
	private void setLayoutParam() {
		imageParam = (LayoutParams) imageViewChartBackground.getLayoutParams();
		chartParam = (LayoutParams) testingChart.getLayoutParams();
		android.widget.LinearLayout.LayoutParams skinDateParams = (android.widget.LinearLayout.LayoutParams) tvSkinTestDate
				.getLayoutParams();

		int border = dpToPx(getActivity(), 5);
		int imageWidth = (int) ((width - 2 * border) * 0.8);
		int imageHeight = (int) (imageWidth / 2);
		imageParam.width = imageWidth;
		imageParam.height = imageHeight;
		chartParam.width = (int) (imageWidth * 0.95);
		chartParam.height = (int) (imageHeight);
		imageViewChartBackground.setLayoutParams(imageParam);
		skinDateParams.width = width;
		skinDateParams.height = (int) (imageHeight * 0.15);
		tvSkinTestDate.setLayoutParams(skinDateParams);
		testingChart.setLayoutParams(chartParam);

	}

	/*
	 * 单位转化
	 */
	private int dpToPx(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	// 更新折线图的数据
	private void updateCharDate(int flag) {
		switch (flag) {
		case 0:
			// 请求上一周数据
			String time = "";
			if (testPosition == 1) {
				currentFaceTime -= 6 * 24 * 60 * 60 * 1000;
				time = getLastTime(currentFaceTime);

				tvSkinTestDate.setText(ShowTimeUtil
						.getLocalShowTime(currentFaceTime - 6 * 24 * 60 * 60
								* 1000)
						+ "-"
						+ ShowTimeUtil.getLocalShowTime(currentFaceTime)
						+ "脸部颜值趋势图");

			} else {
				currentHandTime -= 6 * 24 * 60 * 60 * 1000;
				time = getLastTime(currentHandTime);

				tvSkinTestDate.setText(ShowTimeUtil
						.getLocalShowTime(currentHandTime - 6 * 24 * 60 * 60
								* 1000)
						+ "-"
						+ ShowTimeUtil.getLocalShowTime(currentHandTime)
						+ "手部数值趋势图");
			}
			requestSkinScore(time);

			break;
		case 1:
			// 请求下一周数据
			String lastTime = "";
			if (testPosition == 1) {

				if (WeekUtil.isOverLastDate(currentFaceTime)) {
					AppToast.toastMsg(getActivity(), "没有更多数据了").show();

					dismiss();
					return;
				}
				currentFaceTime += 6 * 24 * 60 * 60 * 1000;
				lastTime = getLastTime(currentFaceTime);

				tvSkinTestDate.setText(ShowTimeUtil
						.getLocalShowTime(currentFaceTime - 6 * 24 * 60 * 60
								* 1000)
						+ "-"
						+ ShowTimeUtil.getLocalShowTime(currentFaceTime)
						+ "脸部颜值趋势图");

			} else {
				if (WeekUtil.isOverLastDate(currentHandTime)) {
					AppToast.toastMsg(getActivity(), "没有更多数据了").show();
					dismiss();
					return;
				}

				currentHandTime += 6 * 24 * 60 * 60 * 1000;
				lastTime = getLastTime(currentHandTime);
				tvSkinTestDate.setText(ShowTimeUtil
						.getLocalShowTime(currentHandTime - 6 * 24 * 60 * 60
								* 1000)
						+ "-"
						+ ShowTimeUtil.getLocalShowTime(currentHandTime)
						+ "手部数值趋势图");

			}
			requestSkinScore(lastTime);

			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageViewChartLast:
			dialogUploadImage.show();

			updateCharDate(0);
			break;
		case R.id.imageViewChartNext:
			dialogUploadImage.show();

			updateCharDate(1);
			break;
		case R.id.tvSkinTestDate:
			hintPopuwindow.show(getActivity());
			break;
		case R.id.viewComparaLastWeek:
			hintPopuwindow.show(getActivity());
			break;
		}
	}
	
	
	class GridViewAdapter extends BaseAdapter {
		private ArrayList<Integer> data;

		public GridViewAdapter(ArrayList<Integer> data) {
			super();
			this.data = data;
		}

		public ArrayList<Integer> getData() {
			return data;
		}

		public void setData(ArrayList<Integer> data) {
			this.data = data;
		}

		@Override
		public int getCount() {
			if (data == null) {
				return 0;
			}
			return data.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			GridViewHolder holder = null;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.testing_deep_gridview_item, null);
				holder = new GridViewHolder();
				holder.tv = (TextView) convertView
						.findViewById(R.id.textViewDeepItem);
				holder.iv = (ImageView) convertView
						.findViewById(R.id.imageViewDeepItem);
				convertView.setTag(holder);
			} else {
				holder = (GridViewHolder) convertView.getTag();
			}
			switch (arg0) {
			case 0:
				holder.iv.setImageResource(R.drawable.water_small_icon);
				if (data.get(arg0) == 0) {
					holder.tv.setText("水分:");
				} else {
					holder.tv.setText("水分:" + data.get(arg0) + "%");
				}
				holder.tv.setTextSize(13);
				holder.tv.setTextColor(getResources().getColor(
						R.color.deep_test_gridbottom_gray));
				convertView.setBackgroundColor(getResources().getColor(
						R.color.test_deep_item_back_lwhite));
				break;
			case 3:
				holder.iv.setImageResource(R.drawable.oil_small_icon);

				if (data.get(arg0) == 0) {
					holder.tv.setText("油份:");
				} else {
					holder.tv.setText("油份:" + data.get(arg0) + "%");
				}

				holder.tv.setTextSize(13);
				holder.tv.setTextColor(getResources().getColor(
						R.color.deep_test_gridbottom_gray));
				convertView.setBackgroundColor(getResources().getColor(
						R.color.test_deep_item_back_lwhite));
				break;
			case 6:
				holder.iv.setImageResource(R.drawable.flexible_small_icon);

				if (data.get(arg0) == 0) {
					holder.tv.setText("弹性:");
				} else {
					holder.tv.setText("弹性:" + data.get(arg0) + "  ");
				}

				holder.tv.setTextSize(13);
				holder.tv.setTextColor(getResources().getColor(
						R.color.deep_test_gridbottom_gray));
				convertView.setBackgroundColor(getResources().getColor(
						R.color.test_deep_item_back_lwhite));
				break;

			case 7:
				holder.tv.setTextColor(getResources().getColor(
						R.color.deep_test_gridbottom_gray));
				convertView.setBackgroundColor(getResources().getColor(
						R.color.test_deep_item_back_lwhite));
				if (data.get(arg0) > 0) {

					if (data.get(arg0) <= 9) {
						holder.tv.setText("  +" + data.get(arg0) + "   ");
					} else {
						holder.tv.setText("+" + data.get(arg0) + "   ");
					}

					holder.iv.setImageResource(R.drawable.testing_up);

					holder.tv.setTextSize(13);
				} else if (data.get(arg0) == 0) {
					holder.iv.setImageResource(R.drawable.testing_up);
					holder.tv.setText("   " + data.get(arg0) + "   ");
					holder.tv.setTextSize(13);
				} else if (data.get(arg0) == NOVALUE) {
					holder.tv.setText("—");
					holder.iv.setImageDrawable(null);

					holder.tv.setTextSize(13);
				} else {
					if (data.get(arg0) >= -9) {
						holder.tv.setText("  " + data.get(arg0) + "   ");
					} else {
						holder.tv.setText(data.get(arg0) + "   ");
					}
					holder.iv.setImageResource(R.drawable.testing_down);

					holder.tv.setTextSize(13);
				}
				break;
			case 8:
				holder.tv.setTextColor(getResources().getColor(
						R.color.deep_test_gridbottom_gray));
				convertView.setBackgroundColor(getResources().getColor(
						R.color.test_deep_item_back_lwhite));
				if (data.get(arg0) > 0) {

					if (data.get(arg0) <= 9) {
						holder.tv.setText("  +" + data.get(arg0) + "   ");
					} else {
						holder.tv.setText("+" + data.get(arg0) + "   ");
					}

					holder.iv.setImageResource(R.drawable.testing_up);

					holder.tv.setTextSize(13);
				} else if (data.get(arg0) == 0) {
					holder.iv.setImageResource(R.drawable.testing_up);
					holder.tv.setText("   " + data.get(arg0) + "   ");
					holder.tv.setTextSize(13);
				} else if (data.get(arg0) == NOVALUE) {
					holder.tv.setText("—");
					holder.iv.setImageDrawable(null);

					holder.tv.setTextSize(13);
				} else {
					if (data.get(arg0) >= -9) {
						holder.tv.setText("  " + data.get(arg0) + "   ");
					} else {
						holder.tv.setText("  " + data.get(arg0) + "   ");
					}
					holder.iv.setImageResource(R.drawable.testing_down);

					holder.tv.setTextSize(13);
				}
				break;

			default:
				holder.tv.setTextColor(getResources().getColor(
						R.color.deep_test_gridbottom_gray));
				convertView.setBackgroundColor(getResources().getColor(
						R.color.test_deep_item_back_lwhite));
				if (data.get(arg0) > 0) {

					if (data.get(arg0) <= 9) {
						holder.tv.setText("  +" + data.get(arg0) + "%");
					} else {
						holder.tv.setText("+" + data.get(arg0) + "%");
					}

					holder.iv.setImageResource(R.drawable.testing_up);

					holder.tv.setTextSize(13);
				} else if (data.get(arg0) == 0) {
					holder.iv.setImageResource(R.drawable.testing_up);
					holder.tv.setText("    " + data.get(arg0) + "%");
					holder.tv.setTextSize(13);
				} else if (data.get(arg0) == NOVALUE) {
					holder.tv.setText("—");
					holder.iv.setImageDrawable(null);

					holder.tv.setTextSize(13);
				} else {
					if (data.get(arg0) >= -9) {
						holder.tv.setText("  " + data.get(arg0) + "%");
					} else {
						holder.tv.setText(data.get(arg0) + "%");
					}
					holder.iv.setImageResource(R.drawable.testing_down);

					holder.tv.setTextSize(13);
				}
				break;
			}
			return convertView;
		}
	}

	// 持有者
	class GridViewHolder {
		TextView tv;
		ImageView iv;
	}

	// 接收测试结果
	@Override
	public void onEvent(String event, Object... args) {
		if (event.equals(KVOEvents.TEST_RESULT)) {
			waterValue = (Float) (args[0]);
			oilValue = (Float) args[1];
			flexibleValue = (Float) args[2];

			data.set(0, translateNum(waterValue));
			data.set(3, translateNum(oilValue));
			data.set(6, translateNum(flexibleValue));
			mAdapter.notifyDataSetChanged();

			textViewLastData
					.setText("最近测试日期:"
							+ ShowTimeUtil.getLocalShowTime(System
									.currentTimeMillis()));

			if (partFlag > 3) {
				faceFlag = true;
				RadioButton rb = (RadioButton) radioGroupDeep.getChildAt(1);
				rb.setChecked(true);
				testPosition = 2;
				imageViewTestPart.setImageResource(R.drawable.deephand);
				textViewTestPart.setText("手部");
				currentHandTime = System.currentTimeMillis();

				Log.e(TAG, "=======faceSuggestion:" + faceSuggestion
						+ "faceAgeFactor:" + faceAgeFactor);
				if (faceAgeFactor != -999 && faceSuggestion != null) {
					setSAS(UserAgeUtil.getUserAge(user), faceAgeFactor,
							faceSuggestion);
				}

			} else {
				handFlag = true;
				RadioButton rb = (RadioButton) radioGroupDeep.getChildAt(0);
				rb.setChecked(true);
				testPosition = 1;
				imageViewTestPart.setImageResource(R.drawable.testingdeepicon);
				textViewTestPart.setText("脸部");
				currentFaceTime = System.currentTimeMillis();
				Log.e(TAG, "=======handSuggestion:" + handSuggestion
						+ "handAgeFactor:" + handAgeFactor);
				if (handAgeFactor != -999 && handSuggestion != null) {
					setSAS(UserAgeUtil.getUserAge(user), handAgeFactor,
							handSuggestion);
				}

			}
			// 请求对比数据
			requestComparaData(getTestPart());

		} else if (event.equals(KVOEvents.TEST_PART)) {
			int part = (Integer) args[0];
			partFlag = part;

		} else if (event.equals(KVOEvents.USER_LOGININ)) {
			user = DBService.getUserEntity();

			if (user != null) {
				deepTestDataProvider = new DeepTestDataProvider(user);

				if (user != null) {

					setAgeAndSkinState(user);
					switch (testPosition) {
					case 1:
						requestLastData(user.getToken(),
								TestingPartUtils.FACEPART, false);
						currentFaceTime = System.currentTimeMillis();

						requestSkinScore(getLastTime(currentFaceTime));
						Log.e(TAG, "=======faceSuggestion:" + faceSuggestion
								+ "faceAgeFactor:" + faceAgeFactor);
						if (faceAgeFactor != -999 && faceSuggestion != null) {

							setSAS(UserAgeUtil.getUserAge(user), faceAgeFactor,
									faceSuggestion);
						}

						break;
					case 2:
						requestLastData(user.getToken(),
								TestingPartUtils.HANDPART, false);

						currentHandTime = System.currentTimeMillis();
						requestSkinScore(getLastTime(currentHandTime));
						Log.e(TAG, "=======handSuggestion:" + handSuggestion
								+ "handAgeFactor:" + handAgeFactor);
						if (handAgeFactor != -999 && handSuggestion != null) {

							setSAS(UserAgeUtil.getUserAge(user), handAgeFactor,
									handSuggestion);
						}

						break;
					}
				}

			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		hintPopuwindow.destroy();
		MyApplication.getInstance().getKvo()
				.removeObserver(KVOEvents.TEST_RESULT, this);
		MyApplication.getInstance().getKvo()
				.removeObserver(KVOEvents.TEST_PART, this);
		MyApplication.getInstance().getKvo()
				.removeObserver(KVOEvents.USER_LOGININ, this);
		if (dialogUploadImage != null && dialogUploadImage.isShowing()) {
			dialogUploadImage.dismiss();
			dialogUploadImage = null;
		}

	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(getActivity());
		StatService.onResume(getActivity());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		MobclickAgent.onPause(getActivity());
		StatService.onPause(getActivity());
	}

	@Override
	public void close() {
		hintPopuwindow.dismiss();
	}
}
