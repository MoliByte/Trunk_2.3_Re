package com.skinrun.trunk.main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.util.Log;

public class ShowTimeUtil {
	private static String TAG ="ShowTimeUtil";
	
	public static String getYMDShowTime(String time){
		
		try {
			Calendar c=Calendar.getInstance();
			c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time));
			return getShowTime(c.getTimeInMillis());
			
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
	}
	/*
	 * 测试页面时间显示
	 */
	public static String getShowTime(long testTime){
		long currentTime=System.currentTimeMillis();
		Calendar c=Calendar.getInstance();
		if(currentTime-testTime<=5*60*1000){
			return "刚刚";
		}else if(currentTime-testTime<=60*60*1000){
			long m=(currentTime-testTime)/(1000*60);
			return m+"分钟前";
		}else if(currentTime-testTime<=24*60*60*1000){
			long h=(currentTime-testTime)/(1000*60*60);
			return h+"小时前";
		}else if(currentTime-testTime<=6*24*60*60*1000){
			long d=(currentTime-testTime)/(1000*60*60*24);
			return d+"天前";
		}else {
			c.setTimeInMillis(testTime);
			int testYear=c.get(Calendar.YEAR);
			int testMonth=c.get(Calendar.MONTH)+1;
			int testDay=c.get(Calendar.DAY_OF_MONTH);
			
			
			c.setTimeInMillis(System.currentTimeMillis());
			int currentYear=c.get(Calendar.YEAR);
			
			if(testYear!=currentYear){
				return testYear+"年"+testMonth+"月"+testDay+"日";
			}
			
			return testMonth+"月"+testDay+"日";
		}
	}
	
	
	/*
	 * 深度报告页面时间，传过来的值格式为2013-09-09
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getShowTime(String time){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		long currentTime=System.currentTimeMillis();
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(format.parse(time));
			int year=c.get(Calendar.YEAR);
			int month=c.get(Calendar.MONTH)+1;
			int day=c.get(Calendar.DAY_OF_MONTH);
			c.setTimeInMillis(currentTime);
			int cYear=c.get(Calendar.YEAR);
			
			if(year==cYear){
				return month+"月"+day+"日";
			}
			return  year+"年"+month+"月"+day+"日";
			
		} catch (ParseException e) {
			e.printStackTrace();
			return time;
		}
	}
	/*
	 * 深度报告页面时间，传过来的值格式为毫秒数
	 */
	
	public static String getLocalShowTime(long time){
		Calendar c=Calendar.getInstance();
		c.setTimeInMillis(time);
		int year=c.get(Calendar.YEAR);
		int month=c.get(Calendar.MONTH)+1;
		int day=c.get(Calendar.DAY_OF_MONTH);
		
		c.setTimeInMillis(System.currentTimeMillis());
		
		if(c.get(Calendar.YEAR)==year){
			Log.e(TAG, "当前年："+c.get(Calendar.YEAR)+"测试年："+year+"月份"+month);
			return month+"月"+day+"日";
			
		}
		Log.e(TAG, "当前年222："+c.get(Calendar.YEAR)+"测试年："+year);
		return year+"年"+month+"月"+day+"日";
	}
}
/*
*13、时间规则
5分钟以内：刚刚
5分钟及以上开始统计时间：XX分钟以前
60分钟及以上以小时计：XX小时以前
一天以上按天计：X天以前
第6天及以上显示发布月日：06-24，当年不显示年份，往年加上2013-06-24

*/