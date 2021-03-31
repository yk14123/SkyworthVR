package com.chinafocus.hvrskyworthvr.rtr.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import com.chinafocus.hvrskyworthvr.util.TimeUtil;
import com.chinafocus.hvrskyworthvr.util.ViewClickUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowRtrVideoListViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final List<VideoContentList> mVideoContentLists;

    private Callback mCallback;
    private final Map<String, GradientDrawable> mGradientDrawableMap;

    public void setOnClickCallback(Callback callback) {
        mCallback = callback;
    }

    public ShowRtrVideoListViewAdapter(List<VideoContentList> videoContentLists) {
        mVideoContentLists = videoContentLists;
        mGradientDrawableMap = new HashMap<>();
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
        holder.setText(R.id.tv_video_classify, mVideoContentLists.get(position).getClassName());
        holder.setText(R.id.tv_video_list_duration, TimeUtil.timeParse(mVideoContentLists.get(position).getDuration()));

        handleGradientDrawable(holder, position);
    }

    private String[] mColors = {"#597EF7", "#36CFC9", "#40A9FF", "#ff0000"};
    private int mColorIndex;

    private void handleGradientDrawable(BaseViewHolder holder, int position) {

        String classify = mVideoContentLists.get(position).getClassify();

        GradientDrawable temp = mGradientDrawableMap.get(classify);
        if (temp == null) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setCornerRadii(new float[]{0.f, 0.f, 0.f, 0.f, 20.f, 20.f, 0.f, 0.f});
            gradientDrawable.setColor(Color.parseColor(mColors[mColorIndex++]));
            mGradientDrawableMap.put(classify, gradientDrawable);
            temp = gradientDrawable;
        }

        holder.getView(R.id.tv_video_classify).setBackground(temp);
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
