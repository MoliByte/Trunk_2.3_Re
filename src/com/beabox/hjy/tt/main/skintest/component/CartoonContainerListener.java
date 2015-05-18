package com.beabox.hjy.tt.main.skintest.component;

import android.content.Context;
import android.content.res.Resources;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.app.base.init.MyApplication;
import com.base.app.utils.DensityUtil;
import com.base.app.utils.KVOEvents;
import com.beabox.hjy.tt.R;

/**
 * 卡通容器监听器
 * Created by fangjilue on 14-10-6.
 */
public class CartoonContainerListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
    private GestureDetector mGestureDetector;
    private Context mContext;
    private int testPreDiff;
    private int testScore;

    //移动最小距离50dp
    private float FLING_MIN_DISTANCE = 50;
    //当前索引
    private int index = 0;
    //可切换个数
    private int size = 0;
    //是否正在切换
    private boolean isRunning = false;

    public CartoonContainerListener(Context mContext, int testPreDiff, int testScore) {
        this.mContext = mContext;
        this.testPreDiff = testPreDiff;
        this.testScore = testScore;

        String[] desc = null;
        Resources res = mContext.getResources();
        if (testPreDiff == 0) {
            desc = res.getStringArray(R.array.skintest_value_same_desc);
        } else if (testPreDiff > 0) {
            desc = res.getStringArray(R.array.skintest_value_up_desc);
        } else {
            desc = res.getStringArray(R.array.skintest_value_down_desc);
        }
        if (desc != null) {
            size = desc.length;
        }

        this.mGestureDetector = new GestureDetector(mContext, this);
        this.FLING_MIN_DISTANCE = DensityUtil.dip2px(mContext, FLING_MIN_DISTANCE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE) {
            //向左滑动,向前
            //Toast.makeText(mContext, "Fling Left", Toast.LENGTH_SHORT).show();
            switchNext();
        } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE) {
            //向右滑动,向后
            //Toast.makeText(mContext, "Fling Right", Toast.LENGTH_SHORT).show();
            switchPrevious();
        }
        return false;
    }

    private void switchPrevious() {
        if (index <= 0) {
            //循环
            index = size - 1;
            switchCartoon(index);
        } else {
            switchCartoon(--index);
        }
    }

    private void switchNext() {
        if (index >= size - 1) {
            index = 0;
            switchCartoon(index);
        } else {
            switchCartoon(++index);
        }
    }

    private void switchCartoon(int postion) {
        if (isRunning) {
            return;
        }        
        isRunning = true;
        //切换图片和文字
        MyApplication.getInstance().getKvo().fire(KVOEvents.GartoonSwitchEvents, testPreDiff, postion, testScore);
        isRunning = false;
    }
}
