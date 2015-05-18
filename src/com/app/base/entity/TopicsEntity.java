package com.app.base.entity;

import java.io.Serializable;
import java.util.ArrayList;
/**
"id": 1,
"title": "tesss",
"content": "ttttt",
"img_url": "aaaaa",
"open_date": "2015-01-01",
"start_time": "",
"end_time": "2015-01-01",
"create_time": "2015-01-22 14:40:32",
"create_uid": 8070,
"create_user": "ttt",
"lastup_time": "0000-00-00 00:00:00",
"lastup_uid": 0,
"lastup_user": ""
*/
public class TopicsEntity implements Serializable{
	private long id ;
	private String title ;
	private String content ;
	private String img_url ;
	private String open_date ;
	private String start_time ;
	private String end_time ;
	private String create_time ;
	private long create_uid ;
	private String create_user ;
	private String lastup_time ;
	private long lastup_uid ;
	private String lastup_user ;
	private ArrayList<UserEntity> prize_user ;//获奖用户名单
	
	
	public ArrayList<UserEntity> getPrize_user() {
		return prize_user;
	}
	public void setPrize_user(ArrayList<UserEntity> prize_user) {
		this.prize_user = prize_user;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	public String getImg_url() {
		return img_url;
	}
	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}
	public String getOpen_date() {
		return open_date;
	}
	public void setOpen_date(String open_date) {
		this.open_date = open_date;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public long getCreate_uid() {
		return create_uid;
	}
	public void setCreate_uid(long create_uid) {
		this.create_uid = create_uid;
	}
	public String getCreate_user() {
		return create_user;
	}
	public void setCreate_user(String create_user) {
		this.create_user = create_user;
	}
	public String getLastup_time() {
		return lastup_time;
	}
	public void setLastup_time(String lastup_time) {
		this.lastup_time = lastup_time;
	}
	public long getLastup_uid() {
		return lastup_uid;
	}
	public void setLastup_uid(long lastup_uid) {
		this.lastup_uid = lastup_uid;
	}
	public String getLastup_user() {
		return lastup_user;
	}
	public void setLastup_user(String lastup_user) {
		this.lastup_user = lastup_user;
	}
}
