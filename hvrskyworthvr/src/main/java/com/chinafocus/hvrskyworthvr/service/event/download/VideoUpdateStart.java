package com.chinafocus.hvrskyworthvr.service.event.download;

public class VideoUpdateStart {
    private static final VideoUpdateStart vrConnect = new VideoUpdateStart();

    public static VideoUpdateStart obtain() {
        return vrConnect;
    }
}
