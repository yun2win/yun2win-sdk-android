package com.y2w.uikit.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.y2w.uikit.common.ExternalStorage;
import com.y2w.uikit.common.StorageType;

import java.io.File;

/**
 * Created by maa2 on 2016/1/21.
 */
public class StorageUtil {

    public final static long K = 1024;
    public final static long M = 1024 * 1024;

    private static final long THRESHOLD_WARNING_SPACE = 100 * M;

    public static final long THRESHOLD_MIN_SPCAE = 20 * M;

    public static void init(Context context, String rootPath) {
        ExternalStorage.getInstance().init(context, rootPath);
    }

    /**
     *
     * @param fileName
     * @param fileType
     * @return
     */
    public static String getWritePath(String fileName, StorageType fileType) {
        return getWritePath(null, fileName, fileType, false);
    }

    /**
     *
     * @param fileName
     *
     * @param tip
     *
     */
    private static String getWritePath(Context context, String fileName, StorageType fileType, boolean tip) {
        String path = ExternalStorage.getInstance().getWritePath(fileName, fileType);
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File dir = new File(path).getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }

    /**
     *
     */
    public static boolean isExternalStorageExist() {
        return ExternalStorage.getInstance().isSdkStorageReady();
    }


    /**
     *
     * @param context
     * @param fileType
     * @param tip
     * @return false:
     */
    public static boolean hasEnoughSpaceForWrite(Context context, StorageType fileType, boolean tip) {
        if (!ExternalStorage.getInstance().isSdkStorageReady()) {
            return false;
        }

        long residual = ExternalStorage.getInstance().getAvailableExternalSize();
        if (residual < fileType.getStorageMinSize()) {
            return false;
        } else if (residual < THRESHOLD_WARNING_SPACE) {
        }

        return true;
    }
    /**
     *
     * @param fileName
     * @param fileType
     * @return
     */
    public static String getReadPath(String fileName, StorageType fileType) {
        return ExternalStorage.getInstance().getReadPath(fileName, fileType);
    }

    /**
     *
     * @param context
     * @param fileName
     * @param fileType
     * @return
     */
    public static String getWritePath(Context context, String fileName, StorageType fileType) {
        return getWritePath(context, fileName, fileType, true);
    }

    public static String getDirectoryByDirType(StorageType fileType) {
        return ExternalStorage.getInstance().getDirectoryByDirType(fileType);
    }

    public static String getSystemImagePath() {
        if (Build.VERSION.SDK_INT > 7) {
            String picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            return picturePath + "/nim/";
        } else {
            String picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
            return picturePath + "/nim/";
        }
    }

    public static boolean isInvalidVideoFile(String filePath) {
        return filePath.toLowerCase().endsWith(".3gp")
                || filePath.toLowerCase().endsWith(".mp4");
    }
}
