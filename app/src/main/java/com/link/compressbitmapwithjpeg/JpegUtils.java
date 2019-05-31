package com.link.compressbitmapwithjpeg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.File;
import java.io.IOException;

public class JpegUtils {
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * 压缩图片
     * @param bitmap
     * @param width
     * @param height
     * @param fileName
     * @param quality
     * @return
     */
    public static native String compressBitmapPath(Bitmap bitmap, int width, int height, String fileName, int quality);

    /**
     * 生产压缩图片的路径
     * @param context
     * @param tempFile
     * @return
     */
    public static String generateFilePath(Context context, File tempFile) {
        File file = new File(context.getCacheDir().getPath() + File.pathSeparator + FileUtil.FILES_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath() + File.separator + FileUtil.splitFileName(FileUtil.getFileName(context, Uri.fromFile(tempFile)))[0] + "." + "jpg";
    }

    /**
     * 矫正被旋转的图片
     * @param path
     * @return
     */
    public static Bitmap getCompressBitmap(String path){
        Bitmap bmp = BitmapFactory.decodeFile(path).copy(Bitmap.Config.ARGB_8888, true);
        //check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            bmp = Bitmap.createBitmap(bmp, 0, 0,
                    bmp.getWidth(), bmp.getHeight(),
                    matrix, true);
            return bmp;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
