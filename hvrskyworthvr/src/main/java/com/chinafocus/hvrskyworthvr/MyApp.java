package com.chinafocus.hvrskyworthvr;

import android.app.Application;

import com.blankj.utilcode.util.LogUtils;
import com.chinafocus.hvrskyworthvr.model.DeviceInfoManager;
import com.chinafocus.hvrskyworthvr.net.NetworkRequestInfo;
import com.chinafocus.lib_network.net.ApiManager;
import com.devyk.crash_module.Crash;
import com.devyk.crash_module.inter.JavaCrashUtils;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;

public class MyApp extends Application implements JavaCrashUtils.OnCrashListener {
    @Override
    public void onCreate() {
        super.onCreate();

        ApiManager.init(new NetworkRequestInfo(this));

        DeviceInfoManager.getInstance().initDeviceInfo(this);

        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();

        //非wifi情况下，主动下载x5内核
        QbSdk.setDownloadWithoutWifi(true);
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                LogUtils.d("开启TBS===X5加速成功");
            }

            @Override
            public void onCoreInitFinished() {
                LogUtils.d("开启TBS===X5加速失败");

            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);

        HashMap<String, Object> map = new HashMap<>();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);

        collectCrashInfo();
    }

    private void collectCrashInfo() {
        File externalFilesDir = getExternalFilesDir("");
        File dir = new File(externalFilesDir, "crash");
        if (!dir.exists()) {
            dir.mkdir();
        }

        new Crash.CrashBuild(getApplicationContext())
                .javaCrashPath(dir.getAbsolutePath(), this)
                .build();
    }

    @Override
    public void onCrash(String crashInfo, Throwable e) {
    }
}
