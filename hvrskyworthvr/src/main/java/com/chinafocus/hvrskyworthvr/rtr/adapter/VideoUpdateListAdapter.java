package com.chinafocus.hvrskyworthvr.rtr.adapter;

import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.RecyclerView;

import com.chinafocus.hvrskyworthvr.GlideApp;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.download.DownLoadHolder;
import com.chinafocus.hvrskyworthvr.download.VideoType;
import com.chinafocus.hvrskyworthvr.net.ImageProcess;
import com.chinafocus.hvrskyworthvr.ui.adapter.BaseViewHolder;
import com.chinafocus.hvrskyworthvr.util.TimeUtil;
import com.chinafocus.hvrskyworthvr.util.widget.VideoUpdateTypeView;

import java.util.List;

public class VideoUpdateListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<DownLoadHolder> mDownLoadHolders;

    public void refreshDownLoadHolder(List<DownLoadHolder> data) {
        mDownLoadHolders = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_update, parent, false);
        return new BaseViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        GlideApp.with(holder.itemView.getContext())
                .load(mDownLoadHolders.get(position).getImageUrl() + ImageProcess.process(600, 400))
                .placeholder(R.drawable.media_item_cover_bg)
                .error(R.drawable.media_item_cover_bg)
                .into((ImageFilterView) holder.getView(R.id.iv_video_update_image_url));

        holder.setText(R.id.tv_video_update_title, mDownLoadHolders.get(position).getTitle());
        holder.setText(R.id.tv_video_update_duration, TimeUtil.timeParse(mDownLoadHolders.get(position).getDuration()));

        updateProgressColor(holder, position);
        updateItemText(holder, position);
        initVideoType(holder, position);
        updateVideoSize(holder, position);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            Log.e("MyLog", " payloads !=null 更新了文字内容 >>> " + mDownLoadHolders.get(position).getCurrentStatus());
            updateProgressColor(holder, position);
            updateItemText(holder, position);
            updateVideoSize(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return mDownLoadHolders == null ? 0 : mDownLoadHolders.size();
    }

    private void initVideoType(@NonNull BaseViewHolder holder, int position) {
        VideoType videoType = mDownLoadHolders.get(position).getVideoType();
        if (videoType == VideoType.REAL_VIDEO) {
            ((VideoUpdateTypeView) holder.getView(R.id.view_video_update_type)).setTypeRealVideo();
        } else if (videoType == VideoType.PRE_VIDEO) {
            ((VideoUpdateTypeView) holder.getView(R.id.view_video_update_type)).setTypePreVideo();
        }
    }

    private void updateItemText(@NonNull BaseViewHolder holder, int position) {
        holder.setText(R.id.tv_video_update_status, mDownLoadHolders.get(position).getCurrentStatus());
        holder.setTextColor(R.id.tv_video_update_status, mDownLoadHolders.get(position).getCurrentStatusColor());
    }

    private void updateProgressColor(@NonNull BaseViewHolder holder, int position) {
        ((ProgressBar) holder.getView(R.id.pb_video_update)).setProgress(mDownLoadHolders.get(position).getProgress());
        ((ProgressBar) holder.getView(R.id.pb_video_update)).setProgressTintList(ColorStateList.valueOf(mDownLoadHolders.get(position).getProgressingColor()));
    }

    private void updateVideoSize(BaseViewHolder holder, int position) {
        holder.setText(R.id.tv_video_update_size, mDownLoadHolders.get(position).getVideoSize());
    }

}
