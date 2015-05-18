package com.skinrun.trunk.main;

import java.util.Calendar;
import java.util.List;

import com.app.base.entity.SkinTypeEntity;
import com.app.base.entity.UserEntity;
import com.base.app.utils.DBService;

public class DeepTestDataProvider {
	private UserEntity userEntity;
	public DeepTestDataProvider(UserEntity userEntity){
		this.userEntity=userEntity;
	}
	//得到年龄
	public int getRealAge(){
		int age=25;
		if(userEntity!=null){
			Calendar calendar=Calendar.getInstance();
			int currentYear=calendar.get(Calendar.YEAR);
			if(userEntity.getBirthday()!=null&&!userEntity.getBirthday().equals("")){
				String[] strs=userEntity.getBirthday().split("-");
				int birthYear=Integer.parseInt(strs[0]);
				age=currentYear-birthYear;
			}
		}
		return age;
	}
	//得到肌肤类型
	public String getSkinType(){
		try{
			int skinType=Integer.parseInt(userEntity.getSkinType());
			List<SkinTypeEntity> skins=DBService.getDB().findAllByWhere(SkinTypeEntity.class, "id="+skinType);
			if(skins!=null&&skins.size()>0){
				return skins.get(0).getName();
			}
		}catch(NumberFormatException e){
			
		}
		return "未知";
	}
	//得到肌肤状况
	public String getSkinState(int ageFactor){
		if(ageFactor<=-4){
			return "极致";
		}else if(ageFactor<=-2){
			return "优良";
		}else if(ageFactor<1){
			return "良好";
		}else if(ageFactor<4){
			return "较差";
		}else {
			return "警示";
		}
	}
	//得到肌肤年龄
	public int getSkinAge(int ageFactor){
		return getRealAge()+ageFactor;
	}
}
