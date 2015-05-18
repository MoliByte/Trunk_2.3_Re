package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.UserEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class ModifiedPasswordService implements HttpAysnTaskInterface{
	
	private final static String TAG = "ModifiedPasswordService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public ModifiedPasswordService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	//密码修改
	public void doPasswordChange(String token , String oldPassword,String newPassword) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String v1_password = context.getResources().getString(R.string.v1_password)+"?client=2";
			HttpClientUtils client = new HttpClientUtils();
			Map<String,String> headermap = new HashMap<String,String>() ;
			Map<String,String> bodymap = new HashMap<String,String>() ;
			bodymap.put("old_password", oldPassword);
			bodymap.put("new_password", newPassword);
			headermap.put("Authorization", "Token " + token);
			Log.e(TAG, "v1_password------->"+HttpClientUtils.BASE_URL+v1_password);
			client.put(context, mTag, v1_password,
					headermap, bodymap,ModifiedPasswordService.this);
		} catch (Exception e) {
			Log.e(TAG, "doPasswordChange error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object body, boolean complete) {
		if(null != this.callback){
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(header)) ;
		}
	}
	
	/**
	 * @param result
	 * @return
	 */
	public UserEntity parseJsonObject(Object body){
		try {
			Log.e(TAG, "update user ifno --->"+body);
			return new UserEntity() ;
		} catch (Exception e) {
			return null ;
		}
	}
}
