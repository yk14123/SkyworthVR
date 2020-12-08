package com.chinafocus.hvrskyworthvr.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.chinafocus.hvrskyworthvr.model.bean.Banner;
import com.chinafocus.hvrskyworthvr.net.ApiService;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.base.BaseViewModel;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;
import com.chinafocus.lib_network.net.observer.BaseObserver;

import java.util.List;


public class BannerViewModel extends BaseViewModel {
    // TODO: Implement the ViewModel
    public MutableLiveData<List<Banner>> bannerMutableLiveData = new MutableLiveData<>();

    public BannerViewModel(@NonNull Application application) {
        super(application);
    }

    public void getDefaultCloudUrl() {
        addSubscribe(
                ApiManager
                        .getService(ApiService.class)
                        .getBanner(),
                new BaseObserver<List<Banner>>() {
                    @Override
                    public void onSuccess(List<Banner> defaultCloudUrlBaseResponse) {
                        bannerMutableLiveData.postValue(defaultCloudUrlBaseResponse);
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {
                        //Log.e("MyLog", " Observer Throwable : code >>> " + e.code + " message >>> " + e.message);
                    }
                });
    }

}