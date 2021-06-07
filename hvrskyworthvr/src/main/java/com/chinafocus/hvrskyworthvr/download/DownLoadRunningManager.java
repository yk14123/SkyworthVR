package com.chinafocus.hvrskyworthvr.download;

import android.graphics.Color;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.Utils;
import com.chinafocus.hvrskyworthvr.global.ConfigManager;
import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.hvrskyworthvr.service.event.download.VideoUpdateManagerStatus;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.DownloadCallback;
import com.chinafocus.lib_network.net.errorhandler.HttpErrorHandler;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static com.chinafocus.hvrskyworthvr.download.VideoUpdateStatus.COMPLETED;
import static com.chinafocus.hvrskyworthvr.download.VideoUpdateStatus.DOWNLOADING;
import static com.chinafocus.hvrskyworthvr.download.VideoUpdateStatus.RETRY;
import static com.chinafocus.hvrskyworthvr.download.VideoUpdateStatus.START;


public class DownLoadRunningManager {

    private Disposable mRealDownLoadSubscribe;
    private Disposable mDownLoadEngineSubscribe;

    private DownLoadRunningManager() {
        this.mDownLoadTaskTotal = new ArrayList<>();
    }

    private static DownLoadRunningManager INSTANCE = new DownLoadRunningManager();

    public static DownLoadRunningManager getInstance() {
        return INSTANCE;
    }

    public boolean isDownLoadRunning() {
        return isDownLoadRunning;
    }

    // 重试次数
    private static final int retryCount = 5;
    private static final int retryTimeOut = 5000;
    // 当前下载速度
    private long mSpeedByte;
    // 是否正在下载中
    private boolean isDownLoadRunning;
    // 下载列表
    private List<DownLoadHolder> mDownLoadTaskTotal;

    /**
     * 设置下载任务
     *
     * @param downLoadTaskTotal 下载任务
     */
    public void setDownLoadTaskTotal(List<DownLoadHolder> downLoadTaskTotal) {
        mDownLoadTaskTotal.clear();

        for (int i = 0; i < downLoadTaskTotal.size(); i++) {
            try {
                DownLoadHolder clone = downLoadTaskTotal.get(i).clone();
                clone.setPos(i);
                mDownLoadTaskTotal.add(clone);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    public void postDownLoadTaskTotal() {
        EventBus.getDefault().post(mDownLoadTaskTotal);
        EventBus.getDefault().post(VideoUpdateManagerStatus.obtain());
    }

    private DownLoadHolder mRetryDownLoadHolder;

    /**
     * 下载引擎
     */
    public void startDownloadEngine() {
        if (mDownLoadTaskTotal.size() > 0) {

            EventBus.getDefault().post(mDownLoadTaskTotal);
            VideoUpdateManagerStatus.obtain().setTotal(mDownLoadTaskTotal.size());
            if (mRetryDownLoadHolder != null) {
                int tempPos = mRetryDownLoadHolder.getPos();
                VideoUpdateManagerStatus.obtain().setCurrentIndex(++tempPos);
            } else {
                VideoUpdateManagerStatus.obtain().setCurrentIndex(1);
            }
            VideoUpdateManagerStatus.obtain().setVideoUpdateStatus(START);
            EventBus.getDefault().post(VideoUpdateManagerStatus.obtain());

            isDownLoadRunning = true;
            // 开始下载
            mDownLoadEngineSubscribe = Observable
                    .fromIterable(mDownLoadTaskTotal)
                    .filter(DownLoadHolder::isShouldDownload)
                    .subscribeOn(Schedulers.io())
                    // 配合retry断点续下使用
                    .doOnNext(this::calculateFileRange)
                    .doOnNext(this::realDownLoad)
                    .doOnNext(downLoadHolder -> {
                        mRetryDownLoadHolder = null;
                        if (downLoadHolder.isShouldDownload()) {
                            mRetryDownLoadHolder = downLoadHolder;
                            throw new IllegalArgumentException("----------只要没有下载完成，就抛出异常----------");
                        }
                    })
                    .retry(retryCount, throwable -> {
                        Log.e("MyLog", "---------- 等待5秒后 重试retry >>> ");
                        SystemClock.sleep(retryTimeOut);
                        return true;
                    })
                    .doOnNext(downLoadHolder -> {
                        // TODO 下载完其中一个
                        Log.e("MyLog", " ------------ 下载完其中一个 >>> " + downLoadHolder.getTitle());
                        File src = new File(downLoadHolder.getOutputPath());
                        File dest = new File(downLoadHolder.getFinalPath());
                        boolean b = src.renameTo(dest);
                        if (b) {
                            Log.e("MyLog", " ------------ 从temp目录移动到 video目录 成功 >>>  " + downLoadHolder.getTitle());
                        } else {
                            // TODO 如果出现了源文件已经存在。那么移动失败。需要把源文件删除再移动
                            Log.e("MyLog", " ------------ 从temp目录移动到 video目录 失败 >>> " + downLoadHolder.getTitle());
                        }
                    })
                    .doOnComplete(() -> {
                        // TODO 全部任务完成
                        Log.e("MyLog", " ------------ 所有任务全部下载完成 >>> ");
                        isDownLoadRunning = false;
                        VideoUpdateManagerStatus.obtain().setVideoUpdateStatus(COMPLETED);
                        EventBus.getDefault().post(VideoUpdateManagerStatus.obtain());
                    })
                    .doOnError(throwable -> {
                        // TODO 下载失败
                        Log.e("MyLog", " ------------ 整体下载结束  抛出错误 >>> " + throwable.getMessage());
//                        isDownLoadRunning = false;
                        VideoUpdateManagerStatus.obtain().setVideoUpdateStatus(RETRY);
                        EventBus.getDefault().post(VideoUpdateManagerStatus.obtain());

                        if (mRetryDownLoadHolder != null) {
                            mRetryDownLoadHolder.setCurrentStatus("等待继续下载");
                            mRetryDownLoadHolder.setCurrentStatusColor(Color.parseColor("#FFA0A0A3"));
                            mRetryDownLoadHolder.setProgressingColor(Color.parseColor("#33000000"));
                            EventBus.getDefault().post(mRetryDownLoadHolder);
                        }

                    })
                    .subscribe();
        }
    }

    /**
     * 处理断点续下的位置
     *
     * @param downLoadHolder 下载任务
     */
    private void calculateFileRange(DownLoadHolder downLoadHolder) {
        long downloadFileRange;
        File file = new File(downLoadHolder.getOutputPath());
        long length = file.length();
        if (file.exists() && downLoadHolder.isEncrypted()) {
            downloadFileRange = length - 10L;
        } else {
            Log.e("MyLog", "如果不存在文件 或者 存在文件但不加密 那么 file.length() >>> " + file.length());
            downloadFileRange = length;
        }
        downLoadHolder.setDownloadFileRange(downloadFileRange);
        downLoadHolder.setLocalTempFileLength(length);
    }

    /**
     * 真正开始下载
     *
     * @param downLoadHolder 下载任务
     */
    private void realDownLoad(DownLoadHolder downLoadHolder) {
        mRealDownLoadSubscribe = ApiManager
                .getService(ApiMultiService.class)
                .executeDownload("bytes=" + downLoadHolder.getDownloadFileRange() + "-", downLoadHolder.getDownLoadUrl())
                .subscribeOn(Schedulers.trampoline())
                .map(response -> {
                    if (response.isSuccessful() && response.body() != null) {

                        int pos = downLoadHolder.getPos();
                        VideoUpdateManagerStatus.obtain().setCurrentIndex(++pos);
                        VideoUpdateManagerStatus.obtain().setVideoUpdateStatus(DOWNLOADING);
                        EventBus.getDefault().post(VideoUpdateManagerStatus.obtain());
                        mSpeedByte = 0L;
                        Disposable speedDisposable = Observable
                                .interval(1L, TimeUnit.SECONDS)
                                .map(aLong -> {
                                    long speed = mSpeedByte;
                                    mSpeedByte = 0L;
                                    String s = Formatter.formatFileSize(Utils.getApp().getApplicationContext(), speed);
                                    return s.replaceAll(" ", "") + "/s";
                                })
                                .distinct()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(s -> {
                                    downLoadHolder.setCurrentStatus(s);
                                    downLoadHolder.setProgressingColor(Color.parseColor("#FF14C27B"));
                                    downLoadHolder.setCurrentStatusColor(Color.parseColor("#FF14C27B"));
                                    EventBus.getDefault().post(downLoadHolder);
                                    Log.e("MyLog", "当前的速度为 >>> " + s + " 当前线程为 >>> " + Thread.currentThread().getName());
                                })
                                .subscribe();

                        downloadFile(
                                downLoadHolder.isEncrypted(),
                                downLoadHolder.getLocalTempFileLength(),
                                response.body(),
                                downLoadHolder.getOutputPath(),
                                new DownloadCallback() {
                                    @Override
                                    public void onStart() {
                                        // TODO 当前任务开始下载
                                        if (downLoadHolder.getLocalTempFileLength() == 0) {
                                            Log.e("MyLog", " 开启新的下载 >>> " + downLoadHolder.getTitle());
                                        } else {
                                            Log.e("MyLog", " 开启断点续下 >>> " + downLoadHolder.getTitle());
                                        }
                                    }

                                    @Override
                                    public void onProgress(int p) {
                                        // TODO 当前任务下载百分比
                                        downLoadHolder.setProgress(p);
                                    }

                                    @Override
                                    public void onFinish(String path) {
                                        // TODO 当前任务下载完成
                                        Log.e("MyLog", " ------------ 下载完成 >>> " + path);
                                        downLoadHolder.setShouldDownload(false);
                                        speedDisposable.dispose();
                                        downLoadHolder.setProgress(100);
                                        downLoadHolder.setProgressingColor(Color.parseColor("#FF14C27B"));
                                        downLoadHolder.setCurrentStatus("已完成,并同步至视频列表");
                                        downLoadHolder.setCurrentStatusColor(Color.parseColor("#FF14C27B"));
                                        EventBus.getDefault().post(downLoadHolder);
                                    }

                                    @Override
                                    public void onError(String msg) {
                                        // TODO 当前任务下载错误
                                        Log.e("MyLog", " ------------ 下载错误 >>> " + msg);
                                        downLoadHolder.setShouldDownload(true);
                                        speedDisposable.dispose();
                                        downLoadHolder.setCurrentStatus("0B/s");
                                        EventBus.getDefault().post(downLoadHolder);
                                    }
                                });
                    } else {
                        // TODO 当前任务下载错误
                        Log.e("MyLog", "server contact failed");
                        downLoadHolder.setShouldDownload(true);
                    }
                    return downLoadHolder;

                })
                .onErrorResumeNext(new HttpErrorHandler<>())
                .subscribe();
    }

    /**
     * 写入硬盘
     *
     * @param isEncrypted      是否加密下载
     * @param fileLength       当前文件长度
     * @param responseBody     responseBody
     * @param localFilePath    当前文件路径
     * @param downloadCallback 状态回调
     */
    private void downloadFile(boolean isEncrypted, long fileLength, ResponseBody responseBody, String localFilePath, final DownloadCallback downloadCallback) {
        downloadCallback.onStart();
        RandomAccessFile randomAccessFile = null;
        InputStream inputStream = null;

        long total = 0;

        try {
            inputStream = responseBody.byteStream();
            randomAccessFile = new RandomAccessFile(localFilePath, "rwd");
            randomAccessFile.seek(fileLength);

            byte[] buf = new byte[2048];
            int progress = 0;
            int lastProgress;
            int len;

            if (fileLength == 0 && isEncrypted) {
                randomAccessFile.write("chinafocus".getBytes(), 0, "chinafocus".length());
                total += "chinafocus".length();
            } else {
                total = fileLength;
            }

            long totalLength = responseBody.contentLength() + total;

            while ((len = inputStream.read(buf)) != -1) {
                if (!isDownLoadRunning) {
                    throw new IllegalArgumentException("---------------手动退出下载引擎---------------");
                }

                randomAccessFile.write(buf, 0, len);
                total += len;
                mSpeedByte += len;
                lastProgress = progress;
                progress = (int) (total * 100 / totalLength);
                if (progress > 0 && progress != lastProgress) {
                    downloadCallback.onProgress(progress);
//                    Log.e("MyLog", " isEncrypted >>> " + isEncrypted + " responseBody.contentLength() >>> " + responseBody.contentLength() + " 当前range是 >>> " + fileLength + " 当前进度是 >>> " + progress + " 下载多少字节 >>> " + total + " 文件总大小 >>> " + totalLength);
                    Log.e("MyLog", " isEncrypted >>> " + isEncrypted + " 当前进度是 >>> " + progress);
                }
            }
            downloadCallback.onFinish(localFilePath);
        } catch (Exception e) {
            downloadCallback.onError(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }

                if (inputStream != null) {
                    inputStream.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void cancelDownLoadEngine() {
        isDownLoadRunning = false;
        if (mRealDownLoadSubscribe != null) {
            mRealDownLoadSubscribe.dispose();
            mRealDownLoadSubscribe = null;
        }
        if (mDownLoadEngineSubscribe != null) {
            mDownLoadEngineSubscribe.dispose();
            mDownLoadEngineSubscribe = null;
        }
        FileUtils.deleteAllInDir(ConfigManager.getInstance().getPreVideoTempFilePath());
        FileUtils.deleteAllInDir(ConfigManager.getInstance().getRealVideoTempFilePath());
        VideoUpdateManagerStatus.obtain().setCurrentIndex(1);
    }
}
