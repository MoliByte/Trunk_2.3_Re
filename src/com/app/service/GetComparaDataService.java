package com.app.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.ComparaDataEntity;
import com.app.base.entity.ComparaTestResultEntity;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpAysnTaskInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;

public class GetComparaDataService implements HttpAysnTaskInterface {
	private Context context;
	private Integer mTag;
	private HttpAysnResultInterface callback;
	private static final String TAG="GetComparaDataService";

	public GetComparaDataService(Context context, Integer mTag,
			HttpAysnResultInterface callback) {
		this.context = context;
		this.mTag = mTag;
		this.callback = callback;
	}

	// 请求
	public void doGetComparaData(String token, String area) {
		try {
			if(!SystemTool.checkNet(context)){
				AppToast.toastMsgCenter(context, context.getResources().getString(R.string.no_network)).show();
				return ;
			}
			String v1_post_url = context.getResources().getString(
					R.string.TEST_SKIN)+"?client=2";
			JSONObject param = new JSONObject();
			param.put("module", "skin_report_diff");
			param.put("area", area);

			Map<String, String> mapHead = new HashMap<String, String>();
			mapHead.put("Authorization", "Token " + token);

			HttpClientUtils client = new HttpClientUtils();
			StringEntity bodyEntity = new StringEntity(param.toString(),
					"UTF-8");
			
			Log.e("TAG", "=======================Token:"+token);
			Log.e("TAG", "======请求对比数据传入的参数："+param.toString());
			
			client.post_with_head_and_body(context, mTag, v1_post_url, mapHead,
					bodyEntity, GetComparaDataService.this);

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void requestComplete(Object tag, int statusCode, Object header,
			Object result, boolean complete) {
		String resultString = result.toString().trim().replaceAll("/n", "").replaceAll("/r", "").replaceAll(" ", "");
		Log.e(TAG, "=======请求对比数据返回码："+statusCode);
		Log.e(TAG, "=======请求对比数据返回结果："+resultString);
		callback.dataCallBack(tag, statusCode, parse(resultString));
		
	}

	private ComparaTestResultEntity parse(String resultString) {
		try {
			ComparaTestResultEntity resultEntity = new ComparaTestResultEntity();
			ComparaDataEntity compare_2_lastweek=new ComparaDataEntity();
			ComparaDataEntity same_age=new ComparaDataEntity();
			
			JSONObject jsonObject = new JSONObject(resultString);
			JSONObject data = jsonObject.getJSONObject("data");
			
			
			JSONObject weekCompare=data.getJSONObject("compare_2_lastweek");
			JSONObject sameAgeCompara=data.getJSONObject("same_age");
			compare_2_lastweek.setElasticity(weekCompare.getDouble("elasticity")/100);
			compare_2_lastweek.setHas_value(weekCompare.getInt("has_value"));
			compare_2_lastweek.setUid(weekCompare.getString("uid"));
			compare_2_lastweek.setWater(weekCompare.getDouble("water")/100);
			compare_2_lastweek.setOil(weekCompare.getDouble("oil")/100);
			
			same_age.setElasticity(sameAgeCompara.getDouble("elasticity")/100);
			same_age.setHas_value(sameAgeCompara.getInt("has_value"));
			same_age.setUid(sameAgeCompara.getString("uid"));
			same_age.setWater(sameAgeCompara.getDouble("water")/100);
			same_age.setOil(sameAgeCompara.getDouble("oil")/100);
			
			resultEntity.setCompare_2_lastweek(compare_2_lastweek);
			resultEntity.setSame_age(same_age);
			Log.e(TAG, "========解析对比数据结束");
			return resultEntity;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "========解析对比数据异常");
		}
		return null;
	}
}
