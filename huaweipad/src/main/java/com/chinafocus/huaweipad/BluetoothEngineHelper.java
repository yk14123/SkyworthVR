package com.chinafocus.huaweipad;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;

public class BluetoothEngineHelper {

    private static final String TAG = "BluetoothEngineService";
    private static final int REQUEST_ENABLE_BT = 10001;

    private BluetoothEngineService bluetoothEngineService;
    private BluetoothAdapter mBluetoothAdapter;


    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothEngineService.STATE_CONNECTED:
                            // 链接成功
                            break;
                        case BluetoothEngineService.STATE_CONNECTING:
                            // 链接中
                            break;
                        case BluetoothEngineService.STATE_LISTEN:
                        case BluetoothEngineService.STATE_NONE:
                            // 等待配对中
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    // TODO 收到byte数据
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.e(TAG, " readMessage :" + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String deviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    // 设备名称
                    break;
                case Constants.MESSAGE_TOAST:
                    // 链接错误
                    break;
            }
        }
    };

    /**
     * 启动蓝牙引擎
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
//                    ensureDiscoverable(activity);
                    // Register for broadcasts when a device is discovered.
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    activity.registerReceiver(receiver, filter);

                    // Register for broadcasts when discovery has finished
                    filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    activity.registerReceiver(receiver, filter);

                    doDiscovery();
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

//    /**
//     * Makes this device discoverable for 300 seconds (5 minutes).
//     * 启用自身设备可检测性
//     */
//    private void ensureDiscoverable(Context context) {
//        if (mBluetoothAdapter.getScanMode() !=
//                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
//            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//            context.startActivity(discoverableIntent);
//        }
//    }

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

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "---------请先初始化BluetoothAdapter---------");
            return;
        }
        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
        Log.d(TAG, "---------开始扫描蓝牙周边---------（对方必须处于蓝牙可见状态）");
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (Objects.requireNonNull(device).getBondState() != BluetoothDevice.BOND_BONDED) {
                    String deviceName = device.getName();
                    String deviceAddress = device.getAddress();
                    if (!TextUtils.isEmpty(deviceName)) {
                        Log.d(TAG, "发现设备 deviceName >>> " + deviceName + " MAC address >>> " + deviceAddress);
                        // 开始链接蓝牙
                        if (deviceName.startsWith("中图云创")) {
                            connectDevice(deviceAddress);
                        }
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "----------蓝牙扫描完毕-----------");
            }
        }
    };

    public synchronized void unBondDevice() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if (deviceName.startsWith("中图云创")) {
                    boolean b = unPairDevice(device);
                    Log.d(TAG, "解除绑定设备 BluetoothDevice 对象 deviceName >>> " + deviceName
                            + " MAC address >>> " + deviceHardwareAddress
                            + " 是否成功 >>> " + b);
                }
            }
        }
    }

    private boolean unPairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getDeclaredMethod("cancelPairing", (Class<?>) null);
            m.setAccessible(true);
            return (boolean) m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
        return false;
    }

    /**
     * 释放引擎
     */
    public void releaseAll(Context context) {
        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // Don't forget to unregister the ACTION_FOUND receiver.
        context.unregisterReceiver(receiver);

        if (bluetoothEngineService != null) {
            bluetoothEngineService.stopEngine();
        }
    }

    public void retryConnect() {
        if (bluetoothEngineService != null) {
            bluetoothEngineService.stopEngine();
        }
        tryConnectBondedDevices();
    }

}
