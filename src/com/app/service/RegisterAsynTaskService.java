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
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
/**
 * 用户提交注册
 * @author zhupei
 *
 */
public class RegisterAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "RegisterAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public RegisterAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void doRegisterAsynTaskService(final String username,final String verify_code,final String password) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			final String v1_register = context.getResources().getString(R.string.v1_account)+"?client=2";
//			HttpClientUtils client = new HttpClientUtils();
			JSONObject paramJson = new JSONObject();
			paramJson.put("loginName", username);
			paramJson.put("password", password);
			paramJson.put("verify_code", verify_code);
//			StringEntity entityParams = new StringEntity(paramJson.toString(),"UTF-8");
//			Log.e(TAG, "v1_register------->"+HttpClientUtils.BASE_URL+v1_register);
//			Log.e(TAG, "v1_register_params------->"+username+";"+ password+";"+ verify_code);
//			client.post(context, mTag, v1_register,
//					entityParams, RegisterAsynTaskService.this);
			new Thread(new Runnable() {
				@Override
				public void run() {
					Looper.prepare() ;
					try {
						HttpClient  httpclient = new DefaultHttpClient();
						HttpPost httppost = new HttpPost(HttpClientUtils.BASE_URL+v1_register);
						httppost.addHeader("Content-Type", "application/json");
						JSONObject paramJson = new JSONObject();
						paramJson.put("loginName", username);
						paramJson.put("password", password);
						paramJson.put("verify_code", verify_code);
						
						httppost.setEntity(new StringEntity(paramJson.toString()));
						HttpResponse response;
						response = httpclient.execute(httppost);
						//检验状态码，如果成功接收数据
						int code = response.getStatusLine().getStatusCode();
						Log.e(TAG, "code = "+code+"");
						if (code == 200 || code == 201) {
							String rev = EntityUtils.toString(response.getEntity());
							RegisterAsynTaskService.this
							.requestComplete(RegisterAsynTaskService.this.mTag,
									code, response.getAllHeaders(),
									rev.toString(), true);
							
							Log.e(TAG, "result = "+rev);
						}else{
							RegisterAsynTaskService.this
							.requestComplete(RegisterAsynTaskService.this.mTag,
									code, response.getAllHeaders(),
									"error", false);
						}
					} catch (Exception e) {
						RegisterAsynTaskService.this
						.requestComplete(RegisterAsynTaskService.this.mTag,
								400, null,
								"error", false);
						Log.e(TAG, e.toString());
					}
					Looper.loop();
				}
			}).start();
			
		} catch (Exception e) {
			Log.e(TAG, "doRegisterAsynTaskService error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object body, boolean complete) {
		if(null != this.callback){
			Log.e(TAG, ""+statusCode +">>>>>>>>>"+ body.toString());
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(header)) ;
		}
		
		new ParserIntegral(context).parse(body.toString());
		
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
