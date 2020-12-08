package com.chinafocus.hvrskyworthvr.exo.ui;

/**
 * @author
 * @date 2020/1/5
 * description：
 */
public interface IControlViewInterAction {
    // 新增全屏功能
    default void onSwitchFullScreen() {
    }

    // 新增返回
    default void onGoBackActivity() {
    }

    // 新增视频语言切换
    default void onVideoLangChange() {
    }

    // 新增视频分辨率切换
    default void onVideoRatioChange() {
    }

    // 连接Vr眼镜
    default void onLinkVR() {
    }

    // 进入屏保播放画面
    default void onEnterScreen() {
    }

    // 视频方向回正
    default void onVideoContentReset() {
    }

    // 播放下一个视频
    default void onVideoNextPlay() {
    }

    // ================== 下面3个接口 澳门快讯项目用不到

    // 视频设置画面与投诉
    default void onVideoSetting() {
    }

    // 展示视频详情
    default void onVideoDetail() {
    }

    // 进入主页面
    default void onEnterHome() {
    }

}
