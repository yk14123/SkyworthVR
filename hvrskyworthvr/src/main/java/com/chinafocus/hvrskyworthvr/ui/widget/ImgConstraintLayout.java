package com.chinafocus.hvrskyworthvr.ui.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import com.chinafocus.hvrskyworthvr.R;

import static android.animation.ValueAnimator.INFINITE;


public class ImgConstraintLayout extends FrameLayout {


    private ValueAnimator valueAnimator;

    public ImgConstraintLayout(Context context) {
        this(context, null);
    }

    public ImgConstraintLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImgConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_img_constra, this);
    }

    public void start() {
        if (valueAnimator == null || !valueAnimator.isRunning()) {
            move(findViewById(R.id.light));
        }
    }

    public void remove() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    private void move(final View view) {

        view.bringToFront();
        final int width = getWidth();
        valueAnimator = ValueAnimator.ofFloat(((Integer) (-width - 30)).floatValue(), ((Integer) (width + 30)).floatValue());
        valueAnimator.addUpdateListener(animation -> {
            float aFloat = (float) animation.getAnimatedValue();
            view.setTranslationX(aFloat);
            float alpha = aFloat / width;
            float a1 = (alpha > 0 ? (1 - alpha) : (1 + alpha));
            float a2 = (float) (a1 / 2 + 0.3);
            view.setAlpha(a2);

        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(GONE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                view.setVisibility(VISIBLE);
            }
        });
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        int d = width / 355 - 1;
//        float ff = 1000 * (d * 0.25f + 1);
//        valueAnimator.setDuration(((Float) ff).longValue());
        valueAnimator.setDuration(3000);
        valueAnimator.setRepeatCount(INFINITE);
        valueAnimator.start();
    }

}
