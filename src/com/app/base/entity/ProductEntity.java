package com.app.base.entity;

import java.io.Serializable;

import org.kymjs.aframe.database.annotate.Id;
import org.kymjs.aframe.database.annotate.Property;
import org.kymjs.aframe.database.annotate.Table;
@Table (name="ProductEntity")
public class ProductEntity implements Serializable {
	@Id(column="token")
	private String token;
	@Property(column="productId")
	private int productId;
	@Property(column="pro_brand_name")
	private String pro_brand_name;
	@Property(column="pro_image")
	private String pro_image;
	
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getPro_brand_name() {
		return pro_brand_name;
	}
	public void setPro_brand_name(String pro_brand_name) {
		this.pro_brand_name = pro_brand_name;
	}
	public String getPro_image() {
		return pro_image;
	}
	public void setPro_image(String pro_image) {
		this.pro_image = pro_image;
	}
}

