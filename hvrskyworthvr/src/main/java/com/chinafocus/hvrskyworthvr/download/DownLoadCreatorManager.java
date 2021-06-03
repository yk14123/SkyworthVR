package com.chinafocus.hvrskyworthvr.download;

import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.chinafocus.hvrskyworthvr.model.bean.VideoContentList;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDetail;
import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.hvrskyworthvr.net.RequestBodyManager;
import com.chinafocus.hvrskyworthvr.service.event.download.VideoUpdateLatest;
import com.chinafocus.hvrskyworthvr.service.event.download.VideoUpdateListError;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.beans.BaseResponse;
import com.chinafocus.lib_network.net.errorhandler.HttpErrorHandler;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
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

    /**
     * 拉取网络数据并对比本地文件，生成下载任务
     */
    public void checkedVideoUpdateTask() {
        isDownLoadChecking = true;
        ApiManager
                .getService(ApiMultiService.class)
                .getEduVideoContentList(RequestBodyManager.getVideoListRequestBody(1))
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(new HttpErrorHandler<>())
                .flatMap(listBaseResponse -> Observable.fromIterable(listBaseResponse.getData()))
                .doOnNext(this::createDownLoadHolder)
                .flatMap(this::createVideoDetailObservable)
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
//                        DownLoadRunningManager instance = DownLoadRunningManager.getInstance();
//                        instance.setDownLoadTaskTotal(mDownLoadHolders);
//                        instance.startDownloadEngine();

                        List<DownLoadHolder> loadHolders = new ArrayList<>();
                        for (DownLoadHolder temp : mDownLoadHolders) {
                            loadHolders.add(temp.clone());
                        }
                        EventBus.getDefault().post(loadHolders);
                    } else {
                        // TODO 列表已经是最新的
                        Log.e("MyLog", " 列表已经是最新的 ");
                        EventBus.getDefault().post(VideoUpdateLatest.obtain());
                    }
                    mDownLoadHolders.clear();
                    isDownLoadChecking = false;
                })
                .doOnError(throwable -> {
                    // TODO 最开始拉取列表对比就失败了，如果拉取横向大列表成功，但是中途再拉取详情出错不走这个异常
                    Log.e("MyLog", " 拉取横向大列表失败 throwable >>> " + throwable.getMessage());
                    mDownLoadHolders.clear();
                    isDownLoadChecking = false;

                    EventBus.getDefault().post(VideoUpdateListError.obtain());
                })
                .subscribe();
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
                                .setContext(mContext)
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

}
