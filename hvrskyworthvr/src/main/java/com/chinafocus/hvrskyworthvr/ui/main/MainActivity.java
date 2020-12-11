package com.chinafocus.hvrskyworthvr.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.service.event.VrConnect;
import com.chinafocus.hvrskyworthvr.service.event.VrDisConnect;
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
        radioGroup.check(R.id.rb_main_publish);


    }

    private VrModeMainDialog vrModeMainDialog;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showVRMode(VrConnect event) {
        if (vrModeMainDialog == null) {
            vrModeMainDialog = new VrModeMainDialog(this);
        }
        if (!vrModeMainDialog.isShowing()) {
            vrModeMainDialog.show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void hideVRMode(VrDisConnect event) {
        if (vrModeMainDialog != null) {
            vrModeMainDialog.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void goToMediaPlayActivityAndActiveVRPlayerStatus(VrSyncPlayInfo event) {
        Intent intent = new Intent(this, MediaPlayActivity.class);
        intent.putExtra(MEDIA_ID, event.videoId);
        intent.putExtra(MEDIA_FROM_TAG, event.tag);
//        intent.putExtra(MEDIA_CATEGORY_TAG, video_tag);
        intent.putExtra(MEDIA_SEEK, event.seek);
        intent.putExtra(MEDIA_LINK_VR, true);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rb_main_publish) {//快讯
            switchFragment(mPublishFragment);
            switchBackgroundDrawable(R.id.iv_main_publish_bg);
            setAboutBgShow(false);
        } else if (checkedId == R.id.rb_main_video) { //全景
            switchFragment(mVideoFragment);
            switchBackgroundDrawable(R.id.iv_main_video_bg);
            setAboutBgShow(false);
        } else if (checkedId == R.id.rb_main_about) {//杂志
            switchFragment(mAboutFragment);
            switchBackgroundDrawable(R.id.iv_main_about_bg);
            setAboutBgShow(true);
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
