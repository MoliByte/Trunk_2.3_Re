package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;

import com.app.base.entity.UpLoadImagePathEntity;
import com.avos.avoscloud.LogUtil.log;
import com.base.app.utils.SavaUpLoadImagePathUtil;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class UpLoadImageService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	private String token;
	
	
	public UpLoadImageService(Context context, Integer mTag,
			HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	
	public void putImage(String token,String image,int productId){
		if(!SystemTool.checkNet(context)){
			AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
			return ;
		}
		this.token=token;
		String finalUrl=context.getResources().getString(R.string.upload_facialmask_image)+"?id="+productId+"&client=2";
		HttpClientUtils client = new HttpClientUtils();
		Map<String,String> headermap = new HashMap<String,String>() ;
		Map<String,String> bodymap = new HashMap<String,String>() ;
		
		headermap.put("Authorization", "Token " + token);
		bodymap.put("avatar", image);
		client.put(context, mTag, finalUrl,
				headermap, bodymap,UpLoadImageService.this);
	}

	@Override
	public void requestComplete(Object tag, int statusCode, Object header,
			Object result, boolean complete) {
		log.e("UpLoadImageService", "==========上传图片返回码："+statusCode);
		callback.dataCallBack(mTag, statusCode, parse(result.toString()));	
	}
	private String parse(String json){
		try {
			JSONObject jsonObject=new JSONObject(json);
			String pro_image=jsonObject.getString("pro_image");
			SavaUpLoadImagePathUtil.sava(new UpLoadImagePathEntity(token, pro_image));
			return pro_image;
			
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
}
