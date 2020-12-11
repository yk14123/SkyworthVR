package com.chinafocus.hvrskyworthvr.service.event;

public class VrMediaWaitSelected {
    private static final VrMediaWaitSelected vrConnect = new VrMediaWaitSelected();

    public static VrMediaWaitSelected obtain() {
        return vrConnect;
    }
}
