package com.frostsowner.magic.weather.domain.weather;

import com.fm.commons.domain.BaseEntity;

public class YunWeather extends BaseEntity{

    private YunCondition condition;
    private YunForecast forecast;

    private long lastUpdateTime;
    private long updateTime;
    private boolean isLocate;
    private String cityName;
    private String cityId;
    private String city;
    private String district;
    private String address;
    private String semaAptag;

    public YunCondition getCondition() {
        return condition;
    }

    public void setCondition(YunCondition condition) {
        this.condition = condition;
    }

    public YunForecast getForecast() {
        return forecast;
    }

    public void setForecast(YunForecast forecast) {
        this.forecast = forecast;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isLocate() {
        return isLocate;
    }

    public void setLocate(boolean locate) {
        isLocate = locate;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSemaAptag() {
        return semaAptag;
    }

    public void setSemaAptag(String semaAptag) {
        this.semaAptag = semaAptag;
    }
}
