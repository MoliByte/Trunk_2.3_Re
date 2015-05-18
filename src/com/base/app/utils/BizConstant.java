/**
 *
 */
package com.base.app.utils;

/**
 * @author fangjilue
 *         <p/>
 *         业务常量
 */
public interface BizConstant {
    //百度基础 api key
    String BAIDU_API_KEY = "hFCOpmICMr0je9Zxd5P4Bro2";//hFCOpmICMr0je9Zxd5P4Bro2,aAXqUuFWSdaLu9oqF8tKsiMM

    //分享
    String SINAWEIBO_CLIENT_ID = "3828250409";
    String SINAWEIBO_CLIENT_KEY = "022b6f4c13515065c1b1b84d167526c9";

    String QZONE_CLIENT_ID = "100381049";
    String QQFRIEND_CLIENT_ID = "100381049";
    String QQFRIEND_CLIENT_NAME = "肌肤管家";
    String QQ_APP_KEY = "3ad747d61375966b12e2862ef8a70e61";

    String WEIXIN_CLIENT_ID = "wx4297582bc8743dc3";
    String WEIXIN_CLIENT_KEY = "fad627ead5f6622ecacde5caf71895ec";

    //百度统计
    String BAIDU_TJ_CHANNEL = "website";
    int BAIDU_TJ_SessionTimeout = 60;
    String BAIDU_TJ_REPORTID = "e54a1b2dcb";

    //数据库密钥
    String db_secret_key = "hjy123456";
    //数据库密钥
    String data_file_key = "1#$^%&sd";

    //验证码发送时间间隔
    int COUNT_DOWN_TIME = 120 * 1000;//倒计时120秒
    int INTERVAL_TIME = 1 * 1000;//时间间隔1秒

    //测试监控时间
    int TEST_MONITOR_TIME = 20 * 1000;//20 秒无响应
    int TEST_MONITOR_INTERVAL_TIME = 1 * 1000;

}
