package com.chinafocus.hvrskyworthvr.service.event;

public class NotifyVideoContentList {
    private static final NotifyVideoContentList vrConnect = new NotifyVideoContentList();

    public static NotifyVideoContentList obtain() {
        return vrConnect;
    }
}
