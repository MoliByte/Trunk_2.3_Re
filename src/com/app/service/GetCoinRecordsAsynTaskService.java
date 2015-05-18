package com.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.GoldCoinsEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
/**
 * 获取用户金币记录
 * @author zhupei
 *
 */
public class GetCoinRecordsAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "GetCoinRecordsAsynTaskService-->";
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public GetCoinRecordsAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void doCoinRecords(String token,int pageIndex,int pageSize) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String url = context.getResources().getString(R.string.my_integral_record)+"?client=2";
			Map<String,String> mapHead = new HashMap<String,String>();
			mapHead.put("Authorization", "Token " + token);
			
			JSONObject param = new JSONObject() ;
			param.put("action", "myIntegralLog");
			param.put("page", pageIndex);
			param.put("pagesize", pageSize);
			
			StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			HttpClientUtils client = new HttpClientUtils();
			client.post_with_head_and_body(context, mTag, url, mapHead, bodyEntity, this);
			
			
		} catch (Exception e) {
			Log.e(TAG, "doCoinRecords error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object body, boolean complete) {
		if(null != this.callback){
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(body)) ;
			Log.e(TAG, "==============积分记录："+body.toString());
		}
	}
	
	/**
	 * @param result
	 * @return
	 */
	public ArrayList<GoldCoinsEntity> parseJsonObject(Object body){
		try {
			
			JSONObject jsonObject = new JSONObject(body.toString());
			JSONArray items = jsonObject.getJSONArray("items");
			
			ArrayList<GoldCoinsEntity> data = new ArrayList<GoldCoinsEntity>();
			for(int i=0;i<items.length();i++){
				GoldCoinsEntity entity=new GoldCoinsEntity();
				JSONObject item=items.getJSONObject(i);
				
				entity.setId(""+item.getString("id"));
				entity.setCredit(""+item.getString("credit"));
				entity.setCreate_time(""+item.getString("create_time"));
				entity.setType(""+item.getString("type"));
				
				data.add(entity);
			}
			
			
			return data ;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return null ;
		}
	}
}
