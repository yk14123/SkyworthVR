package com.chinafocus.hvrskyworthvr.exo.tools;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.exo.ui.PlayerControlView;
import com.chinafocus.hvrskyworthvr.exo.ui.PlayerView;
import com.chinafocus.hvrskyworthvr.exo.ui.spherical.SphericalGLSurfaceView;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.lang.ref.WeakReference;

import static com.blankj.utilcode.util.NetworkUtils.NetworkType.NETWORK_WIFI;


/**
 * @author yang
 * @date 2020/1/8
 * description：配合ExoPlayer播放器，专做横竖屏切换的各种状态处理
 */
public class OrientationHelper implements PlayerControlView.VisibilityListener, PlaybackPreparer, NetworkUtils.OnNetworkStatusChangedListener {

    private int mPortraitPlayerViewWidth;
    private int mPortraitPlayerViewHeight;

    private final PlayerView mPortraitPlayerView;
    private final PlayerView mLandscapePlayerView;

    private final ViewGroup mRootView;

    private WeakReference<AppCompatActivity> mActivity;
    // 监听横竖屏状态类
    private final ScreenOrientationSwitcher mScreenOrientationSwitcher;
    // 数据绑定类
    private IViewBinder mViewBindHelper;

    private SimpleExoPlayer mPlayer;

    public void setViewBinder(IViewBinder viewBindHelper) {
        mViewBindHelper = viewBindHelper;
    }

    OrientationHelper(PlayerView portraitPlayerView, PlayerView landscapePlayerView, ViewGroup rootView, AppCompatActivity activity) {
        this.mPortraitPlayerView = portraitPlayerView;
        mLandscapePlayerView = landscapePlayerView;
        mRootView = rootView;
        mActivity = new WeakReference<>(activity);

        initChangeOrientationSize();
        // playerView当点击画面后，回调监听，可以show某些View
        mLandscapePlayerView.setControllerVisibilityListener(this);
        mScreenOrientationSwitcher = new ScreenOrientationSwitcher(mActivity.get());

    }

    /**
     * 初始化横竖屏切换PlayerView所需要的宽高尺寸
     */
    private void initChangeOrientationSize() {
        mPortraitPlayerView.post(() -> {
            mPortraitPlayerViewWidth = mPortraitPlayerView.getWidth();
            mPortraitPlayerViewHeight = mPortraitPlayerView.getHeight();
        });
    }

    @SuppressLint("SourceLockedOrientationActivity")
    void onBackPressed() {
        if (mViewBindHelper != null) {
            mViewBindHelper.shouldCloseLandView();
        }
        mActivity.get().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 检测当前Activity横屏？还是竖屏
     *
     * @return true为横屏
     */
    boolean isActivityOrientationLandscape() {
        return Configuration.ORIENTATION_LANDSCAPE == mActivity.get().getResources().getConfiguration().orientation;
    }

    /**
     * 检测当前Activity横屏？还是竖屏
     *
     * @return true为竖屏
     */
    private boolean isActivityOrientationPortrait() {
        return Configuration.ORIENTATION_PORTRAIT == mActivity.get().getResources().getConfiguration().orientation;
    }


    /**
     * 切换到竖屏的时候，切换player，PlayView，PlayControlView
     */
    private void onPlayerWithOrientationPortrait() {
        mPortraitPlayerView.setVisibility(View.VISIBLE);
        if (mLandscapePlayerView.isControllerVisible()) {
            mPortraitPlayerView.showController();
        } else {
            mPortraitPlayerView.hideController();
        }
        PlayerView.switchTargetView(mPlayer, mLandscapePlayerView, mPortraitPlayerView);
        if (mPlayer != null && mPlayer.getCurrentPosition() >= mPlayer.getDuration()) {
            mPlayer.seekTo(mPlayer.getDuration());
        }
        mLandscapePlayerView.setVisibility(View.INVISIBLE);
        // 竖屏
        RelativeLayout.LayoutParams layoutParamsSV = new RelativeLayout.LayoutParams(mPortraitPlayerViewWidth, mPortraitPlayerViewHeight);
        mPortraitPlayerView.setLayoutParams(layoutParamsSV);

        // 竖屏模式由于已经开启了沉浸式状态栏，所以这里需要加padding，防止竖屏模式内容侵入
//        mRootView.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
        // 竖屏模式取消ViewGroup添加View，删除View的Layout动画
        mRootView.setLayoutTransition(null);
    }

    /**
     * 切换到横屏的时候，切换player，PlayView，PlayControlView
     */
    private void onPlayerWithOrientationLandscape() {
        mLandscapePlayerView.setVisibility(View.VISIBLE);
        if (mPortraitPlayerView.isControllerVisible()) {
            mLandscapePlayerView.showController();
        } else {
            mLandscapePlayerView.hideController();
        }
        PlayerView.switchTargetView(mPlayer, mPortraitPlayerView, mLandscapePlayerView);
        if (mPlayer != null && mPlayer.getCurrentPosition() >= mPlayer.getDuration()) {
            mPlayer.seekTo(mPlayer.getDuration());
        }
        mPortraitPlayerView.setVisibility(View.INVISIBLE);
        // 横屏
        RelativeLayout.LayoutParams layoutParamsSV = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mLandscapePlayerView.setLayoutParams(layoutParamsSV);

        // 回复rootViewPadding
        mRootView.setPadding(0, 0, 0, 0);
        // 横屏模式开启ViewGroup添加View，添加View的Layout动画
        mRootView.setLayoutTransition(new LayoutTransition());
    }

    /**
     * 横竖屏切换后，触发Activity的onConfigurationChanged
     *
     * @param newConfig
     */
    void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            onLandscapeStatus();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            onPortraitStatus();
        }
    }

    private SphericalGLSurfaceView.GlStatusHolder mGlStatusHolder;

    /**
     * 横屏模式的所有状态
     */
    private void onLandscapeStatus() {
        updateLandGlStatus();
        onPlayerWithOrientationLandscape();
        startFullScreenModeShortEdges();
        if (mViewBindHelper != null) {
            mViewBindHelper.shouldCloseProView();
        }
    }

    private void updateLandGlStatus() {
        if (mGlStatusHolder == null) {
            mGlStatusHolder = new SphericalGLSurfaceView.GlStatusHolder();
        }
        SphericalGLSurfaceView porGlSurfaceView = (SphericalGLSurfaceView) mPortraitPlayerView.getVideoSurfaceView();
        SphericalGLSurfaceView landGlSurfaceView = (SphericalGLSurfaceView) mLandscapePlayerView.getVideoSurfaceView();
        if (porGlSurfaceView != null) {
            porGlSurfaceView.saveGLStatus(mGlStatusHolder);
        }
        if (landGlSurfaceView != null) {
            landGlSurfaceView.restoreGLStatus(mGlStatusHolder);
        }
    }

    /**
     * 竖屏模式的所有状态
     */
    private void onPortraitStatus() {
        updatePortraitGlStatus();
        onPlayerWithOrientationPortrait();
        cancelFullScreenModeShortEdges();
        if (mViewBindHelper != null) {
            mViewBindHelper.shouldCloseLandView();
        }
    }

    private void updatePortraitGlStatus() {
        if (mGlStatusHolder == null) {
            mGlStatusHolder = new SphericalGLSurfaceView.GlStatusHolder();
        }
        SphericalGLSurfaceView porGlSurfaceView = (SphericalGLSurfaceView) mPortraitPlayerView.getVideoSurfaceView();
        SphericalGLSurfaceView landGlSurfaceView = (SphericalGLSurfaceView) mLandscapePlayerView.getVideoSurfaceView();
        if (landGlSurfaceView != null) {
            landGlSurfaceView.saveGLStatus(mGlStatusHolder);
        }
        if (porGlSurfaceView != null) {
            porGlSurfaceView.restoreGLStatus(mGlStatusHolder);
        }
    }

    /**
     * 切换到横屏模式的时候，必须全面屏展示
     */
    private void startFullScreenModeShortEdges() {
        // 设置全屏
        Window window = mActivity.get().getWindow();
        WindowManager.LayoutParams attrs = window.getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setAttributes(attrs);
        window.addFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        // 设置全面屏适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            attrs.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        window.setAttributes(attrs);

        // 横屏模式状态栏 需要第一时间展示，且状态栏颜色改变为#33000000
        BarUtils.setStatusBarVisibility(window, true);
        // 澳门快讯状态栏改为透明
        BarUtils.setStatusBarColor(window, Color.TRANSPARENT);
    }

    /**
     * 切换到竖屏模式的时候，必须退出全面屏展示
     */
    private void cancelFullScreenModeShortEdges() {
        // 取消全屏
        Window window = mActivity.get().getWindow();
        WindowManager.LayoutParams attrs = window.getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setAttributes(attrs);
        window.clearFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * PlayView绑定ExoPlayer
     *
     * @param player
     */
    void BindPlayer(SimpleExoPlayer player) {
        mPlayer = player;
        if (isActivityOrientationLandscape()) {
            mLandscapePlayerView.setPlayer(player);
            mLandscapePlayerView.setPlaybackPreparer(this);
            onLandscapeStatus();
        } else if (isActivityOrientationPortrait()) {
            // 绑定playerView
            mPortraitPlayerView.setPlayer(player);
            // 设置 player.retry
            mLandscapePlayerView.setPlaybackPreparer(this);
            onPortraitStatus();
        }
    }

    /**
     * controlView可见或者不可见的回调
     * 当横屏模式下，controlView可见的时候，状态栏可见
     * 当横屏模式下，controlView不可见的时候，状态栏不可见
     *
     * @param visibility The new visibility. Either {@link View#VISIBLE} or {@link View#GONE}.
     */
    @Override
    public void onVisibilityChange(int visibility) {
        if (visibility != View.VISIBLE) {
            BarUtils.setStatusBarVisibility(mActivity.get().getWindow(), false);
        } else {
            BarUtils.setStatusBarVisibility(mActivity.get().getWindow(), true);
        }
    }

    /**
     * 注册陀螺仪和网络状态监听
     */
    void onStart() {
        // 初始化Player播放器
        if (mPortraitPlayerView != null) {
            // 注册陀螺仪监听
            mPortraitPlayerView.onResume();
        }
        if (mLandscapePlayerView != null) {
            mLandscapePlayerView.onResume();
        }
        mScreenOrientationSwitcher.enable();
        NetworkUtils.registerNetworkStatusChangedListener(this);
    }

    /**
     * 遥控器触发播放按钮的事件处理
     *
     * @param event
     * @return
     */
    boolean dispatchKeyEvent(KeyEvent event) {
        if (isActivityOrientationLandscape()) {
            return mLandscapePlayerView.dispatchKeyEvent(event);
        } else if (isActivityOrientationPortrait()) {
            return mPortraitPlayerView.dispatchKeyEvent(event);
        }
        return false;
    }

    /**
     * 取消陀螺仪监听和网络监听
     */
    void onStop() {
        if (mPortraitPlayerView != null) {
            mPortraitPlayerView.onPause();
        }
        if (mLandscapePlayerView != null) {
            mLandscapePlayerView.onPause();
        }

        if (mViewBindHelper != null) {
            mViewBindHelper.shouldCloseLandView();
            mViewBindHelper.shouldCloseProView();
        }

        mScreenOrientationSwitcher.disable();
        NetworkUtils.unregisterNetworkStatusChangedListener(this);
    }

    /**
     * 释放mPlayer引用
     */
    void releasePlayer() {
        mPlayer = null;
    }

    /**
     * 销毁Activity的引用
     */
    void onDestroy() {
        mActivity.clear();
        mActivity = null;
    }

    @Override
    public void preparePlayback() {
        mPlayer.retry();
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnected(NetworkUtils.NetworkType networkType) {
        if (networkType == NETWORK_WIFI) {
//            Toast.makeText(mActivity.get(), mActivity.get().getString(R.string.expressreader_video_info_wifi), Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(mActivity.get(), mActivity.get().getString(R.string.expressreader_video_info_unwifi), Toast.LENGTH_SHORT).show();
        }
    }
}
