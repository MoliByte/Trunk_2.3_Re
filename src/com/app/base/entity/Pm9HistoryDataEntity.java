package com.app.base.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;
/**
 * pm9 往期回顾
 * @author zhupei
 *
 */
@SuppressWarnings("serial")
public class Pm9HistoryDataEntity implements Serializable /*,Comparable<SuggestionFeedBackDataEntity>*/{
	@SerializedName("id")
	private int id ; //pm9 id
	@SerializedName("logo")
	private String nine_clock_logo;// 图片
	@SerializedName("time")
	private String pm9_time  ;//日期
	@SerializedName("paraticipant")
	private String participant ;//参与人数
	@SerializedName("prize")
	private String prize ; //奖品
	@SerializedName("prizewinner")
	private String awarded_list ; //获奖名单
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNine_clock_logo() {
		return nine_clock_logo;
	}
	public void setNine_clock_logo(String nine_clock_logo) {
		this.nine_clock_logo = nine_clock_logo;
	}
	public String getPm9_time() {
		return pm9_time;
	}
	public void setPm9_time(String pm9_time) {
		this.pm9_time = pm9_time;
	}
	public String getParticipant() {
		return participant;
	}
	public void setParticipant(String participant) {
		this.participant = participant;
	}
	public String getPrize() {
		return prize;
	}
	public void setPrize(String prize) {
		this.prize = prize;
	}
	public String getAwarded_list() {
		return awarded_list;
	}
	public void setAwarded_list(String awarded_list) {
		this.awarded_list = awarded_list;
	}
}
