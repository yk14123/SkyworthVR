package com.chinafocus.hvrskyworthvr.ui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.model.bean.DefaultCloudUrl;
import com.chinafocus.hvrskyworthvr.service.AliasService;
import com.chinafocus.hvrskyworthvr.service.BluetoothService;
import com.chinafocus.hvrskyworthvr.service.WifiService;
import com.chinafocus.hvrskyworthvr.ui.main.MainActivity;
import com.chinafocus.hvrskyworthvr.ui.widget.SettingViewGroup;

import static com.chinafocus.hvrskyworthvr.ui.widget.SettingViewGroup.CONNECTING;
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
    private AppCompatButton mBtAllDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mSettingWifiView = findViewById(R.id.view_devices_wifi);
        mSettingBluetoothView = findViewById(R.id.view_devices_bluetooth);
        mSettingAliasView = findViewById(R.id.view_devices_alias);

        mBtAllDone = findViewById(R.id.bt_setting_all_done);
        mBtAllDone.setOnClickListener(this::enterMainActivity);

        handleWifi();
        handleBluetooth();
        handleAlias();

        DefaultUrlViewModel model = new ViewModelProvider(this).get(DefaultUrlViewModel.class);
        model.getDefaultCloudUrl();

        model.defaultCloudUrlMutableLiveData.observe(this, new Observer<DefaultCloudUrl>() {
            @Override
            public void onChanged(DefaultCloudUrl defaultCloudUrl) {
                Constants.DEFAULT_URL = defaultCloudUrl.getCloudUrl();
            }
        });

    }

    private void handleAlias() {
        mSettingAliasView.postStatusMessage(INIT);
        AliasService.getInstance().init(new AliasService.AliasStatusListener() {
            @Override
            public void aliasStatusInit() {
                mSettingAliasView.postStatusMessage(INIT);
            }

            @Override
            public void aliasSettingSuccess(String name) {
                mSettingAliasView.postStatusMessage(CONNECT_SUCCESS, name);
            }
        });
        mSettingAliasView.getIvSettingSet().setOnClickListener(v -> AliasService.getInstance().onClick(SettingActivity.this));
    }

    private void handleWifi() {
        mSettingWifiView.postStatusMessage(INIT);
        mSettingWifiView.getIvSettingSet().setOnClickListener(v -> {
            WifiService.getInstance().startSettingWifi(SettingActivity.this);
            mSettingWifiView.postStatusMessage(CONNECTING);
        });
        WifiService.getInstance().setWifiStatusListener(new WifiService.WifiStatusListener() {
            @Override
            public void wifiStatusInit() {
                mSettingWifiView.postStatusMessage(INIT);
                isWifiConnected = false;
                checkedEnterMainActivityEnable();
            }

            @Override
            public void wifiConnectSuccess(String name) {
                mSettingWifiView.postStatusMessage(CONNECT_SUCCESS, name);
                isWifiConnected = true;
                checkedEnterMainActivityEnable();
            }

            @Override
            public void wifiNetWorkError(String name) {
                mSettingWifiView.postStatusMessage(CONNECT_CHECK_AGAIN, name);
                isWifiConnected = false;
                checkedEnterMainActivityEnable();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        WifiService.getInstance().onStart(this);
    }

    private void handleBluetooth() {
        mSettingBluetoothView.postStatusMessage(INIT);
        BluetoothService.getInstance().startBluetoothEngine(this);
        BluetoothService.getInstance().setBluetoothStatusListener(new BluetoothService.BluetoothStatusListener() {
            @Override
            public void autoConnecting() {
                mSettingBluetoothView.postStatusMessage(INIT);
                isBluetoothConnected = false;
                checkedEnterMainActivityEnable();
            }

            @Override
            public void connectSuccess(String deviceName) {
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
        });
        mSettingBluetoothView.getTvSettingRetry().setOnClickListener(v -> BluetoothService.getInstance().startBluetoothEngine(this));
    }

    private void checkedEnterMainActivityEnable() {
        if (isBluetoothConnected && isWifiConnected) {
            mBtAllDone.setEnabled(true);
        } else {
            mBtAllDone.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        BluetoothService.getInstance().cancelDiscoveryAndUnregisterReceiver(this);
        super.onDestroy();
    }

    private void enterMainActivity(View v) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}