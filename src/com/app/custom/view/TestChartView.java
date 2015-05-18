package com.app.custom.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.app.base.entity.Point;
import com.beabox.hjy.tt.R;

public class TestChartView extends View{
	
	private ArrayList<Point> points=new ArrayList<Point>();
	
	public ArrayList<Point> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}

	public TestChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TestChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TestChartView(Context context) {
		super(context);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);// 去锯齿
		paint.setColor(getResources().getColor(R.color.line_blue));// 颜色
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		
		Paint paint1 = new Paint();
		paint1.setAntiAlias(true);// 去锯齿
		paint1.setColor(getResources().getColor(R.color.line_blue));// 颜色
		
		if(points==null){
			return;
		}
		
		
		if(points.size()>0){
			Path path=new Path();
			path.moveTo(points.get(0).getX(), points.get(0).getY());
			canvas.drawCircle(points.get(0).getX(), points.get(0).getY(), 4, paint1);
			if(points.size()>1){
				for(int i=1;i<points.size();i++){
					canvas.drawCircle(points.get(i).getX(), points.get(i).getY(), 4, paint1);
					path.lineTo(points.get(i).getX(), points.get(i).getY());
				}
				canvas.drawPath(path, paint);
			}
			
		}
	}
}
