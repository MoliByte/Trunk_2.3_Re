package com.skinrun.trunk.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class UseTimeProvider {
	private Context context;
	public UseTimeProvider(Context context){
		this.context=context;
	} 
	public int getUseTime(){
		SharedPreferences sharedPreferences =context.getSharedPreferences("UseTimeFile", Activity.MODE_PRIVATE);
		int useTime=sharedPreferences.getInt("useTime", 1);
		//使用次数增加
		SharedPreferences.Editor editor = sharedPreferences.edit();
		int tem=useTime+1;
		editor.putInt("useTime",tem);
		editor.commit();
		return useTime;
	}
	public int getPopuWindowShowTime(){
		SharedPreferences sharedPreferences =context.getSharedPreferences("ShowTimeFile", Activity.MODE_PRIVATE);
		int showTime=sharedPreferences.getInt("showTime", 0);
		//使用次数增加
		SharedPreferences.Editor editor = sharedPreferences.edit();
		int tem=showTime+1;
		editor.putInt("showTime",tem);
		editor.commit();
		return showTime;
	}
}

