package com.chinafocus.hvrskyworthvr.ui.main.publish;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.ui.adapter.BannerViewAdapter;
import com.chinafocus.hvrskyworthvr.ui.main.BannerViewModel;
import com.chinafocus.hvrskyworthvr.ui.adapter.VideoListAdapter;
import com.chinafocus.hvrskyworthvr.util.MyRollHandler;


import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL;

public class PublishFragment extends Fragment {

    private MyRollHandler mHandler;
    private ViewPager2 viewPagerBanner;
    private int currentItem = 1;

    public static PublishFragment newInstance() {
        return new PublishFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_publish, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewPagerBanner = requireView().findViewById(R.id.vp_publish_banner_root);
        viewPagerBanner.setOrientation(ORIENTATION_HORIZONTAL);
        viewPagerBanner.setOffscreenPageLimit(3);

        BannerViewModel bannerViewModel = new ViewModelProvider(this).get(BannerViewModel.class);
        bannerViewModel.getDefaultCloudUrl();
        bannerViewModel.bannerMutableLiveData.observe(getViewLifecycleOwner(), bannerList -> {
            viewPagerBanner.setAdapter(new BannerViewAdapter(bannerList));
            viewPagerBanner.setCurrentItem(currentItem, false);
            startRollHandler();
        });

        RecyclerView recyclerView = requireView().findViewById(R.id.rv_publish_list);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        PublishViewModel publishViewModel = new ViewModelProvider(this).get(PublishViewModel.class);
        publishViewModel.getVideoListData();
        publishViewModel.videoListDataMutableLiveData.observe(getViewLifecycleOwner(), videoListData -> recyclerView.setAdapter(new VideoListAdapter(videoListData.getList(), 1)));

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