package com.app.base.entity;


public class ParentCommentEntity {
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
	//private ArrayList<ChildCommentEntity> children_comment;
	
	
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
	/*public ArrayList<ChildCommentEntity> getChildren_comment() {
		return children_comment;
	}
	public void setChildren_comment(ArrayList<ChildCommentEntity> children_comment) {
		this.children_comment = children_comment;
	}*/
	
}
