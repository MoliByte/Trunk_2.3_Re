package com.app.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.ArticleCommentEntity;
import com.app.base.entity.ArticleDiscussEntity;
import com.app.base.entity.ArticleReplayEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
import com.loopj.android.http.RequestParams;
/**
 * 获取文章 无用废弃
 * @author zhup
 *
 */
public class ArticleAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "ArticleAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
//	public ArticleAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
//		this.context = context;
//		this.mTag = mTag;
//		this.callback = callback;
//	}

	public void doArticleList(long id,int pageIndex) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			Long times = System.currentTimeMillis();
			String v1_post_url = context.getResources().getString(R.string.v1_postComment)+"?client=2";
			RequestParams params = new RequestParams();
			params.put("times", times);
			params.put("post_id", id);
			params.put("page", pageIndex);
			HttpClientUtils client = new HttpClientUtils();
			
			client.get(context, mTag,v1_post_url,params, ArticleAsynTaskService.this);
		} catch (Exception e) {
			Log.e(TAG, "doArticleList error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object result, boolean complete) {
		if(null != this.callback){
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
			
			JSONObject obj = new JSONObject(result.toString()) ;
			entity.setId(obj.optLong("id"));
			entity.setAuthor_id(obj.optLong("author_id"));
			entity.setAuthor_nick(obj.optString("author_nick"));
			entity.setTitle(obj.optString("title"));
			entity.setContent(obj.optString("content"));
			entity.setView_count(obj.optInt("view_count"));
			entity.setComment_count(obj.optInt("comment_count"));
			entity.setPublic_time(obj.optString("public_time"));
			entity.setCreate_time(obj.optString("create_time"));
			
			//获取文章的评论
			JSONArray comments = obj.optJSONArray("comments");
			int initcomments_size  = comments == null ?0:comments.length() ;
			
			ArrayList<ArticleCommentEntity> commentList = new ArrayList<ArticleCommentEntity>(initcomments_size);
			for (int i = 0; i < initcomments_size; i++) {
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
				
				
				JSONArray replys = obj_.optJSONArray("replys");
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
				commentList.add(comment);
				replay_list = null ;
				comment = null ;
			}
			entity.setCommentList(commentList);
			
			return entity ;
		} catch (Exception e) {
			return null ;
		}
	}
}
