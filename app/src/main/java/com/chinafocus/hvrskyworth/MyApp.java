package com.chinafocus.hvrskyworth;

import android.app.Application;

import com.chinafocus.hvrskyworth.test.AbcSet;
import com.ssnwt.vr.androidmanager.AndroidInterface;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AndroidInterface.getInstance().init(this);


    }
}
