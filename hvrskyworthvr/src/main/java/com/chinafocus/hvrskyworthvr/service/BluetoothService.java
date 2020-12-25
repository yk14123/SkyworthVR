package com.chinafocus.hvrskyworthvr.service;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.service.event.VrMainConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMainDisConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMainSyncMediaInfo;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaDisConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaSyncMediaInfo;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaWaitSelected;
import com.chinafocus.hvrskyworthvr.service.event.VrRotation;
import com.chinafocus.hvrskyworthvr.service.event.VrSyncPlayInfo;
import com.chinafocus.lib_bluetooth.BluetoothEngineHelper;
import com.chinafocus.lib_bluetooth.BluetoothEngineService;

import org.greenrobot.eventbus.EventBus;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class BluetoothService {

    private static final int CONNECT = 1;
    private static final int DISCONNECT = 2;
    private static final int SYNC_PLAY = 3;
    private static final int SYNC_ROTATION = 4;
    private static final int SYNC_WAIT_VR_SELECTED = 5;

    private final Executor executor;

    private final BluetoothEngineHelper bluetoothEngineHelper;

    private BluetoothService() {
        bluetoothEngineHelper = new BluetoothEngineHelper(mHandler);
        executor = Executors.newCachedThreadPool();
    }

    private static BluetoothService instance;

    public static BluetoothService getInstance() {
        if (instance == null) {
            synchronized (BluetoothService.class) {
                if (instance == null) {
                    instance = new BluetoothService();
                }
            }
        }
        return instance;
    }

    public void unBondDevice(Activity activity) {
        bluetoothEngineHelper.unBondDevice(activity);
    }

    public void releaseAll(Activity activity) {
        bluetoothEngineHelper.releaseAll(activity);
    }

    public void startBluetoothEngine(Activity activity) {
        bluetoothEngineHelper.startBluetoothEngine(activity);
    }

    public synchronized void sendMessage(int videoTag, int videoCategory, int videoId, long seek) {

        Log.e("MyLog", "发送给服务端的信息" +
                " >>> video_tag : " + videoTag
                + " >>> video_category : " + videoCategory
                + " >>> video_id : " + videoId
                + " >>> seek : " + seek);

        executor.execute(() -> {
            byte[] mediaInfoByte = new byte[34];
            ByteBuffer.wrap(mediaInfoByte).putInt(30);

            ByteBuffer.wrap(mediaInfoByte).putInt(4, 3);
            ByteBuffer.wrap(mediaInfoByte).putInt(8, 3);

            ByteBuffer.wrap(mediaInfoByte).putShort(12, (short) 20);

            ByteBuffer.wrap(mediaInfoByte).putInt(14, videoTag);
            ByteBuffer.wrap(mediaInfoByte).putInt(18, videoCategory);
            ByteBuffer.wrap(mediaInfoByte).putInt(22, videoId);
            ByteBuffer.wrap(mediaInfoByte).putLong(26, seek);

            bluetoothEngineHelper.sendMessage(mediaInfoByte);

        });

    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case com.chinafocus.lib_bluetooth.Constants.MESSAGE_STATE_CHANGE:
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
                case com.chinafocus.lib_bluetooth.Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    break;
                case com.chinafocus.lib_bluetooth.Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    onHandleWork(readBuf, msg.arg1);
//                    Log.e(TAG, " readMessage :" + readMessage);
                    break;
                case com.chinafocus.lib_bluetooth.Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String deviceName = msg.getData().getString(com.chinafocus.lib_bluetooth.Constants.DEVICE_NAME);
                    // 设备名称
                    break;
                case com.chinafocus.lib_bluetooth.Constants.MESSAGE_TOAST:
                    // 链接错误
                    break;
            }
        }
    };

    protected void onHandleWork(byte[] bytes, int len) {

        int cursor = 0;
        int handledTotalMessageLength = 0;

        if (len > 0) {

            while (true) {

                // 队列
                int messageBodyLen = ByteBuffer.wrap(bytes).getInt(cursor);
                int messageLen = messageBodyLen + 4;
                int tag = ByteBuffer.wrap(bytes).getInt(cursor + 4);
                int category = ByteBuffer.wrap(bytes).getInt(cursor + 8);

                Log.e("MyLog", "socketInputStream.read >>> "
                        + " >>> 消息body长度是 : " + messageBodyLen
                        + " >>> 消息类型是 : " + tag
                        + " >>> 消息category是 : " + category
                        + " >>> 消息总长度是 : " + len

                );

                switch (tag) {
                    case CONNECT:
                        handConnect();
                        break;
                    case DISCONNECT:
                        handDisconnect();
                        break;
                    case SYNC_PLAY:
                        // 这里多加了2 是因为unity使用了框架，封装成了object，多了2个short类型
                        handSyncPlay(bytes, cursor + 14);
                        break;
                    case SYNC_ROTATION:
                        handRotation(bytes, cursor + 14);
                        break;
                    case SYNC_WAIT_VR_SELECTED:
                        handWaitVrSelected();
                        break;
                }

                handledTotalMessageLength += messageLen;

                if (len == handledTotalMessageLength) {
                    return;
                } else if (len > handledTotalMessageLength) {
                    // 多条数据
                    cursor = len - handledTotalMessageLength;
                }
            }
        }
    }

    /**
     * VR没有取下来的时候，进入VR选片
     */
    private void handWaitVrSelected() {
        EventBus.getDefault().post(VrMediaWaitSelected.obtain());
    }

    /**
     * 处理断开连接
     */
    private void handDisconnect() {
        if (Constants.ACTIVITY_TAG == Constants.ACTIVITY_MAIN) {
            EventBus.getDefault().post(VrMainDisConnect.obtain());
        } else if (Constants.ACTIVITY_TAG == Constants.ACTIVITY_MEDIA) {
            EventBus.getDefault().post(VrMediaDisConnect.obtain());
        }
    }

    /**
     * 处理连接
     */
    private void handConnect() {
        if (Constants.ACTIVITY_TAG == Constants.ACTIVITY_MAIN) {
            EventBus.getDefault().post(VrMainConnect.obtain());
        } else if (Constants.ACTIVITY_TAG == Constants.ACTIVITY_MEDIA) {
            EventBus.getDefault().post(VrMediaConnect.obtain());
        }
    }

    /**
     * 处理媒体播放信息
     *
     * @param bytes 媒体信息
     */
    private void handSyncPlay(byte[] bytes, int head) {
        int tag = ByteBuffer.wrap(bytes).getInt(head);
        int category = ByteBuffer.wrap(bytes).getInt(head + 4);
        int id = ByteBuffer.wrap(bytes).getInt(head + 8);
        long seek = ByteBuffer.wrap(bytes).getLong(head + 12);

        VrSyncPlayInfo obtain = VrSyncPlayInfo.obtain();

        obtain.tag = tag;
        obtain.category = category;
        obtain.videoId = id;
        obtain.seek = seek;

        Log.e("MyLog", "收到服务端的Media信息 obtain >> " + obtain);

        if (Constants.ACTIVITY_TAG == Constants.ACTIVITY_MAIN) {
            EventBus.getDefault().post(VrMainSyncMediaInfo.obtain());
        } else if (Constants.ACTIVITY_TAG == Constants.ACTIVITY_MEDIA) {
            EventBus.getDefault().post(VrMediaSyncMediaInfo.obtain());
        }
    }

    /**
     * 处理四元数
     *
     * @param rotationByte 旋转信息
     */
    private void handRotation(byte[] rotationByte, int head) {
        VrRotation obtain = VrRotation.obtain();

        obtain.x = ByteBuffer.wrap(rotationByte).getFloat(head);
        obtain.y = ByteBuffer.wrap(rotationByte).getFloat(head + 4);
        obtain.z = ByteBuffer.wrap(rotationByte).getFloat(head + 8);
        obtain.w = ByteBuffer.wrap(rotationByte).getFloat(head + 12);

        EventBus.getDefault().post(obtain);
    }


}
