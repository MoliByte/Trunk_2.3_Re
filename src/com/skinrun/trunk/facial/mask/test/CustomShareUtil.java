//package com.skinrun.trunk.facial.mask.test;
//
//import java.io.File;
//import java.io.UnsupportedEncodingException;
//
//import android.app.Activity;
//import android.graphics.Bitmap;
//
//import com.baidu.frontia.Frontia;
//import com.baidu.frontia.api.FrontiaSocialShare;
//import com.baidu.frontia.api.FrontiaStatistics;
//import com.base.app.utils.BizConstant;
//import com.base.app.utils.StringUtil;
//import com.umeng.socialize.bean.SHARE_MEDIA;
//import com.umeng.socialize.bean.SocializeEntity;
//import com.umeng.socialize.bean.StatusCode;
//import com.umeng.socialize.controller.UMServiceFactory;
//import com.umeng.socialize.controller.UMSocialService;
//import com.umeng.socialize.controller.listener.SocializeListeners;
//import com.umeng.socialize.media.SinaShareContent;
//import com.umeng.socialize.media.UMImage;
//import com.umeng.socialize.weixin.controller.UMWXHandler;
//import com.umeng.socialize.weixin.media.CircleShareContent;
//
//public class CustomShareUtil {
//	private static final CustomShareUtil customShareUtil = new CustomShareUtil();
//	private UMSocialService mController;
//	private FrontiaSocialShare mSocialShare;
//	private FrontiaStatistics stat;
//
//	private CustomShareUtil() {
//		mController = UMServiceFactory.getUMSocialService("com.umeng.share");
//		
//		mSocialShare = Frontia.getSocialShare();
//	}
//
//	public static synchronized CustomShareUtil getInstance() {
//		return customShareUtil;
//	}
//
//	public UMSocialService getUmsocialService() {
//		return mController;
//	}
//
//	// 获取新浪微博内容
//	private String getSinaContent(String title, String content, String url) {
//		try {
//			String temp = "@肌肤管家skinrun " + title + "#" + content;
//			String urlTemp = " " + url;
//			if ((temp + urlTemp).getBytes("GBK").length <= 140 * 2) {
//				return temp + urlTemp;
//			} else {
//				urlTemp = "... " + url;
//				int urlByteLen = urlTemp.getBytes("GBK").length;
//				// 新浪微博要求140字
//				int len = 140 * 2 - urlByteLen;
//				return StringUtil.subStr(temp, len) + urlTemp;
//			}
//
//		} catch (UnsupportedEncodingException e) {
//			// 暂不处理
//			e.printStackTrace();
//			// AppLog.debug(e.getMessage());
//		}
//		return "";
//	}
//	public static interface ShareAction {
//        public void onSuccess();
//    }
//
//	class ShareListener implements SocializeListeners.SnsPostListener {
//
//		private ShareAction shareAction;
//
//		private ShareListener(ShareAction shareAction) {
//			this.shareAction = shareAction;
//		}
//
//		@Override
//		public void onStart() {
//			// AppLog.debug("share onStart ");
//		}
//
//		@Override
//		public void onComplete(SHARE_MEDIA share_media, int eCode,
//				SocializeEntity socializeEntity) {
//			if (eCode == StatusCode.ST_CODE_SUCCESSED) {
//				if (shareAction != null) {
//					shareAction.onSuccess();
//				}
//			}
//			// AppLog.debug("share onComplete code = "+ eCode);
//		}
//	}
//
//	public void sinaShare(final Activity activity, String title,String content, String linkUrl, Bitmap localImage, String imagePath,final ShareAction action) {
//		UMImage umImage = null;
//		if (!StringUtil.isBlank(imagePath)) {
//			umImage = new UMImage(activity, new File(imagePath));
//		} else {
//			umImage = new UMImage(activity, localImage);
//		}
//		if(title==null){
//			title="";
//		}
//		if(linkUrl==null){
//			linkUrl="";
//		}
//		 //新浪微博
//        SinaShareContent sinaShareContent = new SinaShareContent(umImage);
//        //新浪微博要求140字 所以要做截取
//        sinaShareContent.setShareContent(getSinaContent(title,content,linkUrl));
//        mController.setShareMedia(sinaShareContent);
//        
//        //新浪微博分享
//        mController.postShare(activity, SHARE_MEDIA.SINA, new ShareListener(action));
//	}
//	
//	public void WXHShare(final Activity activity, String title,String content, String linkUrl, Bitmap localImage, String imagePath,final ShareAction action){
//		 // 添加微信朋友圈
//        UMWXHandler wxCircleHandler = new UMWXHandler(activity,BizConstant.WEIXIN_CLIENT_ID,BizConstant.WEIXIN_CLIENT_KEY);
//        wxCircleHandler.setToCircle(true);
//        wxCircleHandler.addToSocialSDK();
//        
//        UMImage umImage = null;
//        if (!StringUtil.isBlank(imagePath)) {
//			umImage = new UMImage(activity, new File(imagePath));
//		} else {
//			umImage = new UMImage(activity, localImage);
//		}
//		if(title==null){
//			title="";
//		}
//		if(linkUrl==null){
//			linkUrl="";
//		}
//		
//		//设置微信朋友圈分享内容
//
//        CircleShareContent circleMedia = new CircleShareContent();
//        circleMedia.setShareImage(umImage);
//        circleMedia.setTargetUrl(linkUrl);
//        //circleMedia.setTitle(getCircleContent(title,content));
//        //circleMedia.setShareContent(content);
//        mController.setShareMedia(circleMedia);
//        mController.postShare(activity, SHARE_MEDIA.WEIXIN_CIRCLE, new ShareListener(action));
//	}
//
//}
