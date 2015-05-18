package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.UserEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
/**
 * 获取用户测试数据信息
 * @author zhup
 *
 */
public class GetUserTestRecordService implements HttpAysnTaskInterface{
	
	private final static String TAG = "GetUserTestRecordService";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public GetUserTestRecordService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void getUserTestRecordInfo(String Token,String date) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String v1_test_record = context.getResources().getString(R.string.v1_test_record)+"?date="+date+"&client=2";
			HttpClientUtils client = new HttpClientUtils();
			Map<String,String> map = new HashMap<String,String>() ;
			map.put("Authorization", "Token " + Token);
			
			
			client.get_with_head(context, mTag, v1_test_record,
					map, GetUserTestRecordService.this);
		} catch (Exception e) {
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode, Object header ,Object result, boolean complete) {
		if(null != this.callback){
			this.callback.dataCallBack(tag,statusCode,result) ;
			Log.e(TAG, "===========获取用户测试信息" + result.toString());
		}
	}
	
	/**
	 * @param result
	 * @return
	 */
	private  UserEntity parseJsonObject(Object result){
		try {
			
			UserEntity entity  = new UserEntity();
			JSONObject obj = new JSONObject(result.toString());
			return entity ;
		} catch (Exception e) {
			return null ;
		}
	}
}
