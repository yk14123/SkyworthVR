package com.chinafocus.hvrskyworthvr.rtr.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ScreenUtils;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.util.widget.CircleProgressBar;

/**
 * App更新Dialog
 */
public class RtrAppUpdateDialog extends Dialog implements View.OnClickListener {

    private Group mGroupUpdatePreHandle;
    private Group mGroupUpdatePreForce;
    private Group mGroupUpdateRunningHandle;
    private Group mGroupUpdatePreStatus;
    private Group mGroupUpdateRunningStatus;
    private Group mGroupUpdateErrorStatus;

    private AppCompatTextView mVersionCode;
    private AppCompatTextView mVersionDes;
    private AppCompatTextView mTvUpdateRunningDes;

    private AppCompatButton mBtUpdateRunningOk;

    private CircleProgressBar mProgressChart;

    private DownLoadListener mDownLoadListener;

    private static final int HANDLE = 0;
    private static final int FORCE = 1;

    private static final int TASK_PAUSE = 0;
    private static final int TASK_RESUME = 1;
    private static final int TASK_COMPLETE = 2;

    private int currentStatus;

    public void setDownLoadListener(DownLoadListener downLoadListener) {
        mDownLoadListener = downLoadListener;
    }

    public RtrAppUpdateDialog(Context context) {
        super(context, R.style.VrModeMainDialog);
        init(context);
    }

    private void init(Context context) {
        @SuppressLint("InflateParams") View mContentView = LayoutInflater.from(context).inflate(R.layout.rtr_dialog_check_version, null);
        int screenWidth = ScreenUtils.getScreenWidth();
        int screenHeight = ScreenUtils.getScreenHeight();
        setContentView(mContentView, new ViewGroup.LayoutParams(screenWidth, screenHeight));

        // 下载之前
        mVersionCode = mContentView.findViewById(R.id.tv_check_version_code);
        mVersionDes = mContentView.findViewById(R.id.tv_check_version_des);
        mContentView.findViewById(R.id.bt_update_pre_cancel).setOnClickListener(this);
        mContentView.findViewById(R.id.bt_status_update_handle).setOnClickListener(this);
        mContentView.findViewById(R.id.bt_status_update_force).setOnClickListener(this);

        mGroupUpdatePreHandle = mContentView.findViewById(R.id.group_update_pre_handle);
        mGroupUpdatePreForce = mContentView.findViewById(R.id.group_update_pre_force);

        // 下载中
        mGroupUpdateRunningHandle = mContentView.findViewById(R.id.group_update_running_handle);
        mProgressChart = mContentView.findViewById(R.id.pc_update_running);
        mTvUpdateRunningDes = mContentView.findViewById(R.id.tv_update_running_des);
        mContentView.findViewById(R.id.bt_update_running_cancel).setOnClickListener(this);
        mBtUpdateRunningOk = mContentView.findViewById(R.id.bt_update_running_ok);
        mBtUpdateRunningOk.setOnClickListener(this);

        // 下载失败
        mContentView.findViewById(R.id.bt_status_error_cancel).setOnClickListener(this);
        // 下载失败重新下载
        mContentView.findViewById(R.id.bt_status_error_retry).setOnClickListener(this);

        // 三种状态切换
        mGroupUpdatePreStatus = mContentView.findViewById(R.id.group_update_pre);
        mGroupUpdateRunningStatus = mContentView.findViewById(R.id.group_update_running);
        mGroupUpdateErrorStatus = mContentView.findViewById(R.id.group_update_error);

        // 设置外部可以取消
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        showUpdatePreUI();
        pauseUpdateRunningButtonUI();
    }

    private void pauseUpdateRunningButtonUI() {
        currentStatus = TASK_PAUSE;
        mTvUpdateRunningDes.setText(getContext().getString(R.string.check_version_download_des));
        mBtUpdateRunningOk.setText(getContext().getString(R.string.check_version_pause));
        mBtUpdateRunningOk.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.shape_check_version_pause_bg));
    }

    private void resumeUpdateRunningButtonUI() {
        currentStatus = TASK_RESUME;
        mTvUpdateRunningDes.setText(getContext().getString(R.string.check_version_download_des));
        mBtUpdateRunningOk.setText(getContext().getString(R.string.check_version_resume));
        mBtUpdateRunningOk.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.shape_check_version_confirm_bg));
    }

    public void showUpdatePreUI() {
        mGroupUpdatePreStatus.setVisibility(View.VISIBLE);
        mGroupUpdateRunningStatus.setVisibility(View.GONE);
        mGroupUpdateErrorStatus.setVisibility(View.GONE);
    }

    private void showUpdateRunningUI() {
        mGroupUpdatePreStatus.setVisibility(View.GONE);
        mGroupUpdateRunningStatus.setVisibility(View.VISIBLE);
        mGroupUpdateErrorStatus.setVisibility(View.GONE);
    }

    private void showUpdateErrorUI() {
        mGroupUpdatePreStatus.setVisibility(View.GONE);
        mGroupUpdateRunningStatus.setVisibility(View.GONE);
        mGroupUpdateErrorStatus.setVisibility(View.VISIBLE);
    }

    public void postTaskRunningProgress(int f) {
        mProgressChart.setProgress(f);
    }

    public void postTaskComplete() {
        currentStatus = TASK_COMPLETE;
        mGroupUpdateRunningHandle.setVisibility(View.VISIBLE);
        mTvUpdateRunningDes.setText(getContext().getString(R.string.check_version_download_complete));
        mBtUpdateRunningOk.setText(getContext().getString(R.string.check_version_install_immediately));
        mBtUpdateRunningOk.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.shape_check_version_confirm_bg));
    }

    public void postTaskFail() {
        showUpdateErrorUI();
    }

    public void postVersionCodeAndDes(String versionName, String versionIntro) {
        mVersionCode.setText(String.format("V%s", versionName));
        mVersionDes.setText(versionIntro);
        mVersionDes.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    /**
     * 强制更新 或者 手动更新
     *
     * @param autoDownLoad 1：强制更新 0：手动更新
     */
    public void postStatusForce(int autoDownLoad) {
        switch (autoDownLoad) {
            case HANDLE:
                mGroupUpdatePreHandle.setVisibility(View.VISIBLE);
                mGroupUpdatePreForce.setVisibility(View.GONE);
                mGroupUpdateRunningHandle.setVisibility(View.VISIBLE);
                break;
            case FORCE:
                mGroupUpdatePreHandle.setVisibility(View.GONE);
                mGroupUpdatePreForce.setVisibility(View.VISIBLE);
                mGroupUpdateRunningHandle.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_status_update_handle:
            case R.id.bt_status_update_force:
                immediatelyDownLoad();
                break;
            case R.id.bt_update_pre_cancel:
            case R.id.bt_update_running_cancel:
            case R.id.bt_status_error_cancel:
                mProgressChart.setProgress(0);
                dismiss();
                break;
            case R.id.bt_status_error_retry:
                retryDownLoad();
                break;
            case R.id.bt_update_running_ok:
                pauseOrResumeOrInstall();
                break;
        }
    }

    private void pauseOrResumeOrInstall() {
        switch (currentStatus) {
            case TASK_PAUSE:
                pauseDownLoad();
                break;
            case TASK_RESUME:
                resumeDownLoad();
                break;
            case TASK_COMPLETE:
                installApp();
                break;
        }
    }

    // 立即恢复
    private void resumeDownLoad() {
        if (mDownLoadListener != null) {
            mDownLoadListener.resumeDownLoad();
        }
        pauseUpdateRunningButtonUI();
    }

    // 立即暂停
    private void pauseDownLoad() {
        if (mDownLoadListener != null) {
            mDownLoadListener.pauseDownLoad();
        }
        resumeUpdateRunningButtonUI();
    }

    // 立即安装
    private void installApp() {
        if (mDownLoadListener != null) {
            mDownLoadListener.installApp();
        }
    }

    // 重新下载
    private void retryDownLoad() {
        showUpdateRunningUI();
        pauseUpdateRunningButtonUI();
        if (mDownLoadListener != null) {
            mDownLoadListener.retryDownLoad();
        }
    }

    // 立即下载
    private void immediatelyDownLoad() {
        showUpdateRunningUI();
        pauseUpdateRunningButtonUI();
        if (mDownLoadListener != null) {
            mDownLoadListener.immediatelyDownLoad();
        }
    }

    public interface DownLoadListener {

        void immediatelyDownLoad();

        void retryDownLoad();

        void pauseDownLoad();

        void resumeDownLoad();

        void installApp();

    }

}
