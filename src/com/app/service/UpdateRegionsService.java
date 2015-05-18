package com.app.service;

import java.util.ArrayList;

import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.RegionsEntity;
import com.base.app.utils.DBService;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
import com.loopj.android.http.RequestParams;
/**
 * 本地更新地区信息
 * @author zhupei
 *
 */
public class UpdateRegionsService implements HttpAysnTaskInterface{
	
	private final static String TAG = "UpdateRegionsService-->";
	private Context context;
	private Integer mTag;// Action 标签
	private HttpAysnResultInterface callback ;
	
	public UpdateRegionsService(Context context, Integer mTag,HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void doRegionUpdate(String token,String niakname) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String v1_region = context.getResources().getString(R.string.v1_region)+"?client=2";
			HttpClientUtils client = new HttpClientUtils();
			Log.e(TAG, "v1_region------->"+HttpClientUtils.BASE_URL+v1_region);
			client.get(context, mTag, v1_region,
					new RequestParams(), UpdateRegionsService.this);
		} catch (Exception e) {
			Log.e(TAG, "doRegionUpdate error:" + e.toString());
		}
	}

	@Override
	public void requestComplete(Object tag,int statusCode,Object header , Object body, boolean complete) {
		if(null != this.callback){
			this.callback.dataCallBack(tag,statusCode,parseJsonObject(header)) ;
		}
	}
	
	/**
	 * @param result
	 * @return
	 */
	public ArrayList<RegionsEntity> parseJsonObject(Object body){
		try {
			ArrayList<RegionsEntity> list = new ArrayList<RegionsEntity>();
			DBService.getDB().save(list);
			Log.e(TAG, "update region name--->"+body);
			return list ;
		} catch (Exception e) {
			return null ;
		}
	}
}
