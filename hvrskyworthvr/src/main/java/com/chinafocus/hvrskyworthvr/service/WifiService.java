package com.chinafocus.hvrskyworthvr.service;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.chinafocus.hvrskyworthvr.model.multibean.DeviceInfoManager;

public class WifiService {

    private WifiManager mWifiManager;
    private String mWifiConnectedName;

    //    private String mCurrentDeviceUUID;
    private String mAccountName;

    private WifiService() {
    }

    private static WifiService instance;

    public static WifiService getInstance() {
        if (instance == null) {
            synchronized (WifiService.class) {
                if (instance == null) {
                    instance = new WifiService();
                }
            }
        }
        return instance;
    }

    /**
     * 获取当前渠道名称
     *
     * @return 渠道名称
     */
    public String getAccountName() {
        return mAccountName;
    }

    /**
     * 开启wifi设置页面，并且该页面不属于系统页面，属于本应用内部页面
     *
     * @param context 上下文
     */
    public void startSettingWifi(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
        context.startActivity(intent);
    }

    /**
     * 获取wifi名称
     *
     * @param context 上下文
     * @return wifi名称。如果没有连接返回<unknown ssid>
     */
    private String getWifiConnectedName(Context context) {
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }

        WifiInfo info = mWifiManager.getConnectionInfo();
        if (info != null) {
            String infoSSID = info.getSSID();
            if (infoSSID.equals("<unknown ssid>")) {
                return null;
            }
            return infoSSID;
        }
        return null;
    }

    private WifiStatusListener mWifiStatusListener;

    public void setWifiStatusListener(WifiStatusListener wifiStatusListener) {
        mWifiStatusListener = wifiStatusListener;
    }

    public interface WifiStatusListener {
        // wifi状态初始化的时候调用
        void wifiStatusInit();

        // wifi链接成功，但该wifi是否可以上网，暂不知道
        void wifiConnectedSuccess(String name);

        // 检查网络，连接正常
        void checkedNetWorkConnectedSuccess();

        // 检查网络，连接错误
        void wifiNetWorkError(String name);

        // 加载渠道名称
        void loadAccountName(String name);
    }

    /**
     * 当网络通顺后，加载渠道名称
     */
    public void loadAccountName() {
        if (DeviceInfoManager.getInstance().isDeviceUUIDExist() && TextUtils.isEmpty(mAccountName)) {
            // TODO 当网络通畅后，访问渠道接口，拿到渠道名称
            if (mWifiStatusListener != null) {
                mAccountName = "账户名称";
                mWifiStatusListener.loadAccountName(mAccountName);
            }
        }
    }

    public void initDeviceInfo() {

        if (DeviceInfoManager.getInstance().isDeviceUUIDExist() && !TextUtils.isEmpty(mWifiConnectedName)) {
//            ApiManager
//                    .getService(ApiService.class)
//                    .getDefaultCloudUrl()
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread(), true)
//                    .onErrorResumeNext(new HttpErrorHandler<>())
//                    .subscribe(new BaseObserver<DefaultCloudUrl>() {
//                        @Override
//                        public void onSuccess(DefaultCloudUrl defaultCloudUrlBaseResponse) {
//                            Constants.DEFAULT_URL = defaultCloudUrlBaseResponse.getCloudUrl();
//                            Log.d("MyLog", "-----DEFAULT_URL >>>" + Constants.DEFAULT_URL);
//                            // 如果网络正常，则成功连接
//                            if (mWifiStatusListener != null) {
//                                mWifiStatusListener.checkedNetWorkConnectedSuccess();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(ExceptionHandle.ResponseThrowable e) {
//                            Log.e("MyLog", "-----初始化 DEFAULT_URL 失败 >>> " + e.message);
//                            // 如果WIFI链接，但是路由器没有网络
//                            if (mWifiStatusListener != null) {
//                                mWifiStatusListener.wifiNetWorkError(mWifiConnectedName);
//                            }
//                        }
//
//                        @Override
//                        protected void onServiceMessage(String errMsg) {
//                            Log.e("MyLog", "-----服务端返回 DEFAULT_URL 异常 >>> " + errMsg);
//                            // 如果WIFI链接，但是路由器没有网络
//                            if (mWifiStatusListener != null) {
//                                mWifiStatusListener.wifiNetWorkError(mWifiConnectedName);
//                            }
//                        }
//                    });
        }
    }

    public void onStart(Context context) {
        mWifiConnectedName = getWifiConnectedName(context);
        if (TextUtils.isEmpty(mWifiConnectedName)) {
            if (mWifiStatusListener != null) {
                mWifiStatusListener.wifiStatusInit();
            }
        } else {
            if (mWifiStatusListener != null) {
                mWifiStatusListener.wifiConnectedSuccess(mWifiConnectedName);
            }
            initDeviceInfo();
//            loadAccountName();
        }
    }

}
