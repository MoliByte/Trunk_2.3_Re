package com.app.base.entity;

public class MerchantDetailEntity {
	private String id;
	private String company_name;
	private String contact_phone;
	private String remark;
	private String address;
	private String merchant_img;
	private int img_width;
	private int img_height;
	
	public String getMerchant_img() {
		return merchant_img;
	}
	public void setMerchant_img(String merchant_img) {
		this.merchant_img = merchant_img;
	}
	public int getImg_width() {
		return img_width;
	}
	public void setImg_width(int img_width) {
		this.img_width = img_width;
	}
	public int getImg_height() {
		return img_height;
	}
	public void setImg_height(int img_height) {
		this.img_height = img_height;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getContact_phone() {
		return contact_phone;
	}
	public void setContact_phone(String contact_phone) {
		this.contact_phone = contact_phone;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
}
/*
	
        "id": "28",
        "company_name": "莱卡塔城店",
        "contact_phone": "13817842161/02159520908",
        "remark": "专业美容",
        "address": "null",
        "merchant_img": "http://v2.api.skinrun.me/uploads/product/image/20150417/20150417094956_23642.png",
        "img_width": 400,
        "img_height": 400

*/