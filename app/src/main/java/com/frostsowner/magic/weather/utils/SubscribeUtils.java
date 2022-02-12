package com.frostsowner.magic.weather.utils;

import com.fm.commons.http.ContextHolder;
import com.fm.commons.util.ApkResources;
import com.frostsowner.magic.weather.logic.CrashHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by yulijun on 2017/6/5.
 */

public class SubscribeUtils {

    private static Logger logger = LoggerFactory.getLogger(com.frostsowner.magic.weather.utils.SubscribeUtils.class);

    public static <T>Subscription generate(Func0<Observable<T>> func0, Action1<T> action1){
        Subscription subscription = Observable.defer(func0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action1, throwable -> {
                    StringWriter writer = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(writer);
                    throwable.printStackTrace(printWriter);
                    throwable.printStackTrace();
                    logger.error(writer.toString());
                    if(!ApkResources.isDebug(ContextHolder.get()))
                        CrashHandler.getInstance().handleException(throwable);
                });
        return subscription;
    }

    public static <T>Subscription generate(Func0<Observable<T>> func0, Action1<T> action1, Action1<Throwable> action2){
        Subscription subscription = Observable.defer(func0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action1, action2);
        return subscription;
    }

    /**
     * 倒计时
     * @param time
     * @param action
     * @return
     */
    public static Subscription counter(int time, Action1<Long> action){
        Subscription subscription = Observable.interval(0,1,TimeUnit.SECONDS)
                .take(time+1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action, throwable -> {
                    StringWriter writer = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(writer);
                    throwable.printStackTrace(printWriter);
                    throwable.printStackTrace();
                    logger.error(writer.toString());
                    if(!ApkResources.isDebug(ContextHolder.get()))
                        CrashHandler.getInstance().handleException(throwable);
                });
        return subscription;
    }

    /**
     * 倒计时
     * @param time
     * @param action
     * @return
     */
    public static Subscription counter(long time, long period,Action1<Long> action){
        Subscription subscription = Observable.interval(0,period,TimeUnit.MILLISECONDS)
                .take(time+period,TimeUnit.MILLISECONDS)
                .onBackpressureBuffer(1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action, throwable -> {
                    StringWriter writer = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(writer);
                    throwable.printStackTrace(printWriter);
                    throwable.printStackTrace();
                    logger.error(writer.toString());
                    if(!ApkResources.isDebug(ContextHolder.get()))
                        CrashHandler.getInstance().handleException(throwable);
                });
        return subscription;
    }

    public static Subscription doOnUIThread(Action0 action0){
        Subscription subscription = Observable.just(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(action0)
                .doOnError(throwable -> {
                    StringWriter writer = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(writer);
                    throwable.printStackTrace(printWriter);
                    throwable.printStackTrace();
                    logger.error(writer.toString());
                    if(!ApkResources.isDebug(ContextHolder.get()))
                        CrashHandler.getInstance().handleException(throwable);
                })
                .subscribe();
        return subscription;
    }

    public static Subscription doOnUIThreadDelayed(Action0 action0, long delay){
        Subscription subscription = Observable.just(true)
                .delay(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(action0)
                .doOnError(throwable -> {
                    StringWriter writer = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(writer);
                    throwable.printStackTrace(printWriter);
                    throwable.printStackTrace();
                    logger.error(writer.toString());
                    if(!ApkResources.isDebug(ContextHolder.get()))
                        CrashHandler.getInstance().handleException(throwable);
                })
                .subscribe();
        return subscription;
    }

    public static Subscription interval(Action1<Long> action1,long interval, TimeUnit unit){
         Subscription subscription = Observable.interval(interval,unit)
                .onBackpressureBuffer(100000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action1,throwable -> {
                    StringWriter writer = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(writer);
                    throwable.printStackTrace(printWriter);
                    throwable.printStackTrace();
                    logger.error(writer.toString());
                    if(!ApkResources.isDebug(ContextHolder.get()))
                        CrashHandler.getInstance().handleException(throwable);
                });
         return subscription;
    }
}
