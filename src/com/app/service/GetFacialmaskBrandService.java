package com.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;

import com.app.base.entity.ProductEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class GetFacialmaskBrandService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public GetFacialmaskBrandService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	
	public void getProductName(String token,String content){
		if(!SystemTool.checkNet(context)){
			AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
			return ;
		}
		String baseUrl=context.getResources().getString(R.string.get_facialmask_brand)+"/product?type=product&name="+content+"&client=2";
		Map<String,String> map = new HashMap<String,String>();
		map.put("Authorization", "Token " + token);

		HttpClientUtils client = new HttpClientUtils();
		client.get_with_head(context, mTag, baseUrl,map, GetFacialmaskBrandService.this);
		
	}

	@Override
	public void requestComplete(Object tag, int statusCode, Object header,
			Object result, boolean complete) {
		if(callback!=null){
			callback.dataCallBack(tag, statusCode, parse(result.toString()));
		}
		
	}
	
	private ArrayList<ProductEntity> parse(String json){
		try {
			JSONObject jsonObject=new JSONObject(json);
			int code=jsonObject.getInt("code");
			if(code==200){
				JSONArray items=jsonObject.getJSONArray("items");
				ArrayList<ProductEntity> products=new ArrayList<ProductEntity>();
				for(int i=0;i<items.length();i++){
					JSONObject item=items.getJSONObject(i);
					ProductEntity p=new ProductEntity();
					p.setProductId(item.getInt("id"));
					p.setPro_brand_name(item.getString("pro_brand_name"));
					p.setPro_image(item.getString("pro_image"));
					products.add(p);
				}
				return products;
			}
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}
