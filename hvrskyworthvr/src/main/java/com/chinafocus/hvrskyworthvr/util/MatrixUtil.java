package com.chinafocus.hvrskyworthvr.util;

public class MatrixUtil {

    public static void setRotationWithQuaternion(float[] rm, float x, float y, float z, float w) {
        rm[3] = 0;
        rm[7] = 0;
        rm[11] = 0;

        rm[12] = 0;
        rm[13] = 0;
        rm[14] = 0;
        rm[15] = 1;

        rm[0] = 2 * ((float) Math.pow(x, 2) + (float) Math.pow(w, 2)) - 1;
        rm[1] = 2 * (x * y + z * w);
        rm[2] = 2 * (x * z - y * w);

        rm[4] = 2 * (x * y - z * w);
        rm[5] = 2 * ((float) Math.pow(y, 2) + (float) Math.pow(w, 2)) - 1;
        rm[6] = 2 * (y * z + x * w);

        rm[8] = 2 * (x * z + y * w);
        rm[9] = 2 * (y * z - x * w);
        rm[10] = 2 * ((float) Math.pow(z, 2) + (float) Math.pow(w, 2)) - 1;
    }

}
