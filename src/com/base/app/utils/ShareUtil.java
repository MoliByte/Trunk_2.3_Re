//package com.base.app.utils;
//
//import android.app.Activity;
//import android.graphics.Bitmap;
//import android.net.Uri;
//
//import com.app.base.init.MyApplication;
//import com.baidu.frontia.Frontia;
//import com.baidu.frontia.api.FrontiaAuthorization;
//import com.baidu.frontia.api.FrontiaSocialShare;
//import com.baidu.frontia.api.FrontiaSocialShareContent;
//import com.baidu.frontia.api.FrontiaSocialShareListener;
//import com.baidu.frontia.api.FrontiaStatistics;
//import com.idongler.widgets.ShareDialog;
//
///**
// * Created by Lerry on 14-5-21.
// */
//public class ShareUtil {
//    private static final ShareUtil shareUtil = new ShareUtil();
//    private FrontiaSocialShare mSocialShare;
//    private FrontiaStatistics stat;
//
//
//    private ShareUtil() {
//        Frontia.init(MyApplication.getInstance(), BizConstant.BAIDU_API_KEY);
//
//        mSocialShare = Frontia.getSocialShare();
//        mSocialShare.setClientId(FrontiaAuthorization.MediaType.SINAWEIBO.toString(), BizConstant.SINAWEIBO_CLIENT_ID);
//        mSocialShare.setClientId(FrontiaAuthorization.MediaType.QZONE.toString(), BizConstant.QZONE_CLIENT_ID);
//        mSocialShare.setClientId(FrontiaAuthorization.MediaType.QQFRIEND.toString(), BizConstant.QQFRIEND_CLIENT_ID);
//        //QQ好友分享需要设置分享平台的应用名称
//        mSocialShare.setClientName(FrontiaAuthorization.MediaType.QQFRIEND.toString(), BizConstant.QQFRIEND_CLIENT_NAME);
//        mSocialShare.setClientId(FrontiaAuthorization.MediaType.WEIXIN.toString(), BizConstant.WEIXIN_CLIENT_ID);
//
//        stat = Frontia.getStatistics();
//        stat.setAppDistributionChannel(BizConstant.BAIDU_TJ_CHANNEL, true);//设置应用发布渠道
//        stat.setSessionTimeout(BizConstant.BAIDU_TJ_SessionTimeout);//测试时，可以使用1秒钟session过期，这样不断的启动退出会产生大量日志。
//        //stat.enableExceptionLog();//开启异常日志
//        stat.setReportId(BizConstant.BAIDU_TJ_REPORTID);//reportId必须在mtj网站上注册生成，该设置也可以在AndroidManifest.xml中填写
//    }
//
//    public static synchronized ShareUtil getInstance() {
//        return shareUtil;
//    }
//
//    /**
//     * 分享自定义弹出框
//     *
//     * @param activity
//     * @param title
//     * @param content
//     * @param linkUrl
//     * @param imageUrl
//     * @param localImage
//     */
//    public void share(Activity activity, String title, String content, String linkUrl, String imageUrl, Bitmap localImage) {
//        share(activity, title, content, linkUrl, imageUrl, localImage, null);
//    }
//
//    /**
//     * 分享自定义弹出框
//     *
//     * @param activity
//     * @param title
//     * @param content
//     * @param linkUrl
//     * @param imageUrl
//     * @param localImage
//     * @param action
//     */
//    public void share(Activity activity, String title, String content, String linkUrl, String imageUrl, Bitmap localImage, final ShareAction action) {
//        mSocialShare.setContext(activity);
//        //设置分享菜单的父窗口
//        //mSocialShare.setParentView(activity.getWindow().getDecorView());
//
//        final FrontiaSocialShareContent mImageContent = new FrontiaSocialShareContent();
//        mImageContent.setTitle(title);
//        mImageContent.setContent(content);
//        mImageContent.setLinkUrl(linkUrl);
//        if (imageUrl != null) {
//            mImageContent.setImageUri(Uri.parse(imageUrl));
//        } else if (localImage != null) {
//            mImageContent.setImageData(localImage);
//        }
//
//        /*
//        mSocialShare.show(activity.getWindow().getDecorView(), mImageContent, FrontiaSocialShare.FrontiaTheme.DARK,  new FrontiaSocialShareListener(){
//
//            @Override
//            public void onSuccess() {
//                Log.d("share","share complete");
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//                Log.d("share","share fail");
//            }
//
//            @Override
//            public void onCancel() {
//                Log.d("share","share cancel");
//            }
//        });
//        */
//
//        ShareDialog dialog = new ShareDialog(activity);
//        dialog.setAction(new ShareDialog.Action() {
//            @Override
//            public void showQQ() {
//                mSocialShare.share(mImageContent, FrontiaAuthorization.MediaType.QQFRIEND.toString(), new ShareListener(action), true);
//
//                FrontiaStatistics.Event event = new FrontiaStatistics.Event("share_qq_friend", "分享到qq好友");
//                stat.logEvent(event);
//            }
//
//            @Override
//            public void showWx() {
//
//                mSocialShare.share(mImageContent, FrontiaAuthorization.MediaType.WEIXIN_FRIEND.toString(), new ShareListener(action), true);
//
//                FrontiaStatistics.Event event = new FrontiaStatistics.Event("share_wechat_session", "分享到微信好友");
//                stat.logEvent(event);
//            }
//
//            @Override
//            public void showWxb() {
//                mImageContent.setTitle(mImageContent.getContent());
//                mSocialShare.share(mImageContent, FrontiaAuthorization.MediaType.WEIXIN_TIMELINE.toString(), new ShareListener(action), true);
//
//                FrontiaStatistics.Event event = new FrontiaStatistics.Event("share_wechat_timeline", "分享到微信朋友圈");
//                stat.logEvent(event);
//            }
//
//            @Override
//            public void showSm() {
//                mSocialShare.share(mImageContent, FrontiaAuthorization.MediaType.SMS.toString(), new ShareListener(action), true);
//
//                FrontiaStatistics.Event event = new FrontiaStatistics.Event("share_sms", "短信分享");
//                stat.logEvent(event);
//
//            }
//
//            @Override
//            public void showWb() {
//                mSocialShare.share(mImageContent, FrontiaAuthorization.MediaType.SINAWEIBO.toString(), new ShareListener(action), true);
//
//                FrontiaStatistics.Event event = new FrontiaStatistics.Event("share_sina_weibo", "微博分享");
//                stat.logEvent(event);
//
//            }
//        });
//        dialog.showDialog();
//    }
//    //新浪分享
//    public void sinaShare(Activity activity, String title, String content, String linkUrl, String imageUrl, Bitmap localImage, final ShareAction action){
//    	mSocialShare.setContext(activity);
//    	final FrontiaSocialShareContent mImageContent = new FrontiaSocialShareContent();
//        mImageContent.setTitle(title);
//        mImageContent.setContent(content);
//        mImageContent.setLinkUrl(linkUrl);
//        if (imageUrl != null) {
//             mImageContent.setImageUri(Uri.parse(imageUrl));
//        } else if (localImage != null) {
//            mImageContent.setImageData(localImage);
//        }
//         mSocialShare.share(mImageContent, FrontiaAuthorization.MediaType.SINAWEIBO.toString(), new ShareListener(action), true);
//
//         FrontiaStatistics.Event event = new FrontiaStatistics.Event("share_sina_weibo", "微博分享");
//         stat.logEvent(event);
//    }
//    
//    public void WXCShare(Activity activity, String title, String content, String linkUrl, String imageUrl, Bitmap localImage, final ShareAction action){
//    	mSocialShare.setContext(activity);
//    	final FrontiaSocialShareContent mImageContent = new FrontiaSocialShareContent();
//        mImageContent.setTitle(title);
//        mImageContent.setContent(content);
//        mImageContent.setLinkUrl(linkUrl);
//        if (imageUrl != null) {
//             mImageContent.setImageUri(Uri.parse(imageUrl));
//        } else if (localImage != null) {
//            mImageContent.setImageData(localImage);
//        }
//        mImageContent.setTitle(mImageContent.getContent());
//        mSocialShare.share(mImageContent, FrontiaAuthorization.MediaType.WEIXIN_TIMELINE.toString(), new ShareListener(action), true);
//
//        FrontiaStatistics.Event event = new FrontiaStatistics.Event("share_wechat_timeline", "分享到微信朋友圈");
//        stat.logEvent(event);
//    }
//
//    class ShareListener implements FrontiaSocialShareListener {
//
//        private ShareAction shareAction;
//
//        private ShareListener(ShareAction shareAction) {
//            this.shareAction = shareAction;
//        }
//
//        @Override
//        public void onSuccess() {
//            //AppLog.debug("share onSuccess");
//            if (shareAction != null) {
//                shareAction.onSuccess();
//            }
//        }
//
//        @Override
//        public void onFailure(int errCode, String errMsg) {
//            //AppLog.debug("share onFailure:" + errCode + "," + errMsg);
//        }
//
//        @Override
//        public void onCancel() {
//            //AppLog.debug("share onCancel");
//        }
//
//    }
//
//    public static interface ShareAction {
//        public void onSuccess();
//    }
//
//    /**
//     * 自定义分享图片
//     * @param activity
//     * @param linkUrl
//     * @param localImage
//     * @param action
//     */
//    public void shareImage(Activity activity, String linkUrl, Bitmap localImage, final ShareAction action) {
//        mSocialShare.setContext(activity);
//
//        final FrontiaSocialShareContent mImageContent = new FrontiaSocialShareContent();
//        mImageContent.setTitle("");
//        mImageContent.setContent("");
//        mImageContent.setLinkUrl(linkUrl);
//        mImageContent.setImageData(localImage);
//        mImageContent.setWXMediaObjectType(FrontiaSocialShareContent.FrontiaIMediaObject.TYPE_IMAGE);
//        mImageContent.setQQRequestType(FrontiaSocialShareContent.FrontiaIQQReqestType.TYPE_IMAGE);
//        mImageContent.setQQFlagType(FrontiaSocialShareContent.FrontiaIQQFlagType.TYPE_DEFAULT);
//        //默认100
//        mImageContent.setCompressDataQuality(80);
//
//        ShareDialog dialog = new ShareDialog(activity,ShareDialog.SHARE_MODE_IMAGE);
//        dialog.setAction(new ShareDialog.Action() {
//            @Override
//            public void showQQ() {
//                mSocialShare.share(mImageContent, FrontiaAuthorization.MediaType.QQFRIEND.toString(), new ShareListener(action), true);
//
//                FrontiaStatistics.Event event = new FrontiaStatistics.Event("share_qq_friend", "分享到qq好友");
//                stat.logEvent(event);
//            }
//
//            @Override
//            public void showWx() {
//                mSocialShare.share(mImageContent, FrontiaAuthorization.MediaType.WEIXIN_FRIEND.toString(), new ShareListener(action), true);
//
//                FrontiaStatistics.Event event = new FrontiaStatistics.Event("share_wechat_session", "分享到微信好友");
//                stat.logEvent(event);
//            }
//
//            @Override
//            public void showWxb() {
//                mSocialShare.share(mImageContent, FrontiaAuthorization.MediaType.WEIXIN_TIMELINE.toString(), new ShareListener(action), true);
//
//                FrontiaStatistics.Event event = new FrontiaStatistics.Event("share_wechat_timeline", "分享到微信朋友圈");
//                stat.logEvent(event);
//            }
//
//            @Override
//            public void showSm() {
//
//            }
//
//            @Override
//            public void showWb() {
//                mSocialShare.share(mImageContent, FrontiaAuthorization.MediaType.SINAWEIBO.toString(), new ShareListener(action), true);
//
//                FrontiaStatistics.Event event = new FrontiaStatistics.Event("share_sina_weibo", "分享到新泿微博");
//                stat.logEvent(event);
//            }
//        });
//        dialog.showDialog();
//    }
//
//    // 分享图片到qq好友
//    /*public void shareImageToQQFiend(Activity activity, String linkUrl, Bitmap localImage, final ShareAction action) {
//        shareImage(FrontiaAuthorization.MediaType.QQFRIEND, activity, linkUrl, localImage, action);
//    }*/
//
//    // 分享图片到qq空间,必须要有title，点击不在是放大图片需要链接到linkUrl
//    /*public void shareImageToQQZone(Activity activity, String linkUrl, Bitmap localImage, final ShareAction action) {
//        shareImage(FrontiaAuthorization.MediaType.QZONE, activity, linkUrl, localImage, action);
//    }*/
//
//
//    // 分享图片到微信好友
//    /*public void shareImageToWenXinFriend(Activity activity, String linkUrl, Bitmap localImage, final ShareAction action) {
//        shareImage(FrontiaAuthorization.MediaType.WEIXIN_FRIEND, activity, linkUrl, localImage, action);
//    }
//    */
//
//    //分享图片到微信朋友圈
//    /*public void shareImageToWenXinTimeline(Activity activity, String linkUrl, Bitmap localImage, final ShareAction action) {
//        shareImage(FrontiaAuthorization.MediaType.WEIXIN_TIMELINE, activity, linkUrl, localImage, action);
//    }*/
//
//    /*private void shareImage(FrontiaAuthorization.MediaType mediaType, Activity activity, String linkUrl, Bitmap localImage, final ShareAction action) {
//        mSocialShare.setContext(activity);
//
//        final FrontiaSocialShareContent mImageContent = new FrontiaSocialShareContent();
//        mImageContent.setTitle("");
//        mImageContent.setContent("");
//        mImageContent.setLinkUrl(linkUrl);
//        mImageContent.setImageData(localImage);
//        mImageContent.setWXMediaObjectType(FrontiaSocialShareContent.FrontiaIMediaObject.TYPE_IMAGE);
//        mImageContent.setQQRequestType(FrontiaSocialShareContent.FrontiaIQQReqestType.TYPE_IMAGE);
//        mImageContent.setQQFlagType(FrontiaSocialShareContent.FrontiaIQQFlagType.TYPE_DEFAULT);
//        //默认100
//        mImageContent.setCompressDataQuality(85);
//
//        mSocialShare.share(mImageContent, mediaType.toString(), new ShareListener(action), true);
//
//        switch (mediaType) {
//            case QQFRIEND:
//                FrontiaStatistics.Event event1 = new FrontiaStatistics.Event("share_qq_friend", "分享到qq好友");
//                stat.logEvent(event1);
//                break;
//            case WEIXIN_FRIEND:
//                FrontiaStatistics.Event event2 = new FrontiaStatistics.Event("share_wechat_session", "分享到微信好友");
//                stat.logEvent(event2);
//                break;
//            case WEIXIN_TIMELINE:
//                FrontiaStatistics.Event event3 = new FrontiaStatistics.Event("share_wechat_timeline", "分享到微信朋友圈");
//                stat.logEvent(event3);
//                break;
//        }
//    }*/
//
//}
