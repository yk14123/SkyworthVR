package com.chinafocus.huaweimdm.tools;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimerTaskManager {
    // 时间间隔
    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;
    // 首次开始的时间
    private Date mDate;

    private static TimerTaskManager sTimerTaskManager;
    private Timer mTimer;

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
        Calendar calendar = Calendar.getInstance();

        /*** 定制每日2:00执行方法 ***/

        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        mDate = calendar.getTime(); //第一次执行定时任务的时间
//        System.out.println(mDate);
//        System.out.println("before 方法比较：" + mDate.before(new Date()));
        //如果第一次执行定时任务的时间 小于 当前的时间
        //此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。循环执行的周期则以当前时间为准
        if (mDate.before(new Date())) {
            mDate = this.addDay(mDate, 1);
            System.out.println(mDate);
        }

    }

    public boolean isStartTimerTask() {
        return mTimer != null;
    }

    public void startTimerTask(final Context context) {
        if (mTimer == null) {
            mTimer = new Timer();
            //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    MdmTools.getInstance().reboot(context);
                }
            }, mDate);
        }
    }

    public void cancelTimerTask() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    // 增加或减少天数
    private Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }
}