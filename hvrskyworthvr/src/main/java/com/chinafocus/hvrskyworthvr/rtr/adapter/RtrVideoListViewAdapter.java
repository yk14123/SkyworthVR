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
import com.chinafocus.hvrskyworthvr.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class RtrVideoListViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<VideoContentList> mVideoContentLists;

    private int currentPos = -1;

    public void setVideoContentLists(List<VideoContentList> videoContentLists) {
        if (mVideoContentLists != null && mVideoContentLists.size() > 0) {
            mVideoContentLists.clear();
            mVideoContentLists.addAll(videoContentLists);
        } else {
            mVideoContentLists = videoContentLists;
        }
    }

    private OnRecyclerViewItemClickAnimator mOnRecyclerViewItemClickAnimator;

    public void setOnRecyclerViewItemClickAnimator(OnRecyclerViewItemClickAnimator onRecyclerViewItemClickAnimator) {
        mOnRecyclerViewItemClickAnimator = onRecyclerViewItemClickAnimator;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.rtr_item_video_list, parent, false);
        BaseViewHolder baseViewHolder = new BaseViewHolder(inflate);

        baseViewHolder.itemView.setOnClickListener(v -> {

            if (mOnRecyclerViewItemClickAnimator != null && mOnRecyclerViewItemClickAnimator.isRunning()) {
                return;
            }

            int temp = baseViewHolder.getAdapterPosition();

            if (temp != currentPos) {

                VrSyncPlayInfo.obtain().restoreVideoInfo();
                EventBus.getDefault().post(VrCancelTimeTask.obtain());

                BaseViewHolder viewHolderForAdapterPositionOut = (BaseViewHolder) ((RecyclerView) parent).findViewHolderForAdapterPosition(currentPos);
                if (viewHolderForAdapterPositionOut != null) {
                    // 旧的View startOut
                    hidePreView(viewHolderForAdapterPositionOut.itemView);
                    if (mOnRecyclerViewItemClickAnimator != null) {
                        mOnRecyclerViewItemClickAnimator.startOut(viewHolderForAdapterPositionOut.itemView);
                    }
                }
                currentPos = temp;
                if (mPostCurrentPosListener != null) {
                    mPostCurrentPosListener.postCurrentPos(currentPos);
                }

                // 新View startIn
                if (mOnRecyclerViewItemClickAnimator != null) {
                    mOnRecyclerViewItemClickAnimator.startIn(v);
                }
                showPreView(v);
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
            if (mOnRecyclerViewItemClickAnimator != null) {
                mOnRecyclerViewItemClickAnimator.showInImmediately(holder.itemView);
            }
            showPreView(holder.itemView);
        } else {
            if (mOnRecyclerViewItemClickAnimator != null) {
                mOnRecyclerViewItemClickAnimator.showOutImmediately(holder.itemView);
            }
            hidePreView(holder.itemView);
        }
    }

    private void hidePreView(View view) {
        view.findViewById(R.id.group_video_item_play).setVisibility(View.GONE);
    }

    private void showPreView(View view) {
        view.findViewById(R.id.group_video_item_play).setVisibility(View.VISIBLE);
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
        currentPos = pos;
        if (pos >= 0) {
            if (holder != null) {
                if (mOnRecyclerViewItemClickAnimator != null) {
                    mOnRecyclerViewItemClickAnimator.startIn(holder.itemView);
                }
                showPreView(holder.itemView);
                if (mVideoInfoCallback != null && mVideoContentLists != null) {
                    mVideoInfoCallback.postVideoContent(mVideoContentLists.get(currentPos), currentPos, mVideoContentLists.size());
                }
                if (mBgAndMenuVideoUrlCallback != null && mVideoContentLists != null) {
                    mBgAndMenuVideoUrlCallback.postVideoBgAndMenuVideoUrl(
                            ConfigManager.getInstance().getDefaultUrl() + mVideoContentLists.get(currentPos).getImgUrl() + ImageProcess.process(2560, 1600),
                            ConfigManager.getInstance().getDefaultUrl() + mVideoContentLists.get(currentPos).getMenuVideoUrl());
                }
            }
        }
    }

    private VideoInfoCallback mVideoInfoCallback;
    private BgAndMenuVideoUrlCallback mBgAndMenuVideoUrlCallback;
    private PostCurrentPosListener mPostCurrentPosListener;

    public void setPostCurrentPosListener(PostCurrentPosListener postCurrentPosListener) {
        mPostCurrentPosListener = postCurrentPosListener;
    }

    public void setVideoInfoCallback(VideoInfoCallback videoInfoCallback) {
        mVideoInfoCallback = videoInfoCallback;
    }

    public void setBgAndMenuVideoUrlCallback(BgAndMenuVideoUrlCallback bgAndMenuVideoUrlCallback) {
        mBgAndMenuVideoUrlCallback = bgAndMenuVideoUrlCallback;
    }

    public int getPositionFromVideoIdAndType(int videoId, int videoType) {
        for (int i = 0; i < mVideoContentLists.size(); i++) {
            VideoContentList temp = mVideoContentLists.get(i);
            if (temp.getId() == videoId && temp.getType() == videoType) {
                return i;
            }
        }
        return 0;
    }

    public interface BgAndMenuVideoUrlCallback {
        void postVideoBgAndMenuVideoUrl(String bg, String videoUrl);
    }

    public interface PostCurrentPosListener {
        void postCurrentPos(int index);
    }

    public interface VideoInfoCallback {
        void postVideoContent(VideoContentList contentList, int pos, int total);
    }

}
