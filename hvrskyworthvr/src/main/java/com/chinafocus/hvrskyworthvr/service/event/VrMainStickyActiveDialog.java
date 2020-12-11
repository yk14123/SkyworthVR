package com.chinafocus.hvrskyworthvr.service.event;

public class VrMainStickyActiveDialog {
    private static final VrMainStickyActiveDialog VR_MAIN_STICK_CONNECT = new VrMainStickyActiveDialog();

    public static VrMainStickyActiveDialog obtain() {
        return VR_MAIN_STICK_CONNECT;
    }
}
