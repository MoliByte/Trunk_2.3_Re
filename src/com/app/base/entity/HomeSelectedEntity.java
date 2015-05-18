package com.app.base.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class HomeSelectedEntity implements Serializable,HomeInterface{
	private String data_type;
	private String uid;
	private String nickname;
	private String age;
	private String user_avatar;
	private String skin_type_name;
	private String upload_img;
	private String testbefore1;
	private String testbefore2;
	private String testbefore3;
	private String remark;
	private String id;
	private String author_id;
	private String author_nick;
	private String type;
	private String title;
	private String content;
	private String view_count;
	private String public_time;
	private String create_time;
	private int comment_count;
	private int praise_count;
	private int favorite_count;
	private String imgurl;
	private String share_imgurl;
	private int img_width;
	private int img_height;
	private int can_praise;
	private int can_favorite;
	private String addtime;
	
	private String end_time;
	private int joincount;
	
	
	
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public int getJoincount() {
		return joincount;
	}
	public void setJoincount(int joincount) {
		this.joincount = joincount;
	}
	public String getAddtime() {
		return addtime;
	}
	public void setAddtime(String addtime) {
		this.addtime = addtime;
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
	public String getData_type() {
		return data_type;
	}
	public void setData_type(String data_type) {
		this.data_type = data_type;
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
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
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
	public String getUpload_img() {
		return upload_img;
	}
	public void setUpload_img(String upload_img) {
		this.upload_img = upload_img;
	}
	public String getTestbefore1() {
		return testbefore1;
	}
	public void setTestbefore1(String testbefore1) {
		this.testbefore1 = testbefore1;
	}
	public String getTestbefore2() {
		return testbefore2;
	}
	public void setTestbefore2(String testbefore2) {
		this.testbefore2 = testbefore2;
	}
	public String getTestbefore3() {
		return testbefore3;
	}
	public void setTestbefore3(String testbefore3) {
		this.testbefore3 = testbefore3;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAuthor_id() {
		return author_id;
	}
	public void setAuthor_id(String author_id) {
		this.author_id = author_id;
	}
	public String getAuthor_nick() {
		return author_nick;
	}
	public void setAuthor_nick(String author_nick) {
		this.author_nick = author_nick;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getView_count() {
		return view_count;
	}
	public void setView_count(String view_count) {
		this.view_count = view_count;
	}
	public String getPublic_time() {
		return public_time;
	}
	public void setPublic_time(String public_time) {
		this.public_time = public_time;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
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
	public String getImgurl() {
		return imgurl;
	}
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
	public String getShare_imgurl() {
		return share_imgurl;
	}
	public void setShare_imgurl(String share_imgurl) {
		this.share_imgurl = share_imgurl;
	}
	@Override
	public String toString() {
		return "HomeSelectedEntity [data_type=" + data_type + ", uid=" + uid
				+ ", nickname=" + nickname + ", age=" + age + ", user_avatar="
				+ user_avatar + ", skin_type_name=" + skin_type_name
				+ ", upload_img=" + upload_img + ", testbefore1=" + testbefore1
				+ ", testbefore2=" + testbefore2 + ", testbefore3="
				+ testbefore3 + ", remark=" + remark + ", id=" + id
				+ ", author_id=" + author_id + ", author_nick=" + author_nick
				+ ", type=" + type + ", title=" + title + ", content="
				+ content + ", view_count=" + view_count + ", public_time="
				+ public_time + ", create_time=" + create_time
				+ ", comment_count=" + comment_count + ", praise_count="
				+ praise_count + ", favorite_count=" + favorite_count
				+ ", imgurl=" + imgurl + ", share_imgurl=" + share_imgurl
				+ ", img_width=" + img_width + ", img_height=" + img_height
				+ ", can_praise=" + can_praise + ", can_favorite="
				+ can_favorite + "]";
	}
	
	
}
/*
      "data_type": "facemark",
      "uid": "25340",
      "nickname": "Lenny",
      "age": 25,
      "user_avatar": "",
      "skin_type_name": "中性肤质",
      "id": "30",
      "upload_img": "",
      "testbefore1": "32.63",
      "testbefore2": "0.00",
      "testbefore3": "32.12",
      "remark": "",
      "comment_count": 0,
      "praise_count": 0,
      "favorite_count": 0
{
      "data_type": "archive",
      "id": "181",
      "author_id": "33621",
      "author_nick": "管家二妞",
      "type": "post",
      "title": "肌肤管家~~~给肌肤最亲的接触",
      "content": 
      "view_count": "105",
      "praise_count": "11",
      "comment_count": "18",
      "public_time": "2015-03-13 16:36:49",
      "create_time": "2015-03-13 16:36:49",
      "imgurl": "1426235809261.jpg",
      "share_imgurl": "1426235809261.jpg"
    }
*/