package com.frostsowner.magic.weather.utils;

import android.text.TextUtils;

import com.frostsowner.magic.weather.R;

public class WeatherResUtils {

    private static final int[][] weather_icons = {
            {0,R.drawable.w0},
            {1,R.drawable.w1},
            {2,R.drawable.w2},
            {3,R.drawable.w3},
            {4,R.drawable.w4},
            {5,R.drawable.w5},
            {6,R.drawable.w6},
            {7,R.drawable.w7},
            {8,R.drawable.w8},
            {9,R.drawable.w9},
            {10,R.drawable.w10},
            {13,R.drawable.w13},
            {14,R.drawable.w14},
            {15,R.drawable.w15},
            {16,R.drawable.w16},
            {17,R.drawable.w17},
            {18,R.drawable.w18},
            {19,R.drawable.w19},
            {20,R.drawable.w20},
            {29,R.drawable.w29},
            {30,R.drawable.w30},
            {31,R.drawable.w31},
            {32,R.drawable.w32},
            {33,R.drawable.w33},
            {34,R.drawable.w34},
            {35,R.drawable.w35},
            {36,R.drawable.w20},
            {44,R.drawable.w44},
            {45,R.drawable.w45},
            {46,R.drawable.w45},
    };

    public static final int[][] condition_day_icons = {
            {1,R.drawable.w0},
            {2,R.drawable.w0},
            {3,R.drawable.w0},
            {4,R.drawable.w0},
            {5,R.drawable.w0},
            {6,R.drawable.w0},
            {7,R.drawable.w0},
            {8,R.drawable.w1},
            {9,R.drawable.w1},
            {10,R.drawable.w1},
            {11,R.drawable.w1},
            {12,R.drawable.w1},
            {13,R.drawable.w2},
            {14,R.drawable.w2},
            {15,R.drawable.w3},
            {16,R.drawable.w3},
            {17,R.drawable.w3},
            {18,R.drawable.w3},
            {19,R.drawable.w3},
            {20,R.drawable.w3},
            {21,R.drawable.w3},
            {22,R.drawable.w3},
            {23,R.drawable.w3},
            {24,R.drawable.w13},
            {25,R.drawable.w13},
            {26,R.drawable.w18},
            {27,R.drawable.w18},
            {28,R.drawable.w18},
            {29,R.drawable.w20},
            {30,R.drawable.w29},
            {31,R.drawable.w29},
            {32,R.drawable.w29},
            {33,R.drawable.w20},
            {34,R.drawable.w45},
            {35,R.drawable.w45},
            {36,R.drawable.w2},
            {37,R.drawable.w4},
            {38,R.drawable.w4},
            {39,R.drawable.w4},
            {40,R.drawable.w4},
            {41,R.drawable.w4},
            {42,R.drawable.w4},
            {43,R.drawable.w4},
            {44,R.drawable.w5},
            {45,R.drawable.w5},
            {46,R.drawable.w5},
            {47,R.drawable.w5},
            {48,R.drawable.w5},
            {49,R.drawable.w6},
            {50,R.drawable.w6},
            {51,R.drawable.w7},
            {52,R.drawable.w7},
            {53,R.drawable.w8},
            {54,R.drawable.w9},
            {55,R.drawable.w10},
            {56,R.drawable.w10},
            {57,R.drawable.w10},
            {58,R.drawable.w14},
            {59,R.drawable.w14},
            {60,R.drawable.w15},
            {61,R.drawable.w15},
            {62,R.drawable.w16},
            {63,R.drawable.w17},
            {64,R.drawable.w19},
            {65,R.drawable.w19},
            {66,R.drawable.w7},
            {67,R.drawable.w8},
            {68,R.drawable.w9},
            {69,R.drawable.w10},
            {70,R.drawable.w10},
            {71,R.drawable.w14},
            {72,R.drawable.w14},
            {73,R.drawable.w14},
            {74,R.drawable.w15},
            {75,R.drawable.w15},
            {76,R.drawable.w16},
            {77,R.drawable.w15},
            {78,R.drawable.w8},
            {79,R.drawable.w45},
            {80,R.drawable.w1},
            {81,R.drawable.w1},
            {82,R.drawable.w1},
            {83,R.drawable.w18},
            {84,R.drawable.w18},
            {85,R.drawable.w2},
            {86,R.drawable.w3},
            {87,R.drawable.w4},
            {88,R.drawable.w4},
            {89,R.drawable.w4},
            {90,R.drawable.w4},
            {91,R.drawable.w7},
            {92,R.drawable.w9},
            {93,R.drawable.w10},
            {94,R.drawable.w15},
    };

    public static final int[][] condition_night_icons = {
            {1,R.drawable.w30},
            {2,R.drawable.w30},
            {3,R.drawable.w30},
            {4,R.drawable.w30},
            {5,R.drawable.w30},
            {6,R.drawable.w30},
            {7,R.drawable.w30},
            {8,R.drawable.w31},
            {9,R.drawable.w31},
            {10,R.drawable.w31},
            {11,R.drawable.w31},
            {12,R.drawable.w31},
            {13,R.drawable.w2},
            {14,R.drawable.w2},
            {15,R.drawable.w33},
            {16,R.drawable.w33},
            {17,R.drawable.w33},
            {18,R.drawable.w33},
            {19,R.drawable.w33},
            {20,R.drawable.w33},
            {21,R.drawable.w33},
            {22,R.drawable.w33},
            {23,R.drawable.w33},
            {24,R.drawable.w34},
            {25,R.drawable.w34},
            {26,R.drawable.w32},
            {27,R.drawable.w32},
            {28,R.drawable.w32},
            {29,R.drawable.w20},
            {30,R.drawable.w35},
            {31,R.drawable.w35},
            {32,R.drawable.w35},
            {33,R.drawable.w20},
            {34,R.drawable.w45},
            {35,R.drawable.w45},
            {36,R.drawable.w2},
            {37,R.drawable.w4},
            {38,R.drawable.w4},
            {39,R.drawable.w4},
            {40,R.drawable.w4},
            {41,R.drawable.w4},
            {42,R.drawable.w4},
            {43,R.drawable.w4},
            {44,R.drawable.w5},
            {45,R.drawable.w5},
            {46,R.drawable.w5},
            {47,R.drawable.w5},
            {48,R.drawable.w5},
            {49,R.drawable.w6},
            {50,R.drawable.w6},
            {51,R.drawable.w7},
            {52,R.drawable.w7},
            {53,R.drawable.w8},
            {54,R.drawable.w9},
            {55,R.drawable.w10},
            {56,R.drawable.w10},
            {57,R.drawable.w10},
            {58,R.drawable.w14},
            {59,R.drawable.w14},
            {60,R.drawable.w15},
            {61,R.drawable.w15},
            {62,R.drawable.w16},
            {63,R.drawable.w17},
            {64,R.drawable.w19},
            {65,R.drawable.w19},
            {66,R.drawable.w7},
            {67,R.drawable.w8},
            {68,R.drawable.w9},
            {69,R.drawable.w10},
            {70,R.drawable.w10},
            {71,R.drawable.w14},
            {72,R.drawable.w14},
            {73,R.drawable.w14},
            {74,R.drawable.w15},
            {75,R.drawable.w15},
            {76,R.drawable.w16},
            {77,R.drawable.w15},
            {78,R.drawable.w8},
            {79,R.drawable.w45},
            {80,R.drawable.w31},
            {81,R.drawable.w31},
            {82,R.drawable.w31},
            {83,R.drawable.w32},
            {84,R.drawable.w32},
            {85,R.drawable.w2},
            {86,R.drawable.w33},
            {87,R.drawable.w4},
            {88,R.drawable.w4},
            {89,R.drawable.w4},
            {90,R.drawable.w4},
            {91,R.drawable.w7},
            {92,R.drawable.w9},
            {93,R.drawable.w10},
            {94,R.drawable.w15},
    };

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
            {1,R.drawable.w15},
            {2,R.drawable.w4},
            {3,R.drawable.w18},
            {4,R.drawable.w45},
            {5,R.drawable.w29},
            {6,R.drawable.w2},
            {7,R.drawable.w3},
            {8,R.drawable.w1},
            {9,R.drawable.w0}
    };

    private static int[][] yun_weather_night_icon = {
            {0,R.drawable.w44},
            {1,R.drawable.w15},
            {2,R.drawable.w4},
            {3,R.drawable.w32},
            {4,R.drawable.w45},
            {5,R.drawable.w35},
            {6,R.drawable.w2},
            {7,R.drawable.w33},
            {8,R.drawable.w31},
            {9,R.drawable.w30}
    };

    public static int getWeatherIcon(String conditionId){
        if(StringUtils.isEmpty(conditionId))return R.drawable.w44;
        int code = Integer.parseInt(conditionId);
        for(int i = 0; i < weather_icons.length;i++){
            int[] item = weather_icons[i];
            if(code == item[0]){
                return item[1];
            }
        }
        return R.drawable.w44;
    }

    public static int getConditionDayIcon(String conditionId){
        if(StringUtils.isEmpty(conditionId))return R.drawable.w44;
        int code = Integer.parseInt(conditionId);
        for(int i = 0; i < condition_day_icons.length;i++){
            int[] item = condition_day_icons[i];
            if(code == item[0]){
                return item[1];
            }
        }
        return R.drawable.w44;
    }

    public static int getConditionNightIcon(String conditionId){
        if(StringUtils.isEmpty(conditionId))return R.drawable.w44;
        int code = Integer.parseInt(conditionId);
        for(int i = 0; i < condition_night_icons.length;i++){
            int[] item = condition_night_icons[i];
            if(code == item[0]){
                return item[1];
            }
        }
        return R.drawable.w44;
    }

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
