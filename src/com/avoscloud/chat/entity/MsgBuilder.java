package com.avoscloud.chat.entity;

import java.io.IOException;

import org.json.JSONObject;

import com.app.base.init.ACache;
import com.app.base.init.MyApplication;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.LogUtil.log;
import com.avoscloud.chat.service.ChatService;
import com.avoscloud.chat.util.ChatUtils;
import com.avoscloud.chat.util.PathUtils;
import com.avoscloud.chat.util.Utils;
import com.base.app.utils.StringUtil;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class MsgBuilder {
	Msg msg;

	public MsgBuilder() {
		msg = new Msg();
	}

	public void target(RoomType roomType, String toId) {
		String convid;
		msg.setRoomType(roomType);
		if (roomType == RoomType.Single) {
			msg.setToPeerId(toId);
			msg.setRequestReceipt(true);
			convid = ChatUtils.convid(ChatService.getSelfId(), toId);
		} else {
			convid = toId;
		}
		msg.setConvid(convid);
		msg.setReadStatus(Msg.ReadStatus.HaveRead);
	}

	public void text(String content) {
		msg.setType(Msg.Type.Text);
		msg.setContent(content);
	}

	private void file(Msg.Type type, String objectId) {
		msg.setType(type);
		msg.setObjectId(objectId);
	}

	public void image(String content) {
		StringBuffer objectId = new StringBuffer("");
		try {
			final JSONObject messageJson = new JSONObject(content);
			objectId.append(messageJson.opt("objectId"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		file(Msg.Type.Image, objectId.toString());
	}

	public void location(String address, double latitude, double longitude) {
		String content = address + "&" + latitude + "&" + longitude;
		msg.setContent(content);
		msg.setType(Msg.Type.Location);
	}

	public void audio(String objectId) {
		file(Msg.Type.Audio, objectId);
	}

	public Msg preBuild() {
		msg.setStatus(Msg.Status.SendStart);
		msg.setTimestamp(System.currentTimeMillis());
		msg.setFromPeerId(ChatService.getSelfId());
		if (msg.getObjectId() == null) {
			msg.setObjectId(Utils.uuid());
		}
		return msg;
	}

	public static String uploadMsg(Msg msg) throws IOException, AVException {
		if (msg.getType() != Msg.Type.Audio && msg.getType() != Msg.Type.Image) {
			return null;
		}
		String objectId = msg.getObjectId();
		if (objectId == null) {
			throw new NullPointerException("objectId mustn't be null");
		}
		String filePath = PathUtils.getChatFilePath(objectId);
		
		AVFile file = AVFile.withAbsoluteLocalPath(objectId, filePath);
		file.save();
		
		String url = file.getUrl();
		try {

			log.e("image url = " + url);

			final JSONObject messageJson = new JSONObject();

			messageJson.put("uid", ""
					+ ACache.get(MyApplication.ctx).getAsString("uid"));
			messageJson.put("nickname", ""
					+ ACache.get(MyApplication.ctx).getAsString("nickname"));
			messageJson.put("content", "" + url);
			messageJson.put("localpath", filePath);
			if(!SHARE_MEDIA.WEIXIN.equals(ACache.get(MyApplication.ctx).getAsString("type"))
					&&!SHARE_MEDIA.SINA.equals(ACache.get(MyApplication.ctx).getAsString("type"))
					&&!SHARE_MEDIA.QZONE.equals(ACache.get(MyApplication.ctx).getAsString("type"))){
				
				messageJson.put("avatar", HttpClientUtils.USER_IMAGE_URL
								+ StringUtil.getPathByUid("" + ACache.get(MyApplication.ctx).getAsString("uid"))) ;
			}else{
				messageJson.put("avatar", 
						""+ACache.get(MyApplication.ctx).getAsString("avatar"));
			}
			log.e("messageJson = " + messageJson.toString());

			msg.setContent(messageJson.toString());
		} catch (Exception e) {
			AppToast.toastMsgCenter(MyApplication.ctx, "消息发送失败,请稍后重试!").show();
		} catch (OutOfMemoryError e) {
			AppToast.toastMsgCenter(MyApplication.ctx, "消息发送失败,请稍后重试!").show();
		}
		return url;
	}
}
