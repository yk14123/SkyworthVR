package com.chinafocus.hvrskyworthvr.download;

import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.chinafocus.hvrskyworthvr.global.ConfigManager;
import com.chinafocus.hvrskyworthvr.model.bean.VideoContentList;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDetail;
import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.hvrskyworthvr.net.RequestBodyManager;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.beans.BaseResponse;
import com.chinafocus.lib_network.net.errorhandler.HttpErrorHandler;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class DownLoadCreatorManager {

    // 是否正在检查中
    private boolean isDownLoadChecking;

    // 是否加密
    private boolean isEncrypted = true;
    private Context mContext;

    private DownLoadCreatorManager() {
        this.mContext = Utils.getApp().getApplicationContext();
    }

    private static DownLoadCreatorManager INSTANCE = new DownLoadCreatorManager();

    public static DownLoadCreatorManager getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unused")
    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public boolean isDownLoadChecking() {
        return isDownLoadChecking;
    }

    private List<DownLoadHolder> mDownLoadHolders = new CopyOnWriteArrayList<>();

    public void checkedVideoUpdateTask() {
        isDownLoadChecking = true;
        ApiManager
                .getService(ApiMultiService.class)
                .getEduVideoContentList(RequestBodyManager.getVideoListRequestBody(1))
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(new HttpErrorHandler<>())
                .flatMap((Function<BaseResponse<List<VideoContentList>>, Observable<VideoContentList>>) listBaseResponse -> Observable.fromIterable(listBaseResponse.getData()))
                .map(this::createPreviewDownLoadHolder)
                .flatMap((Function<VideoContentList, Observable<BaseResponse<VideoDetail>>>) videoContentList ->
                        ApiManager
                                .getService(ApiMultiService.class)
                                .getVideoDetailData(RequestBodyManager.getVideoDetailDataRequestBody(videoContentList.getType(), videoContentList.getId()))
                                .subscribeOn(Schedulers.trampoline())
                                .onErrorResumeNext(new HttpErrorHandler<>())
                )
                .map(BaseResponse::getData)
                .doOnNext(this::createDownLoadHolder)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    if (mDownLoadHolders.size() > 0) {
                        for (DownLoadHolder downLoadHolder : mDownLoadHolders) {
                            Log.e("MyLog", " DownLoadHolder 名称是 >>> " + downLoadHolder.getTitle());
                            Log.e("MyLog", " DownLoadHolder 地址是 >>> " + downLoadHolder.getDownLoadUrl());
                        }
                        // TODO 有更新任务需要处理
                        Log.e("MyLog", " 有更新！！！ ");
                        DownLoadRunningManager instance = DownLoadRunningManager.getInstance();
                        instance.setDownLoadTaskTotal(mDownLoadHolders);
                        instance.startDownloadEngine();
                    } else {
                        // TODO 列表已经是最新的
                        Log.e("MyLog", " 列表已经是最新的 ");
                    }
                    mDownLoadHolders.clear();
                    isDownLoadChecking = false;
                })
                .doOnError(throwable -> {
                    // TODO 最开始拉取列表对比就失败了，如果拉取横向大列表成功，但是中途再拉取详情出错不走这个异常
                    Log.e("MyLog", " 拉取横向大列表失败 throwable >>> " + throwable.getMessage());
                    mDownLoadHolders.clear();
                    isDownLoadChecking = false;
                })
                .subscribe();
    }

    private void createDownLoadHolder(VideoDetail videoData) {
        Observable
                .just(videoData)
                .subscribeOn(Schedulers.trampoline())
                .map(VideoDetail::getVideoUrl)
                .map(s -> s.substring(s.lastIndexOf("/") + 1))
                .map(s -> s.substring(0, s.indexOf(".")))
                .map(s -> {
                    File mp4FileSuffix = new File(mContext.getExternalFilesDir("Videos"), s + ".mp4");
                    File chinafocusFileSuffix = new File(mContext.getExternalFilesDir("Videos"), s + ".chinafocus");
                    File outputPath = mContext.getExternalFilesDir("Videos/temp");
                    DownLoadHolder downLoadHolder = new DownLoadHolder();
                    if (!(mp4FileSuffix.exists() || chinafocusFileSuffix.exists())) {
                        downLoadHolder.setDownLoadUrl(ConfigManager.getInstance().getDefaultUrl() + videoData.getVideoUrl());
                        if (isEncrypted) {
                            downLoadHolder.setEncrypted(true);
                            downLoadHolder.setOutputPath(new File(outputPath, s + ".chinafocus").getAbsolutePath());
                            downLoadHolder.setFinalPath(new File(mContext.getExternalFilesDir("Videos"), s + ".chinafocus").getAbsolutePath());
                        } else {
                            downLoadHolder.setEncrypted(false);
                            downLoadHolder.setOutputPath(new File(outputPath, s + ".mp4").getAbsolutePath());
                            downLoadHolder.setFinalPath(new File(mContext.getExternalFilesDir("Videos"), s + ".mp4").getAbsolutePath());
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
                        // TODO 添加到任务队列
                        mDownLoadHolders.add(downLoadHolder);
                    }
                })
                .subscribe();
    }

    private VideoContentList createPreviewDownLoadHolder(VideoContentList videoContentList) {
        Observable
                .just(videoContentList)
                .subscribeOn(Schedulers.trampoline())
                .map(VideoContentList::getMenuVideoUrl)
                .map(s -> s.substring(s.lastIndexOf("/") + 1))
                .map(s -> s.substring(0, s.indexOf(".")))
                .map(s -> {
                    File previewMp4FileSuffix = new File(mContext.getExternalFilesDir("preview"), s + ".mp4");
                    File previewChinafocusFileSuffix = new File(mContext.getExternalFilesDir("preview"), s + ".chinafocus");
                    File outputPath = mContext.getExternalFilesDir("preview/temp");
                    DownLoadHolder downLoadHolder = new DownLoadHolder();
                    if (!(previewMp4FileSuffix.exists() || previewChinafocusFileSuffix.exists())) {
                        downLoadHolder.setDownLoadUrl(ConfigManager.getInstance().getDefaultUrl() + videoContentList.getMenuVideoUrl());
                        if (isEncrypted) {
                            downLoadHolder.setEncrypted(true);
                            downLoadHolder.setOutputPath(new File(outputPath, s + ".chinafocus").getAbsolutePath());
                            downLoadHolder.setFinalPath(new File(mContext.getExternalFilesDir("preview"), s + ".chinafocus").getAbsolutePath());
                        } else {
                            downLoadHolder.setEncrypted(false);
                            downLoadHolder.setOutputPath(new File(outputPath, s + ".mp4").getAbsolutePath());
                            downLoadHolder.setFinalPath(new File(mContext.getExternalFilesDir("preview"), s + ".mp4").getAbsolutePath());
                        }
                        downLoadHolder.setTitle(videoContentList.getTitle());
                        downLoadHolder.setShouldDownload(true);
                    } else {
                        downLoadHolder.setShouldDownload(false);
                    }
                    return downLoadHolder;
                })
                .doOnNext(downLoadHolder -> {
                    if (downLoadHolder.isShouldDownload()) {
                        // TODO 添加到任务队列
                        mDownLoadHolders.add(downLoadHolder);
                    }
                })
                .subscribe();
        return videoContentList;
    }

//    private void checkedVideoUpdateTask() {
//        isDownLoadChecking = true;
//        ApiManager
//                .getService(ApiMultiService.class)
//                .getEduVideoContentList(RequestBodyManager.getVideoListRequestBody(1))
//                .subscribeOn(Schedulers.io())
//                .onErrorResumeNext(new HttpErrorHandler<>())
//                .flatMap((Function<BaseResponse<List<VideoContentList>>, ObservableSource<List<DownLoadHolder>>>) listBaseResponse -> {
//                    List<VideoContentList> data = listBaseResponse.getData();
//                    List<DownLoadHolder> list = new CopyOnWriteArrayList<>();
//                    if (data != null && data.size() > 0) {
//                        CountDownLatch countDownLatch = new CountDownLatch(data.size());
//                        Log.e("MyLog", " total >>> " + data);
//                        for (VideoContentList temp : data) {
//                            ApiManager
//                                    .getService(ApiMultiService.class)
//                                    .getVideoDetailData(RequestBodyManager.getVideoDetailDataRequestBody(temp.getType(), temp.getId()))
//                                    .subscribeOn(Schedulers.io())
//                                    .onErrorResumeNext(new HttpErrorHandler<>())
//                                    .doOnError(throwable -> countDownLatch.countDown())
//                                    .doOnNext(
//                                            videoDetailBaseResponse -> {
//                                                VideoDetail videoData = videoDetailBaseResponse.getData();
//                                                if (videoData != null) {
//                                                    Observable
//                                                            .just(videoData)
//                                                            .map(VideoDetail::getVideoUrl)
//                                                            .map(s -> s.substring(s.lastIndexOf("/") + 1))
//                                                            .map(s -> s.substring(0, s.indexOf(".")))
//                                                            .map(s -> {
//                                                                File mp4FileSuffix = new File(getExternalFilesDir("Videos"), s + ".mp4");
//                                                                File chinafocusFileSuffix = new File(getExternalFilesDir("Videos"), s + ".chinafocus");
//                                                                File outputPath = getExternalFilesDir("Videos/temp");
//                                                                DownLoadHolder downLoadHolder = new DownLoadHolder();
//                                                                if (!(mp4FileSuffix.exists() || chinafocusFileSuffix.exists())) {
//                                                                    downLoadHolder.setDownLoadUrl(ConfigManager.getInstance().getDefaultUrl() + videoData.getVideoUrl());
//                                                                    if (isEncrypted) {
//                                                                        downLoadHolder.setEncrypted(true);
//                                                                        downLoadHolder.setOutputPath(new File(outputPath, s + ".chinafocus").getAbsolutePath());
//                                                                        downLoadHolder.setFinalPath(new File(getExternalFilesDir("Videos"), s + ".chinafocus").getAbsolutePath());
//                                                                    } else {
//                                                                        downLoadHolder.setEncrypted(false);
//                                                                        downLoadHolder.setOutputPath(new File(outputPath, s + ".mp4").getAbsolutePath());
//                                                                        downLoadHolder.setFinalPath(new File(getExternalFilesDir("Videos"), s + ".mp4").getAbsolutePath());
//                                                                    }
//                                                                    downLoadHolder.setShouldDownload(true);
//                                                                    downLoadHolder.setTitle(videoData.getTitle());
//                                                                    return downLoadHolder;
//                                                                }
//                                                                downLoadHolder.setShouldDownload(false);
//                                                                return downLoadHolder;
//                                                            })
//                                                            .doOnNext(downLoadHolder -> {
//                                                                if (downLoadHolder.isShouldDownload()) {
//                                                                    list.add(downLoadHolder);
//                                                                }
//                                                                countDownLatch.countDown();
//                                                            })
//                                                            .doOnError(throwable -> countDownLatch.countDown())
//                                                            .subscribe();
//                                                } else {
//                                                    countDownLatch.countDown();
//                                                }
//                                            })
//                                    .subscribe();
//                        }
//                        countDownLatch.await();
//                    }
//                    return Observable.just(list);
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnNext(downLoadHolders -> {
//                    if (downLoadHolders.size() > 0) {
//                        for (DownLoadHolder downLoadHolder : downLoadHolders) {
//                            Log.e("MyLog", " DownLoadHolder 名称是 >>> " + downLoadHolder.getTitle());
//                            Log.e("MyLog", " DownLoadHolder 地址是 >>> " + downLoadHolder.getDownLoadUrl());
//                        }
//                        // TODO 有更新任务需要处理
//                        Log.e("MyLog", " 有更新！！！ ");
//                        mDownLoadTaskTotal.clear();
//                        mDownLoadTaskTotal.addAll(downLoadHolders);
//                        startDownloadEngine();
//                    } else {
//                        // TODO 列表已经是最新的
//                        Log.e("MyLog", " 列表已经是最新的 ");
//                    }
//                    isDownLoadChecking = false;
//                })
//                .doOnError(throwable -> {
//                    // TODO 最开始拉取列表对比就失败了，如果拉取横向大列表成功，但是中途再拉取详情出错不走这个异常
//                    Log.e("MyLog", " 拉取横向大列表失败 throwable >>> " + throwable.getMessage());
//                    isDownLoadChecking = false;
//                })
//                .subscribe();
//    }
}
