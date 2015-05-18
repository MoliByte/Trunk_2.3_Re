package com.skinrun.trunk.main;

import java.util.List;

import com.app.base.entity.LastTestDataEntity;
import com.base.app.utils.DBService;

public class LastTestDataProvider {
	//保存记录
	public static void savaData(LastTestDataEntity entity){
		if(getData(entity.getToken())!=null){
			DBService.getDB().update(entity, "token='"+entity.getToken()+ "'");
		}else{
			DBService.getDB().save(entity);
		}
		
	}
	/*
	 * 查找记录
	 */
	public static LastTestDataEntity getData(String token){
		List<LastTestDataEntity> list=DBService.getDB().findAllByWhere(LastTestDataEntity.class, "token='"+token+ "'");
		if(list!=null&&list.size()>0){
			return list.get(list.size()-1);
		}
		return null;
	}
}
