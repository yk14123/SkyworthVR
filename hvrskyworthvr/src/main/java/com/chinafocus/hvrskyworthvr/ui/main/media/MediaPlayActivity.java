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
import com.chinafocus.hvrskyworthvr.service.SocketService;
import com.chinafocus.hvrskyworthvr.service.event.VrMainStickyActiveDialog;
import com.chinafocus.hvrskyworthvr.service.event.VrMainStickyInactiveDialog;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaDisConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaSyncMediaInfo;
import com.chinafocus.hvrskyworthvr.service.event.VrMediaWaitSelected;
import com.chinafocus.hvrskyworthvr.service.event.VrRotation;
import com.chinafocus.hvrskyworthvr.service.event.VrSyncPlayInfo;
import com.chinafocus.hvrskyworthvr.ui.dialog.VideoDetailDialog;
import com.chinafocus.hvrskyworthvr.ui.dialog.VrModeVideoLinkingDialog;
import com.chinafocus.hvrskyworthvr.util.statusbar.StatusBarCompatFactory;
import com.google.android.exoplayer2.Player;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.stream.Collectors;

public class MediaPlayActivity extends AppCompatActivity implements ViewBindHelper.PlayVideoListener {

    public static final String MEDIA_ID = "media_id";
    public static final String MEDIA_FROM_TAG = "media_from_tag";
    public static final String MEDIA_SEEK = "media_seek";
    public static final String MEDIA_CATEGORY_TAG = "media_category_tag";
    public static final String MEDIA_LINK_VR = "media_link_vr";

    private int video_tag;
    private int category;
    private int video_id;
    private long seek;
    private boolean linkingVr;

    private VrModeVideoLinkingDialog modeVideoLinkingDialog;
    private VideoDetailDialog videoDetailDialog;
    private PlayerView mLandPlayerView;
    private ExoMediaHelper mExoMediaHelper;
    private MediaViewModel mediaViewModel;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBarCompatFactory.getInstance().setStatusBarImmerse(this, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_play);

        handleIntent();
        initView(savedInstanceState);

        mediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);

        String tag = "";
        if (video_tag == 1) {
            tag = "publish";
        } else if (video_tag == 2) {
            tag = "video";
        }

        mediaViewModel.getVideoDetailData(tag, video_id);
        mediaViewModel.videoDetailMutableLiveData.observe(this, videoDetail -> {
            Log.e("MyLog", " videoDetail >>> " + videoDetail.getTitle());

            List<VideoDetail.FilesBean> filesBeanList = null;

            if (video_tag == 1) {
                filesBeanList =
                        videoDetail
                                .getFiles()
                                .stream()
                                .filter(filesBean -> (filesBean.getType() == 10 && filesBean.getBitrate() == 8000) || (filesBean.getType() == 6))
                                .collect(Collectors.toList());
            } else if (video_tag == 2) {
                filesBeanList =
                        videoDetail
                                .getFiles()
                                .stream()
                                .filter(filesBean -> (filesBean.getType() == 1 && filesBean.getBitrate() == 8000) || (filesBean.getType() == 6))
                                .collect(Collectors.toList());
            }

            String format = "";
            String videoUrl = "";
            String subtitle = "";
            for (VideoDetail.FilesBean filesBean : filesBeanList) {
                if (filesBean.getType() == 1 || filesBean.getType() == 10) {
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

            Log.e("MyLog", " 当前视频播放地址是 videoUrl >>> " + videoUrl);

            if (!TextUtils.isEmpty(videoUrl)) {
                mExoMediaHelper.onStart();
                mExoMediaHelper.prepareSource(format, videoUrl, null, subtitle);
                mExoMediaHelper.onResume();
                mExoMediaHelper.seekTo(seek);

                mExoMediaHelper.getPlayer().addListener(new Player.EventListener() {
                    @Override
                    public void onPlaybackStateChanged(int state) {
                        if (linkingVr && state == 4) {
                            // 3. Pad 位于播放结束界面时，如果此时 VR 被激活则 VR 端直接进入一级视频列表界面，Pad 回到视频列表界面的「不可选片状态」
                            // 不用接受命令。
                            // 当链接状态，播放结束后
                            closeAllDialog();
                            VrSyncPlayInfo.obtain().seek = 0L;
                            EventBus.getDefault().postSticky(VrMainStickyActiveDialog.obtain());
                            finish();
                        }
                    }
                });

                if (linkingVr) {
                    mExoMediaHelper.getPlayer().setVolume(0f);
                    mLandPlayerView.showController();
                    ((SphericalGLSurfaceView) mLandPlayerView.getVideoSurfaceView()).syncTouchVR();
                }

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

        video_tag = intent.getIntExtra(MEDIA_FROM_TAG, -1);
        category = intent.getIntExtra(MEDIA_CATEGORY_TAG, -1);
        video_id = intent.getIntExtra(MEDIA_ID, -1);
        seek = intent.getLongExtra(MEDIA_SEEK, 0);
        linkingVr = intent.getBooleanExtra(MEDIA_LINK_VR, false);

        Log.e("MyLog", " MediaPlayActivity handleIntent >>> video_tag : " + video_tag + " >>> video_id : " + video_id);

        VrSyncPlayInfo.obtain().tag = video_tag;
        VrSyncPlayInfo.obtain().category = category;
        VrSyncPlayInfo.obtain().videoId = video_id;
        VrSyncPlayInfo.obtain().seek = seek;
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

        mLandPlayerView.syncSkyWorthMediaStatus(linkingVr);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.ACTIVITY_TAG = Constants.ACTIVITY_MEDIA;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toUnityMediaInfoAndActiveVRPlayerStatus(VrMediaConnect event) {

        closeAllDialog();

        // 2.切换不可操作播放状态
        mLandPlayerView.showController();
        mLandPlayerView.syncSkyWorthMediaStatus(true);
        ((SphericalGLSurfaceView) mLandPlayerView.getVideoSurfaceView()).syncTouchVR();

        linkingVr = true;

        // TODO 1.给VR同步视频信息
        Intent intent = new Intent(this, SocketService.class);
        intent.putExtra(MEDIA_FROM_TAG, VrSyncPlayInfo.obtain().tag);
        intent.putExtra(MEDIA_CATEGORY_TAG, VrSyncPlayInfo.obtain().category);
        intent.putExtra(MEDIA_ID, VrSyncPlayInfo.obtain().videoId);
        long currentPosition = mExoMediaHelper.getPlayer().getCurrentPosition();
        VrSyncPlayInfo.obtain().seek = currentPosition;
        intent.putExtra(MEDIA_SEEK, currentPosition);
        startService(intent);

        mExoMediaHelper.getPlayer().setVolume(0f);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void goActivityAndActiveDialog(VrMediaWaitSelected vrMediaWaitSelected) {
        closeAllDialog();
        VrSyncPlayInfo.obtain().seek = 0L;
        EventBus.getDefault().postSticky(VrMainStickyActiveDialog.obtain());
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void goBackMainActivityAndInactiveMainDialog(VrMediaDisConnect event) {
        closeAllDialog();
        Log.e("MyLog", "MediaPlay VrMediaDisConnect");
        linkingVr = false;
        VrSyncPlayInfo.obtain().seek = mExoMediaHelper.getPlayer().getCurrentPosition();
        EventBus.getDefault().postSticky(VrMainStickyInactiveDialog.obtain());
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadNextSyncMedia(VrMediaSyncMediaInfo vrMediaSyncMediaInfo) {
        closeAllDialog();
        String tag = "";
        if (VrSyncPlayInfo.obtain().tag == 1) {
            tag = "publish";
        } else if (VrSyncPlayInfo.obtain().tag == 2) {
            tag = "video";
        }

        Log.e("MyLog", " loadNextSyncMedia getVideoDetailData "
                + " >>> tag : " + tag
                + " >>> video_id : " + VrSyncPlayInfo.obtain().videoId
                + " >>> linkingVr : " + linkingVr);

        video_tag = VrSyncPlayInfo.obtain().tag;

        mediaViewModel.getVideoDetailData(tag, VrSyncPlayInfo.obtain().videoId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void syncRotation(VrRotation vrRotation) {
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
        if (modeVideoLinkingDialog != null && modeVideoLinkingDialog.isShowing()) {
            modeVideoLinkingDialog.dismiss();
        }
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
