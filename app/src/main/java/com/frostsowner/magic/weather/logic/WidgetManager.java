package com.frostsowner.magic.weather.logic;

import com.fm.commons.logic.BeanFactory;
import com.fm.commons.logic.LocalStorage;
import com.frostsowner.magic.weather.utils.ResultUtil;
import com.frostsowner.magic.weather.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class WidgetManager {

    private List<Integer> widgetIds;
    private LocalStorage localStorage;

    public WidgetManager() {
        localStorage = BeanFactory.getBean(LocalStorage.class);
        readCache();
    }

    public boolean isNeedUpdateWidget(int widgetId){
        if(widgetIds == null)return false;
        if(widgetIds.size() == 0)return false;
        for(Integer id : widgetIds){
            if(widgetId == id)return true;
        }
        return false;
    }

    public boolean checkUpdateWidget(){
        if(widgetIds == null)return false;
        if(widgetIds.size() == 0)return false;
        for(Integer id : widgetIds){
            if(isNeedUpdateWidget(id)){
                return true;
            }
        }
        return false;
    }

    public void addWidget(int widgetId){
        if(widgetIds == null)widgetIds = new LinkedList<>();
        if(widgetIds.contains(widgetId))return;
        widgetIds.add(widgetId);
        saveCache();
    }

    public void removeWidget(int widgetId){
        if(widgetIds == null)widgetIds = new LinkedList<>();
        if(!widgetIds.contains(widgetId))return;
        widgetIds.remove(Integer.valueOf(widgetId));
        saveCache();
    }

    private void readCache(){
        String json = localStorage.get("tt_weather_widget_ids","");
        if(!StringUtils.isEmpty(json)){
            Type type = new TypeToken<List<Integer>>(){}.getType();
            widgetIds = ResultUtil.getDomain(json,type);
        }
        if(widgetIds == null){
            widgetIds = new LinkedList<>();
        }
    }

    private void saveCache(){
        if(widgetIds == null || widgetIds.size() <= 0){
            localStorage.put("tt_weather_widget_ids","");
        }
        String json = ResultUtil.toJson(ResultUtil.toJson(widgetIds));
        localStorage.put("tt_weather_widget_ids",json);
    }



}
