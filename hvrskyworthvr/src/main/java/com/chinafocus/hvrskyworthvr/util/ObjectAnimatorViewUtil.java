package com.chinafocus.hvrskyworthvr.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ObjectAnimatorViewUtil {

    private final ObjectAnimator objectAnimatorIn;
    private final ObjectAnimator objectAnimatorOut;

    public ObjectAnimatorViewUtil(View v) {

        objectAnimatorIn = ObjectAnimator.ofFloat(v, "number", 0f, 1.0f);
        objectAnimatorIn.setDuration(100);
        objectAnimatorIn.setInterpolator(new LinearInterpolator());
        objectAnimatorIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();

                int height = v.getHeight();
                float v1 = fraction * (height * 0.06f);

                float scale = fraction * 0.12f + 1.f;
                v.setScaleX(scale);
                v.setScaleY(scale);

                v.setTranslationY(-v1);

            }
        });

        objectAnimatorOut = ObjectAnimator.ofFloat(v, "number", 0f, 1.0f);
        objectAnimatorOut.setDuration(100);
        objectAnimatorOut.setInterpolator(new LinearInterpolator());
        objectAnimatorOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float fraction = animation.getAnimatedFraction();
                float scale = fraction * -0.12f + 1.12f;

                int height = v.getHeight();
                float v1 = height * 0.06f;
                float v2 = fraction * -v1 + v1;

                v.setScaleX(scale);
                v.setScaleY(scale);

                v.setTranslationY(-v2);

            }
        });
    }

    public void startIn() {
        objectAnimatorIn.start();
    }

    public void startOut() {
        objectAnimatorOut.start();
    }

}
