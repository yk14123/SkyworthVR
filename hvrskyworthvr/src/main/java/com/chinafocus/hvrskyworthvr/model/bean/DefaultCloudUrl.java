package com.chinafocus.hvrskyworthvr.model.bean;

public class DefaultCloudUrl {
    /**
     * id : 1
     * cloudName : 阿里云
     * cloudUrl : https://360-readtree-test.oss-cn-beijing.aliyuncs.com/
     * isDefault : 1
     */

    private int id;
    private String cloudName;
    private String cloudNo;
    private String cloudUrl;
    private int isDefault;

    public String getCloudNo() {
        return cloudNo;
    }

    public void setCloudNo(String cloudNo) {
        this.cloudNo = cloudNo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCloudName() {
        return cloudName;
    }

    public void setCloudName(String cloudName) {
        this.cloudName = cloudName;
    }

    public String getCloudUrl() {
        return cloudUrl;
    }

    public void setCloudUrl(String cloudUrl) {
        this.cloudUrl = cloudUrl;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }
}
