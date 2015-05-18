package com.app.service;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.app.base.entity.DongTaiEntity;
import com.app.base.entity.HomeSelectedEntity;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class PostHomeService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	private String TAG="PostHomeService";
	
	public PostHomeService(Context context, Integer mTag,
			HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	public void doPost(final String token,final String action,final int pagerIndex,final int pagerSize){
		final String url=context.getResources().getString(R.string.home_data)+"?client=2";
		try{
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			
			final Handler handler=new Handler();
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						HttpClient  httpclient = new DefaultHttpClient();
						HttpPost httppost = new HttpPost(url);
						
						httppost.addHeader("Authorization", "Token " + token);
						httppost.addHeader("Content-Type", "application/json");
						
						JSONObject obj = new JSONObject();
						obj.put("action", action);
						obj.put("page", pagerIndex);
						obj.put("pagesize", pagerSize);
						httppost.setEntity(new StringEntity(obj.toString()));
						final HttpResponse response;
						response = httpclient.execute(httppost);
						//检验状态码，如果成功接收数据
						final int code = response.getStatusLine().getStatusCode();
						final String result= EntityUtils.toString(response.getEntity());
						
						
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								if (code == 200 || code == 201) {
									try {
										switch(mTag){
										case HttpTagConstantUtils.POST_SELECTED:
											
											callback.dataCallBack(mTag, 200, parseSelected(result.toString()));
											break;
										case HttpTagConstantUtils.POST_DONGTAI:
											callback.dataCallBack(mTag, 200, parseDongTai(result.toString()));
											break;
										}
									} catch (ParseException e) {
										e.printStackTrace();
									}
									
								}else{
									callback.dataCallBack(mTag, 404,null);
								}
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}).start();
			
		}catch(Exception e){
			
		}
	}
	@Override
	public void requestComplete(Object tag, int statusCode, Object header,
			Object result, boolean complete) {
		if(statusCode==200||statusCode==201){
			Log.e(TAG, "==========result:"+result.toString());
			switch(mTag){
			case HttpTagConstantUtils.POST_SELECTED:
				callback.dataCallBack(tag, statusCode, parseSelected(result.toString()));
				break;
			case HttpTagConstantUtils.POST_DONGTAI:
				callback.dataCallBack(tag, statusCode, parseDongTai(result.toString()));
				break;
			}
		}else{
			callback.dataCallBack(tag, statusCode,result);
		}
	}
	
	private ArrayList<DongTaiEntity> parseDongTai(String json){
		try {
			JSONObject jsonObject=new JSONObject(json);
			JSONArray items=jsonObject.getJSONArray("items");
			
			ArrayList<DongTaiEntity> datas=new ArrayList<DongTaiEntity>();
			
			for(int i=0;i<items.length();i++){
				JSONObject item=items.getJSONObject(i);
				DongTaiEntity entity=new DongTaiEntity();
				
				entity.setUid(item.getString("uid"));
				
				if(item.getString("nickname")==null||item.getString("nickname").equals("")||item.getString("nickname").equals("null")){
					entity.setNickname("格格");
				}else{
					entity.setNickname(item.getString("nickname"));
				}
				
				
				
				entity.setAge(item.getString("age"));
				entity.setUser_avatar(item.getString("user_avatar"));
				entity.setSkin_type_name(item.getString("skin_type_name"));
				entity.setId(item.getString("id"));
				entity.setArea(item.getString("area"));
				entity.setWater(item.getString("water"));
				entity.setOil(item.getString("oil"));
				entity.setElasticity(item.getString("elasticity"));
				entity.setTest_time(item.getString("test_time"));
				entity.setUpload_img(item.getString("upload_img"));
				entity.setComment_count(item.getInt("comment_count"));
				entity.setPraise_count(item.getInt("praise_count"));
				entity.setFavorite_count(item.getInt("favorite_count"));
				entity.setImg_width(item.getInt("img_width"));
				entity.setImg_height(item.getInt("img_height"));
				entity.setCan_praise(item.getInt("can_praise"));
				entity.setCan_favorite(item.getInt("can_favorite"));
				
				datas.add(entity);
			}
			return datas;
			
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	private ArrayList<HomeSelectedEntity> parseSelected(String json){
		try {
			JSONObject jsonObject=new JSONObject(json);
			JSONArray items=jsonObject.getJSONArray("items");
			
			ArrayList<HomeSelectedEntity> datas=new ArrayList<HomeSelectedEntity>();
			
			for(int i=0;i<items.length();i++){
				JSONObject item=items.getJSONObject(i);
				HomeSelectedEntity entity=new HomeSelectedEntity();
				String data_type=item.getString("data_type");
				entity.setData_type(data_type);
				
				if(data_type.equals("facemark")){
					entity.setUid(item.getString("uid"));
					entity.setNickname(item.getString("nickname"));
					entity.setAge(item.getString("age"));
					entity.setUser_avatar(item.getString("user_avatar"));
					entity.setSkin_type_name(item.getString("skin_type_name"));
					entity.setId(item.getString("id"));
					entity.setUpload_img(item.getString("upload_img"));
					entity.setTestbefore1(item.getString("testbefore1"));
					entity.setTestbefore2(item.getString("testbefore2"));
					entity.setTestbefore3(item.getString("testbefore3"));
					entity.setRemark(item.getString("remark"));
					entity.setComment_count(item.getInt("comment_count"));
					entity.setPraise_count(item.getInt("praise_count"));
					entity.setFavorite_count(item.getInt("favorite_count"));
					entity.setCan_favorite(item.getInt("can_favorite"));
					entity.setCan_praise(item.getInt("can_praise"));
					entity.setImg_width(item.getInt("img_width"));
					entity.setImg_height(item.getInt("img_height"));
					entity.setAddtime(item.getString("addtime"));
					datas.add(entity);
					
					
				}else if(data_type.equals("archive")){
					entity.setId(item.getString("id"));
					entity.setAuthor_id(item.getString("author_id"));
					entity.setAuthor_nick(item.getString("author_nick"));
					entity.setType(item.getString("type"));
					entity.setTitle(item.getString("title"));
					//entity.setContent(item.getString("content"));
					entity.setView_count(item.getString("view_count"));
					entity.setPraise_count(item.getInt("praise_count"));
					entity.setComment_count(item.getInt("comment_count"));
					entity.setPublic_time(item.getString("public_time"));
					entity.setCreate_time(item.getString("create_time"));
					entity.setImgurl(item.getString("imgURL"));
					entity.setShare_imgurl(item.getString("share_imgURL"));
					entity.setImg_width(item.getInt("img_width"));
					entity.setImg_height(item.getInt("img_height"));
					if(entity.getType().equals("activity")){
						entity.setEnd_time(item.getString("end_time"));
						entity.setJoincount(item.getInt("joincount"));
					}
					datas.add(entity);
				}
			}
			Log.e("PostHomeService", "==========精选长度："+datas.size());
			return datas;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
