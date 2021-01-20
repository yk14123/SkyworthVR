package com.chinafocus.hvrskyworthvr.global;

public class ConfigManager {
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
}
