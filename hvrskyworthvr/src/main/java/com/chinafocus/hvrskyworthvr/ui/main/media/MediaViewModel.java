package com.chinafocus.hvrskyworthvr.ui.main.media;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.arialyy.aria.core.Aria;
import com.blankj.utilcode.util.SPUtils;
import com.chinafocus.hvrskyworthvr.global.ConfigManager;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDetail;
import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.hvrskyworthvr.net.RequestBodyManager;
import com.chinafocus.hvrskyworthvr.util.LocalLogUtils;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.base.BaseViewModel;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;
import com.chinafocus.lib_network.net.errorhandler.HttpErrorHandler;
import com.chinafocus.lib_network.net.observer.BaseObserver;
import com.google.gson.Gson;

import java.io.File;

import io.reactivex.schedulers.Schedulers;

public class MediaViewModel extends BaseViewModel {

    public MutableLiveData<VideoDetail> videoDetailMutableLiveData = new MutableLiveData<>();

    public MediaViewModel(@NonNull Application application) {
        super(application);
    }

    public void getVideoDetailDataFromLocal(int tag, int id) {
        String obj = SPUtils.getInstance().getString(tag + ";" + id);
        if (!TextUtils.isEmpty(obj)) {
            videoDetailMutableLiveData.postValue(new Gson().fromJson(obj, VideoDetail.class));
        } else {
//            saveVideoDetailDataFromNet(tag, id);
            LocalLogUtils.e("MediaViewModel", " ******[严重错误]******  本地缓存内容为null ***** 对应null的tag和id为 >>> " + tag + ";" + id);
        }
    }

    public void saveVideoDetailDataFromNet(int tag, int id) {
        ApiManager
                .getService(ApiMultiService.class)
                .getVideoDetailData(RequestBodyManager.getVideoDetailDataRequestBody(tag, id))
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(new HttpErrorHandler<>())
                .subscribe(new BaseObserver<VideoDetail>() {
                    @Override
                    public void onSuccess(VideoDetail videoDetail) {
                        SPUtils.getInstance().put(tag + ";" + id, new Gson().toJson(videoDetail));

                        String subtitle = videoDetail.getSubtitle();
                        if (!TextUtils.isEmpty(subtitle)) {
                            String[] split = subtitle.split("/");
                            String fileName = split[split.length - 1];
                            if (fileName.toLowerCase().endsWith("ass")) {
                                downLoadSubTitle(ConfigManager.getInstance().getDefaultUrl() + subtitle, fileName);
                            }
                        }
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {

                    }
                });

    }

    public void downLoadSubTitle(String url, String fileName) {
        File subtitle = getApplication().getApplicationContext().getExternalFilesDir("subtitle");
        File file = new File(subtitle, fileName);
        if (!file.exists()) {
            Aria.download(this)
                    .load(url)     //读取下载地址
                    .setFilePath(file.getAbsolutePath()) //设置文件保存的完整路径
                    .resetState()
                    .create();   //创建并启动下载
        }
    }
}
