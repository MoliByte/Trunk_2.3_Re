package com.yvelabs.satellitemenu.utils;

import java.util.List;

public class ObjectsUtils {
	
	public static boolean containsInt (List<Integer> list, int i) {
		for (Integer iObj : list) {
			if (iObj == i) return true;
		}
		return false;
	}

}
