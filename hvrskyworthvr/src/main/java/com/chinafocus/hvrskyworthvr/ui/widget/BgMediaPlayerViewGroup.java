package com.chinafocus.hvrskyworthvr.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.exo.tools.ExoMediaHelper;
import com.chinafocus.hvrskyworthvr.exo.ui.PlayerView;
import com.chinafocus.hvrskyworthvr.util.ColorUtil;
import com.google.android.exoplayer2.Player;

import jp.wasabeef.glide.transformations.CropTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.google.android.exoplayer2.Player.STATE_READY;

public class BgMediaPlayerViewGroup extends FrameLayout implements LifecycleObserver {

    private PlayerView mPlayerView;
    private BackgroundAnimationRelativeLayout mBackgroundAnimationRelativeLayout;
    private AppCompatImageView mCoverBg;

    private CropTransformation mContentBgTransformation;

    private ExoMediaHelper mExoMediaHelper;

    public BgMediaPlayerViewGroup(@NonNull Context context) {
        this(context, null);
    }

    public BgMediaPlayerViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BgMediaPlayerViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.include_rtr_video_media_player_view, this);
        mPlayerView = findViewById(R.id.player_view_surface_land);
        mPlayerView.hideController();
        mBackgroundAnimationRelativeLayout = findViewById(R.id.view_background_change_animation);
        mCoverBg = findViewById(R.id.iv_main_video_cover);

        mContentBgTransformation = new CropTransformation(2560, 1600);

        ((LifecycleOwner) context).getLifecycle().addObserver(this);
    }

    public void postVideoBgAndMenuVideoUrl(String bgUrl, String videoUrl) {
        handleVideoBgAndCover(bgUrl);
        handleMenuVideoUrl(videoUrl);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        Log.d("MyLog", " OnLifecycleEvent >>> onStart");
        mBackgroundAnimationRelativeLayout.setVisibility(VISIBLE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        Log.d("MyLog", " OnLifecycleEvent >>> onPause");
        if (mExoMediaHelper != null) {
            mExoMediaHelper.onStop();
        }
        isFirst = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        Log.d("MyLog", " OnLifecycleEvent >>> onStop");
        if (mExoMediaHelper != null) {
            mExoMediaHelper.onStop();
        }
    }

    private boolean isFirst;

    private void handleMenuVideoUrl(String videoUrl) {
        Log.d("MyLog", " handleMenuVideoUrl >>> " + videoUrl);
        if (mExoMediaHelper == null) {
            mExoMediaHelper = new ExoMediaHelper(getContext(), mPlayerView);
        }

        mExoMediaHelper.onStart();
        mExoMediaHelper.prepareSource(videoUrl);
        mExoMediaHelper.onResume();
        mExoMediaHelper.getPlayer().setPlayWhenReady(false);
        mExoMediaHelper.getPlayer().setRepeatMode(Player.REPEAT_MODE_ALL);
        if (!isFirst) {
            isFirst = true;
            mExoMediaHelper.getPlayer().addListener(new Player.EventListener() {

                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    if (isPlaying) {
                        mBackgroundAnimationRelativeLayout.setVisibility(INVISIBLE);
                    }
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    Log.e("MyLog", " onPlayerStateChanged playWhenReady >>> " + playWhenReady + " playbackState >>> " + playbackState);
                    if (!playWhenReady && playbackState == STATE_READY) {
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mExoMediaHelper.getPlayer() != null) {
                                    mExoMediaHelper.getPlayer().setPlayWhenReady(true);
                                }
                            }
                        }, 3000);
                    }
                }
            });
        }


    }

    private void handleVideoBgAndCover(String bgUrl) {
        mBackgroundAnimationRelativeLayout.setVisibility(VISIBLE);
        Glide.with(this)
                .load(bgUrl)
                .apply(bitmapTransform(mContentBgTransformation))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                int vibrantColor = palette.getVibrantColor(Color.WHITE);

                                int red = Color.red(vibrantColor);
                                int green = Color.green(vibrantColor);
                                int blue = Color.blue(vibrantColor);

                                int cct = ColorUtil.calculateColorTemperature(red, green, blue);
                                Log.e("MyLog", " cct >>> " + cct);
                                if (cct > 5000) {
                                    // 冷色
                                    mCoverBg.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_color_cold_bg, null));
                                } else {
                                    // 暖色
                                    mCoverBg.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_color_warm_bg, null));
                                }
                            }
                        });

                        mBackgroundAnimationRelativeLayout.setForeground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }
}
