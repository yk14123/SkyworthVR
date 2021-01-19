package com.chinafocus.hvrskyworthvr.model.multibean;

public class DeviceInfo {

    /**
     * uniqueId : 41426b75cfb945c79b01b4a669bbd283
     * alias : 七楼韦小宝
     * loginName : yangke
     * customerName : 杨科
     */

    private String loginName;
    private String customerName;

    private String uniqueId;
    private String alias;

    private String appNo;
    private String userNo;

    public String getAppNo() {
        return appNo;
    }

    public void setAppNo(String appNo) {
        this.appNo = appNo;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
