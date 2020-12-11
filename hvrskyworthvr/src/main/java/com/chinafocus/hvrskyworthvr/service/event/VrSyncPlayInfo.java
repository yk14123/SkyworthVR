package com.chinafocus.hvrskyworthvr.service.event;

public class VrSyncPlayInfo {

    public String tag;
    public int category;
    public int videoId;
    public long seek;

    private static final VrSyncPlayInfo vrSyncPlayInfo = new VrSyncPlayInfo();

    public static VrSyncPlayInfo obtain() {
        return vrSyncPlayInfo;
    }
}
