package com.skinrun.trunk.main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

public class WeekUtil {
	@SuppressLint("SimpleDateFormat")
	public static String getWeek(String time){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		
		 try {
			c.setTime(format.parse(time));
			int year=c.get(Calendar.YEAR);
			int week=c.get(Calendar.WEEK_OF_YEAR);
			return year+" "+week;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	/*
	 * 判断当前时间是不是今天
	 */
	public static boolean isTodayTest(long time){
		Calendar c=Calendar.getInstance();
		c.setTimeInMillis(time);
		int tyear=c.get(Calendar.YEAR);
		int tday=c.get(Calendar.DAY_OF_YEAR);
		c.setTimeInMillis(System.currentTimeMillis());
		int cyear=c.get(Calendar.YEAR);
		int cday=c.get(Calendar.DAY_OF_YEAR);
		
		if(tyear==cyear&&tday==cday){
			return true;
		}
		return false;
	}
	public static boolean isOverLastDate(long curTime){
		if(curTime>=System.currentTimeMillis()){
			return true;
		}
		return false;
	}
	
	
	/*
	 * 判断当前时间是不是今天
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean isTodayTest(String day){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date=new Date(System.currentTimeMillis());
		String ymd=format.format(date);
		if(day.equals(ymd)){
			return true;
		}
		return false;
	}
	/*
	 * 通过日期yyyy-MM-dd得到毫秒
	 */
	@SuppressLint("SimpleDateFormat")
	public static long getTimemili(String time){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		
		 try {
			c.setTime(format.parse(time));
			
			return c.getTimeInMillis();
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
}
