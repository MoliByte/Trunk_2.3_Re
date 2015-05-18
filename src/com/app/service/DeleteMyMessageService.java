package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.beabox.hjy.tt.R;

public class DeleteMyMessageService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	private String TAG="DeleteMyMessageService";
	
	public DeleteMyMessageService(Context context, Integer mTag,
			HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	
	public void delete(String token,long id){
		try{
			String url=context.getResources().getString(R.string.delete_mine_message)+"?client=2";
			Log.e(TAG, "===========删除消息url:"+url);
			Map<String,String> mapHead = new HashMap<String,String>();
			mapHead.put("Authorization", "Token " + token);
			JSONObject param = new JSONObject() ;
			param.put("action", "deleteMessage");
			param.put("id", id);
			
			StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			HttpClientUtils client = new HttpClientUtils();
			
			client.post_with_head_and_body(context, mTag, url, mapHead, bodyEntity, this);
			Log.e(TAG, "===========Token:"+token);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void requestComplete(Object tag, int statusCode, Object header,
			Object result, boolean complete) {
		callback.dataCallBack(tag, statusCode, result);
		Log.e(TAG, "============删除返回："+statusCode+"  "+result.toString());
	}
}
