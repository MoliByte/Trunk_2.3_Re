package com.app.base.entity;

import java.io.Serializable;

/**
 * 文章二级评论回复实例
 * @author zhup
 * 
"id": 10,
"post_id": 1,
"parent_id": 2,
"to_uid": 8070,
"to_nick": "ghhh",
"from_uid": 8070,
"from_nick": "ghhh",
"content": "aaaa",
"comment_count": 0,
"create_time": "2014-12-30 14:16:11"
 * 
 */
@SuppressWarnings("serial")
public class ActionReplayEntity implements Serializable {
	
	private long id ;
	private long post_id; 
	private long parent_id; 
	private long to_uid;
	private long from_uid;
	private String to_nick;
	private String from_nick;
	private String content;
	private int comment_count;
	private String create_time;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getPost_id() {
		return post_id;
	}
	public void setPost_id(long post_id) {
		this.post_id = post_id;
	}
	public long getParent_id() {
		return parent_id;
	}
	public void setParent_id(long parent_id) {
		this.parent_id = parent_id;
	}
	public long getTo_uid() {
		return to_uid;
	}
	public void setTo_uid(long to_uid) {
		this.to_uid = to_uid;
	}
	public long getFrom_uid() {
		return from_uid;
	}
	public void setFrom_uid(long from_uid) {
		this.from_uid = from_uid;
	}
	public String getTo_nick() {
		return to_nick;
	}
	public void setTo_nick(String to_nick) {
		this.to_nick = to_nick;
	}
	public String getFrom_nick() {
		return from_nick;
	}
	public void setFrom_nick(String from_nick) {
		this.from_nick = from_nick;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getComment_count() {
		return comment_count;
	}
	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	
}
