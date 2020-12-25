package com.chinafocus.hvrskyworthvr.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.service.BluetoothService;
import com.chinafocus.hvrskyworthvr.service.SocketService;
import com.chinafocus.hvrskyworthvr.service.event.VrCancelTimeTask;
import com.chinafocus.hvrskyworthvr.service.event.VrMainStickyActiveDialog;
import com.chinafocus.hvrskyworthvr.service.event.VrMainConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMainDisConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMainStickyInactiveDialog;
import com.chinafocus.hvrskyworthvr.service.event.VrMainSyncMediaInfo;
import com.chinafocus.hvrskyworthvr.service.event.VrSyncPlayInfo;
import com.chinafocus.hvrskyworthvr.ui.dialog.VrModeMainDialog;
import com.chinafocus.hvrskyworthvr.ui.main.about.AboutFragment;
import com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity;
import com.chinafocus.hvrskyworthvr.ui.main.publish.PublishFragment;
import com.chinafocus.hvrskyworthvr.ui.main.video.VideoFragment;
import com.chinafocus.hvrskyworthvr.util.statusbar.StatusBarCompatFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity.MEDIA_CATEGORY_TAG;
import static com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity.MEDIA_FROM_TAG;
import static com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity.MEDIA_ID;
import static com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity.MEDIA_LINK_VR;
import static com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity.MEDIA_SEEK;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private List<AppCompatImageView> imageViewList;
    //Fragment管理器
    private FragmentManager mFragmentManager;
    //组件
    private Fragment mCurrentFragment;
    private Fragment mPublishFragment;
    private Fragment mVideoFragment;
    private Fragment mAboutFragment;
    private AppCompatImageView ivAboutBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBarCompatFactory.getInstance().setStatusBarImmerse(this, true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatImageView publishBg = findViewById(R.id.iv_main_publish_bg);
        AppCompatImageView videoBg = findViewById(R.id.iv_main_video_bg);
        AppCompatImageView aboutBg = findViewById(R.id.iv_main_about_bg);
        ivAboutBg = findViewById(R.id.iv_about_bg);

        imageViewList = Arrays.asList(publishBg, videoBg, aboutBg);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(this);

        initFragments();
        radioGroup.check(R.id.rb_main_video);

        BluetoothService.getInstance().startBluetoothEngine(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothService.getInstance().releaseAll(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.ACTIVITY_TAG = Constants.ACTIVITY_MAIN;

    }

    private VrModeMainDialog vrModeMainDialog;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showVRMode(VrMainConnect event) {
        Log.e("MyLog", "MainActivity >>> VrMainConnect");

        if (vrModeMainDialog == null) {
            vrModeMainDialog = new VrModeMainDialog(this);
        }
        if (!vrModeMainDialog.isShowing()) {
            vrModeMainDialog.show();
        }
        // TODO 1.给VR同步视频信息
//        Intent intent = new Intent(this, SocketService.class);
//        intent.putExtra(MEDIA_FROM_TAG, VrSyncPlayInfo.obtain().tag);
//        intent.putExtra(MEDIA_ID, VrSyncPlayInfo.obtain().videoId);
//        intent.putExtra(MEDIA_CATEGORY_TAG, VrSyncPlayInfo.obtain().category);
//        intent.putExtra(MEDIA_SEEK, VrSyncPlayInfo.obtain().seek);
//        startService(intent);

        BluetoothService.getInstance()
                .sendMessage(
                        VrSyncPlayInfo.obtain().tag,
                        VrSyncPlayInfo.obtain().videoId,
                        VrSyncPlayInfo.obtain().category,
                        VrSyncPlayInfo.obtain().seek
                );

        if (VrSyncPlayInfo.obtain().videoId != -1) {
            closeMainDialog();
            startSyncMediaPlayActivity();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void showVRModeSticky(VrMainStickyActiveDialog event) {
        Log.e("MyLog", "MainActivity >>> VrMainStickyActiveDialog");

        if (vrModeMainDialog == null) {
            vrModeMainDialog = new VrModeMainDialog(this);
        }
        if (!vrModeMainDialog.isShowing()) {
            vrModeMainDialog.show();
        }

    }

    private Disposable mDisposable;

    /**
     * 5分钟之后，视频重新选择
     */
    private void startTimeTask() {
        Observable.timer(5, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(Long value) {
                        //Log.d("Timer",""+value);
                        VrSyncPlayInfo.obtain().videoId = -1;
                        VrSyncPlayInfo.obtain().seek = 0L;
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * 关闭定时器
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeTimer(VrCancelTimeTask vrCancelTimeTask) {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void hideVRModeSticky(VrMainStickyInactiveDialog event) {
        Log.e("MyLog", " MainActivity VrMainStickyInactiveDialog");
        ivAboutBg.postDelayed(this::closeMainDialog, 300);
        startTimeTask();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void hideVRMode(VrMainDisConnect event) {
        closeMainDialog();
        startTimeTask();
    }

    private void closeMainDialog() {
        Log.e("MyLog", "MainActivity closeMainDialog before");
        if (vrModeMainDialog != null) {
            Log.e("MyLog", "MainActivity closeMainDialog doing");
            vrModeMainDialog.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void goToMediaPlayActivityAndActiveVRPlayerStatus(VrMainSyncMediaInfo vrMainSyncMediaInfo) {
        closeMainDialog();
        startSyncMediaPlayActivity();
    }

    private void startSyncMediaPlayActivity() {
        Intent intent = new Intent(this, MediaPlayActivity.class);
        intent.putExtra(MEDIA_FROM_TAG, VrSyncPlayInfo.obtain().tag);
        intent.putExtra(MEDIA_CATEGORY_TAG, VrSyncPlayInfo.obtain().category);
        intent.putExtra(MEDIA_ID, VrSyncPlayInfo.obtain().videoId);
        intent.putExtra(MEDIA_SEEK, VrSyncPlayInfo.obtain().seek);
        intent.putExtra(MEDIA_LINK_VR, true);
        startActivity(intent);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rb_main_publish) {//出版
            switchFragment(mPublishFragment);
            switchBackgroundDrawable(R.id.iv_main_publish_bg);
            setAboutBgShow(false);
            VrSyncPlayInfo.obtain().tag = 1;
            VrSyncPlayInfo.obtain().videoId = -1;
        } else if (checkedId == R.id.rb_main_video) { //视频
            switchFragment(mVideoFragment);
            switchBackgroundDrawable(R.id.iv_main_video_bg);
            setAboutBgShow(false);
            VrSyncPlayInfo.obtain().tag = 2;
            VrSyncPlayInfo.obtain().videoId = -1;
        } else if (checkedId == R.id.rb_main_about) {//我的
            switchFragment(mAboutFragment);
            switchBackgroundDrawable(R.id.iv_main_about_bg);
            setAboutBgShow(true);
            VrSyncPlayInfo.obtain().tag = 1;
            VrSyncPlayInfo.obtain().videoId = -1;
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
    protected void onPause() {
        super.onPause();
    }

    private void setAboutBgShow(boolean show) {
        ivAboutBg.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 切换背景球的显示
     *
     * @param id 选中ID
     */
    private void switchBackgroundDrawable(int id) {
        for (AppCompatImageView appCompatImageView : imageViewList) {
            int imageViewId = appCompatImageView.getId();
            if (imageViewId == id) {
                appCompatImageView.setVisibility(View.VISIBLE);
            } else {
                appCompatImageView.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 初始化Fragment
     */
    private void initFragments() {
        mFragmentManager = getSupportFragmentManager();
        Fragment publishFragment = mFragmentManager.findFragmentByTag(PublishFragment.class.getSimpleName());
        if (publishFragment instanceof PublishFragment) {
            mPublishFragment = publishFragment;
        } else {
            mPublishFragment = PublishFragment.newInstance();
        }

        Fragment videoFragment = mFragmentManager.findFragmentByTag(VideoFragment.class.getSimpleName());
        if (videoFragment instanceof VideoFragment) {
            mVideoFragment = videoFragment;
        } else {
            mVideoFragment = VideoFragment.newInstance();
        }

        Fragment aboutFragment = mFragmentManager.findFragmentByTag(AboutFragment.class.getSimpleName());
        if (aboutFragment instanceof AboutFragment) {
            mAboutFragment = aboutFragment;
        } else {
            mAboutFragment = AboutFragment.newInstance();
        }

    }

    /**
     * 切换fragment模块
     *
     * @param targetFragment 目标fragment
     */
    private void switchFragment(Fragment targetFragment) {
        if (targetFragment == null || targetFragment == mCurrentFragment) {
            return;
        }
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        //当前视图未添加,首次装载
        if (mCurrentFragment != null) {
            transaction.hide(mCurrentFragment);
        }
        //判断当前的target视图是否已经添加
        if (targetFragment.isAdded()) {
            //当前目标已经添加过，只需要隐藏当前视图并展示target视图即可
            transaction.show(targetFragment);
        } else {
            transaction.add(R.id.fl_container, targetFragment,
                    targetFragment.getClass().getSimpleName());
        }
        transaction.commit();
        mCurrentFragment = targetFragment;
    }
}
