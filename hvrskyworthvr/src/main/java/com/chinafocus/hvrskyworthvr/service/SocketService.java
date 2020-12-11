package com.chinafocus.hvrskyworthvr.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;

import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.service.event.VrMainConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMainDisConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMainSyncMediaInfo;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaDisConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaSyncMediaInfo;
import com.chinafocus.hvrskyworthvr.service.event.VrRotation;
import com.chinafocus.hvrskyworthvr.service.event.VrSyncPlayInfo;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity.MEDIA_CATEGORY_TAG;
import static com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity.MEDIA_FROM_TAG;
import static com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity.MEDIA_ID;
import static com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity.MEDIA_SEEK;


public class SocketService extends JobIntentService {

    private static final int JOB_ID = 1000;

    public Socket socket;
    private OutputStream socketOutputStream;
    private InputStream socketInputStream;

    private static final int CONNECT = 1;
    private static final int DISCONNECT = 2;
    private static final int SYNC_PLAY = 3;
    private static final int SYNC_ROTATION = 4;

    private Executor executor;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, SocketService.class, JOB_ID, work);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        if (intent != null) {

            int video_tag = intent.getIntExtra(MEDIA_FROM_TAG, -1);
            int video_category = intent.getIntExtra(MEDIA_CATEGORY_TAG, -1);
            int video_id = intent.getIntExtra(MEDIA_ID, -1);
            long seek = intent.getLongExtra(MEDIA_SEEK, 0);

            Log.e("MyLog", "发送给服务端的信息" +
                    " >>> video_tag : " + video_tag
                    + " >>> video_category : " + video_category
                    + " >>> video_id : " + video_id
                    + " >>> seek : " + seek);

            if (socketOutputStream != null) {

                executor.execute(() -> {
                    byte[] mediaInfoByte = new byte[34];
                    ByteBuffer.wrap(mediaInfoByte).putInt(30);

                    ByteBuffer.wrap(mediaInfoByte).putInt(4, 3);
                    ByteBuffer.wrap(mediaInfoByte).putInt(8, 3);

                    ByteBuffer.wrap(mediaInfoByte).putShort(12, (short) 20);

                    ByteBuffer.wrap(mediaInfoByte).putInt(14, video_tag);
                    ByteBuffer.wrap(mediaInfoByte).putInt(18, video_category);
                    ByteBuffer.wrap(mediaInfoByte).putInt(22, video_id);
                    ByteBuffer.wrap(mediaInfoByte).putLong(26, seek);

                    try {

                        socketOutputStream.write(mediaInfoByte);
                        socketOutputStream.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

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

            if (socket.isConnected()) {
                Log.e("MyLog", "VR链接成功");
            } else {
                Log.e("MyLog", "VR链接失败");
            }

            executor = Executors.newCachedThreadPool();

            byte[] pools = new byte[1024];
            byte[] emptyPools = new byte[1024];
            byte[] rotationByte = new byte[16];
            byte[] mediaInfoByte = new byte[20];

            int messageHeadLen = 4;

            while (true) {

//                Log.e("MyLog", "socketInputStream.read >>> 阻塞中 ");

                // 该方程一直阻塞！
                int len = socketInputStream.read(pools);

                if (len > 0) {

                    while (true) {
                        // 队列
                        int messageBodyLen = ByteBuffer.wrap(pools).getInt();
                        int messageLen = messageHeadLen + messageBodyLen;
                        int tag = ByteBuffer.wrap(pools).getInt(4);
                        int category = ByteBuffer.wrap(pools).getInt(8);

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
                                System.arraycopy(pools, 14, mediaInfoByte, 0, mediaInfoByte.length);
                                handSyncPlay(mediaInfoByte);
                                break;
                            case SYNC_ROTATION:
                                System.arraycopy(pools, 14, rotationByte, 0, rotationByte.length);
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
        Observable.just(Constants.ACTIVITY_TAG)
                .subscribeOn(Schedulers.trampoline())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        if (integer.equals(Constants.ACTIVITY_MAIN)) {
                            EventBus.getDefault().post(VrMainDisConnect.obtain());
                        } else if (integer.equals(Constants.ACTIVITY_MEDIA)) {
                            EventBus.getDefault().post(VrMediaDisConnect.obtain());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 处理连接
     */
    private void handConnect() {
        Observable.just(Constants.ACTIVITY_TAG)
                .subscribeOn(Schedulers.trampoline())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        if (integer.equals(Constants.ACTIVITY_MAIN)) {
                            EventBus.getDefault().post(VrMainConnect.obtain());
                        } else if (integer.equals(Constants.ACTIVITY_MEDIA)) {
                            EventBus.getDefault().post(VrMediaConnect.obtain());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

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

        obtain.tag = tag;
        obtain.category = category;
        obtain.videoId = id;
        obtain.seek = seek;

        Log.e("MyLog", "收到服务端的Media信息 obtain >> " + obtain);

        Observable.just(Constants.ACTIVITY_TAG)
                .subscribeOn(Schedulers.trampoline())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Integer integer) {
                        if (integer.equals(Constants.ACTIVITY_MAIN)) {
                            EventBus.getDefault().post(VrMainSyncMediaInfo.obtain());
                        } else if (integer.equals(Constants.ACTIVITY_MEDIA)) {
                            EventBus.getDefault().post(VrMediaSyncMediaInfo.obtain());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
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

        Observable.just(1)
                .subscribeOn(Schedulers.trampoline())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        EventBus.getDefault().post(obtain);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
