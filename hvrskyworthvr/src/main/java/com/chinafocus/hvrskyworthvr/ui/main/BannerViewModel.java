package com.chinafocus.hvrskyworthvr.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.chinafocus.hvrskyworthvr.model.bean.VideoDataInfo;
import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.hvrskyworthvr.net.RequestBodyManager;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.base.BaseViewModel;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;
import com.chinafocus.lib_network.net.observer.BaseObserver;

import java.util.List;


public class BannerViewModel extends BaseViewModel {
    // : Implement the ViewModel
    public MutableLiveData<List<VideoDataInfo>> publishBannerMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<List<VideoDataInfo>> videoBannerMutableLiveData = new MutableLiveData<>();

    public BannerViewModel(@NonNull Application application) {
        super(application);
    }

    public void getPublishBanner() {
        addSubscribe(
                ApiManager
                        .getService(ApiMultiService.class)
                        .getPublishBanner(RequestBodyManager.getDefaultRequestBody()),
                new BaseObserver<List<VideoDataInfo>>() {
                    @Override
                    public void onSuccess(List<VideoDataInfo> videoDataInfoList) {
                        publishBannerMutableLiveData.postValue(videoDataInfoList);
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {
                        //Log.e("MyLog", " Observer Throwable : code >>> " + e.code + " message >>> " + e.message);
                    }
                });
    }

    public void getVideoBanner() {
        addSubscribe(
                ApiManager
                        .getService(ApiMultiService.class)
                        .getVideoBanner(RequestBodyManager.getDefaultRequestBody()),
                new BaseObserver<List<VideoDataInfo>>() {
                    @Override
                    public void onSuccess(List<VideoDataInfo> videoDataInfoList) {
                        videoBannerMutableLiveData.postValue(videoDataInfoList);
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {
                        //Log.e("MyLog", " Observer Throwable : code >>> " + e.code + " message >>> " + e.message);
                    }
                });
    }

}