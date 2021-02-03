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
import com.chinafocus.hvrskyworthvr.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class RtrVideoListViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final List<VideoContentList> mVideoContentLists;

    private int currentPos = -1;

    public RtrVideoListViewAdapter(List<VideoContentList> videoContentLists) {
        mVideoContentLists = videoContentLists;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.rtr_item_video_list, parent, false);
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
                if (mVideoInfoCallback != null && mVideoContentLists != null) {
                    mVideoInfoCallback.postVideoContent(mVideoContentLists.get(currentPos), currentPos, mVideoContentLists.size());
                }
                if (mBgAndMenuVideoUrlCallback != null && mVideoContentLists != null) {
                    mBgAndMenuVideoUrlCallback.postVideoBgAndMenuVideoUrl(
                            ConfigManager.getInstance().getDefaultUrl() + mVideoContentLists.get(currentPos).getImgUrl() + ImageProcess.process(2560, 1600),
                            ConfigManager.getInstance().getDefaultUrl() + mVideoContentLists.get(currentPos).getMenuVideoUrl());
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

        holder.setText(R.id.tv_video_list_duration, TimeUtil.timeParse(mVideoContentLists.get(position).getDuration()));
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
                    if (mVideoInfoCallback != null && mVideoContentLists != null) {
                        mVideoInfoCallback.postVideoContent(mVideoContentLists.get(currentPos), currentPos, mVideoContentLists.size());

                    }
                    if (mBgAndMenuVideoUrlCallback != null && mVideoContentLists != null) {
                        mBgAndMenuVideoUrlCallback.postVideoBgAndMenuVideoUrl(
                                ConfigManager.getInstance().getDefaultUrl() + mVideoContentLists.get(currentPos).getImgUrl() + ImageProcess.process(2560, 1600),
                                ConfigManager.getInstance().getDefaultUrl() + mVideoContentLists.get(currentPos).getMenuVideoUrl());
                    }
                }
            } else {
                if (holder != null) {
                    ObjectAnimatorViewUtil.getInstance().showZoomOutImmediately(holder.itemView);
                }
            }
        }
    }

    private VideoInfoCallback mVideoInfoCallback;
    private BgAndMenuVideoUrlCallback mBgAndMenuVideoUrlCallback;

    public void setVideoInfoCallback(VideoInfoCallback videoInfoCallback) {
        mVideoInfoCallback = videoInfoCallback;
    }

    public void setBgAndMenuVideoUrlCallback(BgAndMenuVideoUrlCallback bgAndMenuVideoUrlCallback) {
        mBgAndMenuVideoUrlCallback = bgAndMenuVideoUrlCallback;
    }

    public int getPositionFromVideoId(int videoId) {
        for (int i = 0; i < mVideoContentLists.size(); i++) {
            if (mVideoContentLists.get(i).getId() == videoId) {
                return i;
            }
        }
        return 0;
    }

    public interface BgAndMenuVideoUrlCallback {
        void postVideoBgAndMenuVideoUrl(String bg, String videoUrl);
    }

    public interface VideoInfoCallback {
        void postVideoContent(VideoContentList contentList, int pos, int total);
    }


}
