package com.app.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class PostSkinScoreService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;
	private HttpAysnResultInterface callback;
	private static String TAG="PostSkinScoreService";
	public PostSkinScoreService(Context context, Integer mTag,HttpAysnResultInterface callback){
		this.context=context;
		this.mTag=mTag;
		this.callback=callback;
	}
	
	public void doPostSkinScore(String Token,String area){
		try{
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String skin_score_url=context.getResources().getString(R.string.TEST_SKIN)+"?client=2";
			JSONObject param = new JSONObject();
			param.put("module", "skin_refresh");
			param.put("test_date", getTime());
			param.put("area", area);
			StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			Map<String,String> map = new HashMap<String,String>();
			map.put("Authorization", "Token " + Token);
			//请求
			HttpClientUtils client=new HttpClientUtils();
			client.post_with_head_and_body(context, mTag, skin_score_url, map, bodyEntity, PostSkinScoreService.this);
			
		}catch(Exception e){
			
		}
	}
	
	@Override
	public void requestComplete(Object tag, int statusCode, Object header,Object result, boolean complete) {
		callback.dataCallBack(tag, statusCode, result);
		Log.e(TAG, "============上传颜值的返回码："+statusCode);
		new ParserIntegral(context).parse(result.toString());
	}
	@SuppressLint("SimpleDateFormat")
	private String getTime(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Date curDate=new  Date(System.currentTimeMillis());//获取当前时间     
		return sdf.format(curDate);
	}
}
