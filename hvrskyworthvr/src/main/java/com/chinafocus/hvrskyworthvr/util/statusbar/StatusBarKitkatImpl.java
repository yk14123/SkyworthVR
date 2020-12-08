package com.chinafocus.hvrskyworthvr.util.statusbar;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

class StatusBarKitkatImpl implements IStatusBar {

    public void setStatusBarColor(Window window, int color, boolean lightStatusBar) {
        int flags = 0;
        if (Build.VERSION.SDK_INT >= 19) {
            flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            window.addFlags(flags);
        }

        ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
        View statusBarView = new View(window.getContext());
        int statusBarHeight = getStatusBarHeight(window.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(color);
        decorViewGroup.addView(statusBarView);
        StatusBarCompat.setFitsSystemWindows(window, true);

        StatusBarCompatFlavorRom.setLightStatusBar(window, lightStatusBar);

    }

    /**
     * 适配虚拟按键
     *
     * @param context
     * @return
     */
    @SuppressWarnings("JavaDoc")
    private static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }


}
