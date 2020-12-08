package com.chinafocus.hvrskyworthvr.ui.main;

import android.annotation.SuppressLint;
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
import java.util.stream.Collectors;


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
                    @SuppressLint("NewApi")
                    @Override
                    public void onSuccess(List<Banner> defaultCloudUrlBaseResponse) {
                        List<Banner> filterList = defaultCloudUrlBaseResponse
                                .stream()
                                .filter(banner -> !banner.getType().equals("field"))
                                .collect(Collectors.toList());
                        bannerMutableLiveData.postValue(filterList);
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {
                        //Log.e("MyLog", " Observer Throwable : code >>> " + e.code + " message >>> " + e.message);
                    }
                });
    }

}