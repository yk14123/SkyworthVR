package com.chinafocus.hvrskyworthvr.ui.main.video;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.chinafocus.hvrskyworthvr.model.bean.VideoCategory;
import com.chinafocus.hvrskyworthvr.net.ApiService;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.base.BaseViewModel;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;
import com.chinafocus.lib_network.net.observer.BaseObserver;

import java.util.List;

public class VideoViewModel extends BaseViewModel {

    MutableLiveData<List<VideoCategory>> videoCategoryMutableLiveData = new MutableLiveData<>();


    public VideoViewModel(@NonNull Application application) {
        super(application);
    }

    void getVideoCategory() {
        addSubscribe(
                ApiManager
                        .getService(ApiService.class)
                        .getVideoCateGory(),
                new BaseObserver<List<VideoCategory>>() {
                    @Override
                    public void onSuccess(List<VideoCategory> videoCategories) {
                        videoCategoryMutableLiveData.postValue(videoCategories);
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {

                    }
                }
        );
    }


}