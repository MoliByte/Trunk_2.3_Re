package com.base.app.utils;

import java.util.List;

import android.util.Log;

import com.app.base.entity.PushEntity;

public class PushNumUtil {
	public static void save(PushEntity entity){
		List<PushEntity> entitys=DBService.getDB().findAllByWhere(PushEntity.class,  "token='"+entity.getToken()+"'");
		if(entitys!=null&&entitys.size()>0){
			DBService.getDB().deleteByWhere(PushEntity.class, "token='"+entity.getToken()+"'");
		}
		Log.e("PushNumUtil", "===========保存推送：");
		DBService.getDB().save(entity);
	}
	
	public static void delete(String token){
		List<PushEntity> entitys=DBService.getDB().findAllByWhere(PushEntity.class,  "token='"+token+"'");
		if(entitys!=null&&entitys.size()>0){
			DBService.getDB().deleteByWhere(PushEntity.class, "token='"+token+"'");
		}
	}
	
	public static PushEntity get(String token){
		List<PushEntity> entitys=DBService.getDB().findAllByWhere(PushEntity.class,  "token='"+token+"'");
		Log.e("PushNumUtil", "===========取出推送11：");
		if(entitys!=null&&entitys.size()>0){
			Log.e("PushNumUtil", "===========取出推送22：");
			return entitys.get(entitys.size()-1);
		}
		return null;
	}
}
