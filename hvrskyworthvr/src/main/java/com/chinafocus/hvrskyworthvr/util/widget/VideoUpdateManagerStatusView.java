package com.chinafocus.hvrskyworthvr.util.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.chinafocus.hvrskyworthvr.R;

import java.util.Locale;

public class VideoUpdateManagerStatusView extends AppCompatTextView {

    public VideoUpdateManagerStatusView(@NonNull Context context) {
        super(context);
    }

    public VideoUpdateManagerStatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoUpdateManagerStatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void showCompleted(int total) {
        setText(String.format(Locale.getDefault(), "下载完成（%d/%d）", total, total));
        setTextColor(ContextCompat.getColor(getContext(), R.color.color_setting_status_gray));
        setEnabled(false);
    }

    public void showDownLoading(int i, int total) {
        setText(String.format(Locale.getDefault(), "正在下载%d/%d", i, total));
        setTextColor(ContextCompat.getColor(getContext(), R.color.color_setting_status_gray));
        setEnabled(false);
    }

    public void showDownLoadError() {
        setText("下载失败，点击重试");
        setTextColor(ContextCompat.getColor(getContext(), R.color.color_check_version_code_pause_bg));
        setEnabled(true);
    }

}
