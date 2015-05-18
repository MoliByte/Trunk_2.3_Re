package com.app.presenter;

import android.content.Context;
import android.util.Log;

import com.app.base.entity.UpdateInfo;
import com.app.iview.IDownloadView;
import com.base.app.utils.EasyLog;
import com.base.service.impl.HttpAysnResultInterface;
import com.beabox.hjy.tt.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class DownloadPresenter implements HttpAysnResultInterface{

	private IDownloadView downloadView;

	public DownloadPresenter(IDownloadView downloadView) {
		this.downloadView = downloadView ;
	}
	
	
	/**
	 * 下载
	 */
	public void download(Context context) {
		String download_url = context.getResources().getString(R.string.download_url);
		Ion.with(context)
        .load(download_url)
        .asJsonObject()
        .setCallback(new FutureCallback<JsonObject>() {
           @Override
            public void onCompleted(Exception e, JsonObject result) {
        	   EasyLog.e(result.toString());
        	    UpdateInfo entity = new Gson().fromJson(result.toString(), UpdateInfo.class);
        	    downloadView.download(entity);
            }
        });
		
	}

	/**
	 * 数据处理
	 */

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		try {
			
		} catch (Exception e) {
			Log.e("exception = ", e.toString());
		}
		
	}


}
