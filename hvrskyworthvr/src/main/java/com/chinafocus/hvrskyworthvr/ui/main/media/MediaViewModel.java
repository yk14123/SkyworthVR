package com.chinafocus.hvrskyworthvr.ui.main.media;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.SPUtils;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDetail;
import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.hvrskyworthvr.net.RequestBodyManager;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.base.BaseViewModel;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;
import com.chinafocus.lib_network.net.observer.BaseObserver;
import com.google.gson.Gson;

import io.reactivex.schedulers.Schedulers;

public class MediaViewModel extends BaseViewModel {

    public MutableLiveData<VideoDetail> videoDetailMutableLiveData = new MutableLiveData<>();

    public MediaViewModel(@NonNull Application application) {
        super(application);
    }

    public void getVideoDetailDataFromLocal(int tag, int id) {
//        addSubscribe(
//                ApiManager
//                        .getService(ApiMultiService.class)
//                        .getVideoDetailData(RequestBodyManager.getVideoDetailDataRequestBody(tag, id)),
//                new BaseObserver<VideoDetail>() {
//                    @Override
//                    public void onSuccess(VideoDetail videoDetail) {
//                        videoDetailMutableLiveData.postValue(videoDetail);
//                    }
//
//                    @Override
//                    public void onFailure(ExceptionHandle.ResponseThrowable e) {
//
//                    }
//                }
//        );

        String obj = SPUtils.getInstance().getString(tag + ";" + id);
        videoDetailMutableLiveData.postValue(new Gson().fromJson(obj, VideoDetail.class));
    }

    public void saveVideoDetailDataFromNet(int tag, int id) {
        ApiManager
                .getService(ApiMultiService.class)
                .getVideoDetailData(RequestBodyManager.getVideoDetailDataRequestBody(tag, id))
                .subscribeOn(Schedulers.io())
                .subscribe(new BaseObserver<VideoDetail>() {
                    @Override
                    public void onSuccess(VideoDetail videoDetail) {
                        SPUtils.getInstance().put(tag + ";" + id, new Gson().toJson(videoDetail));
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {

                    }
                });

    }
}
