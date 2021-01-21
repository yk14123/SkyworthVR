package com.chinafocus.hvrskyworthvr.ui.main.about;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.model.DeviceInfoManager;
import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.hvrskyworthvr.ui.setting.SettingActivity;
import com.chinafocus.hvrskyworthvr.util.TimeOutClickUtil;

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

        AppCompatTextView textView = requireView().findViewById(R.id.tv_back_door);
        textView.setText(DeviceInfoManager.getInstance().getDeviceAccountName());
        textView.setOnClickListener(v -> TimeOutClickUtil.startTimeOutClick(this::startSettingActivity));

        requireView().findViewById(R.id.tv_about_user_protocol)
                .setOnClickListener(
                        v -> WebAboutActivity.startWebAboutActivity(requireActivity(), getString(R.string.about_user_protocol), ApiMultiService.ABOUT_USER_PROTOCOL)
                );
        requireView().findViewById(R.id.tv_about_privacy_protocol)
                .setOnClickListener(
                        v -> WebAboutActivity.startWebAboutActivity(requireActivity(), getString(R.string.about_privacy_protocol), ApiMultiService.ABOUT_PRIVACY_PROTOCOL)
                );
        requireView().findViewById(R.id.tv_about_us_protocol)
                .setOnClickListener(
                        v -> WebAboutActivity.startWebAboutActivity(requireActivity(), getString(R.string.about_us_protocol), ApiMultiService.ABOUT_US_PROTOCOL)
                );

    }

    private void startSettingActivity() {
        startActivity(new Intent(getActivity(), SettingActivity.class));
    }

}