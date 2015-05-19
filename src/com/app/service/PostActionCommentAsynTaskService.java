package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.ArticleDiscussEntity;
import com.base.app.utils.EasyLog;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
/**
 *发布活动评论
 * @author zhup
 *
 */
public class PostActionCommentAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "PostActionCommentAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public PostActionCommentAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	/**
	 * 发布活动评论
	 * @param post_id
	 * @param to_uid
	 * @param comment
	 * @param token
	 */
	public void doPostActionComment(long post_id,long to_uid ,String comment,String token,StringBuffer post_img_base64) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			Long times = System.currentTimeMillis();
			//String v1_post_url = context.getResources().getString(R.string.v1_postComment)+"?client=2&times="+times;
			String v1_post_url = context.getResources().getString(R.string.v2_api_comment)+"?client=2&times="+times;
			JSONObject param = new JSONObject();
			
			param.put("post_id", post_id);
			param.put("content", comment);
			param.put("to_uid", to_uid);
			if(post_img_base64 != null){
				param.put("upload_img", post_img_base64.toString());
			}else{
				param.put("upload_img", "");
			}
			
			
			Map<String,String> mapHead = new HashMap<String,String>() ;
			mapHead.put("Authorization", "Token " + token);
			mapHead.put("Content-Type", "application/json");
			
			HttpClientUtils client = new HttpClientUtils();
			StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			EasyLog.e(">>>>"+HttpClientUtils.NEW_BASE_URL+v1_post_url);
			EasyLog.e("param>>>>"+param.toString());
			EasyLog.e("token>>>>"+token);
			client.new_post_with_head_and_body(context, mTag,v1_post_url,mapHead, bodyEntity,PostActionCommentAsynTaskService.this);
		} catch (Exception e) {
			Log.e(TAG, "doPostActionComment error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object result, boolean complete) {
		if(null != this.callback){
			Log.e(TAG, "doPostActionComment>>>>"+statusCode+"body ="+result.toString());
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(result)) ;
			
		}
		new ParserIntegral(context).parse(result.toString());
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
}
