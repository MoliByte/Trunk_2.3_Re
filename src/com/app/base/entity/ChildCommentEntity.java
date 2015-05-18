package com.app.base.entity;

public class ChildCommentEntity {
	private String id;
	private String source_type;
	private String source_id;
	private String parent_id;
	private String uid;
	private String nickname;
	private String content;
	private String create_time;
	private int praise_count;
	private int bad_count;
	private String drop_status;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSource_type() {
		return source_type;
	}
	public void setSource_type(String source_type) {
		this.source_type = source_type;
	}
	public String getSource_id() {
		return source_id;
	}
	public void setSource_id(String source_id) {
		this.source_id = source_id;
	}
	public String getParent_id() {
		return parent_id;
	}
	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public int getPraise_count() {
		return praise_count;
	}
	public void setPraise_count(int praise_count) {
		this.praise_count = praise_count;
	}
	public int getBad_count() {
		return bad_count;
	}
	public void setBad_count(int bad_count) {
		this.bad_count = bad_count;
	}
	public String getDrop_status() {
		return drop_status;
	}
	public void setDrop_status(String drop_status) {
		this.drop_status = drop_status;
	}
	
}
/*
{
          "id": "2",
          "source_type": "facemark",
          "source_id": "1",
          "parent_id": "1",
          "uid": "222",
          "nickname": "xu",
          "content": "测试回复",
          "create_time": "1426393389",
          "praise_count": "0",
          "bad_count": "0",
          "drop_status": "0"
        }
*/