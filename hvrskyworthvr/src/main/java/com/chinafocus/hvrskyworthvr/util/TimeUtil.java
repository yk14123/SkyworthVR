package com.chinafocus.hvrskyworthvr.util;

/**
 * @author
 * @date 2019/12/17
 * description：
 */
public class TimeUtil {
    /**
     * Android 音乐播放器应用里，读出的音乐时长为 long 类型以[秒]为单位
     * 例如：将 261 转化为分钟和秒应为 04:21 （包含四舍五入）
     *
     * @param duration 音乐时长
     * @return 时间格式
     */
    public static String timeParse(long duration) {
        String time = "";
        long minute = duration / 60;
        long seconds = duration % 60;
        long second = Math.round((float) seconds);
        if (minute < 10) {
            time += "0";
        }
        time += minute + ":";
        if (second < 10) {
            time += "0";
        }
        time += second;
        return time;
    }
}
