package com.chinafocus.hvrskyworthvr.rtr.show;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chinafocus.hvrskyworthvr.GlideApp;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.exo.ExoManager;
import com.chinafocus.hvrskyworthvr.exo.tools.ExoMediaHelper;
import com.chinafocus.hvrskyworthvr.global.ConfigManager;
import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.model.bean.TagHolder;
import com.chinafocus.hvrskyworthvr.model.bean.VideoContentList;
import com.chinafocus.hvrskyworthvr.net.ImageProcess;
import com.chinafocus.hvrskyworthvr.rtr.adapter.ShowRtrVideoListViewAdapter;
import com.chinafocus.hvrskyworthvr.rtr.dialog.RtrAppUpdateDialog;
import com.chinafocus.hvrskyworthvr.rtr.dialog.RtrBluetoothConnectedDialog;
import com.chinafocus.hvrskyworthvr.rtr.dialog.RtrBluetoothLostDialog;
import com.chinafocus.hvrskyworthvr.rtr.dialog.RtrVrModeMainDialog;
import com.chinafocus.hvrskyworthvr.rtr.install.AppInstallViewModel;
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

import java.io.File;
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
import static com.chinafocus.hvrskyworthvr.global.Constants.REQUEST_CODE_PAD_MINE_ACTIVITY;
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
    private View mDividerLine;
    private AppInstallViewModel mAppInstallViewModel;
    private RtrAppUpdateDialog mRtrAppUpdateDialog;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        StatusBarCompatFactory.getInstance().setStatusBarImmerse(this, false);
        setContentView(R.layout.activity_show);

        RtrVideoSubViewModel mViewModel = new ViewModelProvider(this).get(RtrVideoSubViewModel.class);
        mMediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);
        mAppInstallViewModel = new ViewModelProvider(this).get(AppInstallViewModel.class);
        mAppInstallViewModel.register();

        mAppInstallViewModel.checkAppVersionAndUpdate();
        mViewModel.getVideoContentList();

        findViewById(R.id.iv_mine_about).setOnClickListener(v -> TimeOutClickUtil.getDefault().startTimeOutClick(() -> startActivityForResult(new Intent(ShowActivity.this, MineActivity.class), REQUEST_CODE_PAD_MINE_ACTIVITY)));

        mMagicIndicator = findViewById(R.id.magic_Indicator_main_tag);
        mDividerLine = findViewById(R.id.view_divider_line);

        mDiscreteScrollView = findViewById(R.id.rv_main_hot_cover);
        mBackgroundAnimationRelativeLayout = findViewById(R.id.view_background_change_animation);
        mVideoDes = findViewById(R.id.tv_media_des);
        mDiscreteScrollView.setOrientation(DSVOrientation.HORIZONTAL);
        mDiscreteScrollView.setSlideOnFling(true);

        initAppInstallViewModelObserve();

        mViewModel.videoDataMutableLiveData.observe(this, videoContentLists -> {

            if (mAdapter == null) {

                preLoadImage(videoContentLists);
                preLoadAllVideoDetail(videoContentLists);

                setVrSyncPlayInfoTagAndCategory(videoContentLists.get(0));

                mAdapter = new ShowRtrVideoListViewAdapter(videoContentLists);
                mScrollAdapter = InfiniteScrollAdapter.wrap(mAdapter);
                mDiscreteScrollView.addScrollStateChangeListener(new MyScrollStateChangeListener() {
                    @Override
                    public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
                        if (isStartMediaPlayActivity) {
                            return;
                        }
                        // 视频暂停！
                        ExoManager.getInstance().setPlayWhenReady(false);
                        if (!isStartMediaPlayActivity) {
                            Log.d("MyLog", " ---------Pad在【主页面】中，列表开始滑动了，把videoId设置为-1-----");
                            VrSyncPlayInfo.obtain().restoreVideoInfo();
                        }
                        closeTimer(null);
                        mBackgroundAnimationRelativeLayout.removeCallbacks(mMyPostBackGroundRunnable);
                        ((BaseViewHolder) currentItemHolder).getView(R.id.lottie_center_media).setVisibility(View.GONE);
                    }
                });

                mAdapter.setOnClickCallback(adapterPosition -> {
                    if (isStartMediaPlayActivity) {
                        return;
                    }
                    closeTimer(null);
                    // realPosition：在list中，响应点击的实际item位置，0~list.size-1
                    int realPosition = mScrollAdapter.getRealPosition(adapterPosition);
                    // realCurrentPosition：在list中,当前中心点的实际item位置，0~list.size-1
                    int realCurrentPosition = mScrollAdapter.getRealCurrentPosition();

                    if (realCurrentPosition != realPosition && !isStartMediaPlayActivity) {
                        // 视频暂停！
                        ExoManager.getInstance().setPlayWhenReady(false);
                        Log.d("MyLog", " ---------Pad在【主页面】中，非中心的item被点击了，把videoId设置为-1-----");
                        VrSyncPlayInfo.obtain().restoreVideoInfo();
                        // adapterPosition：无线轮播中，recyclerView中的位置，其中Integer.Max/2为起始位置
                        mDiscreteScrollView.smoothScrollToPosition(adapterPosition);
                        return;
                    }

                    isStartMediaPlayActivity = true;

                    VideoContentList videoContentInfo = videoContentLists.get(realPosition);
                    setVrSyncPlayInfoTagAndCategory(videoContentInfo);
                    int videoId = videoContentInfo.getId();

                    Intent intent = new Intent(ShowActivity.this, RtrMediaPlayActivity.class);
                    intent.putExtra(MEDIA_FROM_TAG, VrSyncPlayInfo.obtain().getTag());
                    intent.putExtra(MEDIA_CATEGORY_TAG, VrSyncPlayInfo.obtain().getCategory());
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
                    if (isStartMediaPlayActivity) {
                        return;
                    }
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

    private void initAppInstallViewModelObserve() {
        mAppInstallViewModel.getAppVersionInfoMutableLiveData().observe(this, appVersionInfo -> {

            closeMainDialog();

            if (mRtrAppUpdateDialog == null) {
                mRtrAppUpdateDialog = new RtrAppUpdateDialog(this);
                mRtrAppUpdateDialog.setDownLoadListener(new RtrAppUpdateDialog.DownLoadListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void immediatelyDownLoad() {
                        mAppInstallViewModel.retryDownLoad();
                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void retryDownLoad() {
                        mAppInstallViewModel.retryDownLoad();
                    }

                    @Override
                    public void pauseDownLoad() {
                        mAppInstallViewModel.pauseDownLoad();
                    }

                    @Override
                    public void resumeDownLoad() {
                        mAppInstallViewModel.checkNetWorkAndResumeDownLoad();
                    }

                    @Override
                    public void installApp() {
                        mAppInstallViewModel.installApp();
                    }
                });
                mRtrAppUpdateDialog.setOnDismissListener(dialog -> {
                    mAppInstallViewModel.cancelDownLoad();
                    if (CURRENT_VR_ONLINE_STATUS == VR_STATUS_ONLINE) {
                        showVrModeMainDialog();
                    } else if (CURRENT_VR_ONLINE_STATUS == VR_STATUS_OFFLINE) {
                        fixScrollToPosition(VrSyncPlayInfo.obtain().getVideoId());
                    }
                });
            }
            mRtrAppUpdateDialog.postStatusForce(appVersionInfo.getAutoDownLoad());
            mRtrAppUpdateDialog.postVersionCodeAndDes(appVersionInfo.getVersionName(), appVersionInfo.getVersionIntro());
            mRtrAppUpdateDialog.showUpdatePreUI();
            if (!mRtrAppUpdateDialog.isShowing()) {
                mRtrAppUpdateDialog.show();
            }
        });
        mAppInstallViewModel.getTaskResume().observe(this, aVoid -> {
            if (mRtrAppUpdateDialog != null) {
                mRtrAppUpdateDialog.pauseUpdateRunningButtonUI();
            }
        });
        mAppInstallViewModel.getTaskRunning().observe(this, integer -> {
            if (mRtrAppUpdateDialog != null) {
                mRtrAppUpdateDialog.postTaskRunningProgress(integer);
            }
        });
        mAppInstallViewModel.getTaskComplete().observe(this, aVoid -> {
            if (mRtrAppUpdateDialog != null) {
                mRtrAppUpdateDialog.postTaskComplete();
            }
        });
        mAppInstallViewModel.getTaskFail().observe(this, aVoid -> {
            if (mRtrAppUpdateDialog != null) {
                mRtrAppUpdateDialog.postTaskFail();
            }
        });
        mAppInstallViewModel.getNetWorkError().observe(this, aVoid -> ToastUtils.showShort(ShowActivity.this.getString(R.string.check_network_error)));
    }

    private boolean isAppInstallDialogShow() {
        return mRtrAppUpdateDialog != null && mRtrAppUpdateDialog.isShowing();
    }

    private void setVrSyncPlayInfoTagAndCategory(VideoContentList videoContentInfo) {
        int videoType = -1;
        if (videoContentInfo.getType() == 2) {
            // 全景出版
            videoType = 1;
        } else if (videoContentInfo.getType() == 1) {
            // 全景视频
            videoType = 2;
        }
        VrSyncPlayInfo.obtain().setTag(videoType);
        int videoClassify = Integer.parseInt(videoContentInfo.getClassify());
        VrSyncPlayInfo.obtain().setCategory(videoClassify);
    }

    private boolean isStartMediaPlayActivity;

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
            GlideApp.with(this)
                    .load(ConfigManager.getInstance().getDefaultUrl() + temp.getImgUrl() + ImageProcess.process(600, 400))
                    .preload();
        }
    }

    private void setIndicatorContent(List<VideoContentList> videoContentLists) {
        mTagHolders = new ArrayList<>();

        for (int i = 0; i < videoContentLists.size(); i++) {
            String className = videoContentLists.get(i).getClassName();
            if (!TextUtils.isEmpty(className)) {
                TagHolder tagHolder = new TagHolder(className, i);
                if (!mTagHolders.contains(tagHolder)) {
                    mTagHolders.add(tagHolder);
                }
            }
        }

        if (mTagHolders.size() > 0) {
            CommonNavigatorAdapter commonNavigatorAdapter = new CommonNavigatorAdapter() {

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
            };
            CommonNavigator commonNavigator = new CommonNavigator(this);
            commonNavigator.setAdjustMode(false);
            commonNavigator.setAdapter(commonNavigatorAdapter);
            mMagicIndicator.setNavigator(commonNavigator);

            mDividerLine.setVisibility(View.VISIBLE);
        } else {
            mDividerLine.setVisibility(View.INVISIBLE);
        }

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
//        mScrollAdapter.notifyDataSetChanged();

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

        String menuVideoUrl = videoContentList.getMenuVideoUrl();
        if (!TextUtils.isEmpty(menuVideoUrl)) {
            String previewVideoUrl = ConfigManager.getInstance().getDefaultUrl() + videoContentList.getMenuVideoUrl();
            String[] splitVideoUrl = previewVideoUrl.split("/");
            File file = new File(getExternalFilesDir("preview"), splitVideoUrl[splitVideoUrl.length - 1]);
            if (file.exists()) {
                previewVideoUrl = file.getAbsolutePath();
            }
//            Log.d("MyLog", "-----当前[预览视频]播放地址是 videoUrl >>> " + previewVideoUrl);
            ExoManager.getInstance().prepareSource(getApplicationContext(), previewVideoUrl);
            ExoManager.getInstance().setPlayWhenReady(true);
        }

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
        if (!TextUtils.isEmpty(classify) && !TextUtils.isEmpty(color)) {
            mVideoDes.post(() -> mVideoDes.setContentAndTag("  " + intro, Collections.singletonList(classify), color));
        } else {
            mVideoDes.post(() -> mVideoDes.setText(intro));
        }
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

        isStartMediaPlayActivity = false;
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
//        if (mBluetoothLostDelayTaskRunnable == null) {
//            mBluetoothLostDelayTaskRunnable = new MyBluetoothLostDelayTaskRunnable();
//        }
//        mBackgroundAnimationRelativeLayout.removeCallbacks(mBluetoothLostDelayTaskRunnable);
//        mBackgroundAnimationRelativeLayout.postDelayed(mBluetoothLostDelayTaskRunnable, 6000);
    }

    private void startBluetoothLostTaskImmediately() {
//        if (mBluetoothLostDelayTaskRunnable == null) {
//            mBluetoothLostDelayTaskRunnable = new MyBluetoothLostDelayTaskRunnable();
//        }
//        mBackgroundAnimationRelativeLayout.removeCallbacks(mBluetoothLostDelayTaskRunnable);
//        mBackgroundAnimationRelativeLayout.post(mBluetoothLostDelayTaskRunnable);
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
            Log.e("BluetoothEngineService", "-----在【首页】 收到了蓝牙恢复，需要在【首页】展示【蓝牙恢复页面】-----");
            BluetoothService.getInstance().setBluetoothLostYet(false);
            hideBluetoothLostDialog();
            showBluetoothConnectDialog();
        }
        BluetoothService.getInstance().setBluetoothLostYet(false);
    }


    /**
     * 在首页戴上VR眼镜
     *
     * @param event 戴上VR眼镜事件
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void connectToVR(VrMainConnect event) {
        Log.d("MyLog", "-----在[首页]戴上VR眼镜-----");
        // 1.关闭定时器
        closeTimer(null);
        // 3.给VR同步视频信息，如果当前video==-1的话，VR端需要切换到列表页面
        BluetoothService.getInstance()
                .sendMessage(
                        VrSyncPlayInfo.obtain().getTag(),
                        VrSyncPlayInfo.obtain().getCategory(),
                        VrSyncPlayInfo.obtain().getVideoId(),
                        VrSyncPlayInfo.obtain().getSeekTime()
                );

        if (isAppInstallDialogShow()) {
            return;
        }

        if (VrSyncPlayInfo.obtain().getVideoId() != -1) {
            startSyncMediaPlayActivity();
        } else {
            // 2.展示控制画面
            showVrModeMainDialog();
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
//        Log.d("MyLog", "-----在首页取下VR眼镜-----");
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

        if (!isAppInstallDialogShow()) {
            startSyncMediaPlayActivity();
        }
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
        Log.e("BluetoothEngineService", " ！！！！！！！！！主页面 show 蓝牙断开dialog ");
        if (mRtrBluetoothLostDialog == null) {
            mRtrBluetoothLostDialog = new RtrBluetoothLostDialog(this);
        }

        if (!mRtrBluetoothLostDialog.isShowing()) {
            mRtrBluetoothLostDialog.show();
        }
    }

    private void hideBluetoothLostDialog() {
        if (mRtrBluetoothLostDialog != null && mRtrBluetoothLostDialog.isShowing()) {
            Log.e("BluetoothEngineService", " ！！！！！！！！！主页面 hide  蓝牙断开dialog ");
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
                        Log.d("MyLog", " ---------Pad在【主页面】中，定时器任务执行完毕，把videoId设置为-1-----");
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

        Log.e("MyLog", " resultCode >>> " + resultCode);

        if (resultCode == RESULT_CODE_INACTIVE_DIALOG) {
            hideBluetoothLostDialog();
            hideBluetoothConnectDialog();
            closeMainDialog();
            startTimeTask();
        } else if (resultCode == RESULT_CODE_ACTIVE_DIALOG) {
            hideBluetoothLostDialog();
            hideBluetoothConnectDialog();
            showVrModeMainDialog();
            closeTimer(null);
        } else if (resultCode == RESULT_CODE_SELF_INACTIVE_DIALOG) {
            hideBluetoothLostDialog();
            hideBluetoothConnectDialog();
            VrSyncPlayInfo.obtain().restoreVideoInfo();
            closeTimer(null);
        } else if (resultCode == RESULT_CODE_ACTIVE_BLUETOOTH_LOST) {
            VrSyncPlayInfo.obtain().restoreVideoInfo();
            closeTimer(null);
            startBluetoothLostTaskImmediately();
        } else if (resultCode == RESULT_CODE_ACTIVE_BLUETOOTH_CONNECTED) {
            VrSyncPlayInfo.obtain().restoreVideoInfo();
            closeTimer(null);
            hideBluetoothLostDialog();
            showBluetoothConnectDialog();
        }


        // 修复RecyclerView位置
        if (data != null) {
            int currentVideoId = data.getIntExtra("currentVideoId", Integer.MAX_VALUE / 2);
            fixScrollToPosition(currentVideoId);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void fixScrollToPosition(int videoId) {
        if (mAdapter != null) {
            int crease = mAdapter.calculatePositionFromVideoId(videoId);
            mDiscreteScrollView.scrollToPosition(Integer.MAX_VALUE / 2 + crease);
        }
    }

    /**
     * 关闭定时器
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void closeTimer(VrCancelTimeTask vrCancelTimeTask) {
        if (mDisposable != null && !mDisposable.isDisposed()) {
//            Log.d("MyLog", "-----关闭2分钟定时器-----");
            mDisposable.dispose();
        }
    }


    private void closeMainDialog() {
        if (vrModeMainDialog != null && vrModeMainDialog.isShowing()) {
//            Log.d("MyLog", "-----关闭MainActivity的控制dialog-----");
            vrModeMainDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mMyPostBackGroundRunnable = null;
        mBluetoothLostDelayTaskRunnable = null;
        ExoManager.getInstance().onDestroy();
        ExoMediaHelper.getInstance().onDestroy();
        BluetoothService.getInstance().releaseAll(this);
        mAppInstallViewModel.unRegister();
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
            Log.e("BluetoothEngineService", " ++++++++++在【主页面】 执行了蓝牙断开");
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

        GlideApp.with(this)
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