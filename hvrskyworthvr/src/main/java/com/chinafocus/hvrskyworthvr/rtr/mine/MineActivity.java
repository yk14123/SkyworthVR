package com.chinafocus.hvrskyworthvr.rtr.mine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chinafocus.huaweimdm.MdmMainActivity;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.download.DownLoadRunningManager;
import com.chinafocus.hvrskyworthvr.global.ConfigManager;
import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.model.DeviceInfoManager;
import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.hvrskyworthvr.rtr.dialog.RtrAppUpdateDialog;
import com.chinafocus.hvrskyworthvr.rtr.install.AppInstallViewModel;
import com.chinafocus.hvrskyworthvr.rtr.videoupdate.VideoUpdateManagerActivity;
import com.chinafocus.hvrskyworthvr.service.BluetoothService;
import com.chinafocus.hvrskyworthvr.service.event.VrAboutConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrSyncPlayInfo;
import com.chinafocus.hvrskyworthvr.service.event.download.VideoUpdateCancel;
import com.chinafocus.hvrskyworthvr.service.event.download.VideoUpdateLatest;
import com.chinafocus.hvrskyworthvr.service.event.download.VideoUpdateStart;
import com.chinafocus.hvrskyworthvr.ui.main.about.WebAboutActivity;
import com.chinafocus.hvrskyworthvr.ui.setting.SettingActivity;
import com.chinafocus.hvrskyworthvr.util.TimeOutClickUtil;
import com.chinafocus.hvrskyworthvr.util.ViewClickUtil;
import com.chinafocus.hvrskyworthvr.util.statusbar.StatusBarCompatFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.chinafocus.hvrskyworthvr.global.Constants.ACTIVITY_ABOUT;
import static com.chinafocus.hvrskyworthvr.global.Constants.RESULT_CODE_ACTIVE_DIALOG;
import static com.chinafocus.hvrskyworthvr.global.Constants.RESULT_CODE_INACTIVE_DIALOG;
import static com.chinafocus.hvrskyworthvr.global.Constants.RESULT_CODE_MINE_FINISH;
import static com.chinafocus.hvrskyworthvr.service.BluetoothService.CURRENT_VR_ONLINE_STATUS;
import static com.chinafocus.hvrskyworthvr.service.BluetoothService.VR_STATUS_ONLINE;

public class MineActivity extends AppCompatActivity {

    private AppInstallViewModel mAppInstallViewModel;
    private RtrAppUpdateDialog mRtrAppUpdateDialog;

    private AppCompatImageView mTag;
    private AppCompatImageView mVideoUpdateTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBarCompatFactory.getInstance().setStatusBarImmerse(this, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);

        Constants.ACTIVITY_TAG = ACTIVITY_ABOUT;

        findViewById(R.id.ctl_mine_root).setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);

        mAppInstallViewModel = new ViewModelProvider(this).get(AppInstallViewModel.class);
        mAppInstallViewModel.register();

        AppCompatTextView checkVersionCode = findViewById(R.id.tv_about_check_version_code);
        checkVersionCode.setText(String.format("检查新版本（V%s）", AppUtils.getAppVersionName()));
        mTag = findViewById(R.id.iv_check_version_icon_tag);
        mVideoUpdateTag = findViewById(R.id.iv_video_update_icon_tag);
        checkVersionCode.setOnClickListener(v -> mAppInstallViewModel.checkAppVersionAndUpdate());

        AppCompatTextView account = findViewById(R.id.tv_mine_about_account);
        account.setText(DeviceInfoManager.getInstance().getDeviceAccountName());

        ViewClickUtil.click(
                findViewById(R.id.tv_video_update_enter),
                this::startVideoUpdateManagerActivity
        );

        AppCompatTextView uuid = findViewById(R.id.tv_mine_about_uuid);
        uuid.setText(String.format("%s : %s", getString(R.string.setting_device_uuid), DeviceInfoManager.getInstance().getDeviceUUID()));

        findViewById(R.id.tv_back_door).setOnClickListener(v -> TimeOutClickUtil.getDefault().startTimeOutClick(this::startSettingActivity));
        findViewById(R.id.iv_setting_mdm).setOnClickListener(v -> TimeOutClickUtil.getMDM().startTimeOutClick(this::startMDMActivity));
        findViewById(R.id.ib_mine_back).setOnClickListener(v -> {
            setResult(RESULT_CODE_INACTIVE_DIALOG, new Intent().putExtra("currentVideoId", VrSyncPlayInfo.obtain().getVideoId()));
            finish();
        });

        ViewClickUtil.click(
                findViewById(R.id.tv_about_user_protocol),
                () -> WebAboutActivity.startWebAboutActivity(
                        this,
                        getString(R.string.about_user_protocol),
                        ConfigManager.getInstance().getDefaultUrl() + ApiMultiService.ABOUT_USER_PROTOCOL)
        );

        ViewClickUtil.click(
                findViewById(R.id.tv_about_privacy_protocol),
                () -> WebAboutActivity.startWebAboutActivity(
                        this,
                        getString(R.string.about_privacy_protocol),
                        ConfigManager.getInstance().getDefaultUrl() + ApiMultiService.ABOUT_PRIVACY_PROTOCOL)
        );

        ViewClickUtil.click(
                findViewById(R.id.tv_about_us_protocol),
                () -> WebAboutActivity.startWebAboutActivity(
                        this,
                        getString(R.string.about_us_protocol),
                        ConfigManager.getInstance().getDefaultUrl() + ApiMultiService.ABOUT_US_PROTOCOL)
        );

        initAppInstallViewModelObserve();
    }

    private void initAppInstallViewModelObserve() {
        mAppInstallViewModel.getAppVersionInfoMutableLiveData().observe(this, appVersionInfo -> {
            mTag.setVisibility(View.VISIBLE);
            if (mRtrAppUpdateDialog == null) {
                mRtrAppUpdateDialog = new RtrAppUpdateDialog(this);
                mRtrAppUpdateDialog.setDownLoadListener(new RtrAppUpdateDialog.DownLoadListener() {
                    @Override
                    public void immediatelyDownLoad() {
                        mAppInstallViewModel.restartDownLoad();
                    }

                    @Override
                    public void retryDownLoad() {
                        mAppInstallViewModel.restartDownLoad();
                    }

                    @Override
                    public void pauseDownLoad() {
                        mAppInstallViewModel.pauseDownLoad();
                    }

                    @Override
                    public void resumeDownLoad() {
                        mAppInstallViewModel.checkNetWorkAndResumeDownLoad();
                    }

                    @Override
                    public void installApp() {
                        mAppInstallViewModel.installApp();
                    }
                });
                mRtrAppUpdateDialog.setOnDismissListener(dialog -> {
                    mAppInstallViewModel.cancelDownLoad();
                    if (CURRENT_VR_ONLINE_STATUS == VR_STATUS_ONLINE) {
                        setResult(RESULT_CODE_ACTIVE_DIALOG, new Intent().putExtra("currentVideoId", VrSyncPlayInfo.obtain().getVideoId()));
                        finish();
                    }
                });
            }
//            mRtrAppUpdateDialog.postStatusForce(appVersionInfo.getAutoDownLoad());
            mRtrAppUpdateDialog.postStatusForce(1);
            mRtrAppUpdateDialog.postVersionCodeAndDes(appVersionInfo.getVersionName(), appVersionInfo.getVersionIntro());
            mRtrAppUpdateDialog.showUpdatePreUI();
            if (!mRtrAppUpdateDialog.isShowing()) {
                mRtrAppUpdateDialog.show();
            }
        });
        mAppInstallViewModel.getTaskRunning().observe(this, integer -> {
            if (mRtrAppUpdateDialog != null) {
                mRtrAppUpdateDialog.postTaskRunningProgress(integer);
            }
        });
        mAppInstallViewModel.getTaskComplete().observe(this, aVoid -> {
            if (mRtrAppUpdateDialog != null) {
                mRtrAppUpdateDialog.postTaskComplete();
            }
        });
        mAppInstallViewModel.getTaskFail().observe(this, aVoid -> {
            if (mRtrAppUpdateDialog != null) {
                mRtrAppUpdateDialog.postTaskFail();
            }
        });
        mAppInstallViewModel.getTaskResume().observe(this, aVoid -> {
            if (mRtrAppUpdateDialog != null) {
                mRtrAppUpdateDialog.pauseUpdateRunningButtonUI();
            }
        });
        mAppInstallViewModel.getNetWorkError().observe(this, aVoid -> ToastUtils.showShort(MineActivity.this.getString(R.string.check_network_error)));
        mAppInstallViewModel.getVersionLatest().observe(this, aVoid -> {
            ToastUtils.showShort(MineActivity.this.getString(R.string.check_version_latest));
            mTag.setVisibility(View.GONE);
        });
        mAppInstallViewModel.getTaskUpdateRunning().observe(this, aVoid -> ToastUtils.showShort(MineActivity.this.getString(R.string.app_download_running)));
    }

    private boolean isAppInstallDialogShow() {
        return mRtrAppUpdateDialog != null && mRtrAppUpdateDialog.isShowing();
    }

    private void startVideoUpdateManagerActivity() {
        startActivity(new Intent(this, VideoUpdateManagerActivity.class));
        finish();
    }

    private void startSettingActivity() {
        startActivity(new Intent(this, SettingActivity.class));
        finish();
    }

    private void startMDMActivity() {
        startActivity(new Intent(this, MdmMainActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_MINE_FINISH) {
            setResult(RESULT_CODE_ACTIVE_DIALOG, new Intent().putExtra("currentVideoId", VrSyncPlayInfo.obtain().getVideoId()));
            finish();
        }
    }

    /**
     * 在关于页面戴上VR眼镜
     *
     * @param event 戴上VR眼镜事件
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void connectToVR(VrAboutConnect event) {
        Log.d("MyLog", "-----在[关于]页面戴上VR眼镜-----");
        int videoId = VrSyncPlayInfo.obtain().getVideoId();
        VrSyncPlayInfo.obtain().restoreVideoInfo();
        BluetoothService.getInstance()
                .sendMessage(
                        VrSyncPlayInfo.obtain().getTag(),
                        VrSyncPlayInfo.obtain().getCategory(),
                        VrSyncPlayInfo.obtain().getVideoId(),
                        VrSyncPlayInfo.obtain().getSeekTime()
                );

        if (!isAppInstallDialogShow()) {
            setResult(RESULT_CODE_ACTIVE_DIALOG, new Intent().putExtra("currentVideoId", videoId));
            finish();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        showRedTag();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAppInstallViewModel.unRegister();
    }

    private void showRedTag() {
        if (mAppInstallViewModel.hasUpdate()) {
            mTag.setVisibility(View.VISIBLE);
        } else {
            mTag.setVisibility(View.GONE);
        }
        if (DownLoadRunningManager.getInstance().isDownLoadRunning()) {
            mVideoUpdateTag.setVisibility(View.VISIBLE);
        } else {
            mVideoUpdateTag.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void videoUpdateLatest(VideoUpdateLatest event) {
        Log.d("MyLog", "-----videoUpdateLatest，MineActivity 关闭小红点-----");
        if (mVideoUpdateTag != null) {
            mVideoUpdateTag.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void videoUpdateCancel(VideoUpdateCancel event) {
        Log.d("MyLog", "-----videoUpdateCancel，MineActivity 关闭小红点-----");
        if (mVideoUpdateTag != null) {
            mVideoUpdateTag.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void videoUpdateStart(VideoUpdateStart event) {
        Log.d("MyLog", "-----videoUpdateStart，MineActivity 展示小红点-----");
        if (mVideoUpdateTag != null) {
            mVideoUpdateTag.setVisibility(View.VISIBLE);
        }
    }
}