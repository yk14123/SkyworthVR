package com.chinafocus.hvrskyworthvr.util.statusbar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;


/**
 * @author
 * @date 2019/5/8
 * description：
 */
public class StatusBarCompatFactory {

    private static final StatusBarCompatFactory mInstance = new StatusBarCompatFactory();

    private StatusBarCompatFactory() {
    }

    public static StatusBarCompatFactory getInstance() {
        return mInstance;
    }

    /**
     * 内容部分侵入到状态栏，需要布局按钮自己单独做padding处理
     *
     * @param activity
     * @param lightStatusBar
     */
    public void setStatusBarImmerse(Activity activity, boolean lightStatusBar) {
        setStatusBarColor(activity, Color.TRANSPARENT, lightStatusBar, true);
    }

    /**
     * 内容部分不侵入到状态栏，布局按钮不需要自己处理
     * 文字黑字，背景白色
     *
     * @param activity
     */
    public void setStatusBarNoImmerse(Activity activity) {
        setStatusBarColor(activity, Color.TRANSPARENT, true, false);
    }

    /**
     * @param activity       目标activity
     * @param bgColor        状态栏背景色
     * @param lightStatusBar 状态栏文字颜色 true为黑字 false为白字
     * @param immerse        布局内容是否沉浸到状态栏
     */
    public void setStatusBarColor(Activity activity, int bgColor, boolean lightStatusBar, boolean immerse) {
        if (immerse) {
            statusBarCompatImmerse(activity, bgColor, lightStatusBar);
        } else {
            StatusBarCompat.setStatusBarColor(activity, bgColor, lightStatusBar);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void statusBarCompatImmerse(Activity activity, int bgColor, boolean lightStatusBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            if (lightStatusBar) {
                window.getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(bgColor);
        }
    }

    /**
     * 全屏模式，流海屏，挖孔屏适配
     *
     * @param activity
     */
    public void fullScreenCompatImmerse(Activity activity) {
        //1.设置全屏
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = activity.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //华为， 小米，oppo
        //1.判断手机厂商， 2，判断手机是否刘海， 3，设置是否让内容区域延伸进刘海 4，设置控件是否避开刘海区域  5， 获取刘海的高度

        //判断手机是否是刘海屏
        boolean hasDisplayCutout = hasDisplayCutout(window);
        Log.d("StatusBar", "fullScreenCompatImmerse: hasDisplayCutout >>> " + hasDisplayCutout);
        if (hasDisplayCutout) {
            //2.让内容区域延伸进刘海
            WindowManager.LayoutParams params = window.getAttributes();
            /**
             *  * @see #LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT 全屏模式，内容下移，非全屏不受影响
             *  * @see #LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES 允许内容去延伸进刘海区
             *  * @see #LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER 不允许内容延伸进刘海区
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }
            window.setAttributes(params);

            //3.设置成沉浸式
            int flags = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            int visibility = window.getDecorView().getSystemUiVisibility();
            visibility |= flags; //追加沉浸式设置
            window.getDecorView().setSystemUiVisibility(visibility);
        }
    }

    public boolean hasDisplayCutout(Window window) {
        DisplayCutout displayCutout;
        View rootView = window.getDecorView();
        WindowInsets insets = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            insets = rootView.getRootWindowInsets();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && insets != null) {
            displayCutout = insets.getDisplayCutout();
            if (displayCutout != null) {
                if (displayCutout.getBoundingRects() != null
                        && displayCutout.getBoundingRects().size() > 0
                        && displayCutout.getSafeInsetTop() > 0) {
                    return true;
                }
            }
        }
        return true; //因为模拟器原因，这里设置成true
    }

    //通常情况下，刘海的高就是状态栏的高
    public int heightForDisplayCutout(Activity activity) {
        int resID = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resID > 0) {
            return activity.getResources().getDimensionPixelSize(resID);
        }
        return 96;
    }

//    /**
//     * 增加View的paddingTop,增加的值为状态栏高度 (智能判断，并设置高度)
//     */
//    public static void setPaddingSmart(Context context, View view) {
//        ViewGroup.LayoutParams lp = view.getLayoutParams();
//        if (lp != null && lp.height > 0) {
//            lp.height += ScreenUtils.getStatusBarHeight(context);//增高
//        }
//        view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + ScreenUtils.getStatusBarHeight(context),//增高
//                view.getPaddingRight(), view.getPaddingBottom());
//    }
//
//    /**
//     * 增加View上边距（MarginTop）一般是给高度为 WARP_CONTENT 的小控件用的
//     */
//    public static void setMargin(Context context, View view) {
//        ViewGroup.LayoutParams lp = view.getLayoutParams();
//        if (lp instanceof ViewGroup.MarginLayoutParams) {
//            ((ViewGroup.MarginLayoutParams) lp).topMargin += ScreenUtils.getStatusBarHeight(context);//增高
//        }
//        view.setLayoutParams(lp);
//    }

}
