package com.chinafocus.hvrskyworthvr.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.service.BluetoothService;
import com.chinafocus.hvrskyworthvr.service.event.VrCancelTimeTask;
import com.chinafocus.hvrskyworthvr.service.event.VrMainConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrMainDisConnect;
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

import java.util.Arrays;
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
import static com.chinafocus.hvrskyworthvr.global.Constants.VR_OFFLINE;
import static com.chinafocus.hvrskyworthvr.global.Constants.VR_ONLINE;
import static com.chinafocus.hvrskyworthvr.global.Constants.VR_ONLINE_STATUS;
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

    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBarCompatFactory.getInstance().setStatusBarImmerse(this, true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int currentVRStatus = getIntent().getIntExtra(VR_ONLINE_STATUS, VR_OFFLINE);

        AppCompatImageView publishBg = findViewById(R.id.iv_main_publish_bg);
        AppCompatImageView videoBg = findViewById(R.id.iv_main_video_bg);
        AppCompatImageView aboutBg = findViewById(R.id.iv_main_about_bg);
        ivAboutBg = findViewById(R.id.iv_about_bg);

        imageViewList = Arrays.asList(publishBg, videoBg, aboutBg);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(this);

        initFragments();
        radioGroup.check(R.id.rb_main_video);

        if (currentVRStatus == VR_OFFLINE) {
            disConnectFromVR(null);
        } else if (currentVRStatus == VR_ONLINE) {
//            connectToVR(null);
            // 1.关闭定时器
            closeTimer(null);
            // 2.展示控制画面
            showVrModeMainDialog();
        }
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

    /**
     * 在首页戴上VR眼镜
     *
     * @param event 戴上VR眼镜事件
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void connectToVR(VrMainConnect event) {
        Log.d("MyLog", "-----在首页戴上VR眼镜-----");
        ivAboutBg.post(() -> {
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
        });

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
        Intent intent = new Intent(this, MediaPlayActivity.class);
        intent.putExtra(MEDIA_FROM_TAG, VrSyncPlayInfo.obtain().getTag());
        intent.putExtra(MEDIA_CATEGORY_TAG, VrSyncPlayInfo.obtain().getCategory());
        intent.putExtra(MEDIA_ID, VrSyncPlayInfo.obtain().getVideoId());
        intent.putExtra(MEDIA_SEEK, VrSyncPlayInfo.obtain().getSeekTime());
        intent.putExtra(MEDIA_LINK_VR, true);
        startActivityForResult(intent, REQUEST_CODE_VR_MEDIA_ACTIVITY);
    }

    private void showVrModeMainDialog() {
        if (vrModeMainDialog == null) {
            vrModeMainDialog = new VrModeMainDialog(this);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_CODE_INACTIVE_DIALOG) {
            closeMainDialog();
            startTimeTask();
        } else if (resultCode == RESULT_CODE_ACTIVE_DIALOG) {
            showVrModeMainDialog();
            closeTimer(null);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        closeTimer(null);
        VrSyncPlayInfo.obtain().restoreVideoInfo();

        if (checkedId == R.id.rb_main_publish) {//出版
            switchFragment(mPublishFragment);
            switchBackgroundDrawable(R.id.iv_main_publish_bg);
            setAboutBgShow(false);
            VrSyncPlayInfo.obtain().setTag(1);
        } else if (checkedId == R.id.rb_main_video) { //视频
            switchFragment(mVideoFragment);
            switchBackgroundDrawable(R.id.iv_main_video_bg);
            setAboutBgShow(false);
            VrSyncPlayInfo.obtain().setTag(2);
        } else if (checkedId == R.id.rb_main_about) {//我的
            switchFragment(mAboutFragment);
            switchBackgroundDrawable(R.id.iv_main_about_bg);
            setAboutBgShow(true);
            VrSyncPlayInfo.obtain().setTag(2);
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
