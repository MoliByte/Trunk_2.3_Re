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

import com.app.base.entity.MerchantDetailEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class GetMerchantDetailService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	private String TAG="GetMerchantDetailService";
	
	public GetMerchantDetailService(Context context, Integer mTag,
			HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	public void doGetDetail(String token,int merchantDetail){
		try{
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String url=context.getString(R.string.merchant_detail)+"?client=2";
			
			Map<String,String> mapHead = new HashMap<String,String>();
			mapHead.put("Authorization", "Token " + token);

			JSONObject param = new JSONObject() ;
			param.put("action", "merchantDetail");
			param.put("merchant_id", merchantDetail);
			
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
		Log.e("TAG", "============美容院详情："+result.toString());
		callback.dataCallBack(tag, statusCode, parse(result.toString()));
	}
	
	private MerchantDetailEntity parse(String json){
		try {
			JSONObject jsonObject=new JSONObject(json);
			JSONObject items=jsonObject.getJSONObject("items");
			MerchantDetailEntity entity=new MerchantDetailEntity();
			entity.setId(items.getString("id"));
			entity.setAddress(items.getString("address"));
			entity.setCompany_name(items.getString("company_name"));
			entity.setContact_phone(items.getString("contact_phone"));
			entity.setRemark(items.getString("remark"));
			entity.setMerchant_img(items.getString("merchant_img"));
			entity.setImg_width(items.getInt("img_width"));
			entity.setImg_height(items.getInt("img_height"));
			return entity;
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	/*
	 * "id": "28",
        "company_name": "莱卡塔城店",
        "contact_phone": "13817842161/02159520908",
        "remark": "专业美容",
        "address": "null",
        "merchant_img": "http://v2.api.skinrun.me/uploads/product/image/20150417/20150417094956_23642.png",
        "img_width": 400,
        "img_height": 400
	 */
}
