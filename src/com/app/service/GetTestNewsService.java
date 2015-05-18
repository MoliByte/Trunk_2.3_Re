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

import com.app.base.entity.TestNewsEntity;
import com.base.app.utils.HomeTag;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class GetTestNewsService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	private String TAG="GetTestNewsService";
	
	public GetTestNewsService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	
	public void doGet(String token,String test_type,int pageIndex,int pageSize){
		try{
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String url=context.getResources().getString(R.string.mine_message)+"?client=2";
			
			Map<String,String> mapHead = new HashMap<String,String>();
			mapHead.put("Authorization", "Token " + token);
			
			JSONObject param = new JSONObject() ;
			param.put("action", "myTestMsg");
			param.put("test_type", test_type);
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
		callback.dataCallBack(tag, statusCode, parse(result.toString()));
	}
	
	private ArrayList<TestNewsEntity> parse(String json){
		try {
			JSONObject jsonObject=new JSONObject(json);
			JSONArray items=jsonObject.getJSONArray("items");
			
			ArrayList<TestNewsEntity> data=new ArrayList<TestNewsEntity>();
			for(int i=0;i<items.length();i++){
				TestNewsEntity entity=new TestNewsEntity();
				JSONObject item=items.getJSONObject(i);
				
				entity.setTest_type(item.getString("test_type"));
				entity.setFrom_uid(item.getString("from_uid"));
				entity.setFrom_nickname(item.getString("from_nickname"));
				entity.setFrom_avatar(item.getString("from_avatar"));
				
				entity.setTest_id(item.getString("test_id"));
				entity.setCreate_time(item.getString("create_time"));
				entity.setContent(item.getString("content"));
				entity.setMsg_type(item.getString("msg_type"));
				if(entity.getTest_type().equals(HomeTag.FACE_MARK)){
					
					entity.setRemark(item.getString("remark"));
					entity.setUpload_img(item.getString("upload_img"));
					
				}else if(entity.getTest_type().equals(HomeTag.SKIN_TEST)){
					entity.setArea(item.getString("area"));
					entity.setWater(item.getString("water"));
					entity.setOil(item.getString("oil"));
					entity.setElasticity(item.getString("elasticity"));
				}
				data.add(entity);
			}
			Log.e(TAG, "=======测试消息解析完成");
			return data;
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	/*
	 * "test_type": "skintest",
	      "from_uid": "111",
	      "from_nickname": "",
	      "from_avatar": "",
	      "msg_type": "praises",
	      "test_id": "1",
	      "create_time": "26天前",
	       "content": ""
	      
	      
	      "area": "U",
	      "water": "54.29%",
	      "oil": "24.39%",
	      "elasticity": "37.94%",
	     
	 * 
	 * 
	 *
	 "test_type": "facemark",
	      "from_uid": "25340",
	      "from_nickname": "Lenny",
	      "from_avatar": "http://napi.skinrun.cn/uploads/000/025/340/source.jpg",
	      "msg_type": "praises",
	      "test_id": "8",
	      "create_time": "4天前",
	        "content": ""
	      
	      "remark": "",
	      "upload_img": "",
	    
	*/
}
