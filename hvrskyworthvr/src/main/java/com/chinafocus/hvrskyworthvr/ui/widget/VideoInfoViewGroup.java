package com.chinafocus.hvrskyworthvr.ui.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.model.bean.VideoContentList;
import com.chinafocus.hvrskyworthvr.rtr.media.RtrMediaPlayActivity;
import com.chinafocus.hvrskyworthvr.service.event.VrSyncPlayInfo;
import com.chinafocus.hvrskyworthvr.util.ViewClickUtil;

import static com.chinafocus.hvrskyworthvr.global.Constants.REQUEST_CODE_PAD_MEDIA_ACTIVITY;
import static com.chinafocus.hvrskyworthvr.rtr.media.RtrMediaPlayActivity.MEDIA_CATEGORY_TAG;
import static com.chinafocus.hvrskyworthvr.rtr.media.RtrMediaPlayActivity.MEDIA_FROM_TAG;
import static com.chinafocus.hvrskyworthvr.rtr.media.RtrMediaPlayActivity.MEDIA_ID;

public class VideoInfoViewGroup extends FrameLayout {

    private AppCompatTextView mTvTitle;
    private AppCompatTextView mTvIntro;
    private AppCompatTextView mTvIndex;
    private AppCompatTextView mTvTotalCount;
    private ImgConstraintLayout mImgConstraintLayout;
    private int mVideoId;
    private int mVideoType;
    private int mVideoClassify;

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
        mTvIndex = findViewById(R.id.tv_video_index);
        mTvTotalCount = findViewById(R.id.tv_video_total_count);

        ViewClickUtil.click(findViewById(R.id.tv_video_play), () -> {
            Intent intent = new Intent(context, RtrMediaPlayActivity.class);
            intent.putExtra(MEDIA_FROM_TAG, mVideoType);
            intent.putExtra(MEDIA_CATEGORY_TAG, mVideoClassify);
            intent.putExtra(MEDIA_ID, mVideoId);
            ((Activity) context).startActivityForResult(intent, REQUEST_CODE_PAD_MEDIA_ACTIVITY);
        });

    }

    @SuppressLint("SetTextI18n")
    public void postVideoContentInfo(VideoContentList videoContentInfo, int pos, int total) {
        mTvTitle.setText(videoContentInfo.getTitle());
        mTvIntro.setText(videoContentInfo.getIntro());
        mTvIndex.setText(++pos + "");
        mTvTotalCount.setText("/" + total);

        mImgConstraintLayout.start();

        mVideoId = videoContentInfo.getId();

        if (videoContentInfo.getType().equals("2")) {
            // 全景出版
            mVideoType = 1;
            mVideoClassify = -1;

        } else if (videoContentInfo.getType().equals("1")) {
            // 全景视频
            mVideoType = 2;
            mVideoClassify = Integer.parseInt(videoContentInfo.getClassify());
        }
        VrSyncPlayInfo.obtain().setCategory(mVideoClassify);
        VrSyncPlayInfo.obtain().setTag(mVideoType);
    }

}
