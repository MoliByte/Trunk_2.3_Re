
package com.base.app.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author fjl
 * 
 */
public class DateUtil {
    public static final String DF_SHORT_DATE = "yyyy-MM-dd";
    public static final String DF_LONG_DATE  = "yyyy-MM-dd HH:mm:ss";
    
    public static final String SHORT_DATE_TIME = "MM-dd HH:mm";

    /**
     * 格式化短日期
     * 
     * @param date
     * @return
     */
    public static String formatShortDate(Date date) {
        if (date != null) {
            return new SimpleDateFormat(DF_SHORT_DATE,Locale.US).format(date);
        } else {
            return "";
        }
    }

    /**
     * 格式化长日期
     * 
     * @param date
     * @return
     */
    public static String formatLongDate(Date date) {
        if (date != null) {
            return new SimpleDateFormat(DF_LONG_DATE,Locale.US).format(date);
        } else {
            return "";
        }
    }

    /**
     * 解析短日期
     * 
     * @return
     * @throws java.text.ParseException
     */
    public static Date parseShortDate(String date) throws ParseException {
        if (date == null || date.trim().length() < 1) {
            return null;
        }
        return new SimpleDateFormat(DF_SHORT_DATE,Locale.US).parse(date);
    }

    /**
     * 解析日期
     * 
     * @param date
     *            日期字符串
     * @param format
     * @return
     * @throws java.text.ParseException
     */
    public static Date parseDate(String date, String format) throws ParseException {
        if (date == null || date.trim().length() < 1) {
            return null;
        }
        return new SimpleDateFormat(format,Locale.US).parse(date);
    }

    /**
     * 解析长日期
     * 
     * @return
     * @throws java.text.ParseException
     */
    public static Date parseLongDate(String date) throws ParseException {
        if (date == null || date.trim().length() < 1) {
            return null;
        }
        return new SimpleDateFormat(DF_LONG_DATE,Locale.US).parse(date);
    }

    /**
     * 得到当月第一天
     * 
     * @return
     */
    public static Date getFirstDateOfThisMonth() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DATE, 1);

        return cal.getTime();
    }

    /**
     * 得到当月的下一个月的第一天
     * 
     * @return
     */
    public static Date getFirstDateOfNextMonth() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month + 1);
        cal.set(Calendar.DATE, 1);

        return cal.getTime();
    }

    /**
     * 在当前日期基础上加/减 num 天
     * 
     * @param date
     * @return
     */
    public static Date datePlusOneDay(Date date, int num) {
        Calendar cld = Calendar.getInstance();
        cld.setTime(date);
        cld.add(Calendar.DAY_OF_MONTH, num);
        return cld.getTime();
    }

    /**
     * 在当前日期基础上加一天
     * 
     * @param date
     * @return
     * @throws java.text.ParseException
     */
    public static Date datePlusOneDayByShort(String date) throws ParseException {
        Date temp = parseShortDate(date);
        return datePlusOneDay(temp, 1);
    }

    /**
     * 在当前日期基础上加一天
     * 
     * @param date
     * @return
     * @throws java.text.ParseException
     */
    public static Date datePlusOneDayByLong(String date) throws ParseException {
        Date temp = parseLongDate(date);
        return datePlusOneDay(temp, 1);
    }

    /**
     * 在当前日期基础上减一天
     * 
     * @param date
     * @return
     * @throws java.text.ParseException
     */
    public static Date dateMinusOneDayByShort(String date) throws ParseException {
        Date temp = parseShortDate(date);
        return datePlusOneDay(temp, -1);
    }

    /**
     * 在当前日期基础上减一天
     * 
     * @param date
     * @return
     * @throws java.text.ParseException
     */
    public static Date dateMinusOneDayByLong(String date) throws ParseException {
        Date temp = parseLongDate(date);
        return datePlusOneDay(temp, -1);
    }

    /**
     * 在当前日期基础上加减 minute 分钟
     * @param date
     * @param minute
     * @return
     */
    public static Date datePlusOneMinute(Date date, int minute) {
        Calendar cld = Calendar.getInstance();
        cld.setTime(date);
        cld.add(Calendar.MINUTE, minute);
        return cld.getTime();
    }

    /**
     * 日期月份增减
     * @param date
     * @param count
     * @return
     */
    public static Date datePlusMonth(Date date, int count) {
        Calendar cld = Calendar.getInstance();
        cld.setTime(date);
        cld.add(Calendar.MONTH, count);
        return cld.getTime();
    }

    /**
     * 把时间转换成1970到现在的毫秒数
     * @param date
     * @return
     */
    public static String formatDateToMillisecond(Date date) {
        return date == null ? "" : String.valueOf(date.getTime());
    }

    public static int getAge(String birthday){
        try {
            if(birthday == null || birthday.trim().length() ==0){
                return 0;
            }

            Date birth = parseShortDate(birthday);
            Calendar tmp = Calendar.getInstance();
            tmp.setTime(birth);
            int byear = tmp.get(Calendar.YEAR);
            int bday = tmp.get(Calendar.DAY_OF_YEAR);

            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int day = cal.get(Calendar.DAY_OF_YEAR);

            if(day >= bday){
            	if(year - byear > 0){
            		return year - byear ;
            	}else{
            		return 0 ;
            	}
               
            }else{
            	if((year - byear - 1) > 0){
            		return year - byear ;
            	}else{
            		return 0 ;
            	}
               //return year - byear -1 ;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Date getDate(int weeks,int weekTarget) {
        Calendar cal =Calendar.getInstance();
        //获取指定星期的日期
        cal.set(Calendar.DAY_OF_WEEK, weekTarget);
        cal.add(cal.DATE,7*weeks);
        return cal.getTime();
    }

    /**
     * 获取指定格式的日期
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(Date date,String format) {
        if (date != null) {
            return new SimpleDateFormat(format,Locale.US).format(date);
        } else {
            return "";
        }
    }
}
