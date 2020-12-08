package com.chinafocus.hvrskyworthvr.exo.tools;

import android.content.Context;
import android.provider.Settings;

/**
 * @author
 * @date 2020/6/29
 * description：
 */
public class MiscUtils {
    /**
     * 判断是否开启了 “屏幕自动旋转”
     */
    public static boolean isScreenAutoRotate(Context context) {
        int gravity = 0;
        try {
            gravity = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return gravity == 1;
    }
}
