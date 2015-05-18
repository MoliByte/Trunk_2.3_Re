package com.app.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.GetSkinScoreEntity;
import com.app.base.entity.SkinScoreEntity;
import com.app.base.entity.SkinScoreSeriesEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class GetSkinScoreRecordService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;
	private HttpAysnResultInterface callback;
	private static String TAG="GetSkinScoreRecordService";
	public GetSkinScoreRecordService(Context context, Integer mTag,HttpAysnResultInterface callback){
		this.context=context;
		this.mTag=mTag;
		this.callback=callback;
	}

	public void doGetData(String token,String position,String skintype,String lastday){
		Log.e(TAG, "============请求颜值的Token："+token);
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String v1_post_url = context.getResources().getString(R.string.TEST_SKIN)+"?client=2";
			JSONObject param = new JSONObject();
			param.put("module", "skin_report_age_series");
			param.put("position", position);
			param.put("skintype", skintype);
			param.put("lastday", lastday);
			Map<String,String> mapHead = new HashMap<String,String>();
			mapHead.put("Authorization", "Token " + token);
			
			HttpClientUtils client = new HttpClientUtils();
			StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			Log.e(TAG, "===========请求颜值参数："+param.toString());
			Log.e(TAG, "===========请求颜值TOKEN:"+token);
			Log.e(TAG, "==========="+bodyEntity.toString());
			client.post_with_head_and_body(context, mTag, v1_post_url, mapHead, bodyEntity, GetSkinScoreRecordService.this);
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void requestComplete(Object tag, int statusCode, Object header,Object result, boolean complete) {
		String resultString = result.toString().trim().replaceAll("/n", "").replaceAll("/r", "").replaceAll(" ", "");
		
		callback.dataCallBack(tag, statusCode, parse(resultString));
		
		Log.e(TAG, "============请求颜值的返回码："+statusCode);
		Log.e(TAG, "============请求颜值的返回结果22："+resultString);
	}
	//解析请求返回一周颜值的数据
	private GetSkinScoreEntity parse(String json){
		
		try {
			JSONObject jsonObject=new JSONObject(json);
			JSONObject data=jsonObject.getJSONObject("data");
			
			SkinScoreSeriesEntity skinScoreSeriesEntity=new SkinScoreSeriesEntity();
			GetSkinScoreEntity getSkinScoreEntity=new GetSkinScoreEntity();
			String position=data.getString("position");
			String age=data.getString("age");
			double age_factor=data.getDouble("age_factor");
			String suggestion=data.getString("suggestion");

			getSkinScoreEntity.setPosition(position);
			getSkinScoreEntity.setAge_factor(age_factor);
			getSkinScoreEntity.setAge(age);
			getSkinScoreEntity.setSuggestion(suggestion);
			
			JSONObject series=data.getJSONObject("series");
			
			
			ArrayList<SkinScoreEntity> u_last7daysArray=new ArrayList<SkinScoreEntity>();
			ArrayList<SkinScoreEntity> st_last7daysArray=new ArrayList<SkinScoreEntity>();
			
			
			
			if(series!=null){
				//如果对应KEY:u_last7days对应的不是"NA"则对应的是JSON对象
				String u_last7daysString=series.getString("u_last7days");
				if(!u_last7daysString.equals("0")){
					JSONObject u_last7days=series.getJSONObject("u_last7days");
					if(u_last7days!=null){
						@SuppressWarnings("unchecked")
						Iterator<String> iterator=u_last7days.keys();
						while(iterator.hasNext()){
							String tem=iterator.next();
							u_last7daysArray.add(new SkinScoreEntity(tem,u_last7days.getDouble(tem)));
						}
					}
				}
				
				Collections.sort(u_last7daysArray);
				
				//如果对应KEY:st_last7days对应的不是"NA"则对应的是JSON对象
				String st_last7daysString=series.getString("st_last7days");
				if(!st_last7daysString.equals("0")){
					JSONObject st_last7days=series.getJSONObject("st_last7days");
					if(st_last7days!=null){
						@SuppressWarnings("unchecked")
						Iterator<String> iteratorStandard=st_last7days.keys();
						while(iteratorStandard.hasNext()){
							String tem=iteratorStandard.next();
							st_last7daysArray.add(new SkinScoreEntity(tem, st_last7days.getDouble(tem)));
						}
					}
				}
				
				
				Collections.sort(st_last7daysArray);
			}
			skinScoreSeriesEntity.setSt_last7days(st_last7daysArray);
			skinScoreSeriesEntity.setU_last7days(u_last7daysArray);
			getSkinScoreEntity.setSeries(skinScoreSeriesEntity);
			Log.e(TAG, "============解析颜值结束");
			return getSkinScoreEntity;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "============解析颜值异常");
		}
		return null;
	}
}
