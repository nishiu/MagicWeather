package com.frostsowner.magic.weather.logic;

import com.fm.commons.logic.BeanFactory;
import com.fm.commons.logic.LocalStorage;
import com.frostsowner.magic.weather.domain.weather.YunWeather;

import java.util.HashMap;
import java.util.Map;

public class WeatherRefreshHandler {

    private final long NORMAL_UPDATE_DELAY = 1000*60*30;
    private final long LOCATION_UPDATE_DELAY = 1000*60*5;

    private final String OTHER_CITY_UPDATE_TIME = "magic_weather_other_city_update_time";

    public Map<String,Long> updateTimeCache;

    private LocalStorage localStorage;
    private CityManager cityManager;
    private SimpleDateManager simpleDateManager;

    public WeatherRefreshHandler() {
        localStorage = BeanFactory.getBean(LocalStorage.class);
        cityManager = BeanFactory.getBean(CityManager.class);
        simpleDateManager = BeanFactory.getBean(SimpleDateManager.class);
        updateTimeCache = new HashMap<>();
    }

    public boolean showFakeData(String cityName){
        YunWeather cityWeather = cityManager.getTarget(cityName);
        if(checkCityWeatherData(cityWeather)){
            updateTimeCache.put(cityName,System.currentTimeMillis());
            return false;
        }
        if(updateTimeOut(cityName,cityWeather.isLocate())){
            updateTimeCache.put(cityName,System.currentTimeMillis());
            return false;
        }
        return true;
    }

    private boolean updateTimeOut(String cityName,boolean locate){
        Long data = updateTimeCache.get(cityName);
        if(data == null)return true;
        if(locate){
            return System.currentTimeMillis() - data > LOCATION_UPDATE_DELAY;
        }else{
            String key = OTHER_CITY_UPDATE_TIME+cityName+simpleDateManager.transformTime("yyyy-MM-dd");
            boolean otherCityForceUpdate = localStorage.get(key,false);
            if(!otherCityForceUpdate){
                localStorage.put(key,true);
                return true;
            }
            return System.currentTimeMillis() - data > NORMAL_UPDATE_DELAY;
        }
    }

    private boolean checkCityWeatherData(YunWeather cityWeather){
        if(cityWeather == null)return true;
        if(cityWeather.getCondition() == null)return true;
        if(cityWeather.getForecast() == null)return true;
        if(cityWeather.isLocate())return true;
        return false;
    }
}
