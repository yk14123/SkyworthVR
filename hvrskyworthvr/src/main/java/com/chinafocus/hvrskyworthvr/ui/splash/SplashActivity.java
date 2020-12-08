package com.chinafocus.hvrskyworthvr.ui.splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.model.bean.DefaultCloudUrl;
import com.chinafocus.hvrskyworthvr.ui.login.LoginActivity;
import com.chinafocus.hvrskyworthvr.ui.main.MainActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.observers.DisposableCompletableObserver;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        BarUtils.setStatusBarLightMode(this, true);

        DefaultUrlViewModel model = new ViewModelProvider(this).get(DefaultUrlViewModel.class);
        model.getDefaultCloudUrl();
        model.defaultCloudUrlMutableLiveData.observe(this, this::saveDefaultUrl);

    }

    private void saveDefaultUrl(DefaultCloudUrl url) {
//        SPUtils.getInstance().put(Constants.DEFAULT_URL, url.getCloudUrl());
        Constants.DEFAULT_URL = url.getCloudUrl();
        delayStartLoginActivity();
    }

    private void delayStartLoginActivity() {
        Completable
                .timer(1, TimeUnit.SECONDS)
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });


    }
}