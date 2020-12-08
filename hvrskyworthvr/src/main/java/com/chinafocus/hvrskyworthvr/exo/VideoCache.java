package com.chinafocus.hvrskyworthvr.exo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import com.chinafocus.hvrskyworthvr.util.SDCardPathUtil;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;

/**
 * 方法用途 ：视频缓存单例模式
 */
public class VideoCache {
    private static SimpleCache sDownloadCache;

    /**
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    public static SimpleCache getInstance(Context context) {
        if (sDownloadCache == null) {
             String externalSDCardPath = SDCardPathUtil.getExternalCache(context, SDCardPathUtil.INNER);
            // video缓存目录：/storage/0123-4567/Android/data/com.chinafocus.hvr_local_v2/cache/ExoCache/
            // 设置，应用，存储，删除缓存，会连同cache文件夹一起清空！
            // 设置，应用，存储，删除文件，会连同file文件夹+cache文件夹一起清空！
            sDownloadCache = new SimpleCache(new File(externalSDCardPath, "ExoCache"),
                    new LeastRecentlyUsedCacheEvictor(512 * 1024 * 1024),
                    new ExoDatabaseProvider(context));
        }
        return sDownloadCache;
    }

    public static File getMediaCacheFile(Context context) {
        String directoryPath = "";
        String childPath = "exoPlayer";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 外部储存可用
            File externalFiles = context.getExternalFilesDir(childPath);
            if (externalFiles == null) {
                externalFiles = context.getFilesDir();
            }
            directoryPath = externalFiles.getAbsolutePath();
        } else {
            directoryPath = context.getFilesDir().getAbsolutePath();
        }
        File file = new File(directoryPath, childPath);
        //判断文件目录是否存在
        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }


}