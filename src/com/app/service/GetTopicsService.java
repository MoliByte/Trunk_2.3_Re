package com.app.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.TopicsEntity;
import com.app.base.entity.UserEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
import com.loopj.android.http.RequestParams;
/**
 * 获取晚九点主题
 * @author zhup
 *
 */
public class GetTopicsService implements HttpAysnTaskInterface{
	
	private final static String TAG = "GetTopicsService-->";
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public GetTopicsService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void getTopicsInfo(String date) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			StringBuffer v1_topics = new StringBuffer(context.getResources().getString(R.string.v1_topics));
			RequestParams pramas = new RequestParams() ;
			pramas.put("date", date);
			v1_topics.append("?"+pramas.toString());
			v1_topics.append("&client=2");
			HttpClientUtils client = new HttpClientUtils();
			//Map<String,String> map = new HashMap<String,String>() ;
			//map.put("Authorization", "Token " + Token);
			Log.e(TAG, "获取用户基本信息 ------->"+HttpClientUtils.BASE_URL+v1_topics);
			client.get(context, mTag, v1_topics.toString(),pramas, GetTopicsService.this);
//			HttpClientUtils.getInstance().httpGetOriginal(context, mTag, v1_topics.toString(), new HashMap<String,String>(), this);
		} catch (Exception e) {
			Log.e(TAG, "GetTopicsService error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode, Object header ,Object result, boolean complete) {
		if(null != this.callback){
			Log.e(TAG, "晚九点基本信息---->"+"statusCode>>"+statusCode+"\n>>>"+result.toString());
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(result)) ;
		}
	}
	
	/**
	 * @param result
	 * @return
	 */
	public TopicsEntity parseJsonObject(Object result){
		try {
			TopicsEntity entity  = new TopicsEntity();
			JSONObject obj = new JSONObject(result.toString());
			
			ArrayList<UserEntity> prize_users = new ArrayList<UserEntity>();
			
			JSONArray items = obj.optJSONArray("items");
			for (int i = 0; i < items.length(); i++) {
				JSONObject item = items.getJSONObject(i);
				entity.setId(item.optLong("id"));
				entity.setTitle(item.optString("title").replaceAll("\r\n", ""));
				entity.setContent(item.optString("content"));
				entity.setImg_url(item.optString("img_url"));
				entity.setOpen_date(item.optString("open_date"));
				entity.setStart_time(item.optString("start_time"));
				entity.setEnd_time(item.optString("end_time"));
				entity.setCreate_time(item.optString("create_time"));
				entity.setCreate_uid(item.optLong("create_uid"));
				entity.setCreate_user(item.optString("create_user"));
				entity.setLastup_time(item.optString("lastup_time"));
				entity.setLastup_uid(item.optLong("lastup_uid"));
				entity.setLastup_user(item.optString("lastup_user"));
				try {
					JSONArray  users = new JSONArray(item.optString("prize_user"));//item.optJSONArray("prize_user");
					if(users != null ){
						for (int j = 0; j < users.length(); j++) {
							UserEntity entity_ = new UserEntity();
							entity_.setU_id(users.getJSONObject(j).optInt("uid"));
							entity_.setNiakname(users.getJSONObject(j).optString("nickname"));
							prize_users.add(entity_);
						}
					}
				} catch (Exception e) {
					Log.e(TAG, "prize_user parse exception ");
				}
				
			}
			entity.setPrize_user(prize_users);//获奖用户名单
			
			return entity ;
		} catch (Exception e) {
			return null ;
		}
	}
}
