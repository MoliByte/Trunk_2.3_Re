package com.base.app.utils;

import com.app.base.entity.UserEntity;

public class GetUserInfoUtil {
	//获取用户信息
	public static UserEntity getUserEntity(){
		UserEntity user=DBService.getUserEntity();
		return user;
	}
	//判断是否登录
	public static boolean isLogin(){
		UserEntity user=getUserEntity();
		if(user!=null&&user.getToken()!=null&&!user.getToken().equals("")){
			return true;
		}
		return false;
	}
}
