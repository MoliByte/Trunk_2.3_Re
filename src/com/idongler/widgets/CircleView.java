/**
 *
 */
package com.idongler.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.beabox.hjy.tt.R;

/**
 * 红点
 *
 * @author fangjilue
 */
public class CircleView extends View {
    int mBackgroundColor = Color.RED;

    boolean stroke = false;

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.circle);
        mBackgroundColor = ta.getColor(R.styleable.circle_color, Color.RED);
        stroke=ta.getBoolean(R.styleable.circle_stroke,false);
        ta.recycle();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.circle);
        mBackgroundColor = ta.getColor(R.styleable.circle_color, Color.RED);
        stroke=ta.getBoolean(R.styleable.circle_stroke,false);
        ta.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();
        // 创建画笔
        Paint p = new Paint();
        if(stroke){
            p.setStrokeWidth(2);
            p.setStyle(Paint.Style.STROKE);
        }
        p.setColor(mBackgroundColor);// 设置颜色
        p.setAntiAlias(true);
        canvas.drawColor(Color.TRANSPARENT);
//        if (width < height) {
//            canvas.drawCircle(width / 2, height / 2, width / 2, p);// 圆
//        } else {
//            canvas.drawCircle(width / 2, height / 2, height / 2, p);// 圆
//        }
        RectF oval2 = new RectF(1, 1, width-2, height-2);// 设置个新的长方形，扫描测量（圆的范围大小）
        canvas.drawArc(oval2, 0, 360, false, p);
        super.onDraw(canvas);
    }

}
