package com.chinafocus.hvrskyworthvr.model;

import android.content.Context;
import android.text.TextUtils;

import com.chinafocus.hvrskyworthvr.model.bean.DeviceInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DeviceInfoManager {

    private DeviceInfoManager() {
        mDeviceInfo = new DeviceInfo();
    }

    private static final DeviceInfoManager instance = new DeviceInfoManager();

    public static DeviceInfoManager getInstance() {
        return instance;
    }

    private final DeviceInfo mDeviceInfo;

    public void initDeviceInfo(Context context) {

        mDeviceInfo.setAlias("");

        File file = new File(context.getExternalFilesDir("Config"), "Account_Id.txt");
        File appNo = new File(context.getExternalFilesDir("Config"), "App_No.txt");
        try (
                FileReader fis = new FileReader(file);
                FileReader fis2 = new FileReader(appNo);
                BufferedReader bis = new BufferedReader(fis);
                BufferedReader bis2 = new BufferedReader(fis2)
        ) {
            String s = bis.readLine();
            mDeviceInfo.setUserNo(s);

            String appNoString = bis2.readLine();
            mDeviceInfo.setAppNo(appNoString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isUserNumberExist() {
        return !TextUtils.isEmpty(mDeviceInfo.getUserNo());
    }

    public boolean isDeviceUUIDExist() {
        return !TextUtils.isEmpty(mDeviceInfo.getUniqueId());
    }

    public String getDeviceUUID() {
        return mDeviceInfo.getUniqueId();
    }

    public void postDeviceUUID(String uuid) {
        mDeviceInfo.setUniqueId(uuid);
    }

    public void postDeviceAccountName(String customerName) {
        mDeviceInfo.setCustomerName(customerName);
    }

    public void postDeviceAlias(String alias) {
        mDeviceInfo.setAlias(alias);
    }

    public String getDeviceAlias() {
        return mDeviceInfo.getAlias();
    }

    public String getDeviceAccountName() {
        return mDeviceInfo.getCustomerName();
    }

    public String getDeviceAccountId() {
        return mDeviceInfo.getUserNo();
    }

    public String getAppNo() {
        return mDeviceInfo.getAppNo();
    }

}
