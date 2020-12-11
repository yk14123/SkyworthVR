package com.chinafocus.hvrskyworthvr.service;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;

import com.chinafocus.hvrskyworthvr.service.event.VrConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrDisConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrRotation;
import com.chinafocus.hvrskyworthvr.service.event.VrSyncPlayInfo;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Queue;


public class SocketService extends JobIntentService {

    private static final int JOB_ID = 1000;

    public Socket socket;
    private OutputStream socketOutputStream;
    private InputStream socketInputStream;

    private static final int CONNECT = 1;
    private static final int DISCONNECT = 2;
    private static final int SYNC_PLAY = 3;
    private static final int SYNC_ROTATION = 4;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, SocketService.class, JOB_ID, work);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        if (intent != null) {
            String value = intent.getStringExtra("onStartCommand");
            Log.e("MyLog", " SocketService onStartCommand >>> " + value);

            try {
                if (socketOutputStream != null) {
                    socketOutputStream.write(1);
                    socketOutputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    @SuppressWarnings("all")
    protected void onHandleWork(@NonNull Intent intent) {

        String address = intent.getStringExtra("address");
        int port = intent.getIntExtra("port", -1);

        try {
            socket = new Socket(address, port);
            socketOutputStream = socket.getOutputStream();
            socketInputStream = socket.getInputStream();

            byte[] pools = new byte[1024];
            byte[] emptyPools = new byte[1024];
            byte[] rotationByte = new byte[16];
            byte[] mediaInfoByte = new byte[20];

            int messageHeadLen = 4;

            while (true) {

                // 该方程一直阻塞！
                int len = socketInputStream.read(pools);

                if (len > 0) {

                    while (true) {
                        // 队列
                        int messageBodyLen = ByteBuffer.wrap(pools).getInt();
                        int messageLen = messageHeadLen + messageBodyLen;
                        int tag = ByteBuffer.wrap(pools).getInt(4);

                        switch (tag) {
                            case CONNECT:
                                handConnect();
                                break;
                            case DISCONNECT:
                                handDisconnect();
                                break;
                            case SYNC_PLAY:
                                System.arraycopy(pools, 12, mediaInfoByte, 0, mediaInfoByte.length);
                                handSyncPlay(mediaInfoByte);
                                break;
                            case SYNC_ROTATION:
                                System.arraycopy(pools, 12, rotationByte, 0, rotationByte.length);
                                handRotation(rotationByte);
                                break;
                        }

                        if (len == messageLen) {
                            // 清空pools
                            System.arraycopy(emptyPools, 0, pools, 0, emptyPools.length);
                            break;
                        } else if (len > messageLen) {
                            // 多条数据
                            len -= messageLen;
                            System.arraycopy(pools, messageLen, pools, 0, len);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeAll();
        }

    }

    /**
     * 处理断开连接
     */
    private void handDisconnect() {
        EventBus.getDefault().post(VrDisConnect.obtain());
    }

    /**
     * 处理连接
     */
    private void handConnect() {
        EventBus.getDefault().post(VrConnect.obtain());
    }

    private static final int PUBLISH = 1;
    private static final int VIDEO = 2;

    /**
     * 处理媒体播放信息
     *
     * @param mediaInfoByte 媒体信息
     */
    private void handSyncPlay(byte[] mediaInfoByte) {
        int tag = ByteBuffer.wrap(mediaInfoByte).getInt();
        int category = ByteBuffer.wrap(mediaInfoByte).getInt(4);
        int id = ByteBuffer.wrap(mediaInfoByte).getInt(8);
        long seek = ByteBuffer.wrap(mediaInfoByte).getLong(12);

        VrSyncPlayInfo obtain = VrSyncPlayInfo.obtain();
        if (tag == PUBLISH) {
            obtain.tag = "publish";
            obtain.category = -1;
        } else if (tag == VIDEO) {
            obtain.tag = "video";
            obtain.category = category;
        }
        obtain.videoId = id;
        obtain.seek = seek;
        EventBus.getDefault().post(obtain);

    }

    /**
     * 处理四元数
     *
     * @param rotationByte 旋转信息
     */
    private void handRotation(byte[] rotationByte) {
        VrRotation obtain = VrRotation.obtain();

        obtain.x = ByteBuffer.wrap(rotationByte).getFloat();
        obtain.y = ByteBuffer.wrap(rotationByte).getFloat(4);
        obtain.z = ByteBuffer.wrap(rotationByte).getFloat(8);
        obtain.w = ByteBuffer.wrap(rotationByte).getFloat(12);

        EventBus.getDefault().post(obtain);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        closeAll();
    }

    private void closeAll() {
        if (socketInputStream != null) {
            try {
                socketInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                socketInputStream = null;
            }
        }

        if (socketOutputStream != null) {
            try {
                socketOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                socketOutputStream = null;
            }
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                socket = null;
            }
        }
    }

}
