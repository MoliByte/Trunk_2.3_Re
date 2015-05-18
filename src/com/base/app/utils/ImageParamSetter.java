package com.base.app.utils;

import android.content.Context;
import android.graphics.Point;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.app.base.init.MyApplication;

public class ImageParamSetter {
	private static Point point=MyApplication.screenSize;
	
	
	//线性布局可以用
	public static void setImageXFull(Context context,ImageView iv,double rate,int margin){
		LayoutParams imageParam=(LayoutParams) iv.getLayoutParams();
		int border=DensityUtil.dip2px(context, margin);
		imageParam.width=point.x-2*border;
		imageParam.height=(int)(point.x*rate);
		iv.setLayoutParams(imageParam);
		
	}
}
