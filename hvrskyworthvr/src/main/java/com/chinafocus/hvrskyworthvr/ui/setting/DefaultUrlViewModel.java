package com.chinafocus.hvrskyworthvr.ui.setting;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.chinafocus.hvrskyworthvr.model.bean.DefaultCloudUrl;
import com.chinafocus.hvrskyworthvr.net.ApiService;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.base.BaseViewModel;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;
import com.chinafocus.lib_network.net.observer.BaseObserver;


public class DefaultUrlViewModel extends BaseViewModel {
    // TODO: Implement the ViewModel
    MutableLiveData<DefaultCloudUrl> defaultCloudUrlMutableLiveData = new MutableLiveData<>();

    public DefaultUrlViewModel(@NonNull Application application) {
        super(application);
    }

    void getDefaultCloudUrl() {
        addSubscribe(
                ApiManager
                        .getService(ApiService.class)
                        .getDefaultCloudUrl(),
                new BaseObserver<DefaultCloudUrl>() {
                    @Override
                    public void onSuccess(DefaultCloudUrl defaultCloudUrlBaseResponse) {
                        defaultCloudUrlMutableLiveData.postValue(defaultCloudUrlBaseResponse);
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {
                        //Log.e("MyLog", " Observer Throwable : code >>> " + e.code + " message >>> " + e.message);
                    }
                });
    }

}