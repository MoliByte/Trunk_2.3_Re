package com.base.app.utils;

import java.math.BigDecimal;

/**
 * Created by fangjilue on 14-7-8.
 */
public class NumberUtil {

    /**
     * 除以100 保留一位小数. 拼接百分号:0.0% , 66.9%
     * @param val
     * @return
     */
    public static String getRealVal(Integer val){
        if(val == null){
            return  "0.0%";
        }

        BigDecimal bigVal = new BigDecimal(val.toString());
        BigDecimal div = new BigDecimal("100");

        return bigVal.divide(div,1, BigDecimal.ROUND_HALF_UP).toString().concat("%");
    }

    /**
     * 除以100 保留一位小数. 拼接百分号:0.0% , 66.9%
     * @param val
     * @return
     */
    public static String getRealVal(String val){
        if(StringUtil.isEmpty(val)){
            return  "0.0%";
        }

        return getRealVal(Integer.parseInt(val));
    }

    public static String getValNoPer(Integer val){
        if(val == null){
            return  "0";
        }

        BigDecimal bigVal = new BigDecimal(val.toString());
        BigDecimal div = new BigDecimal("100");

        return bigVal.divide(div,0, BigDecimal.ROUND_HALF_UP).toString();
    }

    public static String getValNoPer(String val){
        if(StringUtil.isEmpty(val)){
            return "0";
        }
        return getValNoPer(Integer.parseInt(val));
    }
}
