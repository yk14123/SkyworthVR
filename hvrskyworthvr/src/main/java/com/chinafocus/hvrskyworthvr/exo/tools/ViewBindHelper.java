package com.chinafocus.hvrskyworthvr.exo.tools;

import android.graphics.Color;
import android.util.Log;

import com.blankj.utilcode.util.BarUtils;
import com.chinafocus.hvrskyworthvr.exo.ui.IControlViewInterAction;
import com.chinafocus.hvrskyworthvr.exo.ui.PlayerView;
import com.chinafocus.hvrskyworthvr.exo.ui.spherical.SphericalGLSurfaceView;
import com.google.android.exoplayer2.text.CaptionStyleCompat;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
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
        mLandscapePlayerView.getSubtitleView().setPadding(0, 0, 0, BarUtils.getStatusBarHeight());

        mLandscapePlayerView.setInterActionWithCustomControlView(new IControlViewInterAction() {

            @Override
            public void onVideoContentReset() {
                setGlReset(mLandscapePlayerView);
            }

            @Override
            public void onLinkVR() {
                if (mPlayNextVideoListener != null) {
                    mPlayNextVideoListener.onLinkVR();
                }
            }

            @Override
            public void onVideoNextPlay() {
                // 播放下一个
                Log.e("MyLog", "next play");
                if (mPlayNextVideoListener != null) {
                    mPlayNextVideoListener.onPlayNextVideo();
                }
            }
        });
    }


    public interface PlayNextVideoListener {
        void onPlayNextVideo();

        void onLinkVR();
    }

    private PlayNextVideoListener mPlayNextVideoListener;

    public void setPlayNextVideoListener(PlayNextVideoListener playNextVideoListener) {
        mPlayNextVideoListener = playNextVideoListener;
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
