package com.chinafocus.hvrskyworthvr.service.event;

public class VrMediaDisConnect {
    private static final VrMediaDisConnect vrDisconnect = new VrMediaDisConnect();

    public static VrMediaDisConnect obtain() {
        return vrDisconnect;
    }
}
