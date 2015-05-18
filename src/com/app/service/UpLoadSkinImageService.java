package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class UpLoadSkinImageService  implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	private String TAG="UpLoadSkinImageService";
	
	public UpLoadSkinImageService(Context context, Integer mTag,
			HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	
	public void upLoad(String token,int id,String image){
		if(!SystemTool.checkNet(context)){
			AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
			return ;
		}
		try{
			String url=context.getResources().getString(R.string.upload_skin_image)+"?client=2";
			Map<String,String> mapHead = new HashMap<String,String>() ;
			mapHead.put("Authorization", "Token " + token);
			
			JSONObject param = new JSONObject() ;
			param.put("action", "uploadImg");
			param.put("id", id);
			param.put("base64_img", image);
			

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
		Log.e(TAG, "===========上传肌肤图片返回码："+statusCode);
	}

}
