package com.chinafocus.hvrskyworthvr.ui.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

public class BackgroundAnimationRelativeLayout extends RelativeLayout {
    private LayerDrawable layerDrawable;
    private ObjectAnimator objectAnimator;

    public BackgroundAnimationRelativeLayout(Context context) {
        super(context);
    }

    public BackgroundAnimationRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BackgroundAnimationRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ColorDrawable colorDrawable = new ColorDrawable(0xff000000);
//        Drawable backgroundDrawable = getContext().getDrawable(R.drawable.ic_blackground);
        Drawable[] drawables = new Drawable[2];
        /*初始化时先将前景与背景颜色设为一致*/
        drawables[0] = colorDrawable;
        drawables[1] = colorDrawable;
        layerDrawable = new LayerDrawable(drawables);
    //监听动画的执行
        objectAnimator = ObjectAnimator.ofFloat(this, "number", 0f, 1.0f);
        objectAnimator.setDuration(100);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

//                animation   0-1  0  开始     1结束   (VSYNC  - start)%duration/duration
                int foregroundAlpha = (int) ((float) animation.getAnimatedValue() * 255);
                layerDrawable.getDrawable(1).setAlpha(foregroundAlpha);
                setBackground(layerDrawable);

            }
        });
//
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onAnimationEnd(Animator animation) {
                /*动画结束后，记得将原来的背景图及时更新*/
                    layerDrawable.setDrawable(0, layerDrawable.getDrawable(
                            1));
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    @SuppressLint("NewApi")
    public void setForeground(Drawable drawable) {
        layerDrawable.setDrawable(1, drawable);
        objectAnimator.start();
    }

}
