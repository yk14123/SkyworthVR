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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 创建服务的时候，只执行一次
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "VideoUpdateService >>> onCreate");
        mDownLoadCreatorManager = DownLoadCreatorManager.getInstance();
        mDownLoadRunningManager = DownLoadRunningManager.getInstance();
    }

    /**
     * 多次执行
     *
     * @param intent  intent
     * @param flags   flags
     * @param startId startId
     * @return 1
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "VideoUpdateService >>> onStartCommand >>> startEngine");
        startEngine();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startEngine() {
        // 如果在下载中或者在检查中，则不检查
        if (mDownLoadCreatorManager.isDownLoadChecking() || mDownLoadRunningManager.isDownLoadRunning()) {
            return;
        }
        // 1.拉取网络对比生成download任务
        mDownLoadCreatorManager.checkedVideoUpdateTask();
    }

}
