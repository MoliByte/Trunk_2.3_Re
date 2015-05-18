package com.yvelabs.satellitemenu;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;

public class SettingPara {
	
	public static final String POSITION_CENTER = "POSITION_CENTER";
	public static final String POSITION_BOTTOM_LEFT = "BOTTOM_LEFT";
	public static final String POSITION_BOTTOM_RIGHT = "BOTTOM_RIGHT";
	public static final String POSITION_TOP_RIGHT = "TOP_RIGHT";
	public static final String POSITION_TOP_LEFT = "TOP_LEFT";
	public static final String POSITION_TOP_CENTER = "TOP_CENTER";
	public static final String POSITION_BOTTOM_CENTER = "BOTTOM_CENTER";
	public static final String POSITION_LEFT_CENTER = "LEFT_CENTER";
	public static final String POSITION_RIGHT_CENTER = "RIGHT_CENTER";
	
	private int planetImgResourceId;
	private Drawable planetImgDrawable;
	private String planetImgAssetPath;
	private int originAngle; //��ʼ�Ƕ�
	private int endAngle; //��ֹ�Ƕ�
	private int satelliteDistance; //���Ǿ���
	private List<SatelliteItemModel> satelliteList = new ArrayList<SatelliteItemModel>();
	
	private AbstractAnimation menuAnimation; //����
	
	private int planetX; //�������X
	private int planetY; //�������Y
	private String planetPosition; //����λ��
	private int customerRadiusAdjust;
	
	public SettingPara (int originAngle, int endAngle, int satelliteDistance, int planetImgResourceId, List<SatelliteItemModel> satelliteList) {
		this.originAngle = originAngle;
		this.endAngle = endAngle;
		this.satelliteDistance = satelliteDistance;
		this.planetImgResourceId = planetImgResourceId;
		this.satelliteList = satelliteList;
	}
	
	public SettingPara (int originAngle, int endAngle, int satelliteDistance, Drawable planetImgDrawable, List<SatelliteItemModel> satelliteList) {
		this.originAngle = originAngle;
		this.endAngle = endAngle;
		this.satelliteDistance = satelliteDistance;
		this.planetImgDrawable = planetImgDrawable;
		this.satelliteList = satelliteList;
	}
	
	public SettingPara (int originAngle, int endAngle, int satelliteDistance, String planetImgAssetPath, List<SatelliteItemModel> satelliteList) {
		this.originAngle = originAngle;
		this.endAngle = endAngle;
		this.satelliteDistance = satelliteDistance;
		this.planetImgAssetPath = planetImgAssetPath;
		this.satelliteList = satelliteList;
	}
	
	
	public int getPlanetImgResourceId() {
		return planetImgResourceId;
	}
	public void setPlanetImgResourceId(int planetImgResourceId) {
		this.planetImgResourceId = planetImgResourceId;
	}
	public Drawable getPlanetImgDrawable() {
		return planetImgDrawable;
	}

	public void setPlanetImgDrawable(Drawable planetImgDrawable) {
		this.planetImgDrawable = planetImgDrawable;
	}

	public String getPlanetImgAssetPath() {
		return planetImgAssetPath;
	}

	public void setPlanetImgAssetPath(String planetImgAssetPath) {
		this.planetImgAssetPath = planetImgAssetPath;
	}

	public int getOriginAngle() {
		return originAngle;
	}
	public void setOriginAngle(int originAngle) {
		this.originAngle = originAngle;
	}
	public int getEndAngle() {
		return endAngle;
	}
	public void setEndAngle(int endAngle) {
		this.endAngle = endAngle;
	}
	public int getSatelliteDistance() {
		return satelliteDistance;
	}
	public void setSatelliteDistance(int satelliteDistance) {
		this.satelliteDistance = satelliteDistance;
	}
	public List<SatelliteItemModel> getSatelliteList() {
		return satelliteList;
	}
	public void setSatelliteList(List<SatelliteItemModel> satelliteList) {
		this.satelliteList = satelliteList;
	}
	public AbstractAnimation getMenuAnimation() {
		if (menuAnimation == null) {
			menuAnimation = new DefaultAnimation();
		}
		
		return menuAnimation;
	}
	public void setMenuAnimation(AbstractAnimation menuAnimation) {
		this.menuAnimation = menuAnimation;
	}
	public int getPlanetX() {
		return planetX;
	}
	public void setPlanetX(int planetX) {
		this.planetX = planetX;
	}
	public int getPlanetY() {
		return planetY;
	}
	public void setPlanetY(int planetY) {
		this.planetY = planetY;
	}

	public String getPlanetPosition() {
		return planetPosition;
	}

	public void setPlanetPosition(String planetPosition) {
		this.planetPosition = planetPosition;
	}

	public int getCustomerRadiusAdjust() {
		return customerRadiusAdjust;
	}

	public void setCustomerRadiusAdjust(int customerRadiusAdjust) {
		this.customerRadiusAdjust = customerRadiusAdjust;
	}

}
