package com.app.base.entity;

import java.io.Serializable;
import java.util.Comparator;

@SuppressWarnings("serial")
public class ActivityDataEntityComPare implements Serializable, Comparator {


	@Override
	public int compare(Object arg0, Object arg1) {
		ActivityDataEntity user0 = (ActivityDataEntity) arg0;
		ActivityDataEntity user1 = (ActivityDataEntity) arg1;

		int flag = user0.getType().compareTo(user1.getType());
		if (flag == 0) {
			return user0.getPublic_time().compareTo(user1.getPublic_time());
		} else {
			return flag;
		}
	}

}
