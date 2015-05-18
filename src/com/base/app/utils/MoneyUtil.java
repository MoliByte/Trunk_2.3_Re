package com.base.app.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by fangjilue on 14-4-30.
 */
public class MoneyUtil {

    private BigDecimal val;

    public MoneyUtil(String val) {
        if (val == null || val.trim().length() == 0) {
            val = "0";
        }
        this.val = new BigDecimal(val).setScale(2, BigDecimal.ROUND_HALF_UP);
    }


    public String toCNString() {
        //单位为万
        int index = val.compareTo(new BigDecimal("10000"));
        if (index == -1) {
            //小于1万
            return format(val);
        } else if (index == 0) {
            //等于1万
            return "1万";
        } else {
            //大于1万
            //单位为亿
            int index2 = val.compareTo(new BigDecimal("100000000"));
            if (index2 == -1) {
                //小于亿单位为万
                return val.divide(new BigDecimal("10000")).longValue() + "万+";
            } else if (index2 == 0) {
                //等于1亿
                return "1亿";
            } else {
                //大于1亿单位为亿
                return val.divide(new BigDecimal("100000000")).longValue() + "亿+";
            }
        }
    }


    private String format(BigDecimal aa) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(aa);
    }

    /**
     * 获取整数部分
     * @return
     */
    public String getIntegerPart(){
        String str = format(val);
        return str.split("[.]")[0];
    }

    /**
     * 小数部分
     * @return
     */
    public String getDecimalPart(){
        String str = format(val);
        return str.split("[.]")[1];
    }

}
