package com.app.base.entity;

import java.io.Serializable;

import org.kymjs.aframe.database.annotate.Id;
import org.kymjs.aframe.database.annotate.Property;
import org.kymjs.aframe.database.annotate.Table;

@Table (name="FacialComparaRecord")
public class FacialComparaEntity implements Serializable {
	@Id(column="token")
	private String token;
	@Property(column="id")
	private int id;
	@Property(column="sid")
	private int sid;
	@Property(column="joinnum")
	private int joinnum;
	@Property(column="testafter")
	private double testafter;
	@Property(column="testbefore1")
	private double testbefore1;
	@Property(column="testbefore2")
	private double testbefore2;
	@Property(column="testbefore3")
	private double testbefore3;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
	public int getJoinnum() {
		return joinnum;
	}
	public void setJoinnum(int joinnum) {
		this.joinnum = joinnum;
	}
	public double getTestafter() {
		return testafter;
	}
	public void setTestafter(double testafter) {
		this.testafter = testafter;
	}
	public double getTestbefore1() {
		return testbefore1;
	}
	public void setTestbefore1(double testbefore1) {
		this.testbefore1 = testbefore1;
	}
	public double getTestbefore2() {
		return testbefore2;
	}
	public void setTestbefore2(double testbefore2) {
		this.testbefore2 = testbefore2;
	}
	public double getTestbefore3() {
		return testbefore3;
	}
	public void setTestbefore3(double testbefore3) {
		this.testbefore3 = testbefore3;
	}
}





//{"message":"success","code":200,"items":{"id":"29","sid":308,"joinnum":"7","testafter":"40.56","testbefore1":"48.56","testbefore2":"0.00","testbefore3":"0.00"}}












