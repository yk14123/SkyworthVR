package com.chinafocus.hvrskyworthvr.rtr.videoupdate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.blankj.utilcode.util.SPUtils;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.download.DownLoadHolder;
import com.chinafocus.hvrskyworthvr.download.VideoUpdateService;
import com.chinafocus.hvrskyworthvr.rtr.dialog.RtrVideoUpdateDialog;
import com.chinafocus.hvrskyworthvr.service.event.download.VideoUpdateLatest;
import com.chinafocus.hvrskyworthvr.service.event.download.VideoUpdateListError;
import com.chinafocus.hvrskyworthvr.service.event.download.VideoUpdateManagerStatus;
import com.chinafocus.hvrskyworthvr.util.SizeUtil;
import com.chinafocus.hvrskyworthvr.util.statusbar.StatusBarCompatFactory;
import com.chinafocus.hvrskyworthvr.util.widget.VideoUpdateStatusView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

import static com.chinafocus.hvrskyworthvr.download.VideoUpdateService.VIDEO_UPDATE_SERVICE;
import static com.chinafocus.hvrskyworthvr.download.VideoUpdateService.VIDEO_UPDATE_SERVICE_CANCEL;
import static com.chinafocus.hvrskyworthvr.download.VideoUpdateService.VIDEO_UPDATE_SERVICE_CHECK;
import static com.chinafocus.hvrskyworthvr.download.VideoUpdateService.VIDEO_UPDATE_SERVICE_START;
import static com.chinafocus.hvrskyworthvr.global.Constants.VIDEO_UPDATE_STATUS;

public class VideoUpdateManagerActivity extends AppCompatActivity {

    private AppCompatTextView mSize;
    private VideoUpdateStatusView mVideoUpdateStatusView;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch mSwitch;

    private RtrVideoUpdateDialog mRtrVideoUpdateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBarCompatFactory.getInstance().setStatusBarImmerse(this, true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_update_manager);

        mSize = findViewById(R.id.tv_total_size);

        mVideoUpdateStatusView = findViewById(R.id.view_video_update_status);
        mVideoUpdateStatusView.setNetErrorRetryClick(this::retryVideoUpdateEngine);
        mVideoUpdateStatusView.setRetryDownLoadClick(this::retryDownLoadEngine);

        mSwitch = findViewById(R.id.view_switch_button);
        findViewById(R.id.iv_video_update_back).setOnClickListener(v -> finish());

        initSwitch();
        initAvailableSizeAndTotalSize();
    }

    private void showVideoUpdateDialog() {
        if (mRtrVideoUpdateDialog == null) {
            mRtrVideoUpdateDialog = new RtrVideoUpdateDialog(this);
            mRtrVideoUpdateDialog.setOnCheckedChangeListener((isChange, switchStatus) -> {
                if (!isChange) {
                    // 没有改变，还原
                    mSwitch.setChecked(!mSwitch.isChecked());
                } else {
                    // 发送了改变
                    if (switchStatus) {
                        Log.e("MyLog", " 开始网络下载对比 !!!!!!!!");
                        mVideoUpdateStatusView.setVisibility(View.INVISIBLE);
                        startVideoUpdateEngine();
                    } else {
                        // TODO 需要清空当前下载任务
                        cancelDownLoadEngine();
                        Log.e("MyLog", " 需要清空当前下载任务 !!!!!!!!");
                        mVideoUpdateStatusView.showVideoUpdateClose();
                    }
                }
            });
        }
        if (!mRtrVideoUpdateDialog.isShowing()) {
            mRtrVideoUpdateDialog.show();
        }
    }

    private void initSwitch() {
        boolean isCheck = SPUtils.getInstance().getBoolean(VIDEO_UPDATE_STATUS);
        if (isCheck) {
            startVideoUpdateEngine();
        }
        mSwitch.setChecked(isCheck);
        mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> showVideoUpdateDialog());
    }

    private void initAvailableSizeAndTotalSize() {
        String availableSize = SizeUtil.getFsAvailableSize(Objects.requireNonNull(getExternalFilesDir("Videos")).getAbsolutePath());
        String totalSize = SizeUtil.getFsTotalSize(Objects.requireNonNull(getExternalFilesDir("Videos")).getAbsolutePath());
        mSize.setText(MessageFormat.format("{0}可用 / 共{1}", availableSize, totalSize));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void VideoUpdateManagerStatus(VideoUpdateManagerStatus event) {
        Log.d("MyLog", "----更新左上方状态显示 1/3 3/3 失败-----");
        mVideoUpdateStatusView.postVideoUpdateManagerStatus(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void VideoUpdatePayload(DownLoadHolder event) {
        Log.d("MyLog", "-----局部刷新 进度-----");
        mVideoUpdateStatusView.postPayload(event);
    }

    @Subscribe()
    @SuppressWarnings("unused")
    public void VideoUpdateStart(List<DownLoadHolder> event) {
        Log.d("MyLog", "-----存在更新，展示recyclerView-----");
        mVideoUpdateStatusView.showVideoUpdateDownload(event);
    }

    @Subscribe()
    @SuppressWarnings("unused")
    public void VideoUpdateLatest(VideoUpdateLatest event) {
        Log.d("MyLog", "-----当前列表已经是最新的-----");
        mVideoUpdateStatusView.showVideoUpdateLatest();
    }

    @Subscribe()
    @SuppressWarnings("unused")
    public void videoUpdateListError(VideoUpdateListError event) {
        Log.d("MyLog", "-----拉取大列表的时候，网络错误-----");
        mVideoUpdateStatusView.showVideoUpdateNetError();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 开启下载引擎
     */
    private void startVideoUpdateEngine() {
        Intent intent = new Intent(this, VideoUpdateService.class);
        intent.putExtra(VIDEO_UPDATE_SERVICE, VIDEO_UPDATE_SERVICE_CHECK);
        startService(intent);
    }

    /**
     * 退出下载引擎
     */
    private void cancelDownLoadEngine() {
        Intent intent = new Intent(this, VideoUpdateService.class);
        intent.putExtra(VIDEO_UPDATE_SERVICE, VIDEO_UPDATE_SERVICE_CANCEL);
        startService(intent);
    }

    /**
     * 重新下载下载引擎
     */
    private void retryDownLoadEngine() {
        Intent intent = new Intent(this, VideoUpdateService.class);
        intent.putExtra(VIDEO_UPDATE_SERVICE, VIDEO_UPDATE_SERVICE_START);
        startService(intent);
    }

    /**
     * 重新比对网络大列表 !!!!!!!!
     *
     * @param v View
     */
    private void retryVideoUpdateEngine(View v) {
        startVideoUpdateEngine();
    }

    /**
     * 重新下载任务 !!!!!!!!
     *
     * @param v View
     */
    private void retryDownLoadEngine(View v) {
        retryDownLoadEngine();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}