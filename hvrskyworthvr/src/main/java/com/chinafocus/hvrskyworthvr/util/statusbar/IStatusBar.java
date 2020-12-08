package com.chinafocus.hvrskyworthvr.util.statusbar;

import android.view.Window;


interface IStatusBar {

    /**
     * 适配沉浸式状态栏
     *
     * @param window
     * @param color
     * @param lightStatusBar
     */
    @SuppressWarnings("JavaDoc")
    void setStatusBarColor(Window window, int color, boolean lightStatusBar);

}
