package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.ArticleDiscussEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
/**
 *活动点赞
 * @author zhup
 *
 */
public class PostActionPraiseAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "PostActionPraiseAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public PostActionPraiseAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void doPostCommentPraise(long comment_id,String token) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			Long times = System.currentTimeMillis();
			String v1_comment_praise = context.getResources().getString(R.string.v1_comment_praise)+"?times="+times+"&client=2";
			JSONObject param = new JSONObject() ;
			
			param.put("comment_id", comment_id);
			
			Map<String,String> mapHead = new HashMap<String,String>() ;
			mapHead.put("Authorization", "Token " + token);
			mapHead.put("Content-Type", "application/json");
			
			HttpClientUtils client = new HttpClientUtils();
			StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			Log.e(TAG, ">>>>"+HttpClientUtils.BASE_URL+v1_comment_praise);
			Log.e(TAG, "param>>>>"+param.toString());
			Log.e(TAG, "token>>>>"+token);
			client.post_with_head_and_body(context, mTag,v1_comment_praise,mapHead, bodyEntity,PostActionPraiseAsynTaskService.this);
		} catch (Exception e) {
			Log.e(TAG, "doPostCommentPraise error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object result, boolean complete) {
		if(null != this.callback){
			Log.e(TAG, "doPostCommentPraise>>>>"+statusCode+"body ="+result.toString());
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
