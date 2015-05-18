package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.UserEntity;
import com.app.base.init.ACache;
import com.base.app.utils.DBService;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
/**
 * 获取用户信息
 * @author zhup
 *
 */
public class GetUserInfoService implements HttpAysnTaskInterface{
	
	private final static String TAG = "GetUserInfoService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public GetUserInfoService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void getUserInfo(String Token) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String user_info_url = context.getResources().getString(R.string.v1_account)+"?client=2";
			HttpClientUtils client = new HttpClientUtils();
			Map<String,String> map = new HashMap<String,String>();
			map.put("Authorization", "Token " + Token);
			Log.e(TAG, "获取用户基本信息 ------->"+HttpClientUtils.BASE_URL+user_info_url);
			client.get_with_head(context, mTag, user_info_url,
					map, GetUserInfoService.this);
		} catch (Exception e) {
			Log.e(TAG, "getUserInfo error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode, Object header ,Object result, boolean complete) {
		if(null != this.callback){
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(result)) ;
		}
	}
	
	/**
	 * @param result
	 * @return
	 */
	public UserEntity parseJsonObject(Object result){
		try {
			Log.e(TAG, "用户基本信息---->"+result);
			UserEntity entity  = new UserEntity();
			JSONObject obj = new JSONObject(result.toString());
			entity.setAvatar(obj.optString("avatar").replaceAll("\\\\", ""));
			entity.setUsername(obj.optString("username"));
			entity.setEmail(obj.optString("email"));
			entity.setU_id(obj.optInt("id"));
			entity.setMember_id(obj.optString("member_id"));
			entity.setMobile(obj.optString("mobile"));
			entity.setNiakname(obj.optString("niakname"));
			entity.setRealname(obj.optString("realname"));
			entity.setGender(obj.optString("gender"));
			entity.setBirthday(obj.optString("birthday"));
			entity.setRegion(obj.optString("region"));
			entity.setIntegral(obj.optInt("integral"));
			entity.setRegionNames(obj.optString("regionNames"));
			entity.setSkinType(obj.optString("skinType"));
			entity.setHasMerchant(obj.optString("hasMerchant"));
			entity.setToken(ACache.get(context).getAsString("Token"));
			
			
			ACache.get(context).put("uid", obj.optInt("id")+"");//放到cache中
			ACache.get(context).put("integral", obj.optInt("integral"));//放到cache中
			ACache.get(context).put("type",""+obj.optString("type"));
			
			
			ACache.get(context).put("nickname", entity.getNiakname());
			ACache.get(context).put("mobile", entity.getMobile());
			ACache.get(context).put("username", entity.getMobile());
			ACache.get(context).put("skin_type", entity.getSkinType());
			ACache.get(context).put("region_name", entity.getRegionNames());
			ACache.get(context).put("region", entity.getRegion());
			ACache.get(context).put("realname", entity.getRealname());
			ACache.get(context).put("birthday", entity.getBirthday());
			ACache.get(context).put("gender", entity.getGender());
			ACache.get(context).put("avatar", entity.getAvatar());
			ACache.get(context).put("integral", entity.getIntegral());
			ACache.get(context).put("uid", obj.optInt("id")+"");//放到cache中
			ACache.get(context).put("uid", obj.optInt("id")+"");//放到cache中
			ACache.get(context).put("integral", obj.optInt("integral"));//放到cache中
			ACache.get(context).put("type",""+obj.optString("type"));
			ACache.get(context).put("avatar", obj.optString("avatar").replaceAll("\\\\", "")+"");
			
			ACache.get(context).put("regionNames",""+obj.optString("regionNames"));
			ACache.get(context).put("province",""+obj.optString("province"));
			ACache.get(context).put("city",""+obj.optString("city"));
			ACache.get(context).put("district",""+obj.optString("district"));
			
			DBService.saveOrUpdateUserEntity(entity);
			return entity ;
		} catch (Exception e) {
			return null ;
		}
	}
	
	/*
{
  "avatar": "http://napi.skinrun.cn/uploads/000/025/340/source.jpg",
  "birthday": "1990-01-01 00:00:00",
  "email": "",
  "gender": 2,
  "hasMerchant": 0,
  "integral": 358,
  "member_id": 0,
  "mobile": "15006181787",
  "niakname": "哈哈哈哈哈哈哈哈哈哈哈哈哈",
  "realname": "",
  "region": "310108",
  "regionNames": "",
  "skinType": 4,
  "username": "15006181787"
}
	 */
}
