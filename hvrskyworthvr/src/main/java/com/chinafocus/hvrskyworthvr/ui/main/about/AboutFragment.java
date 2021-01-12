package com.chinafocus.hvrskyworthvr.ui.main.about;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.ui.setting.SettingActivity;
import com.chinafocus.hvrskyworthvr.util.TimeOutClickUtil;

import static com.chinafocus.hvrskyworthvr.global.Constants.*;

public class AboutFragment extends Fragment {

    private AboutViewModel mViewModel;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AboutViewModel.class);
        // TODO: Use the ViewModel

        requireView().findViewById(R.id.tv_back_door).setOnClickListener(v -> {
            TimeOutClickUtil.startTimeOutClick(this::startSettingActivity);
        });

    }

    private void startSettingActivity() {
        startActivity(new Intent(getActivity(), SettingActivity.class));
        ACTIVITY_TAG = ACTIVITY_SETTING;
    }

}