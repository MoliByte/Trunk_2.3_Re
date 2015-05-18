package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
/**
 * 获取验证码
 * @author zhupei
 *
 */
public class GetVerifyCodeAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "GetVerifyCodeAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public GetVerifyCodeAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void doGetVerifyCode(String tel_num,int type) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String v1_verifyCode = context.getResources().getString(R.string.v1_verifyCode)+"?client=2";
			HttpClientUtils client = new HttpClientUtils();
			JSONObject paramJson = new JSONObject();
			if(type == HttpTagConstantUtils.REGISTER_TYPE){
				paramJson.put("type", "register");
				Log.i(TAG, "register");
			}else{
				paramJson.put("type", "resetpwd");
				Log.i(TAG, "resetpwd");
			}
			paramJson.put("phone", tel_num);
			
			Map<String,String> headMap = new HashMap<String,String>();
			headMap.put("Content-Type", "application/json");
			
			StringEntity entityParams = new StringEntity(paramJson.toString());
			Log.e(TAG, "entityParams------->"+paramJson.toString());
			Log.e(TAG, "v1_verifyCode------->"+HttpClientUtils.BASE_URL+v1_verifyCode);
			Log.e(TAG, "entityParams------->"+entityParams.toString());
			client.post(context, mTag, v1_verifyCode,entityParams, GetVerifyCodeAsynTaskService.this);
/*			client.post_with_head_and_body(context, mTag, v1_verifyCode,
					headMap,entityParams, GetVerifyCodeAsynTaskService.this);
*/		} catch (Exception e) {
			Log.e(TAG, "doGetVerifyCode error:" + e.toString());
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
			return body.toString() ;
		} catch (Exception e) {
			return null ;
		}
	}
}
