package com.app.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.ActivityDataEntity;
import com.avos.avoscloud.LogUtil.log;
import com.base.app.utils.StringUtil;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
import com.loopj.android.http.RequestParams;
/**
 * 获取活动列表
 * @author zhup
 *
 */
public class ActivityAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "ActivityAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public ActivityAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	/**
	 * 获取活动列表 
	 ************************************/
	public void doActivityDataList(int pageIndex) {
		
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			Long times = System.currentTimeMillis();
			String user_login_url =context.getResources().getString(R.string.v1_activity)+"?client=2&page="+pageIndex;
			RequestParams params = new RequestParams();
			params.put("times", times);
			HttpClientUtils client = new HttpClientUtils();
			
			client.get(context, mTag, user_login_url,
					params, ActivityAsynTaskService.this);
		} catch (Exception e) {
			
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode, Object header ,Object result, boolean complete) {
		if(null != this.callback){
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(result)) ;
			log.e(TAG, "==============活动数据："+result.toString());
		}
	}
	
	/**
	 * @param result
	 * @return
	 */
	public ArrayList<ActivityDataEntity> parseJsonObject(Object result){
		try {
			ArrayList<ActivityDataEntity> entitylist = new ArrayList<ActivityDataEntity>();
			JSONObject json_result = new JSONObject(result.toString());
			Log.e(TAG, "result---->"+result.toString());
			JSONArray items = json_result.optJSONArray("items");
			for (int i = 0; i < items.length(); i++) {
				JSONObject obj = items.getJSONObject(i);
				ActivityDataEntity entityInfo = new ActivityDataEntity();
				entityInfo.setId(obj.optLong("id"));
				entityInfo.setCategory(StringUtil.getactivityStatus(obj.optInt("status")));
				entityInfo.setStatus(obj.optInt("status"));
				entityInfo.setCategory_name("试用妆");
				entityInfo.setActivity_details(obj.optString("content"));
				entityInfo.setActivity_img(obj.optString("imgURL"));
				entityInfo.setActivity_left_time(obj.optString("end_time"));
				entityInfo.setActivity_name(obj.optString("title"));
				entityInfo.setActivity_title(obj.optString("title"));
				entityInfo.setView_count(obj.optInt("view_count"));
				entityInfo.setComment_count(obj.optInt("comment_count"));
				entityInfo.setSupport_count(obj.optInt("praise_count"));
				entityInfo.setPublic_time(obj.optString("public_time"));
				entityInfo.setCreate_time(obj.optString("create_time"));
				entityInfo.setType(obj.optString("type"));
				entityInfo.setAuthor_id(obj.optLong("author_id"));
				entityInfo.setAuthor_nick(obj.optString("author_nick"));
				entityInfo.setContent(obj.optString("content"));
				entityInfo.setParticipant("已有"+obj.optInt("joincount")+"人参加");
				if(obj.optInt("status") != 99){
					entitylist.add(entityInfo);
				}
				
				entityInfo = null ;
			}
			
			return entitylist ;
		} catch (Exception e) {
			return null ;
		}
	}
}
