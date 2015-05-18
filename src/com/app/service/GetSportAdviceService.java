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

public class GetSportAdviceService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;
	private HttpAysnResultInterface callback;
	
	
	public GetSportAdviceService(Context context, Integer mTag,
			HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	public void doGetAdvice(String token,String age_factor){
		try{
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String url=context.getResources().getString(R.string.sport_advice)+"?client=2";
			Map<String,String> mapHead = new HashMap<String,String>() ;
			mapHead.put("Authorization", "Token " + token);
			JSONObject param = new JSONObject() ;
			param.put("age_factor", age_factor);
			
			StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			//请求
			HttpClientUtils client=new HttpClientUtils();
			client.post_with_head_and_body(context, mTag, url, mapHead, bodyEntity, GetSportAdviceService.this);
			
			
		}catch(Exception e){
			
		}
	}

	@Override
	public void requestComplete(Object tag, int statusCode, Object header,
			Object result, boolean complete) {
		callback.dataCallBack(tag, statusCode, result);
	}
}
