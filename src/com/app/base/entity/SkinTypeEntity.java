package com.app.base.entity;

import org.kymjs.aframe.database.annotate.Id;
import org.kymjs.aframe.database.annotate.Property;
import org.kymjs.aframe.database.annotate.Table;

@Table (name="skinType_info")
public class SkinTypeEntity {
	@Id(column="id")
	private int id;
	@Property(column="name")
	private String name;
	@Property(column="affect")
	private int affect;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAffect() {
		return affect;
	}
	public void setAffect(int affect) {
		this.affect = affect;
	}
}
