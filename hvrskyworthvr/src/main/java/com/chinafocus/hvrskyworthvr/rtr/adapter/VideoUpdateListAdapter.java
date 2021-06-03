package com.chinafocus.hvrskyworthvr.rtr.adapter;

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
import com.chinafocus.hvrskyworthvr.net.ImageProcess;
import com.chinafocus.hvrskyworthvr.ui.adapter.BaseViewHolder;
import com.chinafocus.hvrskyworthvr.util.TimeUtil;

import java.util.List;

public class VideoUpdateListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<DownLoadHolder> mDownLoadHolders;

    public VideoUpdateListAdapter(List<DownLoadHolder> data) {
        this.mDownLoadHolders = data;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_update, parent, false);
        BaseViewHolder baseViewHolder = new BaseViewHolder(inflate);
//        baseViewHolder.setIsRecyclable(false);
        return baseViewHolder;
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
        holder.setText(R.id.tv_video_update_status, mDownLoadHolders.get(position).getCurrentStatus());
        ((ProgressBar) holder.getView(R.id.pb_video_update)).setProgress(mDownLoadHolders.get(position).getProgress());
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            ((ProgressBar) holder.getView(R.id.pb_video_update)).setProgress(mDownLoadHolders.get(position).getProgress());
            holder.setText(R.id.tv_video_update_status, mDownLoadHolders.get(position).getCurrentStatus());
        }
    }

    @Override
    public int getItemCount() {
        return mDownLoadHolders == null ? 0 : mDownLoadHolders.size();
    }

}
