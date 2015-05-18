package com.idongler.widgets;

/**
 * Created by fangjilue on 14-5-20.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 圆形的Imageview
 * 
 * @author bingyang.djj
 * @since 2012-11-02
 */
public class CircleImageView extends ImageView {

	private float padding = 10f;

	public CircleImageView(Context context) {
		super(context);
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		try {
			Drawable drawable = getDrawable();
			if (drawable == null || getWidth() == 0 || getHeight() == 0) {
				super.onDraw(canvas);
			} else {
				Bitmap b = ((BitmapDrawable) drawable).getBitmap();
				Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
				int w = getWidth(), h = getHeight();

				Bitmap roundBitmap = getCroppedBitmap(bitmap, w, padding);
				canvas.drawBitmap(roundBitmap, 0, 0, null);
			}
		} catch (Exception e) {
			// TODO: handle exception
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
		}

	}

	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius, float padding) {
		Bitmap sbmp;
		try {

			if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
				sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
			} else {
				sbmp = bmp;
			}
			Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
					sbmp.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);
			paint.setDither(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(Color.parseColor("#BAB399"));
			canvas.drawCircle(sbmp.getWidth() / 2, sbmp.getHeight() / 2,
					sbmp.getWidth() / 2 - padding, paint);
			/*
			 * canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() /
			 * 2 + 0.7f, sbmp.getWidth() / 2 + 0.1f, paint);
			 */
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(sbmp, rect, rect, paint);

			return output;
		} catch (Exception e) {
			return null;
		} catch (OutOfMemoryError e) {
			return null;
		}
	}

	public void setPadding(float padding) {
		this.padding = padding;
	}
}
