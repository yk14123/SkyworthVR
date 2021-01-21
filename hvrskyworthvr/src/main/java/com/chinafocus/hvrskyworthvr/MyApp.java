package com.chinafocus.hvrskyworthvr;

import android.app.Application;

import com.chinafocus.hvrskyworthvr.model.DeviceInfoManager;
import com.chinafocus.hvrskyworthvr.net.NetworkRequestInfo;
import com.chinafocus.lib_network.net.ApiManager;

import org.greenrobot.eventbus.EventBus;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ApiManager.init(new NetworkRequestInfo(this));

        DeviceInfoManager.getInstance().initDeviceInfo(this);

        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
    }
}
