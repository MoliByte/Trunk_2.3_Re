package com.skinrun.trunk.test.popuwindow;

import android.app.Activity;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;

import com.beabox.hjy.tt.R;

public class HintPopuWindow implements OnClickListener{
	
	
	
	public static interface HintListener{
		void onGoToBuy();
		void onClose();
	}

	private HintListener hintListener;
	private PopupWindow popupWindow;
	private Activity context;
	
	
	
	
	public void setHintListener(HintListener hintListener) {
		this.hintListener = hintListener;
	}

	public HintPopuWindow(){
		
	}
	
	public void show(Activity context){
		this.context=context;
		
		if(popupWindow==null){
			Rect frame=new Rect();
			context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			
			int width=frame.width();
			int height=frame.height();
			
			LayoutInflater layoutInflater=LayoutInflater.from(this.context);
			View hintView=layoutInflater.inflate(R.layout.test_hint, null);
			initView(hintView);
			
			popupWindow=new PopupWindow(hintView, width, height);
			popupWindow.setFocusable(true);
			//popupWindow.setAnimationStyle(R.style.AnimBottom);
		}
		
		 View rootView = context.getWindow().getDecorView().findViewById(android.R.id.content);
	        //popupWindow.setAnimationStyle(R.style.DialogAnimation);
	     popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
	}
	
	
	private void initView(View view){
		view.findViewById(R.id.imageViewGoToKnow).setOnClickListener(this);
		view.findViewById(R.id.imageViewIKnow).setOnClickListener(this);
	}
	
	public void dismiss(){
		if(popupWindow!=null&&popupWindow.isShowing()){
			popupWindow.dismiss();
		}
	}
	public void destory(){
		context=null;
		popupWindow=null;
		hintListener=null;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.imageViewGoToKnow:
			if(hintListener!=null){
				hintListener.onGoToBuy();
			}
			
			break;
		case R.id.imageViewIKnow:
			if(hintListener!=null){
				hintListener.onClose();
			}
			
			break;
		}
	}

}
