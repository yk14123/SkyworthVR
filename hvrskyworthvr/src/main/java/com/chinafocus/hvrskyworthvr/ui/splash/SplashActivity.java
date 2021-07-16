package com.chinafocus.hvrskyworthvr.ui.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.BrightnessUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.model.DeviceInfoManager;
import com.chinafocus.hvrskyworthvr.ui.setting.SettingActivity;
import com.chinafocus.hvrskyworthvr.util.statusbar.StatusBarCompatFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.observers.DisposableCompletableObserver;

import static com.chinafocus.hvrskyworthvr.global.Constants.REQUEST_CODE_LOCATION_SERVICE;
import static com.chinafocus.hvrskyworthvr.global.Constants.REQUEST_CODE_WRITE_SETTINGS;

public class SplashActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompatFactory.getInstance().setStatusBarImmerse(this, false);
        setContentView(R.layout.activity_splash);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        if (checkWriteSettings()) {
            if (checkLocationManager()) {
                requestPermissions();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkWriteSettings() {
        boolean b = Settings.System.canWrite(this);
        if (!b) {
            showPermissionsDialog("修改系统设置", () -> {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
            });
        }
        return b;
    }

    private void requestPermissions() {
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

    private boolean checkLocationManager() {
        LocationManager lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!ok) {//开了定位服务
            showPermissionsDialog("访问我的位置信息", () -> {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, REQUEST_CODE_LOCATION_SERVICE);
            });
        }
        return ok;
    }

    private void delayStartLoginActivity() {
        BrightnessUtils.setAutoBrightnessEnabled(false);
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

    private void showPermissionsDialog(String permissions, Runnable runnable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("注意：");
        builder.setMessage("我们的应用需要您授权\"" + permissions + "\"的权限,请点击\"设置\"确认开启");
        // 拒绝, 退出应用
        builder.setNegativeButton("退出",
                (dialog, which) -> finish());

        builder.setPositiveButton("确定",
                (dialog, which) -> {
                    if (runnable != null) {
                        runnable.run();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }
}