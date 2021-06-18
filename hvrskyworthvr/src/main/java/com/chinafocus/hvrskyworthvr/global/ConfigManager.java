package com.chinafocus.hvrskyworthvr.global;

public class ConfigManager {

    private String preVideoTempFilePath;
    private String realVideoTempFilePath;
    private String preVideoFilePath;
    private String realVideoFilePath;
    private String subtitleFilePath;

    private String defaultUrl;

    private String cloudNo;

    private ConfigManager() {
    }

    private static final ConfigManager instance = new ConfigManager();

    public static ConfigManager getInstance() {
        return instance;
    }

    public boolean isHWCloud() {
        return "002".equals(cloudNo);
    }

    public boolean isALICloud() {
        return "001".equals(cloudNo);
    }

    public void setCloudNo(String cloudNo) {
        this.cloudNo = cloudNo;
    }

    public String getDefaultUrl() {
        return defaultUrl;
    }

    public void setDefaultUrl(String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    public String getPreVideoTempFilePath() {
        return preVideoTempFilePath;
    }

    public void setPreVideoTempFilePath(String preVideoTempFilePath) {
        this.preVideoTempFilePath = preVideoTempFilePath;
    }

    public String getRealVideoTempFilePath() {
        return realVideoTempFilePath;
    }

    public void setRealVideoTempFilePath(String realVideoTempFilePath) {
        this.realVideoTempFilePath = realVideoTempFilePath;
    }

    public String getPreVideoFilePath() {
        return preVideoFilePath;
    }

    public void setPreVideoFilePath(String preVideoFilePath) {
        this.preVideoFilePath = preVideoFilePath;
    }

    public String getRealVideoFilePath() {
        return realVideoFilePath;
    }

    public void setRealVideoFilePath(String realVideoFilePath) {
        this.realVideoFilePath = realVideoFilePath;
    }

    public String getSubtitleFilePath() {
        return subtitleFilePath;
    }

    public void setSubtitleFilePath(String subtitleFilePath) {
        this.subtitleFilePath = subtitleFilePath;
    }
}
