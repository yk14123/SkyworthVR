package com.chinafocus.hvrskyworthvr.util;

public class TimeOutClickUtil {

    private int count = 1;
    private long startTime;

    private int timeOutMilliseconds;
    private int timeOutClickCount;

    private static TimeOutClickUtil defaultInstance;
    private static TimeOutClickUtil mdmInstance;

    public TimeOutClickUtil(int timeOutMilliseconds, int timeOutClickCount) {
        this.timeOutMilliseconds = timeOutMilliseconds;
        this.timeOutClickCount = timeOutClickCount;
    }

    public static TimeOutClickUtil getDefault() {
        if (defaultInstance == null) {
            defaultInstance = new TimeOutClickUtil(3000, 10);
        }
        return defaultInstance;
    }

    public static TimeOutClickUtil getMDM() {
        if (mdmInstance == null) {
            mdmInstance = new TimeOutClickUtil(5000, 20);
        }
        return mdmInstance;
    }

    public void startTimeOutClick(Runnable runnable) {
        if (count == 1) {
            startTime = System.currentTimeMillis();
        }
        count++;
        long endTime = System.currentTimeMillis();
        if ((endTime - startTime) > timeOutMilliseconds || count > timeOutClickCount) {
            count = 1;
        } else if (count == timeOutClickCount && (endTime - startTime) < timeOutMilliseconds) {
            if (runnable != null) {
                runnable.run();
            }
            count = 1;
        }
    }
}
