package com.app.custom.view;

import com.beabox.hjy.tt.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class IntegralToast {
	private Context context;
	private int num;
	
	
	public IntegralToast(Context context, int num) {
		super();
		this.context = context;
		this.num = num;
	}
	
	public void show(){
		Toast toast=new Toast(context.getApplicationContext());
		View view=LayoutInflater.from(context).inflate(R.layout.intergral_toast_layout, null);
		TextView tvIntegralTextView=(TextView) view.findViewById(R.id.tvIntegral);
		tvIntegralTextView.setText("+"+num);
		toast.setView(view);
		
		toast.setDuration(800);
		toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 200);
		toast.show();
	}
}
