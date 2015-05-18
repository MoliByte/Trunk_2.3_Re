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

import com.app.base.entity.LastTestDataEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
import com.skinrun.trunk.main.LastTestDataProvider;

public class GetLastTestDateService implements HttpAysnTaskInterface{
	private String TAG="GetLastTestDateService";
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	private String token;
	
	public GetLastTestDateService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	
	public void doGetLastTestDate(String token){
		this.token=token;
		
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String v1_test_record = context.getResources().getString(R.string.TEST_SKIN)+"?client=2";
			HttpClientUtils client = new HttpClientUtils();
			Map<String,String> map = new HashMap<String,String>() ;
			map.put("Authorization", "Token " + token);
			JSONObject param = new JSONObject();
			param.put("module", "skin_lastday");
			StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			client.post_with_head_and_body(context, mTag, v1_test_record, map, bodyEntity, this);
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void requestComplete(Object tag, int statusCode, Object header,
			Object result, boolean complete) {
		
			callback.dataCallBack(tag, statusCode, parse(result.toString()));
			Log.e(TAG, "==========最近测试日期的返回码："+statusCode);
	}
	private LastTestDataEntity parse(String json){
		try {
			JSONObject jsonObject=new JSONObject(json);
			int code=jsonObject.getInt("code");
			if(code==200){
				JSONObject data=jsonObject.getJSONObject("data");
				
				LastTestDataEntity entity=new LastTestDataEntity();
				
				entity.setToken(token);
				entity.setFace_lastday(data.getString("face_lastday"));
				entity.setFace_area(data.getString("face_area"));
				entity.setFace_water(data.getInt("face_water")/100);
				entity.setFace_oil(data.getInt("face_oil")/100);
				entity.setFace_elasticity(data.getInt("face_elasticity")/100);
				
				entity.setHand_lastday(data.getString("hand_lastday"));
				entity.setHand_area(data.getString("hand_area"));
				entity.setHand_water(data.getInt("hand_water")/100);
				entity.setHand_oil(data.getInt("hand_oil")/100);
				entity.setHand_elasticity(data.getInt("hand_elasticity")/100);
				
				//数据库缓存
				LastTestDataProvider.savaData(entity);
				
				Log.e(TAG, "==========解析出来的最近测试结果："+entity.toString());
				
				return entity;
			}else{
				return null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
























