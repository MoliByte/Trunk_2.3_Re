package com.app.base.entity;

public class CollectEntity {
	private String favorite_id;
	private String uid;
	private String create_time;
	private String data_type;
	private HomeInterface favorite_info;
	
	
	public String getFavorite_id() {
		return favorite_id;
	}
	public void setFavorite_id(String favorite_id) {
		this.favorite_id = favorite_id;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getData_type() {
		return data_type;
	}
	public void setData_type(String data_type) {
		this.data_type = data_type;
	}
	public HomeInterface getFavorite_info() {
		return favorite_info;
	}
	public void setFavorite_info(HomeInterface favorite_info) {
		this.favorite_info = favorite_info;
	}
	
}

/*
 * Reponse:
{
  "code": 200,
  "message": "success",
  "items": [
    {
      "favorite_id": "7",
      "uid": "111",
      "create_time": "2015-03-16 19:40:13",
      "data_type": "facemark",
      "favorite_info": {
        "uid": "14310",
        "nickname": "\"尹晶\"",
        "age": 25,
        "user_avatar": "",
        "skin_type_name": "中性肤质",
        "id": "1",
        "upload_img": "",
        "testbefore1": "0.00",
        "testbefore2": "40.56",
        "testbefore3": "0.00",
        "remark": "刘胜发表的心情啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊",
        "comment_count": 0,
        "praise_count": 0
      }
    },
    {
      "favorite_id": "5",
      "uid": "111",
      "create_time": "2015-03-15 17:59:52",
      "data_type": "skintest",
      "favorite_info": {
        "uid": "24722",
        "nickname": "康乐",
        "age": 30,
        "user_avatar": "",
        "skin_type_name": "中性肤质",
        "id": "10",
        "area": "U",
        "water": "32.63%",
        "oil": "14.63%",
        "elasticity": "23.06%",
        "test_time": "213天前",
        "upload_img": "",
        "comment_count": 0,
        "praise_count": 0
      }
    },
    {
      "favorite_id": "1",
      "uid": "111",
      "create_time": "2015-03-15 16:27:52",
      "data_type": "facemark",
      "favorite_info": {
        "uid": "21307",
        "nickname": "二路",
        "age": 25,
        "user_avatar": "http://napi.skinrun.cn/uploads/000/021/307/source.jpg",
        "skin_type_name": "混合性肤质",
        "id": "2",
        "upload_img": "",
        "testbefore1": "0.00",
        "testbefore2": "0.00",
        "testbefore3": "0.00",
        "remark": "",
        "comment_count": 0,
        "praise_count": 0
      }
    }
  ]
}
 */









/*
5.我的收藏夹

URL:	http://skinrun.renzhi.net/api/interaction
Method:	Post
Body:
{
	"action":"myFavorites",
	"source_type":"facemark",
	"page":1,
	"pagesize":8
}

Reponse:
{
  "code": 200,
  "message": "success",
  "items": [
    {
      "id": "1",
      "source_type": "facemark",
      "source_id": "2",
      "uid": "111",
      "title": "标题",
      "create_time": "2015-03-15 16:27:52"
    }
  ]
}
*/