package com.base.app.utils;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lidewen on 14-5-13.
 */
public class StringUtil {
    /**
     * 验证 str 是否一个合法手机格式
     *
     * @param str 以 1 开头 11 位数字
     * @return
     */
    public static boolean isMobile(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        //1\\d{10}
        String check = "^1\\d{10}$";
        //String check = "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|2|3|5|6|7|8|9])\\d{8}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(str.trim());
        return matcher.matches();
    }

    /**
     * 1 --> A, 2 --> B ... 26 --> Z
     *
     * @param index
     * @return
     */
    public static String getUpperAlphaChar(int index) {
        if (index > 26 || index < 1) {
            return String.valueOf(index);
        }
        int s = 'A';
        int e = s + index - 1;
        char c = (char) e;
        return String.valueOf(c);
    }

    /**
     * 判断字母是否为 A ... Z
     *
     * @param c
     * @return
     */
    public static boolean isUpperAlphaChar(char c) {
        if ('A' <= c && c <= 'Z') {
            return true;
        }
        return false;
    }

    /**
     * 是否为空字符串
     *
     * @param str null is true, "" is true , "  " is true , other false
     * @return
     */
    public static boolean isBlank(String str) {
        if (str == null || str.trim().length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 是否为空字符串
     *
     * @param str null is true, "" is true , other false
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        return false;
    }

    public static String humanmizeTime(String time) {
        try {
            Date date = DateUtil.parseLongDate(time);
            long minutesToNow = (new Date().getTime() - date.getTime()) / 1000 / 60;//距今分钟数
            if (minutesToNow < 5) {
                return "刚刚";
            }
            if (minutesToNow < 60 && minutesToNow >=5) {
                //创建时间<1 小时,显示:“X 分钟前“;  
                return minutesToNow + "分钟前";
            }
            if (minutesToNow < 24 * 60 && minutesToNow >= 60) {
                //1 小时≤创建时间<24 小时,显示:“X 小时前
                return (minutesToNow / 60) + "小时前";
            }
            if (minutesToNow < 24 * 60 * 30) {
                // 1 天≤创建时间<30 天
            	if(minutesToNow>=24 * 60 * 6){
            		return "".equals(time)?"":time ;
            	}
                return (minutesToNow / 60 / 24) + "天前";
            }
            return time ;
            //1 月≤创建时间,显示:“X 月前“
            //return (minutesToNow / 60 / 24 / 30) + "月前";

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //截取字符串，根据字节长度
    public static String subStr(String str, int subLength) throws UnsupportedEncodingException {
        if (str != null) {
            int tempSubLength = subLength;//截取字节数
            String subStr = str.substring(0, str.length() < subLength ? str.length() : subLength);//截取的子串
            int subStrByetsL = subStr.getBytes("GBK").length;//截取子串的字节长度
            // 说明截取的字符串中包含有汉字
            while (subStrByetsL > tempSubLength) {
                int subSLengthTemp = --subLength;
                subStr = str.substring(0, subSLengthTemp > str.length() ? str.length() : subSLengthTemp);
                subStrByetsL = subStr.getBytes("GBK").length;
            }
            return subStr;
        }
        return "";
    }

    //TextView会自动换行，而且排版文字参差不齐
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }
    
    //获取用户图像地址
    public static String getPathByUid(String uid){
		try {
			StringBuffer stringBuffer = new StringBuffer("");
			int length = uid.length() ;
			long time = System.currentTimeMillis() ;
			if(length < 9){
				uid = "0" + uid  ;
				return getPathByUid(uid) ;
			}
			
			else if(length > 9 && length < 12){
				uid = "0" + uid  ;
				return getPathByUid(uid) ;
			}
			else if(length > 12 && length < 15){
				uid = "0" + uid  ;
				return getPathByUid(uid) ;
			}
			else if(length > 15 && length < 18){
				uid = "0" + uid  ;
				return getPathByUid(uid) ;
			}
			else if(length > 18 && length < 21){
				uid = "0" + uid  ;
				return getPathByUid(uid) ;
			}
			
			if(uid.length() == 9){
				stringBuffer.append(uid.substring(0, 3)+"/"+uid.substring(3, 6) +"/"+uid.substring(6, 9));
			}else if(uid.length() == 12){
				stringBuffer.append(uid.substring(0, 3)+"/"+uid.substring(3, 6) +"/"+uid.substring(6, 9)+"/"+uid.substring(9, 12));
			}else if(uid.length() == 15){
				stringBuffer.append(uid.substring(0, 3)+"/"+uid.substring(3, 6) +"/"+uid.substring(6, 9)+"/"+uid.substring(9, 12)+"/"+uid.substring(12, 15));
			}else if(uid.length() == 18){
				stringBuffer.append(uid.substring(0, 3)+"/"+uid.substring(3, 6) +"/"+uid.substring(6, 9)+"/"+uid.substring(9, 12)
						+"/"+uid.substring(12, 15)
						+"/"+uid.substring(15, 18));
			}else if(uid.length() == 21){
				stringBuffer.append(
						uid.substring(0, 3)
						+"/"+uid.substring(3, 6) 
						+"/"+uid.substring(6, 9)
						+"/"+uid.substring(9, 12)
						+"/"+uid.substring(12, 15)
						+"/"+uid.substring(15, 18)
						+"/"+uid.substring(18, 21));
			}

			stringBuffer.append("/source.jpg?time="+time);
			if(length <= 9){
				return stringBuffer.toString() ;
			}else if(length > 9 && length <=12 ){
				return stringBuffer.toString() ;
			}else if(length > 12 && length <=15 ){
				return stringBuffer.toString() ;
			}else if(length > 15 && length <=18 ){
				return stringBuffer.toString() ;
			}else{
				return stringBuffer.toString() ;
			}
			
		} catch (Exception e) {
			return "" ;
		}
		
	}
    
    //获取用户等级
    public static String getLevel(int integral){
    	String level = "Lv0" ;
    	try {
    		if(integral < 50){
    			level = "Lv0" ;
    		}else if( integral >=50 && integral < 200){
    			level = "Lv1" ;
    		}else if(integral >=200 && integral < 500){
    			level = "Lv2" ;
    		}else if(integral >=500 && integral < 1000){
    			level = "Lv3" ;
    		}else if(integral >=1000 && integral < 2000){
    			level = "Lv4" ;
    		}else if(integral >=2000 && integral < 5000){
    			level = "Lv5" ;
    		}else if(integral >=5000 && integral < 10000){
    			level = "Lv6" ;
    		}else if(integral >=10000 && integral < 20000){
    			level = "Lv7" ;
    		}else if(integral >=20000 && integral < 50000){
    			level = "Lv8" ;
    		}else if(integral >=50000 && integral < 60000){
    			level = "Lv9" ;
    		}else if(integral >=60000 && integral < 120000){
    			level = "Lv10" ;
    		}else if(integral >=120000){
    			level = "Lv11" ;
    		}
    	} catch (Exception e) {
    		return "Lv1" ;
    	}
    	return level ;
    	
    }
    
    //activity status[99:未公布，100：火热进行中，101：活动已结束，等待公布!，102：获奖公布中，103：往期回顾]
    public static String getactivityStatus(int integral){
    	String level = "火热进行中" ;
    	try {
    		if(integral == 99){
    			level = "未公布" ;
    		}else if(integral == 100){
    			level = "火热进行中" ;
    		}else if(integral == 101){
    			level = "活动已结束，等待公布!" ;
    		}else if(integral == 102){
    			level = "获奖公布中" ;
    		}else if(integral == 103){
    			level = "往期回顾" ;
    		}
    	} catch (Exception e) {
    		return "" ;
    	}
    	return level ;
    	
    }
    
    
    //金币规则类型
    public static String getCoinsType(String type){
    	String type_str = "" ;
    	try {
    		if("post".equals(type)){
    			type_str = "评论" ;
    		}else if("comment".equals(type)){
    			type_str = "回复" ;
    		}else if("postpraise".equals(type)){
    			type_str = "评论点赞" ;
    		}else if("commentpraise".equals(type)){
    			type_str = "回复点赞" ;
    		}else if("register".equals(type)){
    			type_str = "注册" ;
    		}
    	} catch (Exception e) {
    		return "" ;
    	}
    	return type_str ;
    	
    }

}
