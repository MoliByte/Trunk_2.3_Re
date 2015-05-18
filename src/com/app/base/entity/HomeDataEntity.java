package com.app.base.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class HomeDataEntity implements Serializable{
	private String userPic ;//用户图像
	private String username;//用户名称
	private String releaseTime ;//发布时间
	private String adPic;//内容图片
	private String title ;//标题
	private String content ;//内容
	private int support_count ;//点赞数
	private int comment_count ;//评论数
	private String details ;//详情
	private String imgURL ;//发布作者图片url
	private String author_img ;
	
	private long author_id ;
	private long article_id ;
	private String author_nick ;
	private String type ;
	
	
	
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
	public String getAuthor_nick() {
		return author_nick;
	}
	public void setAuthor_nick(String author_nick) {
		this.author_nick = author_nick;
	}
	public String getImgURL() {
		return imgURL;
	}
	public void setImgURL(String imgURL) {
		this.imgURL = imgURL;
	}
	public long getAuthor_id() {
		return author_id;
	}
	public void setAuthor_id(long author_id) {
		this.author_id = author_id;
	}
	public long getArticle_id() {
		return article_id;
	}
	public void setArticle_id(long article_id) {
		this.article_id = article_id;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public String getUserPic() {
		return userPic;
	}
	public void setUserPic(String userPic) {
		this.userPic = userPic;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getReleaseTime() {
		return releaseTime;
	}
	public void setReleaseTime(String releaseTime) {
		this.releaseTime = releaseTime;
	}
	public String getAdPic() {
		return adPic;
	}
	public void setAdPic(String adPic) {
		this.adPic = adPic;
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
	public int getSupport_count() {
		return support_count;
	}
	public void setSupport_count(int support_count) {
		this.support_count = support_count;
	}
	public int getComment_count() {
		return comment_count;
	}
	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}
	@Override
	public String toString() {
		return "HomeDataEntity [userPic=" + userPic + ", username=" + username
				+ ", releaseTime=" + releaseTime + ", adPic=" + adPic
				+ ", title=" + title + ", content=" + content
				+ ", support_count=" + support_count + ", comment_count="
				+ comment_count + ", details=" + details + ", imgURL=" + imgURL
				+ ", author_img=" + author_img + ", author_id=" + author_id
				+ ", article_id=" + article_id + ", author_nick=" + author_nick
				+ ", type=" + type + "]";
	}
	
	
}
