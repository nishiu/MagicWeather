package com.frostsowner.magic.weather.base;

import android.app.Dialog;
import android.content.Context;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.fm.commons.util.ApkResources;
import com.fm.commons.widget.SimpleNotice;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.utils.SubscribeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by yulijun on 2018/5/3.
 */

public class BaseDialog extends Dialog {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private CompositeSubscription compositeSubscription;

    public BaseDialog(@NonNull Context context) {
        this(context, R.style.dialog_full);
    }

    public BaseDialog(@NonNull Context context, @StyleRes int resId){
        super(context,resId);
        compositeSubscription = new CompositeSubscription();
    }

    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    protected void logInfo(String msg){
        if(ApkResources.isDebug(getContext()))logger.info(msg);
    }

    protected void showNotice(CharSequence msg){
        SimpleNotice.show(msg);
    }

    protected void interval(Action1<Long> action1, long interval, TimeUnit unit){
        Subscription subscription = SubscribeUtils.interval(action1,interval,unit);
        compositeSubscription.add(subscription);
    }

    protected void postDelayed(Action0 action0,long delayed){
        Subscription subscription = SubscribeUtils.doOnUIThreadDelayed(action0,delayed);
        compositeSubscription.add(subscription);
    }

    protected void addSubscription(Subscription subscription){
        compositeSubscription.add(subscription);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(compositeSubscription != null){
            compositeSubscription.unsubscribe();
            compositeSubscription.clear();
            compositeSubscription=null;
        }
    }

    protected String getVersionKey(){
        return "takefun_"+ApkResources.getClientVerName();
    }
}
