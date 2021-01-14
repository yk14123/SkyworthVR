package com.chinafocus.skyworthvr;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.chinafocus.skyworthvr.bluetooth.BluetoothEngineHelper;
import com.chinafocus.skyworthvr.wifi.WifiEngineHelper;

/**
 * Demo示例代码
 */
public class Demo extends AppCompatActivity {

    private BluetoothEngineHelper bluetoothEngineHelper;
    // 创维入口
    private SkyworthAndroidInterface mSkyworthAndroidInterface;
    private WifiEngineHelper mWifiEngineHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 只初始化一次
        mSkyworthAndroidInterface = new SkyworthAndroidInterface();
        mSkyworthAndroidInterface.initApplication(this);

        bluetoothEngineHelper = new BluetoothEngineHelper();
        bluetoothEngineHelper.initBluetoothEngine(this);

        mWifiEngineHelper = new WifiEngineHelper();
        mWifiEngineHelper.initWifiEngine();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 只销毁一次
        bluetoothEngineHelper.releaseBluetoothEngine();
        mWifiEngineHelper.releaseWifiEngine();
        // 最后调用
        mSkyworthAndroidInterface.releaseAll();
    }

    // --------------wifi通讯调用--------------------
    public void searchWifi() {
        mWifiEngineHelper.searchWifi();
    }

    public void connectWifi(String ssid, String bssid, String capabilities, String password) {
        mWifiEngineHelper.connectWifi(ssid, bssid, capabilities, password);
    }

    public void connectWifi(int nid) {
        mWifiEngineHelper.connectWifi(nid);
    }

    public void forgetWifi(int nid) {
        mWifiEngineHelper.forgetWifi(nid);
    }

    // --------------蓝牙通讯调用--------------------

    public void sendString() {
        bluetoothEngineHelper.sendMessage("VR端发送给Pad端：字符串");
    }

    public void startSyncBluetooth() {
        bluetoothEngineHelper.startBluetoothEngine(this);
    }

    public void unBondBluetooth() {
        bluetoothEngineHelper.unBondDevice();
    }

    public void retryBluetoothConnect() {
        bluetoothEngineHelper.retryConnect();
    }

}
