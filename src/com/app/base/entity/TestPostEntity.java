package com.app.base.entity;

import java.io.Serializable;

import org.kymjs.aframe.database.annotate.Id;
import org.kymjs.aframe.database.annotate.Property;
import org.kymjs.aframe.database.annotate.Table;

@Table (name="testpostinfo")
@SuppressWarnings("serial")
public class TestPostEntity implements Serializable{
	@Id(column="test_time")
	private String test_time;
	@Property(column="module")
	private String module="skin_test";
	@Property(column="uid")
	private int uid;
	@Property(column="area")
	private String area;
	@Property(column="water")
	private int water;
	@Property(column="oil")
	private int oil;
	@Property(column="elasticity")
	private int elasticity;
	@Property(column="lat")
	private double lat;
	@Property(column="lng")
	private double lng;
	@Property(column="temperature")
	private int temperature;
	@Property(column="humidity")
	private int humidity;
	@Property(column="ultraviolet")
	private String ultraviolet;
	@Property(column="data_flag")
	private int data_flag;
	@Property(column="country")
	private int country;
	@Property(column="region")
	private String region;
	@Property(column="st_water")
	private int st_water;
	@Property(column="st_oil")
	private int st_oil;
	@Property(column="os")
	private String os;
	@Property(column="os_version")
	private String os_version;
	@Property(column="app_version")
	private String app_version;
	@Property(column="device_info")
	private String device_info;
	@Property(column="appid")
	private String appid;
	@Property(column="skin_type")
	private int skin_type;
	public String getTest_time() {
		return test_time;
	}
	public void setTest_time(String test_time) {
		this.test_time = test_time;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public int getWater() {
		return water;
	}
	public void setWater(int water) {
		this.water = water;
	}
	public int getOil() {
		return oil;
	}
	public void setOil(int oil) {
		this.oil = oil;
	}
	public int getElasticity() {
		return elasticity;
	}
	public void setElasticity(int elasticity) {
		this.elasticity = elasticity;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public int getTemperature() {
		return temperature;
	}
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	public int getHumidity() {
		return humidity;
	}
	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}
	public String getUltraviolet() {
		return ultraviolet;
	}
	public void setUltraviolet(String ultraviolet) {
		this.ultraviolet = ultraviolet;
	}
	public int getData_flag() {
		return data_flag;
	}
	public void setData_flag(int data_flag) {
		this.data_flag = data_flag;
	}
	public int getCountry() {
		return country;
	}
	public void setCountry(int country) {
		this.country = country;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public int getSt_water() {
		return st_water;
	}
	public void setSt_water(int st_water) {
		this.st_water = st_water;
	}
	public int getSt_oil() {
		return st_oil;
	}
	public void setSt_oil(int st_oil) {
		this.st_oil = st_oil;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getOs_version() {
		return os_version;
	}
	public void setOs_version(String os_version) {
		this.os_version = os_version;
	}
	public String getApp_version() {
		return app_version;
	}
	public void setApp_version(String app_version) {
		this.app_version = app_version;
	}
	public String getDevice_info() {
		return device_info;
	}
	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public int getSkin_type() {
		return skin_type;
	}
	public void setSkin_type(int skin_type) {
		this.skin_type = skin_type;
	}
}