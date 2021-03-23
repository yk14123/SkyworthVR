package com.chinafocus.hvrskyworthvr.rtr.adapter;

import android.view.View;

public interface OnRecyclerViewItemClickAnimator {

    void startOut(View view);

    void startIn(View view);

    boolean isRunning();

    void showInImmediately(View view);

    void showOutImmediately(View view);
}