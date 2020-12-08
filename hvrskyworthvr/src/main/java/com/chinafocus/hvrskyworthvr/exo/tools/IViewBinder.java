package com.chinafocus.hvrskyworthvr.exo.tools;

/**
 * @author
 * @date 2020/7/7
 * description：
 */
public interface IViewBinder {
    // 应该关闭横屏已经展示的View
    void shouldCloseLandView();

    // 应该关闭竖屏已经展示的View
    void shouldCloseProView();
}
