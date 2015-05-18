package com.app.base.entity;
public class ADInfo implements java.io.Serializable {
	
	private long id;
	private String adName;
	private String adImage;
	private String adUrl;
	private String createTime;
	private String updateTime;
	private Integer sort;
	
	private long create_user ;
	private String imgURL ;
	private String create_time ;
	private String begin_date ;
	private String end_date ;
	private String ad_link ;
	private int click_count ;
	private int number ;
	private String title ;
	private String type ;
	private int ad_number ;
	private String author_img ;
	
	
	
	public String getAuthor_img() {
		return author_img;
	}
	public void setAuthor_img(String author_img) {
		this.author_img = author_img;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getCreate_user() {
		return create_user;
	}
	public void setCreate_user(long create_user) {
		this.create_user = create_user;
	}
	public String getImgURL() {
		return imgURL;
	}
	public void setImgURL(String imgURL) {
		this.imgURL = imgURL;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getBegin_date() {
		return begin_date;
	}
	public void setBegin_date(String begin_date) {
		this.begin_date = begin_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	
	public String getAd_link() {
		return ad_link;
	}
	public void setAd_link(String ad_link) {
		this.ad_link = ad_link;
	}
	public int getClick_count() {
		return click_count;
	}
	public void setClick_count(int click_count) {
		this.click_count = click_count;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getAd_number() {
		return ad_number;
	}
	public void setAd_number(int ad_number) {
		this.ad_number = ad_number;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getAdName() {
		return adName;
	}
	public void setAdName(String adName) {
		this.adName = adName;
	}
	public String getAdImage() {
		return adImage;
	}
	public void setAdImage(String adImage) {
		this.adImage = adImage;
	}
	public String getAdUrl() {
		return adUrl;
	}
	public void setAdUrl(String adUrl) {
		this.adUrl = adUrl;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}


}