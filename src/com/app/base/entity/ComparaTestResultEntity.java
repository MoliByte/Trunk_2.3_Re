package com.app.base.entity;

public class ComparaTestResultEntity {
	private ComparaDataEntity compare_2_lastweek;
	private ComparaDataEntity same_age;
	public ComparaDataEntity getCompare_2_lastweek() {
		return compare_2_lastweek;
	}
	public void setCompare_2_lastweek(ComparaDataEntity compare_2_lastweek) {
		this.compare_2_lastweek = compare_2_lastweek;
	}
	public ComparaDataEntity getSame_age() {
		return same_age;
	}
	public void setSame_age(ComparaDataEntity same_age) {
		this.same_age = same_age;
	}
}
