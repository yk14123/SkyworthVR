package com.chinafocus.hvrskyworthvr.model.bean;

import com.google.gson.annotations.SerializedName;

public class AppVersionInfo {

    /**
     * appName : app名称
     * versionId : -1
     * versionCode : "2.0.30"
     * identifier: 30
     * versionIntro : 版本说明
     * versionUrl :
     * size : 123
     * upStartTime : 2021-05-10T05:31:20.173+0000
     * upEndTime : 2021-05-10T05:31:20.173+0000
     * autoDownLoad : 0
     */
    
    private String appName;
    private int versionId;
    @SerializedName("identifier")
    private int defaultVersionCode;
    @SerializedName("versionCode")
    private String versionName;
    private String versionIntro;
    private String versionUrl;
    private String size;
    private String upStartTime;
    private String upEndTime;
    private int autoDownLoad;

    public int getDefaultVersionCode() {
        return defaultVersionCode;
    }

    public void setDefaultVersionCode(int defaultVersionCode) {
        this.defaultVersionCode = defaultVersionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public String getVersionIntro() {
        return versionIntro;
    }

    public void setVersionIntro(String versionIntro) {
        this.versionIntro = versionIntro;
    }

    public String getVersionUrl() {
        return versionUrl;
    }

    public void setVersionUrl(String versionUrl) {
        this.versionUrl = versionUrl;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUpStartTime() {
        return upStartTime;
    }

    public void setUpStartTime(String upStartTime) {
        this.upStartTime = upStartTime;
    }

    public String getUpEndTime() {
        return upEndTime;
    }

    public void setUpEndTime(String upEndTime) {
        this.upEndTime = upEndTime;
    }

    public int getAutoDownLoad() {
        return autoDownLoad;
    }

    public void setAutoDownLoad(int autoDownLoad) {
        this.autoDownLoad = autoDownLoad;
    }
}
