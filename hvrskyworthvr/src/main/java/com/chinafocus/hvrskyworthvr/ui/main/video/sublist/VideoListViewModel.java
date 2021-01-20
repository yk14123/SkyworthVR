package com.chinafocus.hvrskyworthvr.ui.main.video.sublist;

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

public class VideoListViewModel extends BaseViewModel {

    MutableLiveData<List<VideoDataInfo>> videoDataMutableLiveData = new MutableLiveData<>();

    public VideoListViewModel(@NonNull Application application) {
        super(application);
    }

    void getVideoData(int category) {
        addSubscribe(
                ApiManager
                        .getService(ApiMultiService.class)
                        .getVideoListData(RequestBodyManager.getVideoListRequestBody(category)),
                new BaseObserver<List<VideoDataInfo>>() {
                    @Override
                    public void onSuccess(List<VideoDataInfo> videoListData) {
                        videoDataMutableLiveData.postValue(videoListData);
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {

                    }
                }
        );
    }
}