package com.chinafocus.hvrskyworthvr.rtr.videolist.sub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.rtr.adapter.RtrVideoListViewAdapter;

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
        mViewModel = ViewModelProviders.of(this).get(RtrVideoSubViewModel.class);
        // TODO: Use the ViewModel


        RecyclerView recyclerView = requireView().findViewById(R.id.vp_video_list_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), HORIZONTAL, false));

        recyclerView.setAdapter(new RtrVideoListViewAdapter(null));

    }

}