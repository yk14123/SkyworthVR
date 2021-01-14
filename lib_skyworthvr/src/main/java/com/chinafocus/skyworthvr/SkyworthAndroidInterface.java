package com.chinafocus.skyworthvr;

import android.app.Activity;

import com.ssnwt.vr.androidmanager.AndroidInterface;

public class SkyworthAndroidInterface {

    /**
     * 在蓝牙模块和wifi模块初始化之前调用,只调用一次
     *
     * @param activity 当前activity
     */
    public void initApplication(Activity activity) {
        AndroidInterface.getInstance().init(activity.getApplication());
    }

    /**
     * 当应用被完全摧毁的时候调用，释放资源
     */
    public void releaseAll() {
        AndroidInterface.getInstance().release();
    }

}
