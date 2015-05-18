package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.kymjs.aframe.utils.StringUtils;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.ArticleDiscussEntity;
import com.app.base.init.ACache;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
/**
 * 用户反馈信息
 * @author zhup
 *
 */
public class PostFeedBackAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "PostFeedBackAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public PostFeedBackAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void doPostFeedBack(String token,String content,String qq_number,String tel_number) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			
			if(StringUtils.isEmpty(content)){
        		AppToast.toastMsgCenter(context, "请输入您宝贵的建议!",2000).show();
        		return ;
        	}
        	/*if(StringUtils.isEmpty(qq_number)){
        		AppToast.toastMsgCenter(context, "请输入您的QQ,方便联系!").show();
        		return ;
        	}
        	if(!StringUtils.isPhone(tel_number)){
        		AppToast.toastMsgCenter(context, "请填写正确的手机号码!").show();
        		return ;
        	}*/
			String v2_feedback = context.getResources().getString(R.string.v2_feedback)+"?client=2";
			JSONObject param = new JSONObject() ;
			param.put("action", "createFeedback");
			param.put("content", content);
			param.put("qq_number", qq_number);
			param.put("tel_number", tel_number);
			
			Map<String,String> mapHead = new HashMap<String,String>() ;
			mapHead.put("Authorization", "Token " + ACache.get(context).getAsString("Token"));
			
			HttpClientUtils client = new HttpClientUtils();
			StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			Log.e(TAG, ">>>>"+HttpClientUtils.BASE_URL+v2_feedback);
			Log.e(TAG, "param>>>>"+param.toString());
			client.new_post_with_head_and_body(context, mTag,v2_feedback,mapHead, bodyEntity,PostFeedBackAsynTaskService.this);
		} catch (Exception e) {
			Log.e(TAG, "doPostFeedBack error:" + e.toString());
			AppToast.toastMsgCenter(context, context.getResources().getString(R.string.ERROR_404),2000);
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object result, boolean complete) {
		if(null != this.callback){
			Log.e(TAG, "doPostFeedBack>>>>"+statusCode+"body ="+result.toString());
			if(isSuccess(statusCode)){
				AppToast.toastMsgCenter(context, "感谢您的参与，反馈成功!",2000).show();
			}else{
				AppToast.toastMsgCenter(context, "反馈失败!",2000).show();
			}
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(result)) ;
		}
	}
	
	/**
	 * @param result
	 * @return
	 */
	public ArticleDiscussEntity parseJsonObject(Object result){
		try {
			ArticleDiscussEntity entity = new ArticleDiscussEntity();
			
			return entity ;
		} catch (Exception e) {
			return null ;
		}
	}
	
	private boolean isSuccess(int statusCode){
		if(HttpStatus.SC_CREATED==statusCode || HttpStatus.SC_OK==statusCode){
			return true ;
		}
		else{
			return false ;
		}
	}
}
