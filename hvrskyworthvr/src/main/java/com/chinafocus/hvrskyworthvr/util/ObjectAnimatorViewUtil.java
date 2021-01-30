package com.chinafocus.hvrskyworthvr.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class ObjectAnimatorViewUtil {

    private final ObjectAnimator mZoomInObjectAnimator;
    private final ObjectAnimator mZoomOutObjectAnimator;
    private final ZoomInAnimationListener mZoomInAnimationListener;
    private final ZoomOutAnimationListener mZoomOutAnimationListener;

    private static ObjectAnimatorViewUtil instance;

    public static ObjectAnimatorViewUtil getInstance() {
        if (instance == null) {
            synchronized (ObjectAnimatorViewUtil.class) {
                if (instance == null) {
                    instance = new ObjectAnimatorViewUtil();
                }
            }
        }
        return instance;
    }

    private ObjectAnimatorViewUtil() {
        mZoomInAnimationListener = new ZoomInAnimationListener();
        mZoomOutAnimationListener = new ZoomOutAnimationListener();

        mZoomInObjectAnimator = ObjectAnimator.ofFloat(null, "number", 0f, 1.0f);
        mZoomInObjectAnimator.setDuration(100);
        mZoomInObjectAnimator.setInterpolator(new LinearInterpolator());
        mZoomInObjectAnimator.addUpdateListener(mZoomInAnimationListener);
        mZoomInObjectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mZoomInAnimationListener.clear();
                mZoomInObjectAnimator.removeUpdateListener(mZoomInAnimationListener);
            }
        });

        mZoomOutObjectAnimator = ObjectAnimator.ofFloat(null, "number", 0f, 1.0f);
        mZoomOutObjectAnimator.setDuration(100);
        mZoomOutObjectAnimator.setInterpolator(new LinearInterpolator());
        mZoomOutObjectAnimator.addUpdateListener(mZoomOutAnimationListener);
        mZoomOutObjectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mZoomOutAnimationListener.clear();
                mZoomOutObjectAnimator.removeUpdateListener(mZoomOutAnimationListener);
            }
        });
    }

    public void startIn(View v) {
        mZoomInObjectAnimator.addUpdateListener(mZoomInAnimationListener);
        mZoomInAnimationListener.setView(v);
        mZoomInObjectAnimator.start();
    }

    public void startOut(View v) {
        mZoomOutObjectAnimator.addUpdateListener(mZoomOutAnimationListener);
        mZoomOutAnimationListener.setView(v);
        mZoomOutObjectAnimator.start();
    }

    public void showZoomInImmediately(View view) {
        view.setScaleX(1.12f);
        view.setScaleY(1.12f);
        float v1 = view.getHeight() * 0.06f;
        view.setTranslationY(-v1);
    }

    public void showZoomOutImmediately(View view) {
        view.setScaleX(1.f);
        view.setScaleY(1.f);
        view.setTranslationY(0);
    }


    public boolean isRunning() {
        return mZoomInObjectAnimator.isRunning() || mZoomOutObjectAnimator.isRunning();
    }

    private static class ZoomInAnimationListener implements ValueAnimator.AnimatorUpdateListener {

        private View mView;

        public void setView(View view) {
            mView = view;
        }

        public void clear() {
            mView = null;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float fraction = animation.getAnimatedFraction();

            int height = mView.getHeight();
            float v1 = fraction * (height * 0.06f);

            float scale = fraction * 0.12f + 1.f;
            mView.setScaleX(scale);
            mView.setScaleY(scale);

            mView.setTranslationY(-v1);
        }
    }

    private static class ZoomOutAnimationListener implements ValueAnimator.AnimatorUpdateListener {

        private View mView;

        public void setView(View view) {
            mView = view;
        }

        public void clear() {
            mView = null;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float fraction = animation.getAnimatedFraction();
            float scale = fraction * -0.12f + 1.12f;

            int height = mView.getHeight();
            float v1 = height * 0.06f;
            float v2 = fraction * -v1 + v1;

            mView.setScaleX(scale);
            mView.setScaleY(scale);

            mView.setTranslationY(-v2);
        }
    }

}
