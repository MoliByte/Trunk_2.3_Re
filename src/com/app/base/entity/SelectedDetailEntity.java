package com.app.base.entity;

import java.util.ArrayList;

public class SelectedDetailEntity {
	private String uid;
	private String nickname;
	private int age;
	private String user_avatar;
	
	private String skin_type_name;
	private String id;
	private String addtime;
	private String upload_img;
	
	private int img_width;
	private int img_height;
	private int testbefore1;
	private int testbefore2;
	
	private int testbefore3;
	private String remark;
	private int praise_count;
	private int comments_count;
	private ArrayList<PraiseJoinEntity> praise_users;
	
	
	public int getComments_count() {
		return comments_count;
	}
	public void setComments_count(int comments_count) {
		this.comments_count = comments_count;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
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
	public String getAddtime() {
		return addtime;
	}
	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}
	public String getUpload_img() {
		return upload_img;
	}
	public void setUpload_img(String upload_img) {
		this.upload_img = upload_img;
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
	public int getTestbefore1() {
		return testbefore1;
	}
	public void setTestbefore1(int testbefore1) {
		this.testbefore1 = testbefore1;
	}
	public int getTestbefore2() {
		return testbefore2;
	}
	public void setTestbefore2(int testbefore2) {
		this.testbefore2 = testbefore2;
	}
	public int getTestbefore3() {
		return testbefore3;
	}
	public void setTestbefore3(int testbefore3) {
		this.testbefore3 = testbefore3;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getPraise_count() {
		return praise_count;
	}
	public void setPraise_count(int praise_count) {
		this.praise_count = praise_count;
	}
	public ArrayList<PraiseJoinEntity> getPraise_users() {
		return praise_users;
	}
	public void setPraise_users(ArrayList<PraiseJoinEntity> praise_users) {
		this.praise_users = praise_users;
	}
	@Override
	public String toString() {
		return "SelectedDetailEntity [uid=" + uid + ", nickname=" + nickname
				+ ", age=" + age + ", user_avatar=" + user_avatar
				+ ", skin_type_name=" + skin_type_name + ", id=" + id
				+ ", addtime=" + addtime + ", upload_img=" + upload_img
				+ ", img_width=" + img_width + ", img_height=" + img_height
				+ ", testbefore1=" + testbefore1 + ", testbefore2="
				+ testbefore2 + ", testbefore3=" + testbefore3 + ", remark="
				+ remark + ", praise_count=" + praise_count
				+ ", comments_count=" + comments_count + ", praise_users="
				+ praise_users + "]";
	}
}
