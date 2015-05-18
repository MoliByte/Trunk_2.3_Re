package com.app.base.init;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fangjilue on 14-5-1.
 */
public class MapBuilder {
	private Map<String,Object> map = new HashMap<String, Object>();

	public static MapBuilder create(){
		return new MapBuilder();
	}

	public MapBuilder put(String key, Object value){
		map.put(key,value);
		return this;
	}

	public Map<String,Object> get(){
		return map;
	}
}
