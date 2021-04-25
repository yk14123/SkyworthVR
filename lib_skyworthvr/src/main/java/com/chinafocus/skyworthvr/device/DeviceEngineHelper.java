package com.chinafocus.skyworthvr.device;

import com.ssnwt.vr.androidmanager.AndroidInterface;
import com.ssnwt.vr.androidmanager.DeviceUtils;

public class DeviceEngineHelper {

    private DeviceUtils mDeviceUtils;

    /**
     * 初始化DeviceEngine,只调用一次
     */
    public void initDeviceEngine() {
        mDeviceUtils = AndroidInterface.getInstance().getDeviceUtils();
        if (mDeviceUtils == null) {
            throw new IllegalStateException("------请先初始化  SkyworthAndroidInterface : 调用 SkyworthAndroidInterface.initApplication(Activity activity)------");
        }
        mDeviceUtils.resume();
    }

    /**
     * 设置息屏时间
     *
     * @param time 单位毫秒
     */
    public void setScreenOffTimeout(int time) {
        mDeviceUtils.setScreenOffTimeout(time);
    }

    public void releaseDeviceEngine() {
        if (mDeviceUtils != null) {
            mDeviceUtils.pause();
        }
    }
}
