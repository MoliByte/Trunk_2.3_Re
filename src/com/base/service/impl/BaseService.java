package com.base.service.impl;

import org.apache.http.HttpStatus;

public class BaseService {
	public boolean  isSuccess(int statusCode){
		if(HttpStatus.SC_OK == statusCode 
				|| HttpStatus.SC_CREATED == statusCode){
			return true ;
		}else{
			return false ;
		}
	}
}

