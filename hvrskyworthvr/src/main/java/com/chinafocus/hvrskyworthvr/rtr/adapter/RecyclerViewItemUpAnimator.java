package com.chinafocus.hvrskyworthvr.rtr.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class RecyclerViewItemUpAnimator implements OnRecyclerViewItemClickAnimator {

    private final ObjectAnimator mZoomInObjectAnimator;
    private final ObjectAnimator mZoomOutObjectAnimator;
    private final UpAnimationListener mUpAnimationListener;
    private final DownAnimationListener mDownAnimationListener;

    public RecyclerViewItemUpAnimator() {
        mUpAnimationListener = new UpAnimationListener();
        mDownAnimationListener = new DownAnimationListener();

        mZoomInObjectAnimator = ObjectAnimator.ofFloat(null, "number", 0f, 1.0f);
        mZoomInObjectAnimator.setDuration(100);
        mZoomInObjectAnimator.setInterpolator(new LinearInterpolator());
        mZoomInObjectAnimator.addUpdateListener(mUpAnimationListener);
        mZoomInObjectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mUpAnimationListener.clear();
                mZoomInObjectAnimator.removeUpdateListener(mUpAnimationListener);
            }
        });

        mZoomOutObjectAnimator = ObjectAnimator.ofFloat(null, "number", 0f, 1.0f);
        mZoomOutObjectAnimator.setDuration(100);
        mZoomOutObjectAnimator.setInterpolator(new LinearInterpolator());
        mZoomOutObjectAnimator.addUpdateListener(mDownAnimationListener);
        mZoomOutObjectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mDownAnimationListener.clear();
                mZoomOutObjectAnimator.removeUpdateListener(mDownAnimationListener);
            }
        });
    }

    @Override
    public void startIn(View v) {
        mZoomInObjectAnimator.addUpdateListener(mUpAnimationListener);
        mUpAnimationListener.setView(v);
        mZoomInObjectAnimator.start();
    }

    @Override
    public void startOut(View v) {
        mZoomOutObjectAnimator.addUpdateListener(mDownAnimationListener);
        mDownAnimationListener.setView(v);
        mZoomOutObjectAnimator.start();
    }

    @Override
    public void showInImmediately(View view) {
        view.setTranslationY(-40);
    }

    @Override
    public void showOutImmediately(View view) {
        view.setTranslationY(0);
    }

    @Override
    public boolean isRunning() {
        return mZoomInObjectAnimator.isRunning() || mZoomOutObjectAnimator.isRunning();
    }

    private static class UpAnimationListener implements ValueAnimator.AnimatorUpdateListener {

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
            float v1 = fraction * (-40);
            mView.setTranslationY(v1);
        }
    }

    private static class DownAnimationListener implements ValueAnimator.AnimatorUpdateListener {

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
            float v2 = fraction * 40 - 40;
            mView.setTranslationY(v2);
        }
    }

}
