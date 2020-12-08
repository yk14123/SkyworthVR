package com.chinafocus.hvrskyworthvr.exo.tools;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.chinafocus.hvrskyworthvr.exo.VideoCache;
import com.chinafocus.hvrskyworthvr.exo.ui.PlayerView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.extractor.mp4.Mp4Extractor;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExoMediaHelper {

    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";

    private Context mContext;
    // 数据源
    private MediaSource mediaSource;
    // 播放器
    private SimpleExoPlayer player;
    // 加载数据的工厂
    private DataSource.Factory mMediaDataSourceFactory;
    //记录现在的播放位置
    private long startPosition;
    private int startWindow;
    private boolean startAutoPlay;
    private PlayerView mLandscapePlayerView;

    public ExoMediaHelper(AppCompatActivity activity, PlayerView playerView) {
        mContext = activity.getApplicationContext();
        // 初始化加载数据工厂
        mMediaDataSourceFactory = buildDataSourceFactory(mContext);
        mLandscapePlayerView = playerView;
    }

    /**
     * 设置播放的位置
     * 在onResume方法之后调用即可！
     *
     * @param time
     */
    public void seekTo(int time) {
        if (player != null) {
            player.seekTo(time * 1000);
        }
    }

    public void setPlayWhenReady(boolean play) {
        startAutoPlay = play;
    }

    /**
     * 初始化播放器
     */
    private void initializePlayer() {
        if (player == null) {
            player = new SimpleExoPlayer.Builder(mContext)
                    .setLoadControl(new DefaultLoadControl.Builder()
                            .setPrioritizeTimeOverSizeThresholds(false)
                            .build())
                    .build();
            // 监听
            player.addListener(new PlayerEventListener());
            // 设置播放
            player.setPlayWhenReady(startAutoPlay);
            player.setRepeatMode(Player.REPEAT_MODE_OFF);
        }
    }

    /**
     * 恢复播放位置
     */
    private void restorePlayerIfCouldPlay() {
        boolean haveStartPosition = startWindow != C.INDEX_UNSET;
        if (haveStartPosition) {
            // 如果有seek点，则跳到指定位置
            player.seekTo(startWindow, startPosition);
        }
        /**
         *  mediaSource 数据源
         *  resetPosition 是否重置播放位置
         *  resetState 是否重置状态
         */
        if (mediaSource != null) {
//            player.prepare(mediaSource, !haveStartPosition, false);
            player.setMediaSource(mediaSource, !haveStartPosition);
            player.prepare();
        }
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    /**
     * onStart方法
     * 1. 初始化播放器
     * 2. PlayView绑定播放器
     * 3. 恢复播放位置
     * 4. 注册陀螺仪及网络变化监听
     */
    public void onStart() {
        // 初始化Player播放器
        initializePlayer();
        mLandscapePlayerView.setPlayer(player);
    }

    public void onResume() {
        restorePlayerIfCouldPlay();
        if (mLandscapePlayerView != null) {
            // 注册陀螺仪监听
            mLandscapePlayerView.onResume();
        }
    }


    /**
     * onNewIntent
     * 当配合singleTop使用，当singleTop启用的时候（PlayerActivity跳转PlayerActivity），
     * onPause -> onNewIntent -> onResume
     */
    public void onNewIntent() {
        releasePlayer(); // 释放player资源
        clearStartPosition(); // 还原位置标记
    }

    /**
     * onStop方法
     * 1. mExoPlayerOrientationHelper中的播放器 == null
     * 2. 释放PlayView播放器
     * 3. 存储播放位置
     * 4. 取消注册陀螺仪及网络变化监听
     */
    public void onStop() {
        releasePlayer();
        if (mLandscapePlayerView != null) {
            mLandscapePlayerView.onPause();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            // 暂停的时候，获取当前播放的位置
            updateStartPosition();
            /**
             * 释放播放器，释放surface，释放mediaSource
             * 移除mediaSource的监听，surface，surfaceHolder的监听，带宽控制监听
             * 移除各种handle的监听
             * 移除屏幕亮度保持
             */
            player.release();
            player = null;
//            mediaSource = null;
        }
    }


    // TODO onDestroy生命周期
    public void onDestroy() {
        if (player != null) {
            player.release();
            player = null;
        }
        if (mContext != null) {
            mContext = null;
        }
        if (mediaSource != null) {
            mediaSource = null;
        }
    }

    /**
     * 创建默认的数据工厂
     *
     * @param context 上下文
     * @return 数据工厂
     */
    private DataSource.Factory buildDataSourceFactory(Context context) {
        // 创建带宽
        DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter.Builder(context).build();
        // UA
        String userAgent = Util.getUserAgent(context, "AppNameYang");

        // TODO 直播的话 不能使用SimpleCache！
        // 创建加载数据的工厂
//        return new DefaultDataSourceFactory(context, BANDWIDTH_METER,
//                new DefaultHttpDataSourceFactory(userAgent, BANDWIDTH_METER));

        // 创建加载数据的工厂
        DefaultDataSourceFactory upstreamFactory = new DefaultDataSourceFactory(context, BANDWIDTH_METER,
                new DefaultHttpDataSourceFactory());

        // 创建SimpleCache
        SimpleCache simpleCache = VideoCache.getInstance(context);

        //把缓存对象cache和负责缓存数据读取、写入的工厂类CacheDataSinkFactory 相关联
        CacheDataSink.Factory factory = new CacheDataSink.Factory();
        factory.setCache(simpleCache);

        CacheDataSource.Factory factoryDataSource = new CacheDataSource.Factory();
        factoryDataSource.setCache(simpleCache);
        factoryDataSource.setUpstreamDataSourceFactory(upstreamFactory);
        factoryDataSource.setCacheWriteDataSinkFactory(factory);
        factoryDataSource.setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE);

        return factoryDataSource;
    }

    // TODO setPlayWhenReady可用于开始和暂停播放
    // TODO 各种seekTo方法可用于在媒体内搜索
    // TODO setRepeatMode可用于控制媒体是否以及如何循环播放
    // TODO 并且setPlaybackParameters可用于调整播放速度和音调。

    /**
     * 设置播放地址来源 String fileName = "https://v360.oss-cn-beijing.aliyuncs.com/video/v360/cn/test_001/bszg.m3u8";
     */
    public void prepareSource(String videoFormat, String videoUrl, String audioUrl, String subTitle) {

        List<MediaSource> mediaSources = new ArrayList<>();

        MediaSource audioSource = null;
        if (!TextUtils.isEmpty(audioUrl)) {
            MediaItem build = new MediaItem.Builder().setUri(Uri.parse(audioUrl)).build();
            audioSource =
                    new ProgressiveMediaSource.Factory(mMediaDataSourceFactory, Mp3Extractor.FACTORY)
                            .createMediaSource(build);
        }
        if (audioSource != null) {
            mediaSources.add(audioSource);
        }

        MediaSource videoSource = null;

        if (videoFormat.equalsIgnoreCase("m3u8")) {
            // 创建资源
            // TODO MediaSource实例不适用于重新使用的情况。 如果您想用相同的media多次准备播放器，请每次使用新的实例。
            MediaItem build = new MediaItem.Builder()
                    .setUri(Uri.parse(videoUrl)).setMimeType(MimeTypes.APPLICATION_M3U8)
                    .build();
            videoSource =
                    new HlsMediaSource.Factory(mMediaDataSourceFactory)
                            .createMediaSource(build);
        } else if (videoFormat.equalsIgnoreCase("mp4")) {
            MediaItem build = new MediaItem.Builder()
                    .setUri(Uri.parse(videoUrl))
                    .build();
            videoSource =
                    new ProgressiveMediaSource.Factory(mMediaDataSourceFactory, Mp4Extractor.FACTORY)
                            .createMediaSource(build);
        }

        if (videoSource != null) {
            mediaSources.add(videoSource);
        }

        MediaSource subtitleSource = null;

        if (!TextUtils.isEmpty(subTitle)) {

            MediaItem.Subtitle subtitle =
                    new MediaItem.Subtitle(Uri.parse(subTitle), MimeTypes.TEXT_SSA, null, C.SELECTION_FLAG_DEFAULT);

            subtitleSource =
                    new SingleSampleMediaSource.Factory(mMediaDataSourceFactory)
                            .createMediaSource(subtitle, C.TIME_UNSET);
        }

        if (subtitleSource != null) {
            mediaSources.add(subtitleSource);
        }

        if (mediaSources.size() > 1) {
            mediaSource = new MergingMediaSource(mediaSources.toArray(new MediaSource[0]));
        } else if (mediaSources.size() == 1) {
            mediaSource = videoSource;
        } else {
            mediaSource = null;
        }
    }

    /**
     * 清空播放记录点
     */
    public void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }

    public void initStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
    }

    /**
     * onCreate中，恢复播放视频
     *
     * @param savedInstanceState
     */
    public void restoreSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // 是否自动播放
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
            // 获取保存的window和pos位置
            startWindow = savedInstanceState.getInt(KEY_WINDOW);
            startPosition = savedInstanceState.getLong(KEY_POSITION);
        } else {
            initStartPosition();
        }
    }

    /**
     * 更新播放位置
     */
    public void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
            startWindow = player.getCurrentWindowIndex();

            long max = Math.max(0, player.getContentPosition());
            if (max == 0 && startPosition > 0) {
                return;
            }
            startPosition = max;
        }
    }

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
    }

    public int getStartPosition() {
        long max = Math.max(0, player.getContentPosition());
        return (int) (max / 1000);
    }

    /**
     * Activity意外关闭的时候，保存状态
     *
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState) {
        updateStartPosition();
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
        outState.putInt(KEY_WINDOW, startWindow);
        outState.putLong(KEY_POSITION, startPosition);
    }

    public class PlayerEventListener implements Player.EventListener {

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.e("MyLog", "ExoPlaybackException >>> " + error.getMessage());
            if (isBehindLiveWindow(error)) {
                clearStartPosition();
                // 遇到BehindLiveWindowException这个错误，重头播放！
                // 在播放直播地址的时候，横竖屏切换，偶尔回报这个错误！
                // 是从开始播放，但是状态不重置
                initializePlayer();
                player.setMediaSource(mediaSource, true);
                player.prepare();
            } else {
                // 其他错误，弹出控制页面
            }

            if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                IOException cause = error.getSourceException();
                if (cause instanceof HttpDataSource.HttpDataSourceException) {
                    // An HTTP error occurred.
                    HttpDataSource.HttpDataSourceException httpError = (HttpDataSource.HttpDataSourceException) cause;
                    // This is the request for which the error occurred.
                    DataSpec requestDataSpec = httpError.dataSpec;
                    // It's possible to find out more about the error both by casting and by
                    // querying the cause.
                    if (httpError instanceof HttpDataSource.InvalidResponseCodeException) {
                        // Cast to InvalidResponseCodeException and retrieve the response code,
                        // message and headers.
                    } else {
                        // Try calling httpError.getCause() to retrieve the underlying cause,
                        // although note that it may be null.
                    }
                    // 当前是网络错误，就一直无限轮询请求
                    player.prepare();
                }
            }

        }

    }

    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

}