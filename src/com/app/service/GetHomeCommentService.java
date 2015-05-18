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

import com.app.base.entity.ParentCommentEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class GetHomeCommentService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	private String TAG="GetHomeCommentService";
	
	public GetHomeCommentService(Context context, Integer mTag,
			HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	public void getComment(String action,String source_type,long source_id,int page,int pagesize){
		try{
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			
			String url=context.getResources().getString(R.string.home_interaction)+"?client=2";
			JSONObject param = new JSONObject() ;
			param.put("action", action);
			param.put("source_type", source_type);
			param.put("source_id", source_id);
			param.put("page", page);
			param.put("pagesize", pagesize);
			
			
			Map<String,String> mapHead = new HashMap<String,String>() ;
			//mapHead.put("Authorization", "Token " + token);
			
			StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			HttpClientUtils client = new HttpClientUtils();
			
			client.post_with_head_and_body(context, mTag, url, mapHead, bodyEntity, this);
			Log.e(TAG, "========请求评论参数："+action+":"+source_type+":"+source_id);
			
		}catch(Exception e){
			
		}
	}

	@Override
	public void requestComplete(Object tag, int statusCode, Object header,
			Object result, boolean complete) {
		
		Log.e(TAG, "=========获取评论结果："+statusCode+"    "+result.toString());
		
			callback.dataCallBack(tag, statusCode, parse(result.toString()));
		
	}
	
	private ArrayList<ParentCommentEntity> parse(String json){
		try {
			JSONObject jsonObject=new JSONObject(json);
			JSONArray items=jsonObject.getJSONArray("items");
			ArrayList<ParentCommentEntity> data=new ArrayList<ParentCommentEntity>();
			
			for(int i=0;i<items.length();i++){
				JSONObject item=items.getJSONObject(i);
				
				ParentCommentEntity parentEntity=new ParentCommentEntity();
				parentEntity.setId(item.getString("id"));
				parentEntity.setSource_type(item.getString("source_type"));
				parentEntity.setSource_id(item.getString("source_id"));
				parentEntity.setParent_id(item.getString("parent_id"));
				parentEntity.setUid(item.getString("uid"));
				parentEntity.setNickname(item.getString("nickname"));
				parentEntity.setContent(item.getString("content"));
				parentEntity.setCreate_time(item.getString("create_time"));
				parentEntity.setPraise_count(item.getInt("praise_count"));
				parentEntity.setBad_count(item.getInt("bad_count"));
				
				/*JSONArray childItems=item.getJSONArray("children_comment");
				ArrayList<ChildCommentEntity> childs=new ArrayList<ChildCommentEntity>();
				
				for(int j=0;j<childItems.length();j++){
					JSONObject childItem=childItems.getJSONObject(i);
					ChildCommentEntity childEntity=new ChildCommentEntity();
					
					childEntity.setId(childItem.getString("id"));
					childEntity.setSource_type(childItem.getString("source_type"));
					childEntity.setSource_id(childItem.getString("source_id"));
					childEntity.setParent_id(childItem.getString("parent_id"));
					childEntity.setUid(childItem.getString("uid"));
					childEntity.setNickname(childItem.getString("nickname"));
					childEntity.setContent(childItem.getString("content"));
					childEntity.setCreate_time(childItem.getString("create_time"));
					childEntity.setPraise_count(childItem.getInt("praise_count"));
					childEntity.setBad_count(childItem.getInt("bad_count"));
					childEntity.setDrop_status(childItem.getString("drop_status"));
					
					childs.add(childEntity);
				}
				parentEntity.setChildren_comment(childs);*/
				
				
				data.add(parentEntity);
				
			}
			Log.e(TAG, "============获取评论的个数："+data.size());
			return data;
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}/*
	{
      "id": "1",
      "source_type": "facemark",
      "source_id": "1",
      "parent_id": 0,
      "uid": "111",
      "nickname": "aslan",
      "content": "测试评论",
      "create_time": "2015-03-15 12:22:56",
      "praise_count": "0",
      "bad_count": "0",
      "children_comment": [
        {
          "id": "2",
          "source_type": "facemark",
          "source_id": "1",
          "parent_id": "1",
          "uid": "222",
          "nickname": "xu",
          "content": "测试回复",
          "create_time": "1426393389",
          "praise_count": "0",
          "bad_count": "0",
          "drop_status": "0"
        }
      ]
    }
	*/
}
