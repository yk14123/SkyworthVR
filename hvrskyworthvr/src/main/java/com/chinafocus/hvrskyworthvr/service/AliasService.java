package com.chinafocus.hvrskyworthvr.service;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.SPUtils;
import com.chinafocus.hvrskyworthvr.ui.dialog.SettingAliasDialog;

import static com.chinafocus.hvrskyworthvr.global.Constants.DEVICE_ALIAS;

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

    public void onClick(Context context) {
        if (mSettingAliasDialog == null) {
            mSettingAliasDialog = new SettingAliasDialog(context);
            mSettingAliasDialog.setAliasSettingListener(name -> {
                Log.e("MyLog", "name>>>" + name);
                if (!TextUtils.isEmpty(name)) {
                    // TODO 这里后期需要添加网络接口
                    if (mAliasStatusListener != null) {
                        mAliasStatusListener.aliasSettingSuccess(name);
                    }
                    SPUtils.getInstance().put(DEVICE_ALIAS, name);
                } else {
                    initDefaultDeviceName();
                }
            });
        }
        if (!mSettingAliasDialog.isShowing()) {
            mSettingAliasDialog.show();
        }
    }

    public void init(@NonNull AliasStatusListener aliasStatusListener) {
        mAliasStatusListener = aliasStatusListener;
        initDefaultDeviceName();
    }

    private void initDefaultDeviceName() {
        String name = SPUtils.getInstance().getString(DEVICE_ALIAS);
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

    }

}
