package com.base.app.utils;

import java.io.File;

import com.app.base.init.AppBaseUtil;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.beabox.hjy.tt.R;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class CleanCacheTask extends AsyncTask<Void, Integer, Void> {
	private TextView tv;
	private Context context;
	private static String TAG="CleanCacheTask";
	private NiftyDialogBuilder dialogUploadImage;

	public CleanCacheTask( Context context) {
		super();
		this.context = context;
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		FileUtil fileUtil=new FileUtil(context);
		String cacheName=fileUtil.getImageStorageDirectory();
		
		File cacheDir1=new File(cacheName);
		File cacheDir2 = StorageUtils.getOwnCacheDirectory(context,
				AppBaseUtil.appName + File.separator + AppBaseUtil.imgcache);
		long length=0,killed=0;
		if(cacheDir1!=null&&cacheDir1.exists()){
			File[] files=cacheDir1.listFiles();
			for(int i=0;i<files.length;i++){
				length+=files[i].length();
			}
			Log.e(TAG, "==========计算缓存文件大小1111");
		}
		
		if(cacheDir2!=null&&cacheDir2.exists()){
			File[]files=cacheDir2.listFiles();
			for(int i=0;i<files.length;i++){
				length+=files[i].length();
			}
			Log.e(TAG, "==========计算缓存文件大小22222");
			
		}
		
		if(cacheDir1!=null&&cacheDir1.exists()){
			File[] files=cacheDir1.listFiles();
			for(int i=0;i<files.length;i++){
				files[i].delete();
				killed+=files[i].length();
				publishProgress((int)(killed/length));
			}
			Log.e(TAG, "==========清除缓存完成11111");
		}
		
		if(cacheDir2!=null&&cacheDir2.exists()){
			File[]files=cacheDir2.listFiles();
			for(int i=0;i<files.length;i++){
				
				files[i].delete();
				killed+=files[i].length();
				publishProgress((int)(killed/length));
			}
			Log.e(TAG, "==========清除缓存完成22222");
			
		}
		
		
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		int a=values[0];
		tv.setText("已经清除："+a+"%");
	}
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				tv.setText("缓存清除完成");
				dialogUploadImage.dismiss();
			}
		}, 1000);
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialogUploadImage= NiftyDialogBuilder.getInstance(context,R.layout.dialog_login_layout);
			final View cleanView = LayoutInflater.from(context).inflate(R.layout.dialog_login_view, null);
			tv= (TextView) cleanView.findViewById(R.id.loading_text);
			tv.setText("正在清除缓存");
		dialogUploadImage.withTitle(null).withMessage(null)
		.withEffect(Effectstype.Fadein).withDuration(100)
		.isCancelableOnTouchOutside(false)
		.setCustomView(cleanView, context.getApplicationContext())
		.show();
	}
	
}
