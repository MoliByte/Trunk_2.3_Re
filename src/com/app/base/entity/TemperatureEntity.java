package com.app.base.entity;

import org.kymjs.aframe.database.annotate.Id;
import org.kymjs.aframe.database.annotate.Property;
import org.kymjs.aframe.database.annotate.Table;

@Table (name="temperature_info")
public class TemperatureEntity {
	@Id(column="id")
	private int id;
	@Property(column="min_temp")
	private int min_temp;
	@Property(column="max_temp")
	private int max_temp;
	@Property(column="affect")
	private int affect;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMin_temp() {
		return min_temp;
	}
	public void setMin_temp(int min_temp) {
		this.min_temp = min_temp;
	}
	public int getMax_temp() {
		return max_temp;
	}
	public void setMax_temp(int max_temp) {
		this.max_temp = max_temp;
	}
	public int getAffect() {
		return affect;
	}
	public void setAffect(int affect) {
		this.affect = affect;
	}
}
