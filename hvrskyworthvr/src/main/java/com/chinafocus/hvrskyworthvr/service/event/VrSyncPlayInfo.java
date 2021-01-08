package com.chinafocus.hvrskyworthvr.service.event;

import androidx.annotation.NonNull;

public class VrSyncPlayInfo {
    // 1是全景出版 2是全景视频
    private int mTag;
    private int mCategory;
    private int mVideoId;
    private long mSeekTime;

    private static final VrSyncPlayInfo vrSyncPlayInfo = new VrSyncPlayInfo();

    public static VrSyncPlayInfo obtain() {
        return vrSyncPlayInfo;
    }

    private VrSyncPlayInfo() {
        initState();
    }

    public void setTag(int mTag) {
        this.mTag = mTag;
    }

    @SuppressWarnings("unused")
    public void setCategory(int mCategory) {
        this.mCategory = mCategory;
    }

    @SuppressWarnings("unused")
    public void setVideoId(int mVideoId) {
        this.mVideoId = mVideoId;
    }

    public void setSeekTime(long mSeekTime) {
        this.mSeekTime = mSeekTime;
    }

    public int getTag() {
        return mTag;
    }

    public int getCategory() {
        return mCategory;
    }

    public int getVideoId() {
        return mVideoId;
    }

    public long getSeekTime() {
        return mSeekTime;
    }

    public void saveAllState(int tag, int category, int videoId, long seek) {
        mTag = tag;
        mCategory = category;
        mVideoId = videoId;
        mSeekTime = seek;
    }

    public void restoreVideoInfo() {
        mVideoId = -1;
        mSeekTime = 0L;
    }

    public void clearVideoTime() {
        mSeekTime = 0L;
    }

    /**
     * 回复初始状态
     */
    public void initState() {
        mTag = 2;
        mCategory = 0;
        restoreVideoInfo();
    }


    @Override
    @NonNull
    public String toString() {
        return "VrSyncPlayInfo{" +
                "tag=" + mTag +
                ", category=" + mCategory +
                ", videoId=" + mVideoId +
                ", seek=" + mSeekTime +
                '}';
    }
}
