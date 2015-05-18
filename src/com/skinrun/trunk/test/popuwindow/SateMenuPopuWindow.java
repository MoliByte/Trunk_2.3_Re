package com.skinrun.trunk.test.popuwindow;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.app.base.entity.Point;
import com.app.base.init.MyApplication;
import com.beabox.hjy.tt.R;
import com.skinrun.trunk.main.TestingPartUtils;
import com.yvelabs.satellitemenu.AbstractAnimation;
import com.yvelabs.satellitemenu.DefaultAnimation;
import com.yvelabs.satellitemenu.SatelliteItemModel;
import com.yvelabs.satellitemenu.SatelliteMenu;
import com.yvelabs.satellitemenu.SatelliteMenu.OnPlanetClickedListener;
import com.yvelabs.satellitemenu.SatelliteMenu.OnSatelliteClickedListener;
import com.yvelabs.satellitemenu.SettingPara;
public class SateMenuPopuWindow{
	
	public interface OnexpandListener{
		void onClickCenter();
		void onClickSide(int part);
	}
	
	private Activity context;
	private PopupWindow popupWindow;
	private OnexpandListener onexpandListener;
	private int centerTop, CenterLeft;
	private SatelliteMenu satelliteMenu;
	
	private static String TAG="SateMenuPopuWindow";
	
	public SateMenuPopuWindow(){
		
	}
	
	
	public void setOnexpandListener(OnexpandListener onexpandListener) {
		this.onexpandListener = onexpandListener;
	}

	public void show(Activity context){
		this.context=context;
		if(popupWindow==null){
			 Rect frame = new Rect();
			 this.context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			 
			 int width=frame.width();
			 int height=frame.height();
			 
			 LayoutInflater layoutInflater=LayoutInflater.from(context);
			 
			 View view=layoutInflater.inflate(R.layout.testing_select_face, null);
			 initView(view);
			 
			 popupWindow=new PopupWindow(view, width, height, true);
			// popupWindow.setAnimationStyle(R.style.AnimTop);
			 //popupWindow.setAnimationStyle(R.style.AnimBottom);
		}
		
		 View rootView = context.getWindow().getDecorView().findViewById(android.R.id.content);
	     //popupWindow.setAnimationStyle(R.style.DialogAnimation);
	     popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
	     new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {

				Point point=satelliteMenu.autoExpand();
				centerTop = point.getY();
				CenterLeft = point.getX();
				
				Log.e(TAG, "===========中心点的坐标");
			}
		}, 200);
		
		
	}
	private void initView(View view){
		satelliteMenu=(SatelliteMenu) view.findViewById(R.id.satelliteMenu);
		
	
		ArrayList<SatelliteItemModel> satllites = new ArrayList<SatelliteItemModel>();

		satllites.add(new SatelliteItemModel(1, R.drawable.testing_part5));
		satllites.add(new SatelliteItemModel(2, R.drawable.testing_part3));
		satllites.add(new SatelliteItemModel(3, R.drawable.testing_part2));
		satllites.add(new SatelliteItemModel(4, R.drawable.testing_part1));

		// set originAngle, endAngle and distance
		
		android.graphics.Point point=MyApplication.screenSize;
		SettingPara para = new SettingPara(270, 90, point.x/4,R.drawable.testing_select_face, satllites);
		
		AbstractAnimation anim = new DefaultAnimation();
		para.setMenuAnimation(anim);
		try {
			satelliteMenu.setting(para);
		} catch (Exception e) {
			e.printStackTrace();
		}
		satelliteMenu.setOnPlanetClickedListener(new OnPlanetClickedListener() {
			
			@Override
			public void onClick(View view) {
				onexpandListener.onClickCenter();
			}
		});
		satelliteMenu.setOnSatelliteClickedListener(new OnSatelliteClickedListener() {
			
			@Override
			public void onClick(View v) {
				if(onexpandListener!=null){
					int partFlag=0;
					// 判断点击的选项
					
					Log.e(TAG, "==========卫星地点的坐标："+v.getLeft()+":"+v.getTop());
					
					if (v.getLeft() <= CenterLeft && v.getTop() < centerTop) {
						partFlag = TestingPartUtils.TFace;
					} else if (v.getLeft() > CenterLeft
							&& v.getTop() < centerTop) {
						partFlag = TestingPartUtils.IceFace;
					} else if (v.getLeft() > CenterLeft
							&& v.getTop() >centerTop) {
						partFlag = TestingPartUtils.UFace;
					} else if (v.getLeft() <= CenterLeft
							&& v.getTop() > centerTop) {
						partFlag = TestingPartUtils.OnHand;
					}
					onexpandListener.onClickSide(partFlag);
				}
			}
		});
	}
	public void dismiss(){
		 if (popupWindow != null && popupWindow.isShowing()) {
			 
			 new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					 popupWindow.dismiss();
				}
			}, 1500);
	           
	     }
	}
	
	public void destroy(){
		 context = null;
	     popupWindow = null;
	     onexpandListener = null;
	     satelliteMenu=null;
	}
	
}
