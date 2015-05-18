package com.app.base.entity;

import org.kymjs.aframe.database.annotate.Id;
import org.kymjs.aframe.database.annotate.Property;
import org.kymjs.aframe.database.annotate.Table;

@Table (name="uv_info")
public class UvEntity {
	@Id(column="id")
	private int id;
	@Property(column="uv")
	private String uv;
	@Property(column="affect")
	private int affect;
	
	
	public UvEntity() {
		super();
	}
	public UvEntity(int id, String uv, int affect) {
		super();
		this.id = id;
		this.uv = uv;
		this.affect = affect;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUv() {
		return uv;
	}
	public void setUv(String uv) {
		this.uv = uv;
	}
	public int getAffect() {
		return affect;
	}
	public void setAffect(int affect) {
		this.affect = affect;
	}
}
