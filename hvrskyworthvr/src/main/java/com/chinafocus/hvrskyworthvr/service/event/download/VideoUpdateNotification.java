package com.chinafocus.hvrskyworthvr.service.event.download;

public class VideoUpdateNotification {
    private static final VideoUpdateNotification vrConnect = new VideoUpdateNotification();

    public static VideoUpdateNotification obtain() {
        return vrConnect;
    }
}
