package com.frostsowner.magic.weather;

import android.Manifest;
import android.os.Environment;

import java.io.File;

public class Constant {

    public static final String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DEFAULT_BASE_PATH = ROOT + File.separator + "MagicWeather";

    public static String BASE_PATH;
    public static String UPGRADE_PATH;
    public static String AD_CACHE_PATH;

    public static final String USER_AGENT = "MagicWeather";
    public static String CHANNEL = "official";

    public static final String YIYUNKE_APPID = "95281864";
    public static final String YIYUNKE_SECRET = "iNya4ngC";
    public static final String BUGLY_APP_ID = "7cd01afeb7";

    public static boolean SHOW_AD = false;
    public static boolean HOME_HAS_DEAD = true;
    public static boolean SHOW_SIGN = false;
    public static boolean CONDITION_NEWS_STICKY = false;
    public static boolean isEmulator = false;//是否是模拟器
    public static String SALT;
    public static boolean AB_TEST_BUBBLE = true;
    public static boolean AB_TEST_NEWS = true;
    public static boolean SHOW_SPLASH_AD = false;

    public static String UM_AD_FLAG_KEY = "ad_382";

    public static final int WEATHER_TYPE_SINGLE = 100;//请求单个城市数据
    public static final int WEATHER_TYPE_MULTI  = 101;//请求多个城市数据

    public static final String[] CALENDAR_PERMISSION = {
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
    };
    public static final String TYPE_GPS = "type_gps";//根据gps查询
    public static final String TYPE_NAME = "type_name";//根据城市名称查询

    public static final long SHORT_FORECAST_UPDATE_INTERVAL = 1000*60*60L;//短时预报天气刷新间隔 /1小时
    public static final long CONDITION_UPDATE_INTERVAL = 1000*60*30L;//实况天气刷新时间 /30分钟
    public static final long FORECAST_UPDATE_INTERVAL = 1000*60*60*24;//15天天气逐日预报刷新时间 /1天
    public static final long HOURLY_UPDATE_INTERVAL = 1000*60*60;//24小时天气逐日预报刷新时间 /1小时
    public static final long AIR_UPDATE_INTERVAL = 1000*60*30L;//实况空气质量刷新时间 / 30分钟
    public static final long AQI_FORECAST_UPDATE_INTERVAL = 1000*60*60*24L;//空气质量5天预报刷新时间 /1天
    public static final long AIR_DAILY_UPDATE_INTERVAL = 1000*60*60*24L;//5天空气质量逐日预报刷新时间 /1天
    public static final long LIVE_INDEX_UPDATE_INTERVAL = 1000*60*60*24L;//生活指数刷新时间 /1天
    public static final long LIMIT_UPDATE_INTERVAL = 1000*60*60*24*6L;//汽车尾号限行刷新时间 /6天
    public static final long ALERT_UPDATE_INTERVAL = 1000*60*60L;//实况预警刷新时间 /1小时

    public static final long CITY_CHANGE_UPDATE_INTERVAL = 1000*60*5L;//切换城市时,从服务器刷新天气数据的间隔 /30分钟
    public static final long DRAWING_UPDATE_OVERTIME = 1000*15;//刷新超时

    public static final long INSERT_AD_SHOW_INTERVAL = 1000*60*60*3L;//插屏广告弹出间隔
    public static final long BANNER_AD_SHOW_INTERVAL = 1000*60*15L;//Banner广告关闭后重启间隔
    public static final long SOURCE_AD_SHOW_INTERVAL = 1000*60*60L;//信息流广告关闭后重启间隔


    public static final String ACTION_WIDGET = "widget";
    public static final String ACTION_15FORECAST = "15forecast";
    public static final String ACTION_CONDITION = "condition";
    public static final String ACTION_HOME = "home";

    public static final String YUN_LOCAL_DATA = "tt_mj_weather_city_local_data_v3.7.7";
    public static final String NEED_REQUEST_AD_FLAG = "need_request_ad_flag";
    public static final String VIP_GUIDE_SHOW = "tt_weather_vip_dialog_guide_show";
    public static final String APPROVAL_SHOW = "tt_weather_approval_dialog_show";
    public static final String APPROVAL_DAY_TIME = "tt_weather_approval_day_time";
    public static final String AUTO_REMOVE_AD_DAY_TIME = "tt_weather_auto_remove_ad_day_time";
    public static final String AUTO_REMOVE_AD_SHOW = "tt_weather_auto_remove_ad_show";
    public static final String FAKE_AD_WATCH_VIDEO = "tt_weather_fake_ad_watch_video";

    public static String DEVICE_ID;
}
