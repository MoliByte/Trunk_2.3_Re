package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.UserEntity;
import com.base.app.utils.StringUtil;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class UpdateNickNameService implements HttpAysnTaskInterface{
	
	private final static String TAG = "UpdateNickNameService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public UpdateNickNameService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	//用户昵称修改
	public void doNickNameUpdate(String token,String niakname) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			
			if(niakname.length()>10){
				AppToast.toastMsgCenter(context, "昵称过长,请重新输入!").show();
				return ;
			}
			
			
			
			String v1_upate_user_ino = context.getResources().getString(R.string.v1_upate_user_ino)+"?client=2";
			HttpClientUtils client = new HttpClientUtils();
			Map<String,String> headermap = new HashMap<String,String>() ;
			Map<String,String> bodymap = new HashMap<String,String>() ;
			bodymap.put("nickname", niakname);
			headermap.put("Authorization", "Token " + token);
			Log.e(TAG, "v1_avatar------->"+HttpClientUtils.BASE_URL+v1_upate_user_ino);
			client.put(context, mTag, v1_upate_user_ino,
					headermap, bodymap,UpdateNickNameService.this);
		} catch (Exception e) {
			Log.e(TAG, "doNickNameUpdate error:" + e.toString());
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
			Log.e(TAG, "update nick name--->"+body);
			return new UserEntity() ;
		} catch (Exception e) {
			return null ;
		}
	}
}
