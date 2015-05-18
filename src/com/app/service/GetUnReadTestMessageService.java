package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.UnReadMessageEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class GetUnReadTestMessageService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	public GetUnReadTestMessageService(Context context, Integer mTag,
			HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	
	public void doGetUnread(String token,String test_type){
		try{
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String url=context.getResources().getString(R.string.mine_message)+"?client=2";
			
			Map<String,String> mapHead = new HashMap<String,String>();
			mapHead.put("Authorization", "Token " + token);
			
			JSONObject param = new JSONObject() ;
			param.put("action", "unreadMsg");
			param.put("test_type", test_type);
			
			StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			HttpClientUtils client = new HttpClientUtils();
			
			client.post_with_head_and_body(context, mTag, url, mapHead, bodyEntity, this);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void requestComplete(Object tag, int statusCode, Object header,
			Object result, boolean complete) {
		callback.dataCallBack(tag, statusCode, parse(result.toString()));
	}
	
	private UnReadMessageEntity parse(String json){
		try {
			JSONObject jsonObject=new JSONObject(json);
			UnReadMessageEntity entity=new UnReadMessageEntity();
			entity.setUnread(jsonObject.getInt("unread"));
			Log.e("UnReadMessageEntity", "=========未读消息条数："+entity.getUnread());
			
			entity.setLast_uid(jsonObject.getString("last_uid"));
			entity.setLast_user_avatar(jsonObject.getString("last_user_avatar"));
			return entity;
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
