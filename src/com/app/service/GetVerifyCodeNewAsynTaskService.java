package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.os.Looper;
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
public class GetVerifyCodeNewAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "GetVerifyCodeNewAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public GetVerifyCodeNewAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void doGetVerifyCode(final String tel_num,final int type) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			final String v1_verifyCode = context.getResources().getString(R.string.v1_new_verifyCode)+"?client=2";
//			HttpClientUtils client = new HttpClientUtils();
			JSONObject paramJson = new JSONObject();
			if(type == HttpTagConstantUtils.REGISTER_TYPE){
				paramJson.put("type", "register");
			}else{
				paramJson.put("type", "resetpwd");
			}
			paramJson.put("action", "verifycode");
			paramJson.put("mobile", tel_num);
			
			Map<String,String> headMap = new HashMap<String,String>();
			headMap.put("Content-Type", "application/json; charset=utf-8");
			
			StringEntity entityParams = new StringEntity(paramJson.toString(),"UTF-8");
			Log.e(TAG, "entityParams------->"+paramJson.toString());
			Log.e(TAG, "v1_verifyCode------->"+HttpClientUtils.NEW_BASE_URL+v1_verifyCode);
			Log.e(TAG, "entityParams------->"+entityParams.toString());
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					Looper.prepare() ;
					try {
						HttpClient  httpclient = new DefaultHttpClient();
						HttpPost httppost = new HttpPost(v1_verifyCode/*"http://v2.api.skinrun.me/api/member"*/);
						httppost.addHeader("Content-Type", "application/json");
						JSONObject paramJson = new JSONObject();
						if(type == HttpTagConstantUtils.REGISTER_TYPE){
							paramJson.put("type", "register");
						}else{
							paramJson.put("type", "resetpwd");
						}
						paramJson.put("action", "verifycode");
						paramJson.put("mobile", tel_num);
						
						httppost.setEntity(new StringEntity(paramJson.toString()));
						HttpResponse response;
						response = httpclient.execute(httppost);
						//检验状态码，如果成功接收数据
						int code = response.getStatusLine().getStatusCode();
						Log.e(TAG, "code = "+code+"");
						if (code == 200 || code == 201) {
							String rev = EntityUtils.toString(response.getEntity());
							GetVerifyCodeNewAsynTaskService.this
							.requestComplete(GetVerifyCodeNewAsynTaskService.this.mTag,
									code, response.getAllHeaders(),
									rev.toString(), true);
							
							Log.e(TAG, "result = "+rev);
						}else{
							GetVerifyCodeNewAsynTaskService.this
							.requestComplete(GetVerifyCodeNewAsynTaskService.this.mTag,
									code, response.getAllHeaders(),
									"error", false);
						}
					} catch (Exception e) {
						GetVerifyCodeNewAsynTaskService.this
						.requestComplete(GetVerifyCodeNewAsynTaskService.this.mTag,
								400, null,
								"error", false);
						Log.e(TAG, e.toString());
					}
					Looper.loop();
				}
			}).start();
			
//			client.post(context, mTag, v1_verifyCode,entityParams, GetVerifyCodeNewAsynTaskService.this);
			
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
