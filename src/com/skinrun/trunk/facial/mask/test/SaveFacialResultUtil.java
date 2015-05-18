package com.skinrun.trunk.facial.mask.test;

import java.util.List;

import com.app.base.entity.FacialTestEntity;
import com.base.app.utils.DBService;

public class SaveFacialResultUtil {
	//保存记录
	public static void save(FacialTestEntity entity){
		List<FacialTestEntity> oldEntitys=DBService.getDB().findAllByWhere(FacialTestEntity.class, "token='"+entity.getToken()+"'");
		if(oldEntitys!=null&&oldEntitys.size()>0){
			for(int i=0;i<oldEntitys.size();i++){
				DBService.getDB().delete(oldEntitys.get(i));
			}
		}
		
		DBService.getDB().save(entity);
	}
	
	//查找记录
	public static FacialTestEntity getRecord(String token){
		List<FacialTestEntity> oldEntitys=DBService.getDB().findAllByWhere(FacialTestEntity.class, "token='"+token+"'");
		if(oldEntitys!=null&&oldEntitys.size()>0){
			return oldEntitys.get(oldEntitys.size()-1);
		}
		return null;
	}
	//删除记录
	public static void delete(String token){
		DBService.getDB().deleteByWhere(FacialTestEntity.class, "token='"+token+"'");
	}
}
