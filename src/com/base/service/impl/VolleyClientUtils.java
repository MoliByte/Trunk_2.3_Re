package com.base.service.impl;

import java.security.MessageDigest;

import org.json.JSONObject;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.RequestParams;

public class VolleyClientUtils {
	private final static String TAG = "VolleyClientUtils--:" ;
	
	//服务URL前缀
	public static final String BASE_URL = "http://services.shobserver.com";
	
	//图片URL前缀
	public static final String IMAGE_URL = "http://www.shobserver.com/shgc/news/720_450/";
	
	//超时毫秒
	private static final int TIME_OUT = 30*1000;
	
	//请求队列
	//public static RequestQueue requestQueue  ;
	
	//请求服务标识
	private Object mTag;
	private HttpAysnTaskInterface handlerListener ; //异步任务处理接口
	
	public void jsonRequest(final Context context, final int tag, String url,
			RequestParams params, HttpAysnTaskInterface handlerListener) {
		VolleyClientUtils.this.mTag = tag;
		VolleyClientUtils.this.handlerListener = handlerListener;
		String request_url =  getAbsoluteUrl(url) + params.toString() ;
		RequestQueue requestQueue = Volley.newRequestQueue(context);
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.GET, request_url, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						VolleyClientUtils.this.handlerListener.requestComplete(
								VolleyClientUtils.this.mTag,0,null , response, true);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						VolleyClientUtils.this.handlerListener.requestComplete(
								VolleyClientUtils.this.mTag,0, null,null, false);
					}
				});
		jsonObjectRequest.setTag(mTag);
		requestQueue.add(jsonObjectRequest);
	}
	
	public static String getAbsoluteUrl(String relativeUrl) {
		String Currend_URL = BASE_URL + relativeUrl ;
		return Currend_URL;
	}
	
	public static String md5(String sourceStr) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(sourceStr.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString();
		} catch (Exception e) {

		}
		return result;
	}

}
