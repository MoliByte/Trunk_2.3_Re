package com.app.base.entity;

public class BrandProductEntity {
	private String product_id;
	private String product_name;
	private String market_price;
	private String suitable;
	private String img_url;
	private int img_width;
	private int img_height;
	private String keywords;
	private int test_count;
	private int praise_count;
	
	
	public String getProduct_id() {
		return product_id;
	}
	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public String getMarket_price() {
		return market_price;
	}
	public void setMarket_price(String market_price) {
		this.market_price = market_price;
	}
	public String getSuitable() {
		return suitable;
	}
	public void setSuitable(String suitable) {
		this.suitable = suitable;
	}
	public String getImg_url() {
		return img_url;
	}
	public void setImg_url(String img_url) {
		this.img_url = img_url;
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
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public int getTest_count() {
		return test_count;
	}
	public void setTest_count(int test_count) {
		this.test_count = test_count;
	}
	public int getPraise_count() {
		return praise_count;
	}
	public void setPraise_count(int praise_count) {
		this.praise_count = praise_count;
	}
}
/*
 * "product_id": "1",
        "product_name": "测试产品名称",
        "market_price": "88.00",
        "suitable": "0",
        "img_url": null,
        "img_width": 0,
        "img_height": 0,
        "keywords": "保湿,控油",
        "test_count": "6",
        "praise_count": 0
 */