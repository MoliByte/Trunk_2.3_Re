package com.app.base.entity;

import java.io.Serializable;

public class DongTaiEntity implements Serializable,HomeInterface{
	private String uid;
	private String age;
	private String nickname;
	private String user_avatar;
	private String skin_type_name;
	private String id;
	private String area;
	private String water;
	private String oil;
	private String elasticity;
	private String test_time;
	private String upload_img;
	private int comment_count;
	private int praise_count;
	private int favorite_count;
	private int img_width;
	private int img_height;
	private int can_praise;
	private int can_favorite;
	
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
	public int getCan_praise() {
		return can_praise;
	}
	public void setCan_praise(int can_praise) {
		this.can_praise = can_praise;
	}
	public int getCan_favorite() {
		return can_favorite;
	}
	public void setCan_favorite(int can_favorite) {
		this.can_favorite = can_favorite;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getUser_avatar() {
		return user_avatar;
	}
	public void setUser_avatar(String user_avatar) {
		this.user_avatar = user_avatar;
	}
	public String getSkin_type_name() {
		return skin_type_name;
	}
	public void setSkin_type_name(String skin_type_name) {
		this.skin_type_name = skin_type_name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getWater() {
		return water;
	}
	public void setWater(String water) {
		this.water = water;
	}
	public String getOil() {
		return oil;
	}
	public void setOil(String oil) {
		this.oil = oil;
	}
	public String getElasticity() {
		return elasticity;
	}
	public void setElasticity(String elasticity) {
		this.elasticity = elasticity;
	}
	
	public String getTest_time() {
		return test_time;
	}
	public void setTest_time(String test_time) {
		this.test_time = test_time;
	}
	public String getUpload_img() {
		return upload_img;
	}
	public void setUpload_img(String upload_img) {
		this.upload_img = upload_img;
	}
	public int getComment_count() {
		return comment_count;
	}
	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}
	public int getPraise_count() {
		return praise_count;
	}
	public void setPraise_count(int praise_count) {
		this.praise_count = praise_count;
	}
	public int getFavorite_count() {
		return favorite_count;
	}
	public void setFavorite_count(int favorite_count) {
		this.favorite_count = favorite_count;
	}
}
/*{
      "uid": "32519",
      "nickname": "",
      "age": 18,
      "user_avatar": "http://tp1.sinaimg.cn/1422177620/180/40053542992/0",
      "skin_type_name": "",
      "id": "132331",
      "area": "T",
      "water": "2406",
      "oil": "1076",
      "elasticity": "1719",
      "create_time": "2015-03-02 09:45:14",
      "upload_img": "",
      "comment_count": 0,
      "praise_count": 0,
      "favorite_count": 0
    }


*/