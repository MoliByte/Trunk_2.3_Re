package com.app.base.entity;

public class TestNewsEntity {
	private String test_type;
	private String from_uid;
	private String from_nickname;
	private String from_avatar;
	
	private String msg_type;
	private String test_id;
	private String create_time;
	private String remark;
	
	private String upload_img;
	private String content;
	
	private String area;
	private String water;
	private String oil;
	private String elasticity;
	
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getWater() {
		return water;
	}
	public void setWater(String water) {
		this.water = water;
	}
	public String getOil() {
		return oil;
	}
	public void setOil(String oil) {
		this.oil = oil;
	}
	public String getElasticity() {
		return elasticity;
	}
	public void setElasticity(String elasticity) {
		this.elasticity = elasticity;
	}
	public String getTest_type() {
		return test_type;
	}
	public void setTest_type(String test_type) {
		this.test_type = test_type;
	}
	public String getFrom_uid() {
		return from_uid;
	}
	public void setFrom_uid(String from_uid) {
		this.from_uid = from_uid;
	}
	public String getFrom_nickname() {
		return from_nickname;
	}
	public void setFrom_nickname(String from_nickname) {
		this.from_nickname = from_nickname;
	}
	public String getFrom_avatar() {
		return from_avatar;
	}
	public void setFrom_avatar(String from_avatar) {
		this.from_avatar = from_avatar;
	}
	public String getMsg_type() {
		return msg_type;
	}
	public void setMsg_type(String msg_type) {
		this.msg_type = msg_type;
	}
	public String getTest_id() {
		return test_id;
	}
	public void setTest_id(String test_id) {
		this.test_id = test_id;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getUpload_img() {
		return upload_img;
	}
	public void setUpload_img(String upload_img) {
		this.upload_img = upload_img;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
/*
 * "test_type": "skintest",
      "from_uid": "111",
      "from_nickname": "",
      "from_avatar": "",
      "msg_type": "praises",
      "test_id": "1",
      "create_time": "26天前",
      "area": "U",
      "water": "54.29%",
      "oil": "24.39%",
      "elasticity": "37.94%",
      "content": ""
 * 
 * 
 *
 "test_type": "facemark",
      "from_uid": "25340",
      "from_nickname": "Lenny",
      "from_avatar": "http://napi.skinrun.cn/uploads/000/025/340/source.jpg",
      "msg_type": "praises",
      "test_id": "8",
      "create_time": "4天前",
      "remark": "",
      "upload_img": "",
      "content": ""
*/