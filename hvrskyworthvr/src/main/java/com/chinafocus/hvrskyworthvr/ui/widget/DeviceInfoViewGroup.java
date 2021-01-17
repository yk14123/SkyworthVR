package com.chinafocus.hvrskyworthvr.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.service.WifiService;

public class DeviceInfoViewGroup extends FrameLayout {

    private AppCompatTextView mTvUuid;
    private ProgressBar mProgressBar;
    private ProgressBar mPbAccountName;
    private AppCompatTextView mTvAccountName;

    public DeviceInfoViewGroup(@NonNull Context context) {
        this(context, null);
    }

    public DeviceInfoViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeviceInfoViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.include_setting_device_info, this);

        mTvUuid = findViewById(R.id.tv_uuid);
        mProgressBar = findViewById(R.id.pb_device_info_uuid);

        mPbAccountName = findViewById(R.id.pb_device_info_account_name);
        mTvAccountName = findViewById(R.id.tv_setting_device_info_account_name);

        String accountName = WifiService.getInstance().getAccountName();
        if (TextUtils.isEmpty(accountName)) {
            showInitAccountName();
        } else {
            postAccountName(accountName);
        }

        String currentDeviceUUID = WifiService.getInstance().getCurrentDeviceUUID();
        if (TextUtils.isEmpty(currentDeviceUUID)) {
            showAutoSyncUUID();
        } else {
            postUUIDMessage(currentDeviceUUID);
        }
    }

    private void showInitAccountName() {
        mTvAccountName.setText("等待自动生成");
        mPbAccountName.setVisibility(VISIBLE);
    }

    private void showAutoSyncUUID() {
        mTvUuid.setText("等待自动生成");
        mProgressBar.setVisibility(VISIBLE);
    }

    public void postUUIDMessage(String uuid) {
        mTvUuid.setText(uuid);
        mProgressBar.setVisibility(INVISIBLE);
    }

    public void postAccountName(String name) {
        mTvAccountName.setText(name);
        mPbAccountName.setVisibility(INVISIBLE);
    }
}
