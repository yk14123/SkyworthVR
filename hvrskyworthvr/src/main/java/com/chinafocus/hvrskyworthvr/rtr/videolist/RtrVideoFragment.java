package com.chinafocus.hvrskyworthvr.rtr.videolist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.rtr.videolist.sub.RtrVideoSubFragment;
import com.chinafocus.hvrskyworthvr.util.widget.BaseFragmentStateAdapter;
import com.chinafocus.hvrskyworthvr.util.widget.ViewPager2Helper;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.ArrayList;
import java.util.List;

public class RtrVideoFragment extends Fragment {

    private RtrVideoViewModel mViewModel;

    public static RtrVideoFragment newInstance() {
        return new RtrVideoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rtr_video_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RtrVideoViewModel.class);
        // TODO: Use the ViewModel

        MagicIndicator magicIndicator = requireView().findViewById(R.id.magic_Indicator_sub_list);
        ViewPager2 viewPagerVideoList = requireView().findViewById(R.id.vp_video_sub_list);
        viewPagerVideoList.setUserInputEnabled(false);

//        String[] strings = {"最美中国", "世界园林", "动物王国", "科普知识", "更多欣赏"};
        String[] strings = {};

        List<RtrVideoSubFragment> fragments = new ArrayList<>();
//        for (String temp : strings) {
        RtrVideoSubFragment videoListFragment = RtrVideoSubFragment.newInstance();
        fragments.add(videoListFragment);
//        }

        CommonNavigator commonNavigator = new CommonNavigator(requireActivity());
        commonNavigator.setAdjustMode(false);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return strings.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setTextSize(13);
                colorTransitionPagerTitleView.setPadding(40, 0, 40, 0);
                colorTransitionPagerTitleView.setNormalColor(getResources().getColor(R.color.color_white_a40));
                colorTransitionPagerTitleView.setSelectedColor(getResources().getColor(R.color.color_white));
                colorTransitionPagerTitleView.setText(strings[index]);
                colorTransitionPagerTitleView.setOnClickListener(view
                        -> viewPagerVideoList.setCurrentItem(index, false));
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                WrapPagerIndicator indicator = new WrapPagerIndicator(context);
                indicator.setFillColor(getResources().getColor(R.color.color_white_a20));
                indicator.setRoundRadius(78);
                indicator.setHorizontalPadding(40);
                indicator.setVerticalPadding(24);
                return indicator;
            }
        });

        magicIndicator.setNavigator(commonNavigator);

        BaseFragmentStateAdapter<RtrVideoSubFragment> adapter = new BaseFragmentStateAdapter<>(this, fragments);
        viewPagerVideoList.setAdapter(adapter);

        ViewPager2Helper.bind(magicIndicator, viewPagerVideoList);

    }

}