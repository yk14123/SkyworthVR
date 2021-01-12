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
import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.model.bean.Banner;
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
import static com.chinafocus.hvrskyworthvr.ui.main.video.VideoFragment.CURRENT_CATEGORY;

public class BannerViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final List<Banner> mBannerLists;
    private final ColorFilterTransformation colorFilterTransformation;

    public BannerViewAdapter(@NonNull List<Banner> bannerLists) {
        mBannerLists = bannerLists;
        colorFilterTransformation = new ColorFilterTransformation(0x1A000000);
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
            String type = mBannerLists.get(adapterPosition).getType();

            Intent intent = new Intent(parent.getContext(), MediaPlayActivity.class);
            intent.putExtra(MEDIA_ID, id);
            intent.putExtra(MEDIA_FROM_TAG, type.equals("video") ? 2 : 1);

            int media_category_tag;
            if (id == 10082) {
                // 红色力量根据地
                media_category_tag = 15;
            } else if (id == 10091) {
                // 奇松与怪石
                media_category_tag = 13;
            } else {
                media_category_tag = CURRENT_CATEGORY;
            }

            intent.putExtra(MEDIA_CATEGORY_TAG, media_category_tag);

            ((Activity) parent.getContext()).startActivityForResult(intent, REQUEST_CODE_PAD_MEDIA_ACTIVITY);
        });
        return baseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

        Banner banner = mBannerLists.get(position % mBannerLists.size());

        holder
                .setText(R.id.tv_banner_title, banner.getTitle())
                .setText(R.id.tv_banner_des, banner.getIntro());

        Glide.with(holder.itemView.getContext())
                .load(Constants.DEFAULT_URL + banner.getCoverImg())
                .apply(RequestOptions.bitmapTransform(colorFilterTransformation))
                .into((ImageView) holder.getView(R.id.iv_banner_bg));

    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }
}
