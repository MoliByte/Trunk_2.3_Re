package com.skinrun.trunk.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.app.base.entity.HandCommentEntity;
import com.app.base.entity.HumidityEntity;
import com.app.base.entity.IndustryStandardEntity;
import com.app.base.entity.SkinCommentEntity;
import com.app.base.entity.SkinTypeEntity;
import com.app.base.entity.TemperatureEntity;
import com.app.base.entity.UserEntity;
import com.app.base.entity.UvEntity;
import com.app.base.entity.WeatherEntity;
import com.app.base.init.MyApplication;
import com.app.service.RequestWeatherService;
import com.avos.avoscloud.LogUtil.log;
import com.base.app.utils.DBService;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;

public class StandardProvider {
	private Context context;
	private UserEntity userInfo;
	private ArrayList<Double> standardValue;
	private WeatherEntity weatherEntity;
	private Address address;

	private String Tag = "StandardProvider";

	public WeatherEntity getWeatherEntity() {
		return weatherEntity;
	}

	public StandardProvider(Context context) {
		this.context = context;
		initWeatherService();
		standardValue = new ArrayList<Double>();
	}

	// 返回数据标准
	public ArrayList<Double> getStandardValue(UserEntity userInfo) {
		this.userInfo = userInfo;
		standardValue.clear();

		standardValue.add(getWaterStandard());
		standardValue.add(getOilStandard());
		standardValue.add(getStandardSkinScore());
		return standardValue;
	}

	public ArrayList<Double> getStandardValue(UserEntity userInfo,
			WeatherEntity weatherEntity) {
		this.userInfo = userInfo;
		this.weatherEntity = weatherEntity;

		standardValue.clear();
		standardValue.add(getWaterStandard());
		standardValue.add(getOilStandard());
		standardValue.add(getStandardSkinScore());

		// 返回结果后初始化天气
		initWeatherService();

		return standardValue;
	}

	// 计算标准颜值
	private double getStandardSkinScore() {
		return getWaterStandard() * 0.861 + getOilStandard() * 0.75;
	}

	/*
	 * 获取肌肤评价
	 */
	public String getSkinComment(UserEntity userInfo, double personalSkinScore) {

		int age_factor = getCommentAgeFactor(userInfo, personalSkinScore);

		List<SkinCommentEntity> skinComments = DBService.getDB()
				.findAllByWhere(SkinCommentEntity.class,
						"age_factor = " + age_factor);
		Random random = new Random();

		if (skinComments != null && skinComments.size() != 0) {
			int randomNum = random.nextInt(skinComments.size());
			SkinCommentEntity skinCommentEntity = skinComments.get(randomNum);
			return skinCommentEntity.getComment();
		} else {
			return "护肤是一场持久战，撑到最后的才是美女子！";
		}

	}

	private int getCommentAgeFactor(UserEntity userInfo,
			double personalSkinScore) {
		this.userInfo = userInfo;
		int age = UserAgeUtil.getUserAge(userInfo);
		int age_factor = 0;
		if (age <= 40) {
			age_factor = (int) (getStandardSkinScore() - personalSkinScore) / 4;
		} else {
			age_factor = (int) (getStandardSkinScore() - personalSkinScore) / 10;
		}
		if (age_factor < -5) {
			age_factor = -5;
		}

		if (age_factor > 5) {
			age_factor = 5;
		}
		return age_factor;
	}

	public String getHandComment(UserEntity userInfo, double personalSkinScore) {
		int age_factor = getCommentAgeFactor(userInfo, personalSkinScore);
		List<HandCommentEntity> handComments = DBService.getDB()
				.findAllByWhere(HandCommentEntity.class, "age = " + age_factor);
		Random random = new Random();
		if (handComments != null && handComments.size() > 0) {
			int randomNum = random.nextInt(handComments.size());
			HandCommentEntity handCommentEntity = handComments.get(randomNum);
			return handCommentEntity.getSuggestion();
		} else {
			return "护肤是一场持久战，撑到最后的才是美女子！";
		}
	}

	/*
	 * 特殊情况下的年龄系数
	 */

	public int getAgeFactor(UserEntity userInfo, double personalSkinScore) {

		this.userInfo = userInfo;
		int age = UserAgeUtil.getUserAge(userInfo);
		if (age == 0) {
			age = 25;
		}

		if (age <= 16) {
			age = 16;
		}

		if (age >= 80) {
			age = 80;
		}

		int age_factor = 0;

		if (personalSkinScore == 0) {
			return 0;
		}

		if (age <= 40) {
			age_factor = (int) (getStandardSkinScore() - personalSkinScore) / 4;
		} else {
			age_factor = (int) (getStandardSkinScore() - personalSkinScore) / 10;
		}

		if (age_factor < -5) {
			age_factor = -5;
		}

		if (age_factor > 5) {
			age_factor = 5;
		}

		return age_factor;
	}

	/*
	 * 计算环境因素系数
	 */
	private int countEnvirmentFactor() {
		int a1 = 0, a2 = 0, a3 = 0, a4 = 0;
		// 直接从数据库查找
		if (userInfo.getSkinType() != null
				|| weatherEntity.getTag().equals("OFFLINE")) {
			List<SkinTypeEntity> skins = DBService.getDB().findAllByWhere(
					SkinTypeEntity.class,
					"id= '" + userInfo.getSkinType() + "'");
			if (skins == null || skins.size() == 0) {
				a4 = 0;
			} else {
				a4 = skins.get(0).getAffect();
			}

			if (weatherEntity == null) {
				return a4;
			}
		}

		weatherEntity = new WeatherEntity();

		List<TemperatureEntity> temps = DBService.getDB().findAllByWhere(
				TemperatureEntity.class,
				"min_temp <= " + weatherEntity.getTemperature()
						+ " and max_temp >= " + weatherEntity.getTemperature());
		if (temps == null || temps.size() == 0) {
			a1 = 0;
		} else {
			a1 = temps.get(0).getAffect();
		}

		List<HumidityEntity> hums = DBService.getDB().findAllByWhere(
				HumidityEntity.class,
				"min_hum <=" + weatherEntity.getHumidity() + " and max_hum >="
						+ weatherEntity.getHumidity());

		if (hums == null || hums.size() == 0) {
			a2 = 0;
		} else {
			a2 = hums.get(0).getAffect();
		}

		List<UvEntity> uvs = DBService.getDB().findAllByWhere(UvEntity.class,
				"uv='" + weatherEntity.getUv() + "'");
		if (uvs == null || uvs.size() == 0) {
			a3 = 0;
		} else {
			a3 = uvs.get(0).getAffect();
		}

		return a1 + a2 + a3 + a4;
	}

	// 获取标准水份
	private double getWaterStandard() {
		int age = UserAgeUtil.getUserAge(userInfo);
		if (age == 0) {
			age = 25;
		}

		if (age <= 16) {
			age = 16;
		}
		if (age >= 80) {
			age = 80;
		}

		double standardWater = 0;

		List<IndustryStandardEntity> industrys = DBService.getDB()
				.findAllByWhere(IndustryStandardEntity.class, "age=" + age);
		standardWater = industrys.get(0).getStandard();
		return standardWater + countEnvirmentFactor();
	}

	// 获取标准油分
	public double getOilStandard() {
		return getWaterStandard() / 2.23;
	}

	private void sendRequestWeather() {
		final Location location = requestLocation();

		double lat = 0;
		double lng = 0;
		if (location == null) {
			lat = 31.50;
			lng = 121.5;
		} else {
			lat = location.getLatitude();
			lng = location.getLongitude();
		}

		// Log.e(Tag,
		// "=======请求天气时获取的经纬度："+location.getLatitude()+","+location.getLongitude());

		RequestWeatherService requestWeatherService = new RequestWeatherService(
				context, lng, lat, HttpTagConstantUtils.GET_WEATHER,
				new HttpAysnResultInterface() {

					@Override
					public void dataCallBack(Object tag, int statusCode,
							Object result) {
						weatherEntity = (WeatherEntity) result;
						if (weatherEntity != null) {
							Log.e(Tag, "========天气对象为空");
							weatherEntity.setLastTime(System.currentTimeMillis());
							weatherEntity.setLat(location.getLatitude());
							weatherEntity.setLng(location.getLongitude());
							weatherEntity.setCountry(86);
							weatherEntity.setTag("ONLINE");
							if (address != null) {
								weatherEntity
										.setRegion(MyApplication.mProvinceMap
												.get(address.getSubLocality()));
							} else {
								weatherEntity
										.setRegion(MyApplication.mProvinceMap
												.get("闸北区"));
							}

							DBService.savaWeatherInfo(weatherEntity);
						} else {
							weatherEntity = new WeatherEntity();
							weatherEntity.setLastTime(System
									.currentTimeMillis());
							weatherEntity.setLat(location.getLatitude());
							weatherEntity.setLng(location.getLongitude());
							weatherEntity.setCountry(86);
							weatherEntity.setTemperature(20);
							weatherEntity.setHumidity(50);
							weatherEntity.setUv("弱");
							weatherEntity.setTag("OFFLINE");
							if (address != null) {
								weatherEntity
										.setRegion(MyApplication.mProvinceMap
												.get(address.getSubLocality()));
							} else {
								weatherEntity.setRegion("200000");
							}

						}
					}
				});
		requestWeatherService.doRequestWeather();
	}

	// 初始化天气服务类
	private void initWeatherService() {
		// 判断网络状态
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		weatherEntity = DBService.findWeatherInfo();
		// 网络连接异常
		if (networkInfo == null || !networkInfo.isConnected()) {
			if (weatherEntity == null) {
				weatherEntity = new WeatherEntity();
				weatherEntity.setCountry(86);
				weatherEntity.setHumidity(40);
				weatherEntity.setLastTime(System.currentTimeMillis());
				weatherEntity.setLat(31.2);
				weatherEntity.setLng(121.5);
				weatherEntity.setRegion("200000");
				weatherEntity.setUv("弱");
				weatherEntity.setTemperature(20);
				weatherEntity.setTag("OFFLINE");
				Log.e(Tag, "默认的天气信息：" + weatherEntity.toString());
			}

		} else {
			sendRequestWeather();
		}
	}

	private Location requestLocation() {
		try {
			Location location = null;
			LocationManager locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				location = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (location != null) {
					return location;
				}
			} else {
				LocationListener locationListener = new LocationListener() {

					// Provider被enable时触发此函数，比如GPS被打开
					@Override
					public void onProviderEnabled(String arg0) {

					}

					// Provider被disable时触发此函数，比如GPS被关闭
					@Override
					public void onProviderDisabled(String arg0) {

					}

					// 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
					@Override
					public void onLocationChanged(Location arg0) {

					}

					// Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
					@Override
					public void onStatusChanged(String arg0, int arg1,
							Bundle arg2) {

					}
				};
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 1000, 0,
						locationListener);
				location = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (location != null) {
					getLocationInfo(location);
					return location;
				}
			}

		} catch (Exception e) {

		}
		return null;

	}

	private void getLocationInfo(Location location) {
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		try {
			List<Address> addresses = geocoder.getFromLocation(
					location.getLatitude(), location.getLongitude(), 1);
			if (addresses.size() > 0) {
				address = addresses.get(0);
				Log.e("位置信息11：", "============="+address.getAdminArea());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
