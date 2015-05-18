package com.app.base.entity;

import java.io.Serializable;

import org.kymjs.aframe.database.annotate.Id;
import org.kymjs.aframe.database.annotate.Property;
import org.kymjs.aframe.database.annotate.Table;
@Table (name="testgetinfo")
@SuppressWarnings("serial")
public class TestEntity implements Serializable{
	@Id(column="token")
	private String token;
	@Property(column="water")
	private String water;
	@Property(column="testTime")
	private long testTime;
	@Property(column="oil")
	private String oil;
	@Property(column="elasticity")
	private String elasticity;
	@Property(column="star")
	private String star;
	@Property(column="star_comment")
	private String star_comment;
	@Property(column="water_title")
	private String water_title;
	@Property(column="oil_title")
	private String oil_title;
	@Property(column="elasticity_title")
	private String elasticity_title;
	@Property(column="water_content")
	private String water_content;
	@Property(column="oil_content")
	private String oil_content;
	@Property(column="elasticity_content")
	private String elasticity_content;
	@Property(column="skin_age")
	private String skin_age;
	@Property(column="test_score")
	private String test_score;
	@Property(column="personal_standard")
	private String personal_standard;
	@Property(column="test_pre_diff")
	private String test_pre_diff;
	
	
	public long getTestTime() {
		return testTime;
	}
	public void setTestTime(long testTime) {
		this.testTime = testTime;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getWater() {
		return water;
	}
	public void setWater(String water) {
		this.water = water;
	}
	public String getOil() {
		return oil;
	}
	public void setOil(String oil) {
		this.oil = oil;
	}
	public String getElasticity() {
		return elasticity;
	}
	public void setElasticity(String elasticity) {
		this.elasticity = elasticity;
	}
	public String getStar() {
		return star;
	}
	public void setStar(String star) {
		this.star = star;
	}
	public String getStar_comment() {
		return star_comment;
	}
	public void setStar_comment(String star_comment) {
		this.star_comment = star_comment;
	}
	public String getWater_title() {
		return water_title;
	}
	public void setWater_title(String water_title) {
		this.water_title = water_title;
	}
	public String getOil_title() {
		return oil_title;
	}
	public void setOil_title(String oil_title) {
		this.oil_title = oil_title;
	}
	public String getElasticity_title() {
		return elasticity_title;
	}
	public void setElasticity_title(String elasticity_title) {
		this.elasticity_title = elasticity_title;
	}
	public String getWater_content() {
		return water_content;
	}
	public void setWater_content(String water_content) {
		this.water_content = water_content;
	}
	public String getOil_content() {
		return oil_content;
	}
	public void setOil_content(String oil_content) {
		this.oil_content = oil_content;
	}
	public String getElasticity_content() {
		return elasticity_content;
	}
	public void setElasticity_content(String elasticity_content) {
		this.elasticity_content = elasticity_content;
	}
	public String getSkin_age() {
		return skin_age;
	}
	public void setSkin_age(String skin_age) {
		this.skin_age = skin_age;
	}
	public String getTest_score() {
		return test_score;
	}
	public void setTest_score(String test_score) {
		this.test_score = test_score;
	}
	public String getPersonal_standard() {
		return personal_standard;
	}
	public void setPersonal_standard(String personal_standard) {
		this.personal_standard = personal_standard;
	}
	public String getTest_pre_diff() {
		return test_pre_diff;
	}
	public void setTest_pre_diff(String test_pre_diff) {
		this.test_pre_diff = test_pre_diff;
	}
}