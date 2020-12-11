package com.chinafocus.hvrskyworthvr.service.event;

public class VrSyncPlayInfo {

    public int tag;
    public int category;
    public int videoId = -1;
    public long seek;

    private static final VrSyncPlayInfo vrSyncPlayInfo = new VrSyncPlayInfo();

    public static VrSyncPlayInfo obtain() {
        return vrSyncPlayInfo;
    }

    @Override
    public String toString() {
        return "VrSyncPlayInfo{" +
                "tag=" + tag +
                ", category=" + category +
                ", videoId=" + videoId +
                ", seek=" + seek +
                '}';
    }
}
