package com.chinafocus.hvrskyworthvr.vr;

public interface SkyWorthVrBus {

    interface MainBus {

        // CONNECT
        // 2. Pad 位于一级界面的视频列表时 VR 被激活，VR 也进入 APP 一级界面与 Pad 端相同的视频列表；
        // 同时，Pad 端进入「不可选片状态」，显示出遮罩。，如果Pad在首页，则打开mainDialog
        void activeMainDialog();

        // DISCONNECT
        // 2.VR 位于一级界面视频列表内时（Pad 正位于视频列表的遮罩界面）
        // VR休眠，Pad需要关闭mainDialog
        void inactiveMainDialog();

        // SYNC_PLAY
        // 3.当VR开始播放的时候，Pad需要进入播放页面
        void goToMediaPlayActivityAndActiveVRPlayerStatus();

    }

    interface MediaPlayBus {

        // CONNECT
        // TODO Pad 切换到 VR
        // 1. Pad 正在播放视频时 VR 被激活。需要告诉unity开始播哪个视频
        void toUnityMediaInfoAndActiveVRPlayerStatus();

        // DISCONNECT
        // TODO VR 切换到 Pad
        // 1.VR 正在播放视频时（Pad 也正在同步播放视频）
        // VR 切换到休眠待机状态后，Pad 需要同时退出视频播放界面并回到视频列表，开启「自由操作状态」。
        void goBackMainActivityAndInactiveMainDialog();

        // 3. Pad 位于播放结束界面时，如果此时 VR 被激活则 VR 端直接进入一级视频列表界面，Pad 回到视频列表界面的「不可选片状态」
        // 不用接受命令。
        // 当链接状态，播放结束后
        void goBackMainActivityAndActiveMainDialog();

        // SYNC_ROTATION
        // 同步VR方向
        void syncRotation(float[] rotation);


    }

}
