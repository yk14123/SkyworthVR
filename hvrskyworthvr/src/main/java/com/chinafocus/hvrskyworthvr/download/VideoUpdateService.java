package com.chinafocus.hvrskyworthvr.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * @author yksm
 * @date 2020/8/22
 * description：
 */
public class VideoUpdateService extends Service {

    private final String TAG = VideoUpdateService.class.getSimpleName();
    private DownLoadCreatorManager mDownLoadCreatorManager;
    private DownLoadRunningManager mDownLoadRunningManager;

    public static final String VIDEO_UPDATE_SERVICE_START = "video_update_service_start";
    public static final String VIDEO_UPDATE_SERVICE_RESTART = "video_update_service_restart";
    public static final String VIDEO_UPDATE_SERVICE_CANCEL = "video_update_service_cancel";
    public static final String VIDEO_UPDATE_SERVICE = "video_update_service";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "VideoUpdateService >>> onCreate");
        mDownLoadCreatorManager = DownLoadCreatorManager.getInstance();
        mDownLoadRunningManager = DownLoadRunningManager.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "VideoUpdateService >>> onStartCommand >>> startEngine");
        String stringExtra = intent.getStringExtra(VIDEO_UPDATE_SERVICE);
        if (VIDEO_UPDATE_SERVICE_START.equals(stringExtra)) {
            checkVideoUpdateEngine();
        } else if (VIDEO_UPDATE_SERVICE_RESTART.equals(stringExtra)) {
            restartDownLoadTaskEngine();
        } else if (VIDEO_UPDATE_SERVICE_CANCEL.equals(stringExtra)) {
            cancelDownLoadTaskEngine();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void checkVideoUpdateEngine() {
        if (mDownLoadRunningManager.isDownLoadRunning()) {
            mDownLoadRunningManager.postDownLoadTaskTotal();
            return;
        }

        // 如果在检查中，则不检查
        if (mDownLoadCreatorManager.isDownLoadChecking()) {
            return;
        }
        // 1.拉取网络对比生成download任务
        mDownLoadCreatorManager.checkedTaskAndDownLoad();
    }

    private void restartDownLoadTaskEngine() {
        mDownLoadRunningManager.startDownloadEngine();
    }

    private void cancelDownLoadTaskEngine() {
        mDownLoadCreatorManager.cancelDownLoadEngine();
        mDownLoadRunningManager.cancelDownLoadEngine();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}