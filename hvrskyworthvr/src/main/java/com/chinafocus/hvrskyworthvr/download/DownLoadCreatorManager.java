package com.chinafocus.hvrskyworthvr.download;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.chinafocus.hvrskyworthvr.global.ConfigManager;
import com.chinafocus.hvrskyworthvr.model.bean.VideoContentList;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDetail;
import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.hvrskyworthvr.net.RequestBodyManager;
import com.chinafocus.hvrskyworthvr.service.event.download.VideoUpdateLatest;
import com.chinafocus.hvrskyworthvr.service.event.download.VideoUpdateListError;
import com.chinafocus.hvrskyworthvr.service.event.download.VideoUpdateNotification;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.beans.BaseResponse;
import com.chinafocus.lib_network.net.errorhandler.HttpErrorHandler;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DownLoadCreatorManager {

    // 是否正在检查中
    private boolean isDownLoadChecking;

    // 是否加密
    private boolean isEncrypted = true;

    // 整体流程控制
    private Disposable mSubscribe;

    private static final DownLoadCreatorManager INSTANCE = new DownLoadCreatorManager();

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

    private final List<DownLoadHolder> mDownLoadHolders = new CopyOnWriteArrayList<>();

    private final List<String> mDeletedName = new ArrayList<>();

    /**
     * 拉取网络数据并对比本地文件，生成下载任务
     */
    public void checkedTaskAndDownLoad() {
        isDownLoadChecking = true;
        // 防止清理不干净，这里需要再次清理集合
        mDownLoadHolders.clear();
        mDeletedName.clear();
        doWork();
    }

    @SuppressLint("NewApi")
    private void doWork() {
        mSubscribe = createVideoContentListObservable()
                .doOnNext(listBaseResponse -> {
                    listBaseResponse.getData().forEach(this::addDeletedName);
                    checkPreVideoFileDeleted();
                })
                .flatMap(listBaseResponse -> Observable.fromIterable(listBaseResponse.getData()))
                .doOnNext(this::createDownLoadHolder)
                .flatMap(this::createVideoDetailObservable)
                .map(BaseResponse::getData)
                .doOnNext(this::createDownLoadHolder)
                .doOnNext(this::addDeletedName)
                .doOnComplete(this::checkRealVideoFileDeleted)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    if (mDownLoadHolders.size() > 0) {
                        for (DownLoadHolder downLoadHolder : mDownLoadHolders) {
                            Log.e("MyLog", " DownLoadHolder 名称是 >>> " + downLoadHolder.getTitle());
                        }
                        // TODO 有更新任务需要处理
                        EventBus.getDefault().post(VideoUpdateNotification.obtain());
                        DownLoadRunningManager instance = DownLoadRunningManager.getInstance();
                        instance.setDownLoadTaskTotal(mDownLoadHolders);
                        instance.startDownloadEngine();
                    } else {
                        // TODO 列表已经是最新的
                        Log.e("MyLog", " 列表已经是最新的 ");
                        EventBus.getDefault().post(VideoUpdateLatest.obtain());
                    }
                })
                .doOnError(throwable -> {
                    // TODO 最开始拉取列表对比就失败了，如果拉取横向大列表成功，但是中途再拉取详情出错不走这个异常
                    Log.e("MyLog", " 拉取横向大列表失败 throwable >>> " + throwable.getMessage());
                    EventBus.getDefault().post(VideoUpdateListError.obtain());
                })
                .doFinally(this::clearAll)
                .subscribe();
    }

    @SuppressLint("NewApi")
    private <T> void addDeletedName(T t) {
        String name = null;
        if (t instanceof VideoDetail) {
            name = ((VideoDetail) t).getVideoUrl();
        } else if (t instanceof VideoContentList) {
            name = ((VideoContentList) t).getMenuVideoUrl();
        }

        Optional.ofNullable(name)
                .filter(s -> !TextUtils.isEmpty(s))
                .ifPresent(mDeletedName::add);
    }

    private void checkPreVideoFileDeleted() {
        checkAndDeletedFile(ConfigManager.getInstance().getPreVideoFilePath());
    }

    private void checkRealVideoFileDeleted() {
        checkAndDeletedFile(ConfigManager.getInstance().getRealVideoFilePath());
    }

    private void checkAndDeletedFile(String filePath) {
        File file = new File(filePath);
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                String s = f.getName();
                if (s.equals("temp")) {
                    continue;
                }
                boolean isDeleted = true;
                String tempName;
                int i = s.indexOf(".");
                if (i != -1) {
                    tempName = s.substring(0, i);
                } else {
                    tempName = s;
                }
                for (String temp : mDeletedName) {
                    if (temp.contains(tempName)) {
                        isDeleted = false;
                        break;
                    }
                }
                if (isDeleted) {
                    FileUtils.delete(f);
                }
            }
        }
        mDeletedName.clear();
    }

    /**
     * 创建下载任务
     *
     * @param videoData 网络数据
     * @param <T>       预览数据或详情数据
     */
    private <T> void createDownLoadHolder(T videoData) {
        Observable
                .just(videoData)
                .subscribeOn(Schedulers.trampoline())
                .map(videoDetail ->
                        new DownLoadHolderBuilder(videoDetail)
                                .setEncrypted(isEncrypted)
                                .build()
                )
                .doOnNext(downLoadHolder -> {
                    if (downLoadHolder.isShouldDownload()) {
                        // TODO 添加到任务队列
                        mDownLoadHolders.add(downLoadHolder);
                    }
                })
                .subscribe();
    }

    /**
     * 创建VideoDetail网络接口Observable
     *
     * @param videoContentList 主列表中的item
     * @return 网络接口Observable
     */
    private Observable<BaseResponse<VideoDetail>> createVideoDetailObservable(VideoContentList videoContentList) {
        return ApiManager
                .getService(ApiMultiService.class)
                .getVideoDetailData(RequestBodyManager.getVideoDetailDataRequestBody(videoContentList.getType(), videoContentList.getId()))
                .subscribeOn(Schedulers.trampoline())
                .onErrorResumeNext(new HttpErrorHandler<>());
    }

    /**
     * 创建横向大列表VideoContentList网络接口Observable
     *
     * @return 网络接口Observable
     */
    private Observable<BaseResponse<List<VideoContentList>>> createVideoContentListObservable() {
        return ApiManager
                .getService(ApiMultiService.class)
                .getEduVideoContentList(RequestBodyManager.getVideoListRequestBody(1))
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(new HttpErrorHandler<>());
    }

    /**
     * 手动关闭下载引擎按钮
     */
    void cancelDownLoadEngine() {
        if (mSubscribe != null && !mSubscribe.isDisposed()) {
            mSubscribe.dispose();
        }
        mSubscribe = null;
    }

    /**
     * 清空数据和状态
     */
    private void clearAll() {
        isDownLoadChecking = false;
        mDownLoadHolders.clear();
        mDeletedName.clear();
    }

}
