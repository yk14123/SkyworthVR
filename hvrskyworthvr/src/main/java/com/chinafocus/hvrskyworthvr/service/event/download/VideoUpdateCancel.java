package com.chinafocus.hvrskyworthvr.service.event.download;

public class VideoUpdateCancel {
    private static final VideoUpdateCancel vrConnect = new VideoUpdateCancel();

    public static VideoUpdateCancel obtain() {
        return vrConnect;
    }
}
