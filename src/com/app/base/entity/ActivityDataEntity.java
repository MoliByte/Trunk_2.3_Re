package com.app.base.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ActivityDataEntity implements Serializable ,Comparable<ActivityDataEntity>{

	private String activity_img;// 活动图片
	private String category_name;// 活动类别一级名称
	private String category;// 活动类别二级类别
	private String activity_name;// 活动名称
	private String activity_left_time;// 活动剩余时间
	
	private String participant;// 参与人数
	private String activity_title;// 活动标题
	private String activity_details;// 活动详情
	private int status;// [99:未公布，100：火热进行中，101：活动已结束，等待公布!，102：获奖公布中，103：往期回顾]
	private int joincount;// 参与人数

	private long id;
	private long author_id; // 活动作者id
	private String author_nick; // 活动作者昵称

	private int view_count; // 阅读次数
	private int comment_count;// 评论次数

	private String public_time; // 发布时间
	private String create_time; // 创建时间
	private String end_time;	// 活动结束

	private String type; // post 文章 ; activity 活动
	private String title; // 活动标题

	private String content;// 活动内容

	private int support_count;
	
	private String author_img ;
	
	

	
	public String getAuthor_img() {
		return author_img;
	}

	public void setAuthor_img(String author_img) {
		this.author_img = author_img;
	}

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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getSupport_count() {
		return support_count;
	}

	public void setSupport_count(int support_count) {
		this.support_count = support_count;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getView_count() {
		return view_count;
	}

	public void setView_count(int view_count) {
		this.view_count = view_count;
	}

	public int getComment_count() {
		return comment_count;
	}

	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAuthor_id() {
		return author_id;
	}

	public void setAuthor_id(long author_id) {
		this.author_id = author_id;
	}

	public String getAuthor_nick() {
		return author_nick;
	}

	public void setAuthor_nick(String author_nick) {
		this.author_nick = author_nick;
	}

	public String getActivity_title() {
		return activity_title;
	}

	public void setActivity_title(String activity_title) {
		this.activity_title = activity_title;
	}

	public String getActivity_details() {
		return activity_details;
	}

	public void setActivity_details(String activity_details) {
		this.activity_details = activity_details;
	}

	public String getActivity_img() {
		return activity_img;
	}

	public void setActivity_img(String activity_img) {
		this.activity_img = activity_img;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getActivity_name() {
		return activity_name;
	}

	public void setActivity_name(String activity_name) {
		this.activity_name = activity_name;
	}

	public String getActivity_left_time() {
		return activity_left_time;
	}

	public void setActivity_left_time(String activity_left_time) {
		this.activity_left_time = activity_left_time;
	}

	public String getParticipant() {
		return participant;
	}

	public void setParticipant(String participant) {
		this.participant = participant;
	}

	@Override
	public int compareTo(ActivityDataEntity o) {
		return this.getStatus() - o.getStatus();
	}

	@Override
	public String toString() {
		return "ActivityDataEntity [activity_img=" + activity_img
				+ ", category_name=" + category_name + ", category=" + category
				+ ", activity_name=" + activity_name + ", activity_left_time="
				+ activity_left_time + ", participant=" + participant
				+ ", activity_title=" + activity_title + ", activity_details="
				+ activity_details + ", status=" + status + ", joincount="
				+ joincount + ", id=" + id + ", author_id=" + author_id
				+ ", author_nick=" + author_nick + ", view_count=" + view_count
				+ ", comment_count=" + comment_count + ", public_time="
				+ public_time + ", create_time=" + create_time + ", end_time="
				+ end_time + ", type=" + type + ", title=" + title
				+ ", content=" + content + ", support_count=" + support_count
				+ ", author_img=" + author_img + "]";
	}
	
}
