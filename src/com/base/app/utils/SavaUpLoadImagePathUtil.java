package com.base.app.utils;

import java.util.List;

import com.app.base.entity.UpLoadImagePathEntity;

public class SavaUpLoadImagePathUtil {
	
	public static void sava(UpLoadImagePathEntity u){
		List<UpLoadImagePathEntity> us=DBService.getDB().findAllByWhere(UpLoadImagePathEntity.class, "token='"+u.getToken()+"'");
		if(us!=null&&us.size()>0){
			DBService.getDB().deleteByWhere(UpLoadImagePathEntity.class, "token='"+u.getToken()+"'");
		}
		
		DBService.getDB().save(u);
	}
	public static UpLoadImagePathEntity get(String token){
		List<UpLoadImagePathEntity> us=DBService.getDB().findAllByWhere(UpLoadImagePathEntity.class, "token='"+token+"'");
		if(us!=null&&us.size()>0){
			return us.get(0);
		}
		return null;
	}
	
}
