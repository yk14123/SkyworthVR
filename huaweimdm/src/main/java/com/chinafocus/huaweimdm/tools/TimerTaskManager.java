package com.chinafocus.huaweimdm.tools;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 每天凌晨1点中开始下载任务
 */
public class TimerTaskManager {
    // 时间间隔
    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;
    // 首次开始的时间
    private Date mCheckVideoDownloadDate = new Date();
    private Date mCancelVideoDownloadDate = new Date();
    private Date mCancelAppDownloadDate = new Date();

    private static volatile TimerTaskManager sTimerTaskManager;
    private Timer mCheckVideoDownloadTask;
    private Timer mCancelVideoDownloadTask;
    private Timer mCancelAppDownloadTask;

    public static TimerTaskManager getInstance() {
        if (sTimerTaskManager == null) {
            synchronized (TimerTaskManager.class) {
                if (sTimerTaskManager == null) {
                    sTimerTaskManager = new TimerTaskManager();
                }
            }
        }
        return sTimerTaskManager;
    }

    private TimerTaskManager() {
        initDate(mCheckVideoDownloadDate, 1);
        initDate(mCancelVideoDownloadDate, 6);
        initDate(mCancelAppDownloadDate, 8);
    }

    /*** 定制每日hour:minute:second执行方法 ***/
    private void initDate(Date date, int hour) {
        this.initDate(date, hour, 0, 0);
    }

    private void initDate(@NonNull Date date, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        Date temp = calendar.getTime(); //第一次执行定时任务的时间
        if (temp.before(new Date())) {
            // 如果第一次执行定时任务的时间 小于 当前的时间
            // 此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。循环执行的周期则以当前时间为准
            temp = this.addDay(temp, 1);
            System.out.println(temp);
        }
        date.setTime(temp.getTime());
    }

//    public boolean isStartTimerTask() {
//        return mCheckVideoDownloadTask != null;
//    }

    public void startCheckVideoDownloadTask(final Runnable runnable) {
        if (mCheckVideoDownloadTask == null && runnable != null) {
            mCheckVideoDownloadTask = new Timer();
            //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
            mCheckVideoDownloadTask.schedule(new TimerTask() {
                @Override
                public void run() {
                    runnable.run();
                }
            }, mCheckVideoDownloadDate, PERIOD_DAY);
        }
    }

    public void startCancelVideoDownloadTask(final Runnable runnable) {
        if (mCancelVideoDownloadTask == null && runnable != null) {
            mCancelVideoDownloadTask = new Timer();
            mCancelVideoDownloadTask.schedule(new TimerTask() {
                @Override
                public void run() {
                    runnable.run();
                }
            }, mCancelVideoDownloadDate, PERIOD_DAY);
        }
    }

    public void startCancelAppDownloadTask(final Runnable runnable) {
        if (mCancelAppDownloadTask == null && runnable != null) {
            mCancelAppDownloadTask = new Timer();
            mCancelAppDownloadTask.schedule(new TimerTask() {
                @Override
                public void run() {
                    runnable.run();
                }
            }, mCancelAppDownloadDate, PERIOD_DAY);
        }
    }

//    public void cancelCheckVideoDownloadTask() {
//        if (mCheckVideoDownloadTask != null) {
//            mCheckVideoDownloadTask.cancel();
//            mCheckVideoDownloadTask = null;
//        }
//    }

    // 增加或减少天数
    private Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }
}