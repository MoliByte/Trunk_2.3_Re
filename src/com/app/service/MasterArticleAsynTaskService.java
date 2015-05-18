package com.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.MasterArticleEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
/**
 * 获取导师文章
 * @author zhup
 *
 */
public class MasterArticleAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "MasterArticleAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public MasterArticleAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void doMasterArticle() {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String user_login_url = "/v1/session?client=2";
			HttpClientUtils client = new HttpClientUtils();
			Map<String,String> map = new HashMap<String,String>() ;
			client.post(context, mTag, user_login_url,
					map, MasterArticleAsynTaskService.this);
		} catch (Exception e) {
			Log.e(TAG, "doMasterArticle error:" + e.toString());
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
	public ArrayList<MasterArticleEntity> parseJsonObject(Object result){
		try {
			ArrayList<MasterArticleEntity> list = new ArrayList<MasterArticleEntity>();
			for (int i = 0; i <3; i++) {
				MasterArticleEntity entity = new MasterArticleEntity();
				entity.setPic_url("");
				entity.setComment_count(200);
				entity.setContent("自然衰老是每个人都要面临的问题，之于男人，这是一种成熟的积淀。但之于女人，岁月太残忍，我们真的不想要……本期《美丽不手软》有权威专家的不老秘笈和保养达人的心得手法，快跟小编一起偷师，让我们勇敢的对岁月Say No！");
				entity.setRelease_time("2014-12-19 12:47");
				entity.setSupport_count(900);
				entity.setTitle("摆脱岁月痕迹的不老秘籍");
				list.add(entity);
				entity = null ;
			}
			return list ;
		} catch (Exception e) {
			return null ;
		}
	}
}
