package com.chinafocus.hvrskyworthvr.service.event;

public class VrSyncMediaStatus {

    // 1.播放 2.暂停
    private int playStatusTag;
    // -1的话不需要seek
    private long mSeek;

    private static final VrSyncMediaStatus vrConnect = new VrSyncMediaStatus();

    public static VrSyncMediaStatus obtain() {
        return vrConnect;
    }

    public void saveAllState(int tag, long seek) {
        playStatusTag = tag;
        mSeek = seek;
    }

    public long getSeek() {
        return mSeek;
    }

    public int getPlayStatusTag() {
        return playStatusTag;
    }

    public boolean seekNow() {
        return mSeek != -1;
    }

    @Override
    public String toString() {
        return "VrSyncMediaStatus{" +
                "playStatusTag=" + (playStatusTag == 1 ? "VR端控制Pad播放" : "VR端控制Pad暂停") +
                ", mSeek=" + mSeek +
                '}';
    }
}
