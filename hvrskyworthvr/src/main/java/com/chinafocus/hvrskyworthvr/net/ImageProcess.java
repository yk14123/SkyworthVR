package com.chinafocus.hvrskyworthvr.net;

import com.chinafocus.hvrskyworthvr.global.ConfigManager;

public class ImageProcess {

    public static String process(int w, int h) {
        String process = "";
        if (ConfigManager.getInstance().isALICloud()) {
            process = "?x-oss-process=image/resize,m_fill,h_" + h + ",w_" + w;
        } else if (ConfigManager.getInstance().isHWCloud()) {
            process = "?x-image-process=image/resize,w_" + w;
        }
        
        return process;
    }

}
