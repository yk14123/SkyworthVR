package com.chinafocus.hvrskyworthvr.ui.splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SPUtils;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.model.bean.DefaultCloudUrl;
import com.chinafocus.hvrskyworthvr.net.TcpClient;
import com.chinafocus.hvrskyworthvr.service.SocketService;
import com.chinafocus.hvrskyworthvr.service.event.VrSyncPlayInfo;
import com.chinafocus.hvrskyworthvr.ui.login.LoginActivity;
import com.chinafocus.hvrskyworthvr.ui.main.MainActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        BarUtils.setStatusBarLightMode(this, true);

        DefaultUrlViewModel model = new ViewModelProvider(this).get(DefaultUrlViewModel.class);

        PermissionUtils.permission(PermissionConstants.STORAGE, PermissionConstants.PHONE).callback(new PermissionUtils.SimpleCallback() {
            @Override
            public void onGranted() {
                model.getDefaultCloudUrl();
            }

            @Override
            public void onDenied() {
                finish();
                Toast.makeText(SplashActivity.this, "权限请求失败", Toast.LENGTH_SHORT).show();
            }
        }).request();

        model.defaultCloudUrlMutableLiveData.observe(this, this::saveDefaultUrl);

    }

    private void saveDefaultUrl(DefaultCloudUrl url) {
//        SPUtils.getInstance().put(Constants.DEFAULT_URL, url.getCloudUrl());
        startSocketService();
        Constants.DEFAULT_URL = url.getCloudUrl();
        delayStartLoginActivity();
    }

    private void startSocketService() {
        Intent intent = new Intent(this, SocketService.class);
        intent.putExtra("address", "192.168.8.170");
//        intent.putExtra("address", "10.10.21.69");
        intent.putExtra("port", 10102);
        SocketService.enqueueWork(this, intent);
    }

    private void delayStartLoginActivity() {
        Completable
                .timer(2, TimeUnit.SECONDS)
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
//                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });


    }
}