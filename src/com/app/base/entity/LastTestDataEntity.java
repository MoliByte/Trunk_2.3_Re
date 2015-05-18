package com.app.base.entity;

import org.kymjs.aframe.database.annotate.Id;
import org.kymjs.aframe.database.annotate.Property;
import org.kymjs.aframe.database.annotate.Table;

@Table (name="lastTestData")
public class LastTestDataEntity {
	@Id(column="token")
	private String token;
	@Property(column="face_lastday")
	private String face_lastday;
	@Property(column="face_area")
	private String face_area;
	@Property(column="face_water")
	private int face_water;
	@Property(column="face_oil")
	private int face_oil;
	@Property(column="face_elasticity")
	private int face_elasticity;
	@Property(column="hand_lastday")
	private String hand_lastday;
	@Property(column="hand_area")
	private String hand_area;
	@Property(column="hand_water")
	private int hand_water;
	@Property(column="hand_oil")
	private int hand_oil;
	@Property(column="hand_elasticity")
	private int hand_elasticity;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getFace_lastday() {
		return face_lastday;
	}
	public void setFace_lastday(String face_lastday) {
		this.face_lastday = face_lastday;
	}
	public String getFace_area() {
		return face_area;
	}
	public void setFace_area(String face_area) {
		this.face_area = face_area;
	}
	public int getFace_water() {
		return face_water;
	}
	public void setFace_water(int face_water) {
		this.face_water = face_water;
	}
	public int getFace_oil() {
		return face_oil;
	}
	public void setFace_oil(int face_oil) {
		this.face_oil = face_oil;
	}
	public int getFace_elasticity() {
		return face_elasticity;
	}
	public void setFace_elasticity(int face_elasticity) {
		this.face_elasticity = face_elasticity;
	}
	public String getHand_lastday() {
		return hand_lastday;
	}
	public void setHand_lastday(String hand_lastday) {
		this.hand_lastday = hand_lastday;
	}
	public String getHand_area() {
		return hand_area;
	}
	public void setHand_area(String hand_area) {
		this.hand_area = hand_area;
	}
	public int getHand_water() {
		return hand_water;
	}
	public void setHand_water(int hand_water) {
		this.hand_water = hand_water;
	}
	public int getHand_oil() {
		return hand_oil;
	}
	public void setHand_oil(int hand_oil) {
		this.hand_oil = hand_oil;
	}
	public int getHand_elasticity() {
		return hand_elasticity;
	}
	public void setHand_elasticity(int hand_elasticity) {
		this.hand_elasticity = hand_elasticity;
	}
	@Override
	public String toString() {
		return "LastTestDataEntity [token=" + token + ", face_lastday="
				+ face_lastday + ", face_area=" + face_area + ", face_water="
				+ face_water + ", face_oil=" + face_oil + ", face_elasticity="
				+ face_elasticity + ", hand_lastday=" + hand_lastday
				+ ", hand_area=" + hand_area + ", hand_water=" + hand_water
				+ ", hand_oil=" + hand_oil + ", hand_elasticity="
				+ hand_elasticity + "]";
	}
}
