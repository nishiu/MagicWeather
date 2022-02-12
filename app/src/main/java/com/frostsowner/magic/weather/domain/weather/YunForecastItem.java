package com.frostsowner.magic.weather.domain.weather;

import com.fm.commons.domain.BaseEntity;

public class YunForecastItem extends BaseEntity {

    private String date_nl;
    private String date;
    private String week;
    private String wea;
    private String wea_c;
    private String wea_img;
    private String wea_day;
    private String wea_day_img;
    private String wea_night;
    private String wea_night_img;
    private String tem1;
    private String tem2;
    private String win;
    private String win_day;
    private String win_night;

    public String getDate_nl() {
        return date_nl;
    }

    public void setDate_nl(String date_nl) {
        this.date_nl = date_nl;
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

    public String getWea() {
        return wea;
    }

    public void setWea(String wea) {
        this.wea = wea;
    }

    public String getWea_c() {
        return wea_c;
    }

    public void setWea_c(String wea_c) {
        this.wea_c = wea_c;
    }

    public String getWea_img() {
        return wea_img;
    }

    public void setWea_img(String wea_img) {
        this.wea_img = wea_img;
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

    public String getWin_day() {
        return win_day;
    }

    public void setWin_day(String win_day) {
        this.win_day = win_day;
    }

    public String getWin_night() {
        return win_night;
    }

    public void setWin_night(String win_night) {
        this.win_night = win_night;
    }
}
