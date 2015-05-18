package com.app.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;

import com.app.base.entity.HomeDataEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
import com.loopj.android.http.RequestParams;

public class HomeDataAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "HomeDataAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public HomeDataAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	//获取文章列表
	public void doHomeDataList(int page) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			Long times = System.currentTimeMillis();
			String v1_archive_url = context.getResources().getString(R.string.v1_archive)+"?client=2";
			RequestParams params = new RequestParams();
			params.put("times", times);
			params.put("page", page);
			
			HttpClientUtils client = new HttpClientUtils();
			client.get(context, mTag, v1_archive_url,
					params, HomeDataAsynTaskService.this);
		} catch (Exception e) {
			
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object result, boolean complete) {
		if(null != this.callback){
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(result)) ;
		}
	}
	
	/**
	 * @param result
	 * @return
	 */
	public ArrayList<HomeDataEntity> parseJsonObject(Object result){
		try {
			ArrayList<HomeDataEntity> entitylist = new ArrayList<HomeDataEntity>();
			JSONObject json_result = new JSONObject(result.toString());
			JSONArray items = json_result.optJSONArray("items");//得到文章列表
			for (int i = 0; i < items.length(); i++) {
				JSONObject article_obj = items.getJSONObject(i);
				HomeDataEntity entityInfo = new HomeDataEntity();
				entityInfo.setAdPic(article_obj.optString("imgURL"));
				entityInfo.setArticle_id(article_obj.optLong("id"));
				entityInfo.setAuthor_id(article_obj.optLong("author_id"));
				entityInfo.setUsername(article_obj.optString("author_nick"));
				entityInfo.setAuthor_nick(article_obj.optString("author_nick"));
				entityInfo.setReleaseTime(article_obj.optString("public_time"));
				entityInfo.setAuthor_img(article_obj.optString("author_img"));
				entityInfo.setComment_count(article_obj.optInt("comment_count"));
				entityInfo.setSupport_count(article_obj.optInt("praise_count"));
				entityInfo.setContent(article_obj.optString("content"));
				entityInfo.setTitle(article_obj.optString("title"));
				entityInfo.setImgURL(article_obj.optString("imgURL"));
				entityInfo.setType(article_obj.optString("type"));
				entityInfo.setDetails("");
				entitylist.add(entityInfo);
				entityInfo = null ;
			}
			
			return entitylist ;
		} catch (Exception e) {
			return null ;
		}
	}
}
