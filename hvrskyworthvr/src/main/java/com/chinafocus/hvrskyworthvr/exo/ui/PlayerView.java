/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain NetWorkUtil copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chinafocus.hvrskyworthvr.exo.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.exo.ui.spherical.SphericalGLSurfaceView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.flac.PictureFrame;
import com.google.android.exoplayer2.metadata.id3.ApicFrame;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.ui.spherical.SingleTapListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.RepeatModeUtil;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoDecoderGLSurfaceView;
import com.google.android.exoplayer2.video.VideoListener;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.exoplayer2.Player.STATE_ENDED;

/**
 * A high level view for {@link Player} media playbacks. It displays video, subtitles and album art
 * during playback, and displays playback controls using NetWorkUtil {@link PlayerControlView}.
 *
 * <p>A PlayerView can be customized by setting attributes (or calling corresponding methods),
 * overriding the view's layout file or by specifying NetWorkUtil custom view layout file, as outlined below.
 *
 * <h3>Attributes</h3>
 * <p>
 * The following attributes can be set on NetWorkUtil PlayerView when used in NetWorkUtil layout XML file:
 *
 * <ul>
 * <li><b>{@code use_artwork}</b> - Whether artwork is used if available in audio streams.
 * <ul>
 * <li>Corresponding method: {@link #setUseArtwork(boolean)}
 * <li>Default: {@code true}
 * </ul>
 * <li><b>{@code default_artwork}</b> - Default artwork to use if no artwork available in audio
 * streams.
 * <ul>
 * <li>Corresponding method: {@link #setDefaultArtwork(Drawable)}
 * <li>Default: {@code null}
 * </ul>
 * <li><b>{@code use_controller}</b> - Whether the playback controls can be shown.
 * <ul>
 * <li>Corresponding method: {@link #setUseController(boolean)}
 * <li>Default: {@code true}
 * </ul>
 * <li><b>{@code hide_on_touch}</b> - Whether the playback controls are hidden by touch events.
 * <ul>
 * <li>Corresponding method: {@link #setControllerHideOnTouch(boolean)}
 * <li>Default: {@code true}
 * </ul>
 * <li><b>{@code auto_show}</b> - Whether the playback controls are automatically shown when
 * playback starts, pauses, ends, or fails. If set to false, the playback controls can be manually
 * operated with {@link #showController()} and {@link #hideController()}.
 * <ul>
 * <li>Corresponding method: {@link #setControllerAutoShow(boolean)}
 * <li>Default: {@code true}
 * </ul>
 * <li><b>{@code hide_during_ads}</b> - Whether the playback controls are hidden during ads.
 * Controls are always shown during ads if they are enabled and the player is paused.
 * <ul>
 * <li>Corresponding method: {@link #setControllerHideDuringAds(boolean)}
 * <li>Default: {@code true}
 * </ul>
 * <li><b>{@code show_buffering}</b> - Whether the buffering spinner is displayed when the player
 * is buffering. Valid values are {@code never}, {@code when_playing} and {@code always}.
 * <ul>
 * <li>Corresponding method: {@link #setShowBuffering(int)}
 * <li>Default: {@code never}
 * </ul>
 * <li><b>{@code resize_mode}</b> - Controls how video and album art is resized within the view.
 * Valid values are {@code fit}, {@code fixed_width}, {@code fixed_height} and {@code fill}.
 * <ul>
 * <li>Corresponding method: {@link #setResizeMode(int)}
 * <li>Default: {@code fit}
 * </ul>
 * <li><b>{@code surface_type}</b> - The type of surface view used for video playbacks. Valid
 * values are {@code surface_view}, {@code texture_view}, {@code spherical_gl_surface_view}, {@code
 * video_decoder_gl_surface_view} and {@code none}. Using {@code none} is recommended for audio only
 * applications, since creating the surface can be expensive. Using {@code surface_view} is
 * recommended for video applications. Note, TextureView can only be used in NetWorkUtil hardware accelerated
 * window. When rendered in software, TextureView will draw nothing.
 * <ul>
 * <li>Corresponding method: None
 * <li>Default: {@code surface_view}
 * </ul>
 * <li><b>{@code shutter_background_color}</b> - The background color of the {@code exo_shutter}
 * view.
 * <ul>
 * <li>Corresponding method: {@link #setShutterBackgroundColor(int)}
 * <li>Default: {@code unset}
 * </ul>
 * <li><b>{@code keep_content_on_player_reset}</b> - Whether the currently displayed video frame
 * or media artwork is kept visible when the player is reset.
 * <ul>
 * <li>Corresponding method: {@link #setKeepContentOnPlayerReset(boolean)}
 * <li>Default: {@code false}
 * </ul>
 * <li><b>{@code player_layout_id}</b> - Specifies the id of the layout to be inflated. See below
 * for more details.
 * <ul>
 * <li>Corresponding method: None
 * <li>Default: {@code R.layout.exo_player_view}
 * </ul>
 * <li><b>{@code controller_layout_id}</b> - Specifies the id of the layout resource to be
 * inflated by the child {@link PlayerControlView}. See below for more details.
 * <ul>
 * <li>Corresponding method: None
 * <li>Default: {@code R.layout.exo_player_control_view}
 * </ul>
 * <li>All attributes that can be set on {@link PlayerControlView} and {@link DefaultTimeBar} can
 * also be set on NetWorkUtil PlayerView, and will be propagated to the inflated {@link PlayerControlView}
 * unless the layout is overridden to specify NetWorkUtil custom {@code exo_controller} (see below).
 * </ul>
 *
 * <h3>Overriding the layout file</h3>
 * <p>
 * To customize the layout of PlayerView throughout your app, or just for certain configurations,
 * you can define {@code exo_player_view.xml} layout files in your application {@code res/layout*}
 * directories. These layouts will override the one provided by the ExoPlayer library, and will be
 * inflated for use by PlayerView. The view identifies and binds its children by looking for the
 * following ids:
 *
 * <p>
 *
 * <ul>
 * <li><b>{@code exo_content_frame}</b> - A frame whose aspect ratio is resized based on the video
 * or album art of the media being played, and the configured {@code resize_mode}. The video surface
 * view is inflated into this frame as its first child.
 * <ul>
 * <li>Type: {@link AspectRatioFrameLayout}
 * </ul>
 * <li><b>{@code exo_shutter}</b> - A view that's made visible when video should be hidden. This
 * view is typically an opaque view that covers the video surface, thereby obscuring it when
 * visible. Obscuring the surface in this way also helps to prevent flicker at the start of playback
 * when {@code surface_type="surface_view"}.
 * <ul>
 * <li>Type: {@link View}
 * </ul>
 * <li><b>{@code exo_buffering}</b> - A view that's made visible when the player is buffering.
 * This view typically displays NetWorkUtil buffering spinner or animation.
 * <ul>
 * <li>Type: {@link View}
 * </ul>
 * <li><b>{@code exo_subtitles}</b> - Displays subtitles.
 * <ul>
 * <li>Type: {@link SubtitleView}
 * </ul>
 * <li><b>{@code exo_artwork}</b> - Displays album art.
 * <ul>
 * <li>Type: {@link ImageView}
 * </ul>
 * <li><b>{@code exo_error_message}</b> - Displays an error message to the user if playback fails.
 * <ul>
 * <li>Type: {@link TextView}
 * </ul>
 * <li><b>{@code exo_controller_placeholder}</b> - A placeholder that's replaced with the inflated
 * {@link PlayerControlView}. Ignored if an {@code exo_controller} view exists.
 * <ul>
 * <li>Type: {@link View}
 * </ul>
 * <li><b>{@code exo_controller}</b> - An already inflated {@link PlayerControlView}. Allows use
 * of NetWorkUtil custom extension of {@link PlayerControlView}. {@link PlayerControlView} and {@link
 * DefaultTimeBar} attributes set on the PlayerView will not be automatically propagated through to
 * this instance. If NetWorkUtil view exists with this id, any {@code exo_controller_placeholder} view will be
 * ignored.
 * <ul>
 * <li>Type: {@link PlayerControlView}
 * </ul>
 * <li><b>{@code exo_ad_overlay}</b> - A {@link FrameLayout} positioned on top of the player which
 * is used to show ad UI (if applicable).
 * <ul>
 * <li>Type: {@link FrameLayout}
 * </ul>
 * <li><b>{@code exo_overlay}</b> - A {@link FrameLayout} positioned on top of the player which
 * the app can access via {@link #getOverlayFrameLayout()}, provided for convenience.
 * <ul>
 * <li>Type: {@link FrameLayout}
 * </ul>
 * </ul>
 *
 * <p>All child views are optional and so can be omitted if not required, however where defined
 * they must be of the expected type.
 *
 * <h3>Specifying NetWorkUtil custom layout file</h3>
 * <p>
 * Defining your own {@code exo_player_view.xml} is useful to customize the layout of PlayerView
 * throughout your application. It's also possible to customize the layout for NetWorkUtil single instance in
 * NetWorkUtil layout file. This is achieved by setting the {@code player_layout_id} attribute on NetWorkUtil
 * PlayerView. This will cause the specified layout to be inflated instead of {@code
 * exo_player_view.xml} for only the instance on which the attribute is set.
 */
public class PlayerView extends FrameLayout implements AdsLoader.AdViewProvider {


    // LINT.IfChange

    /**
     * Determines when the buffering view is shown. One of {@link #SHOW_BUFFERING_NEVER}, {@link
     * #SHOW_BUFFERING_WHEN_PLAYING} or {@link #SHOW_BUFFERING_ALWAYS}.
     */
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHOW_BUFFERING_NEVER, SHOW_BUFFERING_WHEN_PLAYING, SHOW_BUFFERING_ALWAYS})
    public @interface ShowBuffering {

    }

    /**
     * The buffering view is never shown.
     */
    public static final int SHOW_BUFFERING_NEVER = 0;
    /**
     * The buffering view is shown when the player is in the {@link Player#STATE_BUFFERING buffering}
     * state and {@link Player#getPlayWhenReady() playWhenReady} is {@code true}.
     */
    public static final int SHOW_BUFFERING_WHEN_PLAYING = 1;
    /**
     * The buffering view is always shown when the player is in the {@link Player#STATE_BUFFERING
     * buffering} state.
     */
    public static final int SHOW_BUFFERING_ALWAYS = 2;
    // LINT.ThenChange(../../../../../../res/values/attrs.xml)

    // LINT.IfChange
    private static final int SURFACE_TYPE_NONE = 0;
    private static final int SURFACE_TYPE_SURFACE_VIEW = 1;
    private static final int SURFACE_TYPE_TEXTURE_VIEW = 2;
    private static final int SURFACE_TYPE_SPHERICAL_GL_SURFACE_VIEW = 3;
    private static final int SURFACE_TYPE_VIDEO_DECODER_GL_SURFACE_VIEW = 4;
    // LINT.ThenChange(../../../../../../res/values/attrs.xml)

    private final ComponentListener componentListener;
    @Nullable
    private final AspectRatioFrameLayout contentFrame;
    @Nullable
    private final View shutterView;
    @Nullable
    private final View surfaceView;
    @Nullable
    private final ImageView artworkView;
    @Nullable
    private final SubtitleView subtitleView;
    @Nullable
    private final View bufferingView;
    @Nullable
    private final TextView errorMessageView;
    @Nullable
    private PlayerControlView controller;
    @Nullable
    private final FrameLayout adOverlayFrameLayout;
    @Nullable
    private final FrameLayout overlayFrameLayout;

    @Nullable
    private Player player;
    private boolean useController;
    @Nullable
    private PlayerControlView.VisibilityListener controllerVisibilityListener;
    private boolean useArtwork;
    @Nullable
    private Drawable defaultArtwork;
    private @ShowBuffering
    int showBuffering;
    private boolean keepContentOnPlayerReset;
    @Nullable
    private ErrorMessageProvider<? super ExoPlaybackException> errorMessageProvider;
    @Nullable
    private CharSequence customErrorMessage;
    private int controllerShowTimeoutMs;
    private boolean controllerAutoShow;
    private boolean controllerHideDuringAds;
    private boolean controllerHideOnTouch;
    private int textureViewRotation;
    private boolean isTouching;
    private static final int PICTURE_TYPE_FRONT_COVER = 3;
    private static final int PICTURE_TYPE_NOT_SET = -1;

    public PlayerView(Context context) {
        this(context, /* attrs= */ null);
    }

    public PlayerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, /* defStyleAttr= */ 0);
    }

    @SuppressWarnings({"nullness:argument.type.incompatible", "nullness:method.invocation.invalid"})
    public PlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        componentListener = new ComponentListener();

        if (isInEditMode()) {
            contentFrame = null;
            shutterView = null;
            surfaceView = null;
            artworkView = null;
            subtitleView = null;
            bufferingView = null;
            errorMessageView = null;
            controller = null;
            adOverlayFrameLayout = null;
            overlayFrameLayout = null;
            ImageView logo = new ImageView(context);
            if (Util.SDK_INT >= 23) {
                configureEditModeLogoV23(getResources(), logo);
            } else {
                configureEditModeLogo(getResources(), logo);
            }
            addView(logo);
            return;
        }

        // 遮罩颜色
        boolean shutterColorSet = false;
        int shutterColor = 0;
        // player主布局
        /**
         * AspectRatioFrameLayout 主要负责宽高适应 里面有错误图,加载中转圈,SubtitleView
         * exo_ad_overlay 一个frameLayout
         * exo_overlay 一个frameLayout
         * exo_controller_placeholder 一个View
         *
         */
        int playerLayoutId = R.layout.exo_player_view;
        // 艺术品
        boolean useArtwork = true;
        // 默认艺术品Id
        int defaultArtworkId = 0;
        // 是否用控制器
        boolean useController = true;
        // 根据Type,创建surfaceView
        int surfaceType = SURFACE_TYPE_SURFACE_VIEW;
        // 根据某种规则,设置宽高比
        int resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT;
        // 默认 显示超时时间 DEFAULT_SHOW_TIMEOUT_MS == 5秒
        int controllerShowTimeoutMs = PlayerControlView.DEFAULT_SHOW_TIMEOUT_MS;
        // 控制View隐藏后,是否可以触摸
        boolean controllerHideOnTouch = true;
        // 控制View是否自动展示
        boolean controllerAutoShow = true;
        // 控制View,在广告播放期间是否隐藏
        boolean controllerHideDuringAds = true;
        // 缓冲的时候,bufferView是否展示
        int showBuffering = SHOW_BUFFERING_NEVER;

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PlayerView, 0, 0);
            try {
                // 可以在xml中,替换的参数
                shutterColorSet = a.hasValue(R.styleable.PlayerView_shutter_background_color);
                shutterColor = a.getColor(R.styleable.PlayerView_shutter_background_color, shutterColor);
                // 主布局
                playerLayoutId = a.getResourceId(R.styleable.PlayerView_player_layout_id, playerLayoutId);
                useArtwork = a.getBoolean(R.styleable.PlayerView_use_artwork, useArtwork);
                defaultArtworkId =
                        a.getResourceId(R.styleable.PlayerView_default_artwork, defaultArtworkId);
                useController = a.getBoolean(R.styleable.PlayerView_use_controller, useController);
                surfaceType = a.getInt(R.styleable.PlayerView_surface_type, surfaceType);
                resizeMode = a.getInt(R.styleable.PlayerView_resize_mode, resizeMode);
                controllerShowTimeoutMs =
                        a.getInt(R.styleable.PlayerView_show_timeout, controllerShowTimeoutMs);
                controllerHideOnTouch =
                        a.getBoolean(R.styleable.PlayerView_hide_on_touch, controllerHideOnTouch);
                controllerAutoShow = a.getBoolean(R.styleable.PlayerView_auto_show, controllerAutoShow);
                showBuffering = a.getInteger(R.styleable.PlayerView_show_buffering, showBuffering);
                keepContentOnPlayerReset =
                        a.getBoolean(
                                R.styleable.PlayerView_keep_content_on_player_reset, keepContentOnPlayerReset);
                controllerHideDuringAds =
                        a.getBoolean(R.styleable.PlayerView_hide_during_ads, controllerHideDuringAds);
            } finally {
                a.recycle();
            }
        }
        // 加载主布局
        LayoutInflater.from(context).inflate(playerLayoutId, this);
        // 子View优先处理焦点
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

        // Content frame.
        // 就是找的:AspectRatioFrameLayout 或者自定义R.id.exo_content_frame的布局
        contentFrame = findViewById(R.id.exo_content_frame);
        if (contentFrame != null) {
            // 设置resizeMode
            setResizeModeRaw(contentFrame, resizeMode);
        }

        // Shutter view.
        // AspectRatioFrameLayout 内部的 exo_shutter
        shutterView = findViewById(R.id.exo_shutter);
        if (shutterView != null && shutterColorSet) {
            //单纯的一个View设置背景色
            shutterView.setBackgroundColor(shutterColor);
        }

        // Create NetWorkUtil surface view and insert it into the content frame, if there is one.
        if (contentFrame != null && surfaceType != SURFACE_TYPE_NONE) {
            // 存在AspectRatioFrameLayout,并且不是纯音频{SURFACE_TYPE_NONE 代表没有Surface}
            // 有趣的是 AspectRatioFrameLayout是什么尺寸,surfaceView就是什么尺寸
            ViewGroup.LayoutParams params =
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            switch (surfaceType) {
                case SURFACE_TYPE_TEXTURE_VIEW:
                    surfaceView = new TextureView(context);
                    break;
                case SURFACE_TYPE_SPHERICAL_GL_SURFACE_VIEW:
                    SphericalGLSurfaceView sphericalGLSurfaceView = new SphericalGLSurfaceView(context);
                    sphericalGLSurfaceView.setSingleTapListener(componentListener);
                    surfaceView = sphericalGLSurfaceView;
                    break;
                case SURFACE_TYPE_VIDEO_DECODER_GL_SURFACE_VIEW:
                    surfaceView = new VideoDecoderGLSurfaceView(context);
                    break;
                default:
                    surfaceView = new SurfaceView(context);
                    break;
            }
            surfaceView.setLayoutParams(params);
            contentFrame.addView(surfaceView, 0);
            // 把surfaceView,添加到contentFrame上面!!
        } else {
            surfaceView = null;
        }

        // Ad overlay frame layout.
        // 添加透明的广告图
        adOverlayFrameLayout = findViewById(R.id.exo_ad_overlay);

        // Overlay frame layout.
        // 又添加一个透明层
        overlayFrameLayout = findViewById(R.id.exo_overlay);

        // Artwork view.
        // AspectRatioFrameLayout内部的一个ImageView
        artworkView = findViewById(R.id.exo_artwork);
        this.useArtwork = useArtwork && artworkView != null;
        if (defaultArtworkId != 0) {
            // 获取艺术图,其实就是占位图,这个占位图就是给artworkView使用的
            defaultArtwork = ContextCompat.getDrawable(getContext(), defaultArtworkId);
        }

        // Subtitle view.
        // 字幕
        subtitleView = findViewById(R.id.exo_subtitles);
        if (subtitleView != null) {
            subtitleView.setUserDefaultStyle();
            subtitleView.setUserDefaultTextSize();
        }

        // Buffering view.
        // 缓冲的时候,展示的View,默认是 红色的ProgressBar
        // Buffering view.
        bufferingView = findViewById(R.id.exo_buffering);
        if (bufferingView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
                ProgressBar progressBar = (ProgressBar) bufferingView;
                progressBar.setIndeterminateTintList(colorStateList);
            }
            bufferingView.setVisibility(View.GONE);
        }
        // Buffering view.
        // 缓冲的时候,展示的View,默认是 红色的ProgressBar
//        View bufferingViewTemp = findViewById(R.id.exo_buffering);
//        if (contentFrame != null) {
//            contentFrame.removeView(bufferingViewTemp);
//        }
//        bufferingView = LayoutInflater.from(context).inflate(R.layout.exo_loading, null);
//
//        if (contentFrame != null && bufferingView != null) {
//            contentFrame.addView(bufferingView,
//                    new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                            ViewGroup.LayoutParams.WRAP_CONTENT,
//                            Gravity.CENTER));
//            bufferingView.setVisibility(View.GONE);
//        }

        // 是否展示缓冲View
        this.showBuffering = showBuffering;

        // Error message view.
        // 错误信息 TextView
        errorMessageView = findViewById(R.id.exo_error_message);
        if (errorMessageView != null) {
            errorMessageView.setVisibility(View.GONE);
        }

        // Playback control view.
        // 这个必定为null,因为 R.layout.exo_player_view 的4层布局里面,没有该Id
        PlayerControlView customController = findViewById(R.id.exo_controller);
        // 4层叠加布局,最上层是exo_controller_placeholder
        View controllerPlaceholder = findViewById(R.id.exo_controller_placeholder);
        if (customController != null) {
            this.controller = customController;
        } else if (controllerPlaceholder != null) {
            // Propagate attrs as playbackAttrs so that PlayerControlView's custom attributes are
            // transferred, but standard attributes (e.g. background) are not.
            // 这里是自定义PlayerControlView的构造方法,把PlayerView的attrs,传递给PlayerControlView
            this.controller = new PlayerControlView(context, null, 0, attrs);

            // 在这里动态设置了R.id.exo_controller.以便上面能找到
            controller.setId(R.id.exo_controller);
            // placeHolder什么布局属性,controlView就是什么布局属性
            controller.setLayoutParams(controllerPlaceholder.getLayoutParams());
            // parent就是PlayerView对象本身
            ViewGroup parent = ((ViewGroup) controllerPlaceholder.getParent());
            // 获取controllerPlaceholder的位置
            int controllerIndex = parent.indexOfChild(controllerPlaceholder);
            // 移除View
            parent.removeView(controllerPlaceholder);
            // 相当于用controller 替换了占位图
            parent.addView(controller, controllerIndex);
        } else {
            this.controller = null;
        }
        this.controllerShowTimeoutMs = controller != null ? controllerShowTimeoutMs : 0;
        this.controllerHideOnTouch = controllerHideOnTouch;
        this.controllerAutoShow = controllerAutoShow;
        this.controllerHideDuringAds = controllerHideDuringAds;
        this.useController = useController && controller != null;
        // 初始化先 隐藏controllerView
        hideController();
        // 更新内容描述
        updateContentDescription();
        if (controller != null) {
            // controller 注册回调接口,内部调用接口,把响应的结果,回传给ComponentListener
            controller.addVisibilityListener(/* listener= */ componentListener);
        }
    }

    //------------------自定义区域------------------//

    public void syncSkyWorthMediaStatus(boolean sync) {
        controller.syncSkyWorthMediaStatus(sync);
    }

    public void shouldHideVideoNextButton(boolean hide) {
        controller.shouldHideVideoNextButton(hide);
    }

    public void hideLinkVR() {
        controller.hideLinkVR();
    }

    public void setVideoRatio(String ratio) {
        controller.setVideoRatio(ratio);
    }

    public void setVideoLangName(String langName) {
        controller.setVideoLangName(langName);
    }

    public void setControllerPadding(int left, int top, int right, int bottom) {
        controller.setRootPadding(left, top, right, bottom);
    }

    /**
     * 设置播放进度监听！
     *
     * @param listener
     */
    public void setProgressUpdateListener(PlayerControlView.ProgressUpdateListener listener) {
        controller.setProgressUpdateListener(listener);
    }

    /**
     * 更新横屏模式网络状态 5G,4G,WIFI
     *
     * @param status
     */
    public void setNetStatus(String status) {
        controller.setNetStatus(status);
    }

    /**
     * 更新视频名称
     *
     * @param title
     */
    public void setVideoTitle(String title) {
        controller.setVideoTitle(title);
    }

    /**
     * 设置视频标题不能交互
     */
    public void setVideoTitleNoAction() {
        controller.setVideoTitleNoAction();
    }

    /**
     * 隐藏视频设置按钮
     */
    public void hideVideoSetting() {
        controller.hideVideoSetting();
    }


    /**
     * 当前是全景视频，确定展示回正按钮
     */
    public void showVideoReset() {
        controller.showVideoReset();
    }

    /**
     * ControlView和外部数据交互接口
     *
     * @param interAction
     */
    public void setInterActionWithCustomControlView(IControlViewInterAction interAction) {
        controller.setInterAction(interAction);
    }

    /**
     * 设置是否显示进入首页
     *
     * @param show
     */
    public void setEnterHomeVisibility(boolean show) {
        controller.setEnterHomeVisibility(show);
    }

    /**
     * 设置是否显示回退按钮
     *
     * @param show
     */
    public void setBackVisibility(boolean show) {
        controller.setBackVisibility(show);
    }

    //------------------自定义区域------------------//

    /**
     * Switches the view targeted by NetWorkUtil given {@link Player}.
     *
     * @param player        The player whose target view is being switched.
     * @param oldPlayerView The old view to detach from the player.
     * @param newPlayerView The new view to attach to the player.
     */
    public static void switchTargetView(
            Player player, @Nullable PlayerView oldPlayerView, @Nullable PlayerView newPlayerView) {
        if (oldPlayerView == newPlayerView) {
            return;
        }
        // We attach the new view before detaching the old one because this ordering allows the player
        // to swap directly from one surface to another, without transitioning through NetWorkUtil state where no
        // surface is attached. This is significantly more efficient and achieves NetWorkUtil more seamless
        // transition when using platform provided video decoders.
        if (newPlayerView != null) {
            newPlayerView.setPlayer(player);
        }
        if (oldPlayerView != null) {
            oldPlayerView.setPlayer(null);
        }
    }

    /**
     * Returns the player currently set on this view, or null if no player is set.
     */
    @Nullable
    public Player getPlayer() {
        return player;
    }

    /**
     * Set the {@link Player} to use.
     *
     * <p>To transition NetWorkUtil {@link Player} from targeting one view to another, it's recommended to use
     * {@link #switchTargetView(Player, PlayerView, PlayerView)} rather than this method. If you do
     * wish to use this method directly, be sure to attach the player to the new view <em>before</em>
     * calling {@code setPlayer(null)} to detach it from the old one. This ordering is significantly
     * more efficient and may allow for more seamless transitions.
     *
     * @param player The {@link Player} to use, or {@code null} to detach the current player. Only
     *               players which are accessed on the main thread are supported ({@code
     *               player.getApplicationLooper() == Looper.getMainLooper()}).
     */
    public void setPlayer(@Nullable Player player) {
        // 核实当前方法,必须在主线程调用
        Assertions.checkState(Looper.myLooper() == Looper.getMainLooper());
        // 核实player!=null
        Assertions.checkArgument(
                player == null || player.getApplicationLooper() == Looper.getMainLooper());
        if (this.player == player) {
            // 如果是同一个播放器,则直接return
            return;
        }
        @Nullable Player oldPlayer = this.player;
        // 如果另外一个全新的player设置进来,那么oldPlayer,移除监听,释放资源
        if (oldPlayer != null) {
            // 移除oldPlayer监听
            oldPlayer.removeListener(componentListener);
            @Nullable Player.VideoComponent oldVideoComponent = oldPlayer.getVideoComponent();
            if (oldVideoComponent != null) {
                // 移除视频播放监听
                oldVideoComponent.removeVideoListener(componentListener);
                // oldVideoComponent解除SurfaceView绑定
                if (surfaceView instanceof TextureView) {
                    oldVideoComponent.clearVideoTextureView((TextureView) surfaceView);
                } else if (surfaceView instanceof SphericalGLSurfaceView) {
                    ((SphericalGLSurfaceView) surfaceView).setVideoComponent(null);
                } else if (surfaceView instanceof VideoDecoderGLSurfaceView) {
                    oldVideoComponent.clearVideoSurfaceView(null);
                } else if (surfaceView instanceof SurfaceView) {
                    oldVideoComponent.clearVideoSurfaceView((SurfaceView) surfaceView);
                }
            }
            @Nullable Player.TextComponent oldTextComponent = oldPlayer.getTextComponent();
            if (oldTextComponent != null) {
                // 移除字幕监听
                oldTextComponent.removeTextOutput(componentListener);
            }
        }
        this.player = player;
        if (useController()) {
            // 因为控制层View,需要调用播放,暂停等功能,所以需要把player,传递给controller
            controller.setPlayer(player);
        }
        if (subtitleView != null) {
            // 设置字幕
            subtitleView.setCues(null);
        }
        // 更新,根据状态展示bufferingView
        /**
         * player状态必须等于BUFFERING,且 如果 是否展示==ALWAYS 或者 是否展示== 当前是否正在播
         * player.getPlaybackState() == Player.STATE_BUFFERING
         * showBuffering == SHOW_BUFFERING_ALWAYS || showBuffering == SHOW_BUFFERING_WHEN_PLAYING && player.getPlayWhenReady())
         */
        updateBuffering();
        // 更新错误信息是否显示
        /**
         * player.getPlaybackError() !=null && errorMessageProvider != null
         */
        updateErrorMessage();
        // ....
        updateForCurrentTrackSelections(/* isNewPlayer= */ true);
        if (player != null) {
            @Nullable Player.VideoComponent newVideoComponent = player.getVideoComponent(); // player.getVideoComponent() return this; this指的是ExoPlayer本身
            if (newVideoComponent != null) {
                // 让SphericalGLSurfaceView 绑定 ExoPlayer
                if (surfaceView instanceof TextureView) {
                    newVideoComponent.setVideoTextureView((TextureView) surfaceView);
                } else if (surfaceView instanceof SphericalGLSurfaceView) {
                    ((SphericalGLSurfaceView) surfaceView).setVideoComponent(newVideoComponent);
                } else if (surfaceView instanceof VideoDecoderGLSurfaceView) {
                    newVideoComponent.setVideoSurfaceView((VideoDecoderGLSurfaceView) surfaceView);
                } else if (surfaceView instanceof SurfaceView) {
                    newVideoComponent.setVideoSurfaceView((SurfaceView) surfaceView);
                }
                // 给ExoPlayer添加Video回调监听
                newVideoComponent.addVideoListener(componentListener);
            }
            // 设置字幕监听
            @Nullable Player.TextComponent newTextComponent = player.getTextComponent();
            if (newTextComponent != null) {
                newTextComponent.addTextOutput(componentListener);
            }
            // 设置播放器监听
            player.addListener(componentListener);
            maybeShowController(false);
        } else {
            hideController();
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (surfaceView instanceof SurfaceView) {
            // Work around https://github.com/google/ExoPlayer/issues/3160.
            surfaceView.setVisibility(visibility);
        }
    }

    /**
     * Sets the {@link AspectRatioFrameLayout.ResizeMode}.
     *
     * @param resizeMode The {@link AspectRatioFrameLayout.ResizeMode}.
     */
    public void setResizeMode(@AspectRatioFrameLayout.ResizeMode int resizeMode) {
        Assertions.checkStateNotNull(contentFrame);
        contentFrame.setResizeMode(resizeMode);
    }

    /**
     * Returns the {@link AspectRatioFrameLayout.ResizeMode}.
     */
    public @AspectRatioFrameLayout.ResizeMode
    int getResizeMode() {
        Assertions.checkStateNotNull(contentFrame);
        return contentFrame.getResizeMode();
    }

    /**
     * Returns whether artwork is displayed if present in the media.
     */
    public boolean getUseArtwork() {
        return useArtwork;
    }

    /**
     * Sets whether artwork is displayed if present in the media.
     *
     * @param useArtwork Whether artwork is displayed.
     */
    public void setUseArtwork(boolean useArtwork) {
        Assertions.checkState(!useArtwork || artworkView != null);
        if (this.useArtwork != useArtwork) {
            this.useArtwork = useArtwork;
            updateForCurrentTrackSelections(/* isNewPlayer= */ false);
        }
    }

    /**
     * Returns the default artwork to display.
     */
    @Nullable
    public Drawable getDefaultArtwork() {
        return defaultArtwork;
    }

    /**
     * Sets the default artwork to display if {@code useArtwork} is {@code true} and no artwork is
     * present in the media.
     *
     * @param defaultArtwork the default artwork to display.
     * @deprecated use (@link {@link #setDefaultArtwork(Drawable)} instead.
     */
    @Deprecated
    public void setDefaultArtwork(@Nullable Bitmap defaultArtwork) {
        setDefaultArtwork(
                defaultArtwork == null ? null : new BitmapDrawable(getResources(), defaultArtwork));
    }

    /**
     * Sets the default artwork to display if {@code useArtwork} is {@code true} and no artwork is
     * present in the media.
     *
     * @param defaultArtwork the default artwork to display
     */
    public void setDefaultArtwork(@Nullable Drawable defaultArtwork) {
        if (this.defaultArtwork != defaultArtwork) {
            this.defaultArtwork = defaultArtwork;
            updateForCurrentTrackSelections(/* isNewPlayer= */ false);
        }
    }

    /**
     * Returns whether the playback controls can be shown.
     */
    public boolean getUseController() {
        return useController;
    }

    /**
     * Sets whether the playback controls can be shown. If set to {@code false} the playback controls
     * are never visible and are disconnected from the player.
     *
     * @param useController Whether the playback controls can be shown.
     */
    public void setUseController(boolean useController) {
        Assertions.checkState(!useController || controller != null);
        if (this.useController == useController) {
            return;
        }
        this.useController = useController;
        if (useController()) {
            controller.setPlayer(player);
        } else if (controller != null) {
            controller.hide();
            controller.setPlayer(/* player= */ null);
        }
        updateContentDescription();
    }

    /**
     * Sets the background color of the {@code exo_shutter} view.
     *
     * @param color The background color.
     */
    public void setShutterBackgroundColor(int color) {
        if (shutterView != null) {
            shutterView.setBackgroundColor(color);
        }
    }

    /**
     * Sets whether the currently displayed video frame or media artwork is kept visible when the
     * player is reset. A player reset is defined to mean the player being re-prepared with different
     * media, the player transitioning to unprepared media, {@link Player#stop(boolean)} being called
     * with {@code reset=true}, or the player being replaced or cleared by calling {@link
     * #setPlayer(Player)}.
     *
     * <p>If enabled, the currently displayed video frame or media artwork will be kept visible until
     * the player set on the view has been successfully prepared with new media and loaded enough of
     * it to have determined the available tracks. Hence enabling this option allows transitioning
     * from playing one piece of media to another, or from using one player instance to another,
     * without clearing the view's content.
     *
     * <p>If disabled, the currently displayed video frame or media artwork will be hidden as soon as
     * the player is reset. Note that the video frame is hidden by making {@code exo_shutter} visible.
     * Hence the video frame will not be hidden if using NetWorkUtil custom layout that omits this view.
     *
     * @param keepContentOnPlayerReset Whether the currently displayed video frame or media artwork is
     *                                 kept visible when the player is reset.
     */
    public void setKeepContentOnPlayerReset(boolean keepContentOnPlayerReset) {
        if (this.keepContentOnPlayerReset != keepContentOnPlayerReset) {
            this.keepContentOnPlayerReset = keepContentOnPlayerReset;
            updateForCurrentTrackSelections(/* isNewPlayer= */ false);
        }
    }

    /**
     * Sets whether NetWorkUtil buffering spinner is displayed when the player is in the buffering state. The
     * buffering spinner is not displayed by default.
     *
     * @param showBuffering Whether the buffering icon is displayed
     * @deprecated Use {@link #setShowBuffering(int)}
     */
    @Deprecated
    public void setShowBuffering(boolean showBuffering) {
        setShowBuffering(showBuffering ? SHOW_BUFFERING_WHEN_PLAYING : SHOW_BUFFERING_NEVER);
    }

    /**
     * Sets whether NetWorkUtil buffering spinner is displayed when the player is in the buffering state. The
     * buffering spinner is not displayed by default.
     *
     * @param showBuffering The mode that defines when the buffering spinner is displayed. One of
     *                      {@link #SHOW_BUFFERING_NEVER}, {@link #SHOW_BUFFERING_WHEN_PLAYING} and {@link
     *                      #SHOW_BUFFERING_ALWAYS}.
     */
    public void setShowBuffering(@ShowBuffering int showBuffering) {
        if (this.showBuffering != showBuffering) {
            this.showBuffering = showBuffering;
            updateBuffering();
        }
    }

    /**
     * Sets NetWorkUtil custom error message to be displayed by the view. The error message will be displayed
     * permanently, unless it is cleared by passing {@code null} to this method.
     *
     * @param message The message to display, or {@code null} to clear NetWorkUtil previously set message.
     */
    public void setCustomErrorMessage(@Nullable CharSequence message) {
        Assertions.checkState(errorMessageView != null);
        customErrorMessage = message;
        updateErrorMessage();
    }

    /**
     * Sets the optional {@link ErrorMessageProvider}.
     *
     * @param errorMessageProvider The error message provider.
     */
    public void setErrorMessageProvider(
            @Nullable ErrorMessageProvider<? super ExoPlaybackException> errorMessageProvider) {
        if (this.errorMessageProvider != errorMessageProvider) {
            this.errorMessageProvider = errorMessageProvider;
            updateErrorMessage();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (player != null && player.isPlayingAd()) {
            return super.dispatchKeyEvent(event);
        }

        boolean isDpadKey = isDpadKey(event.getKeyCode());
        boolean handled = false;
        if (isDpadKey && useController() && !controller.isVisible()) {
            // Handle the key event by showing the controller.
            maybeShowController(true);
            handled = true;
        } else if (dispatchMediaKeyEvent(event) || super.dispatchKeyEvent(event)) {
            // The key event was handled as NetWorkUtil media key or by the super class. We should also show the
            // controller, or extend its show timeout if already visible.
            maybeShowController(true);
            handled = true;
        } else if (isDpadKey && useController()) {
            // The key event wasn't handled, but we should extend the controller's show timeout.
            maybeShowController(true);
        }
        return handled;
    }

    /**
     * Called to process media key events. Any {@link KeyEvent} can be passed but only media key
     * events will be handled. Does nothing if playback controls are disabled.
     *
     * @param event A key event.
     * @return Whether the key event was handled.
     */
    public boolean dispatchMediaKeyEvent(KeyEvent event) {
        return useController() && controller.dispatchMediaKeyEvent(event);
    }

    /**
     * Returns whether the controller is currently visible.
     */
    public boolean isControllerVisible() {
        return controller != null && controller.isVisible();
    }

    /**
     * Shows the playback controls. Does nothing if playback controls are disabled.
     *
     * <p>The playback controls are automatically hidden during playback after {{@link
     * #getControllerShowTimeoutMs()}}. They are shown indefinitely when playback has not started yet,
     * is paused, has ended or failed.
     */
    public void showController() {
        showController(shouldShowControllerIndefinitely());
    }

    /**
     * Returns the playback controls timeout. The playback controls are automatically hidden after
     * this duration of time has elapsed without user input and with playback or buffering in
     * progress.
     *
     * @return The timeout in milliseconds. A non-positive value will cause the controller to remain
     * visible indefinitely.
     */
    public int getControllerShowTimeoutMs() {
        return controllerShowTimeoutMs;
    }

    /**
     * Hides the playback controls. Does nothing if playback controls are disabled.
     */
    public void hideController() {
        if (controller != null) {
            controller.hide();
        }
    }

    /**
     * Sets the playback controls timeout. The playback controls are automatically hidden after this
     * duration of time has elapsed without user input and with playback or buffering in progress.
     *
     * @param controllerShowTimeoutMs The timeout in milliseconds. A non-positive value will cause the
     *                                controller to remain visible indefinitely.
     */
    public void setControllerShowTimeoutMs(int controllerShowTimeoutMs) {
        Assertions.checkStateNotNull(controller);
        this.controllerShowTimeoutMs = controllerShowTimeoutMs;
        if (controller.isVisible()) {
            // Update the controller's timeout if necessary.
            showController();
        }
    }

    /**
     * Returns whether the playback controls are hidden by touch events.
     */
    public boolean getControllerHideOnTouch() {
        return controllerHideOnTouch;
    }

    /**
     * Sets whether the playback controls are hidden by touch events.
     *
     * @param controllerHideOnTouch Whether the playback controls are hidden by touch events.
     */
    public void setControllerHideOnTouch(boolean controllerHideOnTouch) {
        Assertions.checkStateNotNull(controller);
        this.controllerHideOnTouch = controllerHideOnTouch;
        updateContentDescription();
    }

    /**
     * Returns whether the playback controls are automatically shown when playback starts, pauses,
     * ends, or fails. If set to false, the playback controls can be manually operated with {@link
     * #showController()} and {@link #hideController()}.
     */
    public boolean getControllerAutoShow() {
        return controllerAutoShow;
    }

    /**
     * Sets whether the playback controls are automatically shown when playback starts, pauses, ends,
     * or fails. If set to false, the playback controls can be manually operated with {@link
     * #showController()} and {@link #hideController()}.
     *
     * @param controllerAutoShow Whether the playback controls are allowed to show automatically.
     */
    public void setControllerAutoShow(boolean controllerAutoShow) {
        this.controllerAutoShow = controllerAutoShow;
    }

    /**
     * Sets whether the playback controls are hidden when ads are playing. Controls are always shown
     * during ads if they are enabled and the player is paused.
     *
     * @param controllerHideDuringAds Whether the playback controls are hidden when ads are playing.
     */
    public void setControllerHideDuringAds(boolean controllerHideDuringAds) {
        this.controllerHideDuringAds = controllerHideDuringAds;
    }

    /**
     * Set the {@link PlayerControlView.VisibilityListener}.
     *
     * @param listener The listener to be notified about visibility changes, or null to remove the
     *                 current listener.
     */
    public void setControllerVisibilityListener(
            @Nullable PlayerControlView.VisibilityListener listener) {
        Assertions.checkStateNotNull(controller);
        if (this.controllerVisibilityListener == listener) {
            return;
        }
        if (this.controllerVisibilityListener != null) {
            controller.removeVisibilityListener(this.controllerVisibilityListener);
        }
        this.controllerVisibilityListener = listener;
        if (listener != null) {
            controller.addVisibilityListener(listener);
        }
    }

    /**
     * Sets the {@link PlaybackPreparer}.
     *
     * @param playbackPreparer The {@link PlaybackPreparer}, or null to remove the current playback
     *                         preparer.
     */
    public void setPlaybackPreparer(@Nullable PlaybackPreparer playbackPreparer) {
        Assertions.checkStateNotNull(controller);
        controller.setPlaybackPreparer(playbackPreparer);
    }

    /**
     * Sets the {@link ControlDispatcher}.
     *
     * @param controlDispatcher The {@link ControlDispatcher}, or null to use {@link
     *                          DefaultControlDispatcher}.
     */
    public void setControlDispatcher(@Nullable ControlDispatcher controlDispatcher) {
        Assertions.checkStateNotNull(controller);
        controller.setControlDispatcher(controlDispatcher);
    }

    /**
     * Sets the rewind increment in milliseconds.
     *
     * @param rewindMs The rewind increment in milliseconds. A non-positive value will cause the
     *                 rewind button to be disabled.
     */
    public void setRewindIncrementMs(int rewindMs) {
        Assertions.checkStateNotNull(controller);
        controller.setRewindIncrementMs(rewindMs);
    }

    /**
     * Sets the fast forward increment in milliseconds.
     *
     * @param fastForwardMs The fast forward increment in milliseconds. A non-positive value will
     *                      cause the fast forward button to be disabled.
     */
    public void setFastForwardIncrementMs(int fastForwardMs) {
        Assertions.checkStateNotNull(controller);
        controller.setFastForwardIncrementMs(fastForwardMs);
    }

    /**
     * Sets which repeat toggle modes are enabled.
     *
     * @param repeatToggleModes A set of {@link RepeatModeUtil.RepeatToggleModes}.
     */
    public void setRepeatToggleModes(@RepeatModeUtil.RepeatToggleModes int repeatToggleModes) {
        Assertions.checkStateNotNull(controller);
        controller.setRepeatToggleModes(repeatToggleModes);
    }

    /**
     * Sets whether the shuffle button is shown.
     *
     * @param showShuffleButton Whether the shuffle button is shown.
     */
    public void setShowShuffleButton(boolean showShuffleButton) {
        Assertions.checkStateNotNull(controller);
        controller.setShowShuffleButton(showShuffleButton);
    }

    /**
     * Sets whether the time bar should show all windows, as opposed to just the current one.
     *
     * @param showMultiWindowTimeBar Whether to show all windows.
     */
    public void setShowMultiWindowTimeBar(boolean showMultiWindowTimeBar) {
        Assertions.checkStateNotNull(controller);
        controller.setShowMultiWindowTimeBar(showMultiWindowTimeBar);
    }

    /**
     * Sets the millisecond positions of extra ad markers relative to the start of the window (or
     * timeline, if in multi-window mode) and whether each extra ad has been played or not. The
     * markers are shown in addition to any ad markers for ads in the player's timeline.
     *
     * @param extraAdGroupTimesMs The millisecond timestamps of the extra ad markers to show, or
     *                            {@code null} to show no extra ad markers.
     * @param extraPlayedAdGroups Whether each ad has been played, or {@code null} to show no extra ad
     *                            markers.
     */
    public void setExtraAdGroupMarkers(
            @Nullable long[] extraAdGroupTimesMs, @Nullable boolean[] extraPlayedAdGroups) {
        Assertions.checkStateNotNull(controller);
        controller.setExtraAdGroupMarkers(extraAdGroupTimesMs, extraPlayedAdGroups);
    }

    /**
     * Set the {@link AspectRatioFrameLayout.AspectRatioListener}.
     *
     * @param listener The listener to be notified about aspect ratios changes of the video content or
     *                 the content frame.
     */
    public void setAspectRatioListener(
            @Nullable AspectRatioFrameLayout.AspectRatioListener listener) {
        Assertions.checkStateNotNull(contentFrame);
        contentFrame.setAspectRatioListener(listener);
    }

    /**
     * Gets the view onto which video is rendered. This is NetWorkUtil:
     *
     * <ul>
     * <li>{@link SurfaceView} by default, or if the {@code surface_type} attribute is set to {@code
     * surface_view}.
     * <li>{@link TextureView} if {@code surface_type} is {@code texture_view}.
     * <li>{@link SphericalGLSurfaceView} if {@code surface_type} is {@code
     * spherical_gl_surface_view}.
     * <li>{@link VideoDecoderGLSurfaceView} if {@code surface_type} is {@code
     * video_decoder_gl_surface_view}.
     * <li>{@code null} if {@code surface_type} is {@code none}.
     * </ul>
     *
     * @return The {@link SurfaceView}, {@link TextureView}, {@link SphericalGLSurfaceView}, {@link
     * VideoDecoderGLSurfaceView} or {@code null}.
     */
    @Nullable
    public View getVideoSurfaceView() {
        return surfaceView;
    }

    /**
     * Gets the overlay {@link FrameLayout}, which can be populated with UI elements to show on top of
     * the player.
     *
     * @return The overlay {@link FrameLayout}, or {@code null} if the layout has been customized and
     * the overlay is not present.
     */
    @Nullable
    public FrameLayout getOverlayFrameLayout() {
        return overlayFrameLayout;
    }

    /**
     * Gets the {@link SubtitleView}.
     *
     * @return The {@link SubtitleView}, or {@code null} if the layout has been customized and the
     * subtitle view is not present.
     */
    @Nullable
    public SubtitleView getSubtitleView() {
        return subtitleView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!useController() || player == null) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouching = true;
                return true;
            case MotionEvent.ACTION_UP:
                if (isTouching) {
                    isTouching = false;
                    performClick();
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return toggleControllerVisibility();
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (!useController() || player == null) {
            return false;
        }
        maybeShowController(true);
        return true;
    }

    /**
     * Should be called when the player is visible to the user and if {@code surface_type} is {@code
     * spherical_gl_surface_view}. It is the counterpart to {@link #onPause()}.
     *
     * <p>This method should typically be called in {@code Activity.onStart()}, or {@code
     * Activity.onResume()} for API versions &lt;= 23.
     */
    public void onResume() {
        if (surfaceView instanceof SphericalGLSurfaceView) {
            ((SphericalGLSurfaceView) surfaceView).onResume();
        }
    }

    /**
     * Called when there's NetWorkUtil change in the aspect ratio of the content being displayed. The default
     * implementation sets the aspect ratio of the content frame to that of the content, unless the
     * content view is NetWorkUtil {@link SphericalGLSurfaceView} in which case the frame's aspect ratio is
     * cleared.
     *
     * @param contentAspectRatio The aspect ratio of the content.
     * @param contentFrame       The content frame, or {@code null}.
     * @param contentView        The view that holds the content being displayed, or {@code null}.
     */
    protected void onContentAspectRatioChanged(
            float contentAspectRatio,
            @Nullable AspectRatioFrameLayout contentFrame,
            @Nullable View contentView) {
        if (contentFrame != null) {
            contentFrame.setAspectRatio(
                    contentView instanceof SphericalGLSurfaceView ? 0 : contentAspectRatio);
        }
    }

    /**
     * Should be called when the player is no longer visible to the user and if {@code surface_type}
     * is {@code spherical_gl_surface_view}. It is the counterpart to {@link #onResume()}.
     *
     * <p>This method should typically be called in {@code Activity.onStop()}, or {@code
     * Activity.onPause()} for API versions &lt;= 23.
     */
    public void onPause() {
        if (surfaceView instanceof SphericalGLSurfaceView) {
            ((SphericalGLSurfaceView) surfaceView).onPause();
        }
    }

    // AdsLoader.AdViewProvider implementation.

    @Override
    public ViewGroup getAdViewGroup() {
        return Assertions.checkStateNotNull(
                adOverlayFrameLayout, "exo_ad_overlay must be present for ad playback");
    }

    @Override
    public View[] getAdOverlayViews() {
        ArrayList<View> overlayViews = new ArrayList<>();
        if (overlayFrameLayout != null) {
            overlayViews.add(overlayFrameLayout);
        }
        if (controller != null) {
            overlayViews.add(controller);
        }
        return overlayViews.toArray(new View[0]);
    }

    // Internal methods.

    //  @EnsuresNonNullIf(expression = "controller", result = true)
    private boolean useController() {
        if (useController) {
            Assertions.checkStateNotNull(controller);
            return true;
        }
        return false;
    }

    //  @EnsuresNonNullIf(expression = "artworkView", result = true)
    private boolean useArtwork() {
        if (useArtwork) {
            Assertions.checkStateNotNull(artworkView);
            return true;
        }
        return false;
    }

    private boolean toggleControllerVisibility() {
        if (!useController() || player == null) {
            return false;
        }
        if (!controller.isVisible()) {
            maybeShowController(true);
        } else if (controllerHideOnTouch) {
            controller.hide();
        }
        return true;
    }

    /**
     * Shows the playback controls, but only if forced or shown indefinitely.
     */
    private void maybeShowController(boolean isForced) {
        if (isPlayingAd() && controllerHideDuringAds) {
            return;
        }
        if (useController()) {
            boolean wasShowingIndefinitely = controller.isVisible() && controller.getShowTimeoutMs() <= 0;
            boolean shouldShowIndefinitely = shouldShowControllerIndefinitely();
            if (isForced || wasShowingIndefinitely || shouldShowIndefinitely) {
                showController(shouldShowIndefinitely);
            }
        }
    }

    private boolean shouldShowControllerIndefinitely() {
        if (player == null) {
            return true;
        }
        int playbackState = player.getPlaybackState();
        return controllerAutoShow
                && (playbackState == Player.STATE_IDLE
                || playbackState == STATE_ENDED
                || !player.getPlayWhenReady());
    }

    private void showController(boolean showIndefinitely) {
        if (!useController()) {
            return;
        }
        controller.setShowTimeoutMs(showIndefinitely ? 0 : controllerShowTimeoutMs);
        controller.show();
    }

    private boolean isPlayingAd() {
        return player != null && player.isPlayingAd() && player.getPlayWhenReady();
    }

    private void updateForCurrentTrackSelections(boolean isNewPlayer) {
        @Nullable Player player = this.player;
        if (player == null || player.getCurrentTrackGroups().isEmpty()) {
            if (!keepContentOnPlayerReset) {
                hideArtwork();
                closeShutter();
            }
            return;
        }

        if (isNewPlayer && !keepContentOnPlayerReset) {
            // Hide any video from the previous player.
            closeShutter();
        }

        TrackSelectionArray selections = player.getCurrentTrackSelections();
        for (int i = 0; i < selections.length; i++) {
            if (player.getRendererType(i) == C.TRACK_TYPE_VIDEO && selections.get(i) != null) {
                // Video enabled so artwork must be hidden. If the shutter is closed, it will be opened in
                // onRenderedFirstFrame().
                hideArtwork();
                return;
            }
        }

        // Video disabled so the shutter must be closed.
        closeShutter();
        // Display artwork if enabled and available, else hide it.
        if (useArtwork()) {
            for (int i = 0; i < selections.length; i++) {
                @Nullable TrackSelection selection = selections.get(i);
                if (selection != null) {
                    for (int j = 0; j < selection.length(); j++) {
                        @Nullable Metadata metadata = selection.getFormat(j).metadata;
                        if (metadata != null && setArtworkFromMetadata(metadata)) {
                            return;
                        }
                    }
                }
            }
            if (setDrawableArtwork(defaultArtwork)) {
                return;
            }
        }
        // Artwork disabled or unavailable.
        hideArtwork();
    }

    //  @RequiresNonNull("artworkView")
    private boolean setArtworkFromMetadata(Metadata metadata) {
        boolean isArtworkSet = false;
        int currentPictureType = PICTURE_TYPE_NOT_SET;
        for (int i = 0; i < metadata.length(); i++) {
            Metadata.Entry metadataEntry = metadata.get(i);
            int pictureType;
            byte[] bitmapData;
            if (metadataEntry instanceof ApicFrame) {
                bitmapData = ((ApicFrame) metadataEntry).pictureData;
                pictureType = ((ApicFrame) metadataEntry).pictureType;
            } else if (metadataEntry instanceof PictureFrame) {
                bitmapData = ((PictureFrame) metadataEntry).pictureData;
                pictureType = ((PictureFrame) metadataEntry).pictureType;
            } else {
                continue;
            }
            // Prefer the first front cover picture. If there aren't any, prefer the first picture.
            if (currentPictureType == PICTURE_TYPE_NOT_SET || pictureType == PICTURE_TYPE_FRONT_COVER) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
                isArtworkSet = setDrawableArtwork(new BitmapDrawable(getResources(), bitmap));
                currentPictureType = pictureType;
                if (currentPictureType == PICTURE_TYPE_FRONT_COVER) {
                    break;
                }
            }
        }
        return isArtworkSet;
    }

    //  @RequiresNonNull("artworkView")
    private boolean setDrawableArtwork(@Nullable Drawable drawable) {
        if (drawable != null) {
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            if (drawableWidth > 0 && drawableHeight > 0) {
                float artworkAspectRatio = (float) drawableWidth / drawableHeight;
                onContentAspectRatioChanged(artworkAspectRatio, contentFrame, artworkView);
                artworkView.setImageDrawable(drawable);
                artworkView.setVisibility(VISIBLE);
                return true;
            }
        }
        return false;
    }

    private void hideArtwork() {
        if (artworkView != null) {
            artworkView.setImageResource(android.R.color.transparent); // Clears any bitmap reference.
            artworkView.setVisibility(INVISIBLE);
        }
    }

    private void closeShutter() {
        if (shutterView != null) {
            shutterView.setVisibility(View.VISIBLE);
        }
    }

    private void updateBuffering() {
        if (bufferingView != null) {
            boolean showBufferingSpinner =
                    player != null
                            && player.getPlaybackState() == Player.STATE_BUFFERING
                            && (showBuffering == SHOW_BUFFERING_ALWAYS
                            || (showBuffering == SHOW_BUFFERING_WHEN_PLAYING && player.getPlayWhenReady()));
            bufferingView.setVisibility(showBufferingSpinner ? View.VISIBLE : View.GONE);
        }
    }

    private void updateErrorMessage() {
        if (errorMessageView != null) {
            if (customErrorMessage != null) {
                errorMessageView.setText(customErrorMessage);
                errorMessageView.setVisibility(View.VISIBLE);
                return;
            }
            @Nullable ExoPlaybackException error = player != null ? player.getPlaybackError() : null;
            if (error != null && errorMessageProvider != null) {
                CharSequence errorMessage = errorMessageProvider.getErrorMessage(error).second;
                errorMessageView.setText(errorMessage);
                errorMessageView.setVisibility(View.VISIBLE);
            } else {
                errorMessageView.setVisibility(View.GONE);
            }
        }
    }

    private void updateContentDescription() {
        if (controller == null || !useController) {
            setContentDescription(/* contentDescription= */ null);
        } else if (controller.getVisibility() == View.VISIBLE) {
            setContentDescription(
                    /* contentDescription= */ controllerHideOnTouch
                            ? getResources().getString(R.string.exo_controls_hide)
                            : null);
        } else {
            setContentDescription(
                    /* contentDescription= */ getResources().getString(R.string.exo_controls_show));
        }
    }

    @TargetApi(23)
    private static void configureEditModeLogoV23(Resources resources, ImageView logo) {
        logo.setImageDrawable(resources.getDrawable(R.drawable.exo_edit_mode_logo, null));
        logo.setBackgroundColor(resources.getColor(R.color.exo_edit_mode_background_color, null));
    }

    private static void configureEditModeLogo(Resources resources, ImageView logo) {
        logo.setImageDrawable(resources.getDrawable(R.drawable.exo_edit_mode_logo));
        logo.setBackgroundColor(resources.getColor(R.color.exo_edit_mode_background_color));
    }

    @SuppressWarnings("ResourceType")
    private static void setResizeModeRaw(AspectRatioFrameLayout aspectRatioFrame, int resizeMode) {
        aspectRatioFrame.setResizeMode(resizeMode);
    }

    /**
     * Applies NetWorkUtil texture rotation to NetWorkUtil {@link TextureView}.
     */
    private static void applyTextureViewRotation(TextureView textureView, int textureViewRotation) {
        Matrix transformMatrix = new Matrix();
        float textureViewWidth = textureView.getWidth();
        float textureViewHeight = textureView.getHeight();
        if (textureViewWidth != 0 && textureViewHeight != 0 && textureViewRotation != 0) {
            float pivotX = textureViewWidth / 2;
            float pivotY = textureViewHeight / 2;
            transformMatrix.postRotate(textureViewRotation, pivotX, pivotY);

            // After rotation, scale the rotated texture to fit the TextureView size.
            RectF originalTextureRect = new RectF(0, 0, textureViewWidth, textureViewHeight);
            RectF rotatedTextureRect = new RectF();
            transformMatrix.mapRect(rotatedTextureRect, originalTextureRect);
            transformMatrix.postScale(
                    textureViewWidth / rotatedTextureRect.width(),
                    textureViewHeight / rotatedTextureRect.height(),
                    pivotX,
                    pivotY);
        }
        textureView.setTransform(transformMatrix);
    }

    @SuppressLint("InlinedApi")
    private boolean isDpadKey(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_DPAD_UP
                || keyCode == KeyEvent.KEYCODE_DPAD_UP_RIGHT
                || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN_RIGHT
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_UP_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_CENTER;
    }

    private final class ComponentListener
            implements Player.EventListener,
            TextOutput,
            VideoListener,
            OnLayoutChangeListener,
            SingleTapListener,
            PlayerControlView.VisibilityListener {

        // TextOutput implementation

        @Override
        public void onCues(List<Cue> cues) {
            if (subtitleView != null) {
                subtitleView.onCues(cues);
            }
        }

        // VideoListener implementation

        @Override
        public void onVideoSizeChanged(
                int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            float videoAspectRatio =
                    (height == 0 || width == 0) ? 1 : (width * pixelWidthHeightRatio) / height;

            if (surfaceView instanceof TextureView) {
                // Try to apply rotation transformation when our surface is NetWorkUtil TextureView.
                if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                    // We will apply NetWorkUtil rotation 90/270 degree to the output texture of the TextureView.
                    // In this case, the output video's width and height will be swapped.
                    videoAspectRatio = 1 / videoAspectRatio;
                }
                if (textureViewRotation != 0) {
                    surfaceView.removeOnLayoutChangeListener(this);
                }
                textureViewRotation = unappliedRotationDegrees;
                if (textureViewRotation != 0) {
                    // The texture view's dimensions might be changed after layout step.
                    // So add an OnLayoutChangeListener to apply rotation after layout step.
                    surfaceView.addOnLayoutChangeListener(this);
                }
                applyTextureViewRotation((TextureView) surfaceView, textureViewRotation);
            }

            onContentAspectRatioChanged(videoAspectRatio, contentFrame, surfaceView);
        }

        @Override
        public void onRenderedFirstFrame() {
            if (shutterView != null) {
                shutterView.setVisibility(INVISIBLE);
            }
        }

        @Override
        public void onTracksChanged(TrackGroupArray tracks, TrackSelectionArray selections) {
            updateForCurrentTrackSelections(/* isNewPlayer= */ false);
        }

        // Player.EventListener implementation

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, @Player.State int playbackState) {
            updateBuffering();
            updateErrorMessage();
            if (isPlayingAd() && controllerHideDuringAds) {
                hideController();
            } else {
                maybeShowController(false);
            }
        }

        @Override
        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
            if (isPlayingAd() && controllerHideDuringAds) {
                hideController();
            }
        }

        // OnLayoutChangeListener implementation

        @Override
        public void onLayoutChange(
                View view,
                int left,
                int top,
                int right,
                int bottom,
                int oldLeft,
                int oldTop,
                int oldRight,
                int oldBottom) {
            applyTextureViewRotation((TextureView) view, textureViewRotation);
        }

        // SingleTapListener implementation

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return toggleControllerVisibility();
        }

        // PlayerControlView.VisibilityListener implementation

        @Override
        public void onVisibilityChange(int visibility) {
            updateContentDescription();
        }
    }
}
