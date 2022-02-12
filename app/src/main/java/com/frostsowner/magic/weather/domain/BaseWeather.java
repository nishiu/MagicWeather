package com.frostsowner.magic.weather.domain;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.fm.commons.domain.BaseEntity;

public abstract class BaseWeather extends BaseEntity implements MultiItemEntity {

    public static final int TYPE_TOP_ALERT = 1_000;
    public static final int TYPE_CONDITION = 2_000;
    public static final int TYPE_TWO_DAYS = 3_000;
    public static final int TYPE_24_HOURLY = 4_000;
    public static final int TYPE_BANNER_1 = 5_000;
    public static final int TYPE_FORECAST = 6_000;
    public static final int TYPE_AQI = 7_000;
    public static final int TYPE_BANNER_2 = 8_000;
    public static final int TYPE_LIVE = 9_000;
    public static final int TYPE_NATIVE = 10_000;
    public static final int TYPE_NEWS = 11_000;
    public static final int TYPE_CALENDAR= 12_000;

}
