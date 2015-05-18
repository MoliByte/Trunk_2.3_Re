package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
/**
 * 校验验证码
 * @author zhupei
 *
 */
public class PutVerifyCodeAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "PutVerifyCodeAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public PutVerifyCodeAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void doPutVerifyCode(String tel_num,String verify_code) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String v1_verifyCode = context.getResources().getString(R.string.v1_verifyCode)+"?client=2";
			HttpClientUtils client = new HttpClientUtils();
			Map<String,String> headMap = new HashMap<String,String>();
			headMap.put("Content-Type", "application/json");
			
			Map<String,String> bodyMap = new HashMap<String,String>();
			bodyMap.put("phone", tel_num);
			bodyMap.put("verifyCode", verify_code);
			
//			JSONObject json_obj = new JSONObject() ;
//			json_obj.put("phone", tel_num);
//			json_obj.put("verifyCode", verify_code);
//			StringEntity entityParams = new StringEntity(json_obj.toString());
			
			Log.e(TAG, "v1_verifyCode------->"+HttpClientUtils.BASE_URL+v1_verifyCode);
			client.put(context, mTag, v1_verifyCode,headMap,
					bodyMap, PutVerifyCodeAsynTaskService.this);
		} catch (Exception e) {
			Log.e(TAG, "doPutVerifyCode error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object body, boolean complete) {
		if(null != this.callback){
			Log.e(TAG, "输入手机号返回的code>>>>>>"+statusCode);
			Log.e(TAG, "输入手机号返回的Body>>>>>>"+body.toString());
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(body)) ;
		}
	}
	
	/**
	 * @param result
	 * @return
	 */
	public String parseJsonObject(Object body){
		try {
			JSONObject obj = new JSONObject(body.toString());
			return obj.optString("message") ;
		} catch (Exception e) {
			return null ;
		}
	}
}
