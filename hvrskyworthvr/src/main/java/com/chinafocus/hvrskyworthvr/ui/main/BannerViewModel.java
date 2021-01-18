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

                        Banner banner = new Banner();
                        banner.setTitle("俏花旦");
                        banner.setIntro("中国杂技团《俏花旦》，是一支集体空竹表演，其最特别的是在杂技中融入京剧艺术元素，通过京剧服饰、音乐、动作、身段等运用，体现了国粹的大气和典雅华丽。演员身着京剧服饰，表演技巧新颖巧妙，动作惊险高难又不失轻松愉快，文活武演妩媚阳刚，是目前国际杂技表演中最具代表性、最具知名度的表演之一。");
                        banner.setId(10080);
                        banner.setType("video");
                        banner.setCoverImg("test-temp-cover/700X400/slide/qiaohuadan.jpg");

                        filterList.add(2, banner);
                        bannerMutableLiveData.postValue(filterList);
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {
                        //Log.e("MyLog", " Observer Throwable : code >>> " + e.code + " message >>> " + e.message);
                    }
                });
    }

}