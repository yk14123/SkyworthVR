package com.chinafocus.hvrskyworthvr;

import android.app.Application;

import com.chinafocus.hvrskyworthvr.net.NetworkRequestInfo;
import com.chinafocus.lib_network.net.ApiManager;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ApiManager.init(new NetworkRequestInfo(this));
    }
}
