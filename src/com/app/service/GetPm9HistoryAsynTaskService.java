package com.app.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;

import com.app.base.entity.Pm9HistoryDataEntity;
import com.app.base.entity.SuggestionFeedBackDataEntity;
import com.avos.avoscloud.LogUtil.log;
import com.base.app.utils.EasyLog;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.RequestParams;
/**
 * 获取PM9历史记录 Android V2.3
 * @author zhup
 *
 */
public class GetPm9HistoryAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "GetPm9HistoryAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public GetPm9HistoryAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	/**
	 * 获取内容
	 ************************************/
	public void doDataList(int pageIndex) {
		
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			Long times = System.currentTimeMillis();
			String user_login_url =context.getResources().getString(R.string.pm9_history)+"?page="+pageIndex+"&client=2";
			RequestParams params = new RequestParams();
			params.put("times", times);
			HttpClientUtils client = new HttpClientUtils();
			
			client.new_get(context, mTag, user_login_url,
					params, GetPm9HistoryAsynTaskService.this);
		} catch (Exception e) {
			
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode, Object header ,Object result, boolean complete) {
		if(null != this.callback){
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(result)) ;
			log.e(TAG, "==============PM9历史数据："+result.toString());
		}
	}
	
	/**
	 * @param result
	 * @return
	 */
	public ArrayList<Pm9HistoryDataEntity> parseJsonObject(Object result){
		ArrayList<Pm9HistoryDataEntity> entitylist = new ArrayList<Pm9HistoryDataEntity>();
		try {
			
			JSONObject object = new JSONObject(result.toString());
			
			JSONObject items = object.optJSONObject("items");
			
			JSONArray listArray = items.optJSONArray("list");
			
			for (int i = 0; i < listArray.length(); i++) {
				JSONObject itemJsonObject = listArray.optJSONObject(i);
				Pm9HistoryDataEntity entity = new Gson().fromJson(itemJsonObject.toString(), Pm9HistoryDataEntity.class) ;
				entitylist.add(entity);
			}
			
			return entitylist ;
		} catch (Exception e) {
			EasyLog.e("parseJsonObject-->"+e.toString());
			return null ;
		}
	}
}
