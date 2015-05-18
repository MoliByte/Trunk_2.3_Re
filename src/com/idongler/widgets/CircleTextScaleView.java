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
import android.widget.TextView;

import com.beabox.hjy.tt.R;

/**
 * 圆带文字带刻度
 * @author Lerry
 *
 */
public class CircleTextScaleView extends TextView{
    int mBackgroundColor = Color.RED;
    boolean stroke = false;

    int scaleColor = Color.RED;

    int start=0;
    int angle=360;

    int scaleStart=0;
    int scaleAngle=360;


    public CircleTextScaleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.circle);
        mBackgroundColor = ta.getColor(R.styleable.circle_color,Color.RED);
        scaleColor = ta.getColor(R.styleable.circle_scalecolor,Color.RED);
        stroke=ta.getBoolean(R.styleable.circle_stroke, false);

        start=ta.getInt(R.styleable.circle_start,0);
        angle=ta.getInt(R.styleable.circle_angle,360);

        scaleStart=ta.getInt(R.styleable.circle_scaleStart,0);
        scaleAngle=ta.getInt(R.styleable.circle_scaleAngle,360);

        ta.recycle();
    }

    public CircleTextScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.circle);
        mBackgroundColor = ta.getColor(R.styleable.circle_color,Color.RED);
        scaleColor = ta.getColor(R.styleable.circle_scalecolor,Color.RED);
        stroke=ta.getBoolean(R.styleable.circle_stroke, false);

        start=ta.getInt(R.styleable.circle_start,0);
        angle=ta.getInt(R.styleable.circle_angle,360);

        scaleStart=ta.getInt(R.styleable.circle_scaleStart,0);
        scaleAngle=ta.getInt(R.styleable.circle_scaleAngle,360);

        ta.recycle();
    }

    public CircleTextScaleView(Context context) {
        super(context);
    }
    
    @Override  
    protected void onDraw(Canvas canvas) {  
        
        int width = this.getWidth();
        int height = this.getHeight();
        // 创建画笔  
        Paint p = new Paint();
        if(stroke){
            p.setStyle(Paint.Style.STROKE);
            //设置画笔粗细
            p.setStrokeWidth(2);
        }
        p.setColor(mBackgroundColor);// 设置红色
        p.setAntiAlias(true);
        canvas.drawColor(Color.TRANSPARENT);
//        if(width < height){
//            canvas.drawCircle(width/2, height/2, width/2, p);// 圆
//        }else{
//            canvas.drawCircle(width/2, height/2, height/2, p);// 圆
//        }

        RectF oval2 = new RectF(2, 2, width-4, height-4);// 设置个新的长方形，扫描测量（圆的范围大小）
        canvas.drawArc(oval2, start, angle, false, p);

        p.setColor(scaleColor);
        //1.圆的范围大小 2.起始角度 3.圆心角角度 4中心(true为扇形 false为弧形) 5画笔
        canvas.drawArc(oval2, scaleStart, scaleAngle, false, p);

        super.onDraw(canvas);
    }

    public void setmBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
    }

    public void setStroke(boolean stroke) {
        this.stroke = stroke;
    }

    public void setScaleColor(int scaleColor) {
        this.scaleColor = scaleColor;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public void setScaleStart(int scaleStart) {
        this.scaleStart = scaleStart;
    }

    public void setScaleAngle(int scaleAngle) {
        this.scaleAngle = scaleAngle;
    }
}