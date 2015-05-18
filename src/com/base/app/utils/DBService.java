package com.base.app.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.kymjs.aframe.database.KJDB;

import android.util.Log;

import com.app.base.entity.RegionsEntity;
import com.app.base.entity.UserEntity;
import com.app.base.entity.WeatherEntity;
import com.app.base.init.ACache;
import com.app.base.init.MyApplication;
import com.avoscloud.chat.base.ChatMsgEntity;
import com.avoscloud.chat.entity.Msg;
import com.avoscloud.chat.entity.Pm9Message;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.google.gson.Gson;

/**
 * ORM 图片地址映射
 * 
 * @author zhup 缓存查询或更新
 */

public class DBService {
private final static String TAG = "DBService" ;
	public static KJDB getDB() {
		return MyApplication.getDB();
	}

	// 保存基本用户信息
	public static void saveOrUpdateUserEntity(UserEntity info) {
		try {
			List<UserEntity> list = getDB().findAllByWhere(UserEntity.class,
					"mobile='" + info.getMobile() + "'");
			if (list==null || (null != list && list.size() <= 0)) {
				DBService.getDB().save(info);
			} else {
				DBService.getDB().update(info,
						"mobile='" + info.getMobile() + "'");
			}
		} catch (Exception e) {
			
		}
	}
	
	//保存天气信息
	public static void savaWeatherInfo(WeatherEntity weatherEntity){
		List<WeatherEntity>	 list=getDB().findAll(WeatherEntity.class);
		if(list!=null&&list.size()>0){
			DBService.getDB().update(weatherEntity);
		}
		DBService.getDB().save(weatherEntity);
	}
	//读取天气信息
	public static WeatherEntity findWeatherInfo(){
		List<WeatherEntity>	 list=getDB().findAll(WeatherEntity.class);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	// 通过令牌获取用户信息用户信息
	public static UserEntity getEntityByToken(String token) {
		try {
			List<UserEntity> list = getDB().findAllByWhere(UserEntity.class,
					"token='" +token + "'");
			return list.get(0);
		} catch (Exception e) {
			return null ;
		}
	}
	
	// 用户信息
	public static UserEntity getUserEntity() {
		UserEntity entity = null ;
		try {
			List<UserEntity> list = getDB().findAllByWhere(UserEntity.class,
					"uuid='" +HttpTagConstantUtils.UUID + "'");
			entity = list.get(0) ;
			
		} catch (Exception e) {
			
		}
		
		if(entity == null){
			entity  = new UserEntity();
			entity.setAvatar(ACache.get(MyApplication.ctx).getAsString("avatar"));
			entity.setUsername(ACache.get(MyApplication.ctx).getAsString("username"));
			entity.setMobile(ACache.get(MyApplication.ctx).getAsString("username"));
			entity.setNiakname(ACache.get(MyApplication.ctx).getAsString("nickname"));
			entity.setRealname(ACache.get(MyApplication.ctx).getAsString("realname"));
			entity.setGender(ACache.get(MyApplication.ctx).getAsString("gender"));
			entity.setBirthday(ACache.get(MyApplication.ctx).getAsString("birthday"));
			entity.setRegion(ACache.get(MyApplication.ctx).getAsString("region"));
			entity.setSkinType(ACache.get(MyApplication.ctx).getAsString("skin_type"));
			
			String integral = ACache.get(MyApplication.ctx).getAsObject("integral")+"";
			
			entity.setIntegral(Integer.valueOf(integral == null
					|| "null".equals(integral) || "".equals(integral) ? "0"
					: integral));
			entity.setUuid(HttpTagConstantUtils.UUID);
			entity.setU_id(ACache.get(MyApplication.ctx).getAsInteger("uid"));//返回的用户id
			entity.setToken(ACache.get(MyApplication.ctx).getAsString("Token"));
		}
		
		return entity ;
	}
	
	//地区数据
	public static List<UserEntity> getRegionData() {
		try {
			List<UserEntity> list = getDB().findAll(UserEntity.class);
			return list;
		} catch (Exception e) {
			return null ;
		}
	}
	//导入地区数据
	public static boolean importRegionData(JSONArray array) {
		try {
			for (int i = 0; i < array.length(); i++) {
				Gson gson =  new Gson() ;
				RegionsEntity entity = gson.fromJson(array.getJSONObject(i).toString(), RegionsEntity.class);
				 getDB().save(entity);
				 entity = null ;
				 gson = null ;
				 System.gc();
			}
			Log.e(TAG, "地区数据导入成功----");
			return true;
		} catch (Exception e) {
			Log.e(TAG, "地区数据导入失败---->"+e.toString());
			return false ;
		}
	}
	
	public static List<RegionsEntity>  getProvinceList(){
		List<RegionsEntity> list = getDB().findAllByWhere(RegionsEntity.class, "code  like '%0000%'");
		return list ;
	}
	
	public static List<RegionsEntity>  getProvinceByCode(String code){
		if(code == null || "".equals(code)){
			code = "111111" ;
		}
		code = code.substring(0, 2);
		List<RegionsEntity> list = getDB().findAllByWhere(RegionsEntity.class, "code='"+code+"0000'");
		return list ;
	}
	public static List<RegionsEntity>  getCityByCode(String code){
		if(code == null || "".equals(code)){
			code = "111111" ;
		}
		code = code.substring(0, 2);
		List<RegionsEntity> list = getDB().findAllByWhere(RegionsEntity.class, "code='"+code+"00'");
		return list ;
	}
	public static List<RegionsEntity>  getCountyByCode(String code){
		if(code == null || "".equals(code)){
			code = "111111" ;
		}
		List<RegionsEntity> list = getDB().findAllByWhere(RegionsEntity.class, "code='"+code+"'");
		return list ;
	}
	
	//删除用户
	public static void deleteSession(){
		try {
			List<UserEntity> list = getDB().findAll(UserEntity.class);
			for (UserEntity entity : list) {
				getDB().delete(entity);
			}
		} catch (Exception e) {
			
		}
	}
	
	// 保存聊天信息
	public static void saveMessage(Pm9Message msg) {
		try {
			DBService.getDB().save(msg);
		} catch (Exception e) {

		}
	}
	
	// 获取聊天信息
	public static List<ChatMsgEntity> loadAllMsg() {
		List<ChatMsgEntity> msgList = new ArrayList<ChatMsgEntity>();
		try {
			List<Pm9Message> list = getDB().findAll(Pm9Message.class);
			for (Pm9Message pm9Message : list) {
				ChatMsgEntity entity = new ChatMsgEntity() ;
				entity.setComMeg(pm9Message.isComMeg());
				entity.setDate(pm9Message.getDate());
				entity.setMsgType(pm9Message.isComMeg());
				entity.setName(pm9Message.getName());
				entity.setText(pm9Message.getText());
				entity.setLocalpath(pm9Message.getLocalPath());
				entity.setType(pm9Message.getMsg_type() == 0 ? Msg.Type.Text : Msg.Type.Image);
				entity.setUid(pm9Message.getUid());
				msgList.add(entity);
				entity = null ;
			}
			
		} catch (Exception e) {
			
		}
		return msgList ;
	}
	// 删除所有聊天信息
	public static void deleteAllMsg() {
		try {
			//getDB().delete(Pm9Message.class);
			getDB().deleteByWhere(Pm9Message.class, "id > 0");
		} catch (Exception e) {
			
		}
	}
	//获取消息总数
	public static int getMsgCount() {
		int count = 0 ;
		try {
			List<Pm9Message> list = getDB().findAll(Pm9Message.class);
			count = list.size() ;
			count = (count % 10  == 0 ? count / 10 : count / 10 + 1) ;
		} catch (Exception e) {
			
		}
		return count ;
	}
	
	/**
	 * 
		select message_time from chat_message order by message_time desc Limit 10 Offset 0 ;
		select message_time from chat_message order by message_time desc Limit 10 Offset 10 ;
		select message_time from chat_message order by message_time desc Limit 10 Offset 20 ;
		select message_time from chat_message order by message_time desc Limit 10 Offset 30 ;
	 * @param timestamp
	 * @return
	 */
	public static List<ChatMsgEntity> loadMsgByPage(int pageIndex) {
		List<ChatMsgEntity> msgList = new ArrayList<ChatMsgEntity>();
		try {
			List<Pm9Message> list = getDB().findAllByWhere(Pm9Message.class, "message_time order by message_time desc Limit 10 Offset " + (10 * (pageIndex - 1)) ) ;//getDB().findAll(Pm9Message.class);
			for (Pm9Message pm9Message : list) {
				ChatMsgEntity entity = new ChatMsgEntity() ;
				entity.setComMeg(pm9Message.isComMeg());
				entity.setDate(pm9Message.getDate());
				entity.setMsgType(pm9Message.isComMeg());
				entity.setName(pm9Message.getName());
				entity.setText(pm9Message.getText());
				entity.setLocalpath(pm9Message.getLocalPath());
				entity.setType(pm9Message.getMsg_type() == 0 ? Msg.Type.Text : Msg.Type.Image);
				entity.setUid(pm9Message.getUid());
				entity.setAvatar(pm9Message.getAvatar());
				msgList.add(entity);
				entity = null ;
			}
			Collections.sort(msgList);
			
		} catch (Exception e) {
			
		}
		return msgList ;
	}
	

}
