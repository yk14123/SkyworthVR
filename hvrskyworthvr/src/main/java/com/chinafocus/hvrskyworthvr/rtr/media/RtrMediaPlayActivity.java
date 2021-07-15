package com.chinafocus.hvrskyworthvr.rtr.media;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.BarUtils;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.exo.tools.ExoMediaHelper;
import com.chinafocus.hvrskyworthvr.exo.tools.ViewBindHelper;
import com.chinafocus.hvrskyworthvr.exo.ui.PlayerControlView;
import com.chinafocus.hvrskyworthvr.exo.ui.PlayerView;
import com.chinafocus.hvrskyworthvr.exo.ui.spherical.SphericalGLSurfaceView;
import com.chinafocus.hvrskyworthvr.global.ConfigManager;
import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.rtr.dialog.RtrBluetoothConnectedDialog;
import com.chinafocus.hvrskyworthvr.rtr.dialog.RtrBluetoothLostDialog;
import com.chinafocus.hvrskyworthvr.rtr.dialog.RtrMediaSettingDialog;
import com.chinafocus.hvrskyworthvr.rtr.dialog.RtrVideoDetailDialog;
import com.chinafocus.hvrskyworthvr.rtr.popup.MediaVRLinkPopupWindow;
import com.chinafocus.hvrskyworthvr.service.BluetoothService;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaCancelBluetoothLostDelayTask;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaDisConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaStartBluetoothLostDelayTask;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaSyncMediaInfo;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaWaitSelected;
import com.chinafocus.hvrskyworthvr.service.event.VrRotation;
import com.chinafocus.hvrskyworthvr.service.event.VrSyncMediaStatus;
import com.chinafocus.hvrskyworthvr.service.event.VrSyncPlayInfo;
import com.chinafocus.hvrskyworthvr.ui.main.media.MediaViewModel;
import com.chinafocus.hvrskyworthvr.util.FileContentUtil;
import com.chinafocus.hvrskyworthvr.util.statusbar.StatusBarCompatFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.chinafocus.hvrskyworthvr.global.Constants.RESULT_CODE_ACTIVE_BLUETOOTH_CONNECTED;
import static com.chinafocus.hvrskyworthvr.global.Constants.RESULT_CODE_ACTIVE_BLUETOOTH_LOST;
import static com.chinafocus.hvrskyworthvr.global.Constants.RESULT_CODE_ACTIVE_DIALOG;
import static com.chinafocus.hvrskyworthvr.global.Constants.RESULT_CODE_INACTIVE_DIALOG;
import static com.chinafocus.hvrskyworthvr.global.Constants.RESULT_CODE_INACTIVE_DIALOG_BACK;
import static com.chinafocus.hvrskyworthvr.global.Constants.RESULT_CODE_SELF_INACTIVE_DIALOG;
import static com.google.android.exoplayer2.Player.STATE_ENDED;

public class RtrMediaPlayActivity extends AppCompatActivity implements ViewBindHelper.PlayVideoListener, PlayerControlView.VisibilityListener {

    public static final String MEDIA_ID = "media_id";
    public static final String MEDIA_FROM_TAG = "media_from_tag";
    public static final String MEDIA_SEEK = "media_seek";
    public static final String MEDIA_CATEGORY_TAG = "media_category_tag";
    public static final String MEDIA_LINK_VR = "media_link_vr";

    private boolean linkingVr;

    private MediaVRLinkPopupWindow mMediaVRLinkPopupWindow;
    private RtrVideoDetailDialog videoDetailDialog;
    private PlayerView mLandPlayerView;
    private ExoMediaHelper mExoMediaHelper;
    private MediaViewModel mediaViewModel;

    private int nextVideoId;
    private int nextVideoType;
    private int currentVideoId;
    private MyBluetoothLostDelayTaskRunnable mMyBluetoothLostDelayTaskRunnable;
    private MyPlayerEventListener mMyPlayerEventListener;
    private RtrMediaSettingDialog mediaSettingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBarCompatFactory.getInstance().setStatusBarImmerse(this, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtr_media_play);

        handleIntent();
        initView(savedInstanceState);

        mediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);

        observerNetData();
    }

    /**
     * 订阅数据
     */
    @SuppressLint("NewApi")
    private void observerNetData() {
        mediaViewModel.videoDetailMutableLiveData.observe(this, videoDetail -> {
            Log.d("MyLog", "-----当前播放视频的标题是 >>> " + videoDetail.getTitle());

            addVideoPlayCount();

            String videoUrl = "";
            String subtitle = "";

            String videoTempUrl = videoDetail.getVideoUrl();
            if (!TextUtils.isEmpty(videoTempUrl)) {
                videoUrl = ConfigManager.getInstance().getDefaultUrl() + videoTempUrl;
            } else {
                Toast.makeText(getApplicationContext(), "当前无播放地址", Toast.LENGTH_SHORT).show();
                return;
            }

            String temp = videoDetail.getSubtitle();
            if (!TextUtils.isEmpty(temp)) {
                subtitle = ConfigManager.getInstance().getDefaultUrl() + temp;

                String[] splitSubtitle = subtitle.split("/");
                File fileSubtitle = new File(getExternalFilesDir("subtitle"), splitSubtitle[splitSubtitle.length - 1]);
                if (fileSubtitle.exists()) {
                    subtitle = fileSubtitle.getAbsolutePath();
                }
            }

            String[] splitVideoUrl = videoUrl.split("/");
            String endUrl = splitVideoUrl[splitVideoUrl.length - 1];
            if (endUrl.contains(".")) {
                String[] split = endUrl.split("\\.");
                endUrl = split[0];

                File fileVideoUrl = new File(getExternalFilesDir("Videos"), endUrl + ".mp4");
                if (fileVideoUrl.exists()) {
                    videoUrl = fileVideoUrl.getAbsolutePath();
                }
                File encryptVideoUrl = new File(getExternalFilesDir("Videos"), endUrl + ".chinafocus");
                if (encryptVideoUrl.exists()) {
                    videoUrl = encryptVideoUrl.getAbsolutePath();
                }
            }

            currentVideoId = videoDetail.getId();
            nextVideoId = videoDetail.getNextId();
            nextVideoType = videoDetail.getNextType();

//            Log.d("MyLog", "-----当前[视频]播放地址是 videoUrl >>> " + videoUrl);
//            Log.d("MyLog", "-----当前[字幕]播放地址是 subtitle >>> " + subtitle);

            mExoMediaHelper.onStart();
            mExoMediaHelper.prepareSource(videoUrl, null, subtitle);
            mExoMediaHelper.onResume();
            mExoMediaHelper.seekTo(VrSyncPlayInfo.obtain().getSeekTime());

            if (mMyPlayerEventListener == null) {
                mMyPlayerEventListener = new MyPlayerEventListener();
                mExoMediaHelper.getPlayer().addListener(mMyPlayerEventListener);
            }

            if (linkingVr) {
                mExoMediaHelper.getPlayer().setVolume(0f);
                mLandPlayerView.showController();
                ((SphericalGLSurfaceView) Objects.requireNonNull(mLandPlayerView.getVideoSurfaceView())).syncTouchVR();
            } else {
                mExoMediaHelper.getPlayer().setVolume(1f);
            }

            mLandPlayerView.setVideoTitle(videoDetail.getTitle());

            if (videoDetailDialog == null) {
                videoDetailDialog = new RtrVideoDetailDialog(this);
                videoDetailDialog.setOnShowListener(dialog -> mLandPlayerView.hideController());
                videoDetailDialog.setOnDismissListener(dialog -> mLandPlayerView.showController());
            }
            videoDetailDialog.setTitle(videoDetail.getTitle());
            videoDetailDialog.setMessage(videoDetail.getDescription());

        });
    }

    private class MyPlayerEventListener implements Player.EventListener {

        @Override
        public void onPlaybackStateChanged(int state) {
//                    if (linkingVr && state == STATE_ENDED) {
            // 3. Pad 位于播放结束界面时，如果此时 VR 被激活则 VR 端直接进入一级视频列表界面，Pad 回到视频列表界面的「不可选片状态」
            // 不用接受命令。
            // 当链接状态，播放结束后
//                        waitSelectedFromVR(null);
//                    } else
            if (!linkingVr && state == STATE_ENDED) {
                VrSyncPlayInfo.obtain().restoreVideoInfo();
                Log.e("MyLog", "当前播放完了:" + VrSyncPlayInfo.obtain());
            }
        }
    }

    private void handleIntent() {
        Intent intent = getIntent();

        int video_tag = intent.getIntExtra(MEDIA_FROM_TAG, -1);
        int category = intent.getIntExtra(MEDIA_CATEGORY_TAG, -1);
        currentVideoId = intent.getIntExtra(MEDIA_ID, -1);
        long seek = intent.getLongExtra(MEDIA_SEEK, 0L);
        linkingVr = intent.getBooleanExtra(MEDIA_LINK_VR, false);

        Log.d("MyLog", "------当前播放页面的初始状态Intent"
                + " >>> video_tag : " + video_tag
                + " >>> category : " + category
                + " >>> video_id : " + currentVideoId
                + " >>> seek : " + seek
                + " >>> linkingVr : " + linkingVr);
        VrSyncPlayInfo.obtain().saveAllState(video_tag, category, currentVideoId, seek);
    }

    /**
     * 初始化View
     *
     * @param savedInstanceState Bundle数据
     */
    private void initView(Bundle savedInstanceState) {
        mLandPlayerView = findViewById(R.id.player_view_land);
        mLandPlayerView.setControllerPadding(
                BarUtils.getStatusBarHeight()
                , BarUtils.getStatusBarHeight()
                , BarUtils.getStatusBarHeight()
                , BarUtils.getStatusBarHeight());

        mLandPlayerView.setControllerVisibilityListener(this);

        ((SphericalGLSurfaceView) Objects.requireNonNull(mLandPlayerView.getVideoSurfaceView())).resetScale();

//        mExoMediaHelper = new ExoMediaHelper(this, mLandPlayerView);
        mExoMediaHelper = ExoMediaHelper.getInstance();
        mExoMediaHelper.setUnexpectedRuntimeError(this::recreate);
        mExoMediaHelper.buildDataSourceFactory(mLandPlayerView);
        mExoMediaHelper.restoreSavedInstanceState(savedInstanceState);
        ViewBindHelper mViewBindHelper = new ViewBindHelper(mLandPlayerView);
        mViewBindHelper.bindPlayerView();
        mViewBindHelper.setPlayVideoListener(this);

        mLandPlayerView.syncSkyWorthMediaStatus(linkingVr);

    }

    /**
     * 在播放页面的时候，戴上VR眼镜
     * 把Pad的播放信息，同步给VR
     *
     * @param event VR链接事件
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void syncMediaInfoToVRConnect(VrMediaConnect event) {
        Log.d("MyLog", "-----当前在[播放页面]的时候，戴上VR眼镜-----");
        // 1.关闭当前dialog
        closeAllDialog();

        // 2.切换不可操作播放状态
        mLandPlayerView.showController();
        mLandPlayerView.syncSkyWorthMediaStatus(true);
        ((SphericalGLSurfaceView) Objects.requireNonNull(mLandPlayerView.getVideoSurfaceView())).syncTouchVR();

        linkingVr = true;

        SimpleExoPlayer player = mExoMediaHelper.getPlayer();
        if (player == null) {
            return;
        }

        if (player.getPlaybackState() == STATE_ENDED) {

            // 3.给VR同步视频信息
            BluetoothService.getInstance()
                    .sendMessage(
                            VrSyncPlayInfo.obtain().getTag(),
                            VrSyncPlayInfo.obtain().getCategory(),
                            VrSyncPlayInfo.obtain().getVideoId(),
                            VrSyncPlayInfo.obtain().getSeekTime()
                    );

            hideBluetoothConnectDialog();
            hideBluetoothLostDialog();
            setResult(RESULT_CODE_ACTIVE_DIALOG, new Intent().putExtra("currentVideoId", currentVideoId));
            finish();
        } else {
            VrSyncPlayInfo.obtain().setSeekTime(player.getCurrentPosition());
            // 4.pad端静音且暂停
            player.setVolume(0f);
            player.setPlayWhenReady(false);

            // 3.给VR同步视频信息
            BluetoothService.getInstance()
                    .sendMessage(
                            VrSyncPlayInfo.obtain().getTag(),
                            VrSyncPlayInfo.obtain().getCategory(),
                            VrSyncPlayInfo.obtain().getVideoId(),
                            VrSyncPlayInfo.obtain().getSeekTime()
                    );
        }
    }

    /**
     * VR同步后，VR摘下眼镜
     * 回到首页面
     *
     * @param event VR断开事件
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void disConnectFromVR(VrMediaDisConnect event) {
        // 1.关闭当前dialog
        closeAllDialog();
        Log.d("MyLog", "-----当前在[播放页面]的时候，取下VR眼镜-----");
        linkingVr = false;

        hideBluetoothLostDialog();
        hideBluetoothConnectDialog();
        // 2.保存当前页面播放时长
        VrSyncPlayInfo.obtain().setSeekTime(mExoMediaHelper.getPlayer().getCurrentPosition());
        setResult(RESULT_CODE_INACTIVE_DIALOG, new Intent().putExtra("currentVideoId", currentVideoId));
        finish();
        // 3.立即切换当前Activity为Main
        Constants.ACTIVITY_TAG = Constants.ACTIVITY_MAIN;
    }

    /**
     * VR端回到了列表页面，正在选择菜单
     * Pad端需要回到MainActivity，并进行等待
     *
     * @param vrMediaWaitSelected 等待VR选片
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void waitSelectedFromVR(VrMediaWaitSelected vrMediaWaitSelected) {
//        if (vrMediaWaitSelected == null) {
//            Log.d("MyLog", "-----VR端还在播放，Pad端提前播放完了-----");
//        } else {
        Log.d("MyLog", "-----VR端回到了[列表页面]，正在选择菜单-----");
//        }
        // 1.关闭当前dialog
        closeAllDialog();
        // 2.恢复视频保存信息
        VrSyncPlayInfo.obtain().restoreVideoInfo();

        hideBluetoothLostDialog();
        hideBluetoothConnectDialog();
        setResult(RESULT_CODE_ACTIVE_DIALOG, new Intent().putExtra("currentVideoId", currentVideoId));
        finish();
        // 3.立即切换当前Activity为Main
        Constants.ACTIVITY_TAG = Constants.ACTIVITY_MAIN;
    }

    /**
     * VR端控制Pad暂停或播放
     *
     * @param vrSyncMediaStatus 同步播放/暂停
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void syncMediaStatus(VrSyncMediaStatus vrSyncMediaStatus) {
        int playStatusTag = vrSyncMediaStatus.getPlayStatusTag();
        long seek = vrSyncMediaStatus.getSeek();
        SimpleExoPlayer player = mExoMediaHelper.getPlayer();
        if (player != null) {
            if (playStatusTag == 1) {
                if (vrSyncMediaStatus.seekNow()) {
                    player.seekTo(seek);
                }
                player.setPlayWhenReady(true);
            } else if (playStatusTag == 2) {
                player.setPlayWhenReady(false);
            }
            mLandPlayerView.hideController();
        }
    }

    /**
     * 戴上VR眼镜后，在VR眼镜内部切换影片!
     * Pad端需要同步展示
     *
     * @param vrMediaSyncMediaInfo VR切换影片
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void loadNextSyncMediaFromVR(VrMediaSyncMediaInfo vrMediaSyncMediaInfo) {
        Log.d("MyLog", "-----VR端加载了新的影片 >>> "
                + VrSyncPlayInfo.obtain());
        // 1.关闭当前dialog
        closeAllDialog();
        // 2.加载视频详情！
        loadNetData(VrSyncPlayInfo.obtain().getVideoId());
    }

    /**
     * 加载视频详情
     */
    private void loadNetData(int videoId) {
        int temp = -1;
        int video_tag = VrSyncPlayInfo.obtain().getTag();
        if (video_tag == 1) {
            temp = 2;
        } else if (video_tag == 2) {
            temp = 1;
        }
        // 2.加载视频
        mediaViewModel.getVideoDetailDataFromLocal(temp, videoId);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    /**
     * 在首页取下VR眼镜
     *
     * @param event 取下VR眼镜事件
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void startBluetoothLostDelayTask(VrMediaStartBluetoothLostDelayTask event) {
//        if (mMyBluetoothLostDelayTaskRunnable == null) {
//            mMyBluetoothLostDelayTaskRunnable = new MyBluetoothLostDelayTaskRunnable();
//        }
//        mLandPlayerView.removeCallbacks(mMyBluetoothLostDelayTaskRunnable);
//        Log.e("BluetoothEngineService"," !!!!!!!!!!!! 播放页面 >>> [开启]2秒延迟任务 ");
//        mLandPlayerView.postDelayed(mMyBluetoothLostDelayTaskRunnable, 6000);
    }

    private RtrBluetoothLostDialog mRtrBluetoothLostDialog;

    private void showBluetoothLostDialog() {
        if (mRtrBluetoothLostDialog == null) {
            mRtrBluetoothLostDialog = new RtrBluetoothLostDialog(this);
        }

        if (!mRtrBluetoothLostDialog.isShowing()) {
            mRtrBluetoothLostDialog.show();
        }
    }

    private void hideBluetoothLostDialog() {
        if (mRtrBluetoothLostDialog != null && mRtrBluetoothLostDialog.isShowing()) {
            mRtrBluetoothLostDialog.dismiss();
        }
    }

    private class MyBluetoothLostDelayTaskRunnable implements Runnable {

        @Override
        public void run() {
            hideBluetoothConnectDialog();
            Log.e("BluetoothEngineService", " ++++++++++在【播放页面】 执行了蓝牙断开");
            BluetoothService.getInstance().setBluetoothLostYet(true);
            if (linkingVr) {
                hideBluetoothLostDialog();
                setResult(RESULT_CODE_ACTIVE_BLUETOOTH_LOST, new Intent().putExtra("currentVideoId", 123));
                finish();
                Constants.ACTIVITY_TAG = Constants.ACTIVITY_MAIN;
            } else {
                showBluetoothLostDialog();
            }
        }

    }

    /**
     * 在首页取下VR眼镜
     *
     * @param event 取下VR眼镜事件
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void cancelBluetoothLostDelayTask(VrMediaCancelBluetoothLostDelayTask event) {
        Log.e("BluetoothEngineService", " !!!!!!!!!!!!!!!!! 播放页面 >>> [移除]延迟任务 ");
        mLandPlayerView.removeCallbacks(mMyBluetoothLostDelayTaskRunnable);
        if (BluetoothService.getInstance().isBluetoothLostYet()) {
            Log.e("BluetoothEngineService", "-----在【播放页面】 收到了蓝牙恢复，需要回到【首页】展示【蓝牙恢复页面】-----");
            BluetoothService.getInstance().setBluetoothLostYet(false);
            hideBluetoothConnectDialog();
            hideBluetoothLostDialog();
            setResult(RESULT_CODE_ACTIVE_BLUETOOTH_CONNECTED, new Intent().putExtra("currentVideoId", 123));
            finish();
            Constants.ACTIVITY_TAG = Constants.ACTIVITY_MAIN;
        }
        BluetoothService.getInstance().setBluetoothLostYet(false);
    }

    private RtrBluetoothConnectedDialog mRtrBluetoothConnectedDialog;

    private void showBluetoothConnectDialog() {
        if (mRtrBluetoothConnectedDialog == null) {
            mRtrBluetoothConnectedDialog = new RtrBluetoothConnectedDialog(this);
        }

        if (!mRtrBluetoothConnectedDialog.isShowing()) {
            mRtrBluetoothConnectedDialog.show();
        }
    }

    private void hideBluetoothConnectDialog() {
        if (mRtrBluetoothConnectedDialog != null && mRtrBluetoothConnectedDialog.isShowing()) {
            mRtrBluetoothConnectedDialog.dismiss();
        }
    }

    /**
     * 同步VR端旋转角度！
     *
     * @param vrRotation 同步角度
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void syncRotationFromVR(VrRotation vrRotation) {
        if (mLandPlayerView != null && linkingVr) {
            SphericalGLSurfaceView surfaceView = (SphericalGLSurfaceView) mLandPlayerView.getVideoSurfaceView();
            if (surfaceView != null) {
                // 同步四元数
                surfaceView.postRotationWithQuaternion(vrRotation.x, vrRotation.y, vrRotation.z, vrRotation.w);
            }
        }
    }

    private void closeAllDialog() {
        if (videoDetailDialog != null && videoDetailDialog.isShowing()) {
            videoDetailDialog.dismiss();
        }
        if (mMediaVRLinkPopupWindow != null && mMediaVRLinkPopupWindow.isShowing()) {
            mMediaVRLinkPopupWindow.dismiss(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (VrSyncPlayInfo.obtain().isVideoIdInvalid()) {
            hideBluetoothConnectDialog();
            hideBluetoothLostDialog();
            setResult(RESULT_CODE_INACTIVE_DIALOG_BACK, new Intent().putExtra("currentVideoId", currentVideoId));
            finish();
            return;
        }

        EventBus.getDefault().register(this);
        loadNetData(VrSyncPlayInfo.obtain().getVideoId());
        Constants.ACTIVITY_TAG = Constants.ACTIVITY_MEDIA;
        mExoMediaHelper.onPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        mExoMediaHelper.onPause();
        mExoMediaHelper.onClear();
        if (mExoMediaHelper.getPlayer() != null) {
            mExoMediaHelper.getPlayer().removeListener(mMyPlayerEventListener);
            mMyPlayerEventListener = null;
        }
        mMyBluetoothLostDelayTaskRunnable = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mExoMediaHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onMediaSetting() {
        Log.e("MyLog", "点击了设置");
        if (mediaSettingDialog == null) {
            mediaSettingDialog = new RtrMediaSettingDialog(this);
        }
        if (!mediaSettingDialog.isShowing()) {
            mediaSettingDialog.show();
        }
    }

    @Override
    public void onGoBackActivity() {
        if (!linkingVr) {
            hideBluetoothConnectDialog();
            hideBluetoothLostDialog();
            setResult(RESULT_CODE_SELF_INACTIVE_DIALOG, new Intent().putExtra("currentVideoId", currentVideoId));
            finish();
            // 3.立即切换当前Activity为Main
            Constants.ACTIVITY_TAG = Constants.ACTIVITY_MAIN;
        }
    }

    @Override
    public void onPlayNextVideo() {
        if (nextVideoId != 0) {
            VrSyncPlayInfo.obtain().clearVideoTime();
            VrSyncPlayInfo.obtain().setVideoId(nextVideoId);
            VrSyncPlayInfo.obtain().setTag(nextVideoType == 1 ? 2 : 1);
            mediaViewModel.getVideoDetailDataFromLocal(nextVideoType, nextVideoId);
        } else {
            Toast.makeText(this, "暂无下一个影片", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLinkVR(View view) {
        if (mMediaVRLinkPopupWindow == null) {
            mMediaVRLinkPopupWindow = new MediaVRLinkPopupWindow(this);
        }

        if (!mMediaVRLinkPopupWindow.isShowing()) {
            mMediaVRLinkPopupWindow.showPopupWindow(view);
        }
    }

    @Override
    public void onVideoSetting() {
        if (videoDetailDialog != null && !videoDetailDialog.isShowing()) {
            videoDetailDialog.show();
        }
    }

    @Override
    public void onVideoRetry() {
        VrSyncPlayInfo.obtain().setVideoId(currentVideoId);
        addVideoPlayCount();
    }

    /**
     * 配合singleTop使用，当singleTop启用的时候（PlayerActivity跳转PlayerActivity跳转），onPause -> onNewIntent ->
     * onResume
     */
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mExoMediaHelper.onNewIntent();
    }

    @Override
    public void onVisibilityChange(int visibility) {
        if (visibility != View.VISIBLE) {
            if (mMediaVRLinkPopupWindow != null && mMediaVRLinkPopupWindow.isShowing()) {
                mMediaVRLinkPopupWindow.dismiss();
            }
        }
    }

    /**
     * 统计播放次数+1
     */
    private void addVideoPlayCount() {
        Observable.fromCallable(() -> FileContentUtil.readCount(getApplicationContext(), "VideoCount.txt"))
                .subscribeOn(Schedulers.io())
                .map(s -> {
                    long i = Long.parseLong(s);
                    i++;
                    return String.valueOf(i);
                })
                .doOnNext(s -> FileContentUtil.writeCount(getApplicationContext(), "VideoCount.txt", s))
                .subscribe();
    }
}