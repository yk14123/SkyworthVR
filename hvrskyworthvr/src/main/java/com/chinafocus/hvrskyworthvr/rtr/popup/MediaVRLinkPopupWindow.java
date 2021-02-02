package com.chinafocus.hvrskyworthvr.rtr.popup;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.chinafocus.hvrskyworthvr.R;

import razerdp.basepopup.BasePopupWindow;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

/**
 * 一个极其好用的第三方PopupWindow。真香！
 * 修改后：集成了RecyclerView列表Material Design
 */
public class MediaVRLinkPopupWindow extends BasePopupWindow {


    public MediaVRLinkPopupWindow(Context context) {
        super(context);
        setPopupGravity(Gravity.CENTER | Gravity.START);
        setOutSideTouchable(true);
        setAlignBackground(false);
        setOverlayStatusbar(true);
        setBackgroundColor(0x00000000);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_media_vr_link);
    }

    /**
     * 添加自定义开启动画
     *
     * @return
     */
    @Override
    protected Animation onCreateShowAnimation() {
        Animation showAnimation = new TranslateAnimation(
                RELATIVE_TO_SELF, 0,
                RELATIVE_TO_SELF, 0,
                RELATIVE_TO_SELF, -1,
                RELATIVE_TO_SELF, 0);
        showAnimation.setDuration(200);
        return showAnimation;
    }

    /**
     * 添加自定义关闭动画
     *
     * @return
     */
    @Override
    protected Animation onCreateDismissAnimation() {
        Animation showAnimation = new TranslateAnimation(
                RELATIVE_TO_SELF, 0,
                RELATIVE_TO_SELF, 0,
                RELATIVE_TO_SELF, 0,
                RELATIVE_TO_SELF, -1);
        showAnimation.setDuration(200);
        return showAnimation;
    }
}
