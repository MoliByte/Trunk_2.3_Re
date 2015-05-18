package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;

import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class PblishMoodService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public PblishMoodService(Context context, Integer mTag,
			HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	
	public void publishMoood(String token,int id,String content){
		if(!SystemTool.checkNet(context)){
			AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
			return ;
		}
		String finalUrl=context.getResources().getString(R.string.publish_facialmsak_mood)+"/facemark?id="+id+"&client=2";
		HttpClientUtils client = new HttpClientUtils();
		
		Map<String,String> headermap = new HashMap<String,String>() ;
		Map<String,String> bodymap = new HashMap<String,String>() ;
		
		headermap.put("Authorization", "Token " + token);
		bodymap.put("content", content);
		client.put(context, mTag, finalUrl,
				headermap, bodymap,PblishMoodService.this);
	}

	@Override
	public void requestComplete(Object tag, int statusCode, Object header,
			Object result, boolean complete) {
		callback.dataCallBack(tag, statusCode, result);
	}
}
