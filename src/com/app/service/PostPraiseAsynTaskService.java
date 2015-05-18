package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
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
 *文章点赞
 * @author zhup
 *
 */
public class PostPraiseAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "PostPraiseAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public PostPraiseAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void doPostArticlePraise(long post_id,String token) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			Long times = System.currentTimeMillis();
			String v1_post_praise = context.getResources().getString(R.string.v1_post_praise)+"?times="+times+"&client=2";
			JSONObject param = new JSONObject() ;
			
			param.put("post_id", post_id);
			
			Map<String,String> mapHead = new HashMap<String,String>() ;
			mapHead.put("Authorization", "Token " + token);
			
			HttpClientUtils client = new HttpClientUtils();
			StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			Log.e(TAG, ">>>>"+HttpClientUtils.BASE_URL+v1_post_praise);
			Log.e(TAG, "param>>>>"+param.toString());
			client.post_with_head_and_body(context, mTag,v1_post_praise,mapHead, bodyEntity,PostPraiseAsynTaskService.this);
		} catch (Exception e) {
			Log.e(TAG, "v1_post_praise error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object result, boolean complete) {
		if(null != this.callback){
			Log.e(TAG, "doPostPraiseComment>>>>"+statusCode+"body ="+result.toString());
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
			JSONObject obj = new JSONObject(result.toString());
			
			return obj.optInt("code") ;
		} catch (Exception e) {
			return -1 ;
		}
	}
}
