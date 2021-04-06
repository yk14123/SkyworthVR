package com.chinafocus.hvrskyworthvr.rtr.show;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.exo.ExoManager;
import com.chinafocus.hvrskyworthvr.global.ConfigManager;
import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.model.bean.VideoContentList;
import com.chinafocus.hvrskyworthvr.net.ImageProcess;
import com.chinafocus.hvrskyworthvr.rtr.adapter.ShowRtrVideoListViewAdapter;
import com.chinafocus.hvrskyworthvr.rtr.dialog.RtrVrModeMainDialog;
import com.chinafocus.hvrskyworthvr.rtr.media.RtrMediaPlayActivity;
import com.chinafocus.hvrskyworthvr.rtr.mine.MineActivity;
import com.chinafocus.hvrskyworthvr.rtr.videolist.sub.RtrVideoSubViewModel;
import com.chinafocus.hvrskyworthvr.service.BluetoothService;
import com.chinafocus.hvrskyworthvr.service.event.VrCancelTimeTask;
import com.chinafocus.hvrskyworthvr.service.event.VrMainConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMainDisConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMainSyncMediaInfo;
import com.chinafocus.hvrskyworthvr.service.event.VrSyncPlayInfo;
import com.chinafocus.hvrskyworthvr.ui.adapter.BaseViewHolder;
import com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity;
import com.chinafocus.hvrskyworthvr.ui.widget.BackgroundAnimationRelativeLayout;
import com.chinafocus.hvrskyworthvr.ui.widget.transformer.MyCenterScaleTransformer;
import com.chinafocus.hvrskyworthvr.ui.widget.transformer.MyScrollStateChangeListener;
import com.chinafocus.hvrskyworthvr.util.TimeOutClickUtil;
import com.chinafocus.hvrskyworthvr.util.statusbar.StatusBarCompatFactory;
import com.chinafocus.hvrskyworthvr.util.widget.TagTextView;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollLayoutManager;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.chinafocus.hvrskyworthvr.global.Constants.REQUEST_CODE_PAD_MEDIA_ACTIVITY;
import static com.chinafocus.hvrskyworthvr.global.Constants.REQUEST_CODE_VR_MEDIA_ACTIVITY;
import static com.chinafocus.hvrskyworthvr.global.Constants.RESULT_CODE_ACTIVE_DIALOG;
import static com.chinafocus.hvrskyworthvr.global.Constants.RESULT_CODE_INACTIVE_DIALOG;
import static com.chinafocus.hvrskyworthvr.global.Constants.RESULT_CODE_SELF_INACTIVE_DIALOG;
import static com.chinafocus.hvrskyworthvr.rtr.media.RtrMediaPlayActivity.MEDIA_CATEGORY_TAG;
import static com.chinafocus.hvrskyworthvr.rtr.media.RtrMediaPlayActivity.MEDIA_FROM_TAG;
import static com.chinafocus.hvrskyworthvr.rtr.media.RtrMediaPlayActivity.MEDIA_ID;
import static com.chinafocus.hvrskyworthvr.service.BluetoothService.CURRENT_VR_ONLINE_STATUS;
import static com.chinafocus.hvrskyworthvr.service.BluetoothService.VR_STATUS_OFFLINE;
import static com.chinafocus.hvrskyworthvr.service.BluetoothService.VR_STATUS_ONLINE;
import static com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity.MEDIA_LINK_VR;
import static com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity.MEDIA_SEEK;

public class ShowActivity extends AppCompatActivity {

    private RtrVideoSubViewModel mViewModel;
    private ShowRtrVideoListViewAdapter mAdapter;
    private BackgroundAnimationRelativeLayout mBackgroundAnimationRelativeLayout;

    private MyRunnable mMyRunnable;

    private Disposable mDisposable;
    private RtrVrModeMainDialog vrModeMainDialog;
    private DiscreteScrollView mDiscreteScrollView;
    private MultiTransformation<Bitmap> mMultiTransformation;
    private TagTextView mVideoDes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompatFactory.getInstance().setStatusBarImmerse(this, false);
        setContentView(R.layout.activity_show);

        mViewModel = new ViewModelProvider(this).get(RtrVideoSubViewModel.class);

        mViewModel.getVideoContentList();

        findViewById(R.id.iv_mine_about).setOnClickListener(v -> TimeOutClickUtil.getDefault().startTimeOutClick(() -> startActivity(new Intent(ShowActivity.this, MineActivity.class))));

        mDiscreteScrollView = findViewById(R.id.rv_main_hot_cover);
        mBackgroundAnimationRelativeLayout = findViewById(R.id.view_background_change_animation);
        mVideoDes = findViewById(R.id.tv_media_des);
        mDiscreteScrollView.setOrientation(DSVOrientation.HORIZONTAL);
        mDiscreteScrollView.setSlideOnFling(true);

        mViewModel.videoDataMutableLiveData.observe(this, videoContentLists -> {
            if (mAdapter == null) {
                mAdapter = new ShowRtrVideoListViewAdapter(videoContentLists);

                InfiniteScrollAdapter<BaseViewHolder> scrollAdapter
                        = InfiniteScrollAdapter.wrap(mAdapter);

                mDiscreteScrollView.addScrollStateChangeListener(new MyScrollStateChangeListener() {
                    @Override
                    public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
                        // 视频暂停！
                        ExoManager.getInstance().setPlayWhenReady(false);
                        VrSyncPlayInfo.obtain().restoreVideoInfo();
                        closeTimer(null);

                        ((BaseViewHolder) currentItemHolder).getView(R.id.lottie_center_media).setVisibility(View.GONE);
                    }
                });

                mDiscreteScrollView.addOnItemChangedListener((viewHolder, adapterPosition) -> {

                    if (mMyRunnable == null) {
                        mMyRunnable = new MyRunnable();
                    }

                    int realPosition = scrollAdapter.getRealPosition(adapterPosition);

                    String intro = videoContentLists.get(realPosition).getIntro();
                    String color = videoContentLists.get(realPosition).getClassStyleColor();
                    String classify = videoContentLists.get(realPosition).getClassName();
                    mVideoDes.post(() -> {
                        mVideoDes.setContentAndTag("  " + intro, Collections.singletonList(classify), color);
                    });

                    postVideoBackgroundUrl(
                            ConfigManager.getInstance().getDefaultUrl()
                                    + videoContentLists.get(realPosition).getImgUrl()
                                    + ImageProcess.process(2560, 1600));

                    if (viewHolder instanceof BaseViewHolder) {

                        DiscreteScrollLayoutManager discreteScrollLayoutManager = (DiscreteScrollLayoutManager) mDiscreteScrollView.getLayoutManager();
                        for (int i = 0; i < Objects.requireNonNull(discreteScrollLayoutManager).getChildCount(); i++) {
                            View view = discreteScrollLayoutManager.getChildAt(i);
                            BaseViewHolder childViewHolder = (BaseViewHolder) mDiscreteScrollView.getChildViewHolder(Objects.requireNonNull(view));
                            if (childViewHolder == viewHolder) {
                                childViewHolder.getView(R.id.lottie_center_media).setVisibility(View.VISIBLE);
                            } else {
                                childViewHolder.getView(R.id.lottie_center_media).setVisibility(View.GONE);
                            }
                        }

                        mMyRunnable.setView(((BaseViewHolder) viewHolder).getView(R.id.iv_video_list_bg));
                        ExoManager.getInstance().init(getApplicationContext(), isPlaying -> {
                            Log.e("MyLog", " isPlaying >>> " + isPlaying);
                            mBackgroundAnimationRelativeLayout.removeCallbacks(mMyRunnable);
                            if (isPlaying) {
                                // 加载视频，开始播放1.5秒后，隐藏图片
                                mBackgroundAnimationRelativeLayout.postDelayed(mMyRunnable, 1500);
                            } else {
                                View view = mMyRunnable.getView();
                                if (view != null) {
                                    view.animate().alpha(1.f).setDuration(300).start();
                                }
                            }
                        });
                        ExoManager.getInstance().setTextureView(((BaseViewHolder) viewHolder).getView(R.id.texture_view_item));
                        ExoManager.getInstance().prepareSource(getApplicationContext(),
                                ConfigManager.getInstance().getDefaultUrl()
                                        + videoContentLists.get(realPosition).getMenuVideoUrl());
                        ExoManager.getInstance().setPlayWhenReady(true);
                    }
                });

                mAdapter.setOnClickCallback(adapterPosition -> {

                    int realPosition = scrollAdapter.getRealPosition(adapterPosition);
                    int realCurrentPosition = scrollAdapter.getRealCurrentPosition();

                    if (realCurrentPosition != realPosition) {
                        // 视频暂停！
                        ExoManager.getInstance().setPlayWhenReady(false);
                        VrSyncPlayInfo.obtain().restoreVideoInfo();
                        closeTimer(null);
                        mDiscreteScrollView.scrollToPosition(Integer.MAX_VALUE / 2 + realPosition);
                        return;
                    }

                    VideoContentList videoContentInfo = videoContentLists.get(realPosition);

                    int videoType = -1;
                    int videoClassify = -1;

                    if (videoContentInfo.getType() == 2) {
                        // 全景出版
                        videoType = 1;
                        videoClassify = -1;

                    } else if (videoContentInfo.getType() == 1) {
                        // 全景视频
                        videoType = 2;
                        videoClassify = Integer.parseInt(videoContentInfo.getClassify());
                    }
                    VrSyncPlayInfo.obtain().setCategory(videoClassify);
                    VrSyncPlayInfo.obtain().setTag(videoType);
                    int videoId = videoContentInfo.getId();

                    Intent intent = new Intent(ShowActivity.this, RtrMediaPlayActivity.class);
                    intent.putExtra(MEDIA_FROM_TAG, videoType);
                    intent.putExtra(MEDIA_CATEGORY_TAG, videoClassify);
                    intent.putExtra(MEDIA_ID, videoId);
                    ShowActivity.this.startActivityForResult(intent, REQUEST_CODE_PAD_MEDIA_ACTIVITY);

                });

                mDiscreteScrollView.setAdapter(scrollAdapter);
                mDiscreteScrollView.setItemTransitionTimeMillis(300);
                mDiscreteScrollView.setItemTransformer(new MyCenterScaleTransformer.Builder()
                        .setMinScale(0.94f)
                        .setMaxScale(1.54f)
                        .build());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CURRENT_VR_ONLINE_STATUS == VR_STATUS_OFFLINE) {
            disConnectFromVR(null);
        } else if (CURRENT_VR_ONLINE_STATUS == VR_STATUS_ONLINE) {
            // 1.关闭定时器
            closeTimer(null);
            // 2.展示控制画面
            showVrModeMainDialog();
        }
        Constants.ACTIVITY_TAG = Constants.ACTIVITY_MAIN;

        ExoManager.getInstance().setPlayWhenReady(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ExoManager.getInstance().setPlayWhenReady(false);
    }

    /**
     * 在首页戴上VR眼镜
     *
     * @param event 戴上VR眼镜事件
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void connectToVR(VrMainConnect event) {
        Log.d("MyLog", "-----在首页戴上VR眼镜-----");

        // 1.关闭定时器
        closeTimer(null);
        // 2.展示控制画面
        showVrModeMainDialog();
        // 3.给VR同步视频信息，如果当前video==-1的话，VR端需要切换到列表页面
        BluetoothService.getInstance()
                .sendMessage(
                        VrSyncPlayInfo.obtain().getTag(),
                        VrSyncPlayInfo.obtain().getCategory(),
                        VrSyncPlayInfo.obtain().getVideoId(),
                        VrSyncPlayInfo.obtain().getSeekTime()
                );

        if (VrSyncPlayInfo.obtain().getVideoId() != -1) {
            startSyncMediaPlayActivity();
        }

    }

    /**
     * 在首页取下VR眼镜
     *
     * @param event 取下VR眼镜事件
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void disConnectFromVR(VrMainDisConnect event) {
        Log.d("MyLog", "-----在首页取下VR眼镜-----");
        closeMainDialog();
        startTimeTask();
    }

    /**
     * 戴上VR后，VR选择了一个影片
     *
     * @param vrMainSyncMediaInfo VR选择影片事件
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void goToMediaPlayActivityAndActiveVRPlayerStatus(VrMainSyncMediaInfo vrMainSyncMediaInfo) {
        Log.d("MyLog", "-----VR选择了一个影片,Pad需要从首页跳转播放-----");
        closeTimer(null);
        startSyncMediaPlayActivity();
    }

    private void startSyncMediaPlayActivity() {
        Intent intent = new Intent(this, RtrMediaPlayActivity.class);
        intent.putExtra(MediaPlayActivity.MEDIA_FROM_TAG, VrSyncPlayInfo.obtain().getTag());
        intent.putExtra(MediaPlayActivity.MEDIA_CATEGORY_TAG, VrSyncPlayInfo.obtain().getCategory());
        intent.putExtra(MediaPlayActivity.MEDIA_ID, VrSyncPlayInfo.obtain().getVideoId());
        intent.putExtra(MEDIA_SEEK, VrSyncPlayInfo.obtain().getSeekTime());
        intent.putExtra(MEDIA_LINK_VR, true);
        startActivityForResult(intent, REQUEST_CODE_VR_MEDIA_ACTIVITY);
    }

    private void showVrModeMainDialog() {
        if (vrModeMainDialog == null) {
            vrModeMainDialog = new RtrVrModeMainDialog(this);
        }

        if (!vrModeMainDialog.isShowing()) {
            vrModeMainDialog.show();
        }
    }

    /**
     * 2分钟之后，视频重新选择
     */
    private void startTimeTask() {
        Observable.timer(2, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        closeTimer(null);
                        Log.d("MyLog", "-----开启2分钟定时器-----");
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(Long value) {
                        Log.d("MyLog", "-----定时器时间finish，执行任务成功！-----");
                        VrSyncPlayInfo.obtain().restoreVideoInfo();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_CODE_INACTIVE_DIALOG) {
            closeMainDialog();
            startTimeTask();
        } else if (resultCode == RESULT_CODE_ACTIVE_DIALOG) {
            showVrModeMainDialog();
            closeTimer(null);
        } else if (resultCode == RESULT_CODE_SELF_INACTIVE_DIALOG) {
            VrSyncPlayInfo.obtain().restoreVideoInfo();
            closeTimer(null);
        }
        // 修复RecyclerView位置
        if (data != null) {
            int currentVideoId = data.getIntExtra("currentVideoId", Integer.MAX_VALUE / 2);
            if (mAdapter != null) {
                int crease = mAdapter.calculatePositionFromVideoId(currentVideoId);
                mDiscreteScrollView.scrollToPosition(Integer.MAX_VALUE / 2 + crease);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 关闭定时器
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void closeTimer(VrCancelTimeTask vrCancelTimeTask) {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            Log.d("MyLog", "-----关闭2分钟定时器-----");
            mDisposable.dispose();
        }
    }


    private void closeMainDialog() {
        if (vrModeMainDialog != null && vrModeMainDialog.isShowing()) {
            Log.d("MyLog", "-----关闭MainActivity的控制dialog-----");
            vrModeMainDialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("MyLog", " ShowActivity onDestroy ");
        ExoManager.getInstance().onDestroy();
        BluetoothService.getInstance().releaseAll(this);
    }

    private static class MyRunnable implements Runnable {
        private View mView;

        public MyRunnable() {
        }

        public View getView() {
            return mView;
        }

        public void setView(View view) {
            mView = view;
        }

        @Override
        public void run() {
            // 加动画溶解
            mView.animate().alpha(0.f).setDuration(300).start();
        }
    }

    private void postVideoBackgroundUrl(String backgroundUrl) {
        if (mMultiTransformation == null) {
            CropTransformation cropTransformation = new CropTransformation(2560, 1600);
            BlurTransformation blurTransformation = new BlurTransformation(40);
            mMultiTransformation = new MultiTransformation<>(cropTransformation, blurTransformation);
        }

        Glide.with(this)
                .load(backgroundUrl)
                .apply(bitmapTransform(mMultiTransformation))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        mBackgroundAnimationRelativeLayout.setForeground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }
}