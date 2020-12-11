package com.chinafocus.hvrskyworthvr.service.event;

public class VrMediaConnect {
    private static final VrMediaConnect vrConnect = new VrMediaConnect();

    public static VrMediaConnect obtain() {
        return vrConnect;
    }
}
