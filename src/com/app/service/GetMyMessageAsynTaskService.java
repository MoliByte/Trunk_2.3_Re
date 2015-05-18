package com.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.MyMessageEntity;
import com.app.base.init.ACache;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class GetMyMessageAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "MyMessageAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public GetMyMessageAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void doMyMessage(String token,int page) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String v1_my_message = context.getResources().getString(R.string.v1_message)+"?page="+page+"&client=2";
			HttpClientUtils client = new HttpClientUtils();
			Map<String,String> map = new HashMap<String,String>() ;
			map.put("Authorization", "Token " + token);
			Log.e(TAG, "v1_my_message------->"+HttpClientUtils.BASE_URL+v1_my_message);
			client.get_with_head(context, mTag, v1_my_message,
					map, GetMyMessageAsynTaskService.this);
		} catch (Exception e) {
			Log.e(TAG, "doMyMessage error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object body, boolean complete) {
		if(null != this.callback){
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(body)) ;
		}
	}
	
	/**
	 * @param result
	 * @return
	 */
	public ArrayList<MyMessageEntity> parseJsonObject(Object body){
		try {
			JSONObject bodyObj = new JSONObject(body.toString());
			JSONArray array = bodyObj.optJSONArray("items");
			
			ArrayList<MyMessageEntity> list = new ArrayList<MyMessageEntity>();
			MyMessageEntity entity = null ;
			for (int i = 0; i < array.length(); i++) {
				entity = new MyMessageEntity();
				JSONObject obj = array.optJSONObject(i);//array.getJSONObject(i);
				entity.setId(obj.optLong("id"));
				entity.setFrom_uid(obj.optLong("from_uid"));
				entity.setFrom_nick(obj.optString("from_nick"));
				entity.setTo_uid(obj.optLong("to_uid"));
				entity.setTitle(obj.optString("title"));
				entity.setContent(obj.optString("content"));
				entity.setMsg_link(obj.optString("msg_link"));
				entity.setStatus(obj.optInt("status"));
				entity.setDeleted(obj.optInt("deleted"));
				entity.setCreate_time(obj.optString("create_time"));
				
				entity.setDetails(obj.optString("content"));
				entity.setReleaseTime(obj.optString("create_time"));
				entity.setUsername(obj.optString("from_nick"));
				entity.setUserPic(ACache.get(context).getAsString("avatar")+"");
				
				list.add(entity);
				entity = null ;
			}
			return list ;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return null ;
		}
	}
}
