package com.zxing.client.android;

/**
 * Created by fangjilue on 14-4-21.
 */
public interface PreferencesConfig {

    public static final String KEY_DECODE_1D = "preferences_decode_1D";

    public static final String KEY_DECODE_QR = "preferences_decode_QR";

    public static final String KEY_DECODE_DATA_MATRIX = "preferences_decode_Data_Matrix";

    /**
     * 自定义搜索网址
     */
    public static final String KEY_CUSTOM_PRODUCT_SEARCH = "preferences_custom_product_search";

    /**
     * 扫描成功时播放提示音
     */
    public static final String KEY_PLAY_BEEP = "preferences_play_beep";

    /**
     * 扫描成功时震动
     */
    public static final String KEY_VIBRATE = "preferences_vibrate";


    /**
     * 虽然这里是front_light，但实际是手机背面的闪光灯
     */
    public static final String KEY_FRONT_LIGHT_MODE = "preferences_front_light_mode";


    /**
     * 使用自动对焦
     */
    public static final String KEY_AUTO_FOCUS = "preferences_auto_focus";

    /**
     * 反向扫描：针对黑色背景上的白色条形码，但不一定有效，具体参考CameraConfigurationManager.setDesiredCameraParameters
     */
    public static final String KEY_INVERT_SCAN = "preferences_invert_scan";

    /**
     * 搜索国家
     */
    public static final String KEY_SEARCH_COUNTRY = "preferences_search_country";

    /**
     * 没有持续关注（只使用标准对焦模式）,具体参考CameraConfigurationManager.setDesiredCameraParameters()方法中对应逻辑
     */
    public static final String KEY_DISABLE_CONTINUOUS_FOCUS = "preferences_disable_continuous_focus";


}
