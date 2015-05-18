/**
 * 
 */
package com.idongler.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 红心带文字
 * @author fangjilue
 *
 */
public class RectangleTextView extends TextView{
    int backgroundColor = Color.RED;

    public RectangleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RectangleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RectangleTextView(Context context) {
        super(context);
    }
    
    @Override  
    protected void onDraw(Canvas canvas) {  
        
        int width = this.getWidth();
        int height = this.getHeight();
        // 创建画笔  
        Paint p = new Paint();  
        p.setColor(backgroundColor);// 设置红色  
        p.setAntiAlias(true);
        canvas.drawColor(Color.TRANSPARENT);

        int r = height / 2;

        RectF oval3 = new RectF(0, 0, width, height);// 设置个新的长方形
        canvas.drawRoundRect(oval3, r, r, p);//第二个参数是x半径，第三个参数是y半径

        super.onDraw(canvas);
    }  
}
