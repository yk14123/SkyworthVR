package com.chinafocus.hvrskyworthvr.service.event.download;

import com.chinafocus.hvrskyworthvr.download.VideoUpdateStatus;

public class VideoUpdateManagerStatus {
    private static final VideoUpdateManagerStatus vrConnect = new VideoUpdateManagerStatus();

    public static VideoUpdateManagerStatus obtain() {
        return vrConnect;
    }

    private int currentIndex;
    private int total;

    private VideoUpdateStatus mVideoUpdateStatus;

    public VideoUpdateStatus getVideoUpdateStatus() {
        return mVideoUpdateStatus;
    }

    public void setVideoUpdateStatus(VideoUpdateStatus videoUpdateStatus) {
        mVideoUpdateStatus = videoUpdateStatus;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
