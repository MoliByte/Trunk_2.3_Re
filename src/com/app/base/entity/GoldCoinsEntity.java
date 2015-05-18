package com.app.base.entity;

/**
 * 金币记录
 * @author zhupei
 *
 */
public class GoldCoinsEntity {
	private String id;
	private String credit;
	private String create_time;
	private String type;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCredit() {
		return credit;
	}
	public void setCredit(String credit) {
		this.credit = credit;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
/* "id": "77579",
      "credit": "+2",
      "create_time": "2015-04-18 10:53:58",
      "type": "评论话题"
*/