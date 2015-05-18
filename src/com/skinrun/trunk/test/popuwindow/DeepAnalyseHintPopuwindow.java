package com.skinrun.trunk.test.popuwindow;

import com.beabox.hjy.tt.R;

import android.app.Activity;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;

public class DeepAnalyseHintPopuwindow {
	public static interface onCloseListener{
		void close();
	}
	
	private Activity context;
	private PopupWindow popuWindow;
	private onCloseListener listener;
	
	public DeepAnalyseHintPopuwindow(onCloseListener listener) {
		super();
		this.listener = listener;
	}
	public void show(Activity context){
		this.context=context;
		
		if(popuWindow==null){
			Rect frame=new Rect();
			context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			
			int width=frame.width();
			int height=frame.height();
			
			LayoutInflater layoutInflater=LayoutInflater.from(this.context);
			View view=layoutInflater.inflate(R.layout.deep_analyse_hint_layout, null);
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					listener.close();
				}
			});
			
			popuWindow=new PopupWindow(view, width, height);
			popuWindow.setFocusable(true);
			//popuWindow.setAnimationStyle(R.style.AnimTop);
			
		}
		
		 View rootView = context.getWindow().getDecorView().findViewById(android.R.id.content);
	        //popupWindow.setAnimationStyle(R.style.DialogAnimation);
		 popuWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
		
	}
	
	public void dismiss(){
		if(popuWindow!=null&&popuWindow.isShowing()){
			popuWindow.dismiss();
		}
	}
	
	public void destroy(){
		context=null;
		popuWindow=null;
		listener=null;
	}
	
}
