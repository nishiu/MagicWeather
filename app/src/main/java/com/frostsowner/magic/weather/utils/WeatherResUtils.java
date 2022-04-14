package com.frostsowner.magic.weather.utils;

import android.text.TextUtils;

import com.frostsowner.magic.weather.R;

public class WeatherResUtils {

    private final static int YUN_WEATHER_XUE = 1;
    private final static int YUN_WEATHER_LEI = 2;
    private final static int YUN_WEATHER_SHACHEN = 3;
    private final static int YUN_WEATHER_WU = 4;
    private final static int YUN_WEATHER_BINGBAO = 5;
    private final static int YUN_WEATHER_YUN = 6;
    private final static int YUN_WEATHER_YU = 7;
    private final static int YUN_WEATHER_YIN = 8;
    private final static int YUN_WEATHER_QING = 9;

    private static int[][] yun_weather_day_icon = {
            {0,R.drawable.w44},
            {1,R.drawable.wf15},
            {2,R.drawable.wf4},
            {3,R.drawable.wf18},
            {4,R.drawable.w45},
            {5,R.drawable.wf46},
            {6,R.drawable.wf2},
            {7,R.drawable.wf3},
            {8,R.drawable.wf1},
            {9,R.drawable.wf0}
    };

    private static int[][] yun_weather_night_icon = {
            {0,R.drawable.w44},
            {1,R.drawable.wf15},
            {2,R.drawable.wf4},
            {3,R.drawable.wf32},
            {4,R.drawable.w45},
            {5,R.drawable.wf46},
            {6,R.drawable.wf2},
            {7,R.drawable.wf33},
            {8,R.drawable.wf31},
            {9,R.drawable.wf30}
    };
    /**
     * xue、lei、shachen、wu、bingbao、yun、yu、yin、qing
     * @param wea_img
     * @return
     */
    private static int getYunWeatherCode(String wea_img){
        if(TextUtils.equals("xue",wea_img))return YUN_WEATHER_XUE;
        if(TextUtils.equals("lei",wea_img))return YUN_WEATHER_LEI;
        if(TextUtils.equals("shachen",wea_img))return YUN_WEATHER_SHACHEN;
        if(TextUtils.equals("wu",wea_img))return YUN_WEATHER_WU;
        if(TextUtils.equals("bingbao",wea_img))return YUN_WEATHER_BINGBAO;
        if(TextUtils.equals("yun",wea_img))return YUN_WEATHER_YUN;
        if(TextUtils.equals("yu",wea_img))return YUN_WEATHER_YU;
        if(TextUtils.equals("yin",wea_img))return YUN_WEATHER_YIN;
        if(TextUtils.equals("qing",wea_img))return YUN_WEATHER_QING;
        return 0;
    }

    public static int getYunWeatherDayIcon(String wea_img){
        if(StringUtils.isEmpty(wea_img))return R.drawable.w44;
        int code = getYunWeatherCode(wea_img);
        for(int i = 0; i < yun_weather_day_icon.length;i++){
            int[] item = yun_weather_day_icon[i];
            if(code == item[0]){
                return item[1];
            }
        }
        return R.drawable.w44;
    }

    public static int getYunWeatherNightIcon(String wea_img){
        if(StringUtils.isEmpty(wea_img))return R.drawable.w44;
        int code = getYunWeatherCode(wea_img);
        for(int i = 0; i < yun_weather_night_icon.length;i++){
            int[] item = yun_weather_night_icon[i];
            if(code == item[0]){
                return item[1];
            }
        }
        return R.drawable.w44;
    }
}
