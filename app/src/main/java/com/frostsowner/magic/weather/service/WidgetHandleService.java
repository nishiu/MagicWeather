package com.frostsowner.magic.weather.service;

import static com.frostsowner.magic.weather.Constant.ACTION_15FORECAST;
import static com.frostsowner.magic.weather.Constant.ACTION_HOME;
import static com.frostsowner.magic.weather.Constant.ACTION_WIDGET;
import static com.frostsowner.magic.weather.event.EventType.WEATHER_REQUEST_SUCCESS;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.SparseArray;
import android.widget.RemoteViews;

import com.fm.commons.event.ActionEvent;
import com.fm.commons.logic.BeanFactory;
import com.fm.commons.widget.SimpleNotice;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.activity.HomeTabActivity;
import com.frostsowner.magic.weather.base.BaseService;
import com.frostsowner.magic.weather.domain.WidgetStyle;
import com.frostsowner.magic.weather.domain.weather.YunCondition;
import com.frostsowner.magic.weather.domain.weather.YunWeather;
import com.frostsowner.magic.weather.logic.CityManager;
import com.frostsowner.magic.weather.logic.SimpleDateManager;
import com.frostsowner.magic.weather.logic.WidgetManager;
import com.frostsowner.magic.weather.utils.AddressUtils;
import com.frostsowner.magic.weather.utils.StringUtils;
import com.frostsowner.magic.weather.utils.WeatherResUtils;
import com.frostsowner.magic.weather.utils.WeatherUtils;
import com.frostsowner.magic.weather.widget.BlueWidgetBig;
import com.frostsowner.magic.weather.widget.BlueWidgetSmall;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

public class    WidgetHandleService extends BaseService {

    private final int WIDGET_CACHE_UPDATE_TIME_MAX = 50;

    private final long WIDGET_SERVICE_ALREM_INTERVAL = 1000*60*30;
    private final long UPDATE_INTERVAL = 1000*60*10;

    private CityManager cityManager;

    private SparseArray<RemoteViews> remoteViewsMap;
    private SparseArray<ComponentName> componentNameMap;

    private Timer bigWidgetTimer;
    private Timer smallWidgetTimer;
    private PowerManager pm;
    private int bigUpdateTime = -1;
    private int smallUpdateTime = -1;

    private WidgetManager widgetManager;
    private SimpleDateManager simpleDateManager;

    @Override
    public void onCreate(){
        super.onCreate();
        cityManager = BeanFactory.getBean(CityManager.class);
        widgetManager = BeanFactory.getBean(WidgetManager.class);
        simpleDateManager = BeanFactory.getBean(SimpleDateManager.class);
        remoteViewsMap = new SparseArray<>();
        componentNameMap = new SparseArray<>();
        pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        setAlarmDuration(WIDGET_SERVICE_ALREM_INTERVAL);
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logInfo("update widget handle ui");
        startForeground();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] bigIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,BlueWidgetBig.class));
        int[] smallIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,BlueWidgetSmall.class));
        if(bigIds != null && bigIds.length > 0){
            updateBlueBigWidget();
            if(bigWidgetTimer == null){
                bigWidgetTimer = new Timer();
                bigWidgetTimer.schedule(new TimerTask(){
                    @Override
                    public void run() {
                        updateBlueBigWidget();
                    }
                },0,UPDATE_INTERVAL);
            }
        }
        if(smallIds != null && smallIds.length > 0){
            updateBlueSmallWidget();
            if(smallWidgetTimer == null){
                smallWidgetTimer = new Timer();
                smallWidgetTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        updateBlueSmallWidget();
                    }
                },0,UPDATE_INTERVAL);
            }
        }
        logInfo("bigIds : "+bigIds.length+" , smallIds : "+smallIds);
        return super.onStartCommand(intent,flags,startId);
    }

    private void updateBlueBigWidget(){
//        if(!widgetManager.isNeedUpdateWidget(WidgetStyle.BLUE_BIG)){
//            if(bigWidgetTimer != null){
//                bigWidgetTimer.cancel();
//                bigWidgetTimer = null;
//            }
//            logInfo("Big Widget 不需要更新桌面控件");
//        }
        if(pm != null && !pm.isInteractive()){
            logInfo("屏幕已熄灭,无需更新");
            return;
        }
        RemoteViews remoteViews = remoteViewsMap.get(WidgetStyle.BLUE_BIG);
        if(remoteViews == null){
            remoteViews = new RemoteViews(getPackageName(),R.layout.layout_blue_widget_big);
            remoteViewsMap.put(WidgetStyle.BLUE_BIG,remoteViews);
        }
        ComponentName componentName = componentNameMap.get(WidgetStyle.BLUE_BIG);
        if(componentName == null){
            componentName = new ComponentName(this, BlueWidgetBig.class);
            componentNameMap.put(WidgetStyle.BLUE_BIG,componentName);
        }
        if(cityManager.getCityWeatherList() == null)return;
        if(cityManager.getCityWeatherList().size() == 0)return;
        YunWeather locationCityWeather = null;
        for(YunWeather cityWeather:cityManager.getCityWeatherList()){
            if(cityWeather.isLocate()){
                locationCityWeather = cityWeather;
            }
        }
        if(locationCityWeather == null)return;
        updateCityName(remoteViews,locationCityWeather);
        updateConditionText(remoteViews,locationCityWeather);
        updateConditionIcon(remoteViews,locationCityWeather);
        updateMaxMinTemp(remoteViews,locationCityWeather);
        updateAqi(remoteViews,locationCityWeather);
        updateTime(remoteViews);
        updateRefresh(remoteViews);

        Intent forecastIntent = new Intent(this, HomeTabActivity.class);
        forecastIntent.putExtra("action",ACTION_15FORECAST);
        PendingIntent forecastHomeIntent = PendingIntent.getActivity(this,3,forecastIntent,0);
        remoteViews.setOnClickPendingIntent(R.id.label_15forecast,forecastHomeIntent);

        Intent intent = new Intent(this, HomeTabActivity.class);
        intent.putExtra("action",ACTION_HOME);
        PendingIntent homeIntent = PendingIntent.getActivity(this,WidgetStyle.BLUE_BIG,intent,0);
        remoteViews.setOnClickPendingIntent(R.id.group_root,homeIntent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(componentName,remoteViews);
        logInfo("update blue widget big, componentName : "+componentName+" , removeView : "+remoteViews);
        checkBigWidgetOverTime();
    }

    private void updateBlueSmallWidget(){
//        if(!widgetManager.isNeedUpdateWidget(WidgetStyle.BLUE_SMALL)){
//            if(smallWidgetTimer != null){
//                smallWidgetTimer.cancel();
//                smallWidgetTimer = null;
//            }
//            logInfo("Small Widget 不需要更新桌面控件");
//        }
        if(pm != null && !pm.isInteractive()){
            logInfo("屏幕已熄灭,无需更新");
            return;
        }
        RemoteViews remoteViews = remoteViewsMap.get(WidgetStyle.BLUE_SMALL);
        if(remoteViews == null){
            remoteViews = new RemoteViews(getPackageName(),R.layout.layout_blue_widget_small);
            remoteViewsMap.put(WidgetStyle.BLUE_SMALL,remoteViews);
        }
        ComponentName componentName = componentNameMap.get(WidgetStyle.BLUE_SMALL);
        if(componentName == null){
            componentName = new ComponentName(this, BlueWidgetSmall.class);
            componentNameMap.put(WidgetStyle.BLUE_SMALL,componentName);
        }
        if(cityManager.getCityWeatherList() == null)return;
        if(cityManager.getCityWeatherList().size() == 0)return;
        YunWeather locationCityWeather = null;
        for(YunWeather cityWeather:cityManager.getCityWeatherList()){
            if(cityWeather.isLocate()){
                locationCityWeather = cityWeather;
            }
        }
        if(locationCityWeather == null)return;
        updateCityName(remoteViews,locationCityWeather);
//        updateConditionText(remoteViews,locationCityWeather);
        updateConditionIcon(remoteViews,locationCityWeather);
        updateMaxMinTemp(remoteViews,locationCityWeather);

        updateAqi(remoteViews,locationCityWeather);
        updateTime(remoteViews);
        updateRefresh(remoteViews);
        Intent intent = new Intent(this, HomeTabActivity.class);
        intent.putExtra("action",ACTION_HOME);
        PendingIntent homeIntent = PendingIntent.getActivity(this,WidgetStyle.BLUE_SMALL,intent,0);
        remoteViews.setOnClickPendingIntent(R.id.group_root,homeIntent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(componentName,remoteViews);
        logInfo("update blue widget small, componentName : "+componentName+" , removeView : "+remoteViews);
        checkSmallWidgetOverTime();
    }

    private void updateCityName(RemoteViews remoteViews,YunWeather cityWeather){
        String address = AddressUtils.detailsAddress(cityWeather.getAddress(),cityWeather.getCity());
        remoteViews.setTextViewText(R.id.tv_location,address);
    }

    private void updateConditionIcon(RemoteViews remoteViews,YunWeather cityWeather){
        if(cityWeather.getCondition() != null){
            YunCondition baseCondition = cityWeather.getCondition();
            boolean isNight = StringUtils.isNight(System.currentTimeMillis());
            remoteViews.setImageViewResource(R.id.icon_condition,isNight?WeatherResUtils.getYunWeatherNightIcon(baseCondition.getWea_night_img()):WeatherResUtils.getYunWeatherDayIcon(baseCondition.getWea_day_img()));
        }
    }

    private void updateConditionText(RemoteViews remoteViews,YunWeather cityWeather){
        if(cityWeather.getCondition() != null){
            YunCondition baseCondition = cityWeather.getCondition();
            remoteViews.setTextViewText(R.id.tv_temp,baseCondition.getTem()+"°");
        }
    }

    private void updateMaxMinTemp(RemoteViews remoteViews,YunWeather cityWeather){
        if(cityWeather.getCondition() != null){
            YunCondition baseCondition = cityWeather.getCondition();
            remoteViews.setTextViewText(R.id.tv_temp_min_max,baseCondition.getTem1()+"°/"+baseCondition.getTem2()+"°");
        }
    }

    private void updateAqi(RemoteViews remoteViews,YunWeather cityWeather){
        if(cityWeather.getCondition() != null){
            YunCondition baseCondition = cityWeather.getCondition();
            String quality = WeatherUtils.getAqiValue(baseCondition.getAir());
            remoteViews.setTextViewText(R.id.tv_aqi,"空气质量 "+quality+" "+baseCondition.getAir());
        }
    }

    private void updateRefresh(RemoteViews remoteViews){
        Intent refreshIntent = new Intent(this, WeatherService.class);
        refreshIntent.putExtra("action",ACTION_WIDGET);
        PendingIntent refreshPendingIntent = PendingIntent.getService(this,4,refreshIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.tv_refresh,refreshPendingIntent);
    }

    private void updateTime(RemoteViews remoteViews){
//        Date date = new Date(System.currentTimeMillis());
//        String dateStr = dateFormat.format(date);
//        remoteViews.setTextViewText(R.id.tv_time,dateStr.substring(6));
//        remoteViews.setTextViewText(R.id.tv_date,dateStr.substring(0,6)+" "+ StringUtils.getWeek(date));
    }

    private void checkSmallWidgetOverTime(){
        if(smallUpdateTime >= WIDGET_CACHE_UPDATE_TIME_MAX){
            remoteViewsMap.remove(WidgetStyle.BLUE_SMALL);
            componentNameMap.remove(WidgetStyle.BLUE_SMALL);
            smallUpdateTime = -1;
        }
        smallUpdateTime++;
    }

    private void checkBigWidgetOverTime(){
        if(bigUpdateTime >= WIDGET_CACHE_UPDATE_TIME_MAX){
            remoteViewsMap.remove(WidgetStyle.BLUE_BIG);
            componentNameMap.remove(WidgetStyle.BLUE_BIG);
            bigUpdateTime = -1;
        }
        bigUpdateTime++;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWidgetEvent(ActionEvent event){
        if(event.getType() == WEATHER_REQUEST_SUCCESS){
            boolean showNotice = event.getAttrBoolean("action");
            if(showNotice){
                SimpleNotice.show("天气刷新成功");
            }
        }
    }

    @Override
    public boolean stopService(Intent name) {
        EventBus.getDefault().unregister(this);
        return super.stopService(name);
    }
}
