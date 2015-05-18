package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;

import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class GoToHomeService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public GoToHomeService(Context context, Integer mTag,
			HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	
	public void goToHome(String token,long test_id){
		try{
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			
			String url=context.getResources().getString(R.string.skin_go_to_home)+"?client=2";
			Map<String,String> mapHead = new HashMap<String,String>();
			mapHead.put("Authorization", "Token " + token);
			
			JSONObject param = new JSONObject() ;
			param.put("action", "toIndex");
			param.put("test_id", test_id);
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
		callback.dataCallBack(tag, statusCode, result);
	}
}
