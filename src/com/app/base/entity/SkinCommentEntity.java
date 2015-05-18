package com.app.base.entity;

import org.kymjs.aframe.database.annotate.Id;
import org.kymjs.aframe.database.annotate.Property;
import org.kymjs.aframe.database.annotate.Table;

@Table(name = "localSkinComment")
public class SkinCommentEntity {
	@Id(column="id")
	private int id;
	@Property(column = "age_factor")
	private int age_factor;
	@Property(column = "comment")
	private String comment;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAge_factor() {
		return age_factor;
	}
	public void setAge_factor(int age_factor) {
		this.age_factor = age_factor;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
}
