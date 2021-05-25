package com.chinafocus.hvrskyworthvr.download;

import android.os.SystemClock;
import android.util.Log;

import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.DownloadApkListener;
import com.chinafocus.lib_network.net.errorhandler.HttpErrorHandler;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class DownLoadRunningManager {

    // 是否正在下载中
    private boolean isDownLoadRunning;

    private List<DownLoadHolder> mDownLoadTaskTotal;

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
    // 下载任务总数量
    private int taskTotalSize;

    /**
     * 设置下载任务
     *
     * @param downLoadTaskTotal 下载任务
     */
    public void setDownLoadTaskTotal(List<DownLoadHolder> downLoadTaskTotal) {
        mDownLoadTaskTotal.clear();
        for (DownLoadHolder temp : downLoadTaskTotal) {
            try {
                mDownLoadTaskTotal.add(temp.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 下载引擎
     */
    public void startDownloadEngine() {
        if (mDownLoadTaskTotal.size() > 0) {
            taskTotalSize = mDownLoadTaskTotal.size();
            isDownLoadRunning = true;
            // 开始下载
            Observable
                    .fromIterable(mDownLoadTaskTotal)
                    .filter(DownLoadHolder::isShouldDownload)
                    .subscribeOn(Schedulers.io())
                    // 配合retry断点续下使用
                    .doOnNext(this::calculateFileRange)
                    .doOnNext(this::realDownLoad)
                    .doOnNext(downLoadHolder -> {
                        if (downLoadHolder.isShouldDownload()) {
                            throw new IllegalArgumentException("----------只要没有下载完成，就抛出异常----------");
                        }
                    })
                    .retry(retryCount, throwable -> {
                        Log.e("MyLog", "---------- 等待5秒后 重试retry >>> ");
                        SystemClock.sleep(5000);
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
                            Log.e("MyLog", " ------------ 从temp目录移动到 video目录 失败 >>> " + downLoadHolder.getTitle());
                        }
                        --taskTotalSize;
                    })
                    .doOnComplete(() -> {
                        if (taskTotalSize <= 0) {
                            // TODO 全部任务完成
                            Log.e("MyLog", " ------------ 所有任务全部下载完成 >>> ");
                            isDownLoadRunning = false;
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
        downLoadHolder.setFileLength(length);
    }

    /**
     * 真正开始下载
     *
     * @param downLoadHolder 下载任务
     */
    private void realDownLoad(DownLoadHolder downLoadHolder) {
        ApiManager
                .getService(ApiMultiService.class)
                .executeDownload("bytes=" + downLoadHolder.getDownloadFileRange() + "-", downLoadHolder.getDownLoadUrl())
                .subscribeOn(Schedulers.trampoline())
                .map(response -> {
                    if (response.isSuccessful() && response.body() != null) {
                        downloadFile(
                                downLoadHolder.isEncrypted(),
                                downLoadHolder.getFileLength(),
                                response.body(),
                                downLoadHolder.getOutputPath(),
                                new DownloadApkListener() {
                                    @Override
                                    public void onStart() {
                                        // TODO 当前任务开始下载
                                        if (downLoadHolder.getFileLength() == 0) {
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
                                        downLoadHolder.setShouldDownload(false);
                                    }

                                    @Override
                                    public void onError(String msg) {
                                        // TODO 当前任务下载错误
                                        Log.e("MyLog", " ------------ 下载错误 >>> " + msg);
                                        downLoadHolder.setShouldDownload(true);
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
    private void downloadFile(boolean isEncrypted, long fileLength, ResponseBody responseBody, String localFilePath, final DownloadApkListener downloadCallback) {
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
                randomAccessFile.write(buf, 0, len);
                total += len;
                lastProgress = progress;
                progress = (int) (total * 100 / totalLength);
                if (progress > 0 && progress != lastProgress) {
                    downloadCallback.onProgress(progress);
                    Log.e("MyLog", " isEncrypted >>> " + isEncrypted + " responseBody.contentLength() >>> " + responseBody.contentLength() + " 当前range是 >>> " + fileLength + " 当前进度是 >>> " + progress + " 下载多少字节 >>> " + total + " 文件总大小 >>> " + totalLength);
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
}
