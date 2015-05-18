package com.skinrun.trunk.main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.app.base.entity.HandCommentEntity;
import com.app.base.entity.HumidityEntity;
import com.app.base.entity.IndustryStandardEntity;
import com.app.base.entity.SkinCommentEntity;
import com.app.base.entity.SkinTypeEntity;
import com.app.base.entity.TemperatureEntity;
import com.app.base.entity.UvEntity;
import com.base.app.utils.DBService;

public class ReadLocalFile {
	public ReadLocalFile(Context context,int tag){
		this.context = context;
		this.tag=tag;
	}
	
	//解析标记
	private int tag;
	private Context context;
	
	public void readFile(final String fileName) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				InputStream is = null;
				ByteArrayOutputStream out=new ByteArrayOutputStream();
				try {
					is = context.getAssets().open(fileName);
					byte[] buffer = new byte[1024];
					int len = is.read(buffer, 0, 1024);
					while (len != -1) {
						out.write(buffer, 0, len);
						len = is.read(buffer, 0, 1024);
					};
					out.flush();
					Parse(new String(out.toByteArray()));
					
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (is != null) {
							is.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	private void Parse(String json){
		switch(tag){
		case TestingPartUtils.TYPE_TEMP:
			parseTemp(json);
			break;
		case TestingPartUtils.TYPE_HUM:
			 parseHum(json);
			break;
		case TestingPartUtils.TYPE_UV:
			parseUv(json);
			break;
		case TestingPartUtils.TYPE_SKIN:
			parseSkinType(json);
			break;
		case TestingPartUtils.INDUSTRY_STANDARD:
			parseStandard(json);
			break;
		case TestingPartUtils.SKIN_COMMENT:
			parseSkinComment(json);
			break;
		case TestingPartUtils.HAND_COMMENT:
			parseHandComment(json);
			break;
		}
	}
	//数据库写入脸部肌肤评价
	private void parseSkinComment(String skinComment){
		try {
			JSONObject jsonObject=new JSONObject(skinComment);
			JSONArray array=jsonObject.getJSONArray("data");
			for(int i=0;i<array.length();i++){
				JSONObject item=array.getJSONObject(i);
				SkinCommentEntity entity=new SkinCommentEntity();
				entity.setId(item.getInt("id"));
				entity.setAge_factor(item.getInt("age_factor"));
				entity.setComment(item.getString("comment"));
				DBService.getDB().save(entity);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	//数据库写入手部评价
	private void parseHandComment(String handComment){
		try{
			JSONObject jsonObject=new JSONObject(handComment);
			JSONArray array=jsonObject.getJSONArray("items");
			for(int i=0;i<array.length();i++){
				JSONObject item=array.getJSONObject(i);
				HandCommentEntity entity=new HandCommentEntity();
				entity.setSid(item.getInt("sid"));
				entity.setPosition(item.getInt("position"));
				entity.setAge(item.getInt("age"));
				entity.setSuggestion(item.getString("suggestion"));
				DBService.getDB().save(entity);
			}
			
		}
		catch (JSONException e) {
			e.printStackTrace();
		}catch(Exception e){
			
		}
	}
	
	
	//数据库写入温度对照数据
	private void parseTemp(String temp){
		try {
			JSONObject jsonObject=new JSONObject(temp);
			JSONArray array=jsonObject.getJSONArray("data");
			for(int i=0;i<array.length();i++){
				JSONObject item=array.getJSONObject(i);
				TemperatureEntity entity=new TemperatureEntity();
				entity.setId(item.getInt("id"));
				entity.setAffect(item.getInt("affect"));
				entity.setMax_temp(item.getInt("max_temp"));
				entity.setMin_temp(item.getInt("min_temp"));
				DBService.getDB().save(entity);
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	//数据库写入湿度对照数据
	private void parseHum(String hum){
		try {
			JSONObject jsonObject=new JSONObject(hum);
			JSONArray array=jsonObject.getJSONArray("data");
			for(int i=0;i<array.length();i++){
				JSONObject item=array.getJSONObject(i);
				HumidityEntity entity=new HumidityEntity();
				entity.setId(item.getInt("id"));
				entity.setAffect(item.getInt("affect"));
				entity.setMax_hum(item.getInt("max_hum"));
				entity.setMin_hum(item.getInt("min_hum"));
				DBService.getDB().save(entity);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	//数据库写入光照对照数据
	private void parseUv(String uv){
		try {
			JSONObject josnObject=new JSONObject(uv);
			JSONArray array=josnObject.getJSONArray("data");
			for(int i=0;i<array.length();i++){
				JSONObject item=array.getJSONObject(i);
				UvEntity entity=new UvEntity();
				entity.setId(item.getInt("id"));
				entity.setAffect(item.getInt("affect"));
				entity.setUv(item.getString("uv"));
				DBService.getDB().save(entity);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	//数据库写入肌肤类型对照参数
	private void parseSkinType(String skin){
		try {
			JSONObject josnObject=new JSONObject(skin);
			JSONArray array=josnObject.getJSONArray("data");
			for(int i=0;i<array.length();i++){
				JSONObject item=array.getJSONObject(i);
				SkinTypeEntity entity=new SkinTypeEntity();
				entity.setAffect(item.getInt("affect"));
				entity.setId(item.getInt("id"));
				entity.setName(item.getString("name"));
				DBService.getDB().save(entity);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	//数据库写入行业标准对照数据
	private void parseStandard(String stan){
		try {
			JSONObject josnObject=new JSONObject(stan);
			JSONArray array=josnObject.getJSONArray("data");
			for(int i=0;i<array.length();i++){
				JSONObject item=array.getJSONObject(i);
				IndustryStandardEntity entity=new IndustryStandardEntity();
				entity.setId(item.getInt("id"));
				entity.setAge(item.getInt("age"));
				entity.setStandard(item.getDouble("standard"));
				DBService.getDB().save(entity);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
