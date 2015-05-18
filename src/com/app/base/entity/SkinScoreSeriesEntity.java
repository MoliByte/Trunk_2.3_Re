package com.app.base.entity;

import java.util.ArrayList;

public class SkinScoreSeriesEntity {
	private ArrayList<SkinScoreEntity> u_last7days;
	private  ArrayList<SkinScoreEntity> st_last7days;
	public ArrayList<SkinScoreEntity> getU_last7days() {
		return u_last7days;
	}
	public void setU_last7days(ArrayList<SkinScoreEntity> u_last7days) {
		this.u_last7days = u_last7days;
	}
	public ArrayList<SkinScoreEntity> getSt_last7days() {
		return st_last7days;
	}
	public void setSt_last7days(ArrayList<SkinScoreEntity> st_last7days) {
		this.st_last7days = st_last7days;
	}
}
