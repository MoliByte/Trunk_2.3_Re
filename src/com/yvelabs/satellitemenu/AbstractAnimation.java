package com.yvelabs.satellitemenu;


import java.util.List;

import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public abstract class AbstractAnimation {
	
	private List<SatelliteItemModel> satelliteList; 
	private ImageView planetMenu; 
	private RelativeLayout parentLayout; 
	private int satelliteDistance; 
	
	protected int getSatelliteDistance() {
		return satelliteDistance;
	}

	protected void setSatelliteDistance(int satelliteDistance) {
		this.satelliteDistance = satelliteDistance;
	}

	protected void setSatelliteList(List<SatelliteItemModel> satelliteList) {
		this.satelliteList = satelliteList;
	}
	
	protected RelativeLayout getParentLayout() {
		return parentLayout;
	}

	protected void setParentLayout(RelativeLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	protected List<SatelliteItemModel> getSatelliteList() {
		return satelliteList;
	}

	protected void init(List<SatelliteItemModel> satelliteList, RelativeLayout parentLayout) {
		this.satelliteList = satelliteList;
		this.parentLayout = parentLayout;
	}

	protected ImageView getPlanetMenu() {
		return planetMenu;
	}

	protected void setPlanetMenu(ImageView planetMenu) {
		this.planetMenu = planetMenu;
	}
	
	public abstract Animation createPlanetLaunchAnimation(View view);
	
	public abstract Animation createPlanetDrawBackAnimation(View view);
	
	public abstract Animation createPlanetItemClickedAnimation(View view);
	
	public abstract Animation createSatelliteLaunchAnimation(View view);
	
	public abstract Animation createSatelliteDrawBackAnimation(View view);
	
	public abstract Animation createSatelliteItemClickedAnimation(View view);
	

}
