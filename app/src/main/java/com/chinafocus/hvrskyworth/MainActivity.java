package com.chinafocus.hvrskyworth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ssnwt.vr.androidmanager.AndroidInterface;
import com.ssnwt.vr.androidmanager.ProximitySensorUtils;
import com.ssnwt.vr.androidmanager.bluetooth.BluetoothUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends AppCompatActivity implements ProximitySensorUtils.SensorListener, View.OnClickListener {

    private BluetoothDevice mBluetoothDevice;

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("MyLog", "MainActivity onCreate");

        AndroidInterface.getInstance().getProximitySensorUtils().addSensorListener(this);

        findViewById(R.id.bt_search).setOnClickListener(this);
        findViewById(R.id.bt_bond).setOnClickListener(this);
        findViewById(R.id.bt_http).setOnClickListener(this);


        AndroidInterface.getInstance().getBluetoothUtils().setListener(new BluetoothUtils.BluetoothListener() {
            @Override
            public void onOpened(boolean b) {

            }

            @Override
            public void onConnected(boolean b) {

            }

            @Override
            public void onDeviceFound(BluetoothDevice bluetoothDevice) {
                String address = bluetoothDevice.getAddress();
                String name = bluetoothDevice.getName();


                if (TextUtils.isEmpty(name)) {
                    return;
                }
                Log.e("blue", " address >>> " + address + " name >>> " + name);

                if (name.toLowerCase().startsWith("huawei")) {
                    mBluetoothDevice = bluetoothDevice;
                }
            }

            @Override
            public void onBondChanged(BluetoothDevice bluetoothDevice) {

            }

            @Override
            public void onScanStart() {
                Log.e("blue", " onScanStart >>> ");
            }

            @Override
            public void onScanFinish() {
                Log.e("blue", " onScanFinish >>> ");
            }

            @Override
            public void onNoSupportBluetooth() {

            }

            @Override
            public void onBondError(int i) {
                Log.e("blue", " onBondError >>> ");
            }

            @Override
            public void onBluetoothUtilsActive(boolean b) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.e("MyLog", "MainActivity onResume");
        AndroidInterface.getInstance().getBluetoothUtils().resume();


    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.e("MyLog", "MainActivity onPause");
    }

    @Override
    protected void onDestroy() {
        AndroidInterface.getInstance().getProximitySensorUtils().removeSensorListener(this);
        AndroidInterface.getInstance().getBluetoothUtils().pause();
        super.onDestroy();
        Log.e("MyLog", "MainActivity onDestroy");
        AndroidInterface.getInstance().release();
    }

    @Override
    public void onDistanceFar(boolean b) {
        // true 取下眼镜 5cm的距离来判断
        // false 戴上眼镜
        Log.e("MyLog", "onDistanceFar >>> " + b);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_search:
                searchBluetooth();
                break;
            case R.id.bt_bond:
                startBond();
                break;
            case R.id.bt_http:
                startHttp();
                break;
        }

    }

    private void startHttp() {

        BluetoothDevice temp = null;

        List<BluetoothDevice> bondedDevices = AndroidInterface.getInstance().getBluetoothUtils().getBondedDevices();
        for (BluetoothDevice bondedDevice : bondedDevices) {
            if (bondedDevice.getName().toLowerCase().startsWith("huawei")) {
                temp = bondedDevice;
            }
        }
        if (temp == null) {
            return;
        }

        BluetoothDevice finalTemp = temp;
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        BluetoothSocket rfcommSocketToServiceRecord = null;
                        OutputStream outputStream = null;
                        try {
                            rfcommSocketToServiceRecord = finalTemp.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                            Log.e("MyLog", "rfcommSocketToServiceRecord.connect() before");
                            rfcommSocketToServiceRecord.connect();
                            Log.e("MyLog", "rfcommSocketToServiceRecord.connect() after");
                            outputStream = rfcommSocketToServiceRecord.getOutputStream();

                            StringBuilder stringBuffer = new StringBuilder();
                            stringBuffer.append("我是大帅B");
                            int i = 0;

                            while (true) {
                                stringBuffer.append(i++);
                                outputStream.write(stringBuffer.toString().getBytes());
                                stringBuffer.delete(5, stringBuffer.length());
                                SystemClock.sleep(16);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            try {
                                rfcommSocketToServiceRecord.close();
                                outputStream.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
        ).start();

    }

    private void startBond() {
        if (mBluetoothDevice != null) {
            AndroidInterface.getInstance().getBluetoothUtils().bond(mBluetoothDevice);
        }
    }

    private void searchBluetooth() {

        BluetoothUtils bluetoothUtils = AndroidInterface.getInstance().getBluetoothUtils();

        boolean open = bluetoothUtils.isOpen();
        Log.e("blue", " 蓝牙是否打开 >>> " + open);

        if (bluetoothUtils.isSearching()) {
            bluetoothUtils.cancelSearch();
        }

        boolean search = bluetoothUtils.search();
        Log.e("blue", " 蓝牙是否搜索成功 >>> " + search);

//        List<BluetoothDevice> bondedDevices = AndroidInterface.getInstance().getBluetoothUtils().getBondedDevices();
//
//        for (BluetoothDevice bondedDevice : bondedDevices) {
//            String alias = bondedDevice.getAlias();
//            String address = bondedDevice.getAddress();
//            String name = bondedDevice.getName();
//            ParcelUuid[] uuids = bondedDevice.getUuids();
//            UUID uuid = uuids[0].getUuid();
//            Log.e("blue", " alias >>> " + alias + " address >>> " + address + " name >>> " + name + " uuid >>> " + uuid.toString());
//        }
    }
}