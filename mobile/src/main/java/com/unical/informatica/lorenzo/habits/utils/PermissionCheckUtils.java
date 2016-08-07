package com.unical.informatica.lorenzo.habits.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by Quoc Cuong on 2/13/2016.
 */
public class PermissionCheckUtils {

    public static boolean hasPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
}
