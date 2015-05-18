package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.FacialComparaEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class PostFacialTestRecordService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;
	private HttpAysnResultInterface callback;
	public PostFacialTestRecordService(Context context, Integer mTag,
			HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void postRecord(String token,float water,int brandid,int productid,int reply,int sid,String pro_image){
		try{
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String skin_score_url=context.getResources().getString(R.string.post_facialmsak_data)+"?client=2";
			JSONObject param = new JSONObject();
			param.put("water", water);
			param.put("brandid", brandid);
			param.put("productid", productid);
			param.put("reply", reply);
			param.put("sid", sid);
			param.put("pro_image", pro_image);
			StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			
			Map<String,String> map = new HashMap<String,String>();
			map.put("Authorization", "Token " + token);
			//请求
			HttpClientUtils client=new HttpClientUtils();
			client.post_with_head_and_body(context, mTag, skin_score_url, map, bodyEntity, PostFacialTestRecordService.this);
			
		}catch(Exception e){
			
		}
	}

	@Override
	public void requestComplete(Object tag, int statusCode, Object header,
			Object result, boolean complete) {
		callback.dataCallBack(tag, statusCode, parse(result.toString()));
		Log.e("PostFacialTestRecordService", "============状态吗："+statusCode);
		Log.e("PostFacialTestRecordService", "============："+result.toString());
		new ParserIntegral(context).parse(result.toString());
		
	}
	private FacialComparaEntity parse(String json){
		try {
			JSONObject jsonObject=new JSONObject(json);
			JSONObject items=jsonObject.getJSONObject("items");
			FacialComparaEntity facialComparaEntity=new FacialComparaEntity();
			//facialComparaEntity.setJoinnum(items.getInt("joinnum"));
			facialComparaEntity.setId(items.getInt("id"));
			facialComparaEntity.setSid(items.getInt("sid"));
			facialComparaEntity.setTestafter(items.getDouble("testafter"));
			facialComparaEntity.setTestbefore1(items.getDouble("testbefore1"));
			facialComparaEntity.setTestbefore2(items.getDouble("testbefore2"));
			facialComparaEntity.setTestbefore3(items.getDouble("testbefore3"));
			return facialComparaEntity;
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
//{"id":"27","joinnum":"2","testafter":"36.97","testbefore1":"41.38","testbefore2":"0.00","testbefore3":"0.00"}}









