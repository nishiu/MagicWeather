package com.frostsowner.magic.weather.domain.weather;


import com.frostsowner.magic.weather.domain.BaseData;

import java.util.List;

public class YunCondition extends BaseData {

    private String cityid;
    private String date;
    private String week;
    private String update_time;
    private String city;
    private String cityEn;
    private String country;
    private String countryEn;
    private String wea;
    private String wea_img;
    private String tem;
    private String tem1;
    private String tem2;
    private String win;
    private String win_speed;
    private String win_meter;
    private String humidity;
    private String visibility;
    private String pressure;
    private String air;
    private String air_pm25;
    private String air_level;
    private String air_tips;
    private String wea_day;
    private String wea_day_img;
    private String wea_night;
    private String wea_night_img;
    private String sunrise;
    private String sunset;
    private YunAlert alarm;
    private YunAqi aqi;
    private List<YunHours> hours;
    private YunLiveIndex zhishu;

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityEn() {
        return cityEn;
    }

    public void setCityEn(String cityEn) {
        this.cityEn = cityEn;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryEn() {
        return countryEn;
    }

    public void setCountryEn(String countryEn) {
        this.countryEn = countryEn;
    }

    public String getWea() {
        return wea;
    }

    public void setWea(String wea) {
        this.wea = wea;
    }

    public String getWea_img() {
        return wea_img;
    }

    public void setWea_img(String wea_img) {
        this.wea_img = wea_img;
    }

    public String getTem() {
        return tem;
    }

    public void setTem(String tem) {
        this.tem = tem;
    }

    public String getTem1() {
        return tem1;
    }

    public void setTem1(String tem1) {
        this.tem1 = tem1;
    }

    public String getTem2() {
        return tem2;
    }

    public void setTem2(String tem2) {
        this.tem2 = tem2;
    }

    public String getWin() {
        return win;
    }

    public void setWin(String win) {
        this.win = win;
    }

    public String getWin_speed() {
        return win_speed;
    }

    public void setWin_speed(String win_speed) {
        this.win_speed = win_speed;
    }

    public String getWin_meter() {
        return win_meter;
    }

    public void setWin_meter(String win_meter) {
        this.win_meter = win_meter;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getAir() {
        return air;
    }

    public void setAir(String air) {
        this.air = air;
    }

    public String getAir_pm25() {
        return air_pm25;
    }

    public void setAir_pm25(String air_pm25) {
        this.air_pm25 = air_pm25;
    }

    public String getAir_level() {
        return air_level;
    }

    public void setAir_level(String air_level) {
        this.air_level = air_level;
    }

    public String getAir_tips() {
        return air_tips;
    }

    public void setAir_tips(String air_tips) {
        this.air_tips = air_tips;
    }

    public String getWea_day() {
        return wea_day;
    }

    public void setWea_day(String wea_day) {
        this.wea_day = wea_day;
    }

    public String getWea_day_img() {
        return wea_day_img;
    }

    public void setWea_day_img(String wea_day_img) {
        this.wea_day_img = wea_day_img;
    }

    public String getWea_night() {
        return wea_night;
    }

    public void setWea_night(String wea_night) {
        this.wea_night = wea_night;
    }

    public String getWea_night_img() {
        return wea_night_img;
    }

    public void setWea_night_img(String wea_night_img) {
        this.wea_night_img = wea_night_img;
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

    public YunAlert getAlarm() {
        return alarm;
    }

    public void setAlarm(YunAlert alarm) {
        this.alarm = alarm;
    }

    public YunAqi getAqi() {
        return aqi;
    }

    public void setAqi(YunAqi aqi) {
        this.aqi = aqi;
    }

    public List<YunHours> getHours() {
        return hours;
    }

    public void setHours(List<YunHours> hours) {
        this.hours = hours;
    }

    public YunLiveIndex getZhishu() {
        return zhishu;
    }

    public void setZhishu(YunLiveIndex zhishu) {
        this.zhishu = zhishu;
    }
}
