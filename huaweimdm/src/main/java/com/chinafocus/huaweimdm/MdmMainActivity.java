/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */

package com.chinafocus.huaweimdm;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chinafocus.huaweimdm.tools.TimerTaskManager;
import com.huawei.android.app.admin.DeviceApplicationManager;
import com.huawei.android.app.admin.DeviceControlManager;
import com.huawei.android.app.admin.DeviceHwSystemManager;
import com.huawei.android.app.admin.DevicePackageManager;
import com.huawei.android.app.admin.DevicePasswordManager;
import com.huawei.android.app.admin.DeviceRestrictionManager;
import com.huawei.android.app.admin.DeviceSettingsManager;

import java.util.ArrayList;
import java.util.List;


/**
 * The MainActivity for this Sample
 *
 * @author huawei mdm
 * @since 2019-10-23
 */
public class MdmMainActivity extends Activity {
    // 禁用/启用管理类
    private DeviceRestrictionManager mDeviceRestrictionManager = null;
    // 设备控制管理类
    private DeviceControlManager mDeviceControlManager;
    // 设备包管理类
    private DevicePackageManager mDevicePackageManager;
    // 设备应用程序管理类
    private DeviceApplicationManager mDeviceApplicationManager;
    // 设备华为系统管理类
    private DeviceHwSystemManager mDeviceHwSystemManager;
    // 设备设置管理类
    private DeviceSettingsManager mDeviceSettingsManager;

    private DevicePolicyManager mDevicePolicyManager = null;
    public ComponentName mAdminName = null;
    private TextView mUninstallStatusText;
    private TextView mStatusBarExpandPaneStatusText;
    private TextView mBackButtonStatusText;
    private TextView mSystemUpdateStatusText;
    private TextView mRebootStateTxt;
    private TextView mPersistentAppStatusText;
    private TextView mSingleAppStatusText;
    private TextView mSuperWhiteListStateTxt;
    private TextView mScreenOffStateTxt;
    private TextView mAllNotificationStateTxt;
    private TextView mKeyguardStateTxt;
    private TextView mSleepByPowerButtonStateTxt;
    private TextView mUsbDataStateTxt;

    private List<String> mPackages;
    private ArrayList<String> mPackagesArray;
    private DevicePasswordManager mDevicePasswordManager;

    private String getVersionName() {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdm_activity_main);

        TextView textView = findViewById(R.id.tv_version);
        textView.setText("版本号是:" + getVersionName());

        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceRestrictionManager = new DeviceRestrictionManager();
        mDeviceControlManager = new DeviceControlManager();
        mDevicePackageManager = new DevicePackageManager();
        mDeviceApplicationManager = new DeviceApplicationManager();
        mDeviceHwSystemManager = new DeviceHwSystemManager();
        mDeviceSettingsManager = new DeviceSettingsManager();
        mDevicePasswordManager = new DevicePasswordManager();
        mAdminName = new ComponentName(getApplicationContext(), SampleDeviceReceiver.class);

        initSampleView();
        updateState();

        new SampleEula(this, mDevicePolicyManager, mAdminName).show();
    }

    private void initSampleView() {
        initStatusBarExpandPaneView();
        initBackButtonView();
        initSystemUpdateView();
        initRebootDeviceView();
        initSilentActiveAdminView();
        initInstallPackageView();
        initUninstallView();
        initPersistentAppView();
        initSingleAppView();
        initSuperWhiteListView();
        initScreenOffView();
        initAllNotificationView();
        initKeyguardStateView();
        initSleepByPowerButtonStateView();
        initEnterSplash();
        initUSBData();
    }

    /**
     * 禁止/允许USB调试，数据传输
     */
    private void initUSBData() {
        mUsbDataStateTxt = (TextView) findViewById(R.id.dataUSBStateTxt);
        Button enableUSBDataButtonState = (Button) findViewById(R.id.enableUSBData);
        Button disableUSBDataButtonState = (Button) findViewById(R.id.disableUSBData);
        enableUSBDataButtonState.setOnClickListener(new SampleOnClickListener());
        disableUSBDataButtonState.setOnClickListener(new SampleOnClickListener());
    }

    /**
     * 禁止/允许亮屏下按电源键休眠
     */
    private void initSleepByPowerButtonStateView() {
        mSleepByPowerButtonStateTxt = (TextView) findViewById(R.id.sleepByPowerButtonStateTxt);
        Button enableSleepByPowerButtonStateBtn = (Button) findViewById(R.id.enableSleepByPowerButtonState);
        Button disableSleepByPowerButtonStateBtn = (Button) findViewById(R.id.disableSleepByPowerButtonState);
        enableSleepByPowerButtonStateBtn.setOnClickListener(new SampleOnClickListener());
        disableSleepByPowerButtonStateBtn.setOnClickListener(new SampleOnClickListener());
    }

    /**
     * 禁用锁屏
     */
    private void initKeyguardStateView() {
        mKeyguardStateTxt = (TextView) findViewById(R.id.keyguardStateTxt);
        Button enableKeyguardStateBtn = (Button) findViewById(R.id.enableKeyguardState);
        Button disableKeyguardStateBtn = (Button) findViewById(R.id.disableKeyguardState);
        enableKeyguardStateBtn.setOnClickListener(new SampleOnClickListener());
        disableKeyguardStateBtn.setOnClickListener(new SampleOnClickListener());
    }

    /**
     * 进入大眼360
     */
    private void initEnterSplash() {
        Button enterSplashBtn = (Button) findViewById(R.id.enter_splash);
        enterSplashBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allDone();
            }
        });
    }

    private void allDone() {
//        finish();
        try {
            mDeviceControlManager.rebootDevice(mAdminName);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_permission), Toast.LENGTH_SHORT).show();
        }
    }

    private void enterSplashActivity() {
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), "com.chinafocus.hvrskyworthvr.ui.splash.SplashActivity");
        MdmMainActivity.this.startActivity(intent);
        finish();
    }

    /**
     * 禁止收到所有系统以及三方应用通知消息
     */
    private void initAllNotificationView() {
        mAllNotificationStateTxt = (TextView) findViewById(R.id.allNotificationStateTxt);
        Button enableAllNotificationBtn = (Button) findViewById(R.id.enableAllNotification);
        Button disableAllNotificationBtn = (Button) findViewById(R.id.disableAllNotification);
        enableAllNotificationBtn.setOnClickListener(new SampleOnClickListener());
        disableAllNotificationBtn.setOnClickListener(new SampleOnClickListener());
    }

    /**
     * 禁用休眠
     */
    private void initScreenOffView() {
        mScreenOffStateTxt = (TextView) findViewById(R.id.screenOffStateTxt);
        Button enableScreenOffBtn = (Button) findViewById(R.id.enableScreenOff);
        Button disableScreenOffBtn = (Button) findViewById(R.id.disableScreenOff);
        enableScreenOffBtn.setOnClickListener(new SampleOnClickListener());
        disableScreenOffBtn.setOnClickListener(new SampleOnClickListener());
    }

    /**
     * 设置可信任应用列表
     */
    private void initSuperWhiteListView() {
        mSuperWhiteListStateTxt = (TextView) findViewById(R.id.superWhiteListStateTxt);
        Button addSuperWhiteListBtn = (Button) findViewById(R.id.addSuperWhiteList);
        Button removeSuperWhiteListBtn = (Button) findViewById(R.id.removeSuperWhiteList);
        addSuperWhiteListBtn.setOnClickListener(new SampleOnClickListener());
        removeSuperWhiteListBtn.setOnClickListener(new SampleOnClickListener());

        if (mPackagesArray == null) {
            mPackagesArray = new ArrayList<>();
            mPackagesArray.add(getPackageName());
        }
    }

    /**
     * 独占应用
     */
    private void initSingleAppView() {
        mSingleAppStatusText = (TextView) findViewById(R.id.singleStateTxt);
        Button addSingleAppBtn = (Button) findViewById(R.id.addSingleApp);
        Button removeSingleAppBtn = (Button) findViewById(R.id.removeSingleApp);
        addSingleAppBtn.setOnClickListener(new SampleOnClickListener());
        removeSingleAppBtn.setOnClickListener(new SampleOnClickListener());
    }

    /**
     * 保持某应用始终运行名单
     */
    private void initPersistentAppView() {
        mPersistentAppStatusText = (TextView) findViewById(R.id.persistentAppStateTxt);
        Button addPersistentAppBtn = (Button) findViewById(R.id.addPersistentApp);
        Button removePersistentAppBtn = (Button) findViewById(R.id.removePersistentApp);
        addPersistentAppBtn.setOnClickListener(new SampleOnClickListener());
        removePersistentAppBtn.setOnClickListener(new SampleOnClickListener());

        if (mPackages == null) {
            mPackages = new ArrayList<>();
            mPackages.add(getPackageName());
        }
    }

    /**
     * 阻止删除
     */
    private void initUninstallView() {
        mUninstallStatusText = (TextView) findViewById(R.id.uninstallStateTxt);
        Button uninstallDisableBtn = (Button) findViewById(R.id.disableUninstall);
        Button uninstallEnableBtn = (Button) findViewById(R.id.enableUninstall);
        uninstallDisableBtn.setOnClickListener(new SampleOnClickListener());
        uninstallEnableBtn.setOnClickListener(new SampleOnClickListener());

        if (mPackages == null) {
            mPackages = new ArrayList<>();
            mPackages.add(getPackageName());
        }
    }

    /**
     * 静默安装应用
     */
    private void initInstallPackageView() {
        Button silentInstallBtn = (Button) findViewById(R.id.bt_silent_install);
        silentInstallBtn.setOnClickListener(new SampleOnClickListener());
    }

    /**
     * 设置静默激活设备管理器
     */
    private void initSilentActiveAdminView() {
        Button silentActiveAdminBtn = (Button) findViewById(R.id.bt_silent_active_admin);
        silentActiveAdminBtn.setOnClickListener(new SampleOnClickListener());
    }

    /**
     * 重启功能
     */
    private void initRebootDeviceView() {
        mRebootStateTxt = (TextView) findViewById(R.id.rebootStateTxt);
        Button backButtonDisableBtn = (Button) findViewById(R.id.enableReboot);
        Button backButtonEnableBtn = (Button) findViewById(R.id.disableReboot);
        backButtonDisableBtn.setOnClickListener(new SampleOnClickListener());
        backButtonEnableBtn.setOnClickListener(new SampleOnClickListener());
    }

    /**
     * 禁用系统升级
     */
    private void initSystemUpdateView() {
        mSystemUpdateStatusText = (TextView) findViewById(R.id.systemUpdateStateTxt);
        Button backButtonDisableBtn = (Button) findViewById(R.id.disableSystemUpdate);
        Button backButtonEnableBtn = (Button) findViewById(R.id.enableSystemUpdate);
        backButtonDisableBtn.setOnClickListener(new SampleOnClickListener());
        backButtonEnableBtn.setOnClickListener(new SampleOnClickListener());
    }

    /**
     * 禁用返回键
     */
    private void initBackButtonView() {
        mBackButtonStatusText = (TextView) findViewById(R.id.backButtonStateTxt);
        Button backButtonDisableBtn = (Button) findViewById(R.id.disableBackButton);
        Button backButtonEnableBtn = (Button) findViewById(R.id.enableBackButton);
        backButtonDisableBtn.setOnClickListener(new SampleOnClickListener());
        backButtonEnableBtn.setOnClickListener(new SampleOnClickListener());
    }

    /**
     * 禁用状态栏下拉菜单
     */
    private void initStatusBarExpandPaneView() {
        mStatusBarExpandPaneStatusText = (TextView) findViewById(R.id.statusBarExpandPaneStateTxt);
        Button statusBarExpandPaneDisableBtn = (Button) findViewById(R.id.disableStatusBarExpandPane);
        Button statusBarExpandPaneEnableBtn = (Button) findViewById(R.id.enableStatusBarExpandPane);
        statusBarExpandPaneDisableBtn.setOnClickListener(new SampleOnClickListener());
        statusBarExpandPaneEnableBtn.setOnClickListener(new SampleOnClickListener());
    }

    private void updateState() {
        if (!isActiveMe()) {
            mStatusBarExpandPaneStatusText.setText(getString(R.string.state_not_actived));
            mBackButtonStatusText.setText(getString(R.string.state_not_actived));
            mSystemUpdateStatusText.setText(getString(R.string.state_not_actived));
            mUninstallStatusText.setText(getString(R.string.state_not_actived));
            mPersistentAppStatusText.setText(getString(R.string.state_not_actived));
            mSingleAppStatusText.setText(getString(R.string.state_not_actived));
            mSuperWhiteListStateTxt.setText(getString(R.string.state_not_actived));
            mScreenOffStateTxt.setText(getString(R.string.state_not_actived));
            mAllNotificationStateTxt.setText(getString(R.string.state_not_actived));
            mKeyguardStateTxt.setText(getString(R.string.state_not_actived));
            mSleepByPowerButtonStateTxt.setText(getString(R.string.state_not_actived));
            mRebootStateTxt.setText(getString(R.string.state_not_actived));
            mUsbDataStateTxt.setText(getString(R.string.state_not_actived));
            return;
        }

        boolean isStatusBarExpandPanelDisabled = false;
        boolean isBackButtonDisabled = false;
        boolean isSystemUpdateDisabled = false;
        boolean isScreenOffDisabled = false;
        boolean isNotificationDisabled = false;
        boolean isKeyguardDisabled = false;
        boolean isSleepByPowerButtonDisabled = false;
        boolean isUsbDataDisabled = false;
        List<String> disallowedUninstallPackageList = null;
        List<String> persistentApp = null;
        String singleApp = "";
        ArrayList<String> superWhiteListForHwSystemManger = null;
        try {
            isStatusBarExpandPanelDisabled = mDeviceRestrictionManager.isStatusBarExpandPanelDisabled(mAdminName);
            isBackButtonDisabled = mDeviceRestrictionManager.isBackButtonDisabled(mAdminName);
            isSystemUpdateDisabled = mDeviceRestrictionManager.isSystemUpdateDisabled(mAdminName);
            disallowedUninstallPackageList = mDevicePackageManager.getDisallowedUninstallPackageList(mAdminName);
            persistentApp = mDeviceApplicationManager.getPersistentApp(mAdminName);
            singleApp = mDeviceApplicationManager.getSingleApp(mAdminName);
            superWhiteListForHwSystemManger = mDeviceHwSystemManager.getSuperWhiteListForHwSystemManger(mAdminName);
            isScreenOffDisabled = mDeviceSettingsManager.isScreenOffDisabled(mAdminName);
            isNotificationDisabled = mDeviceSettingsManager.isNotificationDisabled(mAdminName);

            isKeyguardDisabled = mDevicePasswordManager.isKeyguardDisabled(mAdminName, 0);
            isSleepByPowerButtonDisabled = mDeviceRestrictionManager.isSleepByPowerButtonDisabled(mAdminName);
            isUsbDataDisabled = mDeviceRestrictionManager.isUSBDataDisabled(mAdminName);

        } catch (SecurityException securityException) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_permission), Toast.LENGTH_SHORT).show();
        }

        if (TimerTaskManager.getInstance().isStartTimerTask()) {
            mRebootStateTxt.setText(R.string.state_enable);
        } else {
            mRebootStateTxt.setText(R.string.state_disable);
        }

        if (isStatusBarExpandPanelDisabled) {
            mStatusBarExpandPaneStatusText.setText(R.string.state_restricted);
        } else {
            mStatusBarExpandPaneStatusText.setText(R.string.state_nomal);
        }
        if (isBackButtonDisabled) {
            mBackButtonStatusText.setText(R.string.state_restricted);
        } else {
            mBackButtonStatusText.setText(R.string.state_nomal);
        }
        if (isSystemUpdateDisabled) {
            mSystemUpdateStatusText.setText(R.string.state_restricted);
        } else {
            mSystemUpdateStatusText.setText(R.string.state_nomal);
        }
        if (disallowedUninstallPackageList != null && disallowedUninstallPackageList.size() > 0 &&
                TextUtils.equals(disallowedUninstallPackageList.get(0), getPackageName())) {
            mUninstallStatusText.setText(R.string.state_restricted);
        } else {
            mUninstallStatusText.setText(R.string.state_nomal);
        }
        if (persistentApp != null && persistentApp.size() > 0 &&
                TextUtils.equals(persistentApp.get(0), getPackageName())) {
            mPersistentAppStatusText.setText(R.string.state_enable);
        } else {
            mPersistentAppStatusText.setText(R.string.state_disable);
        }
        if (!TextUtils.isEmpty(singleApp)) {
            mSingleAppStatusText.setText(R.string.state_enable);
        } else {
            mSingleAppStatusText.setText(R.string.state_disable);
        }
        if (superWhiteListForHwSystemManger != null && superWhiteListForHwSystemManger.size() > 0 &&
                TextUtils.equals(superWhiteListForHwSystemManger.get(0), getPackageName())) {
            mSuperWhiteListStateTxt.setText(R.string.state_enable);
        } else {
            mSuperWhiteListStateTxt.setText(R.string.state_disable);
        }
        if (isScreenOffDisabled) {
            mScreenOffStateTxt.setText(R.string.state_restricted);
        } else {
            mScreenOffStateTxt.setText(R.string.state_nomal);
        }
        if (isNotificationDisabled) {
            mAllNotificationStateTxt.setText(R.string.state_restricted);
        } else {
            mAllNotificationStateTxt.setText(R.string.state_nomal);
        }
        if (isKeyguardDisabled) {
            mKeyguardStateTxt.setText(R.string.state_restricted);
        } else {
            mKeyguardStateTxt.setText(R.string.state_nomal);
        }
        if (isSleepByPowerButtonDisabled) {
            mSleepByPowerButtonStateTxt.setText(R.string.state_restricted);
        } else {
            mSleepByPowerButtonStateTxt.setText(R.string.state_nomal);
        }

        if (isUsbDataDisabled) {
            mUsbDataStateTxt.setText(R.string.state_restricted);
        } else {
            mUsbDataStateTxt.setText(R.string.state_nomal);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateState();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isActiveMe() {
        if (mDevicePolicyManager == null) {
            return false;
        } else {
            return mDevicePolicyManager.isAdminActive(mAdminName);
        }
    }

    private class SampleOnClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            try {
                int id = view.getId();
                if (id == R.id.disableUninstall) {
                    mDevicePackageManager.addDisallowedUninstallPackages(mAdminName, mPackages);
                } else if (id == R.id.enableUninstall) {
                    mDevicePackageManager.removeDisallowedUninstallPackages(mAdminName, mPackages);
                } else if (id == R.id.disableStatusBarExpandPane) {
                    mDeviceRestrictionManager.setStatusBarExpandPanelDisabled(mAdminName, true);
                } else if (id == R.id.enableStatusBarExpandPane) {
                    mDeviceRestrictionManager.setStatusBarExpandPanelDisabled(mAdminName, false);
                } else if (id == R.id.disableBackButton) {
                    mDeviceRestrictionManager.setBackButtonDisabled(mAdminName, true);
                } else if (id == R.id.enableBackButton) {
                    mDeviceRestrictionManager.setBackButtonDisabled(mAdminName, false);
                } else if (id == R.id.disableSystemUpdate) {
                    mDeviceRestrictionManager.setSystemUpdateDisabled(mAdminName, true);
                } else if (id == R.id.enableSystemUpdate) {
                    mDeviceRestrictionManager.setSystemUpdateDisabled(mAdminName, false);
                } else if (id == R.id.enableReboot) {
//                    mDeviceControlManager.rebootDevice(mAdminName);
                    if (mDeviceControlManager.isRooted(mAdminName)) {
                        TimerTaskManager.getInstance().startTimerTask(getApplicationContext());
                        Toast.makeText(getApplicationContext(), "开启每日凌晨02:00重启设备", Toast.LENGTH_SHORT).show();
                    }
                } else if (id == R.id.disableReboot) {
//                    mDeviceControlManager.rebootDevice(mAdminName);
                    if (mDeviceControlManager.isRooted(mAdminName)) {
                        TimerTaskManager.getInstance().cancelTimerTask();
                        Toast.makeText(getApplicationContext(), "取消每日凌晨02:00重启设备", Toast.LENGTH_SHORT).show();
                    }
                } else if (id == R.id.bt_silent_active_admin) {
                    mDeviceControlManager.setSilentActiveAdmin(mAdminName);
                    Toast.makeText(getApplicationContext(), "静默激活成功", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.bt_silent_install) {

//                    File file = new File(getFilesDir(), "app-update.apk");
//                    if (file.exists()) {
//                        Uri contentUri = FileProvider.getUriForFile(getApplicationContext(),
//                                "com.chinafocus.hvr_local_v2.myprovider",
//                                file);
//                        grantUriPermission("android", contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//                        mDevicePackageManager.installPackage(mAdminName, contentUri.toString());
//                    }
                    Toast.makeText(getApplicationContext(), "静默安装暂不支持", Toast.LENGTH_SHORT).show();

                } else if (id == R.id.addPersistentApp) {
                    mDeviceApplicationManager.addPersistentApp(mAdminName, mPackages);
                } else if (id == R.id.removePersistentApp) {
                    mDeviceApplicationManager.removePersistentApp(mAdminName, mPackages);
                } else if (id == R.id.addSingleApp) {
                    mDeviceApplicationManager.addSingleApp(mAdminName, getPackageName());
                } else if (id == R.id.removeSingleApp) {
                    mDeviceApplicationManager.clearSingleApp(mAdminName, getPackageName());
                } else if (id == R.id.addSuperWhiteList) {
                    mDeviceHwSystemManager.setSuperWhiteListForHwSystemManger(mAdminName, mPackagesArray);
                } else if (id == R.id.removeSuperWhiteList) {
                    mDeviceHwSystemManager.removeSuperWhiteListForHwSystemManger(mAdminName, mPackagesArray);
                } else if (id == R.id.disableScreenOff) {
                    mDeviceSettingsManager.setScreenOffDisabled(mAdminName, true);
                } else if (id == R.id.enableScreenOff) {
                    mDeviceSettingsManager.setScreenOffDisabled(mAdminName, false);
                } else if (id == R.id.disableAllNotification) {
                    mDeviceSettingsManager.setNotificationDisabled(mAdminName, true);
                } else if (id == R.id.enableAllNotification) {
                    mDeviceSettingsManager.setNotificationDisabled(mAdminName, false);
                } else if (id == R.id.disableKeyguardState) {
                    mDevicePasswordManager.setKeyguardDisabled(mAdminName, 0, true);
                } else if (id == R.id.enableKeyguardState) {
                    mDevicePasswordManager.setKeyguardDisabled(mAdminName, 0, false);
                } else if (id == R.id.disableSleepByPowerButtonState) {
                    mDeviceRestrictionManager.setSleepByPowerButtonDisabled(mAdminName, true);
                } else if (id == R.id.enableSleepByPowerButtonState) {
                    mDeviceRestrictionManager.setSleepByPowerButtonDisabled(mAdminName, false);
                } else if (id == R.id.disableUSBData) {
                    mDeviceRestrictionManager.setUSBDataDisabled(mAdminName, true);
                } else if (id == R.id.enableUSBData) {
                    mDeviceRestrictionManager.setUSBDataDisabled(mAdminName, false);
                }
            } catch (SecurityException securityException) {
                Toast.makeText(getApplicationContext(), getString(R.string.no_permission), Toast.LENGTH_SHORT).show();
            }
            updateState();
        }
    }
}