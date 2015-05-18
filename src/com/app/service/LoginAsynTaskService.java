package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.app.base.entity.UserEntity;
import com.app.base.init.ACache;
import com.app.base.init.MyApplication;
import com.avos.avoscloud.AVUser;
import com.avoscloud.chat.service.receiver.MsgReceiver;
import com.base.app.utils.DBService;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class LoginAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "LoginAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public LoginAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void doLogin(final String tel_num,final String pwd) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			try {
				MsgReceiver.setSessionPaused(true);
				MsgReceiver.closeSession(AVUser.getCurrentUser());
			} catch (Exception e) {
				
			}
			final String user_login_url = context.getResources().getString(R.string.v1_session)+"?client=2";
			HttpClientUtils client = new HttpClientUtils();
			final Map<String,String> paraMap = new HashMap<String,String>() ;
			paraMap.put("Authorization", "Base " + HttpClientUtils.getBase64(tel_num+":"+pwd));
			Log.e(TAG, "login_url------->"+HttpClientUtils.BASE_URL+user_login_url);
			final StringBuffer final_url = new StringBuffer(HttpClientUtils.BASE_URL);
			final_url.append(user_login_url);
			
			client.post_with_head(context, mTag, user_login_url,paraMap, LoginAsynTaskService.this);
		} catch (Exception e) {
			Log.e(TAG, "doLogin error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object body, boolean complete) {
		if(null != this.callback){
			if(statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED){
				MyApplication.isVisitor = false ;
			}else{
				MyApplication.isVisitor = true ;
			}
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(header,body)) ;
		}
	}
	
	/** KJ
	 * @param result
	 * @return
	 */
	public UserEntity parseJsonObject(Object header,Object result){
		try {
			UserEntity entity  = new UserEntity();
			JSONObject obj = new JSONObject(result.toString());
			Log.e(TAG, "登陆返回结果--->"+result.toString());
			/*String userType =  obj.optString("type");
			if(!SHARE_MEDIA.SINA.equals(userType)
					||!SHARE_MEDIA.WEIXIN.equals(userType)
					||!SHARE_MEDIA.QQ.equals(userType)){
				entity.setUsername(obj.optString("username").replaceAll("\\\\", ""));
			}*/
			entity.setAvatar(obj.optString("avatar").replaceAll("\\\\", ""));
			entity.setUsername(obj.optString("username").replaceAll("\\\\", ""));
			entity.setEmail(obj.optString("email"));
			
			entity.setMember_id(obj.optString("member_id"));
			entity.setMobile(obj.optString("mobile"));
			entity.setNiakname(obj.optString("niakname"));
			entity.setRealname(obj.optString("realname"));
			entity.setGender(obj.optString("gender"));
			entity.setBirthday(obj.optString("birthday"));
			entity.setRegion(obj.optString("region"));
			entity.setRegionNames(obj.optString("regionNames"));
			entity.setSkinType(obj.optString("skinType"));
			entity.setHasMerchant(obj.optString("hasMerchant"));
			entity.setIntegral(obj.optInt("integral"));
			entity.setUuid(HttpTagConstantUtils.UUID);
			entity.setU_id(obj.optInt("id"));//返回的用户id
			
			ACache.get(context).put("nickname", (entity.getNiakname()==null || "".equals(entity.getNiakname())?"格格":entity.getNiakname()));
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
			
			Header[] headers = (Header[]) header ;
			for (int i = 0; i < headers.length; i++) {
				Header head = headers[i]; 
				if("X-Token".equals(head.getName())){
					entity.setToken(head.getValue());
					ACache.get(context).put("Token",head.getValue());
					break ;
				}
			}
			Log.e(TAG, entity.getToken());
			
			DBService.saveOrUpdateUserEntity(entity);
			MyApplication.getInstance().informedUser();
			JSONObject session = new JSONObject(result.toString()) ;
			session.put("Token", entity.getToken());
			saveSession(session.toString());
			return entity ;
		} catch (Exception e) {
			return null ;
		} finally {
			
		}
	}
	
	public void saveSession(String user_info){
		//实例化SharedPreferences对象（第一步）
		SharedPreferences mySharedPreferences= context.getSharedPreferences(MyApplication.USER_INFO,
		Activity.MODE_PRIVATE);
		//实例化SharedPreferences.Editor对象（第二步）
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		//用putString的方法保存数据
		editor.putString("user_info", user_info);
		//提交当前数据
		editor.commit(); 
	}
}
