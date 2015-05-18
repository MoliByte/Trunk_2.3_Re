package com.app.base.entity;

import org.kymjs.aframe.database.annotate.Id;
import org.kymjs.aframe.database.annotate.Property;
import org.kymjs.aframe.database.annotate.Table;

@Table (name="industryStandard_info")
public class IndustryStandardEntity {
	@Id(column="id")
	private int id;
	@Property(column="age")
	private int age;
	@Property(column="standard")
	private double standard;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public double getStandard() {
		return standard;
	}
	public void setStandard(double standard) {
		this.standard = standard;
	}
}
