package com.avoscloud.chat.service.receiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.app.base.init.ACache;
import com.app.base.init.MyApplication;
import com.app.service.PostMsgPm9AsynService;
import com.avos.avoscloud.AVGroupMessageReceiver;
import com.avos.avoscloud.AVMessage;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.Group;
import com.avoscloud.chat.entity.Msg;
import com.avoscloud.chat.entity.Pm9Message;
import com.avoscloud.chat.service.listener.GroupEventListener;
import com.avoscloud.chat.service.listener.MsgListener;
import com.avoscloud.chat.service.listener.MsgReceiverListener;
import com.base.app.utils.DBService;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;

/**
 * 接受与发送消息处理
 * 
 * @author zhupei
 * 
 */
public class GroupMessageReceiver extends AVGroupMessageReceiver implements HttpAysnResultInterface {
	public static GroupEventListener groupListeners ;//= new HashSet<GroupEventListener>();
	public static MsgListener msgListeners ;// = new HashSet<MsgListener>();
	public static MsgReceiverListener msgReceiverListener;
	public static String TAG = "DemoGroupMessageReceiver";
	private static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	
	private static StringBuffer msg_Object_id = new StringBuffer("") ;

	@Override
	public void onJoined(Context context, Group group) {
		// 在这里来处理加入成功以后的回调
		Log.e(TAG, "加入群组：" + group.getGroupId() + " - 成功.");
		/*for (GroupEventListener listener : groupListeners) {
			if(listener != null){
				listener.onJoined(group);
			}
		}*/
		if(groupListeners != null){
			groupListeners.onJoined(group);
		}
	}

	@Override
	public void onError(Context context, Group group, Throwable error) {
		Log.e(TAG,
				"加入群组：" + group.getGroupId() + " error by ." + error.toString());
		/*for (GroupEventListener listener : groupListeners) {
			if(listener != null){
				listener.onError(group);
			}
		}*/
		groupListeners.onError(group);
	}

	@Override
	public void onInviteToGroup(Context arg0, Group arg1, String arg2) {

	}

	@Override
	public void onInvited(Context arg0, Group arg1, List<String> arg2) {

	}

	@Override
	public void onKicked(Context arg0, Group arg1, List<String> arg2) {

	}

	@Override
	public void onMemberJoin(Context context, Group group, List<String> arg2) {
		Log.e(TAG, "新成员加入：" + group.getGroupId());
	}

	@Override
	public void onMemberLeft(Context arg0, Group arg1, List<String> arg2) {

	}

	@Override
	public void onMessage(Context context, Group group, AVMessage message) {
		Log.e(TAG,
				"接受群组消息：" + message.getMessage() + " to group "
						+ group.getGroupId() + " receiver ");
		try {
			JSONObject obj = new JSONObject(message.getMessage()) ;
			//消息详情
			JSONObject messageObj = new JSONObject(obj.optString("content").replaceAll("\n", "").replaceAll("\r", ""));
			Log.e(TAG, "username = "+AVUser.getCurrentUser().get("username"));
			Log.e(TAG, "uid = "+ACache.get(MyApplication.ctx).getAsString("uid"));
			if(!msg_Object_id.toString().equals(obj.optString("objectId"))){
				msg_Object_id = new StringBuffer(obj.optString("objectId")) ;
				if (msgReceiverListener != null ) {
					msgReceiverListener.onMessageReceiver(context, group, message);
				}
				long timestamp = System.currentTimeMillis() ;
				int type = obj.optInt("type") ;
				/*AVObject pm9message = new AVObject("Pm9Message");
				 pm9message.put("content", message.getMessage());
				 pm9message.put("messageId", obj.optString("objectId"));
				 pm9message.put("groupId", group.getGroupId());
				 
				 pm9message.put("message_details", messageObj.optString("content"));
				 pm9message.put("uid", messageObj.optString("uid"));
				 pm9message.put("nickname",  messageObj.optString("nickname"));
				 
				 pm9message.put("messageTime", format.format(new Date(timestamp)));
				 pm9message.put("timestamp", timestamp);
				 
				 pm9message.put("type", type);
				 
				 pm9message.put("isComMsg", true);
				 pm9message.put("userId", ""+AVUser.getCurrentUser().getObjectId());
				 pm9message.saveInBackground();*/
				 
				 Pm9Message messageEntity = new Pm9Message() ;
				 messageEntity.setType(type == 0 ? Msg.Type.Text : Msg.Type.Image);
				 messageEntity.setDate(format.format(new Date(timestamp)));
				 messageEntity.setUid(messageObj.optString("uid"));
				 messageEntity.setComMeg(true);
				 messageEntity.setText(messageObj.optString("content"));
				 messageEntity.setMessage_time(timestamp);
				 messageEntity.setNickname(messageObj.optString("nickname"));
				 messageEntity.setName(messageObj.optString("nickname"));
				 messageEntity.setObjectId(obj.optString("objectId"));
				 messageEntity.setUserId(""+AVUser.getCurrentUser().getObjectId());
				 messageEntity.setGroupId(group.getGroupId());
				 messageEntity.setLocalPath(messageObj.optString("localpath"));
				 messageEntity.setMsg_type(type);
				 messageEntity.setAvatar(messageObj.optString("avatar")==null ?"" :messageObj.optString("avatar"));
				 Log.e(TAG, "msg receiver = " + messageObj.optString("avatar"));
				 DBService.saveMessage(messageEntity);
			}
		} catch (Exception e) {
			
		}
		
	}

	@Override
	public void onMessageFailure(Context arg0, Group group, AVMessage message) {
		Log.e(TAG,
				"发送群组消息：" + message.getMessage() + " to group "
						+ group.getGroupId() + " failed ");
	}

	// {"content":"{\"content\":\"广告费\",\"uid\":\"25815\",\"nickname\":\"Andy\"}","objectId":"M7Ncs8Ll9Pfv8Lbr67Nbq6Mu","type":0}
	// {"content":"{\"content\":\"http:\\/\\/ac-vqoyzl9c.clouddn.com\\/9vSedGLmRiqqBQId3gJkDFrtHrhwyOqD8dnewpjk\",\"uid\":\"25815\",\"nickname\":\"Andy\"}",
	// 					"objectId":"4rtzIMSgjSGmrLBBx9RevECM","type":1}
	@Override
	public void onMessageSent(Context arg0, Group group, AVMessage message) {
		Log.e(TAG,
				"发送群组消息：" + message.getMessage() + " to group "
						+ group.getGroupId() + " sent ");
		try {
			 JSONObject obj = new JSONObject(message.getMessage()) ;
			 //消息详情
			 JSONObject messageObj = new JSONObject(obj.optString("content").replaceAll("\n", "").replaceAll("\r", ""));
			
			 long timestamp = System.currentTimeMillis() ;
			 int type = obj.optInt("type") ;
			 /***
			 AVObject pm9message = new AVObject("Pm9Message");
			 pm9message.put("content", message.getMessage());
			 pm9message.put("messageId", obj.optString("objectId"));
			 pm9message.put("groupId", group.getGroupId());
			 pm9message.put("messageTime", format.format(new Date(timestamp)));
			 pm9message.put("timestamp", timestamp);
			 
			 pm9message.put("type", type);
			 
			 pm9message.put("message_details", messageObj.optString("content"));
			 pm9message.put("uid", messageObj.optString("uid"));
			 pm9message.put("nickname",  messageObj.optString("nickname"));
			 
			 pm9message.put("isComMsg", Boolean.FALSE);
			 pm9message.put("userId", ""+MyApplication.CURRENT_USER.getObjectId());
			 pm9message.saveInBackground();//保存到LeanCloud
			 ***/
			 //保存到本地数据库
			 Pm9Message messageEntity = new Pm9Message() ;
			 messageEntity.setType(type == 0 ? Msg.Type.Text : Msg.Type.Image);
			 messageEntity.setDate(format.format(new Date(timestamp)));
			 messageEntity.setUid(messageObj.optString("uid"));
			 messageEntity.setComMeg(Boolean.FALSE);
			 messageEntity.setText(messageObj.optString("content"));
			 messageEntity.setMessage_time(timestamp);
			 messageEntity.setNickname(messageObj.optString("nickname"));
			 messageEntity.setName(messageObj.optString("nickname"));
			 messageEntity.setObjectId(obj.optString("objectId"));
			 messageEntity.setUserId(""+MyApplication.CURRENT_USER.getObjectId());
			 messageEntity.setGroupId(group.getGroupId());
			 messageEntity.setLocalPath(messageObj.optString("localpath"));
			 messageEntity.setMsg_type(type);
			 messageEntity.setAvatar(messageObj.optString("avatar"));
			 DBService.saveMessage(messageEntity);
			 
			 //发送到服务器
			 Map<String,Object> paraMap = new HashMap<String, Object>();
			 
			 paraMap.put("content", message.getMessage());
			 paraMap.put("messageId", obj.optString("objectId"));
			 paraMap.put("groupId", group.getGroupId());
			 paraMap.put("messageTime", format.format(new Date(timestamp)));
			 paraMap.put("timestamp", timestamp);
			 paraMap.put("type", type);
			 paraMap.put("message_details", messageObj.optString("content"));
			 paraMap.put("uid", messageObj.optString("uid"));
			 paraMap.put("nickname",  messageObj.optString("nickname"));
			 paraMap.put("isComMsg", Boolean.FALSE);
			 paraMap.put("userId", ""+MyApplication.CURRENT_USER.getObjectId());
			
			 new PostMsgPm9AsynService(MyApplication.ctx, HttpTagConstantUtils.POST_MSG, this).post_msg(paraMap);
			 
			 
		} catch (Exception e) {
			Log.e(TAG, "发送消息异常。。。。。"+e.toString());
		} catch(OutOfMemoryError error){
			Log.e(TAG, "send message error");
		}
		 
	}

	@Override
	public void onQuit(Context ctx, Group group) {
		Log.e(TAG, "onQuit.......");
	}

	@Override
	public void onReject(Context ctx, Group group, String op,
			List<String> peerIds) {
		Log.e(TAG, "onReject......." + op + "");
	}

	public static void addMsgListener(MsgListener listener) {
		//msgListeners.add(listener);
		msgListeners = listener ;
	
	}

	public static void removeMsgListener(MsgListener listener) {
		//msgListeners.remove(listener);
		msgListeners = null ;
	}

	public static void addListener(GroupEventListener listener) {
		//groupListeners.add(listener);
		groupListeners = listener ;
	}

	public static void removeListener(GroupEventListener listener) {
		//groupListeners.remove(listener);
		groupListeners = null ;
	}

	public static void addMsgRecevierListener(MsgReceiverListener listener) {
		msgReceiverListener = listener;
	}

	public static void removeMsgRecevierListener(MsgReceiverListener listener) {
		msgReceiverListener = null;
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		// TODO Auto-generated method stub
		
	}

}
