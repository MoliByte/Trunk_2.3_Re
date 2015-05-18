package com.base.app.utils;

import java.util.List;

/**
 * Created by fangjilue on 14-5-11.
 */
public class BeanUtil {

    /**
     * 把来源追加到目标后面,去掉重复的
     * @param source
     * @param target
     */
    public static  void sourceAppendAfterOfTarget(List source, List target){

        for(Object item : source){
            if(!target.contains(item)){
                target.add(item);
            }
        }
    }
}
