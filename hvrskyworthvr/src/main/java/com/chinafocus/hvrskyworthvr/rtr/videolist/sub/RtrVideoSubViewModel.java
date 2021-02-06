package com.chinafocus.hvrskyworthvr.rtr.videolist.sub;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.chinafocus.hvrskyworthvr.model.bean.VideoContentList;
import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.hvrskyworthvr.net.RequestBodyManager;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.base.BaseViewModel;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;
import com.chinafocus.lib_network.net.observer.BaseObserver;

import java.util.List;

public class RtrVideoSubViewModel extends BaseViewModel {

    public MutableLiveData<List<VideoContentList>> videoDataMutableLiveData = new MutableLiveData<>();

    public RtrVideoSubViewModel(@NonNull Application application) {
        super(application);
    }

    public void getVideoContentList() {
        addSubscribe(
                ApiManager
                        .getService(ApiMultiService.class)
                        .getVideoContentList(RequestBodyManager.getVideoListRequestBody(1)),
                new BaseObserver<List<VideoContentList>>() {
                    @Override
                    public void onSuccess(List<VideoContentList> videoListData) {
                        videoDataMutableLiveData.postValue(videoListData);
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {

                    }
                }
        );
    }
}