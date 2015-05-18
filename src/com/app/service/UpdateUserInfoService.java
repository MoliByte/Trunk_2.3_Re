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

public class UpdateUserInfoService implements HttpAysnTaskInterface{
	
	private final static String TAG = "UpdateUserInfoService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public UpdateUserInfoService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	//用户昵称修改
	public void doUserInfoUpdate(String token,String nickname,String region,String skin_type,String gender/*性别*/,String birthday) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String v1_upate_user_ino = context.getResources().getString(R.string.v1_upate_user_ino)+"?client=2";
			HttpClientUtils client = new HttpClientUtils();
			Map<String,String> headermap = new HashMap<String,String>() ;
			Map<String,String> bodymap = new HashMap<String,String>() ;
			//bodymap.put("nickname", nickname);
			bodymap.put("region", region);
			bodymap.put("skinType", skin_type);
			bodymap.put("gender", gender);
			bodymap.put("birthday", birthday);
			Log.e(TAG, "bodymap = "+bodymap.toString());
			headermap.put("Authorization", "Token " + token);
			Log.e(TAG, "token = "+token);
			Log.e(TAG, "v1_avatar------->"+HttpClientUtils.BASE_URL+v1_upate_user_ino);
			client.put(context, mTag, v1_upate_user_ino,
					headermap, bodymap,UpdateUserInfoService.this);
		} catch (Exception e) {
			Log.e(TAG, "UpdateUserInfoService error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object body, boolean complete) {
		if(null != this.callback){
			Log.e(TAG, "update user info  body --->"+body);
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
