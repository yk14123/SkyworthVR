package com.chinafocus.hvrskyworthvr.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.ui.setting.SettingActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.observers.DisposableCompletableObserver;

public class SplashActivity extends AppCompatActivity {

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


    }

    private void delayStartLoginActivity() {
        Completable
                .timer(2, TimeUnit.SECONDS)
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        startActivity(new Intent(SplashActivity.this, SettingActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });


    }
}