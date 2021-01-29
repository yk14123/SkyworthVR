package com.chinafocus.hvrskyworthvr.rtr.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDataInfo;
import com.chinafocus.hvrskyworthvr.ui.adapter.BaseViewHolder;
import com.chinafocus.hvrskyworthvr.util.ObjectAnimatorViewUtil;

import java.util.List;

public class RtrVideoListViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final List<VideoDataInfo> mBannerLists;

    private int currentPos = 0;

    public RtrVideoListViewAdapter(List<VideoDataInfo> bannerLists) {
        mBannerLists = bannerLists;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.rtr_item_video_list, parent, false);
        BaseViewHolder baseViewHolder = new BaseViewHolder(inflate);


        baseViewHolder.itemView.setOnClickListener(v -> {

            int temp = baseViewHolder.getAdapterPosition();


//            int firstVisibleItemPosition = ((LinearLayoutManager) (((RecyclerView) parent).getLayoutManager())).findFirstVisibleItemPosition();
//            int lastVisibleItemPosition = ((LinearLayoutManager) (((RecyclerView) parent).getLayoutManager())).findLastVisibleItemPosition();

//            Log.e("MyLog", " 当前点击位置是 >>> " + temp
//                    + " 当前first可以看见的位置 >>> " + firstVisibleItemPosition
//                    + " 当前last可以看见的位置 >>> " + lastVisibleItemPosition);

//            if (firstVisibleItemPosition <= currentPos && currentPos <= lastVisibleItemPosition) {

            BaseViewHolder viewHolderForAdapterPositionOut = (BaseViewHolder) ((RecyclerView) parent).findViewHolderForAdapterPosition(currentPos);
            if (viewHolderForAdapterPositionOut != null) {
                // 旧的View startOut
                ObjectAnimatorViewUtil viewOut = new ObjectAnimatorViewUtil(viewHolderForAdapterPositionOut.itemView);
                viewOut.startOut();
            }
//            }

            BaseViewHolder viewHolderForAdapterPositionIn = (BaseViewHolder) ((RecyclerView) parent).findViewHolderForAdapterPosition(temp);
            //新View startIn
            ObjectAnimatorViewUtil objectAnimatorViewUtil = new ObjectAnimatorViewUtil(viewHolderForAdapterPositionIn.itemView);
            objectAnimatorViewUtil.startIn();


            currentPos = temp;
//            if (currentPos != temp) {

            // 旧的View startOut
//                int recycledViewCount = ((RecyclerView) parent).getRecycledViewPool().getRecycledViewCount(0);
//                Log.e("MyLog", " recycledViewCount 》》》 " + recycledViewCount);
//                int i = temp % recycledViewCount;

//                View childAt = ((RecyclerView) parent).getChildAt(temp);
//
//                ObjectAnimatorViewUtil out = new ObjectAnimatorViewUtil(childAt);
//                ObjectAnimator objectAnimatorOut = out.getObjectAnimatorOut();
//
//                ObjectAnimatorViewUtil objectAnimatorViewUtil = new ObjectAnimatorViewUtil(v);
//                // 新View
//                ObjectAnimator objectAnimatorIn = objectAnimatorViewUtil.getObjectAnimatorIn();
//
//                AnimatorSet set = new AnimatorSet();
//                set.setDuration(100);
//                set.playSequentially(objectAnimatorOut, objectAnimatorIn);//同时执行
//                set.playTogether(objectAnimatorOut, objectAnimatorIn);//同时执行
//        set.setStartDelay(100);//延迟执行
//        set.playSequentially(a1,a2,a3);//顺序执行
//        set.play(a1).with(a2);//a1,a2同时执行，之后执行a3
//        set.play(a3).after(a2);
//                set.start();

//                currentPos = temp;
//            }

        });

        return baseViewHolder;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        BaseViewHolder baseHolder = (BaseViewHolder) ((RecyclerView) holder.itemView.getParent()).findViewHolderForAdapterPosition(currentPos);

        if (baseHolder == holder) {

            Log.e("MyLog", " onViewAttachedToWindow >>> baseHolder == holder ");

//            holder.itemView.setPadding(20, 0, 20, 0);
//            layoutParams.width = 640;
            holder.itemView.setScaleX(1.12f);
            holder.itemView.setScaleY(1.12f);
            int height = holder.itemView.getHeight();
            float v1 = height * 0.06f;
            holder.itemView.setTranslationY(-v1);
        } else {
            Log.e("MyLog", " onViewAttachedToWindow >>> baseHolder != holder ");
            holder.itemView.setScaleX(1.f);
            holder.itemView.setScaleY(1.f);
//            holder.itemView.setPadding(20, 40, 20, 0);
//            layoutParams.width = 560;
            holder.itemView.setTranslationY(0);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        // if position == currentPos 才变大

//        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (position == currentPos) {
//            holder.itemView.setPadding(20, 0, 20, 0);
//            layoutParams.width = 640;
            holder.itemView.setScaleX(1.12f);
            holder.itemView.setScaleY(1.12f);
            int height = holder.itemView.getHeight();
            float v1 = height * 0.06f;
            holder.itemView.setTranslationY(-v1);
        } else {
            holder.itemView.setScaleX(1.f);
            holder.itemView.setScaleY(1.f);
//            holder.itemView.setPadding(20, 40, 20, 0);
//            layoutParams.width = 560;
            holder.itemView.setTranslationY(0);
        }
//        holder.itemView.setLayoutParams(layoutParams);

    }

    @Override
    public int getItemCount() {
        return 10;
    }

}
