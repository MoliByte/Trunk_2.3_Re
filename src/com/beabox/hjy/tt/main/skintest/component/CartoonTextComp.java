package com.beabox.hjy.tt.main.skintest.component;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.app.base.init.MyApplication;
import com.base.app.utils.DensityUtil;
import com.base.app.utils.KVOEvents;
import com.base.app.utils.StringUtil;
import com.beabox.hjy.tt.R;
/**
 * 卡通人物语句组件
 * Created by fangjilue on 14-10-3.
 */
public class CartoonTextComp implements KVO.Observer {

    ViewGroup viewGroup;
    Activity context;

    TextView valueTxt;
    TextView valueDescTxt;

    int diswidth = 0;

    /**
     *
     * @param context
     * @param viewGroup
     * @param container
     * @param testPreDiff
     * @param testScore
     * @param index
     */
    public CartoonTextComp(Activity context, ViewGroup viewGroup, ViewGroup container, int testPreDiff, int testScore, int index) {
        this.context = context;
        this.viewGroup = viewGroup;
        this.valueTxt = (TextView) viewGroup.findViewById(R.id.valueTxt);
        this.valueDescTxt = (TextView) viewGroup.findViewById(R.id.valueDescTxt);

        this.diswidth = DensityUtil.getWindowRect(context).widthPixels - container.getPaddingRight() - container.getPaddingLeft();
        //AppLog.debug("diswidth=" + this.diswidth);

        CartoonCompListener listener = new CartoonCompListener(context, viewGroup, container,OverLayTypeUtil.catonTextComp);
        this.viewGroup.setLongClickable(true);
        this.viewGroup.setOnTouchListener(listener);

        initView(testPreDiff, testScore, index);

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

    private void initView(int testPreDiff, int testScore, int index) {
        //(1)设置值
        Resources res = context.getResources();
        String value = null;
        int absDiff = Math.abs(testPreDiff);
        
        if (testPreDiff == 0&&testScore!=0) {
            absDiff = Math.abs(testScore);
            value = res.getString(R.string.skintest_value_same, absDiff);
        }else if(testPreDiff == 0&&testScore==0){
        	value=res.getString(R.string.skintest_no_test);
        }else if (testPreDiff > 0) {
            value = res.getString(R.string.skintest_value_up, absDiff);
        } else {
            value = res.getString(R.string.skintest_value_down, absDiff);
        }

        if (!StringUtil.isBlank(value)) {

            int start = value.indexOf(String.valueOf(absDiff));

            if (start == -1) {
                valueTxt.setText(value);
            } else {
                int sp18 = DensityUtil.sp2px(context, 36);
                int end = start + String.valueOf(absDiff).length();

                //创建一个 SpannableString对象
                SpannableString msp = new SpannableString(value);

                //设置字体(default,default-bold,monospace,serif,sans-serif)

                //设置字体大小（绝对值,单位：像素）
                msp.setSpan(new AbsoluteSizeSpan(sp18), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                //设置字体大小（相对值,单位：像素） 参数表示为默认字体大小的多少倍
                //msp.setSpan(new RelativeSizeSpan(2.0f), 10, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //2.0f表示默认字体大小的两倍

                //设置字体前景色
                msp.setSpan(new ForegroundColorSpan(Color.parseColor("#fff4bd3f")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                valueTxt.setText(msp);
            }
        }

        //设置描述
        setValueDescTxt(testPreDiff, testScore, index);
        //调整宽度
        setValueDescMargin();
    }

    private void setValueDescTxt(int testPreDiff, int testScore, int index) {
        Resources res = context.getResources();
        String[] desc = null;

        if (testPreDiff == 0) {
            desc = res.getStringArray(R.array.skintest_value_same_desc);
        } else if (testPreDiff > 0) {
            desc = res.getStringArray(R.array.skintest_value_up_desc);
        } else {
            desc = res.getStringArray(R.array.skintest_value_down_desc);
        }

        if (desc != null && desc.length > index) {
            String tmp = String.format(desc[index], testScore);
            valueDescTxt.setText(tmp);
            valueDescTxt.setTag(index);
        }
    }

    void setValueDescMargin() {
        //调整宽度
        Resources res = context.getResources();
        Paint textPaint = new Paint();
        textPaint.setTextSize(valueDescTxt.getTextSize());
        float txtWidth = textPaint.measureText(valueDescTxt.getText().toString());
        float maxWidth = res.getDimension(R.dimen.test_ct_value_desc_width);
        int left = 0;
        if (txtWidth < maxWidth) {
            left = new Double((diswidth - txtWidth) / 2.0).intValue();
        } else {
            left = new Double((diswidth - maxWidth) / 2.0).intValue();
        }

        FrameLayout.MarginLayoutParams lp = (FrameLayout.MarginLayoutParams) viewGroup.getLayoutParams();
        if (left > 0 && lp.leftMargin != left) {
            lp.leftMargin = left;
            viewGroup.setLayoutParams(lp);
        }
    }

    /**
     * 当前描述文本索引
     * @return
     */
    public int getCurrentIndex() {
        Integer i = (Integer) valueDescTxt.getTag();
        return i == null ? 0 : i;
    }


    public void destroy() {
        MyApplication.getInstance().getKvo().removeObserver(KVOEvents.GartoonSwitchEvents, this);
        context = null;
        viewGroup = null;
        valueTxt = null;
        valueDescTxt = null;
    }

    @Override
    public void onEvent(String event, Object... args) {
        if (context == null || context.isFinishing()) {
            return;
        }

        final Integer testPreDiff = (Integer) args[0];
        final Integer index = (Integer) args[1];
        final Integer testScore = (Integer) args[2];
        if (KVOEvents.GartoonSwitchEvents.equals(event)) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setValueDescTxt(testPreDiff, testScore, index);
                }
            });
        }
    }
}
