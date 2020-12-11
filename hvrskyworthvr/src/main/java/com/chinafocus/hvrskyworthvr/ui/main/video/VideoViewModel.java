package com.chinafocus.hvrskyworthvr.ui.main.video;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.chinafocus.hvrskyworthvr.model.bean.VideoCategory;
import com.chinafocus.hvrskyworthvr.net.ApiService;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.base.BaseViewModel;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;
import com.chinafocus.lib_network.net.observer.BaseObserver;

import java.util.List;
import java.util.stream.Collectors;

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
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(List<VideoCategory> videoCategories) {
                        List<VideoCategory> collect = videoCategories
                                .stream()
                                .filter(videoCategory -> videoCategory.getCid() != 11)
                                .collect(Collectors.toList());
                        videoCategoryMutableLiveData.postValue(collect);
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {

                    }
                }
        );
    }


}