package com.chinafocus.hvrskyworthvr.ui.setting;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.ui.widget.SettingViewGroup;

import static com.chinafocus.hvrskyworthvr.ui.widget.SettingViewGroup.*;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        SettingViewGroup settingWifi = findViewById(R.id.view_devices_wifi);
        SettingViewGroup settingBluetooth = findViewById(R.id.view_devices_bluetooth);
        SettingViewGroup settingAlias = findViewById(R.id.view_devices_alias);

        settingWifi.postStatusMessage(CONNECT_CHECK_AGAIN,"adsfa");
        settingBluetooth.postStatusMessage(CONNECTING);
        settingAlias.postStatusMessage(INIT);
    }
}