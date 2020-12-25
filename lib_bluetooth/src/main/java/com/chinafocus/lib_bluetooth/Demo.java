package com.chinafocus.lib_bluetooth;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Demo
 */
public class Demo extends AppCompatActivity {

    private BluetoothEngineHelper bluetoothEngineHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothEngineHelper = new BluetoothEngineHelper();
        bluetoothEngineHelper.startBluetoothEngine(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothEngineHelper.releaseAll(this);
    }

    public void sendString(View view) {
        bluetoothEngineHelper.sendMessage("Pad端发送给VR端：字符串");
    }

    public void startSyncBluetooth(View view) {
        bluetoothEngineHelper.startBluetoothEngine(this);
    }

    public void unBondBluetooth(View view) {
        bluetoothEngineHelper.unBondDevice(this);
    }

}
