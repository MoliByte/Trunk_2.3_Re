package com.base.app.utils;

import java.util.UUID;

public class UUIDGeneratorUtil {
	public static String getUUID() {  
        UUID uuid = UUID.randomUUID();  
        String str = uuid.toString();  
        return str ;
    }
}
