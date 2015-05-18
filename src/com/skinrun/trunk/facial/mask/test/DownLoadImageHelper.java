package com.skinrun.trunk.facial.mask.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.base.app.utils.FileUtil;
import com.base.app.utils.ImageParamSetter;



public class DownLoadImageHelper {
	
	private static Handler handler=new Handler();
	public static void downLoadImage(final Context context,final String url,final ImageView iv) {
		final FileUtil fileUtil=new FileUtil(context);
		final String imageName = url;
		
		Bitmap bitmap=fileUtil.getBitmap(imageName);
		if(bitmap!=null&&bitmap.getWidth()!=0&&bitmap.getHeight()!=0){
			ImageParamSetter.setImageXFull(context, iv, bitmap.getHeight()*1.0/bitmap.getWidth(), 0);
			iv.setImageBitmap(bitmap);
			return;
		}
		
		
		Log.e("下载图片URL", "============URL:"+url);
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				ByteArrayOutputStream out=null;
				InputStream in =null;
				
				try {
					 out=new ByteArrayOutputStream();
					 in = new URL(url).openStream();
					byte[]buffer=new byte[1024];
					int len=in.read(buffer, 0, 1024);
					
					while(len!=-1){
						out.write(buffer, 0, len);
						len=in.read(buffer, 0, 1024);
					}
					Log.e("下载图片", "============下载图片完成1");
					
					out.flush();
					final Bitmap bitmap=BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.toByteArray().length);
					Log.e("下载图片", "============下载图片完成2");
					
					Log.e("下载图片", "============下载图片完成3");
					
					//bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
					
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							ImageParamSetter.setImageXFull(context, iv, bitmap.getHeight()*1.0/bitmap.getWidth(), 0);
							iv.setImageBitmap(bitmap);
						}
					});
					/*if(bitmap!=null){
						fileUtil.savaBitmap(imageName, bitmap, 100);
					}*/
					
					
					
					Log.e("下载图片", "============下载图片完成");
					
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					try {
						if(out!=null){
							out.close();
						}
						if(in!=null){
							in.close();
						}
						
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				
				
			}
		}).start();
	}
}
