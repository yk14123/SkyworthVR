package com.chinafocus.hvrskyworthvr.rtr.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.model.bean.VideoCategory;
import com.chinafocus.hvrskyworthvr.rtr.dialog.RtrVrModeMainDialog;
import com.chinafocus.hvrskyworthvr.rtr.media.RtrMediaPlayActivity;
import com.chinafocus.hvrskyworthvr.rtr.mine.MineActivity;
import com.chinafocus.hvrskyworthvr.rtr.videolist.sub.RtrVideoSubFragment;
import com.chinafocus.hvrskyworthvr.service.BluetoothService;
import com.chinafocus.hvrskyworthvr.service.event.VrCancelTimeTask;
import com.chinafocus.hvrskyworthvr.service.event.VrMainConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMainDisConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMainSyncMediaInfo;
import com.chinafocus.hvrskyworthvr.service.event.VrSyncPlayInfo;
import com.chinafocus.hvrskyworthvr.ui.widget.BgMediaPlayerViewGroup;
import com.chinafocus.hvrskyworthvr.ui.widget.GradientScaleStarPagerTitleView;
import com.chinafocus.hvrskyworthvr.util.ViewClickUtil;
import com.chinafocus.hvrskyworthvr.util.statusbar.StatusBarCompatFactory;
import com.chinafocus.hvrskyworthvr.util.widget.BaseFragmentStateAdapter;
import com.chinafocus.hvrskyworthvr.util.widget.ViewPager2Helper;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.chinafocus.hvrskyworthvr.global.Constants.REQUEST_CODE_VR_MEDIA_ACTIVITY;
import static com.chinafocus.hvrskyworthvr.global.Constants.RESULT_CODE_ACTIVE_DIALOG;
import static com.chinafocus.hvrskyworthvr.global.Constants.RESULT_CODE_INACTIVE_DIALOG;
import static com.chinafocus.hvrskyworthvr.global.Constants.RESULT_CODE_SELF_INACTIVE_DIALOG;
import static com.chinafocus.hvrskyworthvr.rtr.media.RtrMediaPlayActivity.MEDIA_CATEGORY;
import static com.chinafocus.hvrskyworthvr.rtr.media.RtrMediaPlayActivity.MEDIA_ID;
import static com.chinafocus.hvrskyworthvr.rtr.media.RtrMediaPlayActivity.MEDIA_LINK_VR;
import static com.chinafocus.hvrskyworthvr.rtr.media.RtrMediaPlayActivity.MEDIA_SEEK;
import static com.chinafocus.hvrskyworthvr.rtr.media.RtrMediaPlayActivity.MEDIA_TYPE;
import static com.chinafocus.hvrskyworthvr.service.BluetoothService.CURRENT_VR_ONLINE_STATUS;
import static com.chinafocus.hvrskyworthvr.service.BluetoothService.VR_STATUS_OFFLINE;
import static com.chinafocus.hvrskyworthvr.service.BluetoothService.VR_STATUS_ONLINE;


public class RtrMainActivity extends AppCompatActivity {

    private BgMediaPlayerViewGroup mBgMediaPlayerViewGroup;

    private Disposable mDisposable;
    private RtrVrModeMainDialog vrModeMainDialog;
    private List<RtrVideoSubFragment> mFragments;
    private List<Integer> mCategories;
    private ViewPager2 mViewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompatFactory.getInstance().setStatusBarImmerse(this, false);
        setContentView(R.layout.activity_rtr_main);

        MagicIndicator magicIndicator = findViewById(R.id.magic_Indicator);
        mViewPager2 = findViewById(R.id.vp_video_list);
        mBgMediaPlayerViewGroup = findViewById(R.id.view_bg_media_player);
        ViewClickUtil.click(findViewById(R.id.iv_mine_about), () -> startActivity(new Intent(RtrMainActivity.this, MineActivity.class)));
        mViewPager2.setUserInputEnabled(false);

        RtrMainViewModel mViewModel = new ViewModelProvider(this).get(RtrMainViewModel.class);
        mViewModel.getVideoDetailData();

        mViewModel.videoDetailMutableLiveData.observe(this, videoCategories -> {

            CommonNavigator commonNavigator = new CommonNavigator(this);
            commonNavigator.setAdjustMode(false);
            commonNavigator.setAdapter(new CommonNavigatorAdapter() {

                @Override
                public int getCount() {
                    return videoCategories.size();
                }

                @Override
                public IPagerTitleView getTitleView(Context context, int index) {
                    GradientScaleStarPagerTitleView scaleTitleView = new GradientScaleStarPagerTitleView(context);
                    scaleTitleView.setTextSize(23);
                    scaleTitleView.setMinScale(0.7f);
                    scaleTitleView.setText(videoCategories.get(index).getName());
                    scaleTitleView.setOnClickListener(view
                            -> {
                        closeTimer(null);
                        VrSyncPlayInfo.obtain().restoreVideoInfo();
                        mViewPager2.setCurrentItem(index, false);
                    });

                    return scaleTitleView;
                }

                @Override
                public IPagerIndicator getIndicator(Context context) {
                    return null;
                }
            });

            magicIndicator.setNavigator(commonNavigator);

            mFragments = new ArrayList<>();
            mCategories = new ArrayList<>();

            for (VideoCategory videoCategory : videoCategories) {
                RtrVideoSubFragment videoListFragment = RtrVideoSubFragment.newInstance(videoCategory.getId() + "");
                mFragments.add(videoListFragment);
                mCategories.add(videoCategory.getId());
            }

            BaseFragmentStateAdapter<RtrVideoSubFragment> adapter = new BaseFragmentStateAdapter<>(this, mFragments);
            mViewPager2.setAdapter(adapter);

            ViewPager2Helper.bind(magicIndicator, mViewPager2);
        });

    }

    public void postVideoBgAndMenuVideoUrl(String bg, String url) {
        mBgMediaPlayerViewGroup.postVideoBgAndMenuVideoUrl(bg, url);
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
            // 3.暂停播放视频
            mBgMediaPlayerViewGroup.onConnect(true);
        }
        Constants.ACTIVITY_TAG = Constants.ACTIVITY_MAIN;

    }

    @Override
    protected void onPause() {
        super.onPause();
        mBgMediaPlayerViewGroup.onConnect(true);
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

        mBgMediaPlayerViewGroup.onConnect(true);

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
        mBgMediaPlayerViewGroup.onConnect(false);
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
        intent.putExtra(MEDIA_TYPE, VrSyncPlayInfo.obtain().getTag());
        intent.putExtra(MEDIA_CATEGORY, VrSyncPlayInfo.obtain().getCategory());
        intent.putExtra(MEDIA_ID, VrSyncPlayInfo.obtain().getVideoId());
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
            mBgMediaPlayerViewGroup.onConnect(false);
            closeMainDialog();
            startTimeTask();
        } else if (resultCode == RESULT_CODE_ACTIVE_DIALOG) {
            mBgMediaPlayerViewGroup.onConnect(true);
            showVrModeMainDialog();
            closeTimer(null);
        } else if (resultCode == RESULT_CODE_SELF_INACTIVE_DIALOG) {
            // 修复RecyclerView位置
//            ((RtrVideoSubFragment) mFragments.get(0)).setItemPosition(VrSyncPlayInfo.obtain().getVideoId());
            mBgMediaPlayerViewGroup.onConnect(false);
            VrSyncPlayInfo.obtain().restoreVideoInfo();
            closeTimer(null);
        }

        if (data != null) {
            int videoId = data.getIntExtra(MEDIA_ID, -1);
            int videoType = data.getIntExtra(MEDIA_TYPE, -1);
            int category = data.getIntExtra(MEDIA_CATEGORY, -1);

            if (mCategories != null && mCategories.size() > 0) {
                for (int i = 0; i < mCategories.size(); i++) {
                    if (mCategories.get(i).equals(category)) {
                        RtrVideoSubFragment temp = mFragments.get(i);
                        temp.selectedItem(videoId, videoType);
                        mViewPager2.setCurrentItem(i, false);
                        return;
                    }
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
        BluetoothService.getInstance().releaseAll(this);
    }
}