package com.skinrun.trunk.main;

import java.util.List;

import android.util.Log;

import com.app.base.entity.LastTimeTestRecordEntity;
import com.base.app.utils.DBService;

public class LastTestRecordSaver {
	public static void saveRecord(LastTimeTestRecordEntity entity){
		LastTimeTestRecordEntity oldEntity=getRecord(entity.getToken(),entity.getTestPart());
		
		if(oldEntity!=null){
			DBService.getDB().delete(oldEntity);
			Log.e("LastTestRecordSaver", "=========删除上次记录");
			Log.e("LastTestRecordSaver", "=========oldEntity颜值："+oldEntity.getCurrentSkinScore());
		}
		Log.e("LastTestRecordSaver", "=========entity颜色值："+entity.getCurrentSkinScore());
		DBService.getDB().save(entity);
	}
	
	
	public static LastTimeTestRecordEntity getRecord(String token,String testPart){
		List<LastTimeTestRecordEntity> list=DBService.getDB().findAllByWhere(LastTimeTestRecordEntity.class, "token='"+token+ "' and testPart='"+testPart+"'" );
		if(list!=null&&list.size()>0){
			return list.get(list.size()-1);
		}
		return null;
	}
	
	
	public static interface CallLastRecordInterface{
		void callBackLastRecord(LastTimeTestRecordEntity lastTimeTestRecordEntity);
	}
	
	
	
	
	
	/*
	public void onLineRequestLastTestRecord(final Context context,final Integer mTag,final String token,final CallLastRecordInterface callLastRecordInterface){
		//请求最后测试日期
		
		GetLastTestDateService service=new GetLastTestDateService(context, mTag, new HttpAysnResultInterface() {
			
			@Override
			public void dataCallBack(Object tag, int statusCode, Object result) {
				
				
				//请求测试记录
				
				String date=(String)result;
				if(date!=null&&!date.equals("")){
					GetUserTestRecordService recordService=new GetUserTestRecordService(context, mTag, new HttpAysnResultInterface() {
						
						@Override
						public void dataCallBack(Object tag, int statusCode, Object result) {
							ArrayList<LastTimeTestRecordEntity> records=parseJsonObject(result);
							if(records!=null&&records.size()>0){
								callLastRecordInterface.callBackLastRecord(records.get(records.size()-1));
							}						
						}
					});
					
					recordService.getUserTestRecordInfo(token, date);
				}
			}
		});
		
		service.doGetLastTestDate(token);
	}*/
	
	
	
	/*private ArrayList<LastTimeTestRecordEntity> parseJsonObject(Object result) {
		try {
			if (result != null && !result.toString().equals("")) {
				ArrayList<LastTimeTestRecordEntity> records = new ArrayList<LastTimeTestRecordEntity>();

				JSONObject jsonObject = new JSONObject(result.toString());
				JSONArray array = jsonObject.getJSONArray("response");

				if (array != null && array.length() > 0) {
					for (int i = 0; i < array.length(); i++) {
						JSONObject item = array.getJSONObject(i);

						LastTimeTestRecordEntity lastTimeTestRecordEntity = new LastTimeTestRecordEntity();

						lastTimeTestRecordEntity.setOil((float) (item.getDouble("oil")));
						lastTimeTestRecordEntity.setTestPart(item.getString("area"));
						lastTimeTestRecordEntity.setWater((float) (item.getDouble("water")));
						lastTimeTestRecordEntity.setFlexible((float) (item.getDouble("elasticity")));
						lastTimeTestRecordEntity.setTemperature(item.getInt("temperature"));
						lastTimeTestRecordEntity.setHumidity(item.getInt("humidity"));
						lastTimeTestRecordEntity.setUltraviolet(item.getString("ultraviolet"));
						
						String[] strs=item.getString("create_time").split(" ");
						if(strs!=null&&strs.length>0){
							lastTimeTestRecordEntity.setTestTime(strs[0]);
						}
						
						records.add(lastTimeTestRecordEntity);
					}

					return records;
				}

			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}*/
}
