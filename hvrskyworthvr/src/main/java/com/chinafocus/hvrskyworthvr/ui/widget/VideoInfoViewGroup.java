package com.chinafocus.hvrskyworthvr.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chinafocus.hvrskyworthvr.R;

public class VideoInfoViewGroup extends FrameLayout {
    public VideoInfoViewGroup(@NonNull Context context) {
        this(context, null);
    }

    public VideoInfoViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoInfoViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.include_rtr_video_info, this);

    }
}
