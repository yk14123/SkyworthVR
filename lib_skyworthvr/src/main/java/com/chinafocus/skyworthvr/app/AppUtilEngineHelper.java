package com.chinafocus.skyworthvr.app;

import android.util.Log;

import com.ssnwt.vr.androidmanager.AndroidInterface;
import com.ssnwt.vr.androidmanager.apk.ApkUtils;
import com.ssnwt.vr.androidmanager.app.JAppInfo;

public class AppUtilEngineHelper {

    private ApkUtils mAppUtils;

    /**
     * 初始化AppUtilEngine,只调用一次
     */
    public void initAppUtilEngine() {
        mAppUtils = AndroidInterface.getInstance().getApkUtils();
        if (mAppUtils == null) {
            throw new IllegalStateException("------请先初始化  SkyworthAndroidInterface : 调用 SkyworthAndroidInterface.initApplication(Activity activity)------");
        }
        mAppUtils.setListener(new ApkUtils.ApkListener() {
            @Override
            public void onPackageEvent(int i, String s, JAppInfo jAppInfo) {
                Log.e("VR_ApkUtils", " i >>> " + i + " s >>> " + s + " jAppInfo >>> " + jAppInfo);
            }
        });
    }

    /**
     * 安装apk
     *
     * @param path 单位毫秒
     */
    public void installApk(String path) {
        if (mAppUtils != null) {
            mAppUtils.installApk(path);
        }
    }

    /**
     * 安装apk
     *
     * @param path 单位毫秒
     */
    public void installApkByRemote(String path) {
        if (mAppUtils != null) {
            mAppUtils.installApkByRemote(path);
        }
    }
}
