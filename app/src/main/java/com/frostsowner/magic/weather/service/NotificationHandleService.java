package com.frostsowner.magic.weather.service;

import android.content.Intent;
import android.os.Build;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationHandleService extends BaseWeatherService {

    private static final long NOTIFICATION_UPDATE_INTERNAL = 1000*60*30;

    private Timer updateTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        setAlarmDuration(NOTIFICATION_UPDATE_INTERNAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground();
        updateWeatherNotification();
        if(updateTimer == null){
            updateTimer = new Timer();
            updateTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    updateWeatherNotification();
                    updateNotificationFromWeatherService();
                }
            },0,NOTIFICATION_UPDATE_INTERNAL);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateNotificationFromWeatherService(){
        Intent intent = new Intent();
        intent.setClass(this, WeatherService.class);
        intent.putExtra("notification","notification");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(intent);
        }else{
            startService(intent);
        }
        logInfo("自动更新天气");
    }
}
