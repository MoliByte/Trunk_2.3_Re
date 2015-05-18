package com.app.base.entity;

public class BrandEntity {
	private int brand_id;
	private String brand_name;
	private String idx_name;
	private String logo;
	public int getBrand_id() {
		return brand_id;
	}
	public void setBrand_id(int brand_id) {
		this.brand_id = brand_id;
	}
	public String getBrand_name() {
		return brand_name;
	}
	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
	}
	public String getIdx_name() {
		return idx_name;
	}
	public void setIdx_name(String idx_name) {
		this.idx_name = idx_name;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
}
/*

"brand_id": "1",
        "brand_name": "测试品牌",
        "idx_name": "TSPP",
        "logo": "logo.gif"
*/