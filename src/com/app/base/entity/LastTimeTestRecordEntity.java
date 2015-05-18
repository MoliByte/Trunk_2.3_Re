package com.app.base.entity;

import java.io.Serializable;

import org.kymjs.aframe.database.annotate.Id;
import org.kymjs.aframe.database.annotate.Property;
import org.kymjs.aframe.database.annotate.Table;
@Table (name="lastTimeTestRecord")
@SuppressWarnings("serial")
public class LastTimeTestRecordEntity implements Serializable{
	@Id(column="time")
	private String time;
	@Property(column="token")
	private String token;
	@Property(column="water")
	private float water;
	@Property(column="oil")
	private float oil;
	@Property(column="flexible")
	private float flexible;
	@Property(column="standardWater")
	private float standardWater;
	@Property(column="standardOil")
	private float standardOil;
	@Property(column="StandardSkinScore")
	private int StandardSkinScore;
	@Property(column="currentSkinScore")
	private int currentSkinScore;
	@Property(column="testPart")
	private String testPart;
	@Property(column="area")
	private String area;
	
	@Property(column="skinComment")
	private String skinComment;
	
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public float getWater() {
		return water;
	}
	public void setWater(float water) {
		this.water = water;
	}
	public float getOil() {
		return oil;
	}
	public void setOil(float oil) {
		this.oil = oil;
	}
	public float getFlexible() {
		return flexible;
	}
	public void setFlexible(float flexible) {
		this.flexible = flexible;
	}
	public float getStandardWater() {
		return standardWater;
	}
	public void setStandardWater(float standardWater) {
		this.standardWater = standardWater;
	}
	public float getStandardOil() {
		return standardOil;
	}
	public void setStandardOil(float standardOil) {
		this.standardOil = standardOil;
	}
	public int getStandardSkinScore() {
		return StandardSkinScore;
	}
	public void setStandardSkinScore(int standardSkinScore) {
		StandardSkinScore = standardSkinScore;
	}
	public int getCurrentSkinScore() {
		return currentSkinScore;
	}
	public void setCurrentSkinScore(int currentSkinScore) {
		this.currentSkinScore = currentSkinScore;
	}
	public String getTestPart() {
		return testPart;
	}
	public void setTestPart(String testPart) {
		this.testPart = testPart;
	}
	public String getSkinComment() {
		return skinComment;
	}
	public void setSkinComment(String skinComment) {
		this.skinComment = skinComment;
	}
}
