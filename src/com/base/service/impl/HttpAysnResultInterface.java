package com.base.service.impl;

/**
 * 异步返回对象数据接口
 * @author zhup
 *
 */
public interface  HttpAysnResultInterface{
	public void dataCallBack(Object tag,int statusCode ,Object result);
}
