package com.chinafocus.hvrskyworthvr.service.event.download;

public class VideoUpdateListError {
    private static final VideoUpdateListError vrConnect = new VideoUpdateListError();

    public static VideoUpdateListError obtain() {
        return vrConnect;
    }
}
