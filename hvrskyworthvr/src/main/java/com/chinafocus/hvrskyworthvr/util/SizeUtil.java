package com.chinafocus.hvrskyworthvr.util;

import android.text.format.Formatter;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.Utils;

public class SizeUtil {

    public static String getFsAvailableSize(String path) {
        long fsAvailableSize = FileUtils.getFsAvailableSize(path);
        String s = Formatter.formatFileSize(Utils.getApp().getApplicationContext(), fsAvailableSize);
        return s.replaceAll(" ", "");
    }

    public static String getFsTotalSize(String path) {
        long fsTotalSize = FileUtils.getFsTotalSize(path);
        String s = Formatter.formatFileSize(Utils.getApp().getApplicationContext(), fsTotalSize);
        return s.replaceAll(" ", "");
    }
}
