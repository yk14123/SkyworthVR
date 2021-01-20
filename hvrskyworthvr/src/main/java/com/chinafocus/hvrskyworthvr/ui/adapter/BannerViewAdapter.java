package com.chinafocus.hvrskyworthvr.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.global.ConfigManager;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDataInfo;
import com.chinafocus.hvrskyworthvr.net.ImageProcess;
import com.chinafocus.hvrskyworthvr.service.event.VrCancelTimeTask;
import com.chinafocus.hvrskyworthvr.service.event.VrSyncPlayInfo;
import com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import jp.wasabeef.glide.transformations.ColorFilterTransformation;

import static com.chinafocus.hvrskyworthvr.global.Constants.REQUEST_CODE_PAD_MEDIA_ACTIVITY;
import static com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity.MEDIA_CATEGORY_TAG;
import static com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity.MEDIA_FROM_TAG;
import static com.chinafocus.hvrskyworthvr.ui.main.media.MediaPlayActivity.MEDIA_ID;

public class BannerViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final List<VideoDataInfo> mBannerLists;
    private final ColorFilterTransformation colorFilterTransformation;
    private final int mTag;

    public BannerViewAdapter(@NonNull List<VideoDataInfo> bannerLists, int tag) {
        mBannerLists = bannerLists;
        colorFilterTransformation = new ColorFilterTransformation(0x1A000000);
        mTag = tag;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        BaseViewHolder baseViewHolder = new BaseViewHolder(inflate);
        baseViewHolder.itemView.setOnClickListener(v -> {

            VrSyncPlayInfo.obtain().clearVideoTime();
            EventBus.getDefault().post(VrCancelTimeTask.obtain());

            int adapterPosition = baseViewHolder.getAdapterPosition() % mBannerLists.size();
            int id = mBannerLists.get(adapterPosition).getId();

            Intent intent = new Intent(parent.getContext(), MediaPlayActivity.class);
            intent.putExtra(MEDIA_ID, id);
            intent.putExtra(MEDIA_FROM_TAG, mTag);
            if (mTag == 2) {
                intent.putExtra(MEDIA_CATEGORY_TAG, Integer.parseInt(mBannerLists.get(adapterPosition).getClassify()));
            }

            ((Activity) parent.getContext()).startActivityForResult(intent, REQUEST_CODE_PAD_MEDIA_ACTIVITY);
        });
        return baseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

        VideoDataInfo banner = mBannerLists.get(position % mBannerLists.size());

        holder
                .setText(R.id.tv_banner_title, banner.getTitle())
                .setText(R.id.tv_banner_des, banner.getIntro());

        Glide.with(holder.itemView.getContext())
                .load(ConfigManager.getInstance().getDefaultUrl() + banner.getImgUrl() + ImageProcess.process(2160, 1080))
                .apply(RequestOptions.bitmapTransform(colorFilterTransformation))
                .into((ImageView) holder.getView(R.id.iv_banner_bg));

    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }
}
