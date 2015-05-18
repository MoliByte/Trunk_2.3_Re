package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class AvatarUploadTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "AvatarUploadTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public AvatarUploadTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	//用户图像上传
	public void doAvatarUpload(String token,String img) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String v1_avatar = context.getResources().getString(R.string.v1_avatar)+"?client=2";
			HttpClientUtils client = new HttpClientUtils();
			Map<String,String> headermap = new HashMap<String,String>() ;
			Map<String,String> bodymap = new HashMap<String,String>() ;
			bodymap.put("avatar", img);
			headermap.put("Authorization", "Token " + token);
			Log.e(TAG, "v1_avatar------->"+HttpClientUtils.BASE_URL+v1_avatar);
			client.put(context, mTag, v1_avatar,
					headermap, bodymap,AvatarUploadTaskService.this);
		} catch (Exception e) {
			Log.e(TAG, "doAvatarUpload error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object body, boolean complete) {
		if(null != this.callback){
			Log.e(TAG, "statusCode------------"+statusCode);
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(body)) ;
		}
	}
	
	/**
	 * @param result
	 * @return
	 */
	public String parseJsonObject(Object body){
		try {
			Log.e(TAG, "avatar upload body--->"+body.toString());
			return body.toString() ;
		} catch (Exception e) {
			return null ;
		}
	}
}
