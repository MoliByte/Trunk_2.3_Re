package com.app.base.entity;

public class GetSkinScoreEntity {
	private String position;
	private String age;
	private double age_factor;
	private String suggestion;
	private SkinScoreSeriesEntity series;
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public double getAge_factor() {
		return age_factor;
	}
	public void setAge_factor(double age_factor) {
		this.age_factor = age_factor;
	}
	public String getSuggestion() {
		return suggestion;
	}
	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}
	public SkinScoreSeriesEntity getSeries() {
		return series;
	}
	public void setSeries(SkinScoreSeriesEntity series) {
		this.series = series;
	}
	@Override
	public String toString() {
		return "GetSkinScoreEntity [position=" + position + ", age=" + age
				+ ", age_factor=" + age_factor + ", suggestion=" + suggestion
				+ "]";
	}
	
}

