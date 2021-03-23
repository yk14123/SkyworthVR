package com.chinafocus.hvrskyworthvr.rtr.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.chinafocus.hvrskyworthvr.model.bean.VideoCategory;
import com.chinafocus.hvrskyworthvr.net.RequestBodyManager;
import com.chinafocus.hvrskyworthvr.net.edu.EduApiService;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.base.BaseViewModel;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;
import com.chinafocus.lib_network.net.observer.BaseObserver;

import java.util.List;

public class RtrMainViewModel extends BaseViewModel {

    public RtrMainViewModel(@NonNull Application application) {
        super(application);
    }

    MutableLiveData<List<VideoCategory>> videoDetailMutableLiveData = new MutableLiveData<>();

    void getVideoDetailData() {
        addSubscribe(
                ApiManager
                        .getService(EduApiService.class)
                        .getVideoListCategory(RequestBodyManager.getVideoListRequestBody(123)),
                new BaseObserver<List<VideoCategory>>() {
                    @Override
                    public void onSuccess(List<VideoCategory> videoDetail) {
                        videoDetailMutableLiveData.postValue(videoDetail);
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {

                    }
                }
        );
    }
}
