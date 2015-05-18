package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
/**
 * 获取用户收获地址
 * @author zhup
 *
 */
public class GetUserAddressService implements HttpAysnTaskInterface{
	
	private final static String TAG = "GetUserAddressService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public GetUserAddressService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void get_address(String Token) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String user_info_url = context.getResources().getString(R.string.v1_address)+"?client=2";
			HttpClientUtils client = new HttpClientUtils();
			Map<String,String> map = new HashMap<String,String>();
			map.put("Authorization", "Token " + Token);
			Log.e(TAG, "获取用户收货地址 ------->"+HttpClientUtils.BASE_URL+user_info_url);
			client.get_with_head(context, mTag, user_info_url,
					map, GetUserAddressService.this);
		} catch (Exception e) {
			Log.e(TAG, "getUserInfo error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode, Object header ,Object result, boolean complete) {
		if(null != this.callback){
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(result)) ;
		}
	}
	
	/**
	 * @param result
	 * @return
	 */
	public Object parseJsonObject(Object result){
		try {
			Log.e(TAG, "用户收货地址---->"+result);
			JSONObject obj = new JSONObject(result.toString());
			return obj.opt("address")==null?"":obj.opt("address") ;
		} catch (Exception e) {
			return "" ;
		}
	}
}
