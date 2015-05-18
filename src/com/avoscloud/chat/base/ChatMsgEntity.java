package com.avoscloud.chat.base;

import com.avoscloud.chat.entity.Msg;

/**
 * 
 ****************************************** 
 * @文件名称 : ChatMsgEntity.java
 * @创建时间 : 2013-1-27 下午02:33:33
 * @文件描述 : 消息实体
 ****************************************** 
 */
public class ChatMsgEntity implements Comparable<ChatMsgEntity>{

	private String name;

	private String uid;

	private String date;

	private String text;
	private String localpath;

	private Msg.Type type;// [Text,Image]

	private String status;// 发送成功状态

	private boolean isComMeg = true;
	
	private String avatar ;
	

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public boolean isComMeg() {
		return isComMeg;
	}

	public void setComMeg(boolean isComMeg) {
		this.isComMeg = isComMeg;
	}

	public Msg.Type getType() {
		return type;
	}

	public void setType(Msg.Type type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public String getLocalpath() {
		return localpath;
	}

	public void setLocalpath(String localpath) {
		this.localpath = localpath;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean getMsgType() {
		return isComMeg;
	}

	public void setMsgType(boolean isComMsg) {
		isComMeg = isComMsg;
	}

	public ChatMsgEntity() {
	}

	public ChatMsgEntity(String name, String date, String text, Msg.Type type,
			String status, boolean isComMsg) {
		super();
		this.name = name;
		this.date = date;
		this.text = text;
		this.type = type;
		this.status = status;
		this.isComMeg = isComMsg;
	}
	
	@Override
	public int compareTo(ChatMsgEntity o) {
		return this.date.compareTo(o.getDate());
	}

}
