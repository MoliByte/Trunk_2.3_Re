package com.beabox.hjy.tt.main.skintest.component;

import com.beabox.hjy.tt.R;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * 小组件拖动监听器
 * Created by lidewen on 14-10-5.
 */
public class CartoonCompListener implements View.OnTouchListener {
    float lastX;
    float lastY;

    float rawX = 0;
    float rawY = 0;

    //可移动的范围
    int container_width = -1;
    int container_height = -1;

    ViewGroup viewGroup;
    ViewGroup container;
    
    ViewGroup ctImageGroupInner,userInfoGroupInner;
    ImageView delete_image;
    ImageView delete_sign;
    int tag;
    public CartoonCompListener(Context mContext, ViewGroup viewGroup, ViewGroup container,int tag) {
        this.viewGroup = viewGroup;
        this.container = container;
        this.tag=tag;
        switch(tag){
        case OverLayTypeUtil.catonImageComp:
            ctImageGroupInner=(ViewGroup) viewGroup.findViewById(R.id.ctImageGroupInner);
            delete_image=(ImageView)viewGroup.findViewById(R.id.delete_image);
        	break;
        case OverLayTypeUtil.UserInfoComp:
        	userInfoGroupInner=(ViewGroup)viewGroup.findViewById(R.id.userInfoGroupInner);
        	delete_sign=(ImageView)viewGroup.findViewById(R.id.ivDeleteSign);
        	break;
        }
       
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (container_width == -1) {
            container_width = container.getRight() - container.getLeft() - container.getPaddingRight() - container.getPaddingLeft();
            container_height = container.getBottom() - container.getTop() - container.getPaddingBottom() - container.getPaddingTop();
            //AppLog.debug("padding :" + container.getPaddingLeft()+","+container.getPaddingTop()+","+container.getPaddingRight()+","+container.getPaddingBottom());
           
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	switch(tag){
            	case OverLayTypeUtil.catonImageComp:
            		ctImageGroupInner.setSelected(true);
                	delete_image.setVisibility(View.VISIBLE);
            		
            		break;
            	case OverLayTypeUtil.UserInfoComp:
            		userInfoGroupInner.setSelected(true);
            		delete_sign.setVisibility(View.VISIBLE);
            		break;
            	}
            	
            	
                rawX = event.getRawX();
                rawY = event.getRawY();
                FrameLayout.MarginLayoutParams start = (FrameLayout.MarginLayoutParams) viewGroup.getLayoutParams();
                lastX = start.leftMargin;
                lastY = start.topMargin;
                break;
            case MotionEvent.ACTION_MOVE:
                float move_RawX = event.getRawX();
                float move_RawY = event.getRawY();
                //以增量移动
                lastX = lastX - (rawX - move_RawX);
                lastY = lastY - (rawY - move_RawY);
                //AppLog.debug("lastX=" + lastX + ",lastY=" + lastY);
                if (lastX < 0) {
                    lastX = 0;
                }
                if (lastX > container_width - v.getWidth()) {
                    lastX = container_width - v.getWidth();
                }

                if (lastY < 0) {
                    lastY = 0;
                }
                if (lastY > container_height - v.getHeight()) {
                    lastY = container_height - v.getHeight();
                }

                FrameLayout.MarginLayoutParams lp = (FrameLayout.MarginLayoutParams) viewGroup.getLayoutParams();
                if (lp.leftMargin == lastX && lp.topMargin == lastY) {
                    return false;
                }

                lp.leftMargin = (int) lastX;
                lp.topMargin = (int) lastY;
                viewGroup.setLayoutParams(lp);

                rawX = move_RawX;
                rawY = move_RawY;
                break;
            case MotionEvent.ACTION_UP:
            	//ctImageGroupInner.setSelected(false);
                break;
        }
        return true;
    }
}

