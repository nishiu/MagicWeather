package com.frostsowner.magic.weather.logic;

import com.fm.commons.logic.BeanFactory;
import com.fm.commons.logic.LocalStorage;

public class LogicModule {

    public static void init(){
        BeanFactory.getBean(LocalStorage.class);
        BeanFactory.getBean(CityManager.class);
        BeanFactory.getBean(YunCityIdManager.class);
        WeatherConfig.initRecordPath();
    }
}
