package com.frostsowner.magic.weather.domain;


import com.frostsowner.magic.weather.domain.weather.YunAlert;

public class TopAlertItem extends BaseWeather {

    private YunAlert yunAlert;

    public YunAlert getYunAlert() {
        return yunAlert;
    }

    public void setYunAlert(YunAlert yunAlert) {
        this.yunAlert = yunAlert;
    }

    @Override
    public int getItemType() {
        return TYPE_TOP_ALERT;
    }
}
