package com.chinafocus.hvrskyworthvr.util.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.download.DownLoadHolder;
import com.chinafocus.hvrskyworthvr.rtr.adapter.VideoUpdateListAdapter;
import com.chinafocus.hvrskyworthvr.service.event.download.VideoUpdateManagerStatus;

import java.util.List;

import static com.chinafocus.hvrskyworthvr.global.Constants.VIDEO_UPDATE_STATUS;

public class VideoUpdateStatusView extends FrameLayout {

    private Group mGroupVideoUpdateClose;
    private Group mGroupVideoUpdateNetError;
    private Group mGroupVideoUpdateLatest;
    private Group mGroupVideoUpdateDownload;
    private RecyclerView mRecyclerView;
    private VideoUpdateListAdapter mVideoUpdateListAdapter;
    private AppCompatImageView mNetErrorRetry;
    private VideoUpdateManagerStatusView mVideoUpdateManagerStatusView;

    public VideoUpdateStatusView(@NonNull Context context) {
        this(context, null);
    }

    public VideoUpdateStatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoUpdateStatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_video_update_status, this);

        mGroupVideoUpdateClose = findViewById(R.id.group_video_update_close);
        mGroupVideoUpdateNetError = findViewById(R.id.group_video_update_net_error);
        mGroupVideoUpdateLatest = findViewById(R.id.group_video_update_latest);
        mGroupVideoUpdateDownload = findViewById(R.id.group_video_update_download);

        mNetErrorRetry = findViewById(R.id.iv_video_update_net_error_retry);

        mRecyclerView = findViewById(R.id.rv_video_update_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mVideoUpdateManagerStatusView = findViewById(R.id.view_video_update_manager_status);

        boolean aBoolean = SPUtils.getInstance().getBoolean(VIDEO_UPDATE_STATUS);
        if (!aBoolean) {
            showVideoUpdateClose();
        }
    }


    public void showVideoUpdateClose() {
        setVisibility(VISIBLE);
        mGroupVideoUpdateClose.setVisibility(VISIBLE);
        mGroupVideoUpdateNetError.setVisibility(INVISIBLE);
        mGroupVideoUpdateLatest.setVisibility(INVISIBLE);
        mGroupVideoUpdateDownload.setVisibility(INVISIBLE);
    }

    public void showVideoUpdateNetError() {
        setVisibility(VISIBLE);
        mGroupVideoUpdateNetError.setVisibility(VISIBLE);
        mGroupVideoUpdateClose.setVisibility(INVISIBLE);
        mGroupVideoUpdateLatest.setVisibility(INVISIBLE);
        mGroupVideoUpdateDownload.setVisibility(INVISIBLE);
    }

    public void showVideoUpdateLatest() {
        setVisibility(VISIBLE);
        mGroupVideoUpdateLatest.setVisibility(VISIBLE);
        mGroupVideoUpdateNetError.setVisibility(INVISIBLE);
        mGroupVideoUpdateClose.setVisibility(INVISIBLE);
        mGroupVideoUpdateDownload.setVisibility(INVISIBLE);
    }

    public void showVideoUpdateDownload(List<DownLoadHolder> data) {
        setVisibility(VISIBLE);
        mGroupVideoUpdateDownload.setVisibility(VISIBLE);
        mGroupVideoUpdateLatest.setVisibility(INVISIBLE);
        mGroupVideoUpdateNetError.setVisibility(INVISIBLE);
        mGroupVideoUpdateClose.setVisibility(INVISIBLE);

        if (mVideoUpdateListAdapter == null) {
            mVideoUpdateListAdapter = new VideoUpdateListAdapter(data);
            mRecyclerView.setAdapter(mVideoUpdateListAdapter);
        }
    }

    public void setNetErrorRetryClick(OnClickListener l) {
        mNetErrorRetry.setOnClickListener(v -> {
            setVisibility(INVISIBLE);
            l.onClick(v);
        });
    }

    public void setRetryDownLoadClick(OnClickListener l) {
        mVideoUpdateManagerStatusView.setOnClickListener(l);
    }

    public void postPayload(DownLoadHolder event) {
        mVideoUpdateListAdapter.notifyItemChanged(event.getPos(), event);
    }

    public void postVideoUpdateManagerStatus(VideoUpdateManagerStatus event) {
        if (event.getVideoUpdateStatus() == null) {
            return;
        }
        switch (event.getVideoUpdateStatus()) {
            case START:
            case DOWNLOADING:
                mVideoUpdateManagerStatusView.showDownLoading(event.getCurrentIndex(), event.getTotal());
                break;
            case COMPLETED:
                mVideoUpdateManagerStatusView.showCompleted(event.getTotal());
                break;
            case RETRY:
                mVideoUpdateManagerStatusView.showDownLoadError();
                break;
        }
    }
}
