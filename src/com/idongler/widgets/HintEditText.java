package com.idongler.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

import com.beabox.hjy.tt.R;

/**
 * Created by lidewen on 14-7-19.
 */
public class HintEditText extends EditText{

    String newhint = "提示文字";
    int hintcolor = Color.WHITE;
    int hintbackground = Color.WHITE;
    int hintsize = 20;

    public HintEditText(Context context) {
        super(context);
    }

    public HintEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.edittext);
        newhint = ta.getString(R.styleable.edittext_newhint);
        hintbackground = ta.getColor(R.styleable.edittext_hintbackground, Color.WHITE);
        hintcolor = ta.getColor(R.styleable.edittext_hintcolor, Color.WHITE);
        hintsize = ta.getInt(R.styleable.edittext_hintsize, 20);

        ta.recycle();
    }

    public HintEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.edittext);
        newhint = ta.getString(R.styleable.edittext_newhint);
        hintbackground = ta.getColor(R.styleable.edittext_hintbackground,Color.WHITE);
        hintcolor = ta.getColor(R.styleable.edittext_hintcolor,Color.WHITE);
        hintsize = ta.getInt(R.styleable.edittext_hintsize, 20);

        ta.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setTextSize(20);
        paint.setColor(Color.RED);
        canvas.drawText(newhint, 10, getHeight() / 2 + 5, paint);
        super.onDraw(canvas);
    }
}
