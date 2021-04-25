package com.chinafocus.skyworthvr.sensor;

import com.ssnwt.vr.androidmanager.AndroidInterface;
import com.ssnwt.vr.androidmanager.ProximitySensorUtils;
import com.unity3d.player.UnityPlayer;

public class SensorEngineHelper {

    private ProximitySensorUtils mProximitySensorUtils;

    private static final String UNITY_OBJ_NAME = "AndroidSensorManager";

    /**
     * 初始化ProximitySensorEngine,只调用一次
     */
    public void initProximitySensorEngine() {
        mProximitySensorUtils = AndroidInterface.getInstance().getProximitySensorUtils();
        if (mProximitySensorUtils == null) {
            throw new IllegalStateException("------请先初始化  SkyworthAndroidInterface : 调用 SkyworthAndroidInterface.initApplication(Activity activity)------");
        }
        mProximitySensorUtils.addSensorListener(new ProximitySensorUtils.SensorListener() {
            @Override
            public void onDistanceFar(boolean b) {
                UnityPlayer.UnitySendMessage(UNITY_OBJ_NAME, "ReadProximitySensorStatus", (b ? 1 : 0) + "");
            }
        });
    }
}
