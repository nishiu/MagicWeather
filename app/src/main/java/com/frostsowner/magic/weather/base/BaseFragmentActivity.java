package com.frostsowner.magic.weather.base;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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

import com.fm.commons.base.AbstractFragmentActivity;
import com.fm.commons.event.ActionEvent;
import com.fm.commons.http.ContextHolder;
import com.fm.commons.util.ApkResources;
import com.fm.commons.widget.SimpleNotice;
import com.fm.commons.widget.SimpleProgress;
import com.frostsowner.magic.weather.utils.SubscribeUtils;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import rx.Subscription;

public class BaseFragmentActivity extends AbstractFragmentActivity {

    protected Gson gson;
    protected Dialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

    public Context getContext(){
        return this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
//        StatService.onResume(this);
        logInfo("onResume");
    }

    @Override
    protected void onPause(){
        super.onPause();
        MobclickAgent.onPause(this);
//        StatService.onPause(this);
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
        if(progressDialog == null)
            progressDialog = SimpleProgress.show(this,message,true);
        progressDialog.show();
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

}
