package com.chinafocus.hvrskyworthvr.service.event;

public class VrConnect {
    private static final VrConnect vrConnect = new VrConnect();

    public static VrConnect obtain() {
        return vrConnect;
    }
}
