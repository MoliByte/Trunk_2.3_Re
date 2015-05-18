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

public class FacialChartView extends View {

	private ArrayList<Point> points = new ArrayList<Point>();
	private ArrayList<String> texts = new ArrayList<String>();

	public ArrayList<String> getTexts() {
		return texts;
	}

	public void setTexts(ArrayList<String> texts) {
		this.texts = texts;
	}

	public ArrayList<Point> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}

	public FacialChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FacialChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FacialChartView(Context context) {
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

		Paint paintText = new Paint();

		paintText.setAntiAlias(true);// 去锯齿
		paintText.setColor(getResources().getColor(R.color.line_blue));// 颜色
		paintText.setTextSize(30);

		if (points == null) {
			return;
		}

		if (points.size() > 0) {
			Path path = new Path();
			path.moveTo(points.get(0).getX(), points.get(0).getY());
			canvas.drawCircle(points.get(0).getX(), points.get(0).getY(), 4,
					paint1);
			canvas.drawText(texts.get(0), points.get(0).getX() - 30, points
					.get(0).getY() - 15, paintText);

			if (points.size() > 1) {
				for (int i = 1; i < points.size(); i++) {
					canvas.drawCircle(points.get(i).getX(), points.get(i)
							.getY(), 4, paint1);

					canvas.drawText(texts.get(i), points.get(i).getX() - 30,
							points.get(i).getY() - 15, paintText);

					path.lineTo(points.get(i).getX(), points.get(i).getY());
				}
				canvas.drawPath(path, paint);
			}

		}
	}
}
