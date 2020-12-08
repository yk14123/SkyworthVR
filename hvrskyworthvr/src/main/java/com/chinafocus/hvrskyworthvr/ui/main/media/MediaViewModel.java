package com.chinafocus.hvrskyworthvr.ui.main.media;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.chinafocus.hvrskyworthvr.model.bean.VideoDetail;
import com.chinafocus.hvrskyworthvr.net.ApiService;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.base.BaseViewModel;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;
import com.chinafocus.lib_network.net.observer.BaseObserver;

public class MediaViewModel extends BaseViewModel {

    MutableLiveData<VideoDetail> videoDetailMutableLiveData = new MutableLiveData<>();

    public MediaViewModel(@NonNull Application application) {
        super(application);
    }

    void getVideoDetailData(String tag, int id) {
        addSubscribe(
                ApiManager
                        .getService(ApiService.class)
                        .getVideoDetailData(tag, id),
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
