package com.avoscloud.chat.entity;

import java.io.Serializable;

import org.kymjs.aframe.database.annotate.Id;
import org.kymjs.aframe.database.annotate.Property;
import org.kymjs.aframe.database.annotate.Table;

@Table(name = "chat_message")
public class Pm9Message implements Serializable {
	@Id(column = "id")
	private int id;// message

	@Property(column = "objectId")
	private String objectId;

	@Property(column = "message_time")
	private long message_time;

	@Property(column = "uid")
	private String uid;

	@Property(column = "nickname")
	private String nickname;

	@Property(column = "avatar")
	private String avatar;// 图片路径

	@Property(column = "name")
	private String name;

	@Property(column = "date")
	private String date;

	@Property(column = "text")
	private String text;
	
	@Property(column = "localPath")
	private String localPath;
	
	@Property(column = "remotePath")
	private String remotePath;

	@Property(column = "type")
	private Msg.Type type;// [Text,Image]
	
	@Property(column = "msg_type")
	private int msg_type;// [0:Text,1:Image]

	@Property(column = "status")
	private String status;// 发送成功状态

	@Property(column = "isComMeg")
	private boolean isComMeg ;
	
	@Property(column = "groupId")
	private String groupId ;
	
	
	@Property(column = "userId")
	private String userId ;
	
	
	
	
	public int getMsg_type() {
		return msg_type;
	}

	public void setMsg_type(int msg_type) {
		this.msg_type = msg_type;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public long getMessage_time() {
		return message_time;
	}

	public void setMessage_time(long message_time) {
		this.message_time = message_time;
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

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
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

	public void setDate(String date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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

	public boolean isComMeg() {
		return isComMeg;
	}

	public void setComMeg(boolean isComMeg) {
		this.isComMeg = isComMeg;
	}
	
	
}
