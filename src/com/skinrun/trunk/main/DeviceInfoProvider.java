package com.skinrun.trunk.main;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

public class DeviceInfoProvider {
	//获取手机型号
	public String getDeviceModel(){
		return Build.MODEL;
	}
	//获取APP版本
	public String getAppVersion(Context context){
		PackageManager manager=context.getPackageManager();
		PackageInfo packageInfo=null;
		String appVersion="";
		try {
			packageInfo=manager.getPackageInfo(context.getPackageName(), 0);
			appVersion=packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return appVersion;
	}
	//获取操作系统版本
	public String getOsVersion(){
		return android.os.Build.VERSION.RELEASE;
	}
}

