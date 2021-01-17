package com.chinafocus.skyworthvr.wifi;

import com.ssnwt.vr.androidmanager.AndroidInterface;
import com.ssnwt.vr.androidmanager.wifi.WifiUtils;

public class WifiEngineHelper {

    private WifiUtils mWifiUtils;
    private static final String UNITY_OBJ_NAME = "AndroidWIFIManager";

    /**
     * 初始化WifiEngine,只调用一次
     */
    public void initWifiEngine() {
        mWifiUtils = AndroidInterface.getInstance().getWifiUtils();
        if (mWifiUtils == null) {
            throw new IllegalStateException("------请先初始化  SkyworthAndroidInterface : 调用 SkyworthAndroidInterface.initApplication(Activity activity)------");
        }
        // 连接状态
        // i;s
        mWifiUtils.setOnConnectingListener(UNITY_OBJ_NAME, "ReceiveState");
        // 扫描结果
        // WifiInfo的 toString状态
        mWifiUtils.setOnScanResultListener(UNITY_OBJ_NAME, "ReceiveScanResult");
        // 注册监听广播
        mWifiUtils.resume();
    }

    /**
     * 释放wifi引擎
     */
    public void releaseWifiEngine() {
        if (mWifiUtils != null) {
            mWifiUtils.pause();
        }
    }

    /**
     * 搜索wifi
     */
    public void searchWifi() {
        if (mWifiUtils != null) {
            mWifiUtils.searchWifi();
        }
    }

    /**
     * 采用密码连接wifi
     *
     * @param ssid         wifi名称
     * @param bssid        mac地址
     * @param capabilities 协议
     * @param password     密码
     */
    public void connectWifi(String ssid, String bssid, String capabilities, String password) {
        if (mWifiUtils != null) {
            mWifiUtils.connectWifi(ssid, bssid, capabilities, password);
        }
    }

    /**
     * 重连已保存的wifi
     *
     * @param nid 已保存的列表中的networkId
     */
    public void connectWifi(int nid) {
        if (mWifiUtils != null) {
            mWifiUtils.connectWifi(nid);
        }
    }

    /**
     * 忘记指定wifi，会自动连接其他已保存网络
     *
     * @param nid 已保存的列表中的networkId
     */
    public void forgetWifi(int nid) {
        if (mWifiUtils != null) {
            mWifiUtils.forget(nid);
        }
    }

    /**
     * 忘记当前Wifi，会自动连接其他已保存网络
     */
    public void forgetWifi() {
        if (mWifiUtils != null) {
            mWifiUtils.forget();
        }
    }

}
