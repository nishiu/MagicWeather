package com.frostsowner.magic.weather.domain;


import com.frostsowner.magic.weather.domain.weather.YunForecastItem;

import java.util.List;

public class TwoDaysItem extends BaseWeather {

    private List<YunForecastItem> forecastItems;

    public List<YunForecastItem> getForecastItems() {
        return forecastItems;
    }

    public void setForecastItems(List<YunForecastItem> forecastItems) {
        this.forecastItems = forecastItems;
    }

    @Override
    public int getItemType() {
        return TYPE_TWO_DAYS;
    }
}
