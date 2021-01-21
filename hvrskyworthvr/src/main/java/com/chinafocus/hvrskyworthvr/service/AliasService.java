package com.chinafocus.hvrskyworthvr.service;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.chinafocus.hvrskyworthvr.model.DeviceInfoManager;
import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.hvrskyworthvr.net.RequestBodyManager;
import com.chinafocus.hvrskyworthvr.ui.dialog.SettingAliasDialog;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;
import com.chinafocus.lib_network.net.errorhandler.HttpErrorHandler;
import com.chinafocus.lib_network.net.observer.BaseObserver;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AliasService {

    private SettingAliasDialog mSettingAliasDialog;

    private AliasService() {
    }

    private static AliasService instance;

    public static AliasService getInstance() {
        if (instance == null) {
            synchronized (AliasService.class) {
                if (instance == null) {
                    instance = new AliasService();
                }
            }
        }
        return instance;
    }

    private void postSetDeviceInfoAlias(String newName) {
        ApiManager
                .getService(ApiMultiService.class)
                .postSetDeviceAlias(RequestBodyManager.getRequestAliasBody(newName))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread(), true)
                .onErrorResumeNext(new HttpErrorHandler<>())
                .subscribe(new BaseObserver<Object>() {
                    @Override
                    public void onSuccess(Object deviceInfo) {
                        DeviceInfoManager.getInstance().postDeviceAlias(newName);
                        if (mAliasStatusListener != null) {
                            mAliasStatusListener.aliasSettingSuccess(newName);
                        }
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {
                        if (mAliasStatusListener != null) {
                            mAliasStatusListener.aliasSettingError();
                        }
                    }
                });
    }

    public void onClick(Context context) {
        if (mSettingAliasDialog == null) {
            mSettingAliasDialog = new SettingAliasDialog(context);
            mSettingAliasDialog.setAliasSettingListener(name -> {
                Log.e("MyLog", "name>>>" + name);
                if (!TextUtils.isEmpty(name)) {
                    postSetDeviceInfoAlias(name);
                } else {
                    initDefaultDeviceName();
                }
            });
        }
        if (!mSettingAliasDialog.isShowing()) {
            mSettingAliasDialog.show();
        }
    }

    public void clearAliasDialog() {
        mSettingAliasDialog = null;
    }

    public void init(@NonNull AliasStatusListener aliasStatusListener) {
        mAliasStatusListener = aliasStatusListener;
        initDefaultDeviceName();
    }

    private void initDefaultDeviceName() {
        String name = DeviceInfoManager.getInstance().getDeviceAlias();
        if (!TextUtils.isEmpty(name)) {
            if (mAliasStatusListener != null) {
                mAliasStatusListener.aliasSettingSuccess(name);
            }
        } else {
            if (mAliasStatusListener != null) {
                mAliasStatusListener.aliasStatusInit();
            }
        }
    }

    private AliasStatusListener mAliasStatusListener;

    public interface AliasStatusListener {
        void aliasStatusInit();

        void aliasSettingSuccess(String name);

        void aliasSettingError();

    }

}
