package com.chinafocus.skyworthvr;

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
        bluetoothEngineHelper.initBluetoothEngine(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothEngineHelper.releaseAll();
    }

    public void sendString(View view) {
        bluetoothEngineHelper.sendMessage("VR端发送给Pad端：字符串");
    }


    public void startSyncBluetooth(View view) {
        bluetoothEngineHelper.startBluetoothEngine(this);
    }

    // --------------VR--------------------
    public void unBondBluetooth(View view) {
        bluetoothEngineHelper.unBondDevice();
    }

}
