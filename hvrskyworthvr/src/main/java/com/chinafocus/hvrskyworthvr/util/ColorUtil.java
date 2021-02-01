package com.chinafocus.hvrskyworthvr.util;

public class ColorUtil {

    public static int calculateColorTemperature(int rgb_R, int rgb_G, int rgb_B) {
        double trimX = 0;
        double trimY = 0;
        double trimZ = 0;
        double coorX = 0, coorY = 0;
        double CCT = 0;
        double n = 0;
        int R = rgb_R;//255;
        int G = rgb_G;//231;
        int B = rgb_B;//131;
        //以下公式实现RGB转三刺激值
        trimX = 2.789 * R + 1.7517 * G + 1.1302 * B;
        trimY = 1 * R + 4.5907 * G + 0.0601 * B;
        trimZ = 0 * R + 0.0565 * G + 5.5943 * B;
        //以下公式实现三刺激值转色坐标
        coorX = trimX / (trimX + trimY + trimZ);
        coorY = trimY / (trimX + trimY + trimZ);
        n = (coorX - 0.3320) / (0.1858 - coorY);
        //以下公式实现色坐标转色温
        CCT = 437 * n * n * n + 3601 * n * n + 6831 * n + 5517;
//        System.out.println("X:" + trimX + "Y:" + trimY + "Z:" + trimZ+"\n CCT:"+CCT);
        return (int) CCT;
    }
}
