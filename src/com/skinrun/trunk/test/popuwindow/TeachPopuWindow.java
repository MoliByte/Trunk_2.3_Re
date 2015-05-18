package com.skinrun.trunk.test.popuwindow;

import android.app.Activity;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;

import com.beabox.hjy.tt.R;

public class TeachPopuWindow implements OnClickListener {
	public static interface OnTeachListener{
		void onClose();
	}
	
	private OnTeachListener onTeachListener;
	private Activity context;
	private PopupWindow popuWindow;
	
	
	public void setOnTeachListener(OnTeachListener onTeachListener) {
		this.onTeachListener = onTeachListener;
	}
	
	public TeachPopuWindow(){
		 
	}
	
	public void show(Activity context){
		this.context=context;
		
		if(popuWindow==null){
			Rect frame=new Rect();
			context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			
			int width=frame.width();
			int height=frame.height();
			
			LayoutInflater layoutInflater=LayoutInflater.from(this.context);
			View view=layoutInflater.inflate(R.layout.test_teach_layout, null);
			initView(view);
			
			popuWindow=new PopupWindow(view, width, height);
			popuWindow.setFocusable(true);
			//popuWindow.setAnimationStyle(R.style.AnimTop);
			
		}
		
		 View rootView = context.getWindow().getDecorView().findViewById(android.R.id.content);
	        //popupWindow.setAnimationStyle(R.style.DialogAnimation);
		 popuWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
		
	}
	
	private void initView(View view){
		view.findViewById(R.id.imageViewIKnow).setOnClickListener(this);
	}
	
	public void dismiss(){
		if(popuWindow!=null&&popuWindow.isShowing()){
			popuWindow.dismiss();
		}
	}
	
	public void destroy(){
		context=null;
		popuWindow=null;
		onTeachListener=null;
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		case R.id.imageViewIKnow:
			onTeachListener.onClose();
			break;
		}
	}
	
	
}
