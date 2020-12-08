package com.chinafocus.hvrskyworthvr.ui.main.video.sublist;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chinafocus.hvrskyworthvr.R;

import java.util.Objects;

public class VideoListFragment extends Fragment {

    public static final String VIDEO_LIST_CATEGORY = "video_list_category";

    public static VideoListFragment newInstance() {
        return new VideoListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.video_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();
        int category = Objects.requireNonNull(arguments).getInt(VIDEO_LIST_CATEGORY);

        RecyclerView recyclerView = requireView().findViewById(R.id.rv_video_list);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        VideoListViewModel videoListViewModel = new ViewModelProvider(this).get(VideoListViewModel.class);
        videoListViewModel.getVideoData(category);
        videoListViewModel.videoDataMutableLiveData.observe(getViewLifecycleOwner(), videoData -> {
            recyclerView.setAdapter(new VideoListAdapter(videoData.getList(), "video"));
        });

    }

}