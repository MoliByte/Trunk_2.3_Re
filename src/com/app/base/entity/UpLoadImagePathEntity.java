package com.app.base.entity;

import org.kymjs.aframe.database.annotate.Id;
import org.kymjs.aframe.database.annotate.Property;
import org.kymjs.aframe.database.annotate.Table;

@Table (name="UpLoadImagePathEntity")
public class UpLoadImagePathEntity {
	@Id(column="token")
	private String token;
	@Property(column="imageUrl")
	private String imageUrl;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public UpLoadImagePathEntity(String token, String imageUrl) {
		super();
		this.token = token;
		this.imageUrl = imageUrl;
	}
	public UpLoadImagePathEntity() {
		super();
	}
	
}
