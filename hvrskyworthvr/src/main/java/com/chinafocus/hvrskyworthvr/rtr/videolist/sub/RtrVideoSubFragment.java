package com.chinafocus.hvrskyworthvr.rtr.videolist.sub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.rtr.adapter.RecyclerViewItemUpAnimator;
import com.chinafocus.hvrskyworthvr.rtr.adapter.RtrVideoListViewAdapter;
import com.chinafocus.hvrskyworthvr.rtr.main.RtrMainActivity;
import com.chinafocus.hvrskyworthvr.ui.adapter.BaseViewHolder;
import com.chinafocus.hvrskyworthvr.ui.widget.VideoInfoViewGroup;

import java.util.Objects;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;

public class RtrVideoSubFragment extends Fragment {

    private RtrVideoSubViewModel mViewModel;
    private RtrVideoListViewAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private static final String VIDEO_LIST_CATEGORY = "video_list_category";
    private String mCategory;

    public static RtrVideoSubFragment newInstance(String category) {
        RtrVideoSubFragment rtrVideoSubFragment = new RtrVideoSubFragment();
        Bundle bundle = new Bundle();
        bundle.putString(VIDEO_LIST_CATEGORY, category);
        rtrVideoSubFragment.setArguments(bundle);
        return rtrVideoSubFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rtr_video_sub_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mCategory = arguments.getString(VIDEO_LIST_CATEGORY);

            mViewModel = new ViewModelProvider(this).get(RtrVideoSubViewModel.class);

            VideoInfoViewGroup videoInfoViewGroup = requireView().findViewById(R.id.view_video_info);

            mRecyclerView = requireView().findViewById(R.id.vp_video_list_detail);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), HORIZONTAL, false);
            // 当页面退出的时候 调用onViewDetachedFromWindow
//        linearLayoutManager.setRecycleChildrenOnDetach(true);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mViewModel.videoDataMutableLiveData.observe(getViewLifecycleOwner(), videoContentLists -> {
                if (mAdapter == null) {
                    mAdapter = new RtrVideoListViewAdapter();
                    mAdapter.setOnRecyclerViewItemClickAnimator(new RecyclerViewItemUpAnimator());
                    mAdapter.setPostCurrentPosListener(this::setIndex);
                    mAdapter.setBgAndMenuVideoUrlCallback((bg, videoUrl) -> ((RtrMainActivity) Objects.requireNonNull(getActivity())).postVideoBgAndMenuVideoUrl(bg, videoUrl));
                }
                mAdapter.setVideoInfoCallback(videoInfoViewGroup::postVideoContentInfo);
                mAdapter.setVideoContentLists(videoContentLists);
                mRecyclerView.setAdapter(mAdapter);

                mRecyclerView.post(() -> {
                    if (cacheVideoId > 0) {
                        mIndex = mAdapter.getPositionFromVideoIdAndType(cacheVideoId, cacheVideoType);
                        cacheVideoId = -1;
                        cacheVideoType = -1;
                    }
                    mRecyclerView.scrollToPosition(mIndex);
                    BaseViewHolder holder = (BaseViewHolder) mRecyclerView.findViewHolderForAdapterPosition(mIndex);
                    mAdapter.selectedItem(mIndex, holder);
                });

            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.getVideoContentList(mCategory);
    }

    private int mIndex;
    private int cacheVideoId;
    private int cacheVideoType;

    public void setIndex(int index) {
        mIndex = index;
    }

    public void selectedItem(int videoId, int videoType) {
        if (mAdapter != null) {
            mIndex = mAdapter.getPositionFromVideoIdAndType(videoId, videoType);
            cacheVideoId = -1;
            cacheVideoType = -1;

            mRecyclerView.scrollToPosition(mIndex);
            LinearLayoutManager mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            Objects.requireNonNull(mLayoutManager).scrollToPositionWithOffset(mIndex, 0);
        } else {
            // 表示网络还未加载，
            cacheVideoId = videoId;
            cacheVideoType = videoType;
        }
    }

}