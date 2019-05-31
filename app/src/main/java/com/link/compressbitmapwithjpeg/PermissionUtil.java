package com.link.compressbitmapwithjpeg;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * Created by Megumi on 2017/11/7.
 */

public class PermissionUtil {

    public static final String[] CAMERA_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public static final String[] READANDWRITE_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final String[] BLE_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public static final int REQUEST_CAMERA_PERMISSION_CODE = 1000;
    public static final int REQUEST_LOCATION_PERMISSION_CODE = 1100;
    public static final int REQUEST_READANDWRITE_PERMISSION_CODE = 1200;
    public static final int REQUEST_BLE_PERMISSION_CODE = 1200;

    public static boolean checkPermissions(Context context, Object object, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    if (object instanceof Activity) {
                        ActivityCompat.requestPermissions((Activity) object, permissions, requestCode);
                    } else if (object instanceof Fragment) {
                        ((Fragment) object).requestPermissions(permissions, requestCode);
                    }
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }
}
