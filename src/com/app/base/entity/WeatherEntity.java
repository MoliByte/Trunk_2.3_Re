package com.app.base.entity;

import org.kymjs.aframe.database.annotate.Id;
import org.kymjs.aframe.database.annotate.Property;
import org.kymjs.aframe.database.annotate.Table;

@Table (name="weather_info")
public class WeatherEntity {
	@Id(column="lastTime")
	private long lastTime;
	@Property(column="temperature")
	private int temperature;
	@Property(column="uv")
	private String uv;
	@Property(column="humidity")
	private int humidity;
	@Property(column="lat")
	private double lat;
	@Property(column="lng")
	private double lng;
	@Property(column="country")
	private int country;
	@Property(column="region")
	private String region;
	@Property(column="Tag")
	private String Tag;
	
	
	public String getTag() {
		return Tag;
	}
	public void setTag(String tag) {
		Tag = tag;
	}
	public WeatherEntity() {
		super();
	}
	public WeatherEntity(int temperature, String uv, int humidity) {
		super();
		this.temperature = temperature;
		this.uv = uv;
		this.humidity = humidity;
	}
	public long getLastTime() {
		return lastTime;
	}
	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}
	public int getTemperature() {
		return temperature;
	}
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	public String getUv() {
		return uv;
	}
	public void setUv(String uv) {
		this.uv = uv;
	}
	public int getHumidity() {
		return humidity;
	}
	public void setHumidity(int humidity) {
		this.humidity = humidity;
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
	@Override
	public String toString() {
		return "WeatherEntity [lastTime=" + lastTime + ", temperature="
				+ temperature + ", uv=" + uv + ", humidity=" + humidity
				+ ", lat=" + lat + ", lng=" + lng + ", country=" + country
				+ ", region=" + region + "]";
	}
	
}
