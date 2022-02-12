package com.frostsowner.magic.weather.service;

import static com.frostsowner.magic.weather.Constant.ACTION_CONDITION;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.fm.commons.http.ContextHolder;
import com.fm.commons.logic.BeanFactory;
import com.frostsowner.magic.weather.Constant;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.activity.HomeTabActivity;
import com.frostsowner.magic.weather.base.BaseService;
import com.frostsowner.magic.weather.domain.weather.YunCondition;
import com.frostsowner.magic.weather.domain.weather.YunWeather;
import com.frostsowner.magic.weather.logic.CityManager;
import com.frostsowner.magic.weather.logic.SimpleDateManager;
import com.frostsowner.magic.weather.utils.AddressUtils;
import com.frostsowner.magic.weather.utils.StringUtils;
import com.frostsowner.magic.weather.utils.WeatherResUtils;
import com.frostsowner.magic.weather.utils.WeatherUtils;

public class BaseWeatherService extends BaseService {

    public static final String PRIMARY_CHANNEL = "magic_weather_"+Constant.CHANNEL;

    private CityManager cityManager;
    private SimpleDateManager simpleDateManager;

    @Override
    public void onCreate(){
        super.onCreate();
        cityManager = BeanFactory.getBean(CityManager.class);
        simpleDateManager = BeanFactory.getBean(SimpleDateManager.class);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    protected void updateWeatherNotification(){
        logInfo("开始更新推送");
        for(int i = 0; i < cityManager.getCityWeatherList().size();i++){
            YunWeather cityWeather = cityManager.getCityWeatherList().get(i);
            if(cityWeather.isLocate()){
                addNotification(cityWeather);
            }
        }
    }

    private void addNotification(YunWeather cityWeather){
        if(cityWeather.getCondition() != null){
            YunCondition baseCondition = cityWeather.getCondition();
            RemoteViews remoteViews = new RemoteViews(ContextHolder.get().getPackageName(), R.layout.layout_custom_notification);
            updateCustomViewContent(remoteViews,cityWeather);
            NotificationManager nm = (NotificationManager) ContextHolder.get().getSystemService(Context.NOTIFICATION_SERVICE);
            if(nm == null)return;
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL, Constant.CHANNEL,NotificationManager.IMPORTANCE_LOW);
                notificationChannel.setShowBadge(true);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.enableLights(false);
                notificationChannel.setSound(null,null);
                notificationChannel.enableVibration(false);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                nm.createNotificationChannel(notificationChannel);
            }
            Intent intent = new Intent();
            intent.setClass(this, HomeTabActivity.class);
            PendingIntent homeIntent = PendingIntent.getActivity(this,1,intent,0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(ContextHolder.get(),PRIMARY_CHANNEL);

            boolean isNight = StringUtils.isNight(System.currentTimeMillis());
            builder.setSmallIcon(isNight?WeatherResUtils.getYunWeatherNightIcon(baseCondition.getWea_night_img()):WeatherResUtils.getYunWeatherDayIcon(baseCondition.getWea_day_img()));
            builder.setContent(remoteViews);
            builder.setChannelId(PRIMARY_CHANNEL);
            builder.setCustomBigContentView(remoteViews);
            builder.setContentIntent(homeIntent);
            builder.setOngoing(true);
            builder.setColorized(true);
            builder.setOnlyAlertOnce(true);
            builder.setColor(getBaseContext().getResources().getColor(android.R.color.transparent));
            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
            nm.notify(140412,notification);
            startForeground(140412,notification);
            logInfo("add notification view");
        }
    }

    private void updateCustomViewContent(RemoteViews remoteViews, YunWeather cityWeather){
        YunCondition baseCondition = cityWeather.getCondition();
        boolean isNight = StringUtils.isNight(System.currentTimeMillis());
        remoteViews.setImageViewResource(R.id.icon_weather, isNight?WeatherResUtils.getYunWeatherNightIcon(baseCondition.getWea_night_img()):WeatherResUtils.getYunWeatherDayIcon(baseCondition.getWea_day_img()));
        String detailsAddress = AddressUtils.detailsAddress(cityWeather.getAddress(),cityWeather.getCity());
        remoteViews.setTextViewText(R.id.tv_city_name,detailsAddress);
        remoteViews.setTextViewText(R.id.tv_temp_now,baseCondition.getTem()+"°");

        remoteViews.setTextViewText(R.id.tv_temp_min_max,baseCondition.getTem1()+"°/"+baseCondition.getTem2()+"°");

        String quality = WeatherUtils.getAqiValue(baseCondition.getAir());
        if(quality.equals("优")){
            remoteViews.setTextViewText(R.id.tv_air_quality,"优 "+baseCondition.getAir());
            remoteViews.setImageViewResource(R.id.bg_air_quality,R.drawable.bg_quality_1);
        }
        else if(quality.equals("良")){
            remoteViews.setTextViewText(R.id.tv_air_quality,"良 "+baseCondition.getAir());
            remoteViews.setImageViewResource(R.id.bg_air_quality,R.drawable.bg_quality_2);
        }
        else if(quality.equals("轻度")){
            remoteViews.setTextViewText(R.id.tv_air_quality,"轻度 "+baseCondition.getAir());
            remoteViews.setImageViewResource(R.id.bg_air_quality,R.drawable.bg_quality_3);
        }
        else if(quality.equals("中度")){
            remoteViews.setTextViewText(R.id.tv_air_quality,"中度 "+baseCondition.getAir());
            remoteViews.setImageViewResource(R.id.bg_air_quality,R.drawable.bg_quality_4);
        }
        else if(quality.equals("重度") || quality.equals("严重")){
            remoteViews.setTextViewText(R.id.tv_air_quality,"重度 "+baseCondition.getAir());
            remoteViews.setImageViewResource(R.id.bg_air_quality,R.drawable.bg_quality_5);
        }
        remoteViews.setTextViewText(R.id.tv_update_time,baseCondition.getUpdate_time()+" 发布");

        Intent intent = new Intent(this, HomeTabActivity.class);
        intent.putExtra("action",ACTION_CONDITION);
        PendingIntent homeIntent = PendingIntent.getActivity(this,2,intent,0);
        remoteViews.setOnClickPendingIntent(R.id.item_root,homeIntent);
    }
}
