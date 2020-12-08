package com.chinafocus.hvrskyworthvr.ui.main.publish;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.chinafocus.hvrskyworthvr.model.bean.VideoListData;
import com.chinafocus.hvrskyworthvr.net.ApiService;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.base.BaseViewModel;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;
import com.chinafocus.lib_network.net.observer.BaseObserver;

public class PublishViewModel extends BaseViewModel {

    MutableLiveData<VideoListData> videoListDataMutableLiveData = new MutableLiveData<>();

    public PublishViewModel(@NonNull Application application) {
        super(application);
    }

    void getVideoListData() {
        addSubscribe(
                ApiManager
                        .getService(ApiService.class)
                        .getPublishListData(1, 100),
                new BaseObserver<VideoListData>() {
                    @Override
                    public void onSuccess(VideoListData videoListdata) {
                        videoListDataMutableLiveData.postValue(videoListdata);
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {
                    }
                }
        );
    }


}
