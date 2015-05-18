package com.app.base.entity;

import java.io.Serializable;

import org.kymjs.aframe.database.annotate.Id;
import org.kymjs.aframe.database.annotate.Property;
import org.kymjs.aframe.database.annotate.Table;
/**
 * 地区
 * @author zhup
 *
 */

@SuppressWarnings("serial")
@Table(name = "region")
public class RegionsEntity implements Serializable {
	@Id(column="id")
	private int id;
	@Property(column = "name")
	private String name;// 名称

	@Property(column = "code")
	private String code;// code

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
