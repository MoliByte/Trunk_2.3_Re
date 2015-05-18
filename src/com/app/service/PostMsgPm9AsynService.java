package com.app.service;

import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.app.base.init.MyApplication;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.beabox.hjy.tt.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
/**
 * 发送晚九点消息
 * @author zhupei
 *
 */
public class PostMsgPm9AsynService implements HttpAysnTaskInterface{
	
	private final static String TAG = "PostMsgPm9AsynService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public PostMsgPm9AsynService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void post_msg(Map<String,Object> paraMap) {
		try {
			final String v2_pm9_message = context.getResources().getString(R.string.v2_pm9_message)+"?client=2";
//			Log.e(TAG, "v2_pm9_message------->"+HttpClientUtils.NEW_BASE_URL+v2_pm9_message);
//			final StringBuffer final_url = new StringBuffer(HttpClientUtils.NEW_BASE_URL);
//			final_url.append(v2_pm9_message);
			
			JSONObject paramJson = new JSONObject();
			for(String key : paraMap.keySet()){
				paramJson.put(key, paraMap.get(key));
			}
			
			 Log.e(TAG, paramJson.toString());
			StringEntity entity = new StringEntity(paramJson.toString(),"UTF-8");
			
			HttpClientUtils.postNewXml(context,v2_pm9_message,entity,new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					Log.e(TAG, "post msg success.");
				}
				
				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					Log.e(TAG, "post msg failed.");
					
				}
			});
			//client.post_with_head(context, mTag, final_url.toString(),paraMap, PostMsgPm9AsynService.this);
		} catch (Exception e) {
			Log.e(TAG, "PostMsgPm9AsynService error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object body, boolean complete) {
		if(null != this.callback){
			if(statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED){
				MyApplication.isVisitor = false ;
			}else{
				MyApplication.isVisitor = true ;
			}
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(header,body)) ;
		}
	}
	
	/** KJ
	 * @param result
	 * @return
	 */
	public Object parseJsonObject(Object header,Object result){
		try {
			Log.e(TAG, result.toString());
			return  result;
		} catch (Exception e) {
			return null ;
		} finally {
			
		}
	}
}
