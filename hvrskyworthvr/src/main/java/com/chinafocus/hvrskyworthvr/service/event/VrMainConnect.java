package com.chinafocus.hvrskyworthvr.service.event;

public class VrMainConnect {
    private static final VrMainConnect vrConnect = new VrMainConnect();

    public static VrMainConnect obtain() {
        return vrConnect;
    }
}
