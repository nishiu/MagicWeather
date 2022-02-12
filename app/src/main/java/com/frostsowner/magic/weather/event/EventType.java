package com.frostsowner.magic.weather.event;

public class EventType {

    public static final int WEATHER_REQUEST_SUCCESS = 0x123;
    public static final int WEATHER_REQUEST_FAILED = 0x124;
    public static final int WEATHER_REFRESHING = 0x122;

    public static final int CHANGE_SECEN_WEATHER = 0x140;
    public static final int CHANGE_SECEN_CALENDAR = 0x142;
    public static final int CHANGE_SECEN_SETTING = 0x143;

    public static final int CHANGE_SECEN_FORECAST = 0x143;
    public static final int CHANGE_SECEN_CONDITION = 0X144;

    public static final int CITY_UPDATE = 0x125;
    public static final int CHANGE_CURRENT_CITY = 0x126;

    public static final int WEATHER_VIEW_SCROLLING = 1_100;

    public static final int CHECK_UPGRADE = 1_800;

    public static final int CITY_WEATHER_SPEAK = 1_500;

    public static final int LOCATION_START = 1_600;

    public static final int WIDGET_ENTRY = 145;
}
