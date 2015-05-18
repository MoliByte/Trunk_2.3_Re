package com.app.base.init;

import java.io.File;

import android.os.Environment;

/**
 * 应用的基本初始化，包括缓存目录等
 * @author zhup
 *
 */
public class AppBaseUtil {
	
	public final static String appName = "Trunk" ;
	
	public final static String imgcache = "imgcache" ;
	public final static String data = "data" ;
	public final static String download = "download" ;
	
	/**
	 * 图片的缓存目录
	 * ********************/
	public static String DEFAULT_CACHE_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath()
		     + getImageCachePath();
	
	/**
	 * 数据的缓存目录
	 **********************/
	public static String DEFAULT_CACHE_PRIVATE = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ getDataPath();
	
	/**
	 * APK下载目录
	 **********************/
	public static String APK_DOWNLOAD = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ getDownloadPath();
	
	/**
	 * 判断Sdcard是否存在
	 **********************/
	public static boolean isSdCardExists() {
		if (android.os.Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true ;
		}else{
			return false;
		}
	}
	
	/**
	 * 返回apk下载路径
	 * @return
	 **********************/
	public static String getDownloadPath() {
		return  File.separator + appName + File.separator + download; 
	}

	/**
	 * 返回图片缓存路径
	 * @return
	 **********************/
	public static String getImageCachePath(){
		return  File.separator + appName + File.separator + imgcache; 
	}
	
	/**
	 * 返回数据缓存路径
	 * @return
	 **********************/
	public static String getDataPath(){
		return  File.separator + appName + File.separator + data; 
	}
}
