package com.app.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;

import com.app.base.entity.ADInfo;
import com.base.service.impl.BaseService;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
import com.loopj.android.http.RequestParams;
/**
 * 首页广告数据service
 * @author zhup
 *
 */
public class HomeADAsynTaskService extends BaseService implements HttpAysnTaskInterface{
	
	private final static String TAG = "HomeADAsynTaskService-->";
	private final static String ROOT = "object" ;
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public HomeADAsynTaskService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void doHomeADList() {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			Long times = System.currentTimeMillis();
			String v1_advert = context.getResources().getString(R.string.v1_advert)+"?client=2";
			RequestParams params = new RequestParams();
			params.put("times", times);
			HttpClientUtils client = new HttpClientUtils();
			client.get(context, mTag, v1_advert,
					params, HomeADAsynTaskService.this);
		} catch (Exception e) {
			
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object result, boolean complete) {
		if(null != this.callback){
			if(!isSuccess(statusCode)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.ERROR_404),2000).show();
			}
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(result)) ;
		}
	}
	
	/**
	 * @param result
	 * @return
	 */
	public ArrayList<ADInfo> parseJsonObject(Object result){
		try {
			ArrayList<ADInfo> entitylist = new ArrayList<ADInfo>();
			JSONObject objBody = new JSONObject(result.toString());
			JSONArray array = objBody.optJSONArray("items");
			for (int i = 0; i < array.length(); i++) {
				ADInfo entityInfo = new ADInfo();
				JSONObject obj = array.getJSONObject(i);
				entityInfo.setId(obj.optLong("id"));
				entityInfo.setCreate_user(obj.optLong("create_user"));
				entityInfo.setImgURL(obj.optString("imgURL"));
				entityInfo.setCreate_time(obj.optString("create_date"));
				entityInfo.setBegin_date(obj.optString("begin_date"));
				entityInfo.setEnd_date(obj.optString("end_date"));
				entityInfo.setAd_link(obj.optString("ad_link"));
				entityInfo.setClick_count(obj.optInt("click_count"));
				entityInfo.setNumber(obj.optInt("number"));
				entityInfo.setTitle(obj.optString("title"));
				entityInfo.setAd_number(obj.optInt("ad_number"));
				entityInfo.setType(obj.optString("type"));
				entitylist.add(entityInfo);
				entityInfo = null ;
			}
			
			return entitylist ;
		} catch (Exception e) {
			return null ;
		}
	}
	
	
}
