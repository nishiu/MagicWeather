package com.frostsowner.magic.weather.domain;


import com.frostsowner.magic.weather.domain.weather.YunAqi;

public class AqiItem extends BaseWeather {

    private YunAqi yunAqi;

    public YunAqi getYunAqi() {
        return yunAqi;
    }

    public void setYunAqi(YunAqi yunAqi){
        this.yunAqi = yunAqi;
    }

    @Override
    public int getItemType() {
        return TYPE_AQI;
    }
}
