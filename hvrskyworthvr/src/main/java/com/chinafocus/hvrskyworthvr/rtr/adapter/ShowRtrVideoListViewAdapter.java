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
import com.chinafocus.hvrskyworthvr.service.event.VrCancelTimeTask;
import com.chinafocus.hvrskyworthvr.service.event.VrSyncPlayInfo;
import com.chinafocus.hvrskyworthvr.ui.adapter.BaseViewHolder;
import com.chinafocus.hvrskyworthvr.util.ObjectAnimatorViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ShowRtrVideoListViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final List<VideoContentList> mVideoContentLists;

    private int currentPos = -1;

    public ShowRtrVideoListViewAdapter(List<VideoContentList> videoContentLists) {
        mVideoContentLists = videoContentLists;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.rtr_show_item_video_list, parent, false);
        BaseViewHolder baseViewHolder = new BaseViewHolder(inflate);

        baseViewHolder.itemView.setOnClickListener(v -> {

            if (ObjectAnimatorViewUtil.getInstance().isRunning()) {
                return;
            }

            int temp = baseViewHolder.getAdapterPosition();

            if (temp != currentPos) {

                VrSyncPlayInfo.obtain().restoreVideoInfo();
                EventBus.getDefault().post(VrCancelTimeTask.obtain());

                BaseViewHolder viewHolderForAdapterPositionOut = (BaseViewHolder) ((RecyclerView) parent).findViewHolderForAdapterPosition(currentPos);
                if (viewHolderForAdapterPositionOut != null) {
                    // 旧的View startOut
                    ObjectAnimatorViewUtil.getInstance().startOut(viewHolderForAdapterPositionOut.itemView);
                }
                currentPos = temp;
                // 新View startIn
                ObjectAnimatorViewUtil.getInstance().startIn(v);
                // 改变内容和背景图
//                if (mVideoInfoCallback != null && mVideoContentLists != null) {
//                    mVideoInfoCallback.postVideoContent(mVideoContentLists.get(currentPos), currentPos, mVideoContentLists.size());
//                }
                if (mVideoBackgroundUrlCallback != null && mVideoContentLists != null) {
                    mVideoBackgroundUrlCallback.postVideoBackgroundUrl(
                            ConfigManager.getInstance().getDefaultUrl() + mVideoContentLists.get(currentPos).getImgUrl() + ImageProcess.process(2560, 1600));
                }
            }

        });

        return baseViewHolder;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        BaseViewHolder baseHolder = (BaseViewHolder) ((RecyclerView) holder.itemView.getParent()).findViewHolderForAdapterPosition(currentPos);
        if (baseHolder == holder) {
            ObjectAnimatorViewUtil.getInstance().showZoomInImmediately(holder.itemView);
        } else {
            ObjectAnimatorViewUtil.getInstance().showZoomOutImmediately(holder.itemView);
        }
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

    public void selectedItem(int pos, BaseViewHolder holder) {
        if (pos != currentPos) {
            currentPos = pos;
            if (pos >= 0) {
                if (holder != null) {
                    ObjectAnimatorViewUtil.getInstance().showZoomInImmediately(holder.itemView);
//                    if (mVideoInfoCallback != null && mVideoContentLists != null) {
//                        mVideoInfoCallback.postVideoContent(mVideoContentLists.get(currentPos), currentPos, mVideoContentLists.size());
//
//                    }

//                    ConfigManager.getInstance().getDefaultUrl() + mVideoContentLists.get(currentPos).getMenuVideoUrl()

                    if (mVideoBackgroundUrlCallback != null && mVideoContentLists != null) {
                        mVideoBackgroundUrlCallback.postVideoBackgroundUrl(
                                ConfigManager.getInstance().getDefaultUrl() + mVideoContentLists.get(currentPos).getImgUrl() + ImageProcess.process(2560, 1600));
                    }
                }
            } else {
                if (holder != null) {
                    ObjectAnimatorViewUtil.getInstance().showZoomOutImmediately(holder.itemView);
                }
            }
        }
    }

    private VideoBackgroundUrlCallback mVideoBackgroundUrlCallback;

    public void setVideoBackgroundUrl(VideoBackgroundUrlCallback videoBackgroundUrlCallback) {
        mVideoBackgroundUrlCallback = videoBackgroundUrlCallback;
    }

    public int getPositionFromVideoId(int videoId) {
        for (int i = 0; i < mVideoContentLists.size(); i++) {
            if (mVideoContentLists.get(i).getId() == videoId) {
                return i;
            }
        }
        return 0;
    }

    public interface VideoBackgroundUrlCallback {
        void postVideoBackgroundUrl(String bg);
    }

}
