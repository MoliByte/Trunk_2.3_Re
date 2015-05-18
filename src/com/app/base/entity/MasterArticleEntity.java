package com.app.base.entity;

import java.io.Serializable;
/**
 * 导师文章信息
 * @author zhup
 *
 */
@SuppressWarnings("serial")
public class MasterArticleEntity implements Serializable{
	private String title ;//文章标题
	private String release_time;//文章发布时间
	private String pic_url;//文章内容图片
	private String content ;//文章内容
	private int support_count ;//点赞数
	private int comment_count ;//评论数
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRelease_time() {
		return release_time;
	}
	public void setRelease_time(String release_time) {
		this.release_time = release_time;
	}
	public String getPic_url() {
		return pic_url;
	}
	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
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
}
