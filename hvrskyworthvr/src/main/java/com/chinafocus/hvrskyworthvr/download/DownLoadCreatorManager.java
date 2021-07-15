package com.chinafocus.hvrskyworthvr.download;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.arialyy.aria.core.Aria;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.chinafocus.hvrskyworthvr.GlideApp;
import com.chinafocus.hvrskyworthvr.global.ConfigManager;
import com.chinafocus.hvrskyworthvr.model.bean.VideoContentList;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDetail;
import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.hvrskyworthvr.net.ImageProcess;
import com.chinafocus.hvrskyworthvr.net.RequestBodyManager;
import com.chinafocus.hvrskyworthvr.service.event.NotifyVideoContentList;
import com.chinafocus.hvrskyworthvr.service.event.download.VideoUpdateLatest;
import com.chinafocus.hvrskyworthvr.service.event.download.VideoUpdateListError;
import com.chinafocus.hvrskyworthvr.service.event.download.VideoUpdateNotification;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.beans.BaseResponse;
import com.chinafocus.lib_network.net.errorhandler.HttpErrorHandler;
import com.google.gson.Gson;

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

import static com.chinafocus.hvrskyworthvr.rtr.videolist.sub.RtrVideoSubViewModel.VIDEO_LIST_DATA;

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


    private final List<String> mPreVideoDeletedName = new ArrayList<>();
    private final List<String> mRealVideoDeletedName = new ArrayList<>();

    private int type;
    private int id;

    private String tempVideoContentList = "";

    /**
     * 拉取网络数据并对比本地文件，生成下载任务
     */
    public void checkedTaskAndDownLoad() {
        isDownLoadChecking = true;
        // 防止清理不干净，这里需要再次清理集合
        mDownLoadHolders.clear();
        mPreVideoDeletedName.clear();
        mRealVideoDeletedName.clear();
        doWork();
    }

    @SuppressLint("NewApi")
    private void doWork() {
        mSubscribe = createVideoContentListObservable()
                .doOnNext(this::cacheTempVideoContentList)
                .doOnNext(this::preLoadImage)
                .doOnNext(listBaseResponse -> listBaseResponse.getData().forEach(this::addDeletedName))
                .flatMap(listBaseResponse -> Observable.fromIterable(listBaseResponse.getData()))
                .doOnNext(this::createDownLoadHolder)
                .flatMap(this::createVideoDetailObservable)
                .map(BaseResponse::getData)
                .doOnNext(this::saveVideoDetailAndSubtitle)
                .doOnNext(this::createDownLoadHolder)
                .doOnNext(this::addDeletedName)
                .doOnComplete(this::notifyVideoContentList)
                .doOnComplete(this::checkPreVideoFileDeleted)
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

    private void saveVideoDetailAndSubtitle(VideoDetail videoDetail) {
        SPUtils.getInstance().put(type + ";" + id, new Gson().toJson(videoDetail));

        String subtitle = videoDetail.getSubtitle();
        if (!TextUtils.isEmpty(subtitle)) {
            String[] split = subtitle.split("/");
            String fileName = split[split.length - 1];
            if (fileName.toLowerCase().endsWith("ass")) {
                downLoadSubTitle(ConfigManager.getInstance().getDefaultUrl() + subtitle, fileName);
            }
        }

    }

    private void downLoadSubTitle(String url, String fileName) {
        File file = new File(ConfigManager.getInstance().getSubtitleFilePath(), fileName);
        if (!file.exists()) {
            Aria.download(this)
                    .load(url)     //读取下载地址
                    .setFilePath(file.getAbsolutePath()) //设置文件保存的完整路径
                    .resetState()
                    .create();   //创建并启动下载
        }
    }

    /**
     * 当doOnComplete触发的时候，保存本地数据，并通知列表页面刷新数据
     */
    private void notifyVideoContentList() {
        SPUtils.getInstance().put(VIDEO_LIST_DATA, tempVideoContentList);
        EventBus.getDefault().post(NotifyVideoContentList.obtain());
    }

    private void cacheTempVideoContentList(BaseResponse<List<VideoContentList>> listBaseResponse) {
        List<VideoContentList> data = listBaseResponse.getData();
        tempVideoContentList = new Gson().toJson(data);
    }

    private void preLoadImage(BaseResponse<List<VideoContentList>> listBaseResponse) {
        List<VideoContentList> data = listBaseResponse.getData();
        if (data != null && data.size() > 0) {
            realPreLoadImage(listBaseResponse.getData());
        }
    }

    private void realPreLoadImage(List<VideoContentList> videoContentLists) {
        for (VideoContentList temp : videoContentLists) {
            GlideApp.with(Utils.getApp().getApplicationContext())
                    .load(ConfigManager.getInstance().getDefaultUrl() + temp.getImgUrl() + ImageProcess.process(600, 400))
                    .preload();
        }
    }

    @SuppressLint("NewApi")
    private <T> void addDeletedName(T t) {

        if (t instanceof VideoDetail) {
            String name = ((VideoDetail) t).getVideoUrl();
            Optional.ofNullable(name)
                    .filter(s -> !TextUtils.isEmpty(s))
                    .ifPresent(mRealVideoDeletedName::add);
        } else if (t instanceof VideoContentList) {
            String name = ((VideoContentList) t).getMenuVideoUrl();
            Optional.ofNullable(name)
                    .filter(s -> !TextUtils.isEmpty(s))
                    .ifPresent(mPreVideoDeletedName::add);
        }
    }

    private void checkPreVideoFileDeleted() {
        checkAndDeletedFile(ConfigManager.getInstance().getPreVideoFilePath(), VideoType.PRE_VIDEO);
    }

    private void checkRealVideoFileDeleted() {
        checkAndDeletedFile(ConfigManager.getInstance().getRealVideoFilePath(), VideoType.REAL_VIDEO);
    }

    private void checkAndDeletedFile(String filePath, VideoType videoType) {
        List<String> mTempDeletedName = null;
        if (videoType == VideoType.REAL_VIDEO) {
            mTempDeletedName = mRealVideoDeletedName;
        } else if (videoType == VideoType.PRE_VIDEO) {
            mTempDeletedName = mPreVideoDeletedName;
        }
        assert mTempDeletedName != null;

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
                for (String temp : mTempDeletedName) {
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
        type = videoContentList.getType();
        id = videoContentList.getId();
        return ApiManager
                .getService(ApiMultiService.class)
                .getVideoDetailData(RequestBodyManager.getVideoDetailDataRequestBody(type, id))
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
        mPreVideoDeletedName.clear();
        mRealVideoDeletedName.clear();
        tempVideoContentList = "";
    }

}
