package com.app.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.TestPostEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
public class PostTestResultService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;
	private HttpAysnResultInterface callback;
	private static String TAG="PostTestResultService";
	
	public PostTestResultService(Context context,Integer mTag,HttpAysnResultInterface callback){
		this.context=context;
		this.mTag=mTag;
		this.callback=callback;
	}
	
	public void doPostTestData(TestPostEntity postEntity,String token){
		try {
			
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			
			String url = context.getResources().getString(R.string.TEST_SKIN)+"?client=2" ; //"/api_interface.php";
			
			JSONObject param = new JSONObject() ;
			param.put("module", postEntity.getModule());
			param.put("area", postEntity.getArea());
			param.put("water", postEntity.getWater());
			param.put("oil", postEntity.getOil());
			param.put("elasticity", postEntity.getElasticity());
			param.put("lat", postEntity.getLat());
			param.put("lng", postEntity.getLng());
			param.put("temperature", postEntity.getTemperature());
			param.put("humidity", postEntity.getHumidity());
			param.put("ultraviolet", postEntity.getUltraviolet());
			param.put("data_flag", postEntity.getData_flag());
			param.put("test_time", postEntity.getTest_time());
			param.put("country", postEntity.getCountry());
			param.put("region", postEntity.getRegion());
			param.put("st_water", postEntity.getSt_water());
			param.put("st_oil", postEntity.getSt_oil());
			param.put("os", postEntity.getOs());
			param.put("os_version", postEntity.getOs_version());
			param.put("app_version", postEntity.getApp_version());
			param.put("device_info", postEntity.getDevice_info());
			param.put("appid", postEntity.getAppid());
			param.put("skin_type", postEntity.getSkin_type());
			
			Map<String,String> mapHead = new HashMap<String,String>() ;
			mapHead.put("Authorization", "Token " + token);
			
			HttpClientUtils client=new HttpClientUtils();
			StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			client.post_with_head_and_body(context, mTag, url, mapHead, bodyEntity, PostTestResultService.this);
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void requestComplete(Object tag, int statusCode, Object header,Object result, boolean complete) {
		callback.dataCallBack(tag, statusCode, result);
		Log.e(TAG, "================上传测试记录的返回码："+statusCode);
		new ParserIntegral(context).parse(result.toString());
	}
}