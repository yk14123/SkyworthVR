package com.chinafocus.skyworthvrwifi;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ssnwt.vr.androidmanager.AndroidInterface;
import com.ssnwt.vr.androidmanager.wifi.WifiInfo;
import com.ssnwt.vr.androidmanager.wifi.WifiUtils;

public class MainActivity extends AppCompatActivity {

    private WifiUtils mWifiUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidInterface.getInstance().init(getApplication());


        mWifiUtils = AndroidInterface.getInstance().getWifiUtils();
        mWifiUtils.resume();
        // 连接状态
        // i;s
        mWifiUtils.setOnConnectingListener("AndroidWIFIManager", "ReceiveState");
        // 扫描结果
        // WifiInfo的 toString状态
        mWifiUtils.setOnScanResultListener("AndroidWIFIManager", "ReceiveScanResult");

//        mWifiUtils.setListener(new WifiUtils.WifiListener() {
//            @Override
//            public void onOpened(boolean b) {
//
//            }
//
//            @Override
//            public void onConnecting(int i, String s) {
//
//            }
//
//            @Override
//            public void onSearchResult(String s) {
//                Log.e("MyLog", "-----Listener [onSearchResult] >>>" + s);
//            }
//
//            @Override
//            public void onRssiLevelChanegd(int i) {
//
//            }
//        });
//
//
//        mWifiUtils.setListener2(new WifiUtils.WifiListener2() {
//            @Override
//            public void onOpened(boolean b) {
////                Log.e("MyLog", "-----Listener2 [onOpened] >>> " + b);
//            }
//
//            @Override
//            public void onConnecting(int i, String s) {
//                // 注意格式是  i;s
//                Log.e("MyLog", "-----Listener2 [onConnecting] status >>> " + i + " ssid >>> " + s);
//            }
//
//            @Override
//            public void onSearchResult(ArrayList<WifiInfo> arrayList) {
//                for (WifiInfo wifiInfo : arrayList) {
//                    Log.e("MyLog", "-----Listener2 [onSearchResult] "
//                            + " NetworkID 所有没有保存的，都是-1，不管密码是否正确，只要连接了，都会出现保存，顺序从1开始。1,2,3,4... >>> " + wifiInfo.getNetworkID()
//                            + " ssid ChinaFocus >>> " + wifiInfo.getSSID()
//                            + " bsid 06:05:88:a9:a5:90 >>> " + wifiInfo.getBSSID()
//                            + " capabilities [WPA-PSK-CCMP+TKIP][WPA2-PSK-CCMP+TKIP][WPS][ESS] >>> " + wifiInfo.getCapabilities()
//                            + " 信号强度RssiLevel 分为3,2,1 >>> " + wifiInfo.getRssiLevel());
//
//                    if (wifiInfo.getSSID().equals("ChinaFocus")) {
//                        chinafocus = wifiInfo;
//                    } else if (wifiInfo.getSSID().equals("ChinaFocus_yingjiete")) {
//                        yingjiete = wifiInfo;
//                    }
//                }
//            }
//
//            @Override
//            public void onRssiLevelChanegd(int i) {
//                //当前已连接wifi信号强度等级
////                Log.e("MyLog", "-----Listener2 [onRssiLevelChanegd] >>> " + i);
//            }
//        });

    }

    private WifiInfo chinafocus;
    private WifiInfo yingjiete;

    /**
     * 搜索wifi
     */
    public void searchWifi() {
        mWifiUtils.searchWifi();
    }

    /**
     * 采用密码连接wifi
     *
     * @param ssid
     * @param bssid
     * @param capabilities
     * @param password
     */
    public void connectWifi(String ssid, String bssid, String capabilities, String password) {
        mWifiUtils.connectWifi(ssid, bssid, capabilities, password);
    }

    /**
     * 重连已保存的wifi
     *
     * @param nid
     */
    public void connectWifi(int nid) {
        mWifiUtils.connectWifi(nid);
    }

    /**
     * 忘记指定wifi，会自动连接其他已保存网络
     *
     * @param nid
     */
    public void forgetWifi(int nid) {
        mWifiUtils.forget(nid);
    }


    public void connectWifi(View view) {
//        mWifiUtils.connectWifi(chinafocus.getSSID(), chinafocus.getBSSID(), chinafocus.getCapabilities(), "650666888");
        mWifiUtils.connectWifi(chinafocus.getSSID(), chinafocus.getBSSID(), chinafocus.getCapabilities(), "650666888123");
    }

    public void connectedWifi(View view) {
        mWifiUtils.connectWifi(yingjiete.getSSID(), yingjiete.getBSSID(), yingjiete.getCapabilities(), "650666888");
//        mWifiUtils.connectWifi(chinafocus.getSSID(), chinafocus.getBSSID(), chinafocus.getCapabilities(), "650666888");
    }

    public void disconnectWifi(View view) {
        mWifiUtils.forget(chinafocus.getNetworkID());
    }

    public void searchWifi(View view) {
        mWifiUtils.searchWifi();
    }
}
