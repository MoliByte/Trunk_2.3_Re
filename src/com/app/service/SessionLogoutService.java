package com.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.RegionsEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
import com.loopj.android.http.RequestParams;
/**
 * 注销当前用户
 * @author zhupei
 *
 */
public class SessionLogoutService implements HttpAysnTaskInterface{
	
	private final static String TAG = "SessionLogoutService-->";
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public SessionLogoutService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void doLogout(String token) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String v1_session = context.getResources().getString(R.string.v1_session)+"?client=2";
			HttpClientUtils client = new HttpClientUtils();
			Map<String,String> headermap = new HashMap<String,String>() ;
			RequestParams params = new RequestParams();
			headermap.put("Authorization", "Token " + token);
			Log.e(TAG, "doLogout------->"+HttpClientUtils.BASE_URL+v1_session);
			Log.e(TAG, "token ------->"+token);
			client.delete(context, mTag, v1_session,
					headermap, params,SessionLogoutService.this);
		} catch (Exception e) {
			Log.e(TAG, "doLogout error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object body, boolean complete) {
		if(null != this.callback){
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(header)) ;
		}
	}
	
	/**
	 * @param result
	 * @return
	 */
	public ArrayList<RegionsEntity> parseJsonObject(Object body){
		try {
			ArrayList<RegionsEntity> list = new ArrayList<RegionsEntity>();
			return list ;
		} catch (Exception e) {
			return null ;
		}
	}
}
