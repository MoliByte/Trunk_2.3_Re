package com.skinrun.trunk.facial.mask.test;

import java.util.List;

import com.app.base.entity.FacialComparaEntity;
import com.base.app.utils.DBService;

public class SaveComparaDtaUtil {
	public static void save(FacialComparaEntity entity){
		try{
			List<FacialComparaEntity> oldEntitys=DBService.getDB().findAllByWhere(FacialComparaEntity.class, "token='"+entity.getToken()+"'");
			if(oldEntitys!=null&&oldEntitys.size()>0){
				for(int i=0;i<oldEntitys.size();i++){
					DBService.getDB().delete(oldEntitys.get(i));
				}
			}
			
			DBService.getDB().save(entity);
		}catch(Exception e){
			
		}
	}
	
	public static FacialComparaEntity getRecord(String token){
		List<FacialComparaEntity> oldEntitys=DBService.getDB().findAllByWhere(FacialComparaEntity.class, "token='"+token+"'");
		if(oldEntitys!=null&&oldEntitys.size()>0){
			return oldEntitys.get(oldEntitys.size()-1);
		}
		return null;
	}
	//删除记录
	public static void delete(String token){
		DBService.getDB().deleteByWhere(FacialComparaEntity.class, "token='"+token+"'");
	}
}
