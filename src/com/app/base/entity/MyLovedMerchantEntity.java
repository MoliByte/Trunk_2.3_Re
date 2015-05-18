package com.app.base.entity;

import java.io.Serializable;

public class MyLovedMerchantEntity implements Serializable{
	private String id;
	private String name;
	private String logo;
	private int logo_width;
	private int logo_height;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public int getLogo_width() {
		return logo_width;
	}
	public void setLogo_width(int logo_width) {
		this.logo_width = logo_width;
	}
	public int getLogo_height() {
		return logo_height;
	}
	public void setLogo_height(int logo_height) {
		this.logo_height = logo_height;
	}
}
/*
 "id": "160",
      "name": "徐州市闻泽商贸有限公司",
      "logo": "",
      "logo_width": "",
      "logo_height": ""

*/