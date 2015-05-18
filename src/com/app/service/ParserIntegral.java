package com.app.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.custom.view.IntegralToast;

import android.content.Context;

public class ParserIntegral {
	private Context context;
	
	
	public ParserIntegral(Context context) {
		super();
		this.context = context;
	}


	public void parse(String json){
		try {
			JSONObject jsonObject=new JSONObject(json);
			int num=jsonObject.getInt("integral_change");
			new IntegralToast(context, num).show();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
