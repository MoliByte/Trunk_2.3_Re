package com.beabox.hjy.tt.main.skintest.component;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.app.base.init.MyApplication;
import com.base.app.utils.DensityUtil;
import com.base.app.utils.KVOEvents;
import com.beabox.hjy.tt.R;

/**
 * 卡通人物组件
 * Created by fangjilue on 14-10-3.
 */
public class CartoonImageComp implements KVO.Observer {
    ViewGroup viewGroup;
    Activity context;
   
    int diswidth = 0;
    ImageView ctImage;
    
    private  String[] imgName;

    /**
     *
     * @param context
     * @param viewGroup
     * @param container
     * @param testPreDiff
     * @param index
     */
    public CartoonImageComp(Activity context, final ViewGroup viewGroup, ViewGroup container, int testPreDiff, int index) {
        this.context = context;
        this.viewGroup = viewGroup;
        this.diswidth = DensityUtil.getWindowRect(context).widthPixels - container.getPaddingRight() - container.getPaddingLeft();
        //AppLog.debug("diswidth=" + this.diswidth);
        this.ctImage = (ImageView) viewGroup.findViewById(R.id.ctImage);

        CartoonCompListener listener = new CartoonCompListener(context, viewGroup, container,OverLayTypeUtil.catonImageComp);
        this.viewGroup.setLongClickable(true);
        this.viewGroup.setOnTouchListener(listener);
        this.viewGroup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				viewGroup.setVisibility(View.GONE);
			}
		});
        initView(testPreDiff, index);

        MyApplication.getInstance().getKvo().registerObserver(KVOEvents.GartoonSwitchEvents, this);
    }

    public MarginParams getMarginParams() {
        FrameLayout.MarginLayoutParams lp = (FrameLayout.MarginLayoutParams) viewGroup.getLayoutParams();
        return new MarginParams(lp.leftMargin, lp.topMargin);
    }

    public void setMarginParams(MarginParams marginParams) {
        FrameLayout.MarginLayoutParams lp = (FrameLayout.MarginLayoutParams) viewGroup.getLayoutParams();
        lp.leftMargin = marginParams.getLeftMargin();
        lp.topMargin = marginParams.getTopMargin();

        viewGroup.setLayoutParams(lp);
    }

    private void initView(int testPreDiff, int index) {
        updateView(testPreDiff, index, true);
    }

    private void updateView(int testPreDiff, int index, boolean init) {
        Resources res = context.getResources();
        imgName = null;

        if (testPreDiff == 0) {
            imgName = res.getStringArray(R.array.skintest_value_same_img);
        } else if (testPreDiff > 0) {
            imgName = res.getStringArray(R.array.skintest_value_up_img);
        } else {
            imgName = res.getStringArray(R.array.skintest_value_down_img);
        }

        if (imgName != null && imgName.length > index) {
            String resName = imgName[index];

            int resId = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
            if (resId != -1) {
                Drawable drawable = context.getResources().getDrawable(resId);
                ctImage.setImageDrawable(drawable);
                ctImage.setTag(index);

                //特殊逻辑当ctImage 为warp_content 时，不同图像宽度不一样。
                //如果有一张小图在最右边时，需要调整一下leftMargin.
                //为什么使用drawable 的width 而没有使用ctImage 的width的原因是,
                //ctImage的width 还是上一次图像的宽度
                int w = drawable.getMinimumWidth();
                //AppLog.debug(w+"," +drawable.getMinimumWidth()+","+drawable.getIntrinsicWidth());
                MarginParams mp = getMarginParams();
                if (init) {
                    mp.setLeftMargin((diswidth - w) / 2);
                    setMarginParams(mp);
                } else {
                    if (mp.getLeftMargin() > diswidth - w) {
                        mp.setLeftMargin(diswidth - w);
                        setMarginParams(mp);
                    }
                }
            }
        }
    }
    
    
    public void setImageResourse(int index){
    	 if (imgName != null && imgName.length > index) {
             String resName = imgName[index];
             int resId = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
             if(resId!=-1){
            	 Drawable drawable = context.getResources().getDrawable(resId);
            	 ctImage.setImageDrawable(drawable);
             }
          }
    }
    
    public void setImageDrawable(int resource){
    	Drawable drawable = context.getResources().getDrawable(resource);
   	 	ctImage.setImageDrawable(drawable);
    }

    /**
     * 当前卡通图像索引
     *
     * @return
     */
    public int getCurrentIndex() {
        Integer i = (Integer) ctImage.getTag();
        return i == null ? 0 : i;
    }

    public void destroy() {
        MyApplication.getInstance().getKvo().removeObserver(KVOEvents.GartoonSwitchEvents, this);
        context = null;
        viewGroup = null;
        ctImage = null;
    }

    @Override
    public void onEvent(String event, Object... args) {
        if (context == null || context.isFinishing()) {
            return;
        }

        final Integer testPreDiff = (Integer) args[0];
        final Integer index = (Integer) args[1];
        if (KVOEvents.GartoonSwitchEvents.equals(event)) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateView(testPreDiff, index, false);
                }
            });
        }
    }
}
