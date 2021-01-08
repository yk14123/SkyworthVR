package com.chinafocus.hvrskyworthvr.service.event;

public class VrSyncMediaStatus {

    // 1.播放 2.暂停
    private int playStatusTag;

    public int getPlayStatusTag() {
        return playStatusTag;
    }

    public void setPlayStatusTag(int playStatusTag) {
        this.playStatusTag = playStatusTag;
    }

    private static final VrSyncMediaStatus vrConnect = new VrSyncMediaStatus();

    public static VrSyncMediaStatus obtain() {
        return vrConnect;
    }
}
