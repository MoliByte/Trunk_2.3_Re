package com.base.service.impl;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.base.service.action.constant.HttpTagConstantUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpClientUtils  {
	private final static String TAG =  "HttpClientUtils--:";

	// 服务URL前缀
	public static final String BASE_URL = "http://napi.skinrun.cn";
	
	public static final String NEW_BASE_URL = "http://v2.api.skinrun.me" ;

	// 图片URL前缀
	public static final String IMAGE_URL = "http://a.skinrun.cn/images/";
	
	// 管理员图片URL前缀
	public static final String AUTHOR_IMAGE_URL = "http://a.skinrun.cn/userpic/";
	
	//用户图像地址
	public static final String USER_IMAGE_URL = "http://napi.skinrun.cn/uploads/";

	// 超时毫秒
	private static final int TIME_OUT = 30 * 1000;

	// 请求服务标识
	private Object mTag;
	private HttpAysnTaskInterface handlerListener; // 异步任务处理接口
	// private CustomDialog customDialog ;
	private static AsyncHttpClient client = new AsyncHttpClient();
	
	public static HttpClientUtils instance  ;
	
	public static HttpClientUtils getInstance(){
		if(null == instance){
			instance = new HttpClientUtils();
		}
		return instance ;
	}

	/**
	 * [Post Method]
	 * 
	 * @param context
	 * @param tag
	 * @param url
	 * @param params
	 * @param handlerListener
	 */
	public void post(final Context context, final int tag, String url,
			StringEntity params, HttpAysnTaskInterface handlerListener) {
		HttpClientUtils.this.mTag = tag;
		HttpClientUtils.this.handlerListener = handlerListener;
		client.setTimeout(TIME_OUT);
		
		if(null != params){
			params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		}
		
		client.post(context, getAbsoluteUrl(url), params, "application/json",
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable errorInfo) {
						if (HttpClientUtils.this.handlerListener != null) {
							HttpClientUtils.this.handlerListener
									.requestComplete(HttpClientUtils.this.mTag,
											statusCode, headers,
											errorInfo.toString(), false);
						}
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						if (HttpClientUtils.this.handlerListener != null) {
							HttpClientUtils.this.handlerListener
									.requestComplete(HttpClientUtils.this.mTag,
											statusCode, headers, new String(
													responseBody), true);
						}
					}
			});
	}
	
	//test
	public void post_test(final Context context, final int tag, String url,
			StringEntity params, HttpAysnTaskInterface handlerListener) {
		HttpClientUtils.this.mTag = tag;
		HttpClientUtils.this.handlerListener = handlerListener;
		client.setTimeout(TIME_OUT);
		
		if(null != params){
			params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		}
		
		client.post(context, url, params, "application/json",
				new AsyncHttpResponseHandler() {
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable errorInfo) {
				if (HttpClientUtils.this.handlerListener != null) {
					HttpClientUtils.this.handlerListener
					.requestComplete(HttpClientUtils.this.mTag,
							statusCode, headers,
							errorInfo.toString(), false);
				}
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				if (HttpClientUtils.this.handlerListener != null) {
					HttpClientUtils.this.handlerListener
					.requestComplete(HttpClientUtils.this.mTag,
							statusCode, headers, new String(
									responseBody), true);
				}
			}
		});
	}

	/**
	 * [Post Method]
	 * 
	 * @param context
	 * @param tag
	 * @param url
	 * @param params
	 * @param handlerListener
	 */
	public void post(final Context context, final int tag, String url,
			Map<String, String> paraMap, HttpAysnTaskInterface handlerListener) {
		HttpClientUtils.this.mTag = tag;
		HttpClientUtils.this.handlerListener = handlerListener;
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		// 提交两个参数及值
		if (paraMap != null && paraMap.size() != 0) {
			Set<String> keys = paraMap.keySet();
			for (String key : keys) {
				String v = paraMap.get(key);
				nvps.add(new BasicNameValuePair(key, v));
			}
		}
		// nvps.add(new BasicNameValuePair("clientType","android_waiter"));
		client.setTimeout(TIME_OUT);
		try {
			client.post(context, getAbsoluteUrl(url), new UrlEncodedFormEntity(
					nvps, "utf-8"),
					"application/x-www-form-urlencoded",
					new AsyncHttpResponseHandler() {
						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable errorInfo) {
							if (HttpClientUtils.this.handlerListener != null) {
								HttpClientUtils.this.handlerListener
										.requestComplete(
												HttpClientUtils.this.mTag,
												statusCode, headers,
												errorInfo.toString(), false);
							}
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							if (HttpClientUtils.this.handlerListener != null) {
								HttpClientUtils.this.handlerListener
										.requestComplete(
												HttpClientUtils.this.mTag,
												statusCode, headers,
												new String(responseBody), true);
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * [PUT Method]
	 * 
	 * @param context
	 * @param tag
	 * @param url
	 * @param params
	 * @param handlerListener
	 */
	
	public void put(final Context context, final int tag, String url,Map<String, String> headerMap,
			Map<String, String> paraMap, HttpAysnTaskInterface handlerListener) {
		HttpClientUtils.this.mTag = tag;
		HttpClientUtils.this.handlerListener = handlerListener;
		JSONObject jsonParams = new JSONObject();
		// 提交参数及值
		if (paraMap != null && paraMap.size() != 0) {
			Set<String> keys = paraMap.keySet();
			for (String key : keys) {
				String v = paraMap.get(key);
				try {
					jsonParams.put(key, v);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		//添加header
		if (headerMap != null && headerMap.size() != 0) {
			Set<String> keys = headerMap.keySet();
			for (String key : keys) {
				String v = headerMap.get(key);
				client.addHeader(key, v);
			}
		}
		StringEntity bodyEntity = null  ;
		try {
			bodyEntity	= new StringEntity(jsonParams.toString(),"UTF-8");
			Log.e(TAG, "验证验证码参数："+bodyEntity.toString());
		} catch (Exception e) {
			
		}
		
		if(null != bodyEntity){
			bodyEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		}
		
		
		
		client.addHeader("Content-Type", "application/json");
		// nvps.add(new BasicNameValuePair("clientType","android_waiter"));
		client.setTimeout(TIME_OUT);
		
		String finalUrl="";
		if(tag==HttpTagConstantUtils.UPLOAD_IMAGE||tag==HttpTagConstantUtils.PUBLISH_MOOD){
			finalUrl=url;
		}else{
			finalUrl=getAbsoluteUrl(url);
		}
		
		
		try {
			client.put(context, finalUrl, 
					bodyEntity,
					"application/json",
					new AsyncHttpResponseHandler() {
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable errorInfo) {
					if (HttpClientUtils.this.handlerListener != null) {
						HttpClientUtils.this.handlerListener
						.requestComplete(
								HttpClientUtils.this.mTag,
								statusCode, headers,
								errorInfo.toString(), false);
					}
				}
				
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] responseBody) {
					if (HttpClientUtils.this.handlerListener != null) {
						HttpClientUtils.this.handlerListener
						.requestComplete(
								HttpClientUtils.this.mTag,
								statusCode, headers,
								new String(responseBody), true);
					}
				}
			});
		} catch (Exception e) {
			
		}
	}
	

	/**
	 * [Post Method]
	 * 
	 * @param context
	 * @param tag
	 * @param url
	 * @param params
	 * @param handlerListener
	 */
	public void post_with_head(final Context context, final int tag,
			String url, Map<String, String> paraMap,
			HttpAysnTaskInterface handlerListener) {
		HttpClientUtils.this.mTag = tag;
		HttpClientUtils.this.handlerListener = handlerListener;
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		// 提交两个参数及值
		if (paraMap != null && paraMap.size() != 0) {
			Set<String> keys = paraMap.keySet();
			for (String key : keys) {
				String v = paraMap.get(key);
				client.addHeader(key, v); // 认证token
			}
		}

		client.setTimeout(TIME_OUT);
		try {
			client.post(context, getAbsoluteUrl(url), new UrlEncodedFormEntity(
					nvps, "utf-8"),
					"application/json",
					new AsyncHttpResponseHandler() {
						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable errorInfo) {
							if (HttpClientUtils.this.handlerListener != null) {
								HttpClientUtils.this.handlerListener
										.requestComplete(
												HttpClientUtils.this.mTag,
												statusCode, headers,
												errorInfo.toString(), false);
							}
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							/*
							 * for (int i = 0; i < headers.length; i++) { Header
							 * head = headers[i]; //Log.e(TAG,head.getName() +
							 * "," + head.getValue()); }
							 */
							if (HttpClientUtils.this.handlerListener != null) {
								HttpClientUtils.this.handlerListener
										.requestComplete(
												HttpClientUtils.this.mTag,
												statusCode, headers,
												new String(responseBody), true);
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * [Post Method]
	 * 
	 * @param context
	 * @param tag
	 * @param url
	 * @param params
	 * @param handlerListener
	 */
	public void post_with_head_and_body(final Context context, final int tag,
			String url, Map<String, String> headMap,StringEntity bodyEntity,
			HttpAysnTaskInterface handlerListener) {
		HttpClientUtils.this.mTag = tag;
		HttpClientUtils.this.handlerListener = handlerListener;
		// 提交两个参数及值
		if (headMap != null && headMap.size() != 0) {
			Set<String> keys = headMap.keySet();
			for (String key : keys) {
				String v = headMap.get(key);
				client.addHeader(key, v+""); // 认证token
			}
		}
		
		String finalUrl="";
		if(tag==HttpTagConstantUtils.UPLOAD_FACIAL_TESTNUM||tag==HttpTagConstantUtils.GET_FACIALMASK_ADVICE||tag==HttpTagConstantUtils.GET_SPORT_ADVICE
				||tag==HttpTagConstantUtils.POST_HOME_PRAISE||tag==HttpTagConstantUtils.POST_HOME_COMMENT||tag==HttpTagConstantUtils.POST_HOME_COLLECT
				||tag==HttpTagConstantUtils.GET_HOME_COLLECT||tag==HttpTagConstantUtils.GET_HOME_COMMENT||tag==HttpTagConstantUtils.GET_SELECT_COMMENT
				||tag==HttpTagConstantUtils.GET_MY_SKIN_TEST||tag==HttpTagConstantUtils.GET_MY_MASK_TEST||tag==HttpTagConstantUtils.DELETE_MY_COLLECT
				||tag==HttpTagConstantUtils.GET_PRAISE_JOINERS||tag==HttpTagConstantUtils.POST_TDCODE||tag==HttpTagConstantUtils.GET_MY_LOVED_MERCHANT
				||tag==HttpTagConstantUtils.GET_MY_LOVED_BRAND||tag==HttpTagConstantUtils.GET_MY_LOVED_PRODUCT||tag==HttpTagConstantUtils.UPLOAD_SKIN_IMAGE
				||tag==HttpTagConstantUtils.GET_MERCHANT_DETAIL||tag==HttpTagConstantUtils.CANCLE_ATTENTION_MERCHANT
				||tag==HttpTagConstantUtils.GET_SELECTED_DETAIL||tag==HttpTagConstantUtils.GET_MINE_MESSAGE||tag==HttpTagConstantUtils.DELETE_MY_MESSAGE
				||tag==HttpTagConstantUtils.GET_UNREAD_TEST_MESSAGE||tag==HttpTagConstantUtils.GET_MY_TEST_MESSAGE
				||tag==HttpTagConstantUtils.SKIN_GO_TO_HOME||tag==HttpTagConstantUtils.MY_INTEGRAL_RECORD||tag==HttpTagConstantUtils.GET_MESSAGE_NOCOUNT
				||tag==HttpTagConstantUtils.CLEAR_MESSAGE_NOCOUNT||tag==HttpTagConstantUtils.JUDGE_USERINFO){
			
			finalUrl=url;
		}else{
			finalUrl=getAbsoluteUrl(url);
		}
		if(null != bodyEntity){
			bodyEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		}
		client.setTimeout(TIME_OUT);
		try {
			client.post(context, finalUrl, bodyEntity,
					"application/json",
					new AsyncHttpResponseHandler() {
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable errorInfo) {
					if (HttpClientUtils.this.handlerListener != null) {
						HttpClientUtils.this.handlerListener
						.requestComplete(
								HttpClientUtils.this.mTag,
								statusCode, headers,
								errorInfo.toString(), false);
					}
				}
				
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] responseBody) {
					/*
					 * for (int i = 0; i < headers.length; i++) { Header
					 * head = headers[i]; //Log.e(TAG,head.getName() +
					 * "," + head.getValue()); }
					 */
					if (HttpClientUtils.this.handlerListener != null) {
						HttpClientUtils.this.handlerListener
						.requestComplete(
								HttpClientUtils.this.mTag,
								statusCode, headers,
								new String(responseBody), true);
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * [Post Method]
	 * 
	 * @param context
	 * @param tag
	 * @param url
	 * @param params
	 * @param handlerListener
	 */
	public void new_post_with_head_and_body(final Context context, final int tag,
			String url, Map<String, String> headMap,StringEntity bodyEntity,
			HttpAysnTaskInterface handlerListener) {
		HttpClientUtils.this.mTag = tag;
		HttpClientUtils.this.handlerListener = handlerListener;
		// 提交两个参数及值
		if (headMap != null && headMap.size() != 0) {
			Set<String> keys = headMap.keySet();
			for (String key : keys) {
				String v = headMap.get(key);
				client.addHeader(key, v+""); // 认证token
			}
		}
		
		if(null != bodyEntity){
			bodyEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		}
		client.setTimeout(TIME_OUT);
		try {
			client.post(context, getNewAbsoluteUrl(url), bodyEntity,
					"application/json",
					new AsyncHttpResponseHandler() {
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable errorInfo) {
					if (HttpClientUtils.this.handlerListener != null) {
						HttpClientUtils.this.handlerListener
						.requestComplete(
								HttpClientUtils.this.mTag,
								statusCode, headers,
								errorInfo.toString(), false);
					}
				}
				
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] responseBody) {
					if (HttpClientUtils.this.handlerListener != null) {
						HttpClientUtils.this.handlerListener
						.requestComplete(
								HttpClientUtils.this.mTag,
								statusCode, headers,
								new String(responseBody), true);
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void post_with_head_and_body_test(final Context context, final int tag,
			String url, Map<String, String> headMap,StringEntity bodyEntity,
			HttpAysnTaskInterface handlerListener) {
		HttpClientUtils.this.mTag = tag;
		HttpClientUtils.this.handlerListener = handlerListener;
		// 提交两个参数及值
		if (headMap != null && headMap.size() != 0) {
			Set<String> keys = headMap.keySet();
			for (String key : keys) {
				String v = headMap.get(key);
				client.addHeader(key, v+""); // 认证token
			}
		}
		
		if(null != bodyEntity){
			bodyEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		}
		client.setTimeout(TIME_OUT);
		try {
			client.post(context, url, bodyEntity,
					"application/json",
					new AsyncHttpResponseHandler() {
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable errorInfo) {
					if (HttpClientUtils.this.handlerListener != null) {
						HttpClientUtils.this.handlerListener
						.requestComplete(
								HttpClientUtils.this.mTag,
								statusCode, headers,
								errorInfo.toString(), false);
					}
				}
				
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] responseBody) {
					/*
					 * for (int i = 0; i < headers.length; i++) { Header
					 * head = headers[i]; //Log.e(TAG,head.getName() +
					 * "," + head.getValue()); }
					 */
					if (HttpClientUtils.this.handlerListener != null) {
						HttpClientUtils.this.handlerListener
						.requestComplete(
								HttpClientUtils.this.mTag,
								statusCode, headers,
								new String(responseBody), true);
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * [Get Method]
	 * 
	 * @param context
	 * @param tag
	 * @param url
	 * @param params
	 * @param handlerListener
	 */
	public void get_with_head(final Context context, final int tag, String url,
			Map<String, String> paraMap, HttpAysnTaskInterface handlerListener) {
		HttpClientUtils.this.mTag = tag;
		HttpClientUtils.this.handlerListener = handlerListener;
		// 提交两个参数及值
		if (paraMap != null && paraMap.size() != 0) {
			Set<String> keys = paraMap.keySet();
			for (String key : keys) {
				String v = paraMap.get(key);
				client.addHeader(key, v); // 认证token
			}
		}
		client.setTimeout(TIME_OUT);
		try {
			String finalUrl="";
			if(HttpTagConstantUtils.PRODUCT_NAME==tag){
				finalUrl=url;
			}else{
				finalUrl=getAbsoluteUrl(url);
			}
			
			client.get(context,finalUrl ,
					new AsyncHttpResponseHandler() {
						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable errorInfo) {
							if (HttpClientUtils.this.handlerListener != null) {
								HttpClientUtils.this.handlerListener
										.requestComplete(
												HttpClientUtils.this.mTag,
												statusCode, headers,
												errorInfo.toString(), false);
							}
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							if (HttpClientUtils.this.handlerListener != null) {
								HttpClientUtils.this.handlerListener
										.requestComplete(
												HttpClientUtils.this.mTag,
												statusCode, headers,
												new String(responseBody), true);
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * [Get Method]
	 * 
	 * @param context
	 * @param tag
	 * @param url
	 * @param params
	 * @param handlerListener
	 */
	public void get(final Context context, final int tag, String url,
			RequestParams params, HttpAysnTaskInterface handlerListener) {
		HttpClientUtils.this.mTag = tag;
		HttpClientUtils.this.handlerListener = handlerListener;
		client.get(context, getAbsoluteUrl(url), params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable errorInfo) {
						if (HttpClientUtils.this.handlerListener != null) {
							HttpClientUtils.this.handlerListener
									.requestComplete(HttpClientUtils.this.mTag,
											statusCode, headers,
											errorInfo.toString(), false);
						}
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						if (HttpClientUtils.this.handlerListener != null) {
							HttpClientUtils.this.handlerListener
									.requestComplete(HttpClientUtils.this.mTag,
											statusCode, headers, new String(
													responseBody), true);
						}
					}
				});
	}
	/**
	 * [Get Method]
	 * 
	 * @param context
	 * @param tag
	 * @param url
	 * @param params
	 * @param handlerListener
	 */
	public void new_get(final Context context, final int tag, String url,
			RequestParams params, HttpAysnTaskInterface handlerListener) {
		HttpClientUtils.this.mTag = tag;
		HttpClientUtils.this.handlerListener = handlerListener;
		client.get(context, getNewAbsoluteUrl(url), params,
				new AsyncHttpResponseHandler() {
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable errorInfo) {
				if (HttpClientUtils.this.handlerListener != null) {
					HttpClientUtils.this.handlerListener
					.requestComplete(HttpClientUtils.this.mTag,
							statusCode, headers,
							errorInfo.toString(), false);
				}
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				if (HttpClientUtils.this.handlerListener != null) {
					HttpClientUtils.this.handlerListener
					.requestComplete(HttpClientUtils.this.mTag,
							statusCode, headers, new String(
									responseBody), true);
				}
			}
		});
	}
	/**
	 * [Delete Method]
	 * 
	 * @param context
	 * @param tag
	 * @param url
	 * @param params
	 * @param handlerListener
	 */
	public void delete(final Context context, final int tag, String url,Map<String, String> headerMap,
			RequestParams params, HttpAysnTaskInterface handlerListener) {
		HttpClientUtils.this.mTag = tag;
		HttpClientUtils.this.handlerListener = handlerListener;
//		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		Header[] headers_params = new Header[1];
		//添加header
		if (headerMap != null && headerMap.size() != 0) {
			Set<String> keys = headerMap.keySet();
			for (String key : keys) {
				String v = headerMap.get(key);
				client.addHeader(key, v);
			}
		}
		client.setTimeout(TIME_OUT);
		try {
			client.delete(context, getAbsoluteUrl(url), 
//					headers_params,params,
					new AsyncHttpResponseHandler() {
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable errorInfo) {
					if (HttpClientUtils.this.handlerListener != null) {
						HttpClientUtils.this.handlerListener
						.requestComplete(
								HttpClientUtils.this.mTag,
								statusCode, headers,
								errorInfo.toString(), false);
					}
				}
				
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] responseBody) {
					if (HttpClientUtils.this.handlerListener != null) {
						HttpClientUtils.this.handlerListener
						.requestComplete(
								HttpClientUtils.this.mTag,
								statusCode, headers,
								new String(responseBody), true);
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void postXml(Context context, String url, StringEntity params,
			AsyncHttpResponseHandler responseHandler) {

		if(null != params){
			params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		}
		client.post(context,getAbsoluteUrl(url), params, "application/json", responseHandler);
	}
	
	public static void postNewXml(Context context, String url, StringEntity params,
			AsyncHttpResponseHandler responseHandler) {
		
		if(null != params){
			params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		}
		
		client.post(context,getNewAbsoluteUrl(url), params, "application/json", responseHandler);
	}

	/**
	 * @param RelativeUrl
	 * @return
	 */
	public static String getAbsoluteUrl(String relativeUrl) {
		String Currend_URL = BASE_URL + relativeUrl;
		Log.e(TAG, "Currend_URL-->" + Currend_URL);
		return Currend_URL;
	}
	
	/**
	 * 新的接口地址
	 * @param relativeUrl
	 * @return
	 */
	
	public static String getNewAbsoluteUrl(String relativeUrl) {
		String Currend_URL = NEW_BASE_URL + relativeUrl;
		Log.e(TAG, "NEW_Currend_URL-->" + Currend_URL);
		return Currend_URL;
	}

	/**
	 * @param MD5 sourceStr
	 * @return
	 */
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

	public static String getBase64(String s) {
		if (s == null) {
			return "";
		} else {
			Log.e(TAG,
					"Base64----"
							+ Base64.encodeToString(s.getBytes(),
									Base64.DEFAULT));
			return Base64.encodeToString(s.getBytes(), Base64.NO_WRAP);//no Base64.DEFAULT 超过一定长度会换行
		}
	}
	
	/**
	 * PUT Original
	 * @param context
	 * @param tag
	 * @param url
	 * @param headMap
	 * @param bodyEntity
	 * @param handlerListener
	 */
	public void httpPutOriginal(final Context context, final int tag,
			final String url, final Map<String, String> headMap,final StringEntity bodyEntity,
			HttpAysnTaskInterface handlerListener){
		HttpClientUtils.this.mTag = tag;
		HttpClientUtils.this.handlerListener = handlerListener;
		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare() ;
				try {
					HttpClient  httpclient = new DefaultHttpClient();
					HttpPut httpput = new HttpPut(HttpClientUtils.BASE_URL+url);
					if(headMap!=null){
						for(String key : headMap.keySet()){
							httpput.addHeader(key, headMap.get(key));
						}
					}
					httpput.addHeader("Content-Type", "application/json");
					httpput.setEntity(bodyEntity);
					HttpResponse response;
					response = httpclient.execute(httpput);
					int code = response.getStatusLine().getStatusCode();
					Log.e(TAG, "code = "+code+"");
					if (code == 200 || code == 201) {
						String rev = EntityUtils.toString(response.getEntity());
						HttpClientUtils.this.handlerListener
						.requestComplete(HttpClientUtils.this.mTag,
								code, response.getAllHeaders(),
								rev.toString(), true);
						
						Log.e(url+TAG, "result = "+rev);
					}else{
						HttpClientUtils.this.handlerListener
						.requestComplete(HttpClientUtils.this.mTag,
								code, response.getAllHeaders(),
								"error", false);
					}
				} catch (Exception e) {
					Log.e(TAG, "httpPutOriginal Error of " + url );
				}
				Looper.loop();
			}
		}).start();
	}
	/**
	 * GET Original
	 * @param context
	 * @param tag
	 * @param url
	 * @param headMap
	 * @param bodyEntity
	 * @param handlerListener
	 */
	public void httpGetOriginal(final Context context, final int tag,
			final String url, final Map<String, String> headMap,
			HttpAysnTaskInterface handlerListener){
		HttpClientUtils.this.mTag = tag;
		HttpClientUtils.this.handlerListener = handlerListener;
		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare() ;
				try {
					HttpClient  httpclient = new DefaultHttpClient();
					HttpGet httpget = new HttpGet(HttpClientUtils.BASE_URL+url.trim());
					if(headMap!=null){
						for(String key : headMap.keySet()){
							httpget.addHeader(key, headMap.get(key));
						}
					}
					httpget.addHeader("Content-Type", "application/json");
					HttpResponse response;
					response = httpclient.execute(httpget);
					int code = response.getStatusLine().getStatusCode();
					Log.e(TAG, "code = "+code+"");
					if (code == 200 || code == 201) {
						String rev = EntityUtils.toString(response.getEntity());
						HttpClientUtils.this.handlerListener
						.requestComplete(HttpClientUtils.this.mTag,
								code, response.getAllHeaders(),
								rev.toString(), true);
						
						Log.e("httpGetOriginal of "+url+TAG, "result = "+rev);
					}else{
						HttpClientUtils.this.handlerListener
						.requestComplete(HttpClientUtils.this.mTag,
								code, response.getAllHeaders(),
								"error", false);
					}
				} catch (Exception e) {
					Log.e(TAG, "httpGetOriginal Error of " + url + e.toString() );
				}
				Looper.loop();
			}
		}).start();
	}
	/**
	 * POST Original
	 * @param context
	 * @param tag
	 * @param url
	 * @param headMap
	 * @param bodyEntity
	 * @param handlerListener
	 */
	public void httpPostOriginal(final Context context, final int tag,
			final String url, final Map<String, String> headMap,final StringEntity bodyEntity,
			final HttpAysnTaskInterface handlerListener){
		HttpClientUtils.this.mTag = tag;
		HttpClientUtils.this.handlerListener = handlerListener;
		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare() ;
				try {
					HttpClient  httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(HttpClientUtils.BASE_URL+url);
					if(headMap!=null){
						for(String key : headMap.keySet()){
							httppost.addHeader(key, headMap.get(key));
						}
					}
					httppost.addHeader("Content-Type", "application/json");
					httppost.setEntity(bodyEntity);
					HttpResponse response;
					response = httpclient.execute(httppost);
					int code = response.getStatusLine().getStatusCode();
					Log.e(TAG, "code = "+code+"");
					if (code == 200 || code == 201) {
						String rev = EntityUtils.toString(response.getEntity());
						HttpClientUtils.this.handlerListener
						.requestComplete(HttpClientUtils.this.mTag,
								code, response.getAllHeaders(),
								rev.toString(), true);
						
						Log.e(url+TAG, "result = "+rev);
					}else{
						HttpClientUtils.this.handlerListener
						.requestComplete(HttpClientUtils.this.mTag,
								code, response.getAllHeaders(),
								"error", false);
					}
				} catch (Exception e) {
					Log.e(TAG, "httpPostOriginal Error of " + url );
				}
				Looper.loop();
			}
		}).start();
	}

}
