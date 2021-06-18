package com.chinafocus.hvrskyworthvr.rtr.adapter;

import androidx.recyclerview.widget.DiffUtil;

import com.chinafocus.hvrskyworthvr.model.bean.VideoContentList;

import java.util.List;

public class DiffUtilCallback extends DiffUtil.Callback {
    
    private List<VideoContentList> mOldDatas, mNewDatas;

    public DiffUtilCallback(List<VideoContentList> oldDatas, List<VideoContentList> newDatas) {
        mOldDatas = oldDatas;
        mNewDatas = newDatas;
    }

    //老数据集size
    @Override
    public int getOldListSize() {
        return mOldDatas != null ? mOldDatas.size() : 0;
    }

    //新数据集size
    @Override
    public int getNewListSize() {
        return mNewDatas != null ? mNewDatas.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldDatas.get(oldItemPosition).equals(mNewDatas.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        VideoContentList oldData = mOldDatas.get(oldItemPosition);
        VideoContentList newData = mNewDatas.get(newItemPosition);

        if (!oldData.getImgUrl().equals(newData.getImgUrl())) {
            return false;
        }

        if (!oldData.getMenuVideoUrl().equals(newData.getMenuVideoUrl())) {
            return false;
        }

        if (!oldData.getTitle().equals(newData.getTitle())) {
            return false;
        }

        return oldData.getIntro().equals(newData.getIntro());
    }
}
