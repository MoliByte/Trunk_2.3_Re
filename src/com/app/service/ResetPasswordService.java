package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
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
 * 重新设置密码
 * @author zhupei
 *
 */
public class ResetPasswordService implements HttpAysnTaskInterface{
	
	private final static String TAG = "ResetPasswordService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public ResetPasswordService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	//密码重置
	public void doResetPassword(final String token , final String verify_code,final String newPassword) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			final String v1_password = context.getResources().getString(R.string.v1_password)+"?client=2";
			HttpClientUtils client = new HttpClientUtils();
			Map<String,String> headermap = new HashMap<String,String>() ;
			headermap.put("Content-Type", "application/json");
			headermap.put("Authorization", "Token " + token);
			Map<String,String> bodymap = new HashMap<String,String>() ;
			bodymap.put("verify_code", verify_code);
			bodymap.put("new_password", newPassword);
			//headermap.put("Authorization", "Token " + token);
			Log.e(TAG, "v1_setret_password------->"+HttpClientUtils.BASE_URL+v1_password);
//			client.put(context, mTag, v1_password,
//					headermap, bodymap,ResetPasswordService.this);
			JSONObject paramJson = new JSONObject();
			paramJson.put("verify_code", verify_code);
			paramJson.put("new_password", newPassword);
			
			client.httpPutOriginal(context, mTag, v1_password, headermap, new StringEntity(paramJson.toString()), this);
			
			/*new Thread(new Runnable() {
				@Override
				public void run() {
					Looper.prepare() ;
					try {
						HttpClient  httpclient = new DefaultHttpClient();
						HttpPut httpput = new HttpPut(HttpClientUtils.BASE_URL+v1_password);
					
						//HttpPost httppost = new HttpPost(HttpClientUtils.BASE_URL+v1_password);
						httpput.addHeader("Content-Type", "application/json");
						httpput.addHeader("Authorization", "Token " + token);
						JSONObject paramJson = new JSONObject();
						paramJson.put("verify_code", verify_code);
						paramJson.put("new_password", newPassword);
						
						httpput.setEntity(new StringEntity(paramJson.toString()));
						HttpResponse response;
						response = httpclient.execute(httpput);
						//检验状态码，如果成功接收数据
						int code = response.getStatusLine().getStatusCode();
						Log.e(TAG, "code = "+code+"");
						if (code == 200 || code == 201) {
							String rev = EntityUtils.toString(response.getEntity());
							ResetPasswordService.this
							.requestComplete(ResetPasswordService.this.mTag,
									code, response.getAllHeaders(),
									rev.toString(), true);
							
							Log.e(TAG, "result = "+rev);
						}else{
							ResetPasswordService.this
							.requestComplete(ResetPasswordService.this.mTag,
									code, response.getAllHeaders(),
									"error", false);
						}
					} catch (Exception e) {
						ResetPasswordService.this
						.requestComplete(ResetPasswordService.this.mTag,
								400, null,
								"error", false);
						Log.e(TAG, e.toString());
					}
					Looper.loop();
				}
			}).start();*/
		} catch (Exception e) {
			Log.e(TAG, "ResetPassword error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object body, boolean complete) {
		if(null != this.callback){
			
			Log.e(TAG, "ResetPasswordService  Body --->"+body + ",\n statusCode -->" + statusCode);
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(body)) ;
		}
	}
	
	/**
	 * @param result
	 * @return
	 */
	public Object parseJsonObject(Object body){
		try {
			Log.e(TAG, "ResetPasswordService --->"+body);
			return body ;
		} catch (Exception e) {
			return null ;
		}
	}
}
