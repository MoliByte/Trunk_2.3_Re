package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class GetFacialmaskAdviceService implements HttpAysnTaskInterface{
	private Context context;
	private Integer mTag;
	private HttpAysnResultInterface callback;
	private String TAG="GetFacialmaskAdviceService";
	
	public GetFacialmaskAdviceService(Context context, Integer mTag,
			HttpAysnResultInterface callback) {
		super();
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	public void doGetAdvice(String token,float c1,float c2,float c3,int productId){
		try{
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String url=context.getResources().getString(R.string.get_facialmask_advice)+"?client=2";
			Map<String,String> mapHead = new HashMap<String,String>() ;
			mapHead.put("Authorization", "Token " + token);
			
			JSONObject param = new JSONObject() ;
			param.put("Testbefore1", c1);
			param.put("Testbefore2", c2);
			param.put("Testbefore3", c3);
			param.put("productId", productId);
			
			StringEntity bodyEntity = new StringEntity(param.toString(),"UTF-8");
			
			//请求
			HttpClientUtils client=new HttpClientUtils();
			client.post_with_head_and_body(context, mTag, url, mapHead, bodyEntity, GetFacialmaskAdviceService.this);
			
		}catch(Exception e){
			
		}
	}




	@Override
	public void requestComplete(Object tag, int statusCode, Object header,
			Object result, boolean complete) {
		callback.dataCallBack(tag, statusCode, parse(result.toString()));
		Log.e(TAG, "============面膜测试建议："+result.toString());
		
		new ParserIntegral(context).parse(result.toString());
	}
	
	private String  parse(String json){
		String suggestion="";
		try {
			JSONObject jsonObject=new JSONObject(json);
			int code=jsonObject.getInt("code");
			if(code==200){
				suggestion=jsonObject.getString("items");
			}
			return suggestion;
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}catch(Exception e){
			return "";
		}
	}
}