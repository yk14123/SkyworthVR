package com.chinafocus.hvrskyworthvr.service.event.download;

public class VideoUpdateLatest {
    private static final VideoUpdateLatest vrConnect = new VideoUpdateLatest();

    public static VideoUpdateLatest obtain() {
        return vrConnect;
    }
}
