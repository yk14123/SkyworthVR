package com.chinafocus.hvrskyworthvr.exo;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.SparseArray;

import com.google.android.exoplayer2.C;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.extractor.mp4.Mp4Extractor;
import com.google.android.exoplayer2.source.BaseMediaSource;
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
import java.util.Locale;

import static com.google.android.exoplayer2.C.SELECTION_FLAG_DEFAULT;


/**
 * @author
 * @date 2020/4/29
 * description：
 */
public class ExoManager {
    private static ExoManager mExoManager;

    private SparseArray<SimpleExoPlayer> mSimpleExoPlayers;

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

    /**
     * 初始化一个播放器
     *
     * @param context
     * @return
     */
    public SimpleExoPlayer init(Context context) {
        SimpleExoPlayer simpleExoPlayer = new SimpleExoPlayer.Builder(context)
//                new DefaultRenderersFactory(context))
                .setLoadControl(new DefaultLoadControl.Builder()
                        .setBufferDurationsMs(DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                                20000,
                                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS)
                        .setPrioritizeTimeOverSizeThresholds(true)
                        .createDefaultLoadControl())
                .build();

        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
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
                    simpleExoPlayer.retry();
                }
            }
        });

        return simpleExoPlayer;
    }

    /**
     * 注册一个管理的List的ExoPlayer
     *
     * @param context
     * @param pos
     * @return
     */
    public SimpleExoPlayer init(Context context, int pos) {
        if (mSimpleExoPlayers == null) {
            mSimpleExoPlayers = new SparseArray<>();
        }
        // 重复的List位置，取出来复用！
        SimpleExoPlayer simpleExoPlayer = mSimpleExoPlayers.get(pos);
        if (simpleExoPlayer == null) {
            SimpleExoPlayer player = init(context);
            mSimpleExoPlayers.append(pos, player);
            return player;
        }
        return simpleExoPlayer;
    }

    /**
     * 当ViewPager滑动的时候，超出的item，需要回收ExoPlayer
     *
     * @param pos
     */
    public void destroyItem(int pos) {
        if (mSimpleExoPlayers != null) {
            SimpleExoPlayer simpleExoPlayer = mSimpleExoPlayers.get(pos);
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setPlayWhenReady(false);
                simpleExoPlayer.stop(true);
                simpleExoPlayer.release();
            }
            mSimpleExoPlayers.delete(pos);
        }
    }

    /**
     * onResume的时候，恢复播放ExoPlayer
     */
    public void onResume() {
        if (mSimpleExoPlayers != null && mSimpleExoPlayers.size() > 0) {
            for (int i = 0; i < mSimpleExoPlayers.size(); i++) {
                SimpleExoPlayer exoPlayer = mSimpleExoPlayers.valueAt(i);
                exoPlayer.setPlayWhenReady(true);
            }
        }
    }

    /**
     * onStop的时候，暂停播放ExoPlayer
     */
    public void onStop() {
        if (mSimpleExoPlayers != null && mSimpleExoPlayers.size() > 0) {
            for (int i = 0; i < mSimpleExoPlayers.size(); i++) {
                SimpleExoPlayer exoPlayer = mSimpleExoPlayers.valueAt(i);
                exoPlayer.setPlayWhenReady(false);
            }
        }
    }

    /**
     * onDestroy的时候，释放全部ExoPlayer
     */
    public void onDestroy() {
        if (mSimpleExoPlayers != null && mSimpleExoPlayers.size() > 0) {
            for (int i = 0; i < mSimpleExoPlayers.size(); i++) {
                SimpleExoPlayer exoPlayer = mSimpleExoPlayers.valueAt(i);
                exoPlayer.setPlayWhenReady(false);
                exoPlayer.stop(true);
                exoPlayer.release();
            }
            mSimpleExoPlayers.clear();
            mSimpleExoPlayers = null;
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

    public BaseMediaSource prepareSource(Context context, String formatType, String fileName, String subtitle) {

        BaseMediaSource LocalMediaSource;
        if (formatType.equals("m3u8")) {
            // 创建资源
            // TODO MediaSource实例不适用于重新使用的情况。 如果您想用相同的media多次准备播放器，请每次使用新的实例。
            LocalMediaSource = new HlsMediaSource.Factory(buildDataSourceFactory(context)).createMediaSource(Uri.parse(fileName));// 播放地址
        } else if (formatType.equals("mp4")) {
            // 创建资源
            LocalMediaSource = new ProgressiveMediaSource.Factory(buildDataSourceFactory(context)).createMediaSource(Uri.parse(fileName));
        } else {
            // 创建资源
            // TODO MediaSource实例不适用于重新使用的情况。 如果您想用相同的media多次准备播放器，请每次使用新的实例。
            LocalMediaSource = new HlsMediaSource.Factory(buildDataSourceFactory(context)).createMediaSource(Uri.parse(fileName));// 播放地址
        }

        if (!TextUtils.isEmpty(subtitle)) {
            // 创建字幕
            Format subtitleFormat =
                    Format.createTextSampleFormat(
                            /* id= */ null,
                            MimeTypes.TEXT_SSA,
                            SELECTION_FLAG_DEFAULT,
                            Locale.getDefault().getLanguage());

            MediaSource subtitleMediaSource =
                    new SingleSampleMediaSource.Factory(buildDataSourceFactory(context))
                            .createMediaSource(Uri.parse(subtitle), subtitleFormat, C.TIME_UNSET);

            return new MergingMediaSource(LocalMediaSource, subtitleMediaSource);
        }

        return LocalMediaSource;
    }

    public BaseMediaSource prepareSourceTest(Context context) {

        Format subtitleFormat =
                Format.createTextSampleFormat(
                        /* id= */ null,
                        MimeTypes.TEXT_SSA,
                        SELECTION_FLAG_DEFAULT,
                        Locale.getDefault().getLanguage());

        MediaSource subtitleMediaSource =
                new SingleSampleMediaSource.Factory(buildDataSourceFactory(context))
                        .createMediaSource(Uri.parse("https://fdfs.expreader5g.net:9500/M00/00/D0/rBFBgl8EGJmAEvluAAAY65-Ftz8670.ass"), subtitleFormat, C.TIME_UNSET);

//        String videoPath = "http://objtree-5g.expreader5g.net/test/1singleframe.mp4";
        String videoPath = "http://bibf-demo.oss-cn-beijing.aliyuncs.com/video/10000000036.mp4";
        String audioLeftPath = "http://objtree-5g.expreader5g.net/test/2leftaudio.mp3";
        String audioRightPath = "http://objtree-5g.expreader5g.net/test/3rightaudio.mp3";
        String videoFullPath = "http://objtree-5g.expreader5g.net/test/4frameleftaudio.mp4";

        ProgressiveMediaSource videoSource = new ProgressiveMediaSource.Factory(buildDataSourceFactory(context), Mp4Extractor.FACTORY).createMediaSource(Uri.parse(videoPath));
        ProgressiveMediaSource audioRightSource = new ProgressiveMediaSource.Factory(buildDataSourceFactory(context), Mp3Extractor.FACTORY).createMediaSource(Uri.parse(audioRightPath));
        ProgressiveMediaSource audioLeftSource = new ProgressiveMediaSource.Factory(buildDataSourceFactory(context), Mp3Extractor.FACTORY).createMediaSource(Uri.parse(audioLeftPath));

        // 多个音频和视频合成：第一个音频有效，后续无效。必须先音频，再视频
        MergingMediaSource mergingMediaSource = new MergingMediaSource(audioLeftSource, videoSource, subtitleMediaSource);

        return mergingMediaSource;
//        return videoSource;
    }

    public BaseMediaSource prepareSourceTestChange(Context context) {

        Format subtitleFormat =
                Format.createTextSampleFormat(
                        /* id= */ null,
                        MimeTypes.TEXT_SSA,
                        SELECTION_FLAG_DEFAULT,
                        Locale.getDefault().getLanguage());

        MediaSource subtitleMediaSource =
                new SingleSampleMediaSource.Factory(buildDataSourceFactory(context))
                        .createMediaSource(Uri.parse("https://fdfs.expreader5g.net:9500/M00/00/D0/rBFBgl8EGJmAEvluAAAY65-Ftz8670.ass"), subtitleFormat, C.TIME_UNSET);

//        String videoPath = "http://objtree-5g.expreader5g.net/test/一、单画面.mp4";
        String videoPath = "https://v360.oss-cn-beijing.aliyuncs.com/video/v360/cn/test_001/bszg.m3u8";
        String audioLeftPath = "http://objtree-5g.expreader5g.net/test/2leftaudio.mp3";
        String audioRightPath = "http://objtree-5g.expreader5g.net/test/3rightaudio.mp3";
        String videoFullPath = "http://objtree-5g.expreader5g.net/test/4frameleftaudio.mp4";

        HlsMediaSource videoSource = new HlsMediaSource.Factory(buildDataSourceFactory(context)).createMediaSource(Uri.parse(videoPath));
        ProgressiveMediaSource audioRightSource = new ProgressiveMediaSource.Factory(buildDataSourceFactory(context), Mp3Extractor.FACTORY).createMediaSource(Uri.parse(audioRightPath));
        ProgressiveMediaSource audioLeftSource = new ProgressiveMediaSource.Factory(buildDataSourceFactory(context), Mp3Extractor.FACTORY).createMediaSource(Uri.parse(audioLeftPath));

        // 多个音频和视频合成：第一个音频有效，后续无效。必须先音频，再视频
        MergingMediaSource mergingMediaSource = new MergingMediaSource(audioRightSource, videoSource, subtitleMediaSource);

        return mergingMediaSource;
    }

}
