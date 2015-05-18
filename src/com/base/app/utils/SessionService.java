package com.base.app.utils;

import java.io.Serializable;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.app.base.entity.UserEntity;
import com.app.base.init.MyApplication;

public class SessionService implements Serializable {
	
	public SessionService(){};
	
	public Context context ;
	
	private static SharedPreferences preferences ;
	
	public static UserEntity sessionUser ;
	
	public SessionService(Context context){
		this.context = context ;
		preferences = context.getSharedPreferences(MyApplication.USER_INFO, Context.MODE_PRIVATE);
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
		//
	}
	
}
