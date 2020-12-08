package com.chinafocus.hvrskyworthvr.ui.main.video.sublist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.chinafocus.hvrskyworthvr.model.bean.VideoListData;
import com.chinafocus.hvrskyworthvr.net.ApiService;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.base.BaseViewModel;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;
import com.chinafocus.lib_network.net.observer.BaseObserver;

public class VideoListViewModel extends BaseViewModel {

    MutableLiveData<VideoListData> videoDataMutableLiveData = new MutableLiveData<>();

    public VideoListViewModel(@NonNull Application application) {
        super(application);
    }

    void getVideoData(int category) {
        addSubscribe(
                ApiManager
                        .getService(ApiService.class)
                        .getVideoListData(category, 1, 100, 1),
                new BaseObserver<VideoListData>() {
                    @Override
                    public void onSuccess(VideoListData videoListData) {
                        videoDataMutableLiveData.postValue(videoListData);
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {

                    }
                }
        );
    }
}