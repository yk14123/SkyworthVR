package com.chinafocus.hvrskyworthvr.service.event;

public class VrMainStickyInactiveDialog {
    private static final VrMainStickyInactiveDialog VR_MAIN_STICKY_INACTIVE_DIALOG = new VrMainStickyInactiveDialog();

    public static VrMainStickyInactiveDialog obtain() {
        return VR_MAIN_STICKY_INACTIVE_DIALOG;
    }
}
