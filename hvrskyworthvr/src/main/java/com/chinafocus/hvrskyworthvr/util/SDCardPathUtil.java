package com.chinafocus.hvrskyworthvr.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author
 * @date 2020/6/3
 * description：
 */
public class SDCardPathUtil {
    public static final int INNER = 0;
    public static final int OUTER = 1;

    @IntDef({INNER, OUTER})
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SDCardPathType {
    }


    /**
     * 返回sd卡的 /Android/data/包名/files 目录的地址
     * 内置sd卡根路径：/storage/emulated/0/Android/data/com.chinafocus.hvr_local_v2/cache
     * 外置sd卡根路径：/storage/0123-4567/Android/data/com.chinafocus.hvr_local_v2/cache
     *
     * @param context
     * @param pathType
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static String getExternalCache(Context context, @SDCardPathType int pathType) {
        String root = "";

        if (pathType == OUTER) {
            root = getPhysicalExternalFilePathAboveM() + "/Android/data/" + context.getPackageName() + "/cache";
        } else if (pathType == INNER) {
            root = context.getExternalCacheDir().getAbsolutePath();
        }

        return root;
    }


    /**
     * 返回sd卡的 /Android/data/包名/files 目录的地址
     * 内置sd卡根路径：/storage/emulated/0/Android/data/com.chinafocus.hvr_local_v2/files
     * 外置sd卡根路径：/storage/0123-4567/Android/data/com.chinafocus.hvr_local_v2/files
     *
     * @param context
     * @param pathType
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static String getExternalFiles(Context context, @SDCardPathType int pathType) {
        String root = "";

        if (pathType == OUTER) {
            root = getPhysicalExternalFilePathAboveM() + "/Android/data/" + context.getPackageName() + "/files";
        } else if (pathType == INNER) {
            root = context.getExternalFilesDir("").getAbsolutePath();
        }

        return root;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private static String externalSDCardPath(Context context, boolean isRemove) {
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            // 7.0才有的方法
            List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
            Class<?> volumeClass = Class.forName("android.os.storage.StorageVolume");
            Method getPath = volumeClass.getDeclaredMethod("getPath");
            Method isRemovable = volumeClass.getDeclaredMethod("isRemovable");
            getPath.setAccessible(true);
            isRemovable.setAccessible(true);
            for (int i = 0; i < storageVolumes.size(); i++) {
                StorageVolume storageVolume = storageVolumes.get(i);
                String mPath = (String) getPath.invoke(storageVolume);
                Boolean remove = (Boolean) isRemovable.invoke(storageVolume);
                Log.d("tag2", "mPath is === " + mPath + "isRemoveble == " + isRemove);
                if (isRemove == remove) {
                    return mPath;
                }
            }
        } catch (Exception e) {
            Log.d("tag2", "e == " + e.getMessage());
        }
        return "";
    }

    /**
     * 6.0使用此方法获取外置SD卡路径，尝试过反射{@link StorageManager##getVolumeList}
     * 但StorageVolume非Public API 编译不通过（7.0改为公开API）,故使用UserEnvironment
     * 的内部方法getExternalDirs获取所有的路径，通过{@link Environment#isExternalStorageRemovable(File)}
     * 判断若removable则为外部存储
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static String getPhysicalExternalFilePathAboveM() {
        try {
            //===========================获取UserEnvironment================
            Class<?> userEnvironment = Class.forName("android.os.Environment$UserEnvironment");
            Method getExternalDirs = userEnvironment.getDeclaredMethod("getExternalDirs");
            getExternalDirs.setAccessible(true);
            //========获取构造UserEnvironment的必要参数UserId================
            Class<?> userHandle = Class.forName("android.os.UserHandle");
            Method myUserId = userHandle.getDeclaredMethod("myUserId");
            myUserId.setAccessible(true);
            int mUserId = (int) myUserId.invoke(UserHandle.class);
            Constructor<?> declaredConstructor = userEnvironment.getDeclaredConstructor(Integer.TYPE);
            // 得到UserEnvironment instance
            Object instance = declaredConstructor.newInstance(mUserId);
            File[] files = (File[]) getExternalDirs.invoke(instance);
            for (int i = 0; i < files.length; i++) {
                if (Environment.isExternalStorageRemovable(files[i])) {
                    return files[i].getPath();
                }
            }
        } catch (Exception e) {
//            CrashHandler.getInstance().saveExceptionAsCrash(e);
            Log.d("tag2", "e == " + e.getMessage());
        }
        return "";
    }
}
