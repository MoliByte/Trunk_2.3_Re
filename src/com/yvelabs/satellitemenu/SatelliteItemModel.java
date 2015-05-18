package com.yvelabs.satellitemenu;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class SatelliteItemModel {
	
	private int id;
    private int imgResourceId;
    private Drawable imgDrawable;
    private String imgAssetPath;
	private ImageView view;
	private int originX;
	private int originY;
    private int stopX;
    private int stopY;
    private int adjustX;
    private int adjustY;
    
	public int getOriginX() {
		return originX;
	}
	public void setOriginX(int originX) {
		this.originX = originX;
	}
	public int getOriginY() {
		return originY;
	}
	public void setOriginY(int originY) {
		this.originY = originY;
	}
	public SatelliteItemModel (int id, int imgResourceId) {
    	this.id = id;
    	this.imgResourceId = imgResourceId;
    }
	public SatelliteItemModel (int id, Drawable imgDrawable) {
		this.id = id;
		this.imgDrawable = imgDrawable;
	}
	public SatelliteItemModel (int id, String imgAssetPath) {
		this.id = id;
		this.imgAssetPath = imgAssetPath;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getImgResourceId() {
		return imgResourceId;
	}
	public void setImgResourceId(int imgResourceId) {
		this.imgResourceId = imgResourceId;
	}
	public Drawable getImgDrawable() {
		return imgDrawable;
	}
	public void setImgDrawable(Drawable imgDrawable) {
		this.imgDrawable = imgDrawable;
	}
	public ImageView getView() {
		return view;
	}
	public void setView(ImageView view) {
		this.view = view;
	}
	public String getImgAssetPath() {
		return imgAssetPath;
	}
	public void setImgAssetPath(String imgAssetPath) {
		this.imgAssetPath = imgAssetPath;
	}
	public int getStopX() {
		return stopX;
	}
	public void setStopX(int stopX) {
		this.stopX = stopX;
	}
	public int getStopY() {
		return stopY;
	}
	public void setStopY(int stopY) {
		this.stopY = stopY;
	}
	public int getAdjustX() {
		return adjustX;
	}
	public void setAdjustX(int adjustX) {
		this.adjustX = adjustX;
	}
	public int getAdjustY() {
		return adjustY;
	}
	public void setAdjustY(int adjustY) {
		this.adjustY = adjustY;
	}

}
