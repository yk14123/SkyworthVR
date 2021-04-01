package com.chinafocus.hvrskyworthvr.util;

import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

public class ViewClickUtil {

    @SuppressWarnings("all")
    public static void click(View view, Runnable runnable) {
        RxView.clicks(view)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(unit -> {
                    if (runnable != null) {
                        runnable.run();
                    }
                });
    }
}
