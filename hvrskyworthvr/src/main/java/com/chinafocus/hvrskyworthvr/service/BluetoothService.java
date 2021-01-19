package com.chinafocus.hvrskyworthvr.service;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.SPUtils;
import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.model.multibean.DeviceInfoManager;
import com.chinafocus.hvrskyworthvr.service.event.VrMainConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMainDisConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMainSyncMediaInfo;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaDisConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaSyncMediaInfo;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaWaitSelected;
import com.chinafocus.hvrskyworthvr.service.event.VrRotation;
import com.chinafocus.hvrskyworthvr.service.event.VrSyncMediaStatus;
import com.chinafocus.hvrskyworthvr.service.event.VrSyncPlayInfo;
import com.chinafocus.lib_bluetooth.BluetoothEngineHelper;
import com.chinafocus.lib_bluetooth.BluetoothEngineService;

import org.greenrobot.eventbus.EventBus;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.chinafocus.hvrskyworthvr.global.Constants.DEVICE_UUID;
import static com.chinafocus.hvrskyworthvr.global.Constants.VR_OFFLINE;
import static com.chinafocus.hvrskyworthvr.global.Constants.VR_ONLINE;
import static com.chinafocus.lib_bluetooth.Constants.MESSAGE_DEVICE_NAME;
import static com.chinafocus.lib_bluetooth.Constants.MESSAGE_RETRY;
import static com.chinafocus.lib_bluetooth.Constants.MESSAGE_STATE_CHANGE;
import static com.chinafocus.lib_bluetooth.Constants.MESSAGE_TOAST;


public class BluetoothService implements BluetoothEngineService.AsyncThreadReadBytes {

    private static final int CONNECT = 1;
    private static final int DISCONNECT = 2;
    private static final int SYNC_PLAY = 3;
    private static final int SYNC_ROTATION = 4;
    private static final int SYNC_WAIT_VR_SELECTED = 5;
    private static final int SYNC_MEDIA_PLAY_STATUS = 6;
    private static final int SYNC_DEVICE_UUID = 7;
    private static final int BLUETOOTH_RETRY_CONNECT = 8;

    private static final int MESSAGE_CONNECT = 10001;
    private static final int MESSAGE_DISCONNECT = 10002;
    private static final int MESSAGE_SYNC_PLAY = 10003;
    private static final int MESSAGE_SYNC_WAIT_VR_SELECTED = 10004;
    private static final int MESSAGE_SYNC_WAIT_PLAY_STATUS = 10005;
    private static final int MESSAGE_SYNC_DEVICE_UUID = 10006;

    private final ExecutorService executor;

    private final BluetoothEngineHelper bluetoothEngineHelper;

    private int bluetoothCurrentStatus;
    private String currentBluetoothDeviceName;

    private BluetoothService() {
        bluetoothEngineHelper = new BluetoothEngineHelper(mHandler, this);
        executor = Executors.newSingleThreadExecutor();
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

    public void onStart(Activity activity) {
        if (bluetoothCurrentStatus == BluetoothEngineService.STATE_CONNECTED) {
            if (mBluetoothStatusListener != null) {
                mBluetoothStatusListener.connectedDeviceName(currentBluetoothDeviceName);
            }
        } else {
            if (mBluetoothStatusListener != null) {
                mBluetoothStatusListener.autoConnecting();
            }
            BluetoothService.getInstance().startBluetoothEngine(activity);
        }
    }

    public void unBondDevice(Context activity) {
        bluetoothEngineHelper.unBondDevice(activity);
    }

    public void releaseAll(Activity activity) {
        bluetoothEngineHelper.releaseAll(activity);
    }

    public void cancelDiscoveryAndUnregisterReceiver(Activity activity) {
        bluetoothEngineHelper.cancelDiscoveryAndUnregisterReceiver(activity);
    }

    public void startBluetoothEngine(Activity activity) {
        bluetoothEngineHelper.startBluetoothEngine(activity);
    }

    /**
     * 蓝牙重连
     */
    private void sendBluetoothRetryConnect() {
        Log.d("MyLog", "-----发送给VR端的uuid " + " >>> 蓝牙重连 ");
        sendUUIDMessage(1, "bluetoothRetryConnect", BLUETOOTH_RETRY_CONNECT);
    }

    private void sendUUIDMessage(int tag, @NonNull String uuid) {
        sendUUIDMessage(tag, uuid, SYNC_DEVICE_UUID);
    }


    /**
     * message格式是 {int:totalBody长度,int:消息类型,int:消息二级分类,short:MessageBody长度,int:tag,string:UUID}
     *
     * @param tag        UUid是否存在
     * @param uuid       uuid值
     * @param messageTag messageTag
     */
    private void sendUUIDMessage(int tag, @NonNull String uuid, int messageTag) {

        Log.d("MyLog", "-----发送给VR端的uuid" +
                " >>> pad是否存在uuid值（-1不存在，1存在） : " + tag
                + " >>> uuid : " + uuid);

        executor.execute(() -> {

            byte[] uuidBytes = uuid.getBytes();
            int uuidLength = uuidBytes.length;
            byte[] mediaInfoByte = new byte[22 + uuidLength];
            System.arraycopy(uuidBytes, 0, mediaInfoByte, 22, uuidLength);

            ByteBuffer.wrap(mediaInfoByte).putInt(14 + uuidLength); //长度表示totalBody
            ByteBuffer.wrap(mediaInfoByte).putInt(4, messageTag);
            ByteBuffer.wrap(mediaInfoByte).putInt(8, messageTag);

            ByteBuffer.wrap(mediaInfoByte).putShort(12, (short) (uuidLength + 8));//MessageBody长度
            ByteBuffer.wrap(mediaInfoByte).putInt(14, tag);
            ByteBuffer.wrap(mediaInfoByte).putInt(18, uuidLength);

            bluetoothEngineHelper.sendMessage(mediaInfoByte);


        });

    }

    public void sendMessage(int videoTag, int videoCategory, int videoId, long seek) {

        Log.d("MyLog", "-----发送给VR端的信息" +
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

    private BluetoothStatusListener mBluetoothStatusListener;

    public void registerBluetoothStatusListener(BluetoothStatusListener bluetoothStatusListener) {
        mBluetoothStatusListener = bluetoothStatusListener;
    }

    public interface BluetoothStatusListener {
        void autoConnecting();

        void connectedDeviceName(String deviceName);

        void connectError();

        void onSyncUUIDSuccess(String uuid);

    }

    /**
     * 第一次联通蓝牙会同步UUID。
     */
    private boolean isStartSyncUUIDOnce;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    bluetoothCurrentStatus = msg.arg1;
                    switch (msg.arg1) {
                        case BluetoothEngineService.STATE_CONNECTED:
//                            if (mBluetoothStatusListener != null) {
//                                mBluetoothStatusListener.connectedSuccess();
//                            }
                            if (!isStartSyncUUIDOnce) {
                                isStartSyncUUIDOnce = true;
                                BluetoothService.getInstance().startSynchronizedUUID();
                            }
                            // 链接成功
                            break;
                        case BluetoothEngineService.STATE_CONNECTING:
                            // 链接中
                            break;
                        case BluetoothEngineService.STATE_LISTEN:
                        case BluetoothEngineService.STATE_NONE:
                            // 等待配对中
                            if (mBluetoothStatusListener != null) {
                                mBluetoothStatusListener.autoConnecting();
                            }
                            currentBluetoothDeviceName = null;
                            break;
                    }
                    break;
//                case com.chinafocus.lib_bluetooth.Constants.MESSAGE_WRITE:
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    // construct a string from the buffer
//                    break;
//                case com.chinafocus.lib_bluetooth.Constants.MESSAGE_READ:
//                    byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
//                    onHandleWork(readBuf, msg.arg1);
//                    Log.e(TAG, " readMessage :" + readMessage);
//                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    currentBluetoothDeviceName = msg.getData().getString(com.chinafocus.lib_bluetooth.Constants.DEVICE_NAME);
                    // 设备名称
                    if (mBluetoothStatusListener != null) {
                        mBluetoothStatusListener.connectedDeviceName(currentBluetoothDeviceName);
                    }
                    break;
                case MESSAGE_RETRY:
                    // 链接错误
                    if (mBluetoothStatusListener != null) {
                        mBluetoothStatusListener.connectError();
                    }
                    break;
                case MESSAGE_TOAST:
                    // 链接错误
                    break;
                case MESSAGE_CONNECT:
                    postConnect();
                    break;
                case MESSAGE_DISCONNECT:
                    postDisconnect();
                    break;
                case MESSAGE_SYNC_PLAY:
                    postSyncPlay();
                    break;
                case MESSAGE_SYNC_WAIT_VR_SELECTED:
                    postWaitVrSelected();
                    break;
                case MESSAGE_SYNC_WAIT_PLAY_STATUS:
                    postPlayStatus((int) msg.obj);
                    break;
                case MESSAGE_SYNC_DEVICE_UUID:
                    postSyncDeviceUUID((String) msg.obj);
                    break;
            }
        }

    };

    private void postSyncDeviceUUID(String uuid) {
        if (mBluetoothStatusListener != null) {
            mBluetoothStatusListener.onSyncUUIDSuccess(uuid);
        }
        sendBluetoothRetryConnect();

        DeviceInfoManager.getInstance().postDeviceUUID(uuid);

        // 检查网络接口是否可以正常访问！
        WifiService.getInstance().initDeviceInfo();
    }

    /**
     * 发送播放/暂停事件
     *
     * @param playTag 1.播放 2.暂停
     */
    private void postPlayStatus(int playTag) {
        Log.d("MyLog", "------收到VR的播放/暂停需求 >>> " + playTag);
        VrSyncMediaStatus obtain = VrSyncMediaStatus.obtain();
        obtain.setPlayStatusTag(playTag);
        EventBus.getDefault().post(VrSyncMediaStatus.obtain());
    }

    private void onHandleWork(byte[] bytes, int len) {

        int cursor = 0;

        while (len > cursor) {

            // 队列
            int messageBodyLen = ByteBuffer.wrap(bytes).getInt(cursor);
            int messageLen = messageBodyLen + 4;
            int eventTag = ByteBuffer.wrap(bytes).getInt(cursor + 4);
//            int category = ByteBuffer.wrap(bytes).getInt(cursor + 8);

            if (len > 100 && eventTag == SYNC_ROTATION) {
                Log.i("MyLog", "socketInputStream.read"
                        + " >>> 消息类型是 : " + eventTag
                        + " >>> 消息总长度是 : " + len
                        + " >>> cursor : " + cursor
                );
            } else if (eventTag != SYNC_ROTATION) {
                Log.i("MyLog", "socketInputStream.read"
                        + " >>> 消息类型是 : " + eventTag
                        + " >>> 消息总长度是 : " + len
                        + " >>> cursor : " + cursor
                );
            }

            switch (eventTag) {
                case CONNECT:
                    mHandler.obtainMessage(MESSAGE_CONNECT).sendToTarget();
                    break;
                case DISCONNECT:
                    mHandler.obtainMessage(MESSAGE_DISCONNECT).sendToTarget();
                    break;
                case SYNC_PLAY:
                    // 这里多加了2 是因为unity使用了框架，封装成了object，多了2个short类型
                    handSyncPlay(bytes, cursor + 14);
                    mHandler.obtainMessage(MESSAGE_SYNC_PLAY).sendToTarget();
                    break;
                case SYNC_ROTATION:
                    handRotation(bytes, cursor + 14);
                    break;
                case SYNC_WAIT_VR_SELECTED:
                    mHandler.obtainMessage(MESSAGE_SYNC_WAIT_VR_SELECTED).sendToTarget();
                    break;
                case SYNC_MEDIA_PLAY_STATUS:
                    int i = handMediaStatus(bytes, cursor + 14);
                    Log.d("MyLog", " ------------------------- i >>> " + i);
                    mHandler.obtainMessage(MESSAGE_SYNC_WAIT_PLAY_STATUS, i).sendToTarget();
                    break;
                case SYNC_DEVICE_UUID:
                    handSyncUUID(bytes, cursor + 14);
                    break;
            }

            cursor += messageLen;
        }
    }

    /**
     * 通过蓝牙开启双端同步UUID
     */
    public void startSynchronizedUUID() {
        String string = SPUtils.getInstance().getString(DEVICE_UUID);
        if (TextUtils.isEmpty(string)) {
            // pad无
            sendUUIDMessage(-1, "NoUUID");
        } else {
            // pad有
            sendUUIDMessage(1, string);
        }
    }

    // int:totalBody长度 int:tag类型 int:category类型 short:messageBody长度 int:是否有UUID int:uuid长度 string：uuid值
    private void handSyncUUID(byte[] bytes, int tagHead) {
        int tag = ByteBuffer.wrap(bytes).getInt(tagHead);

        if (tag == -1) {
            // 创建UUID
            String createUUID = UUID.randomUUID().toString().replace("-", "");
            SPUtils.getInstance().put(DEVICE_UUID, createUUID);
            sendUUIDMessage(1, createUUID);

        } else if (tag == 1) {

            int uuidLen = ByteBuffer.wrap(bytes).getInt(tagHead + 4);
            byte[] uuidBytes = new byte[uuidLen];
            System.arraycopy(bytes, tagHead + 8, uuidBytes, 0, uuidLen);

            String uuid = new String(uuidBytes);
            SPUtils.getInstance().put(DEVICE_UUID, uuid);

            Log.d("MyLog", "------收到VR端的UUID <<< tag == 1 uuid : " + uuid + " uuidLen <<< " + uuidLen);

            mHandler.obtainMessage(MESSAGE_SYNC_DEVICE_UUID, uuid).sendToTarget();
        }
    }

    /**
     * VR没有取下来的时候，进入VR选片
     */
    private void postWaitVrSelected() {
        EventBus.getDefault().post(VrMediaWaitSelected.obtain());
    }

    /**
     * 处理断开连接
     */
    private void postDisconnect() {
        if (Constants.ACTIVITY_TAG == Constants.ACTIVITY_MAIN) {
            EventBus.getDefault().post(VrMainDisConnect.obtain());
        } else if (Constants.ACTIVITY_TAG == Constants.ACTIVITY_MEDIA) {
            EventBus.getDefault().post(VrMediaDisConnect.obtain());
        }
        Constants.CURRENT_VR_ONLINE_STATUS = VR_OFFLINE;
    }

    /**
     * 处理连接
     */
    private void postConnect() {
        if (Constants.ACTIVITY_TAG == Constants.ACTIVITY_MAIN) {
            EventBus.getDefault().post(VrMainConnect.obtain());
        } else if (Constants.ACTIVITY_TAG == Constants.ACTIVITY_MEDIA) {
            EventBus.getDefault().post(VrMediaConnect.obtain());
        }
        Constants.CURRENT_VR_ONLINE_STATUS = VR_ONLINE;
    }

    /**
     * 处理媒体播放信息
     */
    private void postSyncPlay() {
        if (Constants.ACTIVITY_TAG == Constants.ACTIVITY_MAIN) {
            EventBus.getDefault().post(VrMainSyncMediaInfo.obtain());
        } else if (Constants.ACTIVITY_TAG == Constants.ACTIVITY_MEDIA) {
            EventBus.getDefault().post(VrMediaSyncMediaInfo.obtain());
        }
    }

    /**
     * 处理视频播放和暂停
     */
    private int handMediaStatus(byte[] bytes, int head) {
        return ByteBuffer.wrap(bytes).getInt(head);
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

        obtain.saveAllState(tag, category, id, seek);
        Log.d("MyLog", "-----收到VR端同步过来的Media信息 obtain >> " + obtain);
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

    @Override
    public void asyncThreadReadBytes(byte[] bytes, int len) {
        onHandleWork(bytes, len);
    }
}
