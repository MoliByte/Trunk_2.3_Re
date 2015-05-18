package com.skinrun.trunk.facial.mask.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.avoscloud.chat.util.PathUtils;
import com.base.app.utils.ImageParamSetter;

public class MyImageLoader {
	private static String TAG="MyImageLoader";
	private static Handler handler=new Handler();
	public static void showFullImage(final Context context,final String path,final ImageView iv){
		File dir=new File(PathUtils.getChatFileDir());
		
		final File file=new File(dir, path);
		if(file!=null&&file.exists()){
			FileInputStream is;
			try {
				is = new FileInputStream(file);
				Bitmap bitmap=BitmapFactory.decodeStream(is);
				int w=bitmap.getWidth();
				int h=bitmap.getHeight();
				if(bitmap!=null&&w!=0&&h!=0){
					ImageParamSetter.setImageXFull(context, iv, h*1.0/w, 0);
					iv.setImageBitmap(bitmap);
					return;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		
		Log.e(TAG, "=============1111");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				InputStream is=null;
				FileOutputStream fos=null; 
				Log.e(TAG, "=============5555");
				try{
					// 从网络上获取图片
		            URL url = new URL(path);
		            Log.e(TAG, "=============6666");
		            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		            conn.setConnectTimeout(5000);
		            conn.setRequestMethod("GET");
		            Log.e(TAG, "=============7777");
		            conn.setDoInput(true);
		            Log.e(TAG, "=============2222");
		            if (conn.getResponseCode() == 200) {
		 
		              is = conn.getInputStream();
		              
		                final Bitmap b=BitmapFactory.decodeStream(is);
		                Log.e(TAG, "=============3333");
		                final int w=b.getWidth();
		                final int h=b.getHeight();
		                fos=new FileOutputStream(file);
		                b.compress(Bitmap.CompressFormat.PNG, 100, fos);
		                handler.post(new Runnable() {
							
							@Override
							public void run() {
								 if(b!=null&&w!=0&&h!=0){
					                	ImageParamSetter.setImageXFull(context, iv, h*1.0/w, 0);
					                	iv.setImageBitmap(b);
					                	Log.e(TAG, "=============4444");
					                }
							}
						}) ;
		               
		                
		               
		                
		            }
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					 try {
						 if(is!=null){
							 is.close();
						 }
						
					} catch (IOException e) {
						e.printStackTrace();
					}
					 
					 try {
						 if(fos!=null){
							 fos.close();
						 }
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}
