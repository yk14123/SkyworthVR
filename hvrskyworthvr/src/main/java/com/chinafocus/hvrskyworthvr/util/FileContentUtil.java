package com.chinafocus.hvrskyworthvr.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class FileContentUtil {

    public static String readCount(Context context, String fileName) {
        File file = new File(context.getExternalFilesDir("Config"), fileName);
        if (!file.exists()) {
            try {
                boolean newFile = file.createNewFile();
                if (newFile) {
                    Log.e("MyLog", " VideoCount.txt 文件不存在，创建文件");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (
                    FileReader fis = new FileReader(file);
                    BufferedReader bis = new BufferedReader(fis)
            ) {
                String s = bis.readLine();
                return TextUtils.isEmpty(s) ? "0" : s;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "0";
    }

    public static void writeCount(Context context, String fileName, String content) {
        File file = new File(context.getExternalFilesDir("Config"), fileName);
        if (!file.exists()) {
            try {
                boolean newFile = file.createNewFile();
                if (newFile) {
                    Log.e("MyLog", " VideoCount.txt 文件不存在，创建文件");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (
                FileOutputStream fos = new FileOutputStream(file);
        ) {
            fos.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
