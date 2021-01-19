package com.chinafocus.hvrskyworthvr.model.multibean;

import android.content.Context;
import android.text.TextUtils;

import com.chinafocus.hvrskyworthvr.net.ApiMultiService;

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
        mDeviceInfo.setAppNo(ApiMultiService.appNo);
        File file = new File(context.getExternalFilesDir(""), "userNo.txt");
        try (
                FileReader fis = new FileReader(file);
                BufferedReader bis = new BufferedReader(fis)
        ) {
            String s = bis.readLine();
            mDeviceInfo.setUserNo(s);
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

}
