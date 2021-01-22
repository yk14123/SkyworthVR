package com.chinafocus.huaweimdm.tools;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.chinafocus.huaweimdm.SampleDeviceReceiver;
import com.huawei.android.app.admin.DeviceControlManager;
import com.huawei.android.app.admin.DevicePackageManager;

import java.io.File;

/**
 * @author
 * @date 2020/9/3
 * description：
 */
public class MdmTools {
    private MdmTools() {
    }

    private static MdmTools sMdmTools;

    public static MdmTools getInstance() {
        if (sMdmTools == null) {
            synchronized (MdmTools.class) {
                if (sMdmTools == null) {
                    sMdmTools = new MdmTools();
                }
            }
        }
        return sMdmTools;
    }

    /**
     * 重启
     *
     * @param context
     */
    public void reboot(Context context) {
        try {
            DeviceControlManager deviceControlManager = new DeviceControlManager();
            ComponentName componentName = new ComponentName(context, SampleDeviceReceiver.class);
            deviceControlManager.rebootDevice(componentName);
        } catch (Exception e) {

        }
    }

    /**
     * 静默安装
     *
     * @param context
     * @param apkPath
     */
    public void installPackage(Context context, String apkPath) {
        try {
            File file = new File(apkPath);
            Uri contentUri = FileProvider.getUriForFile(context,
                    "com.chinafocus.hvr_local_v2.myprovider",
                    file);
            context.grantUriPermission("android", contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            DevicePackageManager devicePackageManager = new DevicePackageManager();
            ComponentName adminName = new ComponentName(context, SampleDeviceReceiver.class);
            devicePackageManager.installPackage(adminName, contentUri.toString());

        } catch (Exception e) {

        }
    }

}
