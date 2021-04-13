package com.chinafocus.hvrskyworthvr.util;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class MyRollHandler extends Handler {

    // 轮询切换的时间
    private static int Time = 8000;

    private ViewPager2 mViewPager2;
    private int mCurrentItem;
    private ViewPager2Callback callback;

    public MyRollHandler(@NonNull Looper looper) {
        super(looper);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void bindViewPager2(ViewPager2 viewPager) {
        mViewPager2 = viewPager;

        mViewPager2.setOnTouchListener((v, event) -> {
            final int action = event.getAction();
            final int actionMasked = action & MotionEvent.ACTION_MASK;
            switch (actionMasked) {
                // 因为return false！所以DOWN事件是无法捕获的！只有MOVE和UP事件
                // 这里如果return true的话，ViewPager会无法滑动！！
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    //移除所有的消息，不再自动轮播
                    removeCallbacksAndMessages(null);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    //再次发送消息
                    sendEmptyMessageDelayed(0, Time);
                    break;
            }

            return false;
        });
    }

    public void postCurrentItem(int currentItem) {
        mCurrentItem = currentItem;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        mViewPager2.setCurrentItem(++mCurrentItem, true);
        removeCallbacksAndMessages(null);
        sendEmptyMessageDelayed(0, Time);
    }

    public void cancel() {
        removeCallbacksAndMessages(null);
        mViewPager2.unregisterOnPageChangeCallback(callback);
        callback = null;
    }

    public void start() {
        if (callback == null) {
            callback = new ViewPager2Callback();
        }
        mViewPager2.registerOnPageChangeCallback(callback);
        removeCallbacksAndMessages(null);
        sendEmptyMessageDelayed(0, Time);
    }

    private class ViewPager2Callback extends ViewPager2.OnPageChangeCallback {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // 滑动的时候，改变pos值
            postCurrentItem(position);
        }

        @Override
        public void onPageSelected(int position) {
            // 滑动的时候，改变pos值
            postCurrentItem(position);
        }
    }

}