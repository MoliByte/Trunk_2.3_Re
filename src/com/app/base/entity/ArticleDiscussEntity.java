package com.app.base.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 文章评论
 * @author zhup
 * "id": 1,
    "author_id": 8070,
    "author_nick": "笑",
    "title": "测试文章",
    "content": "测试文章",
    "view_count": 0,
    "comment_count": 4,
    "public_time": "0000-00-00 00:00:00",
    "create_time": "0000-00-00 00:00:00",
 */
@SuppressWarnings("serial")
public class ArticleDiscussEntity implements Serializable {
	private long id ;
	private long author_id ;
	private String author_nick ;
	private String imgURL ;
	private String title ;
	private String content ;
	private int view_count ;
	private String public_time;
	private String create_time;
	private int comment_count; // 评论数目
	
	private int praise_status ; //当前用户是否对该活动或者文章点赞
	private int praise_count ;//总的点赞次数
	
	private ArrayList<ArticleCommentEntity> commentList ;
	private Map<Integer,String> join_users;//参与人数 ;
	
	
	public Map<Integer, String> getJoin_users() {
		if(join_users == null){
			return new HashMap<Integer,String>(0);
		}
		return join_users;
	}
	public void setJoin_users(Map<Integer, String> join_users) {
		this.join_users = join_users;
	}
	public int getPraise_status() {
		return praise_status;
	}
	public void setPraise_status(int praise_status) {
		this.praise_status = praise_status;
	}
	public int getPraise_count() {
		return praise_count;
	}
	public void setPraise_count(int praise_count) {
		this.praise_count = praise_count;
	}
	public String getImgURL() {
		return imgURL;
	}
	public void setImgURL(String imgURL) {
		this.imgURL = imgURL;
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
	public int getView_count() {
		return view_count;
	}
	public void setView_count(int view_count) {
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

	public ArrayList<ArticleCommentEntity> getCommentList() {
		if(commentList == null){
			this.commentList = new ArrayList<ArticleCommentEntity>();
		}
		return commentList;
	}

	public void setCommentList(ArrayList<ArticleCommentEntity> commentList) {
		this.commentList = commentList;
	}
	
	

}
