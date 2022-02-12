package com.frostsowner.magic.weather.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

public class NavigationUtils {

    @SuppressLint("ObsoleteSdkInt")
    public static boolean isHuaWeiNavigationHide(Context context){
        if(!Build.MANUFACTURER.contains("HUAWEI"))return false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return Settings.System.getInt(context.getContentResolver(), "navigationbar_is_min", 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(), "navigationbar_is_min", 0) != 0;
        }
    }

    public static boolean isViVONavigationHide(Context context){
        if(!Build.MANUFACTURER.contains("VIVO") && !Build.MANUFACTURER.contains("vivo"))return false;
        return Settings.Secure.getInt(context.getContentResolver(), "navigation_gesture_on", 0) != 0;
    }

    public static boolean isMiuiNavigationHide(Context context){
        if(TextUtils.isEmpty(System.getProperty("ro.miui.ui.version.name")))return false;
        return Settings.Global.getInt(context.getContentResolver(), "force_fsg_nav_bar", 0) != 0;
    }
}
