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

import com.app.base.entity.BrandEntity;
import com.app.base.entity.BrandProductEntity;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class GetBrandProductService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public GetBrandProductService(Context context, Integer mTag,
			HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	
	public void doGet(String token,int merchant_id,int brand_id,int pageIndex,int pageSize){
		try{
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String url=context.getString(R.string.get_brand_product)+"?client=2";
			
			Map<String,String> mapHead = new HashMap<String,String>();
			mapHead.put("Authorization", "Token " + token);
			
			JSONObject param = new JSONObject() ;
			param.put("action", "merchantBrand");
			param.put("brand_id", brand_id);
			param.put("merchant_id", merchant_id);
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
		Log.e("GetBrandProductService", "=================="+result.toString());
		
		switch(mTag){
		case HttpTagConstantUtils.GET_MY_LOVED_BRAND:
			callback.dataCallBack(tag, statusCode, parseBrand(result.toString()));
			break;
		case HttpTagConstantUtils.GET_MY_LOVED_PRODUCT:
			callback.dataCallBack(tag, statusCode, parseProduct(result.toString()));
			break;
		}
	}
	
	private ArrayList<BrandEntity> parseBrand(String json){
		try {
			JSONObject josnObject=new JSONObject(json);
			JSONObject items=josnObject.getJSONObject("items");
			JSONArray brands=items.getJSONArray("brands");
			
			ArrayList<BrandEntity> data=new ArrayList<BrandEntity>();
			for(int i=0;i<brands.length();i++){
				JSONObject brand=brands.getJSONObject(i);
				BrandEntity entity=new BrandEntity();
				entity.setBrand_id(brand.getInt("brand_id"));
				entity.setBrand_name(brand.getString("brand_name"));
				entity.setIdx_name(brand.getString("idx_name"));
				entity.setLogo(brand.getString("logo"));
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
	
	private ArrayList<BrandProductEntity> parseProduct(String json){
		JSONObject josnObject;
		try {
			josnObject = new JSONObject(json);
			JSONObject items=josnObject.getJSONObject("items");
			
			JSONArray products=items.getJSONArray("products");
			ArrayList<BrandProductEntity> data=new ArrayList<BrandProductEntity>();
			for(int i=0;i<products.length();i++){
				JSONObject product=products.getJSONObject(i);
				
				BrandProductEntity entity=new BrandProductEntity();
				entity.setProduct_id(product.getString("product_id"));
				entity.setProduct_name(product.getString("product_name"));
				entity.setMarket_price(product.getString("market_price"));
				entity.setSuitable(product.getString("suitable"));
				entity.setImg_url(product.getString("img_url"));
				entity.setImg_width(product.getInt("img_width"));
				entity.setImg_height(product.getInt("img_height"));
				entity.setKeywords(product.getString("keywords"));
				entity.setTest_count(product.getInt("test_count"));
				entity.setPraise_count(product.getInt("praise_count"));
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
