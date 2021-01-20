package com.chinafocus.hvrskyworthvr.service;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.model.bean.DefaultCloudUrl;
import com.chinafocus.hvrskyworthvr.model.bean.DeviceInfo;
import com.chinafocus.hvrskyworthvr.model.DeviceInfoManager;
import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.hvrskyworthvr.net.RequestBodyManager;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;
import com.chinafocus.lib_network.net.errorhandler.HttpErrorHandler;
import com.chinafocus.lib_network.net.observer.BaseObserver;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WifiService {

    private WifiManager mWifiManager;
    private String mWifiConnectedName;

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
        // wifi状态初始化的时候调用
        void wifiStatusInit();

        // wifi链接成功，但该wifi是否可以上网，暂不知道
        void wifiConnectedSuccess(String name);

        // 检查网络，连接正常
        void checkedNetWorkConnectedSuccess();

        // 检查网络，连接错误
        void wifiNetWorkError(String name);

        // 加载渠道名称
        void loadAccountNameAndAlias(String accountName, String alias);
    }

    public void initDeviceInfo() {
        if (DeviceInfoManager.getInstance().isDeviceUUIDExist() && !TextUtils.isEmpty(mWifiConnectedName)) {
            registerDeviceInfo();
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
        }
    }

    private void postDeviceInfoName() {
        ApiManager
                .getService(ApiMultiService.class)
                .getDeviceInfoName(RequestBodyManager.getDefaultRequestBody())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread(), true)
                .onErrorResumeNext(new HttpErrorHandler<>())
                .subscribe(new BaseObserver<DeviceInfo>() {
                    @Override
                    public void onSuccess(DeviceInfo deviceInfo) {
                        DeviceInfoManager.getInstance().postAliasAndName(deviceInfo.getAlias(), deviceInfo.getCustomerName());
                        if (mWifiStatusListener != null) {
                            mWifiStatusListener.loadAccountNameAndAlias(
                                    DeviceInfoManager.getInstance().getDeviceInfoName(),
                                    DeviceInfoManager.getInstance().getDeviceAlias());
                        }
                        postResourcesBaseUrl();
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {
                        // 如果WIFI链接，但是路由器没有网络
                        if (mWifiStatusListener != null) {
                            mWifiStatusListener.wifiNetWorkError(mWifiConnectedName);
                        }
                    }
                });
    }

    private void postResourcesBaseUrl() {
        ApiManager
                .getService(ApiMultiService.class)
                .getDefaultCloudUrl(RequestBodyManager.getDefaultRequestBody())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread(), true)
                .onErrorResumeNext(new HttpErrorHandler<>())
                .subscribe(new BaseObserver<DefaultCloudUrl>() {
                    @Override
                    public void onSuccess(DefaultCloudUrl defaultCloudUrl) {
                        Constants.DEFAULT_URL = defaultCloudUrl.getCloudUrl();
                        Log.d("MyLog", "-----DEFAULT_URL >>>" + Constants.DEFAULT_URL);
                        // 如果WIFI链接，但是路由器没有网络
                        if (mWifiStatusListener != null) {
                            mWifiStatusListener.checkedNetWorkConnectedSuccess();
                        }
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {
                        // 如果WIFI链接，但是路由器没有网络
                        if (mWifiStatusListener != null) {
                            mWifiStatusListener.wifiNetWorkError(mWifiConnectedName);
                        }
                    }
                });
    }

    private void registerDeviceInfo() {
        ApiManager
                .getService(ApiMultiService.class)
                .initDeviceInfo(RequestBodyManager.getDefaultRequestBody())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread(), true)
                .onErrorResumeNext(new HttpErrorHandler<>())
                .subscribe(new BaseObserver<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        postDeviceInfoName();
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {
                        // 如果WIFI链接，但是路由器没有网络
                        if (mWifiStatusListener != null) {
                            mWifiStatusListener.wifiNetWorkError(mWifiConnectedName);
                        }
                    }

                    @Override
                    protected void onServiceMessage(String errMsg) {
                        super.onServiceMessage(errMsg);
                        postDeviceInfoName();
                    }
                });
    }

}
