package com.app.base.entity;

import java.io.Serializable;
/**
 * 我的导师
 * @author zhup
 *
 */
@SuppressWarnings("serial")
public class MyMasterEntity implements Serializable{
	private String master_name ;//导师名称
	private String master_pic;//导师图像
	private String master_level;//导师级别
	private String master_organization;//导师机构
	public String getMaster_name() {
		return master_name;
	}
	public void setMaster_name(String master_name) {
		this.master_name = master_name;
	}
	public String getMaster_pic() {
		return master_pic;
	}
	public void setMaster_pic(String master_pic) {
		this.master_pic = master_pic;
	}
	public String getMaster_level() {
		return master_level;
	}
	public void setMaster_level(String master_level) {
		this.master_level = master_level;
	}
	public String getMaster_organization() {
		return master_organization;
	}
	public void setMaster_organization(String master_organization) {
		this.master_organization = master_organization;
	}
}
