package com.app.base.entity;

import org.kymjs.aframe.database.annotate.Id;
import org.kymjs.aframe.database.annotate.Property;
import org.kymjs.aframe.database.annotate.Table;

@Table (name="humidity_info")
public class HumidityEntity {
	@Id(column="id")
	private int id;
	@Property(column="min_hum")
	private int min_hum;
	@Property(column="max_hum")
	private int max_hum;
	@Property(column="affect")
	private int affect;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMin_hum() {
		return min_hum;
	}
	public void setMin_hum(int min_hum) {
		this.min_hum = min_hum;
	}
	public int getMax_hum() {
		return max_hum;
	}
	public void setMax_hum(int max_hum) {
		this.max_hum = max_hum;
	}
	public int getAffect() {
		return affect;
	}
	public void setAffect(int affect) {
		this.affect = affect;
	}
}
