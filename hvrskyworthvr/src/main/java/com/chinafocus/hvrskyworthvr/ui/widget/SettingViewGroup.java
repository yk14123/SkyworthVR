package com.chinafocus.hvrskyworthvr.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.service.AliasService;
import com.chinafocus.hvrskyworthvr.service.BluetoothService;
import com.chinafocus.hvrskyworthvr.service.WifiService;
import com.chinafocus.hvrskyworthvr.util.TimeOutClickUtil;

import java.util.Objects;

public class SettingViewGroup extends FrameLayout {

    private static final int SETTING_VIEW_GROUP_WIFI = 1;
    private static final int SETTING_VIEW_GROUP_BLUETOOTH = 2;
    private static final int SETTING_VIEW_GROUP_ALIAS = 3;

    private AppCompatTextView mTvSettingMainTitle;
    private AppCompatTextView mTvSettingResult;
    private AppCompatImageView mIvSettingSet;
    private AppCompatTextView mTvSettingRetry;
    private AppCompatTextView mTvSettingSubTitle;
    private AppCompatTextView mTvSettingSubBody;
    private int mSettingType;
    private Drawable mIconSuccess;
    private Drawable mIconFail;

    public static final int INIT = 0;
    public static final int CONNECTING = 1;
    public static final int CONNECT_SUCCESS = 2;
    public static final int CONNECT_ERROR = 3;
    public static final int CONNECT_CHECK_AGAIN = 4;

    @IntDef({INIT, CONNECTING, CONNECT_SUCCESS, CONNECT_ERROR, CONNECT_CHECK_AGAIN})
    public @interface MessageType {

    }

    public SettingViewGroup(@NonNull Context context) {
        this(context, null);
    }

    public SettingViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SettingViewGroup, defStyleAttr, 0);
            try {
                mSettingType = a.getInt(R.styleable.SettingViewGroup_settingType, -1);
            } finally {
                a.recycle();
            }
        }

        LayoutInflater.from(context).inflate(R.layout.include_setting_device_status, this);
        mTvSettingMainTitle = findViewById(R.id.tv_setting_main_title);
        mTvSettingResult = findViewById(R.id.tv_setting_result);
        mIvSettingSet = findViewById(R.id.iv_setting_set);

        mTvSettingSubTitle = findViewById(R.id.tv_setting_sub_status_title);
        mTvSettingSubBody = findViewById(R.id.tv_setting_sub_status_body);
        mTvSettingRetry = findViewById(R.id.tv_setting_retry);

        postStatusMessage(INIT);
    }

    public void postStatusMessage(@MessageType int messageType) {
        postStatusMessage(messageType, null);
    }

    public void postStatusMessage(@MessageType int messageType, @Nullable String name) {

        switch (messageType) {
            case INIT:
                if (isWifiType()) {
                    initSettingWifi();
                } else if (isBluetoothType()) {
                    initSettingBluetooth();
                } else if (isAliasType()) {
                    initSettingAlias();
                }
                break;
            case CONNECTING:
                if (isWifiType()) {
                    setWifiConnecting();
                }
                break;
            case CONNECT_SUCCESS:
                if (isWifiType()) {
                    setWifiSuccess(name);
                } else if (isBluetoothType()) {
                    setBluetoothSuccess(name);
                } else if (isAliasType()) {
                    setAliasSuccess(name);
                }
                break;
            case CONNECT_ERROR:
                if (isWifiType()) {
                    // TODO PAD端 WIFI没有连接失败
                    setWifiError();
                } else if (isBluetoothType()) {
                    setBluetoothError();
                }
//                else if (isAlias()) {
                // TODO 当网络不好的时候，设置名称失败
//                }
                break;
            case CONNECT_CHECK_AGAIN:
                if (isWifiType()) {
                    setWifiCheckAgain(name);
                }
                break;
        }
    }

    private void initSettingAlias() {
        switchSubTitle(false);
        mTvSettingMainTitle.setText(getContext().getString(R.string.setting_status_alias_main_title));
        setFinalResultMessage(R.string.setting_status_alias_result, R.color.color_setting_status_gray);
        mIvSettingSet.setEnabled(false);
        mIvSettingSet.setOnClickListener(v -> AliasService.getInstance().onClick(v.getContext()));
    }

    private void initSettingBluetooth() {
        switchSubTitle(true);
        clearMainTitleDrawable();
        mTvSettingRetry.setVisibility(GONE);
        mIvSettingSet.setVisibility(GONE);
        mTvSettingMainTitle.setText(getContext().getString(R.string.setting_status_bluetooth_main_title));
        mTvSettingSubTitle.setText(getContext().getString(R.string.setting_status_bluetooth_sub_title));
        mTvSettingSubBody.setText(getContext().getString(R.string.setting_status_bluetooth_sub_body));

        mTvSettingRetry.setOnClickListener(v -> BluetoothService.getInstance().startBluetoothEngine((Activity) v.getContext()));
        mTvSettingMainTitle.setOnClickListener(v -> TimeOutClickUtil.startTimeOutClick(() -> BluetoothService.getInstance().unBondDevice(v.getContext())));
    }

    private void initSettingWifi() {
        switchSubTitle(false);
        mTvSettingMainTitle.setText(getContext().getString(R.string.setting_status_wifi_main_title));
        setFinalResultMessage(R.string.setting_status_alias_result, R.color.color_setting_status_gray);

        mIvSettingSet.setOnClickListener(v -> {
            WifiService.getInstance().startSettingWifi(v.getContext());
            postStatusMessage(CONNECTING);
        });
    }

    private void setFinalResultMessage(int result, int color) {
        setFinalResultMessage(result, color, "");
    }

    private void setFinalResultMessage(int result, int color, @NonNull String devicesName) {
        mTvSettingResult.setText(String.format("%s%s", getContext().getString(result), devicesName));
        mTvSettingResult.setTextColor(getContext().getResources().getColor(color));
    }

    private void setBluetoothError() {
        showMainTitleSuccessDrawable(false);
        switchSubTitle(false);
        mTvSettingRetry.setVisibility(VISIBLE);
        setFinalResultMessage(R.string.setting_status_bluetooth_error, R.color.color_setting_status_yellow);
    }

    private void setAliasSuccess(String name) {
        switchSubTitle(false);
        setFinalResultMessage(R.string.setting_status_alias_empty, R.color.color_black, name);
        mIvSettingSet.setEnabled(true);
    }

    private void setBluetoothSuccess(String name) {
        showMainTitleSuccessDrawable(true);
        switchSubTitle(false);
        mTvSettingRetry.setVisibility(GONE);
        setFinalResultMessage(R.string.setting_status_bluetooth_success, R.color.color_setting_status_success, name);
    }

    private void setWifiConnecting() {
        clearMainTitleDrawable();
        switchSubTitle(false);
        setFinalResultMessage(R.string.setting_status_wifi_connecting, R.color.color_setting_status_gray);
    }

    private void setWifiError() {
        showMainTitleSuccessDrawable(false);
        switchSubTitle(false);
        setFinalResultMessage(R.string.setting_status_wifi_connect_error, R.color.color_setting_status_gray);
    }

    private void setWifiCheckAgain(String name) {
        showMainTitleSuccessDrawable(false);
        switchSubTitle(true);
        mTvSettingSubTitle.setText(name);
        mTvSettingSubBody.setText(getContext().getString(R.string.setting_status_wifi_check_again));
        mTvSettingSubBody.setTextColor(getContext().getResources().getColor(R.color.color_setting_status_yellow));
    }

    private void setWifiSuccess(String name) {
        showMainTitleSuccessDrawable(true);
        switchSubTitle(false);
        setFinalResultMessage(R.string.setting_status_wifi_success, R.color.color_setting_status_success, name);
    }

    private boolean isWifiType() {
        return mSettingType == SETTING_VIEW_GROUP_WIFI;
    }

    private boolean isBluetoothType() {
        return mSettingType == SETTING_VIEW_GROUP_BLUETOOTH;
    }

    private boolean isAliasType() {
        return mSettingType == SETTING_VIEW_GROUP_ALIAS;
    }

    /**
     * 是否显示subTitle
     */
    private void switchSubTitle(boolean b) {
        if (b) {
            mTvSettingResult.setVisibility(GONE);
            mTvSettingSubTitle.setVisibility(VISIBLE);
            mTvSettingSubBody.setVisibility(VISIBLE);
        } else {
            mTvSettingResult.setVisibility(VISIBLE);
            mTvSettingSubTitle.setVisibility(GONE);
            mTvSettingSubBody.setVisibility(GONE);
        }
    }

    private void showMainTitleSuccessDrawable(boolean b) {
        Drawable drawable;
        if (b) {
            if (mIconSuccess == null) {
                mIconSuccess = ContextCompat.getDrawable(getContext(), R.drawable.sky_worth_setting_device_status_success);
                Objects.requireNonNull(mIconSuccess).setBounds(0, 0, 48, 48);
            }
            drawable = mIconSuccess;
        } else {
            if (mIconFail == null) {
                mIconFail = ContextCompat.getDrawable(getContext(), R.drawable.sky_worth_setting_device_status_fail);
                Objects.requireNonNull(mIconFail).setBounds(0, 0, 48, 48);
            }
            drawable = mIconFail;
        }

        mTvSettingMainTitle.setCompoundDrawables(null, null, drawable, null);
    }

    private void clearMainTitleDrawable() {
        mTvSettingMainTitle.setCompoundDrawables(null, null, null, null);
    }

}
