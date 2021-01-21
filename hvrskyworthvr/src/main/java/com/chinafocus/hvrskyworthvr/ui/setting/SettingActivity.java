package com.chinafocus.hvrskyworthvr.ui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.service.AliasService;
import com.chinafocus.hvrskyworthvr.service.BluetoothService;
import com.chinafocus.hvrskyworthvr.service.WifiService;
import com.chinafocus.hvrskyworthvr.ui.main.MainActivity;
import com.chinafocus.hvrskyworthvr.ui.widget.DeviceInfoViewGroup;
import com.chinafocus.hvrskyworthvr.ui.widget.SettingViewGroup;

import static com.chinafocus.hvrskyworthvr.ui.widget.SettingViewGroup.CONNECT_CHECK_AGAIN;
import static com.chinafocus.hvrskyworthvr.ui.widget.SettingViewGroup.CONNECT_ERROR;
import static com.chinafocus.hvrskyworthvr.ui.widget.SettingViewGroup.CONNECT_SUCCESS;
import static com.chinafocus.hvrskyworthvr.ui.widget.SettingViewGroup.INIT;

public class SettingActivity extends AppCompatActivity {

    private SettingViewGroup mSettingBluetoothView;
    private SettingViewGroup mSettingWifiView;
    private SettingViewGroup mSettingAliasView;

    private boolean isWifiConnected;
    private boolean isBluetoothConnected;
    private AppCompatButton mBtAllReadyDone;


    private DeviceInfoViewGroup mDeviceInfoViewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mSettingWifiView = findViewById(R.id.view_devices_wifi);
        mSettingBluetoothView = findViewById(R.id.view_devices_bluetooth);
        mSettingAliasView = findViewById(R.id.view_devices_alias);

        mDeviceInfoViewGroup = findViewById(R.id.view_devices_info);

        mBtAllReadyDone = findViewById(R.id.bt_setting_all_done);
        mBtAllReadyDone.setOnClickListener(this::enterMainActivity);

        handleWifi();
        handleBluetooth();
        handleAlias();
    }

    private void handleAlias() {
        AliasService.getInstance().init(new AliasService.AliasStatusListener() {
            @Override
            public void aliasStatusInit() {
                mSettingAliasView.postStatusMessage(INIT);
            }

            @Override
            public void aliasSettingSuccess(String name) {
                mSettingAliasView.postStatusMessage(CONNECT_SUCCESS, name);
            }

            @Override
            public void aliasSettingError() {
                Toast.makeText(getApplicationContext(), "修改设备名称失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleWifi() {
        WifiService.getInstance().setWifiStatusListener(new WifiService.WifiStatusListener() {
            @Override
            public void wifiStatusInit() {
                mSettingWifiView.postStatusMessage(INIT);
                isWifiConnected = false;
                checkedEnterMainActivityEnable();
            }

            @Override
            public void wifiConnectedSuccess(String name) {
                mSettingWifiView.postStatusMessage(CONNECT_SUCCESS, name);
            }

            @Override
            public void checkedNetWorkConnectedSuccess() {
                isWifiConnected = true;
                checkedEnterMainActivityEnable();
            }

            @Override
            public void wifiNetWorkError(String name) {
                mSettingWifiView.postStatusMessage(CONNECT_CHECK_AGAIN, name);
                isWifiConnected = false;
                checkedEnterMainActivityEnable();
            }

            @Override
            public void loadAccountNameAndAlias(String accountName, String alias) {
                mDeviceInfoViewGroup.postAccountName(accountName);
                mSettingAliasView.postStatusMessage(CONNECT_SUCCESS, alias);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        WifiService.getInstance().onStart(this);
        BluetoothService.getInstance().onStart(this);
    }

    private void handleBluetooth() {
        BluetoothService.getInstance().registerBluetoothStatusListener(new BluetoothService.BluetoothStatusListener() {
            @Override
            public void autoConnecting() {
                mSettingBluetoothView.postStatusMessage(INIT);
                isBluetoothConnected = false;
                checkedEnterMainActivityEnable();
            }

            @Override
            public void connectedDeviceName(String deviceName) {
                mSettingBluetoothView.postStatusMessage(CONNECT_SUCCESS, deviceName);
                isBluetoothConnected = true;
                checkedEnterMainActivityEnable();
            }

            @Override
            public void connectError() {
                mSettingBluetoothView.postStatusMessage(CONNECT_ERROR);
                isBluetoothConnected = false;
                checkedEnterMainActivityEnable();
            }

            @Override
            public void onSyncUUIDSuccess(String uuid) {
                mDeviceInfoViewGroup.postUUIDMessage(uuid);
            }
        });
    }

    private void checkedEnterMainActivityEnable() {
        if (isBluetoothConnected && isWifiConnected) {
            mBtAllReadyDone.setEnabled(true);
        } else {
            mBtAllReadyDone.setEnabled(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        BluetoothService.getInstance().cancelDiscoveryAndUnregisterReceiver(this);
        AliasService.getInstance().clearAliasDialog();
    }

    private void enterMainActivity(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}