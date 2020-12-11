package com.chinafocus.hvrskyworthvr.ui.main.media;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.BarUtils;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.exo.tools.ExoMediaHelper;
import com.chinafocus.hvrskyworthvr.exo.tools.ViewBindHelper;
import com.chinafocus.hvrskyworthvr.exo.ui.PlayerView;
import com.chinafocus.hvrskyworthvr.exo.ui.spherical.SphericalGLSurfaceView;
import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDetail;
import com.chinafocus.hvrskyworthvr.service.event.VrConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrDisConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrRotation;
import com.chinafocus.hvrskyworthvr.ui.dialog.VideoDetailDialog;
import com.chinafocus.hvrskyworthvr.ui.dialog.VrModeVideoLinkingDialog;
import com.chinafocus.hvrskyworthvr.util.statusbar.StatusBarCompatFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.stream.Collectors;

public class MediaPlayActivity extends AppCompatActivity implements ViewBindHelper.PlayVideoListener {

    public static final String MEDIA_ID = "media_id";
    public static final String MEDIA_FROM_TAG = "media_category_tag";
    public static final String MEDIA_SEEK = "media_seek";
    public static final String MEDIA_CATEGORY_TAG = "media_category_tag";
    public static final String MEDIA_LINK_VR = "media_link_vr";
    private int video_id;
    private String video_tag;
    private long seek;
    private boolean linkingVr;

    private VrModeVideoLinkingDialog modeVideoLinkingDialog;
    private VideoDetailDialog videoDetailDialog;
    private PlayerView mLandPlayerView;
    private ExoMediaHelper mExoMediaHelper;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBarCompatFactory.getInstance().setStatusBarImmerse(this, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_play);

        handleIntent();
        initView(savedInstanceState);

        MediaViewModel mediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);
        mediaViewModel.getVideoDetailData(video_tag, video_id);
        mediaViewModel.videoDetailMutableLiveData.observe(this, videoDetail -> {
            Log.e("MyLog", " videoDetail >>> " + videoDetail.getTitle());

            List<VideoDetail.FilesBean> filesBeanList =
                    videoDetail
                            .getFiles()
                            .stream()
                            .filter(filesBean -> (filesBean.getType() == 1 && filesBean.getBitrate() == 8000) || (filesBean.getType() == 6))
                            .collect(Collectors.toList());

            String format = "";
            String videoUrl = "";
            String subtitle = "";
            for (VideoDetail.FilesBean filesBean : filesBeanList) {
                if (filesBean.getType() == 1) {
                    videoUrl = Constants.DEFAULT_URL + filesBean.getFilePath();
                    if (videoUrl.toLowerCase().endsWith("m3u8")) {
                        format = "m3u8";
                    } else if (videoUrl.toLowerCase().endsWith("mp4")) {
                        format = "mp4";
                    }
                } else if (filesBean.getType() == 6) {
                    subtitle = Constants.DEFAULT_URL + filesBean.getFilePath();
                }
            }

            Log.e("MyLog", " videoUrl >>> " + videoUrl);

            if (!TextUtils.isEmpty(videoUrl)) {
                mExoMediaHelper.onStart();
                mExoMediaHelper.prepareSource(format, videoUrl, null, subtitle);
                mExoMediaHelper.onResume();

                mLandPlayerView.setVideoTitle(videoDetail.getTitle());

                if (videoDetailDialog == null) {
                    videoDetailDialog = new VideoDetailDialog(this);
                    videoDetailDialog.setOnShowListener(dialog -> mLandPlayerView.hideController());
                    videoDetailDialog.setOnDismissListener(dialog -> mLandPlayerView.showController());
                }
                videoDetailDialog.setTitle(videoDetail.getTitle());
                videoDetailDialog.setMessage(videoDetail.getIntro());
            }

        });

    }

    private void handleIntent() {
        Intent intent = getIntent();
        video_id = intent.getIntExtra(MEDIA_ID, -1);
        video_tag = intent.getStringExtra(MEDIA_FROM_TAG);
        seek = intent.getLongExtra(MEDIA_SEEK, 0);
        linkingVr = intent.getBooleanExtra(MEDIA_LINK_VR, false);
    }

    private void initView(Bundle savedInstanceState) {
        mLandPlayerView = findViewById(R.id.player_view_land);
        mLandPlayerView.setControllerPadding(
                BarUtils.getStatusBarHeight()
                , BarUtils.getStatusBarHeight()
                , BarUtils.getStatusBarHeight()
                , BarUtils.getStatusBarHeight());

        ((SphericalGLSurfaceView) mLandPlayerView.getVideoSurfaceView()).resetScale();

        mExoMediaHelper = new ExoMediaHelper(this, mLandPlayerView);
        mExoMediaHelper.restoreSavedInstanceState(savedInstanceState);
        ViewBindHelper mViewBindHelper = new ViewBindHelper(mLandPlayerView);
        mViewBindHelper.bindPlayerView();
        mViewBindHelper.setPlayVideoListener(this);

        mLandPlayerView.syncSkyWorthMediaStatus(false);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toUnityMediaInfoAndActiveVRPlayerStatus(VrConnect event) {
        // 1.给VR同步视频信息

        // 2.切换不可操作播放状态

        linkingVr = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void goBackMainActivityAndInactiveMainDialog(VrDisConnect event) {
        linkingVr = false;
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void syncRotation(VrRotation vrRotation) {
        // 同步四元数
    }

    // 3. Pad 位于播放结束界面时，如果此时 VR 被激活则 VR 端直接进入一级视频列表界面，Pad 回到视频列表界面的「不可选片状态」
    // 不用接受命令。
    // 当链接状态，播放结束后
    void goBackMainActivityAndActiveMainDialog() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mExoMediaHelper.onStop();
    }

    @Override
    public void onStop() {
        super.onStop();
        mExoMediaHelper.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mExoMediaHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mExoMediaHelper.onSaveInstanceState(outState);
    }

//    @Override
//    public void onVisibilityChange(int visibility) {
//        if (visibility == View.VISIBLE) {
//            mVideoProgressBar.setVisibility(View.GONE);
//        } else {
//            mVideoProgressBar.setVisibility(View.VISIBLE);
//        }
//    }


    @Override
    public void onPlayNextVideo() {
//        if (mVideoInfoPresenter != null && mNextVideoId != 0L) {
//            mViewBindHelper.setChangeVideoInfo(true);
////            mExoMediaHelper.clearStartPosition();
//            mViewBindHelper.clearVideoRatioLang();
//            mVideoInfoPresenter.getVideoInfoNewFromChannel(mNextVideoId, mChannelId);
//        }
        Toast.makeText(this, "暂无下一个影片", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLinkVR() {
        if (modeVideoLinkingDialog == null) {
            modeVideoLinkingDialog = new VrModeVideoLinkingDialog(this);
        }

        if (!modeVideoLinkingDialog.isShowing()) {
            modeVideoLinkingDialog.show();
        }
    }

    @Override
    public void onVideoSetting() {
        if (videoDetailDialog != null && !videoDetailDialog.isShowing()) {
            videoDetailDialog.show();
        }
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

}
