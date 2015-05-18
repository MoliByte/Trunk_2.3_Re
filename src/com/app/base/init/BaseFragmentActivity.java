package com.app.base.init;

import java.io.Serializable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.base.app.utils.StringUtil;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
import com.umeng.analytics.MobclickAgent;

@SuppressWarnings("serial")
public abstract class BaseFragmentActivity extends FragmentActivity implements OnClickListener,Serializable{
	private final static String TAG = "BaseFragmentActivity--:" ;
	
	public static Typeface mFace = null ;
	public static ACache mCache;
	public  static boolean isWifiConnected = false ;
	public  static boolean isMobileConnected = false ;
	
	public abstract void setupView();
	public abstract void addListener();
	public abstract void sendMessageHandler(int messageCode);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			mCache = ACache.get(this);
			isWifiConnected = isWifiConnected(this);
			isMobileConnected = isMobileConnected(this);
			//友盟统计
	        initUmeng();
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
		
		//获取屏幕分辨率
		Log.e(TAG,"widthPixels="+widthPixels+"\nheightPixels="+heightPixels);
		return widthPixels;
	}
	
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
        //final BaseFragmentActivity _this = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	AppToast.toastMsgCenter(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
