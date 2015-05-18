package com.beabox.hjy.tt.main.skintest.component;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.base.app.utils.DensityUtil;
import com.base.app.utils.StringUtil;
import com.beabox.hjy.tt.R;

/**
 * 颜值比较弹出框
 * Created by fangjilue on 14-9-20.
 */
public class ComparePopupWindow implements OnClickListener {

    public static interface ConfirmListener {
        void onConfirm(ComparePopupWindow comparePopupWindow);
        void onClosed();
    }

    private ConfirmListener confirmListener;
    private PopupWindow popupWindow;
    private Activity context;
    
    private String TAG="ComparePopupWindow";

    public ComparePopupWindow() {

    }

    public void show(final Activity context, final int testPreDiff, final int testScore) {
        this.context = context;

        if (popupWindow == null) {
            //int width = DensityUtil.dip2px(context, 100);
            //int height = DensityUtil.dip2px(context, 100);

            Rect frame = new Rect();
            context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;
            int width = frame.width();
            int height = frame.height();

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.compare_popup, null);

            initViews(view, testPreDiff, testScore);

            popupWindow = new PopupWindow(view, width, height);
            popupWindow.setFocusable(true);
        } else {
            changeDiffValue(popupWindow.getContentView(), testPreDiff, testScore);
        }
        View rootView = context.getWindow().getDecorView().findViewById(android.R.id.content);
        //popupWindow.setAnimationStyle(R.style.DialogAnimation);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

    }

    public void dismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    public void destory() {
        context = null;
        popupWindow = null;
        confirmListener = null;
    }

    private void initViews(View rootView, int testPreDiff, int testScore) {
        rootView.findViewById(R.id.closeBtn).setOnClickListener(this);
        rootView.findViewById(R.id.openBtn).setOnClickListener(this);

        changeDiffValue(rootView, testPreDiff, testScore);
    }

    private void changeDiffValue(View rootView, int testPreDiff, int testScore) {
        //首选第一个
        ImageView ctImage = (ImageView) rootView.findViewById(R.id.ctImage);
        String resName = null;
        if (testPreDiff == 0) {
            resName = "ct_same_a";
        } else if (testPreDiff > 0) {
            resName = "ct_up_a";
        } else {
            resName = "ct_down_a";
        }

        int resId = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
        if (resId != -1) {
        	if(ctImage==null){
        		Log.e(TAG, "============ctImage为空");
        	}else{
        		 ctImage.setImageDrawable(context.getResources().getDrawable(resId));
        	}
        }

        //文本选一个
        TextView valueTxt = (TextView) rootView.findViewById(R.id.valueTxt);
        Button openBtn = (Button) rootView.findViewById(R.id.openBtn);
        Resources res = context.getResources();
        String value = null;
        String btnTxt = null;
        int absDiff = Math.abs(testPreDiff);

        if (testPreDiff == 0) {
            absDiff = Math.abs(testScore);
            value = res.getString(R.string.skintest_value_same, absDiff);
            btnTxt = res.getString(R.string.skintest_value_same_btn);
        } else if (testPreDiff > 0) {
            value = res.getString(R.string.skintest_value_up, absDiff);
            btnTxt = res.getString(R.string.skintest_value_up_btn);
        } else {
            value = res.getString(R.string.skintest_value_down, absDiff);
            btnTxt = res.getString(R.string.skintest_value_down_btn);
        }
        openBtn.setText(btnTxt);

        if (!StringUtil.isBlank(value)) {

            int index = value.indexOf(String.valueOf(absDiff));
            if (index == -1) {
                valueTxt.setText(value);
            } else {
                int sp18 = DensityUtil.sp2px(context, 36);
                int end = index + String.valueOf(absDiff).length();

                //创建一个 SpannableString对象
                SpannableString msp = new SpannableString(value);

                //设置字体(default,default-bold,monospace,serif,sans-serif)

                //设置字体大小（绝对值,单位：像素）
                msp.setSpan(new AbsoluteSizeSpan(sp18), index, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                //设置字体大小（相对值,单位：像素） 参数表示为默认字体大小的多少倍
                //msp.setSpan(new RelativeSizeSpan(2.0f), 10, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //2.0f表示默认字体大小的两倍

                //设置字体前景色
                msp.setSpan(new ForegroundColorSpan(Color.parseColor("#fff4bd3f")), index, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                valueTxt.setText(msp);
            }
        }
    }

    public void setConfirmListener(ConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openBtn:
                if (confirmListener != null) {
                    confirmListener.onConfirm(this);
                }
                break;
            case R.id.closeBtn:
                dismiss();
                if (confirmListener != null) {
                    confirmListener.onClosed();
                }
                break;
        }
    }
}
