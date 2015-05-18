package com.app.base.entity;

public class SkinScoreEntity implements Comparable<SkinScoreEntity>{
	private String date;
	private double skinScore;
	public SkinScoreEntity() {
		super();
	}
	public SkinScoreEntity(String date, double skinScore) {
		super();
		this.date = date;
		this.skinScore = skinScore;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public double getSkinScore() {
		return skinScore;
	}
	public void setSkinScore(double skinScore) {
		this.skinScore = skinScore;
	}
	@Override
	public int compareTo(SkinScoreEntity s) {
		
		
		return this.date.compareTo(s.date);
	}
}
