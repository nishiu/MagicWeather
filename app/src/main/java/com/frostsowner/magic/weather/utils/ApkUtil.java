package com.frostsowner.magic.weather.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.fm.commons.http.ContextHolder;

import java.io.File;

/**
 * apk util
 * <br/>
 * Apk文件操作相关工具类
 *
 */
public class ApkUtil {

    /**
     * get UnInstallApkPackageName
     *
     * @param context Context
     * @param apkPath apkPath
     * @return apk PackageName
     */
    public static String getUnInstallApkPackageName(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            if (appInfo != null) {
                return appInfo.packageName;
            }
        }
        return null;
    }

    /**
     * install an apk by apkPath
     *
     * @param context Context
     * @param apkPath apkPath
     */
    public static final void installApk(Context context, String apkPath) {
        if (TextUtils.isEmpty(apkPath)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri contentUri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            contentUri = FileProvider.getUriForFile(context, ContextHolder.get().getPackageName() +".fileProvider",new File(apkPath));

        }
        else{
            contentUri = Uri.fromFile(new File(apkPath));
        }
        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
    
    /**
     * unstall an apk by packgeName
     * 
     * @param context
     * @param packgeName
     */
    public static final void unInstallApk(Context context, String packgeName){
    	Uri packageURI = Uri.parse("package:"+packgeName);   
    	Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);   
    	context.startActivity(uninstallIntent);
    }

    /**
     * check whether app installed
     *
     * @param context
     * @param packageName
     * @return
     */
    @SuppressLint("WrongConstant")
    public static boolean checkAppInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_INSTRUMENTATION);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 拉起app
     * @param context
     * @param targetPackageName
     */
    public static void launcherTargetApp(Context context,String targetPackageName){
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(targetPackageName);
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
