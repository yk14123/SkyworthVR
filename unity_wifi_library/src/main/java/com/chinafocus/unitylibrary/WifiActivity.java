package com.chinafocus.unitylibrary;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class WifiActivity extends UnityPlayerActivity {
    public PTWifiAdministrator ptWifiAdministrator;
    //    private String unitygameobjectName = "AndroidBluetooth"; //Unity 中对应挂脚本对象的名称
    private String unitygameobjectName = "WIFIPanel"; //Unity 中对应挂脚本对象的名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);
    }

    //***********WIFI************
    //打开wifi
    public void OpenWifi() {
        Log.e("WIFIPanel", "打开wifi");
        ptWifiAdministrator.OpenWifi();
    }

    //获取wwifi状态
    public boolean GetWifiState() {
        Log.e("WIFIPanel", "获取wwifi状态");
        int state_id = ptWifiAdministrator.GetWifiState();
        UnityPlayer.UnitySendMessage(unitygameobjectName, "PTUnityWifiReceiveState", "state:" + state_id);
        return state_id == 3;
    }

    android.net.wifi.ScanResult scanResult;

    //连接wifi
    public boolean ConnetWifi(int number, String password) {
        Log.e("WIFIPanel", "连接wifi");
        scanResult = ptWifiAdministrator.GetScanResults().get(number);
        return ptWifiAdministrator.connectSpecificAP(scanResult, password);
    }

    //是否连接成功
    public boolean IsConnect() {
        boolean b = ptWifiAdministrator.IsConnect(scanResult);
        if (b) scanResult = null;
        return b;
    }

    public String getWifiConnectedName(Context context) {
        if (ptWifiAdministrator == null)
            ptWifiAdministrator = new PTWifiAdministrator(context);
        return ptWifiAdministrator.getWifiConnectedName();
    }

    //扫描
    public void WifiInit(Context context) {
        Log.e("WIFIPanel", "扫描开始");
        if (ptWifiAdministrator == null)
            ptWifiAdministrator = new PTWifiAdministrator(context);
        ptWifiAdministrator.StartScanWifi();
        //ptWifiAdministrator.GetScanResults();

        String ScanList = ptWifiAdministrator.LookUpScan();
        UnityPlayer.UnitySendMessage(unitygameobjectName, "PTUnityWifiReceive", ScanList);
    }

}
