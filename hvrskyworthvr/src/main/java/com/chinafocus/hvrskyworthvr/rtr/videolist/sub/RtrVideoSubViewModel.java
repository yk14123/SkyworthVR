package com.chinafocus.hvrskyworthvr.rtr.videolist.sub;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.SPUtils;
import com.chinafocus.hvrskyworthvr.model.bean.VideoContentList;
import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.hvrskyworthvr.net.RequestBodyManager;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.base.BaseViewModel;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;
import com.chinafocus.lib_network.net.errorhandler.HttpErrorHandler;
import com.chinafocus.lib_network.net.observer.BaseObserver;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import io.reactivex.schedulers.Schedulers;

public class RtrVideoSubViewModel extends BaseViewModel {

    private static final String VIDEO_LIST_DATA = "video_list_data";

    public MutableLiveData<List<VideoContentList>> videoDataMutableLiveData = new MutableLiveData<>();

    public RtrVideoSubViewModel(@NonNull Application application) {
        super(application);
    }

    public void refreshVideoContentList() {
        ApiManager
                .getService(ApiMultiService.class)
                .getEduVideoContentList(RequestBodyManager.getVideoListRequestBody(1))
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(new HttpErrorHandler<>())
                .subscribe(new BaseObserver<List<VideoContentList>>() {
                    @Override
                    public void onSuccess(List<VideoContentList> videoListData) {
                        SPUtils.getInstance().put(VIDEO_LIST_DATA, new Gson().toJson(videoListData));
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {

                    }
                });
    }

    public void getVideoContentList() {
        String list = SPUtils.getInstance().getString(VIDEO_LIST_DATA);
        if (!TextUtils.isEmpty(list)) {
            List<VideoContentList> videoContentLists = new Gson().fromJson(list, new TypeToken<List<VideoContentList>>() {
            }.getType());
            videoDataMutableLiveData.setValue(videoContentLists);
        } else {
            addSubscribe(
                    ApiManager
                            .getService(ApiMultiService.class)
                            .getEduVideoContentList(RequestBodyManager.getVideoListRequestBody(1)),
                    new BaseObserver<List<VideoContentList>>() {
                        @Override
                        public void onSuccess(List<VideoContentList> videoListData) {
                            if (videoListData.size() > 0) {
                                SPUtils.getInstance().put(VIDEO_LIST_DATA, new Gson().toJson(videoListData));
                                videoDataMutableLiveData.postValue(videoListData);
                            }
                        }

                        @Override
                        public void onFailure(ExceptionHandle.ResponseThrowable e) {

                        }
                    }
            );
        }

    }
}