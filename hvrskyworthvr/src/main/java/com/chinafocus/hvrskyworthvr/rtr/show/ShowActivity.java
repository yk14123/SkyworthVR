package com.chinafocus.hvrskyworthvr.rtr.show;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.rtr.adapter.ShowRtrVideoListViewAdapter;
import com.chinafocus.hvrskyworthvr.rtr.videolist.sub.RtrVideoSubViewModel;
import com.chinafocus.hvrskyworthvr.ui.widget.BackgroundAnimationRelativeLayout;
import com.chinafocus.hvrskyworthvr.util.ScalePageTransformer;
import com.chinafocus.hvrskyworthvr.util.statusbar.StatusBarCompatFactory;

import jp.wasabeef.glide.transformations.CropTransformation;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ShowActivity extends AppCompatActivity {

    private RtrVideoSubViewModel mViewModel;
    private ShowRtrVideoListViewAdapter mAdapter;
    private ViewPager2 mViewPager2;
    private BackgroundAnimationRelativeLayout mBackgroundAnimationRelativeLayout;

    private CropTransformation mContentBgTransformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompatFactory.getInstance().setStatusBarImmerse(this, false);
        setContentView(R.layout.activity_show);

        mViewModel = new ViewModelProvider(this).get(RtrVideoSubViewModel.class);
        // TODO: Use the ViewModel
        mViewModel.getVideoContentList();

        mViewPager2 = findViewById(R.id.vp2_video_list_detail);
        mBackgroundAnimationRelativeLayout = findViewById(R.id.view_background_change_animation);

        mViewPager2.setOrientation(ORIENTATION_HORIZONTAL);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(10));
//        compositePageTransformer.addTransformer(new TransFormer());
//        pager2.setPageTransformer(compositePageTransformer);

//        mViewPager2.setPageTransformer(new MarginPageTransformer(-1000));

        mViewPager2.setOffscreenPageLimit(1);
        RecyclerView recyclerView = (RecyclerView) mViewPager2.getChildAt(0);

        int screenWidth = ScreenUtils.getScreenWidth();
        int padding = (int) (screenWidth * 0.3);
        recyclerView.setPadding(padding, 0, padding, 0);
        recyclerView.setClipToPadding(false);



        int pageWidth = screenWidth - padding - padding;
        float min = (-1f * (pageWidth - padding) / pageWidth);
        float max = padding * 1f / pageWidth + 1;
        float translationX = padding * 0.36f;


        mViewPager2.setPageTransformer(new ScalePageTransformer(min, max, translationX));



        mViewModel.videoDataMutableLiveData.observe(this, videoContentLists -> {
            if (mAdapter == null) {
                mAdapter = new ShowRtrVideoListViewAdapter(videoContentLists);
                mAdapter.setVideoBackgroundUrl(this::postVideoBackgroundUrl);
                mViewPager2.setAdapter(mAdapter);
            }
//            mViewPager2.post(() -> {
//                BaseViewHolder holder = (BaseViewHolder) mViewPager2.getRootView().findViewHolderForAdapterPosition(0);
//                mAdapter.selectedItem(0, holder);
//            });

        });
    }

    private void postVideoBackgroundUrl(String backgroundUrl) {
        if (mContentBgTransformation == null) {
            mContentBgTransformation = new CropTransformation(2560, 1600);
        }

        Glide.with(this)
                .load(backgroundUrl)
                .apply(bitmapTransform(mContentBgTransformation))
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