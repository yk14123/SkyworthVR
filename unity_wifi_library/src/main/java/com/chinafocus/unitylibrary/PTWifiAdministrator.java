package com.chinafocus.unitylibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

public class PTWifiAdministrator {
    private WifiManager wifiManager; //wifi管理api
    private WifiInfo wifiInfo; //具体wifi数据api
    private List<ScanResult> scanResultList; //wifi扫描结果
    private List<WifiConfiguration> wifiConfigurationList;

    WifiManager.WifiLock wifiLock;

    public PTWifiAdministrator(Context context) {
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null)
            wifiInfo = wifiManager.getConnectionInfo();
    }

    //获取wifi状态
    public int GetWifiState() {
        return wifiManager.getWifiState();
    }

    // 创建一个WifiLock
    public void CreateWifiLock() {
        wifiLock = wifiManager.createWifiLock("PTWifiLock");
    }

    public void AcquireWifiLock() {
        wifiLock.acquire();
    }

    // 得到配置好的网络
    public List<WifiConfiguration> GetWifiConfiguration() {
        return wifiConfigurationList;
    }

    // 指定配置好的网络进行连接
    public boolean ConnectConfiguration(int index) {
        if (wifiConfigurationList == null || index > wifiConfigurationList.size()) {
            return false;
        } else {
            return wifiManager.enableNetwork(wifiConfigurationList.get(index).networkId, true);
        }
    }

    //开始扫描周为wifi或者热点
    @SuppressLint("MissingPermission")
    public void StartScanWifi() {
        wifiManager.startScan();
        List<ScanResult> results = wifiManager.getScanResults();
        //scanResultList = wifiManager.getScanResults();
        wifiConfigurationList = wifiManager.getConfiguredNetworks();
        if (results == null) {
//            if (wifiManager.getWifiState() == 3) {
//                Toast.makeText(context, "当前区域没有无线网络", Toast.LENGTH_SHORT).show();
//            } else if (wifiManager.getWifiState() == 2) {
//                Toast.makeText(context, "wifi正在开启，请稍后扫描", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(context, "WiFi没有开启", Toast.LENGTH_SHORT).show();
//            }
        } else {
            scanResultList = new ArrayList<>();
            for (ScanResult result : results) {
                if (result.SSID == null || result.SSID.length() == 0 || result.capabilities.contains("[IBSS]")) {
                    continue;
                }
                boolean found = false;
                for (ScanResult item : scanResultList) {
                    if (item.SSID.equals(result.SSID) && item.capabilities.equals(result.capabilities)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    scanResultList.add(result);
                }
            }
        }
    }

    // 得到网络列表
    public List<ScanResult> GetScanResults() {
        return scanResultList;
    }

    //预览扫描到的wifi
    public String LookUpScan() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < scanResultList.size(); i++) {
            sb.append(i).append(":");
            sb.append(scanResultList.get(i).SSID).append(":");
            sb.append(scanResultList.get(i).level).append(":");
            sb.append(scanResultList.get(i).capabilities).append(":");
            if (IsConnect(scanResultList.get(i))) {
                sb.append("1" + ";");
            } else {
                sb.append("0" + ";");
            }
        }
        return sb.toString();
    }

    // 得到MAC地址
    @SuppressLint("HardwareIds")
    public String GetMacAdress() {
        return wifiInfo == null ? null : wifiInfo.getMacAddress();
    }

    // 得到接入点的BSSID
    public String GetBSSID() {
        return wifiInfo == null ? null : wifiInfo.getBSSID();
    }

    // 得到IP地址
    public int GetIPAdress() {
        return wifiInfo == null ? null : wifiInfo.getIpAddress();
    }

    // 得到连接的ID
    public int GetNetWordID() {
        return wifiInfo == null ? null : wifiInfo.getNetworkId();
    }

    // 得到WifiInfo的所有信息包
    public String GetWifiInfo() {
        return wifiInfo == null ? null : wifiInfo.toString();
    }

    // 断开指定ID的网络
    public void DisConnectWifi(int netId) {
        wifiManager.disableNetwork(netId);
        wifiManager.disconnect();
    }

    //打开wifi
    public boolean OpenWifi() {
        if (!wifiManager.isWifiEnabled()) {
            return wifiManager.setWifiEnabled(true);
        } else {
            return true;
        }
    }

    //关闭wifi
    public boolean CloseWifi() {
        if (wifiManager.isWifiEnabled()) {
            return wifiManager.setWifiEnabled(false);
        }
        return false;
    }

    //连接wifi  根据ssid和password
    public boolean Connect(String SSID, String Password, PTWifiConnect.WifiCipherType Type) {
        if (!this.OpenWifi()) {
            return false;
        }
        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                Thread.currentThread();
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        WifiConfiguration wifiConfig = CreateWifiConfiguration(SSID, Password, Type);
        if (wifiConfig == null) {
            return false;
        }
        WifiConfiguration tempConfig = this.IsExist(SSID);
        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        boolean bRet = wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
        return bRet;
    }

    //判断当前连接是否和该wifi匹配
    public boolean IsConnect(ScanResult result) {
        if (result == null)
            return false;
        wifiInfo = wifiManager.getConnectionInfo();
        String gc = "\"" + result.SSID + "\"";
        if (wifiInfo != null && wifiInfo.getSSID().endsWith(gc)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取wifi名称
     *
     * @return wifi名称。如果没有连接返回<unknown ssid>
     */
    String getWifiConnectedName() {

        WifiInfo info = wifiManager.getConnectionInfo();
        if (info != null) {
            String infoSSID = info.getSSID();
            if (infoSSID.equals("<unknown ssid>")) {
                return "";
            }
            return infoSSID;
        }
        return "";
    }

    private WifiConfiguration IsExist(String SSID) {
        @SuppressLint("MissingPermission") List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration temp : existingConfigs) {
            if (temp.SSID.equals("\"" + SSID + "\"")) {
                return temp;
            }
        }
        return null;
    }

    //生成WifiConfiguration对象 给连接wifi用
    private WifiConfiguration CreateWifiConfiguration(String SSID, String Password, PTWifiConnect.WifiCipherType Type) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.allowedAuthAlgorithms.clear();
        wifiConfiguration.allowedGroupCiphers.clear();
        wifiConfiguration.allowedKeyManagement.clear();
        wifiConfiguration.allowedPairwiseCiphers.clear();
        wifiConfiguration.allowedProtocols.clear();
        wifiConfiguration.SSID = "\"" + SSID + "\"";
        if (Type == PTWifiConnect.WifiCipherType.WIFICIPHER_NOPASS) {
            wifiConfiguration.wepKeys[0] = "";
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfiguration.wepTxKeyIndex = 0;
        } else if (Type == PTWifiConnect.WifiCipherType.WIFICIPHER_WEP) {
            wifiConfiguration.preSharedKey = "\"" + Password + "\"";
            wifiConfiguration.hiddenSSID = true;
            wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfiguration.wepTxKeyIndex = 0;
        } else if (Type == PTWifiConnect.WifiCipherType.WIFICIPHER_WPA) {
            wifiConfiguration.preSharedKey = "\"" + Password + "\"";
            wifiConfiguration.hiddenSSID = true;
            wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        } else {
            return null;
        }
        return wifiConfiguration;
    }

    public String ipIntToString(int ip) {
        try {

            byte[] bytes = new byte[4];
            bytes[0] = (byte) (0xff & ip);
            bytes[1] = (byte) ((0xff00 & ip) >> 8);
            bytes[2] = (byte) ((0xff0000 & ip) >> 16);
            bytes[3] = (byte) ((0xff000000 & ip) >> 24);
            return Inet4Address.getByAddress(bytes).getHostAddress();
        } catch (Exception e) {
            return "";
        }
    }

    public int getConnNetId() {
        // result.SSID;
        wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getNetworkId();
    }

    public boolean connectSpecificAP(ScanResult scan, String password) {
        @SuppressLint("MissingPermission") List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        boolean networkInSupplicant = false;
        boolean connectResult = false;
        wifiManager.disconnect();
        for (WifiConfiguration w : list) {
            // String str = convertToQuotedString(info.ssid);
            if (w.BSSID != null && w.BSSID.equals(scan.BSSID)) {
                connectResult = wifiManager.enableNetwork(w.networkId, true);
                // mWifiManager.saveConfiguration();
                networkInSupplicant = true;
                break;
            }
        }
        if (!networkInSupplicant) {
            WifiConfiguration config = CreateWifiInfo(scan, password);
            connectResult = addNetwork(config);
        }
        return connectResult;
    }

    public boolean addNetwork(WifiConfiguration wcg) {
        if (wcg == null) {
            return false;
        }
        //receiverDhcp = new ReceiverDhcp(ctx, mWifiManager, this, wlanHandler);
        // ctx.registerReceiver(receiverDhcp, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        int wcgID = wifiManager.addNetwork(wcg);
        boolean b = wifiManager.enableNetwork(wcgID, true);
        wifiManager.saveConfiguration();
        System.out.println(b);
        return b;
    }

    public WifiConfiguration CreateWifiInfo(ScanResult scan, String Password) {
        WifiConfiguration config = new WifiConfiguration();
        config.hiddenSSID = false;
        config.status = WifiConfiguration.Status.ENABLED;
        if (scan.capabilities.contains("WEP")) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.SSID = "\"" + scan.SSID + "\"";
            config.wepTxKeyIndex = 0;
            config.wepKeys[0] = Password;
            // config.preSharedKey = "\"" + SHARED_KEY + "\"";
        } else if (scan.capabilities.contains("PSK")) {
            config.SSID = "\"" + scan.SSID + "\"";
            config.preSharedKey = "\"" + Password + "\"";
        } else if (scan.capabilities.contains("EAP")) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.SSID = "\"" + scan.SSID + "\"";
            config.preSharedKey = "\"" + Password + "\"";
        } else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.SSID = "\"" + scan.SSID + "\"";
            // config.BSSID = info.mac;
            config.preSharedKey = null;
        }
        return config;
    }

}
