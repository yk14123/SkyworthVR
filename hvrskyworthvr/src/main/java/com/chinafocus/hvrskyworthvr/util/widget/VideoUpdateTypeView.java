package com.chinafocus.hvrskyworthvr.util.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.chinafocus.hvrskyworthvr.R;

public class VideoUpdateTypeView extends AppCompatTextView {

    private Drawable mPreBg;
    private Drawable mRealBg;

    public VideoUpdateTypeView(@NonNull Context context) {
        super(context);
        init();
    }

    public VideoUpdateTypeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoUpdateTypeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPreBg = ContextCompat.getDrawable(getContext(), R.drawable.shape_video_update_type_pre_bg);
        mRealBg = ContextCompat.getDrawable(getContext(), R.drawable.shape_video_update_type_real_bg);
    }

    public void setTypeRealVideo() {
        setText("正片");
        setBackground(mRealBg);
    }

    public void setTypePreVideo() {
        setText("预览");
        setBackground(mPreBg);
    }

}
