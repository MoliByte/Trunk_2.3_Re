package com.app.base.entity;

import com.google.gson.annotations.SerializedName;


/**
 * Created by ShelWee on 14-5-8.
 */
public class UpdateInfo {
	@SerializedName("appName")
    private String appName;
	@SerializedName("packageName")
    private String packageName;
	@SerializedName("versionCode")
    private String versionCode;
	@SerializedName("versionName")
    private String versionName;
	@SerializedName("apkUrl")
    private String apkUrl;
	@SerializedName("changeLog")
    private String changeLog;
	@SerializedName("updateTips")
    private String updateTips;
	//@Expose
	@SerializedName("is_force")
    private String is_force ;
    
    
	public String getIs_force() {
		return is_force;
	}

	public void setIs_force(String is_force) {
		this.is_force = is_force;
	}

	public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getUpdateTips() {
        return updateTips;
    }

    public void setUpdateTips(String updateTips) {
        this.updateTips = updateTips;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }
}
