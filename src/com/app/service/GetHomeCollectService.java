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

import com.app.base.entity.CollectEntity;
import com.app.base.entity.DongTaiEntity;
import com.app.base.entity.HomeInterface;
import com.app.base.entity.HomeSelectedEntity;
import com.app.base.init.ACache;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class GetHomeCollectService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	private static final String SKIN_TEST="skintest";
	private static final String FACE_MARK="facemark";
	
	public GetHomeCollectService(Context context, Integer mTag,
			HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	
	public void getCollect(String token,String action,String test_type,int page,int pagesize){
		try{
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			
			token = ACache.get(context).getAsString("Token");
			
			String url=context.getResources().getString(R.string.home_interaction)+"?client=2";
			JSONObject param = new JSONObject() ;
			param.put("action", action);
			param.put("page", page);
			param.put("pagesize", pagesize);
			if(mTag==HttpTagConstantUtils.GET_MY_SKIN_TEST||mTag==HttpTagConstantUtils.GET_MY_MASK_TEST){
				param.put("test_type", test_type);
			}
			Log.e("GetHomeCollectService", "=========Token:"+token+"   "+param.toString());
			
			Map<String,String> mapHead = new HashMap<String,String>();
			mapHead.put("Authorization", "Token " + token);
			
			StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			HttpClientUtils client = new HttpClientUtils();
			
			client.post_with_head_and_body(context, mTag, url, mapHead, bodyEntity, this);
			
			
		}catch(Exception e){
			
		}
	}

	@Override
	public void requestComplete(Object tag, int statusCode, Object header,
			Object result, boolean complete) {
		Log.e("GetHomeCollectService", "========返回码："+statusCode+"  "+result.toString());
		
		callback.dataCallBack(tag, statusCode, parse(result.toString()));
		
	}
	
	private ArrayList<CollectEntity> parse(String json){
		try {
			JSONObject jsonObject=new JSONObject(json);
			JSONArray items=jsonObject.getJSONArray("items");
			
			ArrayList<CollectEntity> data=new ArrayList<CollectEntity>();
			
			for(int i=0;i<items.length();i++){
				JSONObject item=items.getJSONObject(i);
				CollectEntity entity=new CollectEntity();
				entity.setFavorite_id(item.getString("favorite_id"));
				entity.setUid(item.getString("uid"));
				entity.setCreate_time(item.getString("create_time"));
				entity.setData_type(item.getString("data_type"));
				
				JSONObject favItem=item.getJSONObject("favorite_info");
				
				try{
					HomeInterface favorite_info=null;
					
					if(entity.getData_type().equals(SKIN_TEST)){
						DongTaiEntity d=new DongTaiEntity();
						d.setUid(favItem.getString("uid"));
						d.setNickname(favItem.getString("nickname"));
						d.setAge(favItem.getString("age"));
						d.setUser_avatar(favItem.getString("user_avatar"));
						
						d.setSkin_type_name(favItem.getString("skin_type_name"));
						d.setId(favItem.getString("id"));
						d.setArea(favItem.getString("area"));
						d.setWater(favItem.getString("water"));
						
						
						d.setOil(favItem.getString("oil"));
						d.setElasticity(favItem.getString("elasticity"));
						d.setTest_time(favItem.getString("test_time"));
						d.setUpload_img(favItem.getString("upload_img"));
						d.setImg_height(favItem.getInt("img_height"));
						d.setImg_width(favItem.getInt("img_width"));
						d.setComment_count(favItem.getInt("comment_count"));
						d.setPraise_count(favItem.getInt("praise_count"));
						
						favorite_info=d;
						
					}else if(entity.getData_type().equals(FACE_MARK)){
						HomeSelectedEntity h=new HomeSelectedEntity();
						h.setUid(favItem.getString("uid"));
						h.setNickname(favItem.getString("nickname"));
						h.setAge(favItem.getString("age"));
						h.setUser_avatar(favItem.getString("user_avatar"));
						h.setSkin_type_name(favItem.getString("skin_type_name"));
						
						h.setId(favItem.getString("id"));
						h.setUpload_img(favItem.getString("upload_img"));
						h.setImg_height(favItem.getInt("img_height"));
						h.setImg_width(favItem.getInt("img_width"));
						h.setTestbefore1(favItem.getString("testbefore1"));
						h.setTestbefore2(favItem.getString("testbefore2"));
						h.setTestbefore3(favItem.getString("testbefore3"));
						
						h.setRemark(favItem.getString("remark"));
						h.setComment_count(favItem.getInt("comment_count"));
						h.setPraise_count(favItem.getInt("praise_count"));
						
						favorite_info=h;
					}
					
					entity.setFavorite_info(favorite_info);
					data.add(entity);
					
				}catch(Exception e){
					e.printStackTrace();
				}
				
				Log.e("GetHomeCollectService", "========收藏长度1111："+data.size());
				
				
				
			}
			Log.e("GetHomeCollectService", "========收藏长度2222："+data.size());
			return data;
			
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("GetHomeCollectService", "========解析异常");
			return null;
		}catch(Exception e){
			e.printStackTrace();
			Log.e("GetHomeCollectService", "========解析异常：");
			return null;
		}
	}
}/*
items": [
{
  "favorite_id": "7",
  "uid": "111",
  "create_time": "2015-03-16 19:40:13",
  "data_type": "facemark",
  "favorite_info": {
    "uid": "14310",
    "nickname": "\"尹晶\"",
    "age": 25,
    "user_avatar": "",
    "skin_type_name": "中性肤质",
    "id": "1",
    "upload_img": "",
    "testbefore1": "0.00",
    "testbefore2": "40.56",
    "testbefore3": "0.00",
    "remark": "刘胜发表的心情啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊",
    "comment_count": 0,
    "praise_count": 0
  }
},
{
  "favorite_id": "5",
  "uid": "111",
  "create_time": "2015-03-15 17:59:52",
  "data_type": "skintest",
  "favorite_info": {
    "uid": "24722",
    "nickname": "康乐",
    "age": 30,
    "user_avatar": "",
    "skin_type_name": "中性肤质",
    "id": "10",
    "area": "U",
    "water": "32.63%",
    "oil": "14.63%",
    "elasticity": "23.06%",
    "test_time": "213天前",
    "upload_img": "",
    "comment_count": 0,
    "praise_count": 0
  } */