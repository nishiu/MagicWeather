package com.frostsowner.magic.weather.base;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.fm.commons.http.ContextHolder;
import com.fm.commons.util.ApkResources;
import com.frostsowner.magic.weather.Constant;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.impl.DebugLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.frostsowner.magic.weather.service.BaseWeatherService.PRIMARY_CHANNEL;

public class BaseService extends Service implements DebugLogger {

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected CompositeSubscription compositeSubscription;

    private long alarmDuration = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        compositeSubscription = new CompositeSubscription();
    }

    protected void addSubscription(Subscription subscription){
        if(subscription == null)return;
        if(compositeSubscription == null){
            compositeSubscription = new CompositeSubscription();
        }
        compositeSubscription.add(subscription);
    }

    protected void setAlarmDuration(long alarmDuration){
        this.alarmDuration = alarmDuration;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setAutoStart();
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void setAutoStart(){
        if(alarmDuration == -1)return;
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(getBaseContext(),getClass());
        PendingIntent alarmPendingIntent = PendingIntent.getService(getBaseContext(), 0,
                alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if(Build.VERSION.SDK_INT < 23){
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + alarmDuration, alarmPendingIntent);
        }else{
            manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + alarmDuration, alarmPendingIntent);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void startForeground(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager nm = (NotificationManager) ContextHolder.get().getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL, Constant.CHANNEL, NotificationManager.IMPORTANCE_MIN);
            notificationChannel.setShowBadge(false);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableLights(false);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannel.enableVibration(false);
            notificationChannel.enableLights(false);
            notificationChannel.setSound(null,null);
            nm.createNotificationChannel(notificationChannel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getPackageName()+"_"+getClass().getName());

            builder.setDefaults(Notification.DEFAULT_ALL);
            builder.setAutoCancel(true);
            builder.setShowWhen(false);
            builder.setTimeoutAfter(100);
            builder.setOnlyAlertOnce(true);
            builder.setPriority(NotificationCompat.PRIORITY_MIN);
            builder.setChannelId(PRIMARY_CHANNEL);
            builder.setSmallIcon(R.drawable.ic_launcher);

            // 这里两个通知使用同一个id且必须按照这个顺序后调用startForeground
//            NotificationManagerCompat.from(this).notify(10010, builder.build());
            startForeground(10010, builder.build());
            stopForeground(true);
            Log.d("ijimu",getClass().getName()+" : startForeground");
        }
    }

    @Override
    public void logInfo(String msg) {
        if(ApkResources.isDebug(this)){
            logger.info(msg);
        }
    }

    public void logError(String error){
        if(ApkResources.isDebug(this)){
            logger.error(error);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(compositeSubscription != null){
            compositeSubscription.clear();
            compositeSubscription.unsubscribe();
            compositeSubscription = null;
        }
        stopForeground(true);
    }
}
