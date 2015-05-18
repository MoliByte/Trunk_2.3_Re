package com.app.base.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public class SuggestionFeedBackDataEntity implements Serializable /*,Comparable<SuggestionFeedBackDataEntity>*/{
	
	@SerializedName("id")				private int id ;
	@SerializedName("uid") 				private int uid ;				//用户id
	@SerializedName("user_img") 		private String user_img;		// 用户图片地址
	@SerializedName("content") 			private String content  ;		//内容
	@SerializedName("guanjia_img") 		private String guanjia_img ;	//管家图片
	@SerializedName("uname") 			private String uname ;			//管家名字
	@SerializedName("release_time") 	private String release_time ; 	//发布时间
	@SerializedName("type") 			private int type ;				//类型是发送还是回复
	
	
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUser_img() {
		return user_img;
	}
	public void setUser_img(String user_img) {
		this.user_img = user_img;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getGuanjia_img() {
		return guanjia_img;
	}
	public void setGuanjia_img(String guanjia_img) {
		this.guanjia_img = guanjia_img;
	}
	public String getRelease_time() {
		return release_time;
	}
	public void setRelease_time(String release_time) {
		this.release_time = release_time;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
}
