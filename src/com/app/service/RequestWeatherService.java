package com.app.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.WeatherEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class RequestWeatherService implements HttpAysnTaskInterface{
	private final static String TAG = "RequestWeatherService" ;
	private Integer mTag;
	
	private StringBuffer url1 = new StringBuffer() ; //"/api_interface.php?module=weather&location=";
	private Context context;
	private HttpAysnResultInterface callback ;

	public RequestWeatherService(Context context,String city,Integer mTag,HttpAysnResultInterface callback) {
		this.context=context;
		this.callback = callback;
		this.mTag=mTag;
		url1.append(context.getResources().getString(R.string.TEST_SKIN)).append ("?module=weather&location=").append(city);
	}
		
	public RequestWeatherService(Context context,double longitude, double latitude,Integer mTag,HttpAysnResultInterface callback) {
		this.context=context;
		this.callback = callback;
		this.mTag=mTag;
		//url = url1 + longitude + "," + latitude;
		url1.append(context.getResources().getString(R.string.TEST_SKIN)).append ("?module=weather&location=").append( longitude + "," + latitude);
		Log.e(TAG,"=========log:"+longitude+"lat:"+latitude);
	}
	
	//请求天气
	public void doRequestWeather(){
		if(!SystemTool.checkNet(context)){
			AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
			return ;
		}
		url1.append("&client=2");
		HttpClientUtils client = new HttpClientUtils();
		Log.e(TAG,"==========请求天气URL："+url1.toString());
		client.get(context, mTag, url1.toString(), null, RequestWeatherService.this);
	}
	@Override
	public void requestComplete(Object tag, int statusCode, Object header,Object result, boolean complete) {
		try {
			String resultString=result.toString().trim().replaceAll("/n", "").replaceAll("/r", "").replaceAll(" ", "");
			Log.e(TAG, "========请求天气返回结果："+resultString);
			callback.dataCallBack(tag, statusCode, parse(resultString));	
		} catch (Exception e) {
		}
	}
	
	private WeatherEntity parse(String result){
		try {
			JSONObject jsonObject=new JSONObject(result);
			JSONObject data=jsonObject.getJSONObject("data");
			WeatherEntity weather=new WeatherEntity();
			Log.e(TAG,"===========返回的天气温度："+data.getInt("temperature"));
			weather.setTemperature(data.getInt("temperature"));
			weather.setHumidity(data.getInt("humidity"));
			weather.setUv(data.getString("uv"));
			return weather;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG,"========解析天气异常");
		}
		return null;
	}
}