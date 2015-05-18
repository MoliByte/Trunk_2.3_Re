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

import com.app.base.entity.MineMessageEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class GetMyMessageService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	private String TAG="GetMyMessageService";
	
	public GetMyMessageService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	
	public void doGet(String token,int type,int pageIndex,int pageSize){
		try{
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			
			String url=context.getResources().getString(R.string.mine_message)+"?client=2";
			
			Map<String,String> mapHead = new HashMap<String,String>();
			mapHead.put("Authorization", "Token " + token);
			
			JSONObject param = new JSONObject() ;
			param.put("action", "myMessages");
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
		callback.dataCallBack(tag, statusCode, parse(result.toString()));
	}
	
	private ArrayList<MineMessageEntity> parse(String json){
		try {
			JSONObject jsonObject=new JSONObject(json);
			JSONArray items=jsonObject.getJSONArray("items");
			ArrayList<MineMessageEntity> data=new ArrayList<MineMessageEntity>();
			for(int i=0;i<items.length();i++){
				JSONObject item=items.getJSONObject(i);
				MineMessageEntity entity=new MineMessageEntity();
				entity.setFrom_uid(item.getString("from_uid"));
				entity.setFrom_nickname(item.getString("from_nickname"));
				entity.setFrom_age(item.getString("from_age"));
				entity.setFrom_user_avatar(item.getString("from_user_avatar"));
				entity.setFrom_skin_type_name(item.getString("from_skin_type_name"));
				entity.setMsg_id(item.getString("msg_id"));
				entity.setMsg_time(item.getString("msg_time"));
				entity.setMsg_type(item.getString("msg_type"));
				entity.setMsg_type_cn(item.getString("msg_type_cn"));
				entity.setMsg_title(item.getString("msg_title"));
				entity.setUrl_type(item.getString("url_type"));
				entity.setMsg_content(item.getString("msg_content"));
				entity.setActivity_id(item.getString("activity_id"));
				entity.setActivity_img(item.getString("activity_img"));
				entity.setActivity_title(item.getString("activity_title"));
				entity.setActivity_external_url(item.getString("activity_external_url"));
				
				data.add(entity);
			}
			
			Log.e(TAG, "============我的消息解析完成,长度："+data.size());
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
/*
"from_uid": "25329",
      "from_nickname": "凯文",
      "from_age": 16,
      "from_user_avatar": "1423109386089.jpg",
      "from_skin_type_name": "中性肤质",
      
      
      "msg_id": "279551",
      "msg_time": "57天前",
      "msg_type": "1",
      "msg_type_cn": "系统消息",
      "msg_title": "1111111111111",
      
      
      "msg_content": "笑死！CNN大篇幅报道中国粉丝为欧美艺人起外号[哈哈]:\"听上去很神秘，但其实你早就认识她。",
      "activity_id": 110,
      "activity_img": "1423713385719.jpg",
      "activity_title": "疯狂盖楼赢大奖！",
      "activity_external_url": ""

*/
