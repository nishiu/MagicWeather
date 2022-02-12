package com.frostsowner.magic.weather.base;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.fm.commons.base.AbstractFragment;
import com.fm.commons.http.ContextHolder;
import com.fm.commons.util.ApkResources;
import com.fm.commons.util.PixelUtils;
import com.fm.commons.widget.SimpleNotice;
import com.fm.commons.widget.SimpleProgress;
import com.frostsowner.magic.weather.event.ActionEvent;
import com.frostsowner.magic.weather.utils.SubscribeUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by yulijun on 2017/10/24.
 */

public abstract class BaseFragment extends AbstractFragment {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private Unbinder unbinder;
    protected CompositeSubscription compositeSubscription;
    protected Dialog progressDialog;
    protected Gson gson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        compositeSubscription = new CompositeSubscription();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(getLayout(),container,false);
        unbinder = ButterKnife.bind(this,root);
        initWidget(root);
        return initRootView(root);
    }

    protected abstract int getLayout();

    protected View initRootView(View root){
        return root;
    }

    protected void initWidget(View root){

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
        if (unbinder != null)
            unbinder.unbind();
    }

    protected void setStatusBar(View statusBarView){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)statusBarView.getLayoutParams();
        layoutParams.height = getStatusBarHeight(getContext());
        statusBarView.setLayoutParams(layoutParams);
    }

    protected void adjustImageBounds(RadioButton button, int widthInDp, int heightInDp) {
        // Get device dpi
        DisplayMetrics dm = getResources().getDisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        // Convert dp unit to pixel
        int width = (int) (widthInDp * ((float) dm.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        int height = (int) (heightInDp * ((float) dm.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        // Get RadioButton drawableTop
        Drawable[] drawables = button.getCompoundDrawables();
        drawables[3].setBounds(0, 0, width, height);
        button.setCompoundDrawables(null, null, null, drawables[3]);
    }

    protected void addSubscription(Subscription subscription){
        compositeSubscription.add(subscription);
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
        compositeSubscription.add(subscription);
    }

    protected void fire(ActionEvent event){
        EventBus.getDefault().post(event);
    }

    protected void showNotice(String msg){
        SubscribeUtils.doOnUIThread(()->{
            SimpleNotice.show(msg);
        });
    }

    protected void debugNotice(String msg){
        if(!ApkResources.isDebug(ContextHolder.get()))return;
        showNotice(msg);
    }

    protected void showProgress(String message){
        showProgress(message,false);
    }

    protected void showProgress(String message,boolean cancelable){
        if(progressDialog == null);
        progressDialog = SimpleProgress.show(getContext(),message,cancelable);
    }

    protected void dismissProgress(){
        if(progressDialog == null)return;
        progressDialog.dismiss();
        progressDialog=null;
    }

    protected void logInfo(String log){
        if(!ApkResources.isDebug(ContextHolder.get()))return;
        logger.info(log);
    }

    protected void logError(String log){
        if(!ApkResources.isDebug(ContextHolder.get()))return;
        logger.error(log);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if(compositeSubscription != null){
            compositeSubscription.unsubscribe();
            compositeSubscription.clear();
        }
    }

    protected int getNavigationBarHeight(){
//        boolean hasMenuKey = ViewConfiguration.get(getContext()).hasPermanentMenuKey();
//        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
//        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
//        logger.info("menu : "+hasMenuKey);
//        logger.info("back : "+hasBackKey);
//        logger.info("home : "+hasHomeKey);
//        if(hasMenuKey || hasBackKey || hasHomeKey)return 0;
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen","android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    //获取是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasNavigationBar;
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

    @Override
    public void onResume() {
        super.onResume();
        logInfo("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        logInfo("onPause");
    }

    protected boolean isProperty(String key){
        FileInputStream fis = null;
        byte[] buffer = null;
        try {
            //获取文件输入流
            fis = getActivity().openFileInput(key);
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
            out = getActivity().openFileOutput(key,MODE_PRIVATE);
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
