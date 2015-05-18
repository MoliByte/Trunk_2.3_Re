package com.idongler.widgets;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.view.ViewGroup;

import com.base.app.utils.DensityUtil;

/**
 * Created by fangjilue on 14-5-8.
 */
public class CustomDialog extends Dialog {
    Context context;

    public CustomDialog(Context context) {
        super(context);
        this.context = context;
        /*if (context instanceof Activity) {
            this.setOwnerActivity((Activity) context);
        }*/
    }

    /**
     * 自定义主题的构造方法
     *
     * @param context
     * @param theme
     */
    public CustomDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        /*if (context instanceof Activity) {
            this.setOwnerActivity((Activity) context);
        }*/
    }

    /**
     * 通过设置宽度的偏移量，来设置宽度。
     * 全屏-偏移量＝实现宽度；高度为包裹内容
     * @param wdp
     * @return
     */
    public CustomDialog setOffsetWidthWrapContentHeight(int wdp) {
        Point point = DensityUtil.getDisplaySize(getWindow().getWindowManager().getDefaultDisplay());
        int width = point.x - DensityUtil.dip2px(context, wdp);
        // 这句话起全屏的作用
        getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        return this;
    }

    /**
     * 设置对话框宽，高（ViewGroup.LayoutParams.WRAP_CONTENT）
     * @param w
     * @param h
     * @return
     */
    public CustomDialog setLayoutWidthHeight(int w, int h) {
        // 这句话起全屏的作用
        getWindow().setLayout(w,h);
        return this;
    }


    /**
     * 按回退键或外边框
     * 设置对话框是否可以取消()
     * @param cancel
     * @return
     */
    public CustomDialog setCancel(boolean cancel){
        super.setCancelable(cancel);
        super.setCanceledOnTouchOutside(cancel);
        return this;
    }
}

