package com.app.service;

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

import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
/**
 * 校验验证码
 * @author zhupei
 *
 */
public class PutVerifyCodeNewAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "PutVerifyCodeNewAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public PutVerifyCodeNewAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void doPutVerifyCode(final String tel_num,final String verify_code) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			final String v1_new_verifyCode = context.getResources().getString(R.string.v1_new_verifyCode)+"?client=2";
//			HttpClientUtils client = new HttpClientUtils();
//			Map<String,String> headMap = new HashMap<String,String>();
//			headMap.put("Content-Type", "application/json");
//			
//			Map<String,String> bodyMap = new HashMap<String,String>();
//			bodyMap.put("phone", tel_num);
//			bodyMap.put("verifyCode", verify_code);
			
//			JSONObject json_obj = new JSONObject() ;
//			json_obj.put("phone", tel_num);
//			json_obj.put("verifyCode", verify_code);
//			StringEntity entityParams = new StringEntity(json_obj.toString());
			
//			Log.e(TAG, "v1_verifyCode------->"+HttpClientUtils.BASE_URL+v1_verifyCode);
//			client.put(context, mTag, v1_verifyCode,headMap,
//					bodyMap, PutVerifyCodeNewAsynTaskService.this);
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					Looper.prepare() ;
					try {
						HttpClient  httpclient = new DefaultHttpClient();
						HttpPost httppost = new HttpPost(v1_new_verifyCode/*"http://v2.api.skinrun.me/api/member"*/);
						httppost.addHeader("Content-Type", "application/json");
						JSONObject paramJson = new JSONObject();
						paramJson.put("action", "validatecode");
						paramJson.put("mobile", tel_num);
						paramJson.put("code", verify_code);
						
						httppost.setEntity(new StringEntity(paramJson.toString()));
						HttpResponse response;
						response = httpclient.execute(httppost);
						//检验状态码，如果成功接收数据
						int code = response.getStatusLine().getStatusCode();
						Log.e(TAG, "code = "+code+"");
						if (code == 200 || code == 201) {
							String rev = EntityUtils.toString(response.getEntity());
							PutVerifyCodeNewAsynTaskService.this
							.requestComplete(PutVerifyCodeNewAsynTaskService.this.mTag,
									code, response.getAllHeaders(),
									rev.toString(), true);
							
							Log.e(TAG, "result = "+rev);
						}else{
							PutVerifyCodeNewAsynTaskService.this
							.requestComplete(PutVerifyCodeNewAsynTaskService.this.mTag,
									code, response.getAllHeaders(),
									"error", false);
						}
					} catch (Exception e) {
						PutVerifyCodeNewAsynTaskService.this
						.requestComplete(PutVerifyCodeNewAsynTaskService.this.mTag,
								400, null,
								"error", false);
						Log.e(TAG, e.toString());
					}
					Looper.loop();
				}
			}).start();
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
	public Object parseJsonObject(Object body){
		try {
			
			return body ;
		} catch (Exception e) {
			return null ;
		}
	}
}
