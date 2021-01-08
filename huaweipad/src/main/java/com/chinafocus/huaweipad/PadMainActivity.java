package com.chinafocus.huaweipad;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PadMainActivity extends AppCompatActivity {

    /**
     * Member object for the chat services
     */
    private TextView textView;
    private StringBuilder stringBuilder;

    private Handler handler = new Handler(Looper.getMainLooper());
    private BluetoothEngineVRHelper bluetoothEngineHelper;
//    private BluetoothEngineHelper bluetoothEngineHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.tv_message);
        stringBuilder = new StringBuilder();

//        AndroidInterface.getInstance().init(getApplication());
//        AndroidInterface.getInstance().getBluetoothUtils().resume();

        bluetoothEngineHelper = new BluetoothEngineVRHelper();
//        bluetoothEngineHelper = new BluetoothEngineHelper();
        bluetoothEngineHelper.initBluetoothEngine(this);
//        bluetoothEngineHelper.startBluetoothEngine(this);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        releaseAll();
        bluetoothEngineHelper.releaseAll();
//        bluetoothEngineHelper.releaseAll(this);
    }


    public void sendString(View view) {

        new Thread(new Runnable() {
            int i;

            @Override
            public void run() {
                while (true) {
                    bluetoothEngineHelper.sendMessage("VR --- > 发送字符串" + ++i);
                    SystemClock.sleep(11);
                }
            }
        }).start();

//        bluetoothEngineHelper.retryConnect();
    }

    public void clearMessage(View view) {
        textView.setText("");
        stringBuilder.delete(0, stringBuilder.length());
    }

    public void connectBluetooth(View view) {
//        bluetoothEngineHelper.sendMessage("pad端发送byte数组".getBytes());
//        bluetoothEngineHelper.connectBondDevice();
    }

    public void resumeBluetooth(View view) {
//        AndroidInterface.getInstance().getBluetoothUtils().resume();
        bluetoothEngineHelper.startBluetoothEngine(this);
    }

    // --------------VR--------------------

    public void bondBluetooth(View view) {
//        bluetoothEngineHelper.silenceBondDevice();
        Intent intent = new Intent();
        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
        startActivity(intent);
    }

    public void unBondBluetooth(View view) {
        bluetoothEngineHelper.unBondDevice();
//        startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));

    }

    public void pauseBluetooth(View view) {
//        AndroidInterface.getInstance().getBluetoothUtils().pause();
    }
}
