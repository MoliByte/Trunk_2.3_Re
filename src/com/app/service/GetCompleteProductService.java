package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;

import com.app.base.entity.CompleteProductEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class GetCompleteProductService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public GetCompleteProductService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	
	public void doGet(String token,int productId){
		if(!SystemTool.checkNet(context)){
			AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
			return ;
		}
		String baseUrl="http://skinrun.renzhi.net/api/product?id="+productId+"&client=2";
		Map<String,String> map = new HashMap<String,String>();
		map.put("Authorization", "Token " + token);

		HttpClientUtils client = new HttpClientUtils();
		client.get_with_head(context, mTag, baseUrl,map, GetCompleteProductService.this);
	}
	
	@Override
	public void requestComplete(Object tag, int statusCode, Object header,
			Object result, boolean complete) {
		callback.dataCallBack(tag, statusCode, parse(result.toString()));
	}
	private CompleteProductEntity parse(String json){
		try {
			JSONObject jsonObject=new JSONObject(json);
			CompleteProductEntity entity=new CompleteProductEntity();
			int code=jsonObject.getInt("code");
			if(code==200){
				JSONObject items=jsonObject.getJSONObject("items");
				entity.setId(items.getString("id"));
				entity.setPro_brand_code(items.getString("pro_brand_code"));
				entity.setPro_id(items.getString("pro_id"));
				entity.setPro_brand_name(items.getString("pro_brand_name"));
				entity.setPro_image(items.getString("pro_image"));
				entity.setPro_adapt_sex(items.getString("pro_adapt_sex"));
				entity.setPro_category_one(items.getString("pro_category_one"));
				entity.setPro_category_two(items.getString("pro_category_two"));
				entity.setPro_price(items.getString("pro_price"));
				entity.setPro_capacity(items.getString("pro_capacity"));
				entity.setPro_introduce(items.getString("pro_introduce"));
				entity.setPro_effect_one(items.getString("pro_effect_one"));
				entity.setPro_effect_two(items.getString("pro_effect_two"));
				entity.setPro_effect_three(items.getString("pro_effect_three"));
				entity.setPro_effect_four(items.getString("pro_effect_four"));
				entity.setPro_page_html(items.getString("pro_page_html"));
				entity.setJoinnum(items.getString("joinnum"));
				return entity;
			}
			return null;
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}catch(Exception e){
			return null;
		}
		
	}
}
/*
 *         "id": "1",
       "pro_brand_code": "1028_000008",
       "pro_id": "s_1000001",
       "pro_brand_name": "1028 青春美之露",
       "pro_image": "upload/product/2015-03-06/1.jpg",
       "pro_adapt_sex": "1",
       "pro_category_one": "10",
       "pro_category_two": "10112",
       "pro_price": "0",
       "pro_capacity": "暂无",
       "pro_introduce": "

能调整皮脂腺分泌，疏通毛孔，杀灭细菌，消除粉刺，不留斑痕及色素，使皮肤逐渐光滑细腻，容颜娇美。消除由脂溢性皮炎引起的头皮红肿及粉刺等问题。防止头皮粉刺引起脱发。 
     同时该产品还可用在颜面、胸背部寻常型痤疮（黑头和白头粉刺、炎症性丘疹）、头面部皮脂溢出过多和酒糟鼻

。

   101养发香波具有止痒、去屑、去脂、营养头发等作用。    本品通过国家级严格审批。是赵章光先生依据多年潜心研究，精选当归、首乌等中药植物，经科学方法配制而成。
　　　　用法：湿发后，用本品适量轻揉，再以清水冲洗干净。 　　注意：若不慎入眼，请立即用清水冲洗。-->

",
       "pro_effect_one": "祛粉刺/祛痘",
       "pro_effect_two": "抗菌消炎",
       "pro_effect_three": "其他功效",
       "pro_effect_four": "",
       "pro_page_html": "http://cosme.pclady.com.cn/product/51217.html",
       "joinnum": "3"
 * 
 */
