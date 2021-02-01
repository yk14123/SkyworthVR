package com.chinafocus.hvrskyworthvr.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.model.bean.VideoContentList;

public class VideoInfoViewGroup extends FrameLayout {

    private AppCompatTextView mTvTitle;
    private AppCompatTextView mTvIntro;
    private AppCompatTextView mTvIndex;
    private AppCompatTextView mTvTotalCount;
    private ImgConstraintLayout mImgConstraintLayout;

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

        mImgConstraintLayout = findViewById(R.id.view_flash_bg);

        mTvTitle = findViewById(R.id.tv_video_title);
        mTvIntro = findViewById(R.id.tv_video_intro);
        findViewById(R.id.bt_video_play);
        mTvIndex = findViewById(R.id.tv_video_index);
        mTvTotalCount = findViewById(R.id.tv_video_total_count);
    }

    @SuppressLint("SetTextI18n")
    public void postVideoContentInfo(VideoContentList videoContentInfo, int pos, int total) {
        mTvTitle.setText(videoContentInfo.getTitle());
        mTvIntro.setText(videoContentInfo.getIntro());
        mTvIndex.setText(++pos + "");
        mTvTotalCount.setText("/" + total);

        mImgConstraintLayout.start();
    }
}
