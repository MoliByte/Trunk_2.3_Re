package com.app.base.entity;

public class UnReadMessageEntity {
	private int unread;
	private String last_uid;
	private String last_user_avatar;
	
	
	public String getLast_uid() {
		return last_uid;
	}

	public void setLast_uid(String last_uid) {
		this.last_uid = last_uid;
	}

	public String getLast_user_avatar() {
		return last_user_avatar;
	}

	public void setLast_user_avatar(String last_user_avatar) {
		this.last_user_avatar = last_user_avatar;
	}

	public int getUnread() {
		return unread;
	}

	public void setUnread(int unread) {
		this.unread = unread;
	}
}
