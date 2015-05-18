package com.base.app.utils;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.graphics.Bitmap;

import com.idongler.widgets.ShareDialog;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * Created by Lerry on 14-5-21.
 */
public class UMShareUtil {
    private static final UMShareUtil shareUtil = new UMShareUtil();
    private UMSocialService mController;

    private UMShareUtil() {
        mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        //设置分享面板上显示的平台
        //mController.getConfig().setPlatforms(SHARE_MEDIA.QQ,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.SINA);
        //设置顺序
        //mController.getConfig().setPlatformOrder(SHARE_MEDIA.QQ,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.SINA);

        //使用新浪微博SSO授权
        //mController.getConfig().setSsoHandler(new SinaSsoHandler());
        
        //mController.getConfig().setSinaCallbackUrl("www.skinrun.me");
        
        

    }

    public static synchronized UMShareUtil getInstance() {
        return shareUtil;
    }

    public UMSocialService getUmsocialService(){
        return mController;
    }

    //获取新浪微博内容
    private String getSinaContent(String title,String content,String url){
        try {
            String temp ="@肌肤管家skinrun " + title+"#"+content;
            String urlTemp = " "+url;
            if((temp+urlTemp).getBytes("GBK").length<=140*2){
                return temp+urlTemp;
            }else{
                urlTemp = "... "+url;
                int urlByteLen = urlTemp.getBytes("GBK").length;
                //新浪微博要求140字
                int len = 140*2-urlByteLen;
                return StringUtil.subStr(temp,len)+urlTemp;
            }

        } catch (UnsupportedEncodingException e) {
            //暂不处理
            e.printStackTrace();
            //AppLog.debug(e.getMessage());
        }
        return "";
    }

    //获取朋友圈内容
    private String getCircleContent(String title,String content){
        String result = title+"#"+content;
        return result;
    }

    class ShareListener implements SocializeListeners.SnsPostListener {

        private ShareAction shareAction;

        private ShareListener(ShareAction shareAction) {
            this.shareAction = shareAction;
        }


        @Override
        public void onStart() {
            //AppLog.debug("share onStart ");
        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int eCode, SocializeEntity socializeEntity) {
            if (eCode == StatusCode.ST_CODE_SUCCESSED) {
                if (shareAction != null) {
                    shareAction.onSuccess();
                }
            }
            //AppLog.debug("share onComplete code = "+ eCode);
        }
    }

    public static interface ShareAction {
        public void onSuccess();
    }


    public void shareImage(final Activity activity, String linkUrl, Bitmap localImage, final ShareAction action) {
        //添加微信好友
        UMWXHandler wxHandler = new UMWXHandler(activity,BizConstant.WEIXIN_CLIENT_ID,BizConstant.WEIXIN_CLIENT_KEY);
        wxHandler.addToSocialSDK();

        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(activity,BizConstant.WEIXIN_CLIENT_ID,BizConstant.WEIXIN_CLIENT_KEY);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();


        // 添加QQ支持, 并且设置QQ分享内容的target url
        /*UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity,BizConstant.QQFRIEND_CLIENT_ID, BizConstant.QQ_APP_KEY);
        qqSsoHandler.setTargetUrl(linkUrl);
        qqSsoHandler.addToSocialSDK();*/

        UMImage umImage = new UMImage(activity,localImage);

        //新浪微博
        SinaShareContent sinaShareContent = new SinaShareContent(umImage);
        //新浪微博要求140字 所以要做截取
        //sinaShareContent.setShareContent(getSinaContent(content,linkUrl));
        mController.setShareMedia(sinaShareContent);


        //设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        //设置分享文字
        //weixinContent.setShareContent(content);
        //设置title
        //weixinContent.setTitle(title);
        //设置分享内容跳转URL
        weixinContent.setTargetUrl(linkUrl);
        //设置分享图片
        weixinContent.setShareImage(umImage);
        mController.setShareMedia(weixinContent);

        //设置微信朋友圈分享内容

        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareImage(umImage);
        circleMedia.setTargetUrl(linkUrl);
        //circleMedia.setTitle(getCircleContent(title,content));
        //circleMedia.setShareContent(content);
        mController.setShareMedia(circleMedia);

        /*QQShareContent qqMedia = new QQShareContent();
        qqMedia.setShareImage(umImage);
        qqMedia.setTargetUrl(linkUrl);
        mController.setShareMedia(qqMedia);*/

        ShareDialog dialog = new ShareDialog(activity,ShareDialog.SHARE_MODE_IMAGE);
        dialog.setAction(new ShareDialog.Action() {
            @Override
            public void showQQ() {
                //mController.directShare(activity,SHARE_MEDIA.QQ,new ShareListener(action));
            }

            @Override
            public void showWx() {
                mController.postShare(activity, SHARE_MEDIA.WEIXIN, new ShareListener(action));
            }

            @Override
            public void showWxb() {
                mController.postShare(activity, SHARE_MEDIA.WEIXIN_CIRCLE, new ShareListener(action));
            }

            @Override
            public void showSm() {
            }

            @Override
            public void showWb() {
                mController.postShare(activity, SHARE_MEDIA.SINA, new ShareListener(action));
            }
        });
        dialog.showDialog();
    }
    //微信分享图片
    public void WXCShareImage(final Activity activity, String linkUrl, Bitmap localImage, final ShareAction action){
    	// 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(activity,BizConstant.WEIXIN_CLIENT_ID,BizConstant.WEIXIN_CLIENT_KEY);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK(); 
    	//设置微信朋友圈分享内容
    	UMImage umImage = new UMImage(activity,localImage);
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareImage(umImage);
        circleMedia.setTargetUrl(linkUrl);
        mController.setShareMedia(circleMedia);
        mController.postShare(activity, SHARE_MEDIA.WEIXIN_CIRCLE, new ShareListener(action));
    }
    
    //新浪分享图片
    public void sinaShareImage(final Activity activity, String linkUrl, Bitmap localImage, final ShareAction action){
    	
    	 UMImage umImage = new UMImage(activity,localImage);
         //新浪微博
         SinaShareContent sinaShareContent = new SinaShareContent(umImage);
         mController.setShareMedia(sinaShareContent);
         mController.postShare(activity, SHARE_MEDIA.SINA, new ShareListener(action));
         
        
    }

    public void share(final Activity activity,String title, String content, String linkUrl, Bitmap localImage, String logoUrl, final ShareAction action) {
        //添加微信好友
        UMWXHandler wxHandler = new UMWXHandler(activity,BizConstant.WEIXIN_CLIENT_ID,BizConstant.WEIXIN_CLIENT_KEY);
        wxHandler.addToSocialSDK();

        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(activity,BizConstant.WEIXIN_CLIENT_ID,BizConstant.WEIXIN_CLIENT_KEY);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();


        // 添加QQ支持, 并且设置QQ分享内容的target url
        /*UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity,BizConstant.QQFRIEND_CLIENT_ID, BizConstant.QQ_APP_KEY);
        qqSsoHandler.setTargetUrl(linkUrl);
        qqSsoHandler.addToSocialSDK();*/
        UMImage umImage = null;
        if(!StringUtil.isBlank(logoUrl)){
            umImage = new UMImage(activity,logoUrl);
        }else {
            umImage = new UMImage(activity,localImage);
        }

        //新浪微博
        SinaShareContent sinaShareContent = new SinaShareContent(umImage);
        //新浪微博要求140字 所以要做截取
        sinaShareContent.setShareContent(getSinaContent(title,content,linkUrl));
        mController.setShareMedia(sinaShareContent);


        //设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        //设置分享文字
        weixinContent.setShareContent(content);
        //设置title
        weixinContent.setTitle(title);
        //设置分享内容跳转URL
        weixinContent.setTargetUrl(linkUrl);
        //设置分享图片
        weixinContent.setShareImage(umImage);
        mController.setShareMedia(weixinContent);

        //设置微信朋友圈分享内容

        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareImage(umImage);
        circleMedia.setTargetUrl(linkUrl);
        circleMedia.setTitle(title);
        circleMedia.setShareContent(content);
        mController.setShareMedia(circleMedia);

        /*QQShareContent qqMedia = new QQShareContent();
        qqMedia.setShareImage(umImage);
        qqMedia.setTargetUrl(linkUrl);
        mController.setShareMedia(qqMedia);*/

        ShareDialog dialog = new ShareDialog(activity,ShareDialog.SHARE_MODE_IMAGE);
        dialog.setAction(new ShareDialog.Action() {
            @Override
            public void showQQ() {
                //mController.directShare(activity,SHARE_MEDIA.QQ,new ShareListener(action));
            }

            @Override
            public void showWx() {
                mController.postShare(activity, SHARE_MEDIA.WEIXIN, new ShareListener(action));
            }

            @Override
            public void showWxb() {
                mController.postShare(activity, SHARE_MEDIA.WEIXIN_CIRCLE, new ShareListener(action));
            }

            @Override
            public void showSm() {
            }

            @Override
            public void showWb() {
                mController.postShare(activity, SHARE_MEDIA.SINA, new ShareListener(action));
            }
        });
        dialog.showDialog();
    }
   
   
}
