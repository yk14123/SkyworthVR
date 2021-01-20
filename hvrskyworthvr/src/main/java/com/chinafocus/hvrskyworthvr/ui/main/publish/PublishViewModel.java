package com.chinafocus.hvrskyworthvr.ui.main.publish;

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

public class PublishViewModel extends BaseViewModel {

    MutableLiveData<List<VideoDataInfo>> videoListDataMutableLiveData = new MutableLiveData<>();

    public PublishViewModel(@NonNull Application application) {
        super(application);
    }

    void getVideoListData() {
        addSubscribe(
                ApiManager
                        .getService(ApiMultiService.class)
                        .getPublishListData(RequestBodyManager.getDefaultRequestBody()),
                new BaseObserver<List<VideoDataInfo>>() {
                    @Override
                    public void onSuccess(List<VideoDataInfo> videoDataInfoList) {
                        videoListDataMutableLiveData.postValue(videoDataInfoList);
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {
                    }
                }
        );
    }


}
