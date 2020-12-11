package com.chinafocus.hvrskyworthvr.service.event;

public class VrMainSyncMediaInfo {
    private static final VrMainSyncMediaInfo vrConnect = new VrMainSyncMediaInfo();

    public static VrMainSyncMediaInfo obtain() {
        return vrConnect;
    }
}
