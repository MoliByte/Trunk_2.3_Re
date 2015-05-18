package com.base.app.utils;

/**
 * Created by fangjilue on 14-3-24.
 */
public class KVOEvents {
    /**
     * 登录成功事件，参数：args[0] - User , args[1] - token
     */
    public static final String KVOLoginSuccess          = "KVOLoginSuccess";

    /**
     * 用户注销，参数：无
     */
    public static final String KVOLogout                = "KVOLogout";

    /**
     * api调用业务异常，参数：args[0] - ApiBizException
     */
    public static final String KVOApiBizException       = "KVOApiBizException";

    /**
     * 百度推送绑定事件,参数 args[0]:appid, args[1]:userId, args[2]:channelId, args[3]:requestId
     */
    public static final String KVOBaiduPushBin          = "KVOBaiduPushBin";

    /**
     * 百度定位成功事件，参数 args[0]: LocationPoint
     */
    public static final String KVOLocationSuccess       = "KVOLocationSuccess";

    /**
     * 停止下载
     */
    public static final String StopDownload             = "StopDownload";

    /**
     * 找到新的版本
     */
    public static final String FindNewVersion           = "FindNewVersion";

    /**
     * 头像刷新 args[0] 头像远程url args[1] 头像本地url
     */
    public static final String UserAvartaUpdateEvents   = "UserAvartaUpdateEvents";

    /**
     * 用户信息刷新 args[0] user
     */
    public static final String UserInfoUpdateEvents     = "UserInfoUpdateEvents";

    /**
     * 扫描任务二维码成功事件，参数：args[0] - mssnId
     */
    public static final String KVOScanMssnSuccess       = "KVOScanMssnSuccess";


    /**
     * 选择肤质成功后事件，参数 args[0] SkinType
     */
    public static final String ChooseSkinTypeEvents     = "ChooseSkinTypeEvents";

    /**
     * 选择地区成功后事件，参数 args[0] region province , args[1] region city, args[2] countryId
     */
    public static final String ChooseRegionEvents       = "ChooseRegionEvents";

    /**
     * 皮肤测试结果页，点击完成。参数无
     */
    public static final String SkinTestDoneEvents       = "SkinTestDoneEvents";

    /**
     * 重置登录密码成功后事件。参数无
     */
    public static final String RestPasswordSuccess      = "RestPasswordSuccess";


    /**
     * 跳转到登录页面。参数无
     */
    public static final String GotoLoginEvents          = "GotoLoginEvents";


    /**
     * 卡通切换事件，参数 args[0] testPreDiff args[1] index 下标
     */
    public static final String GartoonSwitchEvents      = "GartoonSwitchEvents";

    /**
     * 分享成功
     */
    public static final String SHARE_SUCCESS            = "SHARE_SUCCESS";
    /*
     * 测试结果
     */
    public static final String TEST_RESULT				="TEST_RESULT";
    /*
     * 测试部位
     */
    
    public static final String TEST_PART               ="TEST_PART";
    
    /*
     * 网络请求测试数据
     */
    public static final String ONLINE_REQUEST_TESTRECORD="ONLINE_REQUEST_TESTRECORD";
    /*
     * 用户登录事件
     */
    public static final String USER_LOGININ="USER_LOGININ";
    /*
     * 检测测试仪插入事件
     */
    public static final String DEVICE_IN="DEVICE_IN";
    /*
     * 拍照事件
     */
    
    public static final String TAKE_PHOTO="TAKE_PHOTO";
    /*
     * 截图
     */
    public static final String SHOT_SCREEN="SHOT_SCREEN";
    
    /*
     * 肌肤测试截图
     */
    public static final String SHOT_SKIN_SCREEN="SHOT_SKIN_SCREEN";
    /*
     * 收到未读消息通知
     */
    public static final String GET_PUSH_NUM="GET_PUSH_NUM";
    /*
     * 我的未读消息条数
     */
    public static final String GET_MYMESSAGE_COUNT="GET_MYMESSAGE_COUNT";
    
    /*
     * 完善个人信息后续行为处理
     */
    public static final String ACTION_AFTER_INFO="ACTION_AFTER_INFO";
}
