package com.app.custom.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;

import com.beabox.hjy.tt.R;

public class WaterGroupView extends View{
	private int waterNum;
	private Bitmap nBitmap,bitmap;
	private int width=25;
	
	
	public void setWaterNum(int waterNum) {
		this.waterNum = waterNum;
	}

	public WaterGroupView(Context context) {
		super(context);
		createBitmap();
	}

	public WaterGroupView(Context context, AttributeSet attrs) {
		super(context, attrs);
		createBitmap();
	}

	public WaterGroupView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		createBitmap();
	}
	
	private void createBitmap(){
		nBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.facial_no_water);
		bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.facial_water);
		
		Matrix matrix = new Matrix();
		matrix.postScale(1.0f,1.0f); //长和宽放大缩小的比例
		
		nBitmap=Bitmap.createBitmap(nBitmap, 0, 0, nBitmap.getWidth(), nBitmap.getHeight(),matrix,true);
		bitmap=Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),matrix,true);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(waterNum<0){
			waterNum=0;
		}
		
		if(waterNum>5){
			waterNum=5;
		}
		if(bitmap!=null&&nBitmap!=null){
			switch(waterNum){
			case 0:
				canvas.drawBitmap(nBitmap, 5, 5, null);
				canvas.drawBitmap(nBitmap, 5+width, 5, null);
				canvas.drawBitmap(nBitmap, 5+width*2, 5, null);
				canvas.drawBitmap(nBitmap, 5+width*3, 5, null);
				canvas.drawBitmap(nBitmap, 5+width*4, 5, null);
				
				break;
			case 1:
				canvas.drawBitmap(nBitmap, 5, 5, null);
				canvas.drawBitmap(nBitmap, 5+width, 5, null);
				canvas.drawBitmap(nBitmap, 5+width*2, 5, null);
				canvas.drawBitmap(nBitmap, 5+width*3, 5, null);
				canvas.drawBitmap(nBitmap, 5+width*4, 5, null);
				
				
				break;
			case 2:
				canvas.drawBitmap(nBitmap, 5, 5, null);
				canvas.drawBitmap(nBitmap, 5+width, 5, null);
				canvas.drawBitmap(nBitmap, 5+width*2, 5, null);
				canvas.drawBitmap(nBitmap, 5+width*3, 5, null);
				canvas.drawBitmap(nBitmap, 5+width*4, 5, null);
				
				break;
			case 3:
				canvas.drawBitmap(nBitmap, 5, 5, null);
				canvas.drawBitmap(nBitmap, 5+width, 5, null);
				canvas.drawBitmap(nBitmap, 5+width*2, 5, null);
				canvas.drawBitmap(nBitmap, 5+width*3, 5, null);
				canvas.drawBitmap(nBitmap, 5+width*4, 5, null);
				
				
				break;
			case 4:
				canvas.drawBitmap(nBitmap, 5, 5, null);
				canvas.drawBitmap(nBitmap, 5+width, 5, null);
				canvas.drawBitmap(nBitmap, 5+width*2, 5, null);
				canvas.drawBitmap(nBitmap, 5+width*3, 5, null);
				canvas.drawBitmap(nBitmap, 5+width*4, 5, null);
				
				
				break;
			case 5:
				canvas.drawBitmap(nBitmap, 5, 5, null);
				canvas.drawBitmap(nBitmap, 5+width, 5, null);
				canvas.drawBitmap(nBitmap, 5+width*2, 5, null);
				canvas.drawBitmap(nBitmap, 5+width*3, 5, null);
				canvas.drawBitmap(nBitmap, 5+width*4, 5, null);
				break;
			}
		}
	}
}
