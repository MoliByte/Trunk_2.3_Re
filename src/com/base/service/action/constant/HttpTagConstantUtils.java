package com.base.service.action.constant;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Action标签定义 定义唯一的发送请求的动作
 * 
 * @author zhup
 * 
 */
public class HttpTagConstantUtils {
	
	public static final String UUID = "7d5e49a" ;

	public static final int REGISTER_TYPE = 1;					// 注册验证码
	public static final int RESET_PASSWORD_TYPE = 2;			// 重新设置密码
	public static final String[] SKIN_ARRAY = { "干性肤质", "油性肤质", "中性肤质",
			"混合性肤质", "敏感性肤质" };
	// 皮肤类型常量
	public static final String SKIN_TYPE = "[{\"id\":1,\"name\":\"干性肤质\"},{\"id\":2,\"name\":\"油性肤质\"},{\"id\":3,\"name\":\"中性肤质\"},{\"id\":4,\"name\":\"混合性肤质\"},{\"id\":5,\"name\":\"敏感性肤质\"}]";
	public static final int THIRD_LOGIN_TAG = 0x6; 				// 第三方登陆标签
	public static final int LOGIN_TAG = 0x11; 					// 登陆标签
	public static final int HOME_DATA_TAG = 0x12; 				// 首页数据获取
	public static final int HOME_DATA_AD_TAG = 0x13; 			// 首页广告获取
	public static final int ACTIVITY_DATA_TAG = 0x14; 			// 活动数据获取
	public static final int MY_MASTER_TAG = 0x15; 				// 获取我的导师action
	public static final int MASTER_ARTILE = 0x16; 				// 获取导师列表
	public static final int USER_INFO = 0x17; 					// 获取用户信息
	public static final int USER_PIC_UPDATE = 0x18; 			// 用户头像更改
	public static final int UPDATE_NICK_NAME = 0x19; 			// 用户昵称更改
	public static final int UPDATE_USER_INFO = 0x20; 			// 更新用户信息
	public static final int SESSION_DELETE = 0x21; 				// 注销用户
	public static final int MOD_PASSWORD = 0x22; 				// 修改密码
	public static final int VERIFY_CODE = 0x24; 				// 获取手机验证码
	public static final int REGISTER_SUBMIT = 0x23; 			// 提交注册
	public static final int MY_MESSAGE = 0x24; 					// 获取我的消息
	public static final int COIN_RECORD = 0x25; 				// 我的金币记录
	public static final int RESET_PWD = 0x26; 					// 重新设置密码
	public static final int CHECK_CODE = 0x27; 					// 校验验证码

	public static final int ARTICLE_LIST = 0x28;				// 文章列表
	public static final int ACTION_DETAIL = 0x29; 				// 文章活动详情
	public static final int ARTICLE_DETAIL = 0x30; 				// 文章详情
	public static final int POST_COMMENT = 0x31; 				// 发表文章评论
	public static final int POST_PRAISE = 0x32; 				// 文章点赞
	public static final int POST_COMMENT_PRAISE = 0x33; 		// 评论点赞
	
	public static final int TOPIC = 0x34 ;						// 晚九点话题
	
	public static final int POST_COMMENT_REPLAY = 0x35; 		// 评论评论
	public static final int POST_ACTION_COMMENT = 0x36; 		// 发表活动评论
	public static final int POST_PRAISE_ACTION = 0x37; 			// 活动点赞
	public static final int POST_COMMENT_PRAISE_ACTION = 0x38; 	// 活动评论点赞
	
	public static final int PRAISE_CANCEL = 0x60; 				// 取消赞
	
	public static final int TEST_RECORD = 0x39; 				// 获取测试信息
	
	public static final int MESSAGE_RECORD = 0x40; 				// 个人消息记录
	
	public static final int MESSAGE_DETAIL = 0x41; 				// 个人消息记录
	
	public static final int FEED_BACK = 0x42; 					// 意见反馈
	
	public static final int ADVERT = 0x43; 						// 广告
	
	public static final int HOME = 0x48; 						// 首页
	
	public static final int GET_ADDRESS = 0x50; 				// 获取收货地址
	public static final int PUT_ADDRESS = 0x51; 				// 更新收货地址
	
	public static final int CREDIT_LOG = 0x52; 					// 积分日志
	
	public static final int DEL_HUANXIN_USER = 0x58; 			// 环信删除用户
	public static final int CREATE_HUANXIN_USER = 0x59; 		// 环信创建用户
	
	public static final int POST_TEST_RECORD=0x68;              //提交测试数据
	public static final int GET_TEST_RECORD=0x69;				//获取测试数据
	public static final int GET_WEATHER=0x79;					//获取测试数据
	public static final int GET_COMPARA_DATA=0x82;				//获取测试数据
	
	public static final int GET_SKIN_SCORE=0x84;				//获取测试数据
	
	public static final int GET_LAST_TEST_DATA=0x86;            //获取最近测试数据
	
	
	public static final int VISITOR = 0x90;            			//晚九点活动访客注册
	public static final int UID_REG=0x95;            			//晚九点UID注册
	
	public static final int ACTION_COMMENT_LIST = 0x100; 		// 活动评论列表
	public static final int ARTICLE_COMMENT_LIST = 0x101; 		// 专题评论列表
	
	public static final int SHARE_JIFEN = 0x102; 				// 用户分享积分
	
	public static final int PRODUCT_NAME=0x112;                 //产品名称搜索
	
	public static final int UPLOAD_IMAGE=0x122;                 //上传产品图片
	
	public static final int UPLOAD_FACIAL_TESTNUM=0x123;        //上传面膜测试记录
	
	public static final int GET_COMPLETE_PRODUCT=0x128;        //得到完整产品
	
	public static final int GET_FACIALMASK_ADVICE=0x132;        //获取面膜测试建议
	
	public static final int GET_SPORT_ADVICE=0x132;        //深度报告中的运动建议及饮食建议
	
	public static final int PUBLISH_MOOD=0x135;			//面膜测试中发表心情
	
	public static final int POST_SELECTED=0x143;		//首页精选
	
	public static final int POST_DONGTAI=0x144;			//首页动态
	
	public static final int POST_HOME_PRAISE=0x156;     //首页点赞
	
	public static final int POST_HOME_COMMENT=0x157;     //首页评论
	
	public static final int POST_HOME_COLLECT=0x158;     //首页收藏
	
	public static final int GET_HOME_COLLECT=0x159;		//得到首页收藏
	
	public static final int GET_HOME_COMMENT=0x160;		//得到首页评论
	
	public static final int GET_SELECT_COMMENT=0x161;    //得到精选评论  
	
	public static final int GET_MY_SKIN_TEST=0x162;		//我的肌肤测试
	
	public static final int GET_MY_MASK_TEST=0x163;		//我的肌肤测试
	
	public static final int DELETE_MY_COLLECT=0x164;		//删除我的收藏
	
	public static final int GET_PRAISE_JOINERS=0x165;		//获取点赞人头像
	
	public static final int GET_SELECTED_DETAIL=0x166;		//获取精选页面详情
	
	public static final int POST_TDCODE=0x167;				//发上传二维码信息
	
	public static final int POST_MSG=0x169;				//发送晚九点消息
	
	public static final int GET_MY_LOVED_MERCHANT=0x172;	//获取我关注的商家名单
	
	public static final int GET_MERCHANT_PRODUCT=0x175; //获取我关注的商家发布的产品
	
	public static final int GET_MERCHANT_DETAIL=0x178; //获取我关注的商家详情
	
	public static final int GET_MY_LOVED_BRAND=0x179; //获取我关注的品牌
	
	public static final int GET_MY_LOVED_PRODUCT=0x182; //获取我关注的品牌的产品
	
	public static final int UPLOAD_SKIN_IMAGE=0x185; //上传肌肤图片
	
	public static final int PUBLISH_SKIN_MOOD=0x195; //发表肌肤测试心情
	
	public static final int CANCLE_ATTENTION_MERCHANT=0x196; //取消关注商家
	
	public static final int DELETE_MY_MESSAGE=0x197; //删除我的消息
	
	public static final int GET_MINE_MESSAGE=0x199; //得到我的消息
	
	public static final int GET_UNREAD_TEST_MESSAGE=0x201; //得到我的未读测试消息
	
	public static final int GET_MY_TEST_MESSAGE=0x201; //得到我的测试消息
	
	public static final int SKIN_GO_TO_HOME=0x210; //肌肤测试上首页
	
	public static final int MY_INTEGRAL_RECORD=0x214; //我的积分变动
	
	public static final int FEEDBACK_GET = 0x219; //获取反馈内容
	
	public static final int FEEDBACK_POST = 0x220; //发布反馈内容
	
	
	public static final int PM9_HISTORY = 0x225; //PM9 History
	
	public static final int JUDGE_USERINFO = 0x235; //判断用户资料是否完善
	
	public static final int GET_MESSAGE_NOCOUNT = 0x236; //获取未读我的消息条数
	
	public static final int CLEAR_MESSAGE_NOCOUNT = 0x238; //清除未读我的消息条数
	
	
	public static String getSkinTypeByKey(String key) {
		String skinType = "混合性肤质";// default
		try {
			if (null == key || "".equals(key)) {
				return skinType;
			}
			JSONArray skinTypeJson = new JSONArray(SKIN_TYPE);
			for (int i = 0; i < skinTypeJson.length(); i++) {
				JSONObject obj = skinTypeJson.getJSONObject(i);
				if (obj.optInt("id") == Integer.valueOf(key)) {
					skinType = obj.optString("name");
					break;
				}
			}
			return skinType;
		} catch (Exception e) {
			return skinType;
		}
	}

	public static int getIndexByName(String name) {
		int index = 1;
		try {
			if (null == name || "".equals(name)) {
				return index;
			}
			JSONArray skinTypeJson = new JSONArray(SKIN_TYPE);
			for (int i = 0; i < skinTypeJson.length(); i++) {
				JSONObject obj = skinTypeJson.getJSONObject(i);
				if (obj.optString("name").equals(name)) {
					index = i;
					break;
				}
			}
			return index;
		} catch (Exception e) {
			return index;
		}

	}
	
}
