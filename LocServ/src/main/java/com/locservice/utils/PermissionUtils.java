package com.locservice.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionUtils {
    public static final int ACCESS_FINE_LOCATION = 2;
    public static final int ACCESS_COARSE_LOCATION = 3;
    public static final int PERMISSION_REQUEST_CAMERA = 4;
    public static final int WRITE_EXTERNAL_STORAGE = 5;
    public static final int READ_EXTERNAL_STORAGE = 6;

    public static final int READ_PHONE_STATE = 1;

    public static boolean hasPermission(Context context, String permission) {
        return checkPermissionGranted(ContextCompat.checkSelfPermission(context, permission));
    }

    public static boolean checkPermissionGranted(int grantResult) {
        return grantResult == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean ensurePermission(Activity activity, String permission, int requestCode) {
        if (!hasPermission(activity, permission)) {

            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);

            return false;
        }

        return true;
    }
}
