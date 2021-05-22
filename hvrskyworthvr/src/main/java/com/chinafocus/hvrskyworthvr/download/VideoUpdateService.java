package com.chinafocus.hvrskyworthvr.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import com.chinafocus.hvrskyworthvr.global.ConfigManager;
import com.chinafocus.hvrskyworthvr.model.bean.VideoContentList;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDetail;
import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.hvrskyworthvr.net.RequestBodyManager;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.DownloadApkListener;
import com.chinafocus.lib_network.net.beans.BaseResponse;
import com.chinafocus.lib_network.net.errorhandler.HttpErrorHandler;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * @author yksm
 * @date 2020/8/22
 * description：
 */
public class VideoUpdateService extends Service {

    private final String TAG = VideoUpdateService.class.getSimpleName();

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
        mDownLoadTaskTotal = new ArrayList<>();
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
        Log.e(TAG, "VideoUpdateService >>> onStartCommand");
        startEngine();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startEngine() {
        // 如果在下载中，则不检查
        if (isDownLoadRunning) {
            return;
        }
        // 1.拉取网络对比生成download任务
        checkedVideoUpdateTask();
    }

    // 是否正在下载中
    private boolean isDownLoadRunning;

    private List<DownLoadHolder> mDownLoadTaskTotal;

    public void downloadFile(boolean isEncrypted, long range, ResponseBody responseBody, String localFilePath, final DownloadApkListener downloadCallback) {
        downloadCallback.onStart();
        RandomAccessFile randomAccessFile = null;
        InputStream inputStream = null;

        long total = 0;

        try {
            inputStream = responseBody.byteStream();
            randomAccessFile = new RandomAccessFile(localFilePath, "rwd");
            randomAccessFile.seek(range);

            byte[] buf = new byte[2048];
            int progress = 0;
            int lastProgress;
            int len;

            if (range == 0 && isEncrypted) {
                randomAccessFile.write("chinafocus".getBytes(), 0, "chinafocus".length());
                total += 10L;
            } else {
                total = range;
            }

            long totalLength = responseBody.contentLength() + total;

            while ((len = inputStream.read(buf)) != -1) {
                randomAccessFile.write(buf, 0, len);
                total += len;
                lastProgress = progress;
                progress = (int) (total * 100 / totalLength);
                if (progress > 0 && progress != lastProgress) {
                    downloadCallback.onProgress(progress);
                    Log.e("MyLog", " isEncrypted >>> " + isEncrypted + " responseBody.contentLength() >>> " + responseBody.contentLength() + " 当前range是 >>> " + range + " 当前进度是 >>> " + progress + " 下载多少字节 >>> " + total + " 文件总大小 >>> " + totalLength);
                }
            }
            downloadCallback.onFinish(localFilePath);
        } catch (Exception e) {
//            Log.d(TAG, e.getMessage());
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

    // 断点续下的位置
    private long downloadFileRange;
    // 重试次数
    private final int retryCount = 5;
    // 是否加密
    private boolean isEncrypted = true;
    // 下载任务总数量
    private int taskTotalSize;

    private void startDownloadEngine() {
        if (mDownLoadTaskTotal.size() > 0) {
            taskTotalSize = mDownLoadTaskTotal.size();
            isDownLoadRunning = true;
            // 开始下载
            Observable
                    .fromIterable(mDownLoadTaskTotal)
                    .subscribeOn(Schedulers.io())
                    .map(downLoadHolder -> {
                        for (int i = 0; i < retryCount; i++) {
                            File file = new File(downLoadHolder.getOutputPath());
                            if (file.exists() && isEncrypted) {
                                downloadFileRange = file.length() - 10L;
                            } else {
                                Log.e("MyLog", "如果不存在文件 或者 存在文件但不加密 那么 file.length() >>> " + file.length());
                                downloadFileRange = file.length();
                            }
                            ApiManager
                                    .getService(ApiMultiService.class)
                                    .executeDownload("bytes=" + downloadFileRange + "-", downLoadHolder.getDownLoadUrl())
                                    .subscribeOn(Schedulers.trampoline())
                                    .map(response -> {
                                        if (response.isSuccessful() && response.body() != null) {
                                            downloadFile(isEncrypted, file.length(), response.body(), downLoadHolder.getOutputPath(), new DownloadApkListener() {
                                                @Override
                                                public void onStart() {
                                                    // TODO 当前任务开始下载
                                                    if (downloadFileRange == 0) {
                                                        Log.e("MyLog", " 开启新的下载 >>> " + downLoadHolder.getTitle());
                                                    } else {
                                                        Log.e("MyLog", " 开启断点续下 >>> " + downLoadHolder.getTitle());
                                                    }
                                                }

                                                @Override
                                                public void onProgress(int p) {
                                                    // TODO 当前任务下载百分比
//                                                    Log.e("MyLog", "----------下载中 >>> " + p);
                                                }

                                                @Override
                                                public void onFinish(String path) {
                                                    // TODO 当前任务下载完成
                                                    Log.e("MyLog", " ------------ 下载完成 >>> " + path);
                                                    downLoadHolder.setFinish(true);
                                                }

                                                @Override
                                                public void onError(String msg) {
                                                    // TODO 当前任务下载错误
                                                    Log.e("MyLog", " ------------ 下载错误 >>> " + msg);
                                                    downLoadHolder.setFinish(false);
                                                }
                                            });
                                        } else {
                                            // TODO 当前任务下载错误
                                            Log.e("MyLog", "server contact failed");
                                            downLoadHolder.setFinish(false);
                                        }
                                        return downLoadHolder;

                                    })
                                    .onErrorResumeNext(new HttpErrorHandler<>())
                                    .subscribe();

                            if (downLoadHolder.isFinish()) {
                                // 下载完成就不重试
                                break;
                            } else {
                                SystemClock.sleep(5000);
                                int current = i + 1;
                                Log.e("MyLog", "----------重试retry >>> " + current);
                            }
                        }

                        if (!downLoadHolder.isFinish()) {
                            throw new IllegalArgumentException("----------所有重试机会用完，不在下载其他的");
                        }

                        return downLoadHolder;
                    })
                    .map(downLoadHolder -> {
                        // TODO 下载完其中一个
                        Log.e("MyLog", " ------------ 下载完其中一个 >>> " + downLoadHolder.getTitle());
                        return --taskTotalSize;
                    })
                    .doOnNext(totalSize -> {
                        if (totalSize <= 0) {
                            // TODO 全部任务完成
                            Log.e("MyLog", " ------------ 所有任务全部下载完成 >>> ");
                        }
                    })
                    .doOnError(throwable -> {
                        // TODO 下载失败
                        Log.e("MyLog", " ------------ 整体下载结束  抛出错误 >>> " + throwable.getMessage());
                        isDownLoadRunning = false;
                    })
                    .subscribe();
        }
    }

    private void checkedVideoUpdateTask() {
        ApiManager
                .getService(ApiMultiService.class)
                .getEduVideoContentList(RequestBodyManager.getVideoListRequestBody(1))
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(new HttpErrorHandler<>())
                .flatMap((Function<BaseResponse<List<VideoContentList>>, ObservableSource<List<DownLoadHolder>>>) listBaseResponse -> {
                    List<VideoContentList> data = listBaseResponse.getData();
                    List<DownLoadHolder> list = new CopyOnWriteArrayList<>();
                    if (data != null && data.size() > 0) {
                        CountDownLatch countDownLatch = new CountDownLatch(data.size());
                        Log.e("MyLog", " total >>> " + data);
                        for (VideoContentList temp : data) {
                            ApiManager
                                    .getService(ApiMultiService.class)
                                    .getVideoDetailData(RequestBodyManager.getVideoDetailDataRequestBody(temp.getType(), temp.getId()))
                                    .subscribeOn(Schedulers.io())
                                    .onErrorResumeNext(new HttpErrorHandler<>())
                                    .doOnError(throwable -> countDownLatch.countDown())
                                    .doOnNext(
                                            videoDetailBaseResponse -> {
                                                VideoDetail videoData = videoDetailBaseResponse.getData();
                                                if (videoData != null) {
                                                    Observable
                                                            .just(videoData)
                                                            .map(VideoDetail::getVideoUrl)
                                                            .map(s -> s.substring(s.lastIndexOf("/") + 1))
                                                            .map(s -> s.substring(0, s.indexOf(".")))
                                                            .map(s -> {
                                                                File mp4FileSuffix = new File(getExternalFilesDir("Videos"), s + ".mp4");
                                                                File chinafocusFileSuffix = new File(getExternalFilesDir("Videos"), s + ".chinafocus");
                                                                File outputPath = getExternalFilesDir("Videos/temp");
                                                                DownLoadHolder downLoadHolder = new DownLoadHolder();
                                                                if (!(mp4FileSuffix.exists() || chinafocusFileSuffix.exists())) {
                                                                    downLoadHolder.setEncrypted(true);
                                                                    downLoadHolder.setDownLoadUrl(ConfigManager.getInstance().getDefaultUrl() + videoData.getVideoUrl());
                                                                    if (isEncrypted) {
                                                                        downLoadHolder.setOutputPath(new File(outputPath, s + ".chinafocus").getAbsolutePath());
                                                                    } else {
                                                                        downLoadHolder.setOutputPath(new File(outputPath, s + ".mp4").getAbsolutePath());
                                                                    }
                                                                    downLoadHolder.setShouldDownload(true);
                                                                    downLoadHolder.setTitle(videoData.getTitle());
                                                                    return downLoadHolder;
                                                                }
                                                                downLoadHolder.setShouldDownload(false);
                                                                return downLoadHolder;
                                                            })
                                                            .doOnNext(downLoadHolder -> {
                                                                if (downLoadHolder.isShouldDownload()) {
                                                                    list.add(downLoadHolder);
                                                                }
                                                                countDownLatch.countDown();
                                                            })
                                                            .doOnError(throwable -> countDownLatch.countDown())
                                                            .subscribe();
                                                } else {
                                                    countDownLatch.countDown();
                                                }
                                            })
                                    .subscribe();
                        }
                        countDownLatch.await();
                    }
                    return Observable.just(list);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(downLoadHolders -> {
                    if (downLoadHolders.size() > 0) {
                        for (DownLoadHolder downLoadHolder : downLoadHolders) {
                            Log.e("MyLog", " DownLoadHolder 名称是 >>> " + downLoadHolder.getTitle());
                            Log.e("MyLog", " DownLoadHolder 地址是 >>> " + downLoadHolder.getDownLoadUrl());
                        }
                        // TODO 有更新任务需要处理
                        Log.e("MyLog", " 有更新！！！ ");
                        mDownLoadTaskTotal.clear();
                        mDownLoadTaskTotal.addAll(downLoadHolders);
                        startDownloadEngine();
                    } else {
                        // TODO 列表已经是最新的
                        Log.e("MyLog", " 列表已经是最新的 ");
                    }
                })
                .doOnError(throwable -> {
                    // TODO 最开始拉取列表对比就失败了，如果拉取横向大列表成功，但是中途再拉取详情出错不走这个异常
                    Log.e("MyLog", " 拉取横向大列表失败 throwable >>> " + throwable.getMessage());
                })
                .subscribe();
    }
}
