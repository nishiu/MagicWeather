package com.frostsowner.magic.weather.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.frostsowner.magic.weather.activity.SplashActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class BaseApplication extends Application {

    private Logger logger = LoggerFactory.getLogger(getClass());

    // 正常状态
    public static final int STATE_NORMAL = 0;
    // 从后台回到前台
    public static final int STATE_BACK_TO_FRONT = 1;
    // 从前台进入后台
    public static final int STATE_FRONT_TO_BACK = 2;

    // APP状态
    private static int sAppState = STATE_NORMAL;
    // 标记程序是否已进入后台(依据onStop回调)
    private boolean flag;
    // 标记程序是否已进入后台(依据onTrimMemory回调)
    private boolean background;
    // 从前台进入后台的时间
    private static long frontToBackTime;
    // 从后台返回前台的时间
    private static long backToFrontTime;

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);
        // TRIM_MEMORY_UI_HIDDEN是UI不可见的回调, 通常程序进入后台后都会触发此回调,大部分手机多是回调这个参数
        // TRIM_MEMORY_BACKGROUND也是程序进入后台的回调, 不同厂商不太一样, 魅族手机就是回调这个参数
        if (level == Application.TRIM_MEMORY_UI_HIDDEN || level == TRIM_MEMORY_BACKGROUND) {
            background = true;
        } else if (level == Application.TRIM_MEMORY_COMPLETE) {
            background = !isCurAppTop(this);
        }
        if (background) {
            frontToBackTime = System.currentTimeMillis();
            sAppState = STATE_FRONT_TO_BACK;
            logger.error("onTrimMemory: TRIM_MEMORY_UI_HIDDEN || TRIM_MEMORY_BACKGROUND");
        } else {
            sAppState = STATE_NORMAL;
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    protected void initActivityLife(){
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (background || flag) {
                    background = false;
                    flag = false;
                    sAppState = STATE_BACK_TO_FRONT;
                    backToFrontTime = System.currentTimeMillis();
                    logger.info("onResume: STATE_BACK_TO_FRONT");
                    if (canShowAd()) {
                        logger.info("restart splash activity");
                        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                        intent.putExtra("isLifeShowAd",true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } else {
                    sAppState = STATE_NORMAL;
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                //判断当前activity是否处于前台
                if (!isCurAppTop(activity)) {
                    // 从前台进入后台
                    sAppState = STATE_FRONT_TO_BACK;
                    frontToBackTime = System.currentTimeMillis();
                    flag = true;
                    logger.info("onStop: " + "STATE_FRONT_TO_BACK");
                } else {
                    // 否则是正常状态
                    sAppState = STATE_NORMAL;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }
    /**
     * 进入后台间隔10分钟以后可以再次显示广告
     *
     * @return 是否能显示广告
     */
    public static boolean canShowAd() {
        return sAppState == STATE_BACK_TO_FRONT &&
                (backToFrontTime - frontToBackTime) > 1000*30;
    }
    /**
     * 判断当前程序是否前台进程
     *
     * @param context
     * @return
     */
    public static boolean isCurAppTop(Context context) {
        if (context == null) {
            return false;
        }
        String curPackageName = context.getPackageName();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ActivityManager.RunningTaskInfo info = list.get(0);
            String topPackageName = info.topActivity.getPackageName();
            String basePackageName = info.baseActivity.getPackageName();
            if (topPackageName.equals(curPackageName) && basePackageName.equals(curPackageName)){
                return true;
            }
        }
        return false;
    }

    protected boolean isProperty(String key){
        FileInputStream fis = null;
        byte[] buffer = null;
        try {
            //获取文件输入流
            fis = openFileInput(key);
            //定义保存数据的数组
            buffer = new byte[fis.available()];
            //从输入流中读取数据
            fis.read(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                if(fis!=null)fis.close();
                return buffer != null;
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
