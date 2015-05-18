package com.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.MyMasterEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
/**
 * 获取我的导师列表
 * @author zhup
 *
 */
public class MyMasterAsynTaskService implements HttpAysnTaskInterface{
	
	private final static String TAG = "MyMasterAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public MyMasterAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void doMasterList() {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String user_login_url = "/v1/session?client=2";
			HttpClientUtils client = new HttpClientUtils();
			Map<String,String> map = new HashMap<String,String>() ;
			
			client.post_with_head(context, mTag, user_login_url,
					map, MyMasterAsynTaskService.this);
		} catch (Exception e) {
			Log.e(TAG, "doMasterList error:" + e.toString());
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
	public ArrayList<MyMasterEntity> parseJsonObject(Object result){
		try {
			ArrayList<MyMasterEntity> list = new ArrayList<MyMasterEntity>();
			for (int i = 0; i <3; i++) {
				MyMasterEntity entity = new MyMasterEntity();
				entity.setMaster_level("高级导师");
				entity.setMaster_name("小P老师");
				entity.setMaster_organization("克里斯汀上海大华店");
				entity.setMaster_pic("");
				list.add(entity);
				entity = null ;
			}
			return list ;
		} catch (Exception e) {
			return null ;
		}
	}
}
