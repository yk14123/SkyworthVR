package com.chinafocus.hvrskyworthvr.rtr.main;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.rtr.videolist.RtrVideoFragment;
import com.chinafocus.hvrskyworthvr.ui.widget.ScaleTransitionPagerTitleView;
import com.chinafocus.hvrskyworthvr.util.statusbar.StatusBarCompatFactory;
import com.chinafocus.hvrskyworthvr.util.widget.BaseFragmentStateAdapter;
import com.chinafocus.hvrskyworthvr.util.widget.ViewPager2Helper;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;

import java.util.ArrayList;
import java.util.List;

public class RtrMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompatFactory.getInstance().setStatusBarImmerse(this, false);
        setContentView(R.layout.activity_rtr_main);

        MagicIndicator magicIndicator = findViewById(R.id.magic_Indicator);
        ViewPager2 viewPagerVideoList = findViewById(R.id.vp_video_list);
        viewPagerVideoList.setUserInputEnabled(false);

        List<RtrVideoFragment> fragments = new ArrayList<>();

//        String[] strings = {"全景出版", "全景视频"};
        String[] strings = {};
//        for (int i = 0; i < strings.length; i++) {
        RtrVideoFragment videoListFragment = RtrVideoFragment.newInstance();
        fragments.add(videoListFragment);
//        }

        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(false);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return strings.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ScaleTransitionPagerTitleView scaleTitleView = new ScaleTransitionPagerTitleView(context);
                scaleTitleView.setTextSize(23);
                scaleTitleView.setMinScale(0.7f);
                scaleTitleView.setNormalColor(getResources().getColor(R.color.color_white_a60));
                scaleTitleView.setSelectedColor(getResources().getColor(R.color.color_white));
                scaleTitleView.setText(strings[index]);
                scaleTitleView.setOnClickListener(view
                        -> viewPagerVideoList.setCurrentItem(index, false));
                return scaleTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }
        });

        magicIndicator.setNavigator(commonNavigator);

        BaseFragmentStateAdapter<RtrVideoFragment> adapter = new BaseFragmentStateAdapter<>(this, fragments);
        viewPagerVideoList.setAdapter(adapter);

        ViewPager2Helper.bind(magicIndicator, viewPagerVideoList);

    }
}