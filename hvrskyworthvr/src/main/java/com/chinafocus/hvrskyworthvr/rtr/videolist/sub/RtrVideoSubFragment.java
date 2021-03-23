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
            String category = arguments.getString(VIDEO_LIST_CATEGORY);

            mViewModel = new ViewModelProvider(this).get(RtrVideoSubViewModel.class);
            mViewModel.getVideoContentList(category);

            VideoInfoViewGroup videoInfoViewGroup = requireView().findViewById(R.id.view_video_info);

            mRecyclerView = requireView().findViewById(R.id.vp_video_list_detail);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), HORIZONTAL, false);
            // 当页面退出的时候 调用onViewDetachedFromWindow
//        linearLayoutManager.setRecycleChildrenOnDetach(true);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mViewModel.videoDataMutableLiveData.observe(getViewLifecycleOwner(), videoContentLists -> {
                if (mAdapter == null) {
                    mAdapter = new RtrVideoListViewAdapter(videoContentLists);
                    mAdapter.setOnRecyclerViewItemClickAnimator(new RecyclerViewItemUpAnimator());
                    mAdapter.setVideoInfoCallback(videoInfoViewGroup::postVideoContentInfo);
                    mAdapter.setBgAndMenuVideoUrlCallback((bg, videoUrl) -> ((RtrMainActivity) Objects.requireNonNull(getActivity())).postVideoBgAndMenuVideoUrl(bg, videoUrl));
                    mRecyclerView.setAdapter(mAdapter);
                }
                mRecyclerView.post(() -> {
                    BaseViewHolder holder = (BaseViewHolder) mRecyclerView.findViewHolderForAdapterPosition(0);
                    mAdapter.selectedItem(0, holder);
                });

            });

        }
    }

    public void setItemPosition(int videoId) {
        if (mAdapter != null) {
            int index = mAdapter.getPositionFromVideoId(videoId);
            mRecyclerView.scrollToPosition(index);
            LinearLayoutManager mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            Objects.requireNonNull(mLayoutManager).scrollToPositionWithOffset(index, 0);
        }
    }

}