package com.app.service;

import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.PraiseJoinEntity;
import com.app.base.entity.SelectedDetailEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class GetSelectedDetailService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	private String TAG="GetPraiseJoinersService";
	
	public GetSelectedDetailService(Context context, Integer mTag,
			HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}
	
	public void doGet(long id,String action){
		String url=context.getResources().getString(R.string.selected_detail)+"?client=2";
		try{
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			JSONObject param = new JSONObject() ;
			param.put("action", action);
			param.put("id", id);
			
			StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			HttpClientUtils client = new HttpClientUtils();
			
			client.post_with_head_and_body(context, mTag, url, null, bodyEntity, this);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void requestComplete(Object tag, int statusCode, Object header,
			Object result, boolean complete) {
		callback.dataCallBack(tag, statusCode, parse(result.toString()));
	}
	
	private SelectedDetailEntity parse(String json){
		try {
			JSONObject jsonObject=new JSONObject(json);
			JSONObject items=jsonObject.getJSONObject("items");
			
			SelectedDetailEntity entity=new SelectedDetailEntity();
			entity.setUid(items.getString("uid"));
			entity.setNickname(items.getString("nickname"));
			entity.setAge(items.getInt("age"));
			entity.setUser_avatar(items.getString("user_avatar"));
			
			entity.setSkin_type_name(items.getString("skin_type_name"));
			entity.setId(items.getString("id"));
			entity.setAddtime(items.getString("addtime"));
			entity.setUpload_img(items.getString("upload_img"));
			
			entity.setImg_width(items.getInt("img_width"));
			entity.setImg_height(items.getInt("img_height"));
			entity.setTestbefore1(items.getInt("testbefore1"));
			entity.setTestbefore2(items.getInt("testbefore2"));
			
			entity.setTestbefore3(items.getInt("testbefore3"));
			entity.setRemark(items.getString("remark"));
			entity.setPraise_count(items.getInt("praise_count"));
			entity.setComments_count(items.getInt("comments_count"));
			
			JSONArray praise_users=items.getJSONArray("praise_users");
			ArrayList<PraiseJoinEntity> joins=new ArrayList<PraiseJoinEntity>();
			
			
			if(praise_users!=null&&praise_users.length()>0){
				for(int i=0;i<praise_users.length();i++){
					JSONObject item=praise_users.getJSONObject(i);
					PraiseJoinEntity p=new PraiseJoinEntity();
					p.setUid(item.getString("uid"));
					p.setUser_avatar(item.getString("user_avatar"));
					Log.e(TAG, "===========头像信息："+p.getUid()+"  :  "+p.getUser_avatar());
					joins.add(p);
				}
			}
			entity.setPraise_users(joins);
			
			return entity;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
/*{
"code": 200,
"message": "success",
"items": {
  "uid": "20177",
  "nickname": "only娜娜",
  "age": 22,
  "user_avatar": "",
  "skin_type_name": "混合性肤质",
  "id": "39",
  "addtime": "2015-03-04 18:09",
  "upload_img": "http://v2.api.skinrun.me/upload/facemark/20150317/na1.jpg",
  "img_width": 800,
  "img_height": 600,
  "testbefore1": 2020,
  "testbefore2": 1740,
  "testbefore3": 1190,
  "remark": "这款来自台湾的纯植物乳液，有着冰淇淋的质地，推开就被肌肤吃透，保湿补水效果立竿见影，乳液清爽不油腻，还能持续保持肌肤水嫩，一整天第一能水水润润的~~",
  "praise_count": "4",
  "praise_users": [
    {
      "uid": "25340",
      "user_avatar": ""
    },
    {
      "uid": "28369",
      "user_avatar": "http://qzapp.qlogo.cn/qzapp/100381049/7E2A6CADA3A614AE6065A6B6CCF8D251/100"
    },
    {
      "uid": "34464",
      "user_avatar": ""
    },
    {
      "uid": "34582",
      "user_avatar": ""
    }
  ],
  "comments": []
}
}
*/
