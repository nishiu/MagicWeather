package com.frostsowner.magic.weather.domain;


import com.frostsowner.magic.weather.domain.weather.YunHours;

import java.util.List;

public class HourlyItem extends BaseWeather {

    private List<YunHours> hours;
    private String sunrise;
    private String sunset;

    public List<YunHours> getHours() {
        return hours;
    }

    public void setHours(List<YunHours> hours){
        this.hours = hours;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    @Override
    public int getItemType() {
        return TYPE_24_HOURLY;
    }
}
