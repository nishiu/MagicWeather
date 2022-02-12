package com.frostsowner.magic.weather.domain.weather;


import com.frostsowner.magic.weather.domain.BaseData;

import java.util.List;

public class YunForecast extends BaseData {

    private String cityid;
    private String update_time;
    private String city;
    private List<YunForecastItem> data;

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time){
        this.update_time = update_time;
    }

    public String getCity(){
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<YunForecastItem> getData() {
        return data;
    }

    public void setData(List<YunForecastItem> data) {
        this.data = data;
    }
}
