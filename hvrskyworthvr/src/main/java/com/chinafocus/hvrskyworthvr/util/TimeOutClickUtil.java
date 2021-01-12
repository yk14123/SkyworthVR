package com.chinafocus.hvrskyworthvr.util;

public class TimeOutClickUtil {

    private static int count = 1;
    private static long startTime;

    public static int TIME_OUT_MILLISECONDS = 3000;
    public static int TIME_OUT_CLICK_COUNT = 10;

    public static void startTimeOutClick(Runnable runnable) {
        if (count == 1) {
            startTime = System.currentTimeMillis();
        }
        count++;
        long endTime = System.currentTimeMillis();
        if ((endTime - startTime) > TIME_OUT_MILLISECONDS || count > TIME_OUT_CLICK_COUNT) {
            count = 1;
        } else if (count == TIME_OUT_CLICK_COUNT && (endTime - startTime) < TIME_OUT_MILLISECONDS) {
            if (runnable != null) {
                runnable.run();
            }
            count = 1;
        }
    }
}
