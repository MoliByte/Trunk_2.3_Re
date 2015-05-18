package com.app.service;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;
import android.content.Context;
import android.util.Log;

import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class JudgeUserInfoService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public JudgeUserInfoService(Context context, Integer mTag,
			HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	
	public void doGet(String token){
		try{
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			
			String url=context.getResources().getString(R.string.judge_userinfo);
			Map<String,String> mapHead = new HashMap<String,String>() ;
			mapHead.put("Authorization", "Token " + token);
			
			JSONObject param = new JSONObject() ;
			param.put("action", "checkDataCompleted");
			
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
		callback.dataCallBack(tag, statusCode, parse(result.toString()));
		Log.e("JudgeUserInfoService", "============用户是否完善资料返回："+result.toString());
	}
	
	private String parse(String json){
		try {
			JSONObject jsonObject=new JSONObject(json);
			String message=jsonObject.getString("message");
			return message;
		} catch (JSONException e) {
			e.printStackTrace();
			return "1";
		}catch(Exception e){
			e.printStackTrace();
			return "1";
		}
	}
}
