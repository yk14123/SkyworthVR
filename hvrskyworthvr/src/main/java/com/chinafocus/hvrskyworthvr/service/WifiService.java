package com.chinafocus.hvrskyworthvr.service;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.model.bean.DefaultCloudUrl;
import com.chinafocus.hvrskyworthvr.net.ApiService;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;
import com.chinafocus.lib_network.net.errorhandler.HttpErrorHandler;
import com.chinafocus.lib_network.net.observer.BaseObserver;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WifiService {

    private WifiManager mWifiManager;

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
        void wifiStatusInit();

        void wifiConnectSuccess(String name);

        void wifiNetWorkError(String name);
    }

    public void onStart(Context context) {
        String wifiConnectedName = getWifiConnectedName(context);
        if (TextUtils.isEmpty(wifiConnectedName)) {
            if (mWifiStatusListener != null) {
                mWifiStatusListener.wifiStatusInit();
            }
        } else {
            ApiManager
                    .getService(ApiService.class)
                    .getDefaultCloudUrl()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread(), true)
                    .onErrorResumeNext(new HttpErrorHandler<>())
                    .subscribe(new BaseObserver<DefaultCloudUrl>() {
                        @Override
                        public void onSuccess(DefaultCloudUrl defaultCloudUrlBaseResponse) {

                            Constants.DEFAULT_URL = defaultCloudUrlBaseResponse.getCloudUrl();
                            Log.d("MyLog", "-----DEFAULT_URL >>>" + Constants.DEFAULT_URL);
                            // 如果网络正常，则成功连接
                            if (mWifiStatusListener != null) {
                                mWifiStatusListener.wifiConnectSuccess(wifiConnectedName);
                            }
                        }

                        @Override
                        public void onFailure(ExceptionHandle.ResponseThrowable e) {
                            Log.e("MyLog", "-----初始化 DEFAULT_URL 失败 >>> " + e.message);
                            // 如果WIFI链接，但是路由器没有网络
                            if (mWifiStatusListener != null) {
                                mWifiStatusListener.wifiNetWorkError(wifiConnectedName);
                            }
                        }

                        @Override
                        protected void onServiceMessage(String errMsg) {
                            Log.e("MyLog", "-----服务端返回 DEFAULT_URL 异常 >>> " + errMsg);
                            // 如果WIFI链接，但是路由器没有网络
                            if (mWifiStatusListener != null) {
                                mWifiStatusListener.wifiNetWorkError(wifiConnectedName);
                            }
                        }
                    });
        }
    }

}