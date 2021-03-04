package com.chinafocus.hvrskyworthvr.rtr.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.global.ConfigManager;
import com.chinafocus.hvrskyworthvr.model.bean.VideoContentList;
import com.chinafocus.hvrskyworthvr.net.ImageProcess;
import com.chinafocus.hvrskyworthvr.ui.adapter.BaseViewHolder;
import com.chinafocus.hvrskyworthvr.util.ViewClickUtil;

import java.util.List;

public class ShowRtrVideoListViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final List<VideoContentList> mVideoContentLists;

    private Callback mCallback;

    public void setOnClickCallback(Callback callback) {
        mCallback = callback;
    }

    public ShowRtrVideoListViewAdapter(List<VideoContentList> videoContentLists) {
        mVideoContentLists = videoContentLists;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.rtr_show_item_video_list, parent, false);
        BaseViewHolder baseViewHolder = new BaseViewHolder(inflate);

        ViewClickUtil.click(baseViewHolder.itemView, () -> {
            if (mCallback != null) {
                mCallback.onClick(baseViewHolder.getAdapterPosition());
            }
        });

        return baseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(ConfigManager.getInstance().getDefaultUrl() + mVideoContentLists.get(position).getImgUrl() + ImageProcess.process(584, 335))
                .into((AppCompatImageView) holder.getView(R.id.iv_video_list_bg));

        holder.setText(R.id.tv_video_list_title, mVideoContentLists.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mVideoContentLists == null ? 0 : mVideoContentLists.size();
    }

    /**
     * 通过当前播放视频videoId，来返回videoId位置
     *
     * @param currentVideoId 退出播放页面后的videoId
     * @return videoId在list中的位置
     */
    public int calculatePositionFromVideoId(int currentVideoId) {
        if (mVideoContentLists != null) {
            for (int i = 0; i < mVideoContentLists.size(); i++) {
                if (mVideoContentLists.get(i).getId() == currentVideoId) {
                    return i;
                }
            }
        }
        return 0;
    }

    public interface Callback {
        void onClick(int pos);
    }

}
