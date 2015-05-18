package com.app.base.init;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;

import org.apache.http.HttpStatus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.baidu.mobstat.StatService;
import com.base.app.utils.StringUtil;
import com.base.app.utils.UMShareUtil;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;

public abstract class BaseActivity extends Activity implements OnClickListener,
	HttpAysnResultInterface,Serializable{
	
	public static final SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static ACache aCache ;
	private final static String TAG = "BaseActivity--:";
	public static Typeface mFace = null ;
	public  static boolean isWifiConnected = false ;
	public  static boolean isMobileConnected = false ;
	
	public static int TOAST_TIME = 1500 ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			hideTitlebar();
			aCache = ACache.get(getApplicationContext());
			//友盟统计
	        initUmeng();
		} catch (OutOfMemoryError e) {
			
		} catch (Exception e) {
			
		}
	}
	
	public void hideTitlebar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

	public int getWidthPixels() {
		// 获取手机分辨率
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int widthPixels = metrics.widthPixels;
		int heightPixels = metrics.heightPixels;

		//CommonConstant.widthPixels = widthPixels;
		//CommonConstant.heightPixels = heightPixels;
		// 获取屏幕分辨率
		Log.e(TAG, "widthPixels=" + widthPixels + "\nheightPixels="+ heightPixels);
		return widthPixels;
	}

	public abstract void setupView();

	public abstract void addListener();

	public abstract void sendMessageHandler(int messageCode);
	
	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	
	public boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	
	public boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	
	public static boolean isNeedReload(File f){
		if(f.exists()){
			return false ;
		}else{
			return true ;
		}
	}

	
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	public void gotoActivity(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_enter_from_right, R.anim.activity_exit_to_left);
    }

    public void gotoActivity(Class<? extends Activity> activityClass, Bundle bundle) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_enter_from_right, R.anim.activity_exit_to_left);
    }

    protected void gotoActivityForResult(Class<? extends Activity> activityClass, Bundle bundle, int requestCode) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.activity_enter_from_right, R.anim.activity_exit_to_left);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_enter_from_left, R.anim.activity_exit_to_right);
    }

    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        View v = getCurrentFocus();
        if (v == null) {
            return;
        }
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    protected void showMessage(String message,String btnTitle) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setNegativeButton(btnTitle, null)
                .create()
                .show();
    }

    public void showToastText(final String message) {
        //final BaseActivity _this = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppToast.toastMsgCenter(getApplicationContext(), message).show();
            }
        });
    }
    
    
    public boolean isSuccess(int statusCode){
    	if(HttpStatus.SC_CREATED == statusCode  || HttpStatus.SC_OK == statusCode){
    		return  true;
    	}else{
    		return false ;
    	}
    }
    
    public String getToken(){
    	return aCache.getAsString("Token");
    }
    
    
    public boolean isLogin(){
    	String token = aCache.getAsString("Token");
    	if(token == null || "".equals(token)){
    		return false ;
    	}else{
    		return true ;
    	}
    }
    
    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return  "当前版本"+version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    /**
     * 注销本次登录</br>
     */
    public void logout(final SHARE_MEDIA platform) {
    	UMShareUtil.getInstance().getUmsocialService().deleteOauth(this, platform, new SocializeClientListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int status, SocializeEntity entity) {
                
            }
        });
    }
    
    
    private void initUmeng(){
        MobclickAgent.setDebugMode(false);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(this);
    }
    
    /**
     * 自定义事件统计
     *
     * @param eventCode
     * @param eventName
     */
    public void logEvent(String eventCode, String eventName) {
        MobclickAgent.onEvent(this, eventCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //页面开始
        if (!StringUtil.isEmpty(getActivityName())) {
            
            MobclickAgent.onPageStart(getActivityName()); //统计页面
            MobclickAgent.onResume(this);
            StatService.onResume(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //页面结束
        if (!StringUtil.isEmpty(getActivityName())) {
            
            MobclickAgent.onPageEnd(getActivityName()); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
            MobclickAgent.onPause(this);
            StatService.onPause(this);
        }
    }
    
    abstract protected String getActivityName();

}
