package com.chinafocus.hvrskyworthvr.service.event;

public class VrMediaSyncMediaInfo {
    private static final VrMediaSyncMediaInfo vrConnect = new VrMediaSyncMediaInfo();

    public static VrMediaSyncMediaInfo obtain() {
        return vrConnect;
    }
}
