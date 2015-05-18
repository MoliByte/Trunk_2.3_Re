package com.app.base.entity;

import java.io.Serializable;

import org.kymjs.aframe.database.annotate.Id;
import org.kymjs.aframe.database.annotate.Property;
import org.kymjs.aframe.database.annotate.Table;

import com.google.gson.annotations.SerializedName;
/*
{
  "avatar": "http://napi.skinrun.cn/uploads/000/025/340/source.jpg",
  "birthday": "1990-01-01 00:00:00",
  "email": "",
  "gender": 2,
  "hasMerchant": 0,
  "integral": 358,
  "member_id": 0,
  "mobile": "15006181787",
  "niakname": "哈哈哈哈哈哈哈哈哈哈哈哈哈",
  "realname": "",
  "region": "310108",
  "regionNames": "",
  "skinType": 4,
  "username": "15006181787"
}
*/
@Table (name="user_info")
public class UserEntity implements Serializable{
	@SerializedName("id")
	@Id(column="id")
	private int id ;
	
	@Property(column="token")
	private String token ;
	
	@Property(column="username")
	@SerializedName("usename") 
	private String username ;
	
	@SerializedName("email")
	@Property(column="email")
	private String email ;
	
	@SerializedName("mobile")
	@Property(column="mobile")
	private String mobile ;
	
	@SerializedName("niakname")
	@Property(column="niakname")
	private String niakname ;
	
	@SerializedName("realname")
	@Property(column="realname")
	private String realname ;
	
	@SerializedName("region")
	@Property(column="region")
	private String region ;
	
	@SerializedName("regionNames")
	@Property(column="regionNames")
	private String regionNames ;
	
	@SerializedName("birthday")
	@Property(column="birthday")
	private String birthday ;
	
	@SerializedName("skinType")
	@Property(column="skinType")
	private String skinType ;
	
	@SerializedName("hasMerchant")
	@Property(column="hasMerchant")
	private String hasMerchant ;
	
	@SerializedName("avatar")
	@Property(column="avatar")
	private String avatar ;//图片路径
	
	@SerializedName("gender")
	@Property(column="gender")
	private String gender ;
	
	@SerializedName("member_id")
	@Property(column="member_id")
	private String member_id ;
	
	@Property(column="u_id")
	private int u_id ;
	
	@SerializedName("integral")
	@Property(column="integral")
	private int integral ;
	
	@Property(column="uuid")
	private String uuid ;
	
	
	public int getIntegral() {
		return integral;
	}
	public void setIntegral(int integral) {
		this.integral = integral;
	}
	public int getU_id() {
		return u_id;
	}
	public void setU_id(int u_id) {
		this.u_id = u_id;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getNiakname() {
		return niakname;
	}
	public void setNiakname(String niakname) {
		this.niakname = niakname;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getRegionNames() {
		return regionNames;
	}
	public void setRegionNames(String regionNames) {
		this.regionNames = regionNames;
	}
	public String getSkinType() {
		return skinType;
	}
	public void setSkinType(String skinType) {
		this.skinType = skinType;
	}
	public String getHasMerchant() {
		return hasMerchant;
	}
	public void setHasMerchant(String hasMerchant) {
		this.hasMerchant = hasMerchant;
	}
	@Override
	public String toString() {
		
		return "avatar = "+this.avatar +
				"\tbirthday = "+this.birthday +
				"\temail = "+this.email +
				"\tgender = "+this.gender +
				"\thasMerchant = "+this.hasMerchant +
				"\tid = "+this.id +
				"\tmember_id = "+this.member_id +
				"\tmobile = "+this.mobile +
				"\tniakname = "+this.niakname +
				"\t\nrealname = "+this.realname +
				"\tregion = "+this.region + 
				"\tregionNames = "+this.regionNames  +
				"\tskinType = "+this.skinType + 
				"\ttoken = "+this.token + 
				"\tusername = "+this.username +
				"\tuuid = "+this.uuid  +
				"\tintegral" +  this.integral
				;
	}
	
	
}
