package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.app.base.entity.ArticleDiscussEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
/**
 *发布文章评论
 * @author zhup
 *
 */
public class PostArticleCommentAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "PostArticleCommentAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public PostArticleCommentAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	/**
	 * 发布文章评论
	 * @param post_id
	 * @param to_uid
	 * @param comment
	 * @param token
	 */
	public void doPostArticleComment(long post_id,long to_uid ,String comment,final String token) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			Long times = System.currentTimeMillis();
			final String v1_post_url = context.getResources().getString(R.string.v1_postComment)+"?times="+times+"&client=2";
			JSONObject param = new JSONObject() ;
			
			param.put("post_id", post_id);
			param.put("content", comment);
			param.put("to_uid", to_uid);
			
			Map<String,String> mapHead = new HashMap<String,String>() ;
			mapHead.put("Authorization", "Token " + token);
			
			//HttpClientUtils client = new HttpClientUtils();
			final StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			Log.e(TAG, ">>>>"+HttpClientUtils.BASE_URL+v1_post_url);
			Log.e(TAG, "param>>>>"+param.toString());
			Log.e(TAG, "token>>>>"+token);
			//client.post_with_head_and_body(context, mTag,v1_post_url,mapHead, bodyEntity,PostArticleCommentAsynTaskService.this);
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					Looper.prepare() ;
					try {
						HttpClient  httpclient = new DefaultHttpClient();
						HttpPost httppost = new HttpPost(HttpClientUtils.BASE_URL+v1_post_url);
						httppost.addHeader("Authorization", "Token " + token);
						httppost.addHeader("Content-Type", "application/json");
						httppost.setEntity(bodyEntity);
						HttpResponse response;
						response = httpclient.execute(httppost);
						int code = response.getStatusLine().getStatusCode();
						Log.e(TAG, "code = "+code+"");
						if (code == 200 || code == 201) {
							String rev = EntityUtils.toString(response.getEntity());
							PostArticleCommentAsynTaskService.this
							.requestComplete(PostArticleCommentAsynTaskService.this.mTag,
									code, response.getAllHeaders(),
									rev.toString(), true);
							
							Log.e(TAG, "result = "+rev);
						}else{
							PostArticleCommentAsynTaskService.this
							.requestComplete(PostArticleCommentAsynTaskService.this.mTag,
									code, response.getAllHeaders(),
									"error", false);
						}
					} catch (Exception e) {
						Log.e(TAG, "httpPostOriginal Error of " + e.toString() );
					}
					Looper.loop();
				}
			}).start();
			
		} catch (Exception e) {
			Log.e(TAG, "doPostArticleComment error:" + e.toString());
		}
	}
	/**
	 * 发布评论
	 * @param post_id
	 * @param to_uid
	 * @param comment
	 * @param token
	 */
	public void doPostComment(long post_id ,long parent_id,long to_uid ,String comment,final String token) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			Long times = System.currentTimeMillis();
			final String v1_post_url = context.getResources().getString(R.string.v1_postComment)+"?times="+times;
			JSONObject param = new JSONObject() ;
			
			param.put("post_id", post_id);
			param.put("parent_id", parent_id);
			param.put("content", comment);
			param.put("to_uid", to_uid);
			
			Map<String,String> mapHead = new HashMap<String,String>() ;
			mapHead.put("Authorization", "Token " + token);
			
			HttpClientUtils client = new HttpClientUtils();
			final StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			Log.e(TAG, ">>>>"+HttpClientUtils.BASE_URL+v1_post_url);
			Log.e(TAG, "param>>>>"+param.toString());
			client.post_with_head_and_body(context, mTag,v1_post_url,mapHead, bodyEntity,PostArticleCommentAsynTaskService.this);
			/***
			new Thread(new Runnable() {
				@Override
				public void run() {
					Looper.prepare() ;
					try {
						HttpClient  httpclient = new DefaultHttpClient();
						HttpPost httppost = new HttpPost(HttpClientUtils.BASE_URL+v1_post_url);
						httppost.addHeader("Authorization", "Token " + token);
						httppost.addHeader("Content-Type", "application/json");
						httppost.setEntity(bodyEntity);
						HttpResponse response;
						response = httpclient.execute(httppost);
						int code = response.getStatusLine().getStatusCode();
						Log.e(TAG, "code = "+code+"");
						if (code == 200 || code == 201) {
							String rev = EntityUtils.toString(response.getEntity());
							PostArticleCommentAsynTaskService.this
							.requestComplete(PostArticleCommentAsynTaskService.this.mTag,
									code, response.getAllHeaders(),
									rev.toString(), true);
							
							Log.e(TAG, "result = "+rev);
						}else{
							PostArticleCommentAsynTaskService.this
							.requestComplete(PostArticleCommentAsynTaskService.this.mTag,
									code, response.getAllHeaders(),
									"error", false);
						}
					} catch (Exception e) {
						Log.e(TAG, "httpPostOriginal Error of " + e.toString() );
					}
					Looper.loop();
				}
			}).start();
			
			**/
			
		} catch (Exception e) {
			Log.e(TAG, "doPostArticleComment error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object result, boolean complete) {
		if(null != this.callback){
			Log.e(TAG, "doPostArticleComment>>>>"+statusCode+"body ="+result.toString());
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
