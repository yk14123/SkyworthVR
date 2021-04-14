package com.chinafocus.hvrskyworthvr.rtr.show;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.chinafocus.hvrskyworthvr.model.bean.TagHolder;
import com.chinafocus.hvrskyworthvr.model.bean.VideoContentList;
import com.chinafocus.hvrskyworthvr.net.ImageProcess;
import com.chinafocus.hvrskyworthvr.rtr.adapter.ShowRtrVideoListViewAdapter;
import com.chinafocus.hvrskyworthvr.rtr.dialog.RtrBluetoothConnectedDialog;
import com.chinafocus.hvrskyworthvr.rtr.dialog.RtrBluetoothLostDialog;
import com.chinafocus.hvrskyworthvr.rtr.dialog.RtrVrModeMainDialog;
import com.chinafocus.hvrskyworthvr.rtr.media.RtrMediaPlayActivity;
import com.chinafocus.hvrskyworthvr.rtr.mine.MineActivity;
import com.chinafocus.hvrskyworthvr.rtr.videolist.sub.RtrVideoSubViewModel;
import com.chinafocus.hvrskyworthvr.service.BluetoothService;
import com.chinafocus.hvrskyworthvr.service.event.VrCancelTimeTask;
import com.chinafocus.hvrskyworthvr.service.event.VrMainCancelBluetoothLostDelayTask;
import com.chinafocus.hvrskyworthvr.service.event.VrMainConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMainDisConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMainStartBluetoothLostDelayTask;
import com.chinafocus.hvrskyworthvr.service.event.VrMainSyncMediaInfo;
import com.chinafocus.hvrskyworthvr.service.event.VrSyncPlayInfo;
import com.chinafocus.hvrskyworthvr.ui.adapter.BaseViewHolder;
import com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity;
import com.chinafocus.hvrskyworthvr.ui.main.media.MediaViewModel;
import com.chinafocus.hvrskyworthvr.ui.widget.BackgroundAnimationRelativeLayout;
import com.chinafocus.hvrskyworthvr.ui.widget.ScaleTransitionPagerTitleView;
import com.chinafocus.hvrskyworthvr.ui.widget.transformer.MyCenterScaleTransformer;
import com.chinafocus.hvrskyworthvr.ui.widget.transformer.MyScrollStateChangeListener;
import com.chinafocus.hvrskyworthvr.util.TimeOutClickUtil;
import com.chinafocus.hvrskyworthvr.util.statusbar.StatusBarCompatFactory;
import com.chinafocus.hvrskyworthvr.util.widget.TagTextView;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollLayoutManager;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import static com.chinafocus.hvrskyworthvr.global.Constants.RESULT_CODE_ACTIVE_BLUETOOTH_CONNECTED;
import static com.chinafocus.hvrskyworthvr.global.Constants.RESULT_CODE_ACTIVE_BLUETOOTH_LOST;
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
import static net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator.MODE_EXACTLY;

public class ShowActivity extends AppCompatActivity {

    private ShowRtrVideoListViewAdapter mAdapter;
    private BackgroundAnimationRelativeLayout mBackgroundAnimationRelativeLayout;

    private MyRunnable mMyRunnable;

    private Disposable mDisposable;
    private RtrVrModeMainDialog vrModeMainDialog;
    private DiscreteScrollView mDiscreteScrollView;
    private MultiTransformation<Bitmap> mMultiTransformation;
    private TagTextView mVideoDes;
    private MyPostBackGroundRunnable mMyPostBackGroundRunnable;
    private MagicIndicator mMagicIndicator;
    private InfiniteScrollAdapter<BaseViewHolder> mScrollAdapter;
    private List<TagHolder> mTagHolders;
    private MediaViewModel mMediaViewModel;
    private MyBluetoothLostDelayTaskRunnable mBluetoothLostDelayTaskRunnable;
    private RtrBluetoothLostDialog mRtrBluetoothLostDialog;
    private RtrBluetoothConnectedDialog mRtrBluetoothConnectedDialog;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompatFactory.getInstance().setStatusBarImmerse(this, false);
        setContentView(R.layout.activity_show);

        RtrVideoSubViewModel mViewModel = new ViewModelProvider(this).get(RtrVideoSubViewModel.class);
        mMediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);

        mViewModel.getVideoContentList();

        findViewById(R.id.iv_mine_about).setOnClickListener(v -> TimeOutClickUtil.getDefault().startTimeOutClick(() -> startActivity(new Intent(ShowActivity.this, MineActivity.class))));

        mMagicIndicator = findViewById(R.id.magic_Indicator_main_tag);

        mDiscreteScrollView = findViewById(R.id.rv_main_hot_cover);
        mBackgroundAnimationRelativeLayout = findViewById(R.id.view_background_change_animation);
        mVideoDes = findViewById(R.id.tv_media_des);
        mDiscreteScrollView.setOrientation(DSVOrientation.HORIZONTAL);
        mDiscreteScrollView.setSlideOnFling(true);

        mViewModel.videoDataMutableLiveData.observe(this, videoContentLists -> {

            if (mAdapter == null) {

                preLoadImage(videoContentLists);
                preLoadAllVideoDetail(videoContentLists);

                mAdapter = new ShowRtrVideoListViewAdapter(videoContentLists);
                mScrollAdapter = InfiniteScrollAdapter.wrap(mAdapter);
                mDiscreteScrollView.addScrollStateChangeListener(new MyScrollStateChangeListener() {
                    @Override
                    public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
                        // 视频暂停！
                        ExoManager.getInstance().setPlayWhenReady(false);
                        VrSyncPlayInfo.obtain().restoreVideoInfo();
                        closeTimer(null);
                        mBackgroundAnimationRelativeLayout.removeCallbacks(mMyPostBackGroundRunnable);
                        ((BaseViewHolder) currentItemHolder).getView(R.id.lottie_center_media).setVisibility(View.GONE);
                    }
                });

                mAdapter.setOnClickCallback(adapterPosition -> {

                    closeTimer(null);

                    // realPosition：在list中，响应点击的实际item位置，0~list.size-1
                    int realPosition = mScrollAdapter.getRealPosition(adapterPosition);
                    // realCurrentPosition：在list中,当前中心点的实际item位置，0~list.size-1
                    int realCurrentPosition = mScrollAdapter.getRealCurrentPosition();

                    if (realCurrentPosition != realPosition) {
                        // 视频暂停！
                        ExoManager.getInstance().setPlayWhenReady(false);
                        VrSyncPlayInfo.obtain().restoreVideoInfo();
                        // adapterPosition：无线轮播中，recyclerView中的位置，其中Integer.Max/2为起始位置
                        mDiscreteScrollView.smoothScrollToPosition(adapterPosition);
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

                mDiscreteScrollView.setAdapter(mScrollAdapter);
                mDiscreteScrollView.setItemTransitionTimeMillis(220);
                mDiscreteScrollView.setItemTransformer(new MyCenterScaleTransformer.Builder()
                        .setMinScale(0.9377f)
                        .setMaxScale(1.8115f)
                        .build());

                setIndicatorContent(videoContentLists);

                mDiscreteScrollView.addOnItemChangedListener((viewHolder, adapterPosition) -> {

                    int realPosition = mScrollAdapter.getRealPosition(adapterPosition);

                    VideoContentList videoContentList = videoContentLists.get(realPosition);

                    bindItemToIndicator(videoContentList);
                    setTagViewContent(videoContentList);
                    postDelayShowBackground(videoContentList);

                    if (viewHolder instanceof BaseViewHolder) {
                        showLottieAnim(viewHolder);
                        startMenuMediaPlayer((BaseViewHolder) viewHolder, videoContentList);
                    }
                });

            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private void preLoadAllVideoDetail(List<VideoContentList> videoContentLists) {
        for (VideoContentList temp : videoContentLists) {
            mMediaViewModel.saveVideoDetailDataFromNet(temp.getType(), temp.getId());
        }
    }

    private void preLoadImage(List<VideoContentList> videoContentLists) {
        for (VideoContentList temp : videoContentLists) {
            Glide.with(this)
                    .load(ConfigManager.getInstance().getDefaultUrl() + temp.getImgUrl() + ImageProcess.process(600, 400))
                    .preload();
        }
    }

    private void setIndicatorContent(List<VideoContentList> videoContentLists) {
        mTagHolders = new ArrayList<>();

        for (int i = 0; i < videoContentLists.size(); i++) {
            String className = videoContentLists.get(i).getClassName();
            TagHolder tagHolder = new TagHolder(className, i);
            if (!mTagHolders.contains(tagHolder)) {
                mTagHolders.add(tagHolder);
            }
        }

        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(false);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mTagHolders.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ScaleTransitionPagerTitleView scaleTitleView = new ScaleTransitionPagerTitleView(context);
                scaleTitleView.setTextSize(20);
                scaleTitleView.setMinScale(0.833f);
                scaleTitleView.setNormalColor(getResources().getColor(R.color.color_white_a60));
                scaleTitleView.setSelectedColor(getResources().getColor(R.color.color_white));
                scaleTitleView.setText(mTagHolders.get(index).getClassName());
                scaleTitleView.setOnClickListener(view
                        -> {
                    mBackgroundAnimationRelativeLayout.removeCallbacks(mMyPostBackGroundRunnable);
                    bindIndicatorToItem(index);
                });

                return scaleTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setColors(Color.WHITE);
                linePagerIndicator.setLineWidth(62.f);
                linePagerIndicator.setLineHeight(12.f);
                linePagerIndicator.setMode(MODE_EXACTLY);
                linePagerIndicator.setRoundRadius(44);
                return linePagerIndicator;
            }
        });

        mMagicIndicator.setNavigator(commonNavigator);
    }

    private void bindItemToIndicator(VideoContentList videoContentList) {
        String className = videoContentList.getClassName();
        for (int i = 0; i < mTagHolders.size(); i++) {
            if (className.equals(mTagHolders.get(i).getClassName())) {
                mMagicIndicator.onPageSelected(i);
                mMagicIndicator.onPageScrolled(i, 0.0F, 0);
                break;
            }
        }
    }

    private void bindIndicatorToItem(int index) {
        int centerIndex = Integer.MAX_VALUE / 2 + mTagHolders.get(index).getStartIndex();
        mDiscreteScrollView.scrollToPosition(centerIndex);
        mScrollAdapter.notifyDataSetChanged();

        mMagicIndicator.onPageSelected(index);
        mMagicIndicator.onPageScrolled(index, 0.0F, 0);
    }

    private void showLottieAnim(RecyclerView.ViewHolder viewHolder) {
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
    }

    private void startMenuMediaPlayer(BaseViewHolder viewHolder, VideoContentList videoContentList) {
        if (mMyRunnable == null) {
            mMyRunnable = new MyRunnable();
        }
        mMyRunnable.setView(viewHolder.getView(R.id.iv_video_list_bg));
        ExoManager.getInstance().init(getApplicationContext(), isPlaying -> {
            mBackgroundAnimationRelativeLayout.removeCallbacks(mMyRunnable);
            if (isPlaying) {
                // 加载视频，开始播放1.0秒后，隐藏图片
                mBackgroundAnimationRelativeLayout.postDelayed(mMyRunnable, 1000);
            } else {
                View view = mMyRunnable.getView();
                if (view != null) {
                    view.animate().alpha(1.f).setDuration(300).start();
                }
            }
        });
        ExoManager.getInstance().setTextureView(viewHolder.getView(R.id.texture_view_item));
        ExoManager.getInstance().prepareSource(getApplicationContext(),
                ConfigManager.getInstance().getDefaultUrl()
                        + videoContentList.getMenuVideoUrl());
        ExoManager.getInstance().setPlayWhenReady(true);
    }

    private void postDelayShowBackground(VideoContentList videoContentList) {
        if (mMyPostBackGroundRunnable == null) {
            mMyPostBackGroundRunnable = new MyPostBackGroundRunnable();
        }
        mBackgroundAnimationRelativeLayout.removeCallbacks(mMyPostBackGroundRunnable);
        mMyPostBackGroundRunnable.setUrl(ConfigManager.getInstance().getDefaultUrl()
                + videoContentList.getImgUrl()
                + ImageProcess.process(600, 400));
        mBackgroundAnimationRelativeLayout.postDelayed(mMyPostBackGroundRunnable, 300);
    }

    private void setTagViewContent(VideoContentList videoContentList) {
        String intro = videoContentList.getIntro();
        String color = videoContentList.getClassStyleColor();
        String classify = videoContentList.getClassName();
        mVideoDes.post(() -> mVideoDes.setContentAndTag("  " + intro, Collections.singletonList(classify), color));
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
     * 当蓝牙断开链接的时候，处理一个延迟任务，如果超过2秒，就确定断开
     * 如果2秒钟之内恢复了链接，就取消延迟任务
     *
     * @param event 蓝牙断开事件
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void startBluetoothLostDelayTask(VrMainStartBluetoothLostDelayTask event) {
        if (mBluetoothLostDelayTaskRunnable == null) {
            mBluetoothLostDelayTaskRunnable = new MyBluetoothLostDelayTaskRunnable();
        }
        mBackgroundAnimationRelativeLayout.removeCallbacks(mBluetoothLostDelayTaskRunnable);
        mBackgroundAnimationRelativeLayout.postDelayed(mBluetoothLostDelayTaskRunnable, 2000);
    }

    private void startBluetoothLostTaskImmediately() {
        if (mBluetoothLostDelayTaskRunnable == null) {
            mBluetoothLostDelayTaskRunnable = new MyBluetoothLostDelayTaskRunnable();
        }
        mBackgroundAnimationRelativeLayout.removeCallbacks(mBluetoothLostDelayTaskRunnable);
        mBackgroundAnimationRelativeLayout.post(mBluetoothLostDelayTaskRunnable);
    }

    /**
     * 当蓝牙恢复的时候，考虑是否展示蓝牙恢复页面
     *
     * @param event 取下VR眼镜事件
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void cancelBluetoothLostDelayTask(VrMainCancelBluetoothLostDelayTask event) {
        mBackgroundAnimationRelativeLayout.removeCallbacks(mBluetoothLostDelayTaskRunnable);
        if (BluetoothService.getInstance().isBluetoothLostYet()) {
            Log.d("MyLog", "-----在首页展示蓝牙恢复页面-----");
            BluetoothService.getInstance().setBluetoothLostYet(false);
            hideBluetoothLostDialog();
            showBluetoothConnectDialog();
        }
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

    private void showBluetoothLostDialog() {
        if (mRtrBluetoothLostDialog == null) {
            mRtrBluetoothLostDialog = new RtrBluetoothLostDialog(this);
        }

        if (!mRtrBluetoothLostDialog.isShowing()) {
            mRtrBluetoothLostDialog.show();
        }
    }

    private void hideBluetoothLostDialog() {
        if (mRtrBluetoothLostDialog != null && mRtrBluetoothLostDialog.isShowing()) {
            mRtrBluetoothLostDialog.dismiss();
        }
    }

    private void hideBluetoothConnectDialog() {
        if (mRtrBluetoothConnectedDialog != null && mRtrBluetoothConnectedDialog.isShowing()) {
            mRtrBluetoothConnectedDialog.dismiss();
        }
    }

    private void showBluetoothConnectDialog() {
        if (mRtrBluetoothConnectedDialog == null) {
            mRtrBluetoothConnectedDialog = new RtrBluetoothConnectedDialog(this);
        }

        if (!mRtrBluetoothConnectedDialog.isShowing()) {
            mRtrBluetoothConnectedDialog.show();
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
        } else if (resultCode == RESULT_CODE_ACTIVE_BLUETOOTH_LOST) {
            VrSyncPlayInfo.obtain().restoreVideoInfo();
            closeTimer(null);
            startBluetoothLostTaskImmediately();
        } else if (resultCode == RESULT_CODE_ACTIVE_BLUETOOTH_CONNECTED) {
            VrSyncPlayInfo.obtain().restoreVideoInfo();
            closeTimer(null);
            showBluetoothConnectDialog();
        }
        // 修复RecyclerView位置
        if (data != null) {
            int currentVideoId = data.getIntExtra("currentVideoId", Integer.MAX_VALUE / 2);
            if (mAdapter != null) {
                int crease = mAdapter.calculatePositionFromVideoId(currentVideoId);
                mDiscreteScrollView.scrollToPosition(Integer.MAX_VALUE / 2 + crease);
                if (mScrollAdapter != null) {
                    mScrollAdapter.notifyDataSetChanged();
                }
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
        mBackgroundAnimationRelativeLayout.removeCallbacks(mMyPostBackGroundRunnable);
        mMyPostBackGroundRunnable = null;
        mBluetoothLostDelayTaskRunnable = null;
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

    private class MyPostBackGroundRunnable implements Runnable {
        private String url;

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            postVideoBackgroundUrl(url);
        }
    }

    private class MyBluetoothLostDelayTaskRunnable implements Runnable {
        @Override
        public void run() {
            BluetoothService.getInstance().setBluetoothLostYet(true);
            closeMainDialog();
            hideBluetoothConnectDialog();
            showBluetoothLostDialog();
        }
    }

    private void postVideoBackgroundUrl(String backgroundUrl) {
        if (mMultiTransformation == null) {
            CropTransformation cropTransformation = new CropTransformation(640, 400);
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