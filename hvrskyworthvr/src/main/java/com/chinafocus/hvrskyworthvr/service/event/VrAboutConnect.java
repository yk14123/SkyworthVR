package com.chinafocus.hvrskyworthvr.service.event;

public class VrAboutConnect {
    private static final VrAboutConnect vrConnect = new VrAboutConnect();

    public static VrAboutConnect obtain() {
        return vrConnect;
    }
}
