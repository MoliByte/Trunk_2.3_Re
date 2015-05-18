package com.app.base.entity;

import org.kymjs.aframe.database.annotate.Id;
import org.kymjs.aframe.database.annotate.Property;
import org.kymjs.aframe.database.annotate.Table;


@Table(name = "PushEntity")
public class PushEntity {
	@Id(column="token")
	private String token;
	@Property(column = "myMessage")
	private int myMessage;
	@Property(column = "myTestMessage")
	private int myTestMessage;
	
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public int getMyMessage() {
		return myMessage;
	}
	public void setMyMessage(int myMessage) {
		this.myMessage = myMessage;
	}
	public int getMyTestMessage() {
		return myTestMessage;
	}
	public void setMyTestMessage(int myTestMessage) {
		this.myTestMessage = myTestMessage;
	}
}
