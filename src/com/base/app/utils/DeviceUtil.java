package com.base.app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.app.base.init.MyApplication;

/**
 * all rights @idongler.com
 * User: bigknife<br />
 * Date: 13-8-23<br />
 * Time: 上午9:39<br />
 * 与设备打交道的一些工具方法
 */
public class DeviceUtil {
	/**
	 * 是否连接网络在线
	 * @return
	 */
	public static boolean isOnline(){
		ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null){
			return networkInfo.isConnected();
		}
		return false;
	}

    public static String getDeviceInfo(){
        TelephonyManager tm = (TelephonyManager) MyApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        StringBuilder sb = new StringBuilder();
        sb.append("  DeviceId(IMEI) = " + tm.getDeviceId());
        sb.append(", DeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
        sb.append(", Line1Number = " + tm.getLine1Number());
        sb.append(", NetworkCountryIso = " + tm.getNetworkCountryIso());
        sb.append(", NetworkOperator = " + tm.getNetworkOperator());
        sb.append(", NetworkOperatorName = " + tm.getNetworkOperatorName());
        sb.append(", NetworkType = " + tm.getNetworkType());
        sb.append(", PhoneType = " + tm.getPhoneType());
        sb.append(", SimCountryIso = " + tm.getSimCountryIso());
        sb.append(", SimOperator = " + tm.getSimOperator());
        sb.append(", SimOperatorName = " + tm.getSimOperatorName());
        sb.append(", SimSerialNumber = " + tm.getSimSerialNumber());
        sb.append(", SimState = " + tm.getSimState());
        sb.append(", SubscriberId(IMSI) = " + tm.getSubscriberId());
        sb.append(", VoiceMailNumber = " + tm.getVoiceMailNumber());
        return sb.toString();
    }
    
    

    public static String getDeviceId(){
        TelephonyManager tm = (TelephonyManager) MyApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public static String getDeviceName(){
        //Build.MANUFACTURER +":"+ Build.MODEL+","+Build.DEVICE+","+Build.PRODUCT
        return Build.MANUFACTURER +":"+ Build.MODEL;
    }
    
}
