package com.chinafocus.hvrskyworthvr.service.event;

public class VrDisConnect {
    private static final VrDisConnect vrDisconnect = new VrDisConnect();

    public static VrDisConnect obtain() {
        return vrDisconnect;
    }
}
