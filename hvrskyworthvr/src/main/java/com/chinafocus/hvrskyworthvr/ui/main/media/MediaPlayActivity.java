package com.chinafocus.hvrskyworthvr.ui.main.media;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.exo.tools.ExoMediaHelper;
import com.chinafocus.hvrskyworthvr.exo.tools.ViewBindHelper;
import com.chinafocus.hvrskyworthvr.exo.ui.PlayerView;
import com.chinafocus.hvrskyworthvr.exo.ui.spherical.SphericalGLSurfaceView;
import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDetail;
import com.chinafocus.hvrskyworthvr.util.statusbar.StatusBarCompatFactory;

import java.util.List;
import java.util.stream.Collectors;

public class MediaPlayActivity extends AppCompatActivity {

    public static final String MEDIA_ID = "media_id";
    public static final String MEDIA_CATEGORY_TAG = "media_category_tag";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBarCompatFactory.getInstance().setStatusBarImmerse(this, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_play);

        Intent intent = getIntent();
        int video_id = intent.getIntExtra(MEDIA_ID, -1);
        String video_tag = intent.getStringExtra(MEDIA_CATEGORY_TAG);

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
            }

        });

    }

    private PlayerView mLandPlayerView;
    private ExoMediaHelper mExoMediaHelper;

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
    public void onStart() {
        // 假如未登录状态，点击收藏后，返回的时候，需要刷新结果
        super.onStart();
//        mVideoInfoPresenter.getVideoInfoNewFromChannel(mVideoId, mChannelId);
//        mPanoramaMediaHelper.onStart();
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


    //    @Override
    public void onPlayNextVideo() {
//        if (mVideoInfoPresenter != null && mNextVideoId != 0L) {
//            mViewBindHelper.setChangeVideoInfo(true);
////            mExoMediaHelper.clearStartPosition();
//            mViewBindHelper.clearVideoRatioLang();
//            mVideoInfoPresenter.getVideoInfoNewFromChannel(mNextVideoId, mChannelId);
//        }
    }

}


//        mVideoProgressBar = findViewById(R.id.pb_time_bar);
//        mVideoBack = findViewById(R.id.ib_exo_back);
//        mVideoBack.setOnClickListener(v -> finish());
//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVideoBack.getLayoutParams();
//        layoutParams.setMarginStart(getResources().getDimensionPixelSize(R.dimen.dp_5));
//        layoutParams.topMargin = BarUtils.getStatusBarHeight();
//        mVideoBack.setLayoutParams(layoutParams);