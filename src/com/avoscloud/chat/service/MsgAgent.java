package com.avoscloud.chat.service;

import org.kymjs.aframe.utils.SystemTool;

import com.app.base.init.MyApplication;
import com.avos.avoscloud.AVMessage;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.Group;
import com.avos.avoscloud.Session;
import com.avoscloud.chat.entity.Msg;
import com.avoscloud.chat.entity.MsgBuilder;
import com.avoscloud.chat.entity.RoomType;
import com.avoscloud.chat.entity.SendCallback;
import com.avoscloud.chat.service.receiver.MsgReceiver;
import com.avoscloud.chat.util.NetAsyncTask;
import com.base.supertoasts.util.AppToast;

/**
 * Created by lzw on 14/11/23.
 */
public class MsgAgent {
	RoomType roomType;
	String toId;

	public MsgAgent(RoomType roomType, String toId) {
		this.roomType = roomType;
		this.toId = toId;
	}

	public interface MsgBuilderHelper {
		void specifyType(MsgBuilder msgBuilder);
	}

	//发送消息
	public void createAndSendMsg(MsgBuilderHelper msgBuilderHelper,
			final SendCallback callback) {
		final MsgBuilder builder = new MsgBuilder();
		builder.target(roomType, toId);
		msgBuilderHelper.specifyType(builder);
		final Msg msg = builder.preBuild();
		try {
			if(!SystemTool.checkNet(MyApplication.ctx)){
				AppToast.toastMsgCenter(MyApplication.ctx, "消息发送失败,请稍后重试!").show();
				return ;
			}
			if(MsgReceiver.isSessionPaused()){
				AppToast.toastMsgCenter(MyApplication.ctx, "消息发送失败,请稍后重试!").show();
				return ;
			}
			callback.onStart(msg);
			uploadAndSendMsg(msg, callback);
		} catch (Exception e) {
			AppToast.toastMsgCenter(MyApplication.ctx, "消息发送失败,请稍后重试!").show();
		} catch(OutOfMemoryError error){
			AppToast.toastMsgCenter(MyApplication.ctx, "消息发送失败,请稍后重试!").show();
		}
		
	}

	public void uploadAndSendMsg(final Msg msg, final SendCallback callback) {
		new NetAsyncTask(MyApplication.ctx, false) {
			String uploadUrl;

			@Override
			protected void doInBack() throws Exception {
				uploadUrl = MsgBuilder.uploadMsg(msg);
			}

			@Override
			protected void onPost(Exception e) {
				if (e != null) {
					e.printStackTrace();
					//更新本地消息发送状态
					//DBMsg.updateStatus(msg.getObjectId(), Msg.Status.SendFailed);
					callback.onError(e);
				} else {
					if (uploadUrl != null) {
						//DBMsg.updateContent(msg.getObjectId(), uploadUrl);
					}
					sendMsg(msg);
					callback.onSuccess(msg);
				}
			}
		}.execute();
	}

	public Msg sendMsg(Msg msg) {
		AVMessage avMsg = msg.toAVMessage();
		try {
			if(!MsgReceiver.isSessionPaused()){
				Session session = MsgReceiver.getSession(AVUser.getCurrentUser());
				if (roomType == RoomType.Single) {//单聊
					session.sendMessage(avMsg);
				} else {//群聊
					Group group = session.getGroup(toId);
					group.sendMessage(avMsg);
				}
			}else{
				AppToast.toastMsgCenter(MyApplication.ctx, "消息发送失败,请稍后重试!").show();
			}
		} catch (Exception e) {
			AppToast.toastMsgCenter(MyApplication.ctx, "消息发送失败,请稍后重试!").show();
		} catch(OutOfMemoryError error){
			AppToast.toastMsgCenter(MyApplication.ctx, "消息发送失败,请稍后重试!").show();
		}
		return msg;
	}

	public static void resendMsg(Msg msg, SendCallback sendCallback) {
		String toId;
		if (msg.getRoomType() == RoomType.Group) {
			String groupId = msg.getConvid();
			toId = groupId;
		} else {
			toId = msg.getToPeerId();
			msg.setRequestReceipt(true);
		}
		//DBMsg.updateStatus(msg.getObjectId(), Msg.Status.SendStart);
		sendCallback.onStart(msg);
		MsgAgent msgAgent = new MsgAgent(msg.getRoomType(), toId);
		msgAgent.uploadAndSendMsg(msg, sendCallback);
	}
}
