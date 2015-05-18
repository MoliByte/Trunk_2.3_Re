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
 * 分享后发送获取积分
 * @author zhup
 *
 */
public class PostUserShareService implements HttpAysnTaskInterface{
	
	private final static String TAG = "PostUserShareService.java-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public PostUserShareService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void post_share(String Token) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String v1_share = context.getResources().getString(R.string.v1_share)+"?client=2";
			HttpClientUtils client = new HttpClientUtils();
			Map<String,String> map = new HashMap<String,String>();
			map.put("Authorization", "Token " + Token);
			Log.e(TAG, "发送用户获取积分 ------->"+HttpClientUtils.BASE_URL+v1_share);
			client.post_with_head(context, mTag, v1_share,
					map, PostUserShareService.this);
		} catch (Exception e) {
			Log.e(TAG, "post_share error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode, Object header ,Object result, boolean complete) {
		if(null != this.callback){
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(result)) ;
		}
		new ParserIntegral(context).parse(result.toString());
	}
	
	/**
	 * @param result
	 * @return
	 */
	public Object parseJsonObject(Object result){
		try {
			Log.e(TAG, "获取积分成功---->"+result);
			JSONObject obj = new JSONObject(result.toString());
			return obj.opt("message")==null?"":obj.opt("message") ;
		} catch (Exception e) {
			return "" ;
		}
	}
}
