package com.app.custom.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.VideoView;

import com.app.base.init.MyApplication;

public class CustomVideoView extends VideoView {

	public CustomVideoView(Context context) {
		super(context);
	}

	public CustomVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Point p=MyApplication.screenSize;
		
		int width = getDefaultSize(p.x, widthMeasureSpec);
        int height = getDefaultSize(p.y, heightMeasureSpec);
        setMeasuredDimension(width, height);
	}
}
