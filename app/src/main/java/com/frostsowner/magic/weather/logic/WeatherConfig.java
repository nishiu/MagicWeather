package com.frostsowner.magic.weather.logic;

import static com.frostsowner.magic.weather.Constant.AD_CACHE_PATH;
import static com.frostsowner.magic.weather.Constant.BASE_PATH;
import static com.frostsowner.magic.weather.Constant.DEFAULT_BASE_PATH;
import static com.frostsowner.magic.weather.Constant.NEED_REQUEST_AD_FLAG;
import static com.frostsowner.magic.weather.Constant.UPGRADE_PATH;

import android.util.Log;

import com.fm.commons.logic.BeanFactory;
import com.fm.commons.logic.LocalStorage;
import com.fm.commons.util.FileUtils;
import com.fm.commons.util.ResultUtil;
import com.fm.commons.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;

/**
 * Created by yulijun on 2018/2/26.
 */

public class WeatherConfig {

    private static final String BASE_PATH_CONFIG_KEY = "magic_weather_base_path";

    private static final String SIGN_NOTIFICATION_ALERT_KEY = "magic_weather_notify_alert_key";

    private static LocalStorage localStorage = BeanFactory.getBean(LocalStorage.class);

    public static void initRecordPath(){
        File file = localStorage.get(BASE_PATH_CONFIG_KEY,File.class);
        if(file!=null){
            BASE_PATH = file.getPath();
        }else{
            BASE_PATH = DEFAULT_BASE_PATH;
            AD_CACHE_PATH = BASE_PATH+"/addCache";
        }
        Log.d("ijimu","BasePath : "+BASE_PATH);
        UPGRADE_PATH = BASE_PATH+"/upgrade";
        FileUtils.makeDirs(AD_CACHE_PATH);
    }

    public static void setNotificationSignAlertKey(boolean alert){
        localStorage.put(SIGN_NOTIFICATION_ALERT_KEY,alert);
    }

    public static boolean isNotificationSignAlertKey(){
        return localStorage.get(SIGN_NOTIFICATION_ALERT_KEY,false);
    }
}
