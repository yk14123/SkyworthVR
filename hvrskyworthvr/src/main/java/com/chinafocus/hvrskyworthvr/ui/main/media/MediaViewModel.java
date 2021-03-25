package com.chinafocus.hvrskyworthvr.ui.main.media;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.chinafocus.hvrskyworthvr.model.bean.VideoDetail;
import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.hvrskyworthvr.net.RequestBodyManager;
import com.chinafocus.hvrskyworthvr.net.edu.EduApiService;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.base.BaseViewModel;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;
import com.chinafocus.lib_network.net.observer.BaseObserver;

public class MediaViewModel extends BaseViewModel {

    public MutableLiveData<VideoDetail> videoDetailMutableLiveData = new MutableLiveData<>();

    public MediaViewModel(@NonNull Application application) {
        super(application);
    }

    public void getVideoDetailData(int tag, int id) {
        addSubscribe(
                ApiManager
                        .getService(ApiMultiService.class)
                        .getVideoDetailData(RequestBodyManager.getVideoDetailDataRequestBody(tag, id)),
                new BaseObserver<VideoDetail>() {
                    @Override
                    public void onSuccess(VideoDetail videoDetail) {
                        videoDetailMutableLiveData.postValue(videoDetail);
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {

                    }
                }
        );
    }

    public void getVideoDetailData(int tag, int id, String classify) {
        addSubscribe(
                ApiManager
                        .getService(EduApiService.class)
                        .getVideoDetailData(RequestBodyManager.getVideoDetailDataRequestBody(tag, id, classify)),
                new BaseObserver<VideoDetail>() {
                    @Override
                    public void onSuccess(VideoDetail videoDetail) {
                        videoDetailMutableLiveData.postValue(videoDetail);
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {

                    }
                }
        );
    }
}
