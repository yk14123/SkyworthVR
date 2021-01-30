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
import com.chinafocus.hvrskyworthvr.rtr.adapter.RtrVideoListViewAdapter;
import com.chinafocus.hvrskyworthvr.rtr.main.RtrMainActivity;
import com.chinafocus.hvrskyworthvr.ui.adapter.BaseViewHolder;
import com.chinafocus.hvrskyworthvr.ui.widget.VideoInfoViewGroup;

import java.util.Objects;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;

public class RtrVideoSubFragment extends Fragment {

    private RtrVideoSubViewModel mViewModel;

    public static RtrVideoSubFragment newInstance() {
        return new RtrVideoSubFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rtr_video_sub_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RtrVideoSubViewModel.class);
        // TODO: Use the ViewModel
        mViewModel.getVideoContentList();

        VideoInfoViewGroup videoInfoViewGroup = requireView().findViewById(R.id.view_video_info);

        RecyclerView recyclerView = requireView().findViewById(R.id.vp_video_list_detail);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), HORIZONTAL, false);
        // 当页面退出的时候 调用onViewDetachedFromWindow
//        linearLayoutManager.setRecycleChildrenOnDetach(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mViewModel.videoDataMutableLiveData.observe(getViewLifecycleOwner(), videoContentLists -> {
            RtrVideoListViewAdapter adapter = new RtrVideoListViewAdapter(videoContentLists);
            adapter.setVideoInfoCallback(videoInfoViewGroup::postVideoContentInfo);
            adapter.setBgAndMenuVideoUrlCallback((bg, videoUrl) -> ((RtrMainActivity) Objects.requireNonNull(getActivity())).postVideoBgAndMenuVideoUrl(bg, videoUrl));
            recyclerView.setAdapter(adapter);
            recyclerView.post(() -> {
                BaseViewHolder holder = (BaseViewHolder) recyclerView.findViewHolderForAdapterPosition(0);
                adapter.selectedItem(0, holder);
            });

        });

    }

}