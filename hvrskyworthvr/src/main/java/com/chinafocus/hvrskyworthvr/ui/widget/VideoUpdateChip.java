package com.chinafocus.hvrskyworthvr.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.chinafocus.hvrskyworthvr.R;
import com.google.android.material.chip.Chip;

public class VideoUpdateChip extends Chip {

    public VideoUpdateChip(Context context) {
        super(context);
    }

    public VideoUpdateChip(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoUpdateChip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void showVideoUpdateInit() {
        setChipIconVisible(true);
        setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.color_video_update_ok_bg)));
        setText(getContext().getString(R.string.video_update_init_text));
    }

    @SuppressLint("DefaultLocale")
    public void showVideoUpdateRunning(int currentTask, int total) {
        setChipIconVisible(true);
        setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.color_video_update_ok_bg)));
        setText(String.format("%s  %d/%d", getContext().getString(R.string.video_update_running_text), currentTask, total));
    }

    public void showVideoUpdateDownloadError() {
        setChipIconVisible(true);
        setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.color_video_update_error_bg)));
        setText(getContext().getString(R.string.video_update_download_error_text));
    }

    public void showVideoUpdateError() {
        setChipIconVisible(true);
        setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.color_video_update_error_bg)));
        setText(getContext().getString(R.string.video_update_error_text));
    }

    public void showVideoUpdateLatest() {
        setChipIconVisible(false);
        setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.color_video_update_ok_bg)));
        setText(getContext().getString(R.string.video_update_Latest_text));
    }

}
