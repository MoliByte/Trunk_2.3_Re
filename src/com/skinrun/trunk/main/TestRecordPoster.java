package com.skinrun.trunk.main;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.app.base.entity.TestPostEntity;
import com.app.base.entity.UserEntity;
import com.app.service.PostTestResultService;
import com.base.app.utils.DBService;
import com.base.service.impl.HttpAysnResultInterface;
import com.google.gson.JsonObject;


public class TestRecordPoster {
	private Context context;
	private PostTestResultService service;
	private UserEntity userentity;
	private int ON_LINE=1;
	private int LOCAL=2;
	
	public interface IdPassInterface{
		void getId(int id);
	}
	//构造
	public TestRecordPoster( Context context,Integer mTag,UserEntity userentity,final IdPassInterface idPassInterface){
		this.context=context;
		this.userentity=userentity;
		
		service=new PostTestResultService(context, mTag, new HttpAysnResultInterface() {
			
			@Override
			public void dataCallBack(Object tag, int statusCode, Object result) {
				Log.e("TestRecordPoster", "=============上传测试数据返回："+statusCode+"result:  "+result.toString());
				int id=parse(result.toString());
				idPassInterface.getId(id);
			}
		});
	}
	
	private int parse(String json){
		try {
			JSONObject object=new JSONObject(json);
			JSONObject data=object.getJSONObject("data");
			
			int id=data.getInt("id");
			return id;
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}
	/*
	 *  {"code":200,"message":"success","data":{"id":157605}}

	 */
	
	private boolean JudgeNetState(){
		// 判断网络状态
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if(networkInfo==null){
			return false;
		}else{
			return true;
		}
	}
	public void postTestRecord(TestPostEntity testPostEntity){
		//网络正常情况下先上传数据库数据，再传本次数据
		if(JudgeNetState()){
			List<TestPostEntity> list=DBService.getDB().findAll(TestPostEntity.class);
			if(list!=null&&list.size()>0){
				for(int i=0;i<list.size();i++){
					service.doPostTestData(list.get(i),userentity.getToken());
					//清除缓存
					DBService.getDB().delete(list.get(i));
				}
			}
			testPostEntity.setData_flag(ON_LINE);
			service.doPostTestData(testPostEntity, userentity.getToken());
			
		}else{
			testPostEntity.setData_flag(LOCAL);
			DBService.getDB().save(testPostEntity);
			Log.e("postTestRecord", "==========保存测试记录在本地： 测试时间："+testPostEntity.getTest_time());
		}
	}		
}
