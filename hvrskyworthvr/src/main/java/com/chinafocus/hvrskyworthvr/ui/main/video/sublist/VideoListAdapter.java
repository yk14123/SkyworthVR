package com.chinafocus.hvrskyworthvr.ui.main.video.sublist;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.TimeUtils;
import com.bumptech.glide.Glide;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.model.bean.VideoListData;
import com.chinafocus.hvrskyworthvr.ui.adapter.BaseViewHolder;
import com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity;

import java.util.List;

import static com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity.MEDIA_CATEGORY_TAG;
import static com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity.MEDIA_ID;

public class VideoListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<VideoListData.ListBean> videoLists;
    private String video_tag;

    public VideoListAdapter(List<VideoListData.ListBean> videoLists, String tag) {
        this.videoLists = videoLists;
        this.video_tag = tag;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_list, parent, false);
        BaseViewHolder baseViewHolder = new BaseViewHolder(inflate);
        baseViewHolder.itemView.setOnClickListener(v -> {
            int adapterPosition = baseViewHolder.getAdapterPosition();
            int id = videoLists.get(adapterPosition).getId();
            Intent intent = new Intent(parent.getContext(), MediaPlayActivity.class);
            intent.putExtra(MEDIA_ID, id);
            intent.putExtra(MEDIA_CATEGORY_TAG, video_tag);
            parent.getContext().startActivity(intent);
        });
        return baseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

        Glide.with(holder.itemView.getContext())
                .load(Constants.DEFAULT_URL + videoLists.get(position).getCoverImg())
                .into((ImageView) holder.getView(R.id.iv_video_list_bg));

        String time = TimeUtils.millis2String(videoLists.get(position).getDuration() * 1000, "mm:ss");

        holder
                .setText(R.id.tv_video_list_duration, time)
                .setText(R.id.tv_video_list_title, videoLists.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        return videoLists.size();
    }
}
