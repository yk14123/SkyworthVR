package com.chinafocus.hvrskyworthvr.rtr.show;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.rtr.adapter.ShowRtrVideoListViewAdapter;
import com.chinafocus.hvrskyworthvr.rtr.videolist.sub.RtrVideoSubViewModel;
import com.chinafocus.hvrskyworthvr.ui.adapter.BaseViewHolder;
import com.chinafocus.hvrskyworthvr.ui.widget.BackgroundAnimationRelativeLayout;
import com.chinafocus.hvrskyworthvr.ui.widget.transformer.MyCenterScaleTransformer;
import com.chinafocus.hvrskyworthvr.util.statusbar.StatusBarCompatFactory;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;

import jp.wasabeef.glide.transformations.CropTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ShowActivity extends AppCompatActivity {

    private RtrVideoSubViewModel mViewModel;
    private ShowRtrVideoListViewAdapter mAdapter;
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

        DiscreteScrollView discreteScrollView = findViewById(R.id.rv_main_hot_cover);
        mBackgroundAnimationRelativeLayout = findViewById(R.id.view_background_change_animation);
        discreteScrollView.setOrientation(DSVOrientation.HORIZONTAL);
        discreteScrollView.setSlideOnFling(true);

        mViewModel.videoDataMutableLiveData.observe(this, videoContentLists -> {
            if (mAdapter == null) {
                mAdapter = new ShowRtrVideoListViewAdapter(videoContentLists);
                mAdapter.setVideoBackgroundUrl(this::postVideoBackgroundUrl);

                InfiniteScrollAdapter<BaseViewHolder> scrollAdapter
                        = InfiniteScrollAdapter.wrap(mAdapter);

                discreteScrollView.addOnItemChangedListener((viewHolder, adapterPosition) -> {
                    int realPosition = scrollAdapter.getRealPosition(adapterPosition);

                });


                discreteScrollView.setAdapter(scrollAdapter);
                discreteScrollView.setItemTransitionTimeMillis(300);
                discreteScrollView.setItemTransformer(new MyCenterScaleTransformer.Builder()
                        .setMinScale(0.94f)
                        .setMaxScale(1.54f)
                        .build());

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