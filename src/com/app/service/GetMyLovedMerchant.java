package com.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.MyLovedMerchantEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class GetMyLovedMerchant  implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;

	public GetMyLovedMerchant(Context context, Integer mTag,
			HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	
	public void doGet(String token,String type,int pageIndex,int pageSize){
		try{
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String url=context.getResources().getString(R.string.my_follow_merchant)+"?client=2";
			
			Map<String,String> mapHead = new HashMap<String,String>();
			mapHead.put("Authorization", "Token " + token);
			
			JSONObject param = new JSONObject() ;
			param.put("action", "userLiked");
			param.put("type", type);
			param.put("page", pageIndex);
			param.put("pagesize", pageSize);
			
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
		Log.e("GetMyLovedMerchant", "===========商家列表："+result.toString());
		callback.dataCallBack(tag, statusCode, parse(result.toString()));
	}
	
	private ArrayList<MyLovedMerchantEntity> parse(String json){
		try {
			JSONObject jsonObject=new JSONObject(json);
			JSONArray items=jsonObject.getJSONArray("items");
			
			ArrayList<MyLovedMerchantEntity> data=new ArrayList<MyLovedMerchantEntity>();
			
			for(int i=0;i<items.length();i++){
				JSONObject item=items.getJSONObject(i);
				
				MyLovedMerchantEntity entity=new MyLovedMerchantEntity();
				entity.setId(item.getString("id"));
				entity.setName(item.getString("name"));
				entity.setLogo(item.getString("logo"));
				entity.setLogo_height(item.getInt("logo_height"));
				entity.setLogo_width(item.getInt("logo_width"));
				data.add(entity);
			}
			return data;
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
}
