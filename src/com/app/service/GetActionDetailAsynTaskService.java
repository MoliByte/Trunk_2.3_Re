package com.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.ActionCommentEntity;
import com.app.base.entity.ActionDiscussEntity;
import com.app.base.entity.ActionReplayEntity;
import com.app.base.entity.UserEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
/**
 * 获取文章详情以及评论任务
 * @author zhup
 *
 */
public class GetActionDetailAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "GetActionDetailAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public GetActionDetailAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	/**
	 * 获取文章详情以及评论列表
	 * @param id
	 * @param pageIndex
	 */
	public void doActionAndCommentsList(long id,int pageIndex,String Token) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			Long times = System.currentTimeMillis();
			String v1_post_url = context.getResources().getString(R.string.v1_activity_detail)+"/"+id+"?client=2&times="+times;
			Map<String,String> map_head = new HashMap<String,String>();
			map_head.put("Authorization", "Token " + Token);
			HttpClientUtils client = new HttpClientUtils();
			Log.e(TAG, "=========="+HttpClientUtils.BASE_URL+v1_post_url+"  Token: "+Token);
			client.get_with_head(context, mTag,v1_post_url,map_head, GetActionDetailAsynTaskService.this);
		} catch (Exception e) {
			Log.e(TAG, "doActionAndCommentsList error:" + e.toString());
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
	public ActionDiscussEntity parseJsonObject(Object result){
		try {
			ActionDiscussEntity entity = new ActionDiscussEntity();
			Map<Integer,String> join_users = new HashMap<Integer,String>();
			JSONObject obj = new JSONObject(result.toString()) ;
			entity.setId(obj.optLong("id"));
			entity.setAuthor_id(obj.optLong("author_id"));
			entity.setAuthor_nick(obj.optString("author_nick"));
			entity.setTitle(obj.optString("title"));
			entity.setImgURL(obj.optString("imgURL"));
			entity.setContent(obj.optString("content"));
			entity.setView_count(obj.optInt("view_count"));
			entity.setComment_count(obj.optInt("comment_count"));
			entity.setPublic_time(obj.optString("public_time"));
			entity.setCreate_time(obj.optString("create_time"));
			entity.setEnd_time(obj.optString("end_time"));
			entity.setPraise_count(obj.optInt("praise_count"));
			entity.setPraise_status(obj.optInt("praise_status"));

			
			
			JSONArray  users = obj.optJSONArray("prize_user");
			ArrayList<UserEntity> prize_users = new ArrayList<UserEntity>();
			if(users != null ){
				for (int i = 0; i < users.length(); i++) {
					UserEntity entity_ = new UserEntity();
					entity_.setU_id(users.getJSONObject(i).optInt("uid"));
					entity_.setNiakname(users.getJSONObject(i).optString("nickname"));
					prize_users.add(entity_);
				}
			}
			entity.setPrize_user(prize_users);//获奖用户名单
			
			//获取活动的评论
			JSONArray comments = obj.optJSONArray("comments");
			int initcomments_size  = comments == null ?0:comments.length() ;
			
			ArrayList<ActionCommentEntity> commentList = new ArrayList<ActionCommentEntity>(initcomments_size);
			for (int i = 0; i < initcomments_size; i++) {
				ActionCommentEntity comment = new ActionCommentEntity();
				JSONObject obj_ = comments.getJSONObject(i);
				comment.setId(obj_.optLong("id"));
				comment.setPost_id(obj_.optLong("post_id"));
				comment.setParent_id(obj_.optLong("parent_id"));
				comment.setTo_uid(obj_.optLong("to_uid"));
				comment.setFrom_uid(obj_.optLong("from_uid"));
				comment.setTo_nick(obj_.optString("to_nick"));
				comment.setFrom_nick(obj_.optString("from_nick"));
				comment.setContent(obj_.optString("content"));
				comment.setImg(obj_.optString("img"));
				comment.setComment_count(obj_.optInt("comment_count"));
				comment.setCreate_time(obj_.optString("create_time"));
				comment.setSupport_count(obj_.optInt("praise_count"));
				
				comment.setPraise_count(obj_.optInt("praise_count"));
				comment.setPraise_status(obj_.optInt("praise_status"));
				
				join_users.put(obj_.optInt("from_uid"), obj_.optString("from_nick"));
				
				comment.setIntegral(obj_.optInt("integral"));
				
				
				JSONArray replys = obj_.optJSONArray("replys");
				if(null!=replys){
					ArrayList<ActionReplayEntity> replay_list = new ArrayList<ActionReplayEntity>();
					for (int j = 0; j < replys.length(); j++) {
						ActionReplayEntity replay = new ActionReplayEntity();
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
						join_users.put(replay_.optInt("from_uid"), replay_.optString("from_nick"));
						replay_list.add(replay);
						replay = null ;
					}
					comment.setReplys(replay_list);
					replay_list = null ;
				}
				
				commentList.add(comment);
				comment = null ;
			}
			if(commentList.size() <= 0){
				commentList.clear();
				ActionCommentEntity comment = new ActionCommentEntity();
				comment.setId(-1);
				comment.setPost_id(-1);
				comment.setParent_id(-1);
				comment.setTo_uid(-1);
				comment.setFrom_uid(-1);
				comment.setTo_nick("");
				comment.setFrom_nick("");
				comment.setContent("");
				comment.setComment_count(0);
				comment.setCreate_time("");
				comment.setSupport_count(0);
				
				comment.setPraise_count(0);
				comment.setPraise_status(-2);
				commentList.add(comment);
				entity.setCommentList(commentList);
			}else{
				entity.setCommentList(commentList);
			}
			
			entity.setJoin_users(join_users);
			Log.e(TAG, "============解析完成");
			return entity ;
		} catch (Exception e) {
			return null ;
		}
	}
}
