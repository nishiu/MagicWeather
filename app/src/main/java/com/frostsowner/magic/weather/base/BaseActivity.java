package com.frostsowner.magic.weather.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.fm.commons.base.AbstractActivity;
import com.fm.commons.http.ContextHolder;
import com.frostsowner.magic.weather.event.ActionEvent;
import com.fm.commons.util.ApkResources;
import com.fm.commons.util.PixelUtils;
import com.fm.commons.widget.SimpleNotice;
import com.fm.commons.widget.SimpleProgress;
import com.frostsowner.magic.weather.utils.SubscribeUtils;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by yulijun on 2017/4/18.
 */

public class BaseActivity extends AbstractActivity{

    protected Gson gson;
    protected Dialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        NotchUtils.readNotchHeight(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID){
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    public void showNotice(final String msg){
        Subscription subscription = SubscribeUtils.doOnUIThread(() -> SimpleNotice.show(ContextHolder.get(),msg));
        addSubscription(subscription);
    }

    public void showLongNotice(final String msg){
        Subscription subscription = SubscribeUtils.doOnUIThread(() -> SimpleNotice.showLong(ContextHolder.get(),msg));
        addSubscription(subscription);
    }

    public Context getContext(){
        return this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        logInfo("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
//        JPushInterface.onResume(this);
        logInfo("onResume");
    }

    @Override
    protected void onPause(){
        super.onPause();
        MobclickAgent.onPause(this);
//        JPushInterface.onPause(this);
        logInfo("onPause");
    }

    @Override
    protected void onStop(){
        super.onStop();
        logInfo("onStop");
    }

    public void onBack(View view){
        onBackPressed();
    }

    protected void counterButton(final Button button, final String originalText){
        button.setEnabled(false);
        Subscription subscription = SubscribeUtils.counter(60, aLong -> {
            int second = aLong.intValue();
            if(second < 60){
                second = 60-second;
                button.setText(""+second);
            }else{
                button.setEnabled(true);
                button.setText(originalText);
            }
        });
        addSubscription(subscription);
    }
    protected void showProgress(String message){
        showProgress(message,false);
    }

    protected void showProgress(String message,boolean cancelable){
        if(progressDialog == null);
        progressDialog = SimpleProgress.show(this,message,cancelable);
    }

    protected void dismissProgress(){
        if(progressDialog == null)return;
        progressDialog.dismiss();
        progressDialog=null;
    }

    protected void fire(ActionEvent event){
        EventBus.getDefault().post(event);
    }

    protected void setStatusBarBlack(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.parseColor("#000000"));
        }
    }

    protected void setStatusBar(String colorStr){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.parseColor(colorStr));
        }
    }

    protected void logInfo(String log){
        if(!ApkResources.isDebug(getContext()))return;
        logger.info(log);
    }

    public void logError(String error){
        if(ApkResources.isDebug(this)){
            logger.error(error);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logInfo("onDestroy");
        System.gc();
        System.runFinalization();
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
    */
    protected int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier(
        "status_bar_height", "dimen", "android");
        if (resourceId > 0) {
               result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    protected void hideXiaoMiBottomLine(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
        boolean isHideNavigationBar = Settings.Global.getInt(getContentResolver(), "force_fsg_nav_bar", 0) != 0;
    }



    protected void setNavigationBarHeight(View view) {
        int navigationBarHeight = getNavigationBarHeight();
        Log.v("dbw", "navigationBarHeight height:" + navigationBarHeight);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
        layoutParams.bottomMargin = navigationBarHeight;
        view.setLayoutParams(layoutParams);
    }

    protected int getNavigationBarHeight(){
        boolean hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        logger.info("menu : "+hasMenuKey);
        logger.info("back : "+hasBackKey);
        logger.info("home : "+hasHomeKey);
        if(hasMenuKey || hasBackKey || hasHomeKey)return 0;
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen","android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 判断当前view是否在屏幕可见
     */
    public boolean getLocalVisibleRect(Context context, View view) {
        Point p = new Point();
        ((Activity) context).getWindowManager().getDefaultDisplay().getSize(p);
        int screenWidth = p.x;
        int screenHeight = p.y - PixelUtils.dp2px(context,60);
        Rect rect = new Rect(0, 0, screenWidth, screenHeight);
        int[] location = new int[2];
        view.getLocationInWindow(location);
        location[1] = location[1] - PixelUtils.dp2px(context, 60);
//        Log.e("ijimu","screenHeight : "+screenHeight+" , location1 : "+location[1]);
        view.setTag(location[1]);//存储y方向的位置
        if (view.getLocalVisibleRect(rect)){
            return true;
        } else {
            return false;
        }
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

    protected void setProperty(String key,String data){
        FileOutputStream out = null;
        try {
            out = openFileOutput(key,MODE_PRIVATE);
            out.write(data.getBytes());
            out.flush();// 清理缓冲区的数据流
            out.close();// 关闭输出流
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
