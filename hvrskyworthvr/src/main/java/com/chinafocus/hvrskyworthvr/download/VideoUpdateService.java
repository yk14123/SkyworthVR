package com.chinafocus.hvrskyworthvr.download;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
 * @author
 * @date 2020/8/22
 * description：
 */
public class VideoUpdateService extends Service {

    private final String TAG = VideoUpdateService.class.getSimpleName();
//    private List<DownLoadTask> mDownLoadTasks;

    private boolean isTest = false;

    // 每次拉取列表的间隔
    private final int CLOCK_WAIT_TIME = 2 * 60 * 60 * 1000;
    //    private final int CLOCK_WAIT_TIME = 60 * 1000;
    // 下载过程中发送错误后，需要增量下载的间隔
    private final int DOWN_LOAD_ERROR_WAIT_TIME = 5 * 60 * 1000;
//    private final int DOWN_LOAD_ERROR_WAIT_TIME = 30 * 1000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Handler handler;

    private void showToast(final String txt) {
        if (txt == null) {
            Log.e(TAG, "call method showToast, text is null.");
            return;
        }

        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(VideoUpdateService.this, txt, Toast.LENGTH_SHORT)
                        .show();
            }
        });
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
     * @param intent
     * @param flags
     * @param startId
     * @return
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
    // 是否有下载任务
    private boolean haveVideoUpdateTask;

    private boolean isFirst;
    private int index;

    private List<DownLoadHolder> mDownLoadTaskTotal;

//                                                    synchronized (VideoUpdateService.class) {
//                                                        if (!isFirst) {
//                                                            videoData.setVideoUrl("12312313");
//                                                            index++;
//                                                            if (index == 3)
//                                                                isFirst = true;
//                                                        } else {
//                                                        }
//                                                    }

    public void downloadFile(long range, ResponseBody responseBody, String localFilePath, final DownloadApkListener downloadCallback) {

        RandomAccessFile randomAccessFile = null;
        InputStream inputStream = null;
        long total = range;
        long responseLength = 0;
        long totalLength = 0;
        try {
            byte[] buf = new byte[2048];
            int len = 0;
            responseLength = responseBody.contentLength();
            totalLength = responseLength + total;

            inputStream = responseBody.byteStream();

            randomAccessFile = new RandomAccessFile(localFilePath, "rwd");
//            if (range == 0) {
//                randomAccessFile.setLength(responseLength);
//            }
            randomAccessFile.seek(range);

            int progress = 0;
            int lastProgress = 0;

            while ((len = inputStream.read(buf)) != -1) {
                randomAccessFile.write(buf, 0, len);
                total += len;
                lastProgress = progress;
                progress = (int) (total * 100 / totalLength);
                if (progress > 0 && progress != lastProgress) {
                    downloadCallback.onProgress(progress);
                }
            }
            downloadCallback.onFinish(localFilePath);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
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

    private boolean writeResponseBodyToDisk(ResponseBody body, String localFilePath, final DownloadApkListener downloadListener) {
        if (downloadListener != null)
            downloadListener.onStart();

        try {
            // 改成自己需要的存储位置
            File file = new File(localFilePath);
            Log.e(TAG, "writeResponseBodyToDisk() file=" + file.getPath());
//            if (file.exists()) {
//                file.delete();
//            }
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[10 * 1024 * 1024];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    //计算当前下载百分比，并经由回调传出
                    if (downloadListener != null)
                        downloadListener.onProgress((int) (100 * fileSizeDownloaded / fileSize));
//                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
//                    Log.e("MyLog", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                if (downloadListener != null)
                    downloadListener.onFinish(file.getPath());


//                String md5 = "0326923f6dd80058adbafd0e192e095a";

//                String s = DigestUtils.md5Hex(new FileInputStream(file));
//                Log.e(TAG, "video 的DigestUtils.md5Hex方法直接生成 md5 >>> " + s);

                return true;
            } catch (IOException e) {
                if (downloadListener != null)
                    downloadListener.onError("" + e.getMessage());
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    @SuppressLint("CheckResult")
    private void startDownloadEngine() {
        if (mDownLoadTaskTotal.size() > 0) {
            isDownLoadRunning = true;
            // 开始下载
            Observable
                    .fromIterable(mDownLoadTaskTotal)
                    .subscribeOn(Schedulers.io())
                    .map(new Function<DownLoadHolder, DownLoadHolder>() {
                        @Override
                        public DownLoadHolder apply(DownLoadHolder downLoadHolder) throws Exception {

                            File file = new File(downLoadHolder.getOutputPath());
                            long range = 0;
                            if (file.exists()) {
                                range = file.length();
                            }

                            Log.e("MyLog", " 本地已经下载的文件大小是 >>> " + range);
                            Log.e("MyLog", " 断电续下 header >>> " + "bytes=" + range + "-");

                            long finalRange = range;
                            ApiManager
                                    .getService(ApiMultiService.class)
                                    .executeDownload("bytes=" + range + "-", downLoadHolder.getDownLoadUrl())
                                    .subscribeOn(Schedulers.io())
                                    .onErrorResumeNext(new HttpErrorHandler<>())
                                    .blockingSubscribe(
                                            response -> {
                                                if (response.isSuccessful()) {

                                                    downloadFile(finalRange, response.body(), downLoadHolder.getOutputPath(), new DownloadApkListener() {
                                                        @Override
                                                        public void onStart() {
                                                            if (finalRange == 0) {
                                                                Log.e("MyLog", " 开启新的下载 >>> " + downLoadHolder.getTitle());
                                                            } else {
                                                                Log.e("MyLog", " 开启断点续下 >>> " + downLoadHolder.getTitle());
                                                            }
                                                        }

                                                        @Override
                                                        public void onProgress(int p) {
                                                            Log.e("MyLog", "----------下载中 >>> " + p);
                                                        }

                                                        @Override
                                                        public void onFinish(String path) {
                                                            Log.e("MyLog", " ------------ 下载完成 >>> " + path);
                                                            downLoadHolder.setFinish(true);
                                                        }

                                                        @Override
                                                        public void onError(String msg) {
                                                            Log.e("MyLog", " ------------ 下载错误 >>> " + msg);
                                                            downLoadHolder.setFinish(false);
                                                        }
                                                    });

//                                                    boolean myLog = writeResponseBodyToDisk(response.body(), downLoadHolder.getOutputPath(), new DownloadApkListener() {
//                                                        @Override
//                                                        public void onStart() {
//                                                            Log.e("MyLog", " 开启新的下载 >>> " + downLoadHolder.getTitle());
//                                                        }
//
//                                                        @Override
//                                                        public void onProgress(int p) {
//                                                            Log.e("MyLog", "----------下载中。。。" + p);
//                                                        }
//
//                                                        @Override
//                                                        public void onFinish(String path) {
//                                                            Log.e("MyLog", " ------------ 下载完成 >>> " + path);
//                                                            downLoadHolder.setFinish(true);
//                                                        }
//
//                                                        @Override
//                                                        public void onError(String msg) {
//                                                            Log.e("MyLog", " ------------ 下载错误 >>> " + msg);
//                                                            downLoadHolder.setFinish(false);
//                                                        }
//                                                    });
                                                } else {
                                                    Log.e("MyLog", "server contact failed");
                                                }
                                            },
                                            throwable -> {
                                                Log.e("MyLog", "error >>> " + throwable.getMessage());
                                                downLoadHolder.setFinish(false);
                                            });

                            Log.e("MyLog", "----------必须等待下载完成后，才显示这个");

                            return downLoadHolder;
                        }
                    })
                    .subscribe(
                            downLoadHolder -> {
                                Log.e("MyLog", " ------------ 最终下载完成 >>> " + downLoadHolder.getTitle());
                            },
                            throwable -> {
                                Log.e("MyLog", " ------------ 整体下载错误 >>> " + throwable.getMessage());
                            });


        }
    }

    @SuppressLint("CheckResult")
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
                                    .subscribe(
                                            videoDetailBaseResponse -> {
                                                VideoDetail videoData = videoDetailBaseResponse.getData();
                                                if (videoData != null) {
                                                    Observable
                                                            .just(videoData)
                                                            .map(VideoDetail::getVideoUrl)
                                                            .map(s -> s.substring(s.lastIndexOf("/") + 1))
                                                            .map(s -> s.substring(0, s.indexOf(".")))
                                                            .map(s -> {
                                                                File fileVideoUrl = new File(getExternalFilesDir("Videos"), s + ".mp4");
                                                                File outputPath = new File(getExternalFilesDir("Videos"), "temp");
                                                                if (!outputPath.exists()) {
                                                                    outputPath.mkdir();
                                                                }
                                                                DownLoadHolder downLoadHolder = new DownLoadHolder();
                                                                if (!fileVideoUrl.exists()) {
                                                                    downLoadHolder.setEncrypted(true);
                                                                    downLoadHolder.setDownLoadUrl(ConfigManager.getInstance().getDefaultUrl() + videoData.getVideoUrl());
                                                                    downLoadHolder.setOutputPath(new File(outputPath, s + ".mp4").getAbsolutePath());
                                                                    downLoadHolder.setShouldDownload(true);
                                                                    downLoadHolder.setTitle(videoData.getTitle());
                                                                    return downLoadHolder;
                                                                }
                                                                downLoadHolder.setShouldDownload(false);
                                                                return downLoadHolder;
                                                            })
                                                            .subscribe(
                                                                    downLoadHolder -> {
                                                                        if (downLoadHolder.isShouldDownload()) {
                                                                            list.add(downLoadHolder);
                                                                        }
                                                                        countDownLatch.countDown();
                                                                    },
                                                                    throwable -> countDownLatch.countDown());
                                                } else {
                                                    countDownLatch.countDown();
                                                }
                                            },
                                            throwable -> countDownLatch.countDown());
                        }
                        countDownLatch.await();
                    }
                    return Observable.just(list);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        downLoadHolders -> {
                            if (downLoadHolders.size() > 0) {
//                            String outputPath = downLoadHolders.get(0).getOutputPath();
                                for (DownLoadHolder downLoadHolder : downLoadHolders) {
                                    Log.e("MyLog", " DownLoadHolder 名称是 >>> " + downLoadHolder.getTitle());
                                    Log.e("MyLog", " DownLoadHolder 地址是 >>> " + downLoadHolder.getDownLoadUrl());
                                }
                                // TODO 列表已经是最新的
                                Log.e("MyLog", " 有更新！！！ ");
                                mDownLoadTaskTotal.clear();
                                mDownLoadTaskTotal.addAll(downLoadHolders);
                                haveVideoUpdateTask = true;

                                startDownloadEngine();

                            } else {
                                // TODO 列表已经是最新的
                                Log.e("MyLog", " 列表已经是最新的 ");
                                haveVideoUpdateTask = false;
                            }
                        },
                        throwable -> Log.e("MyLog", " 拉去横向大列表失败 throwable >>> " + throwable.getMessage()));
    }


//    /**
//     * 开启检测视频更新服务
//     */
//    private void startCheckVideoUpdateService() {
//        new Thread(new Runnable() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void run() {
//
//                // 首次开启服务后，延迟多久开始检测下载
//                SystemClock.sleep(CLOCK_WAIT_TIME);
//
//                while (true) {
//
//                    Log.e(TAG, "while true 下载引擎开启");
//
//                    // 1.网络下载
//                    List<VideoInfoBean> mNewestVideoInfo = startVideoDataFromNetwork();
//
//                    // 1.1如果mNewestVideoUrl == null表示网络拉取失败，直接等2个小时后，再下
//                    if (mNewestVideoInfo == null) {
//                        Log.e(TAG, "当前网络错误，2个小时后，再尝试链接网络");
//                        if (isTest)
//                            showToast("拉取列表的时候，网络错误，等待2分钟后再试");
//                        SystemClock.sleep(CLOCK_WAIT_TIME);
//                        continue;
//                    }
//
//                    // 下载字幕
//                    downLoadSubtitleFiles(mNewestVideoInfo);
//
//                    // 2.从本地文件，读取最新的show队列，有buffer，优先读buffer
//                    List<VideoInfoBean> videoInfoLocal = readVideoDataFromLocal();
//                    if (videoInfoLocal == null) {
//                        Log.e(TAG, "暂无本地缓存文件，这是致命错误！");
//                        if (isTest)
//                            showToast("暂无本地缓存文件，这是致命错误！");
//                        SystemClock.sleep(CLOCK_WAIT_TIME);
//                        continue;
//                    }
//
//                    // 3.进行比较，生成buffer队列
//                    List<VideoInfoBean> bufferVideoInfoBeans = combineLocalDataAndNetData(videoInfoLocal, mNewestVideoInfo);
//
//                    if (mDownLoadTasks.size() == 0) {
//                        Log.e(TAG, "无新增或者修改的下载任务！");
//                        if (isTest)
//                            showToast("无新增或者修改的下载任务！");
//                        SystemClock.sleep(CLOCK_WAIT_TIME);
//                        continue;
//                    }
//
//                    Log.e(TAG, "下载任务马上开始，准备下载的是 >>> " + mDownLoadTasks.toString());
//
//                    int count = mDownLoadTasks.size();
//
//                    do {
//
//                        for (DownLoadTask downLoadTask : mDownLoadTasks) {
//                            if (downLoadTask.status == 2) {
//                                continue;
//                            }
//                            if (downLoadTask.ossObject == null) {
//                                downLoadTask.ossObject = DownLoadUtil.videoUrlToOssObject(downLoadTask.videoUrl);
//                            }
//                            if (downLoadTask.ossService == null) {
//                                downLoadTask.ossService = new OssService(getApplicationContext(), downLoadTask.ossObject, downLoadTask.groupId, downLoadTask.category);
//                            }
////                                Log.e(TAG, downLoadTask.groupId + " >>> 开始下载！");
//
//                            if (isTest) {
//                                if (downLoadTask.category == DownLoadTask._4K) {
//                                    showToast(downLoadTask.groupId + " >>> 4K开始下载！");
//                                    Log.e(TAG, downLoadTask.groupId + " >>> 4K开始下载！");
//                                } else if (downLoadTask.category == DownLoadTask._SCREEN) {
//                                    showToast(downLoadTask.groupId + " >>> 屏保开始下载！");
//                                    Log.e(TAG, downLoadTask.groupId + " >>> 屏保开始下载！");
//                                }
//                            }
//
//                            boolean isFinish = downLoadTask.ossService.startDownLoad();
//                            if (isFinish) {
//
//                                if (downLoadTask.category == DownLoadTask._4K) {
////                                        Log.e(TAG, downLoadTask.groupId + " >>> 4K下载完成！");
//                                    if (isTest) {
//                                        showToast(downLoadTask.groupId + " >>> 4K下载完成！");
//                                        Log.e(TAG, downLoadTask.groupId + " >>> 4K下载完成！");
//                                    }
//                                    // 下载完成，需要变名字
//                                    File file = new File(getExternalFilesDir("temp"), downLoadTask.groupId + ".mp4");
//                                    File tempFile = new File(getExternalFilesDir("temp"), downLoadTask.groupId);
//                                    tempFile.renameTo(file);
//                                    // 需要存buffer
//                                    for (VideoInfoBean bufferVideoInfoBean : bufferVideoInfoBeans) {
//                                        if (bufferVideoInfoBean.getGroupId().equals(downLoadTask.groupId)) {
//                                            bufferVideoInfoBean.getVR().setDownLoadFlag(20);
//                                        }
//                                    }
//                                    saveLocalVideoToBuffer(bufferVideoInfoBeans);
//
//                                } else if (downLoadTask.category == DownLoadTask._SCREEN) {
////                                        Log.e(TAG, downLoadTask.groupId + " >>> screen下载完成！");
//                                    if (isTest) {
//                                        showToast(downLoadTask.groupId + " >>> 屏保下载完成！");
//                                        Log.e(TAG, downLoadTask.groupId + " >>> 屏保下载完成！");
//                                    }
//
//                                    // 下载完成，需要变名字
//                                    File file = new File(getExternalFilesDir("screen/temp"), downLoadTask.groupId + ".mp4");
//                                    File tempFile = new File(getExternalFilesDir("screen/temp"), downLoadTask.groupId);
//                                    tempFile.renameTo(file);
//
//                                    // 需要存buffer
//                                    for (VideoInfoBean bufferVideoInfoBean : bufferVideoInfoBeans) {
//                                        if (bufferVideoInfoBean.getGroupId().equals(downLoadTask.groupId)) {
//                                            bufferVideoInfoBean.getScreen().setDownLoadFlag(20);
//                                        }
//                                    }
//                                    saveLocalVideoToBuffer(bufferVideoInfoBeans);
//                                }
//
//                                downLoadTask.status = 2;
//                                count--;
//                            } else {
//                                Log.e(TAG, downLoadTask.groupId + " >>> 还没有下载完呢！");
//                                downLoadTask.status = 1;
//                                // 如果下载途中，出现网络中断！等待5分钟后，再访问
//                                SystemClock.sleep(DOWN_LOAD_ERROR_WAIT_TIME);
//                            }
//                        }
//
//                    } while (count != 0);
//
//                    if (isTest) {
//                        showToast("所有任务下载完成！");
//                    }
//                    // 本轮全部都下载完后，再每隔两个小时重新访问网络，拉取最新访问
//                    Log.e(TAG, "所有任务下载完成！");
//                    //  全部都下载完了，清空下载队列任务
//                    mDownLoadTasks.clear();
//                    SystemClock.sleep(CLOCK_WAIT_TIME);
////                    SystemClock.sleep(10 * 1000);
//                }
//            }
//
//        }).start();
//    }
//
//    /**
//     * 下载字幕
//     *
//     * @param mNewestVideoInfo
//     */
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private void downLoadSubtitleFiles(List<VideoInfoBean> mNewestVideoInfo) {
//        long count = mNewestVideoInfo
//                .stream()
//                .filter(videoInfoBean -> !TextUtils.isEmpty(videoInfoBean.getSubtitleFile()))
//                .map(VideoInfoBean::getSubtitleFile)
//                .filter(netSubtitleString -> {
//                    String md5Hex = DigestUtils.md5Hex(netSubtitleString);
//                    File file = new File(getExternalFilesDir("subtitles"), md5Hex + ".ass");
//                    return !file.exists();
//                })
//                .peek(netSubtitleString -> {
//                    // 下载
//                    String md5Hex = DigestUtils.md5Hex(netSubtitleString);
//                    File file = new File(getExternalFilesDir("subtitles"), md5Hex + ".ass");
//                    DownLoadUtil.downloadFile(netSubtitleString, file.getAbsolutePath());
//                })
//                .count();
//
//        Log.e(TAG, "字幕下载了几个 >>> " + count);
//    }
//
//
//    /**
//     * 合并分析网络list和本地list，1.生成下载队列 2.生成大集合buffer
//     *
//     * @param localData 本地list
//     * @param netData   网络list
//     * @return 合并生成大集合buffer
//     */
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private List<VideoInfoBean> combineLocalDataAndNetData(List<VideoInfoBean> localData, List<VideoInfoBean> netData) {
//        List<VideoInfoBean> diffDeleted = localData
//                .stream()
//                .filter(i -> !netData.contains(i))
//                .peek(videoInfoBean -> {
////                    videoInfoBean.setDeletedFlag(true);
//                    videoInfoBean.getVR().setDownLoadFlag(-100);
//                    videoInfoBean.getScreen().setDownLoadFlag(-100);
//                })
//                .collect(Collectors.toList());
//        // 需要删除的是 执行 -100 的队列  最后需要把diffDeleted addAll到buffer中
//        // 需要更新buffer
////        System.out.println("删除队列是 >>> " + diffDeleted);
//
//        if (mDownLoadTasks == null) {
//            mDownLoadTasks = new ArrayList<>();
//        }
//
//        List<VideoInfoBean> buffer =
//                netData
//                        .stream()
//                        .peek(videoInfoBean -> {
//                            // 是否是新增的部分
//                            boolean isIncrease = true;
//                            for (VideoInfoBean localTemp : localData) {
//                                if (localTemp.getGroupId().equals(videoInfoBean.getGroupId())) {
//                                    // 在本地list里，发现了data
//                                    isIncrease = false;
//
//                                    // screen
//                                    VideoInfoBean.ScreenBean netScreen = videoInfoBean.getScreen();
//                                    VideoInfoBean.ScreenBean localScreen = localTemp.getScreen();
//                                    if (!netScreen.getUpdateTime().equals(localScreen.getUpdateTime())) {
//                                        // 如果网络的updateTime不等于本地的。则需要更新
//                                        netScreen.setDownLoadFlag(-50);
//                                        DownLoadTask downLoadTask = new DownLoadTask();
//                                        downLoadTask.groupId = videoInfoBean.getGroupId();
//                                        downLoadTask.videoUrl = netScreen.getVideoUrl();
//                                        downLoadTask.category = DownLoadTask._SCREEN;
//                                        mDownLoadTasks.add(downLoadTask);
////                                        System.out.println("-50 已经存在的文件升级！生成下载任务loading... >>> " + videoInfoBean.groupId);
//                                    } else {
//                                        if (localScreen.getDownLoadFlag() == 20 || localScreen.getDownLoadFlag() == -100) {
////                                            System.out.println(" -" + localTemp.type + " 之前已经下好的恢复成20 >>> " + videoInfoBean.groupId);
//                                            netScreen.setDownLoadFlag(20);
//                                        }
//                                    }
//
//                                    // 4K
//                                    VideoInfoBean.VRBean netVr = videoInfoBean.getVR();
//                                    VideoInfoBean.VRBean localVr = localTemp.getVR();
//                                    if (!netVr.getUpdateTime().equals(localVr.getUpdateTime())) {
//                                        // 如果网络的updateTime不等于本地的。则需要更新
//                                        netVr.setDownLoadFlag(-50);
//                                        DownLoadTask downLoadTask = new DownLoadTask();
//                                        downLoadTask.groupId = videoInfoBean.getGroupId();
//                                        downLoadTask.videoUrl = netVr.getVideoUrl();
//                                        downLoadTask.category = DownLoadTask._4K;
//                                        mDownLoadTasks.add(downLoadTask);
////                                        System.out.println("-50 已经存在的文件升级！生成下载任务loading... >>> " + videoInfoBean.groupId);
//                                    } else {
//                                        if (localVr.getDownLoadFlag() == 20 || localVr.getDownLoadFlag() == -100) {
////                                            System.out.println(" -" + localTemp.type + " 之前已经下好的恢复成20 >>> " + videoInfoBean.groupId);
//                                            netVr.setDownLoadFlag(20);
//                                        }
//                                    }
//
//                                    break;
//                                }
//                            }
//
//                            if (isIncrease) {
//                                // 2. 如果新增的部分，但是没有下载完，则保留temp目录下 xxx 以便增量下载
//                                // 只需要过滤到最终show队列即可
//
//                                // screen
//                                VideoInfoBean.ScreenBean netScreen = videoInfoBean.getScreen();
//                                netScreen.setDownLoadFlag(-50);
//                                DownLoadTask downLoadTaskScreen = new DownLoadTask();
//                                downLoadTaskScreen.groupId = videoInfoBean.getGroupId();
//                                downLoadTaskScreen.videoUrl = netScreen.getVideoUrl();
//                                downLoadTaskScreen.category = DownLoadTask._SCREEN;
//                                mDownLoadTasks.add(downLoadTaskScreen);
//
//                                // 4K
//                                VideoInfoBean.VRBean netVr = videoInfoBean.getVR();
//                                netVr.setDownLoadFlag(-50);
//                                DownLoadTask downLoadTask = new DownLoadTask();
//                                downLoadTask.groupId = videoInfoBean.getGroupId();
//                                downLoadTask.videoUrl = netVr.getVideoUrl();
//                                downLoadTask.category = DownLoadTask._4K;
//                                mDownLoadTasks.add(downLoadTask);
//
////                                videoInfoBean.type = -50;
////                                System.out.println("-50 新增部分！生成下载任务 >>> " + videoInfoBean.groupId);
//                            }
//                        })
//                        .collect(Collectors.toList());
//
//        buffer.addAll(diffDeleted);
//
//        // 把对比结果存入buffer
//        saveLocalVideoToBuffer(buffer);
//
//        return buffer;
//    }
//
//    /**
//     * 当有数据变化，保存最新数据到buffer中
//     *
//     * @param source
//     */
//    private void saveLocalVideoToBuffer(List<VideoInfoBean> source) {
//        String s = new Gson().toJson(source);
//        SPUtils.getInstance("localVideo").put("localVideoInfoBuffer", s, true);
//    }
//
//    /**
//     * 从本地中拉取最终展示的列表
//     */
//    private List<VideoInfoBean> readVideoDataFromLocal() {
//
//        String buffer = SPUtils.getInstance("localVideo").getString("localVideoInfoBuffer");
//        if (!TextUtils.isEmpty(buffer)) {
//            return new Gson().fromJson(buffer, new TypeToken<List<VideoInfoBean>>() {
//            }.getType());
//        }
//
//        String local = SPUtils.getInstance("localVideo").getString("localVideoInfo");
//        if (!TextUtils.isEmpty(local)) {
//            return new Gson().fromJson(local, new TypeToken<List<VideoInfoBean>>() {
//            }.getType());
//        }
//
//        return null;
//    }
//
//    /**
//     * 从网络拉取最新数据
//     *
//     * @return
//     */
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private List<VideoInfoBean> startVideoDataFromNetwork() {
//
//        final List<VideoInfoBean>[] result = new List[]{new ArrayList<>()};
//
//        ApiManager
//                .getService(ApiService.class)
//                .getVideoDetailList()
//                .subscribeOn(Schedulers.trampoline())
//                .onErrorResumeNext(new HttpErrorHandler<>())
//                .subscribe(new DefaultObserver<BaseResponse<List<VideoInfoBean>>>() {
//                    @Override
//                    public void onNext(BaseResponse<List<VideoInfoBean>> response) {
//                        if (response.getErrCode() == 0) {
//                            result[0] = response.getData()
//                                    .stream()
//                                    // 必须 VR 和 屏保 都存在！才添加播放
//                                    .filter(videoInfoBean -> videoInfoBean.getVR() != null && videoInfoBean.getScreen() != null)
//                                    .collect(Collectors.toList());
//                        } else {
//                            result[0] = null;
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e(TAG, " 网络访问错误代码是 >>> " + ((ExceptionHandle.ResponseThrowable) e).code
//                                + " 错误信息是 >>>" + ((ExceptionHandle.ResponseThrowable) e).message);
//                        result[0] = null;
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//
//        return result[0];
//    }

}
