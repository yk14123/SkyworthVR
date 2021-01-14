package com.chinafocus.skyworthvr.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.ssnwt.vr.androidmanager.AndroidInterface;
import com.ssnwt.vr.androidmanager.bluetooth.BluetoothUtils;
import com.unity3d.player.UnityPlayer;

import java.nio.charset.StandardCharsets;
import java.util.Set;

public class BluetoothEngineHelper {

    private static final String TAG = "BluetoothEngineService";
    private static final int REQUEST_ENABLE_BT = 10001;
    private static final String UNITY_NAME = "BlueToothManager";

    private BluetoothEngineService bluetoothEngineService;
    private BluetoothAdapter mBluetoothAdapter;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothEngineService.STATE_CONNECTED:
                            UnityPlayer.UnitySendMessage(UNITY_NAME, "ReadState", BluetoothEngineService.STATE_CONNECTED + "");
                            break;
                        case BluetoothEngineService.STATE_CONNECTING:
                            UnityPlayer.UnitySendMessage(UNITY_NAME, "ReadState", BluetoothEngineService.STATE_CONNECTING + "");
                            break;
                        case BluetoothEngineService.STATE_LISTEN:
                        case BluetoothEngineService.STATE_NONE:
                            UnityPlayer.UnitySendMessage(UNITY_NAME, "ReadState", 1 + "");
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
//                    String writeMessage = new String(writeBuf);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String s = new String(Base64.encode(readBuf, 0, msg.arg1, Base64.DEFAULT), StandardCharsets.US_ASCII);
                    Log.e(TAG, " readMessage :" + s);
                    UnityPlayer.UnitySendMessage(UNITY_NAME, "ReadData", s);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    // 链接成功的地址
                    UnityPlayer.UnitySendMessage(UNITY_NAME, "ReadDeviceName", msg.getData().getString(Constants.DEVICE_NAME));
                    break;
                case Constants.MESSAGE_TOAST:
                    // 出现错误断开链接
                    UnityPlayer.UnitySendMessage(UNITY_NAME, "ReadState", 4 + "");
                    break;
            }
        }
    };


    /**
     * 初始化蓝牙引擎
     *
     * @param activity 上下文
     */
    public void initBluetoothEngine(Activity activity) {
        BluetoothUtils bluetoothUtils = AndroidInterface.getInstance().getBluetoothUtils();
        if (bluetoothUtils == null) {
            throw new IllegalStateException("------请先初始化  SkyworthAndroidInterface : 调用 SkyworthAndroidInterface.initApplication(Activity activity)------");
        }
        AndroidInterface.getInstance().getBluetoothUtils().resume();
        startBluetoothEngine(activity);
    }

    /**
     * 启动蓝牙引擎
     *
     * @param activity 上下文
     */
    public void startBluetoothEngine(Activity activity) {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // 该设备不支持蓝牙
            // Device doesn't support Bluetooth
            Log.e(TAG, "----------Device doesn't support Bluetooth--------");
        } else {
            // 如果蓝牙没有启用，则开启蓝牙启用页面
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {

                if (bluetoothEngineService == null) {
                    bluetoothEngineService = new BluetoothEngineService(mHandler);
                }
                bluetoothEngineService.start();

                if (!tryConnectBondedDevices()) {
                    ensureDiscoverable(activity);
                }
            }
        }
    }

    private boolean tryConnectBondedDevices() {
        boolean successConnect = false;
        // 查询已配对设备
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            // 返回一组表示已配对设备的 BluetoothDevice 对象
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG, "已配对设备的 BluetoothDevice 对象 deviceName >>> " + deviceName + " MAC address >>> " + deviceHardwareAddress);
                // 开始链接蓝牙
                if (deviceName.startsWith("中图云创")) {
                    connectDevice(deviceHardwareAddress);
                    successConnect = true;
                }
            }
        }
        return successConnect;
    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     * 启用自身设备可检测性
     * 默认是2分钟，最大封顶时间是5分钟（300ms）
     * 时间传0就是永远都可以被发现。不安全
     */
    private void ensureDiscoverable(Context context) {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
            context.startActivity(discoverableIntent);
        }
    }


    /**
     * Establish connection with other device
     */
    private void connectDevice(String address) {
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        bluetoothEngineService.connect(device);
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (bluetoothEngineService.getState() != BluetoothEngineService.STATE_CONNECTED) {
            Log.e(TAG, "---------当前蓝牙链接断开，禁止发送消息---------");
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            bluetoothEngineService.write(send);
        }
    }

    /**
     * Sends a message.
     *
     * @param bytes bytes
     */
    public void sendMessage(byte[] bytes) {
        // Check that we're actually connected before trying anything
        if (bluetoothEngineService.getState() != BluetoothEngineService.STATE_CONNECTED) {
            Log.e(TAG, "---------当前蓝牙链接断开，禁止发送消息---------");
            return;
        }

        // Check that there's actually something to send
        // Get the message bytes and tell the BluetoothChatService to write
        bluetoothEngineService.write(bytes);
    }

    public synchronized void unBondDevice() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if (deviceName.startsWith("中图云创")) {
                    AndroidInterface.getInstance().getBluetoothUtils().unbond(device);
                    Log.d(TAG, "解除绑定设备 BluetoothDevice 对象 deviceName >>> " + deviceName + " MAC address >>> " + deviceHardwareAddress);
                }
            }
        }
    }

    /**
     * 释放蓝牙引擎
     */
    public void releaseBluetoothEngine() {
        if (bluetoothEngineService != null) {
            bluetoothEngineService.stopEngine();
        }
        AndroidInterface.getInstance().getBluetoothUtils().pause();
    }

    public void retryConnect() {
        if (bluetoothEngineService != null) {
            bluetoothEngineService.stopEngine();
        }
        tryConnectBondedDevices();
    }
}
