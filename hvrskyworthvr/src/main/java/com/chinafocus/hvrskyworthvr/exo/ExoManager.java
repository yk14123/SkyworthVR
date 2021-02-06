package com.chinafocus.hvrskyworthvr.exo;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
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
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSinkFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExoManager {
    private static ExoManager mExoManager;
    private SimpleExoPlayer mSimpleExoPlayer;

    private ExoManager() {
    }

    public static ExoManager getInstance() {
        if (mExoManager == null) {
            synchronized (ExoManager.class) {
                if (mExoManager == null) {
                    mExoManager = new ExoManager();
                }
            }
        }
        return mExoManager;
    }

    public void setTextureView(TextureView textureView) {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.setVideoTextureView(textureView);
        }
    }

    /**
     * 初始化一个播放器
     */
    public void init(Context context, Callback callback) {
        if (mSimpleExoPlayer != null) {
            return;
        }

        mSimpleExoPlayer = new SimpleExoPlayer.Builder(context)
                .setLoadControl(new DefaultLoadControl.Builder()
                        .setBufferDurationsMs(DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                                DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS)
                        .setPrioritizeTimeOverSizeThresholds(true)
                        .createDefaultLoadControl())
                .build();


        // 预览视频静音
        mSimpleExoPlayer.setVolume(0f);
        this.mCallback = callback;

        mSimpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
        mSimpleExoPlayer.addListener(new Player.EventListener() {

            @SuppressWarnings("all")
            @Override
            public void onPlayerError(@NonNull ExoPlaybackException error) {
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
//                        simpleExoPlayer.retry();
                    }
                    mSimpleExoPlayer.retry();
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (mCallback != null) {
                    mCallback.onIsPlayingChanged(isPlaying);
                }
            }

        });
    }

    /**
     * 初始化一个播放器
     */
    public void init(Context context, SurfaceView surfaceView, Callback callback) {
        if (mSimpleExoPlayer != null) {
            return;
        }

        mSimpleExoPlayer = new SimpleExoPlayer.Builder(context)
                .setLoadControl(new DefaultLoadControl.Builder()
                        .setBufferDurationsMs(DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                                DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS)
                        .setPrioritizeTimeOverSizeThresholds(true)
                        .createDefaultLoadControl())
                .build();

        mSimpleExoPlayer.setVideoSurfaceView(surfaceView);

        // 预览视频静音
        mSimpleExoPlayer.setVolume(0f);
        this.mCallback = callback;

        mSimpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
        mSimpleExoPlayer.addListener(new Player.EventListener() {

            @SuppressWarnings("all")
            @Override
            public void onPlayerError(@NonNull ExoPlaybackException error) {
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
//                        simpleExoPlayer.retry();
                    }
                    mSimpleExoPlayer.retry();
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (mCallback != null) {
                    mCallback.onIsPlayingChanged(isPlaying);
                }
            }

        });
    }

    private Callback mCallback;

    public interface Callback {
        void onIsPlayingChanged(boolean isPlaying);
    }

    /**
     * 释放全部ExoPlayer
     */
    @SuppressWarnings("unused")
    public void onDestroy() {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.setPlayWhenReady(false);
            mSimpleExoPlayer.stop(true);
            mSimpleExoPlayer.release();
        }
    }

    public void setPlayOrPause(boolean b) {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.setPlayWhenReady(b);
        }
    }

    private DataSource.Factory buildDataSourceFactory(Context context) {
        // 创建带宽
        DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter.Builder(context).build();
        // UA
        String userAgent = Util.getUserAgent(context, "AppNameYang");

        // 创建加载数据的工厂
        DefaultDataSourceFactory upstreamFactory = new DefaultDataSourceFactory(context, BANDWIDTH_METER,
                new DefaultHttpDataSourceFactory(userAgent, BANDWIDTH_METER));

        // 创建SimpleCache
        SimpleCache simpleCache = VideoCache.getInstance(context);

        //把缓存对象cache和负责缓存数据读取、写入的工厂类CacheDataSinkFactory 相关联
        CacheDataSinkFactory cacheDataSinkFactory = new CacheDataSinkFactory(simpleCache, Long.MAX_VALUE);

        return new CacheDataSourceFactory(
                simpleCache,
                upstreamFactory,
                new FileDataSource.Factory(),
                cacheDataSinkFactory,
                CacheDataSource.FLAG_BLOCK_ON_CACHE,
                null);
    }

    public void prepareSource(Context context, String videoUrl) {
        prepareSource(context, videoUrl, null, null);
    }

    @SuppressWarnings("unused")
    public void prepareSource(Context context, String videoUrl, String subtitle) {
        prepareSource(context, videoUrl, null, subtitle);
    }

    /**
     * 设置播放地址来源 String fileName = "https://v360.oss-cn-beijing.aliyuncs.com/video/v360/cn/test_001/bszg.m3u8";
     */
    public void prepareSource(Context context, String videoUrl, String audioUrl, String subTitle) {

        List<MediaSource> mediaSources = new ArrayList<>();

        MediaSource audioSource = null;
        if (!TextUtils.isEmpty(audioUrl)) {
            audioSource =
                    new ProgressiveMediaSource.Factory(buildDataSourceFactory(context), Mp3Extractor.FACTORY)
                            .createMediaSource(Uri.parse(audioUrl));
        }
        if (audioSource != null) {
            mediaSources.add(audioSource);
        }

        MediaSource videoSource = null;

        if (videoUrl.toLowerCase().endsWith("m3u8")) {
            // 创建资源
            // TODO MediaSource实例不适用于重新使用的情况。 如果您想用相同的media多次准备播放器，请每次使用新的实例。
//            MediaItem build = new MediaItem.Builder()
//                    .setUri(Uri.parse(videoUrl)).setMimeType(MimeTypes.APPLICATION_M3U8)
//                    .build();
            videoSource =
                    new HlsMediaSource.Factory(buildDataSourceFactory(context))
                            .createMediaSource(Uri.parse(videoUrl));
        } else if (videoUrl.toLowerCase().endsWith("mp4")) {
//            MediaItem build = new MediaItem.Builder()
//                    .setUri()
//                    .build();
            videoSource =
                    new ProgressiveMediaSource.Factory(buildDataSourceFactory(context))
                            .createMediaSource(Uri.parse(videoUrl));
        }

        if (videoSource != null) {
            mediaSources.add(videoSource);
        }

        MediaSource subtitleSource = null;

        if (!TextUtils.isEmpty(subTitle)) {

            Log.d("MyLog", "视频字幕地址是 >>>" + subTitle);
//            MediaItem.Subtitle subtitle =
//                    new MediaItem.Subtitle(Uri.parse(subTitle), MimeTypes.TEXT_SSA, null, C.SELECTION_FLAG_DEFAULT);
//
//            subtitleSource =
//                    new SingleSampleMediaSource.Factory(mMediaDataSourceFactory)
//                            .createMediaSource(subtitle, C.TIME_UNSET);

            // 创建字幕
            Format subtitleFormat =
                    Format.createTextSampleFormat(
                            /* id= */ null,
                            MimeTypes.TEXT_SSA,
                            C.SELECTION_FLAG_DEFAULT,
                            Locale.getDefault().getLanguage());

            subtitleSource =
                    new SingleSampleMediaSource.Factory(buildDataSourceFactory(context))
                            .createMediaSource(Uri.parse(subTitle), subtitleFormat, C.TIME_UNSET);

        }

        if (subtitleSource != null) {
            mediaSources.add(subtitleSource);
        }

        MediaSource mediaSource = null;

        if (mediaSources.size() > 1) {
            mediaSource = new MergingMediaSource(mediaSources.toArray(new MediaSource[0]));
        } else if (mediaSources.size() == 1) {
            mediaSource = videoSource;
        }

        if (mediaSource != null) {
            mSimpleExoPlayer.prepare(mediaSource, true, true);
        }

    }


}
