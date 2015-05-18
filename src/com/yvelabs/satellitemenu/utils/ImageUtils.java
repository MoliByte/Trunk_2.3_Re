package com.yvelabs.satellitemenu.utils;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.yvelabs.satellitemenu.SatelliteItemModel;


public class ImageUtils {
	
	/**
	 * ��������ͼ��
	 * @param context
	 * @param imageView
	 * @param itemModel
	 * @throws Exception
	 */
	public void setImage (Context context, ImageView imageView, SatelliteItemModel itemModel) throws Exception {
		if (itemModel.getImgResourceId() > 0) {
			setImage(imageView, itemModel.getImgResourceId());
		} else if (itemModel.getImgDrawable() != null) {
			setImage(imageView, itemModel.getImgDrawable());
		} else if (itemModel.getImgAssetPath() != null && itemModel.getImgAssetPath().length() > 0) {
			setImage(context, imageView, itemModel.getImgAssetPath());
		} else {
			throw new Exception("Satellite(" + itemModel.getId() + ") need a picture.");
		}
	}
	
	public void setImage (ImageView imageView, int resourceId) {
		imageView.setImageResource(resourceId);
	}
	public void setImage (ImageView imageView, Bitmap image) {
		imageView.setImageBitmap(image);
	}
	public void setImage (ImageView imageView, Drawable drawable) {
		imageView.setImageDrawable(drawable);
	}
	public void setImage (Context context, ImageView imageView, String assetPath) throws IOException {
		setImage(imageView, getImageFromAssetFile(context, assetPath));
	}
	
	/**
	 * �õ�����������ı�
	 * @param satelliteList
	 * @return
	 * @throws IOException 
	 */
	public int getLongestImage (Context context, List<SatelliteItemModel> satelliteList) throws IOException {
		int tempLongest = 0;
		for (SatelliteItemModel itemModel : satelliteList) {
			int lenght = 0;
			if (itemModel.getImgResourceId() > 0) {
				lenght = getLongestSide(context, itemModel.getImgResourceId());
			} else if (itemModel.getImgDrawable() != null) {
				lenght = getLongestSide(context, itemModel.getImgDrawable());
			} else if (itemModel.getImgAssetPath() != null & itemModel.getImgAssetPath().length() > 0) {
				lenght = getLongestSide(context, itemModel.getImgAssetPath());
			}
			
			if (lenght > tempLongest) 
				tempLongest = lenght;
			
		}
		return tempLongest;
	}
	
	/**
	 * �õ�ͼƬ������
	 * @param context
	 * @param resourceId
	 * @return
	 */
	public int getLongestSide (Context context, int resourceId) {
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resourceId);
		return bm.getHeight() > bm.getWidth() ? bm.getHeight() : bm.getWidth();
	}
	
	public int getLongestSide (Context context, String fileName) throws IOException {
		Bitmap bm = getImageFromAssetFile(context, fileName);
		return bm.getHeight() > bm.getWidth() ? bm.getHeight() : bm.getWidth();
	}
	
	public int getLongestSide (Context context, Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			Bitmap bm =  ((BitmapDrawable)drawable).getBitmap();
			return bm.getHeight() > bm.getWidth() ? bm.getHeight() : bm.getWidth();
	    }
		return 0;
	}
	
	/**
	 * �õ�ͼƬ�ĸߺͿ�
	 * @param context
	 * @param resourceId
	 * @return
	 */
	public Map<String, Integer> getImageWH (Context context, int resourceId) {
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resourceId);
		resultMap.put("WIDTH", bm.getWidth());
		resultMap.put("HEIGHT", bm.getHeight());
		return resultMap;
	}
	
	public Map<String, Integer> getImageWH (Context context, String fileName) throws IOException {
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		Bitmap bm = getImageFromAssetFile(context, fileName);
		resultMap.put("WIDTH", bm.getWidth());
		resultMap.put("HEIGHT", bm.getHeight());
		return resultMap;
	}
	
	public Map<String, Integer> getImageWH (Context context, Drawable drawable) {
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		if (drawable instanceof BitmapDrawable) {
			Bitmap bm =  ((BitmapDrawable)drawable).getBitmap();
			resultMap.put("WIDTH", bm.getWidth());
			resultMap.put("HEIGHT", bm.getHeight());
	    }
		return resultMap;
	}
	
	
	public Bitmap getImageFromAssetFile(Context context, String fileName) throws IOException {
		Bitmap image = null;
		AssetManager assetManager = context.getAssets();
		InputStream is = assetManager.open(fileName);
		image = BitmapFactory.decodeStream(is);
		is.close();
		return image;
	}
	
	
	
}
