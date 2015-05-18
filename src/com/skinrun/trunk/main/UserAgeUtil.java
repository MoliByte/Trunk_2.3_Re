package com.skinrun.trunk.main;

import com.app.base.entity.UserEntity;
import com.base.app.utils.DateUtil;

public class UserAgeUtil {
	
	public static int getUserAge(UserEntity user){
		if(user==null){
			return 0;
		}
		
		if(user.getBirthday().equals("0000-00-00 00:00:00")||user.getBirthday().equals("")){
			return 18;
		}else{
			
			return  DateUtil.getAge(user.getBirthday());
		}
	}
}
