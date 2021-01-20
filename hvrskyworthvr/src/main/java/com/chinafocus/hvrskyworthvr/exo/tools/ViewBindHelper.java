package com.chinafocus.hvrskyworthvr.exo.tools;

import android.graphics.Color;
import android.util.Log;

import com.chinafocus.hvrskyworthvr.exo.ui.IControlViewInterAction;
import com.chinafocus.hvrskyworthvr.exo.ui.PlayerView;
import com.chinafocus.hvrskyworthvr.exo.ui.spherical.SphericalGLSurfaceView;
import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity;
import com.google.android.exoplayer2.text.CaptionStyleCompat;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static com.chinafocus.hvrskyworthvr.global.Constants.RESULT_CODE_SELF_INACTIVE_DIALOG;
import static com.google.android.exoplayer2.text.CaptionStyleCompat.EDGE_TYPE_NONE;

public class ViewBindHelper {

    private PlayerView mLandscapePlayerView;

    public ViewBindHelper(PlayerView landscapePlayerView) {
        this.mLandscapePlayerView = landscapePlayerView;
    }

    /**
     * 绑定交互动作
     */
    public void bindPlayerView() {
        bindLandPlayerView();
    }

    /**
     * 横屏展示定义交互接口
     */
    private void bindLandPlayerView() {
        assert mLandscapePlayerView.getSubtitleView() != null;
        mLandscapePlayerView.getSubtitleView().setStyle(new CaptionStyleCompat(Color.WHITE, 0, 0, EDGE_TYPE_NONE, 0, null));
        mLandscapePlayerView.getSubtitleView().setFixedTextSize(COMPLEX_UNIT_SP, 30);
//        mLandscapePlayerView.getSubtitleView().setPadding(0, 0, 0, BarUtils.getStatusBarHeight());

        mLandscapePlayerView.setInterActionWithCustomControlView(new IControlViewInterAction() {

            @Override
            public void onGoBackActivity() {
                ((MediaPlayActivity) mLandscapePlayerView.getContext()).setResult(RESULT_CODE_SELF_INACTIVE_DIALOG);
                ((MediaPlayActivity) mLandscapePlayerView.getContext()).finish();
                // 3.立即切换当前Activity为Main
                Constants.ACTIVITY_TAG = Constants.ACTIVITY_MAIN;
            }

            @Override
            public void onVideoContentReset() {
                setGlReset(mLandscapePlayerView);
            }

            @Override
            public void onLinkVR() {
                if (mPlayVideoListener != null) {
                    mPlayVideoListener.onLinkVR();
                }
            }

            @Override
            public void onVideoNextPlay() {
                // 播放下一个
                Log.e("MyLog", "next play");
                if (mPlayVideoListener != null) {
                    mPlayVideoListener.onPlayNextVideo();
                }
            }

            @Override
            public void onVideoSetting() {
                if (mPlayVideoListener != null) {
                    mPlayVideoListener.onVideoSetting();
                }
            }
        });
    }


    public interface PlayVideoListener {
        void onPlayNextVideo();

        void onLinkVR();

        void onVideoSetting();
    }

    private PlayVideoListener mPlayVideoListener;

    public void setPlayVideoListener(PlayVideoListener playVideoListener) {
        mPlayVideoListener = playVideoListener;
    }

    /**
     * 设置全景视频回正
     *
     * @param playerView playerView
     */
    private void setGlReset(PlayerView playerView) {
        SphericalGLSurfaceView glSurfaceView = ((SphericalGLSurfaceView) playerView.getVideoSurfaceView());
        if (glSurfaceView != null) {
            glSurfaceView.resetVR();
        }
    }
}
