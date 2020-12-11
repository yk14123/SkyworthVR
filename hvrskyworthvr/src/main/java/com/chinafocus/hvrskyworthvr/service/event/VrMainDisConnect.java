package com.chinafocus.hvrskyworthvr.service.event;

public class VrMainDisConnect {
    private static final VrMainDisConnect vrDisconnect = new VrMainDisConnect();

    public static VrMainDisConnect obtain() {
        return vrDisconnect;
    }
}
