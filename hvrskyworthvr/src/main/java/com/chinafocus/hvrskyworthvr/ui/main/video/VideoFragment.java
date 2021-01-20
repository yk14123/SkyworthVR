package com.chinafocus.hvrskyworthvr.ui.main.video;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.model.bean.VideoCategory;
import com.chinafocus.hvrskyworthvr.service.event.VrSyncPlayInfo;
import com.chinafocus.hvrskyworthvr.ui.adapter.BannerViewAdapter;
import com.chinafocus.hvrskyworthvr.ui.main.BannerViewModel;
import com.chinafocus.hvrskyworthvr.ui.main.video.sublist.VideoListFragment;
import com.chinafocus.hvrskyworthvr.ui.widget.GradientLinePagerIndicator;
import com.chinafocus.hvrskyworthvr.util.MyRollHandler;
import com.chinafocus.hvrskyworthvr.util.widget.BaseFragmentStateAdapter;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL;

public class VideoFragment extends Fragment {

    private MyRollHandler mHandler;
    private ViewPager2 viewPagerBanner;
    private int currentItem = 1;
    private ViewPager2 viewPagerVideoList;

    public static int CURRENT_CATEGORY;

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewPagerBanner = requireView().findViewById(R.id.vp_video_banner_root);
        viewPagerBanner.setOrientation(ORIENTATION_HORIZONTAL);
        viewPagerBanner.setOffscreenPageLimit(3);

        BannerViewModel bannerViewModel = new ViewModelProvider(this).get(BannerViewModel.class);
        bannerViewModel.getVideoBanner();
        bannerViewModel.videoBannerMutableLiveData.observe(getViewLifecycleOwner(), bannerList -> {
            viewPagerBanner.setAdapter(new BannerViewAdapter(bannerList, 2));
            setViewPager2ScrollTouchSlop(viewPagerBanner, 1);
            viewPagerBanner.setCurrentItem(1, false);
            startRollHandler();
        });

        MagicIndicator magicIndicator = requireView().findViewById(R.id.magic_Indicator);
        viewPagerVideoList = requireView().findViewById(R.id.vp_video_list);


        VideoViewModel videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);
        videoViewModel.getVideoCategory();
        videoViewModel.videoCategoryMutableLiveData.observe(getViewLifecycleOwner(), videoCategories -> {

            CommonNavigator commonNavigator = new CommonNavigator(requireContext());
            commonNavigator.setAdjustMode(true);
            commonNavigator.setAdapter(new CommonNavigatorAdapter() {

                @Override
                public int getCount() {
                    return videoCategories.size();
                }

                @Override
                public IPagerTitleView getTitleView(Context context, final int index) {
                    ColorTransitionPagerTitleView scaleTitleView = new ColorTransitionPagerTitleView(context);
                    scaleTitleView.setTextSize(20);
                    scaleTitleView.setNormalColor(getResources().getColor(R.color.color_333));
                    scaleTitleView.setSelectedColor(getResources().getColor(R.color.color_test_checked));
                    scaleTitleView.setText(videoCategories.get(index).getName());
                    scaleTitleView.setOnClickListener(view
                            -> viewPagerVideoList.setCurrentItem(index, false));
                    return scaleTitleView;
                }

                @Override
                public IPagerIndicator getIndicator(Context context) {
                    GradientLinePagerIndicator indicator = new GradientLinePagerIndicator(context);
                    indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                    indicator.setLineWidth(53 * 2.5f);
                    indicator.setRoundRadius(UIUtil.dip2px(context, 3));
//                    indicator.setColors(getResources().getColor(R.color.color_indicator));
                    int[] colors = {
                            getResources().getColor(R.color.color_ok_enable_start),
                            getResources().getColor(R.color.color_ok_enable_end)
                    };
                    indicator.setGradientColors(colors);
                    return indicator;
                }
            });

            magicIndicator.setNavigator(commonNavigator);

            List<VideoListFragment> fragments = new ArrayList<>();

            for (VideoCategory temp : videoCategories) {
                VideoListFragment videoListFragment = VideoListFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putInt(VideoListFragment.VIDEO_LIST_CATEGORY, temp.getId());
                videoListFragment.setArguments(bundle);
                fragments.add(videoListFragment);
            }

            BaseFragmentStateAdapter<VideoListFragment> adapter = new BaseFragmentStateAdapter<>(this, fragments);
            viewPagerVideoList.setAdapter(adapter);
            setViewPager2ScrollTouchSlop(viewPagerVideoList, 150);

            CURRENT_CATEGORY = videoCategories.get(0).getId();
            VrSyncPlayInfo.obtain().setCategory(CURRENT_CATEGORY);
            VrSyncPlayInfo.obtain().setTag(2);
//            ViewPager2Helper.bind(magicIndicator, viewPagerVideoList);
            viewPagerVideoList.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    VrSyncPlayInfo.obtain().restoreVideoInfo();
                    VrSyncPlayInfo.obtain().setTag(2);
                }

                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    magicIndicator.onPageSelected(position);

                    CURRENT_CATEGORY = videoCategories.get(position).getId();
                    VrSyncPlayInfo.obtain().setCategory(CURRENT_CATEGORY);
                    VrSyncPlayInfo.obtain().restoreVideoInfo();
                    VrSyncPlayInfo.obtain().setTag(2);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    super.onPageScrollStateChanged(state);
                    magicIndicator.onPageScrollStateChanged(state);
                }
            });

        });

    }

    private void setViewPager2ScrollTouchSlop(ViewPager2 viewPager2, int touchSlop) {
        try {
            Field mRecyclerView = ViewPager2.class.getDeclaredField("mRecyclerView");
            mRecyclerView.setAccessible(true);
            Object recyclerView = mRecyclerView.get(viewPager2);

            Field mTouchSlop = RecyclerView.class.getDeclaredField("mTouchSlop");
            mTouchSlop.setAccessible(true);
            mTouchSlop.setInt(recyclerView, touchSlop);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startRollHandler() {
        if (mHandler == null) {
            mHandler = new MyRollHandler(Looper.getMainLooper());
            mHandler.bindViewPager2(viewPagerBanner);
        }
        mHandler.postCurrentItem(currentItem);
        mHandler.start();
    }

    private void cancelRollHandler() {
        currentItem = viewPagerBanner.getCurrentItem();
        if (mHandler != null) {
            mHandler.cancel();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            cancelRollHandler();
        } else {
            startRollHandler();
        }
    }

}