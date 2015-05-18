package com.app.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.ArticleCommentEntity;
import com.app.base.entity.ArticleReplayEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
import com.loopj.android.http.RequestParams;
/**
 * 获取文章详情评论列表
 * @author zhup
 *
 */
public class GetArticleCommentListAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "GetArticleCommentListAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public GetArticleCommentListAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	/**
	 * 获取文章详情评论列表
	 * @param id
	 * @param pageIndex
	 */
	public void doArticleCommentsList(long id,int pageIndex) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			Long times = System.currentTimeMillis();
			String v1_post_url = context.getResources().getString(R.string.v1_postComment)+"?client=2";
			RequestParams params = new RequestParams() ;
			params.put("post_id", id);
			params.put("page", pageIndex);
			params.put("times", times);//刷新
			HttpClientUtils client = new HttpClientUtils();
			Log.e(TAG, ">>>>"+HttpClientUtils.BASE_URL+v1_post_url);
			client.get(context, mTag,v1_post_url,params, GetArticleCommentListAsynTaskService.this);
		} catch (Exception e) {
			Log.e(TAG, "GetArticleCommentListAsynTaskService error:" + e.toString());
		}
	}
	

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object result, boolean complete) {
		if(null != this.callback){
			Log.e(TAG, ">>>>"+statusCode+"body ="+result.toString());
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(result)) ;
		}
	}
	
	/**
	 * @param result
	 * @return
	 */
	public ArrayList<ArticleCommentEntity> parseJsonObject(Object result){
		try {
			JSONObject obj = new JSONObject(result.toString()) ;
			JSONArray comments = obj.optJSONArray("items");
			ArrayList<ArticleCommentEntity> commentList = new ArrayList<ArticleCommentEntity>();
			for (int i = 0; i < comments.length(); i++) {
				ArticleCommentEntity comment = new ArticleCommentEntity();
				JSONObject obj_ = comments.getJSONObject(i);
				comment.setId(obj_.optLong("id"));
				comment.setPost_id(obj_.optLong("post_id"));
				comment.setParent_id(obj_.optLong("parent_id"));
				comment.setTo_uid(obj_.optLong("to_uid"));
				comment.setFrom_uid(obj_.optLong("from_uid"));
				comment.setTo_nick(obj_.optString("to_nick"));
				comment.setFrom_nick(obj_.optString("from_nick"));
				comment.setContent(obj_.optString("content"));
				comment.setComment_count(obj_.optInt("comment_count"));
				comment.setCreate_time(obj_.optString("create_time"));
				comment.setSupport_count(obj_.optInt("praise_count"));
				
				comment.setPraise_count(obj_.optInt("praise_count"));
				comment.setPraise_status(obj_.optInt("praise_status"));
				
				comment.setIntegral(obj_.optInt("integral"));
				
				
				JSONArray replys = obj_.optJSONArray("replys");
				if(null!=replys){
					ArrayList<ArticleReplayEntity> replay_list = new ArrayList<ArticleReplayEntity>();
					for (int j = 0; j < replys.length(); j++) {
						ArticleReplayEntity replay = new ArticleReplayEntity();
						JSONObject replay_ = replys.getJSONObject(j);
						replay.setId(replay_.optLong("id"));
						replay.setPost_id(replay_.optLong("post_id"));
						replay.setParent_id(replay_.optLong("parent_id"));
						replay.setTo_uid(replay_.optLong("to_uid"));
						replay.setFrom_uid(replay_.optLong("from_uid"));
						replay.setTo_nick(replay_.optString("to_nick"));
						replay.setFrom_nick(replay_.optString("from_nick"));
						replay.setContent(replay_.optString("content"));
						replay.setComment_count(replay_.optInt("comment_count"));
						replay.setCreate_time(replay_.optString("create_time"));
						replay_list.add(replay);
						replay = null ;
					}
					comment.setReplys(replay_list);
					replay_list = null ;
				}
				
				commentList.add(comment);
				comment = null ;
			}
			
			return commentList ;
		} catch (Exception e) {
			return null ;
		}
	}
}
