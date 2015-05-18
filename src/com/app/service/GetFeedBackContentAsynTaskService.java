package com.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;

import com.app.base.entity.SuggestionFeedBackDataEntity;
import com.app.base.init.ACache;
import com.avos.avoscloud.LogUtil.log;
import com.base.app.utils.EasyLog;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
import com.loopj.android.http.RequestParams;
/**
 * 获取反馈内容 Android V2.3
 * @author zhup
 *
 */
public class GetFeedBackContentAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "GetFeedBackContentAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public GetFeedBackContentAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	/**
	 * 获取反馈内容
	 ************************************/
	public void doDataList(int pageIndex) {
		
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			
			String api_feedback = context.getResources().getString(R.string.api_feedback)+"?client=2&page="+pageIndex;
			
			JSONObject param = new JSONObject() ;
			param.put("action", "feedbackList");
			param.put("page", pageIndex);
			
			
			String token = ACache.get(context).getAsString("Token") ;
			EasyLog.e(token);
			
			Map<String,String> mapHead = new HashMap<String,String>() ;
			
			mapHead.put("Authorization", "Token " + token);
			mapHead.put("Content-Type", "application/json");
			
			EasyLog.e(param.toString());
			StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			
			HttpClientUtils client = new HttpClientUtils();
			
			client.new_post_with_head_and_body(context, mTag, api_feedback,mapHead,
					bodyEntity, GetFeedBackContentAsynTaskService.this);
		} catch (Exception e) {
			EasyLog.e(e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode, Object header ,Object result, boolean complete) {
		if(null != this.callback){
			/*try {
				JSONObject rootObject = new JSONObject(result.toString());
				int code = rootObject.optInt("code") ;
				if(code == HttpStatus.SC_OK 
						|| code == HttpStatus.SC_CREATED){
					EasyLog.e("==============反馈数据："+result.toString());
				}else{
					EasyLog.e("==============反馈数据："+statusCode);
				}
			} catch (Exception e) {
				EasyLog.e(e.toString());
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.data_error)).show();
			}*/
			
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(result.toString())) ;
			EasyLog.e("statusCode = "+statusCode+"==============反馈数据："+result.toString());
			
		}
	}
	
	/**
	 * @param result
	 * @return
	 */
	public ArrayList<SuggestionFeedBackDataEntity> parseJsonObject(Object result){
		try {
			ArrayList<SuggestionFeedBackDataEntity> entitylist = new ArrayList<SuggestionFeedBackDataEntity>();
			
			JSONObject rootObject = new JSONObject(result.toString());
			
			JSONArray itemsArray = rootObject.optJSONArray("items");
			
			for (int i = 0; i < itemsArray.length(); i++) {
				JSONObject _obj = itemsArray.optJSONObject(i);
				SuggestionFeedBackDataEntity entity = new SuggestionFeedBackDataEntity();
				entity.setContent(_obj.optString("content"));
				entity.setGuanjia_img(_obj.optString("guanjia_img"));
				entity.setUser_img(_obj.optString("user_img"));
				entity.setUname(_obj.optString("uname"));
				entity.setId(_obj.optInt("id"));
				entity.setType(_obj.optInt("type"));
				entity.setRelease_time(_obj.optString("release_time"));
				entitylist.add(entity);
				entity = null ;
			}
			return entitylist ;
		} catch (Exception e) {
			EasyLog.e(e.getStackTrace()+e.toString());
			return null ;
		}
	}
}
