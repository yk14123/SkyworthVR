package com.chinafocus.hvrskyworthvr.ui.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.model.DeviceInfoManager;
import com.chinafocus.hvrskyworthvr.ui.setting.SettingActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.observers.DisposableCompletableObserver;

public class SplashActivity extends AppCompatActivity {

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        BarUtils.setStatusBarLightMode(this, true);

        PermissionUtils.permission(
                PermissionConstants.STORAGE,
                PermissionConstants.PHONE,
                PermissionConstants.LOCATION).callback(new PermissionUtils.SimpleCallback() {
            @Override
            public void onGranted() {
                delayStartLoginActivity();
            }

            @Override
            public void onDenied() {
                finish();
                Toast.makeText(SplashActivity.this, "权限请求失败", Toast.LENGTH_SHORT).show();
            }
        }).request();


//        DeviceInfo userInfoBean = new DeviceInfo();
//        userInfoBean.setAlias("");
//        userInfoBean.setAppNo("003");
//        userInfoBean.setUserNo("1345966352287924224");
//        userInfoBean.setUniqueId("41426b75cfb945c79b01b4a669bbd283");

//

//        ApiManager
//                .getService(ApiMultiService.class)
//                .initDeviceInfo(body)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(baseResponse -> {
//                    Log.e("MyLog", " baseResponse >>> " + baseResponse.getErrCode());
//                });
//
//        ApiManager
//                .getService(ApiMultiService.class)
//                .getDefaultCloudUrl(body)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(baseResponse -> {
//                    Log.e("MyLog", " getCloudUrl >>> " + baseResponse.getData().getCloudUrl());
//                });
//
//        ApiManager
//                .getService(ApiMultiService.class)
//                .getDeviceInfo(body)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(baseResponse -> {
//                    Log.e("MyLog", " getCustomerName >>> " + baseResponse.getData().getCustomerName());
//                });


    }

    private void delayStartLoginActivity() {
        Completable
                .timer(2, TimeUnit.SECONDS)
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        boolean userNoInit = DeviceInfoManager.getInstance().isUserNumberExist();
                        if (userNoInit) {
                            startActivity(new Intent(SplashActivity.this, SettingActivity.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "请先初始化渠道号", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });


    }
}