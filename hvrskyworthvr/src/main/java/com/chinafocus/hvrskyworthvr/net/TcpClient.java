package com.chinafocus.hvrskyworthvr.net;

import android.content.Context;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpClient {

    private static final TcpClient TCP_CLIENT = new TcpClient();

    public static TcpClient getInstance() {
        return TCP_CLIENT;
    }

    private TcpClient() {
    }

    public Socket socket;

    private ExecutorService executor;
    private OutputStream socketOutputStream;
    private InputStream socketInputStream;

    public void createClient(Context context, final String address, final int port) {

        if (TextUtils.isEmpty(address)) {
            return;
        }

        if (executor == null) {
            executor = Executors.newCachedThreadPool();
        }

        if (socket == null) {

            executor.execute(() -> {
                try {

                    socket = new Socket(address, port);
                    socketOutputStream = socket.getOutputStream();
                    socketInputStream = socket.getInputStream();

                    SystemClock.sleep(1000);
                    Looper.prepare();
                    if (socket.isConnected()) {
                        Toast.makeText(context, "创维VR链接成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "创维VR链接失敗", Toast.LENGTH_SHORT).show();
                    }
                    Looper.loop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void destroyTcp() {
        new Thread(new Runnable() {
            @Override
            public void run() {

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
                if (executor != null) {
                    executor.shutdownNow();
                    executor = null;
                }
            }
        }).start();

    }

    private static final int CONNECT = 1;
    private static final int DISCONNECT = 2;
    private static final int SYNC_PLAY = 3;
    private static final int SYNC_ROTATION = 4;

    public void receiveTcpMessage() {
        if (socket != null && socket.isConnected()) {
            executor.execute(() -> {

                byte[] temp = new byte[1024];
                byte[] intByte = new byte[4];
                byte[] rotationByte = new byte[16];
                byte[] mediaInfoByte = new byte[20];

                while (true) {
                    try {
                        // 该方程一直阻塞！
                        int len = socketInputStream.read(temp);
                        Log.e("MyLog", " len >>> " + len);
                        if (len > 0) {
                            int tag = ByteBuffer.wrap(temp).getInt(4);

                            switch (tag) {
                                case CONNECT:
                                    handConnect();
                                    break;
                                case DISCONNECT:
                                    handDisconnect();
                                    break;
                                case SYNC_PLAY:
                                    ByteBuffer.wrap(temp).get(mediaInfoByte, 12, mediaInfoByte.length);
                                    handSyncPlay(mediaInfoByte);
                                    break;
                                case SYNC_ROTATION:
                                    ByteBuffer.wrap(temp).get(rotationByte, 12, rotationByte.length);
                                    handRotation(rotationByte);
                                    break;
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        destroyTcp();
                        return;
                    }
                }
            });
        }

    }

    /**
     * 处理断开连接
     */
    private void handDisconnect() {
        Log.e("MyLog", " handDisconnect success!");
    }

    /**
     * 处理连接
     */
    private void handConnect() {
        Log.e("MyLog", " handConnect success!");
    }

    private static final int PUBLISH = 1;
    private static final int VIDEO = 2;

    /**
     * 处理媒体播放信息
     *
     * @param mediaInfoByte
     */
    private void handSyncPlay(byte[] mediaInfoByte) {
        int tag = ByteBuffer.wrap(mediaInfoByte).getInt();
        int category = ByteBuffer.wrap(mediaInfoByte).getInt(4);
        int id = ByteBuffer.wrap(mediaInfoByte).getInt(8);
        long seek = ByteBuffer.wrap(mediaInfoByte).getLong(12);
    }

    /**
     * 处理四元数
     *
     * @param rotationByte
     */
    private void handRotation(byte[] rotationByte) {
        float x = ByteBuffer.wrap(rotationByte).getFloat();
        float y = ByteBuffer.wrap(rotationByte).getFloat(4);
        float z = ByteBuffer.wrap(rotationByte).getFloat(8);
        float w = ByteBuffer.wrap(rotationByte).getFloat(12);
    }

    public void sendTcpMessage(int tag, int category, int id, long seek) {
        if (socket != null && socket.isConnected()) {

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] bytes = new byte[20];

                        ByteBuffer.wrap(bytes).putInt(tag);
                        ByteBuffer.wrap(bytes).putInt(4, category);
                        ByteBuffer.wrap(bytes).putInt(8, id);
                        ByteBuffer.wrap(bytes).putLong(12, seek);

                        socketOutputStream.write(bytes);
                        socketOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}