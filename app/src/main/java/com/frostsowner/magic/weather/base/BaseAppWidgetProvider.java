package com.frostsowner.magic.weather.base;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.fm.commons.http.ContextHolder;
import com.fm.commons.logic.BeanFactory;
import com.fm.commons.util.ApkResources;
import com.frostsowner.magic.weather.TrackPlugin;
import com.frostsowner.magic.weather.impl.DebugLogger;
import com.frostsowner.magic.weather.logic.WidgetManager;
import com.frostsowner.magic.weather.service.WidgetHandleService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseAppWidgetProvider extends AppWidgetProvider implements DebugLogger {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected WidgetManager widgetManager;

    public BaseAppWidgetProvider() {
        widgetManager = BeanFactory.getBean(WidgetManager.class);
    }

    @Override
    public void logInfo(String msg) {
        if(ApkResources.isDebug(ContextHolder.get())){
            logger.info(msg);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        logInfo("onUpdate , start widget update service");
        int updateId = update();
        Intent intent = new Intent(context, WidgetHandleService.class);
        intent.putExtra("widgetStyle",updateId);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        }else{
            context.startService(intent);
        }
    }

    @Override
    public void onEnabled(Context context) {
        logInfo("Widget onEnabled , start widget update service , widgetId : "+update());
        int updateId = update();
        widgetManager.addWidget(updateId);
        Intent intent = new Intent(context,WidgetHandleService.class);
        intent.putExtra("widgetStyle",updateId);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        }else{
            context.startService(intent);
        }
    }

    @Override
    public void onDisabled(Context context){
        logInfo("Widget onDisabled");
        widgetManager.removeWidget(update());
        if(widgetManager.checkUpdateWidget())return;
        context.stopService(new Intent(context, WidgetHandleService.class));
        logInfo("stop widget update service");
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(manager != null){
            Intent alarmIntent = new Intent(context, WidgetHandleService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0,alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            manager.cancel(pendingIntent);
        }
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        Log.d("ijimu","WeatherWidgetHandle onRestored");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d("ijimu","WeatherWidgetHandle onReceive");
    }

    public abstract int update();

}
