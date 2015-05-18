package com.app.base.entity;

import java.io.Serializable;

import org.kymjs.aframe.database.annotate.Id;
import org.kymjs.aframe.database.annotate.Property;
import org.kymjs.aframe.database.annotate.Table;

@Table (name="facialTestRecord")
public class FacialTestEntity implements Serializable{
	@Id(column="token")
	private String token;
	@Property(column="water1")
	private float water1;
	@Property(column="time1")
	private String time1;
	@Property(column="water2")
	private float water2;
	@Property(column="time2")
	private String time2;
	@Property(column="water3")
	private float water3;
	@Property(column="time3")
	private String time3;
	@Property(column="water4")
	private float water4;
	@Property(column="time4")
	private String time4;
	
	public FacialTestEntity() {
		super();
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public float getWater1() {
		return water1;
	}
	public void setWater1(float water1) {
		this.water1 = water1;
	}
	public String getTime1() {
		return time1;
	}
	public void setTime1(String time1) {
		this.time1 = time1;
	}
	public float getWater2() {
		return water2;
	}
	public void setWater2(float water2) {
		this.water2 = water2;
	}
	public String getTime2() {
		return time2;
	}
	public void setTime2(String time2) {
		this.time2 = time2;
	}
	public float getWater3() {
		return water3;
	}
	public void setWater3(float water3) {
		this.water3 = water3;
	}
	public String getTime3() {
		return time3;
	}
	public void setTime3(String time3) {
		this.time3 = time3;
	}
	public float getWater4() {
		return water4;
	}
	public void setWater4(float water4) {
		this.water4 = water4;
	}
	public String getTime4() {
		return time4;
	}
	public void setTime4(String time4) {
		this.time4 = time4;
	}
}
