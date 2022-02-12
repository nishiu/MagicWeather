package com.frostsowner.magic.weather.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;

import java.lang.reflect.Method;
import java.util.List;

public class NotchUtils {

    private static int notchHeight;

    public static void readNotchHeight (Activity context){
        if(notchHeight != 0)return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = context.getWindow().getAttributes();
            // 仅当缺口区域完全包含在状态栏之中时，才允许窗口延伸到刘海区域显示
//            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
            // 永远不允许窗口延伸到刘海区域
//            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            // 始终允许窗口延伸到屏幕短边上的刘海区域
//            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
//            context.getWindow().setAttributes(lp);
            boolean cutout = lp.layoutInDisplayCutoutMode == WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT ||
                    lp.layoutInDisplayCutoutMode == WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            Log.e("ijimu","cutout is "+cutout);
        }else{
            Log.e("ijimu","cutout is false");
        }
        if(Build.VERSION.SDK_INT >= 28){
            if(hasNotchInScreenAtHuawei(context)){
                notchHeight = getNotchSizeAtHuawei(context);
            }
            else if(hasNotchInScreenAtOppo(context)){
                notchHeight = getNotchSizeAtOppo();
            }
            else{
                context.getWindow().getDecorView().setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                        Log.d("ijimu","获取刘海屏数据");
                        if(insets != null){
                            DisplayCutout cutout = insets.getDisplayCutout();
                            Log.d("ijimu","cutout = "+cutout);
                            if(cutout != null){
                                List<Rect> rects = cutout.getBoundingRects();
                                Log.d("ijimu","rects = "+rects);
                                if(rects != null){
                                    Log.d("ijimu","notch left : "+cutout.getSafeInsetLeft()+
                                            " , top : "+cutout.getSafeInsetTop()+" , right : "+cutout.getSafeInsetRight()+
                                            " , bottom : "+cutout.getSafeInsetBottom());
                                    int height  = Math.abs(cutout.getSafeInsetBottom() - cutout.getSafeInsetTop());
                                    setNotchHeight(height);
                                }
                            }
                        }
                        return insets;
                    }
                });
            }
        }
    }

    /**
     * 华为start
     */
    // 判断是否是华为刘海屏
    public static boolean hasNotchInScreenAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class<?> HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (Boolean) get.invoke(HwNotchSizeUtil);
            Log.d("NotchScreenUtil", "this Huawei device has notch in screen？"+ret);
        } catch (ClassNotFoundException e) {
            Log.e("NotchScreenUtil", "hasNotchInScreen ClassNotFoundException", e);
        } catch (NoSuchMethodException e) {
            Log.e("NotchScreenUtil", "hasNotchInScreen NoSuchMethodException", e);
        } catch (Exception e) {
            Log.e("NotchScreenUtil", "hasNotchInScreen Exception", e);
        }
        return ret;
    }

    /**
     * 获取华为刘海的高
     * @param context
     * @return
     */
    public static int getNotchSizeAtHuawei(Context context) {
        int[] ret = new int[] { 0, 0 };
        try {
            ClassLoader cl = context.getClassLoader();
            Class<?> HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(HwNotchSizeUtil);

        } catch (ClassNotFoundException e) {
            Log.e("NotchScreenUtil", "getNotchSize ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("NotchScreenUtil", "getNotchSize NoSuchMethodException");
        } catch (Exception e) {
            Log.e("NotchScreenUtil", "getNotchSize Exception");
        }
        return ret[1];
    }

    /**
     * 华为end
     */

    /**
     * Oppo start
     */
    public static boolean hasNotchInScreenAtOppo(Context context) {
        boolean hasNotch = context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        Log.d("NotchScreenUtil", "this OPPO device has notch in screen？"+hasNotch);
        return hasNotch;
    }

    public static int getNotchSizeAtOppo() {
        return 80;
    }

    /**
     * Oppo end
     */

    /**
     * vivo start
     */
    public static final int NOTCH_IN_SCREEN_VOIO = 0x00000020;// 是否有凹槽
    public static final int ROUNDED_IN_SCREEN_VOIO = 0x00000008;// 是否有圆角

    public static boolean hasNotchInScreenAtVivo(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class<?> FtFeature = cl.loadClass("com.util.FtFeature");
            Method get = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (Boolean) get.invoke(FtFeature, NOTCH_IN_SCREEN_VOIO);
            Log.d("NotchScreenUtil", "this VIVO device has notch in screen？" + ret);
        } catch (ClassNotFoundException e) {
            Log.e("NotchScreenUtil", "hasNotchInScreen ClassNotFoundException", e);
        } catch (NoSuchMethodException e) {
            Log.e("NotchScreenUtil", "hasNotchInScreen NoSuchMethodException", e);
        } catch (Exception e) {
            Log.e("NotchScreenUtil", "hasNotchInScreen Exception", e);
        }
        return ret;
    }

    /**
     * 获取手机厂商
     *
     * @return  手机厂商
     */

    public final static int DEVICE_BRAND_OPPO = 0x0001;
    public final static int DEVICE_BRAND_HUAWEI = 0x0002;
    public final static int DEVICE_BRAND_VIVO = 0x0003;


    @SuppressLint("DefaultLocale")
    public static int getDeviceBrand() {
        String brand = Build.BRAND.trim().toUpperCase();
        if (brand.contains("HUAWEI")) {
            Log.d("device brand", "HUAWEI");
            return DEVICE_BRAND_HUAWEI;
        }else if (brand.contains("OPPO")) {
            Log.d("device brand", "OPPO");
            return DEVICE_BRAND_OPPO;
        }else if (brand.contains("VIVO")) {
            Log.d("device brand", "VIVO");
            return DEVICE_BRAND_VIVO;
        }
        return 0;
    }

    private static void setNotchHeight(int height){
        notchHeight = height;
    }

    public static int getNotchHeight(){
        return notchHeight;
    }
}
