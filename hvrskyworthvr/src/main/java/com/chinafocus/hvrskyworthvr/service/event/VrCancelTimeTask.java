package com.chinafocus.hvrskyworthvr.service.event;

public class VrCancelTimeTask {
    private static final VrCancelTimeTask vrCancelTimeTask = new VrCancelTimeTask();

    public static VrCancelTimeTask obtain() {
        return vrCancelTimeTask;
    }
}
