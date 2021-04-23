package com.chinafocus.hvrskyworthvr.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class LocalLogUtils {

    // CHECKED = false 表示需要写入本地文件
    public static boolean CHECKED = false;
    private static String TAG = "LogUtil";
    private static String logPath = null;                   //log日志存放路径
    private static final char VERBOSE = 'v';
    private static final char INFO = 'i';
    private static final char DEBUG = 'd';
    private static final char WARN = 'w';
    private static final char ERROR = 'e';

    public static volatile boolean isUpload = true;

    /**
     * 初始化，须在使用之前设置，最好在Application创建时调用,获得文件储存路径,在后面加"/log"建立子文件夹
     */
    public static void init(Context context) {
        File localLog = context.getExternalFilesDir("LocalLog");
        if (localLog != null) {
            logPath = localLog.getAbsolutePath();
        }
    }

    public static void v(String tag, String msg) {
        if (CHECKED) {
            Log.v(tag, msg);
        } else {
            Log.v(tag, msg);
            writeToFile(VERBOSE, tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (CHECKED) {
            Log.d(tag, msg);
        } else {
            Log.d(tag, msg);
            writeToFile(DEBUG, tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (CHECKED) {
            Log.i(tag, msg);
        } else {
            Log.i(tag, msg);
            writeToFile(INFO, tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (CHECKED) {
            Log.w(tag, msg);
        } else {
            Log.w(tag, msg);
            writeToFile(WARN, tag, msg);
        }
    }

    /**
     * 将log信息写入文件中
     *
     * @param type log级别
     * @param tag  logTag
     * @param msg  logMsg
     */
    private static void writeToFile(char type, String tag, String msg) {
        if (null == logPath) {
            Log.e(TAG, "logPath == null ，未初始化LogToFile");
            return;
        }

        if (!isUpload) {
            return;
        }

        File file = new File(logPath, "debug_log.txt");
        String fileName = file.getAbsolutePath();
        clearCaches(fileName);
//        if (!new File(logPath).exists()) {
//            new File(logPath).mkdirs();
//        }
        String log = DateUtil.getDate() + "--- ( log." + type + " ---> TAG: " + tag + " : " + msg + ") \n";//日志命名规则:时间+日志类型+key+内容
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            fos = new FileOutputStream(fileName, true);//这里的第二个参数代表追加还是覆盖，true为追加，flase为覆盖
            bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            bw.write(log);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();//关闭缓冲流
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void e(String tag, String msg) {
        if (CHECKED) {
            Log.e(tag, msg);
        } else {
            Log.e(tag, msg);
            writeToFile(ERROR, tag, msg);
        }
    }

    /**
     * 清空缓存目录
     */
    @SuppressWarnings("all")
    public static void clearCaches(String Path) {
        File file = new File(Path);
        if (file.length() / 1024 > 1024 * 3) {
            file.delete();
            LocalLogUtils.e(TAG, "清空一次数据,当前时间:" + DateUtil.getDate());
        }
    }
}