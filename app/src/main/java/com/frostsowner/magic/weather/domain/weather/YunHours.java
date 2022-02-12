package com.frostsowner.magic.weather.domain.weather;

import com.fm.commons.domain.BaseEntity;

public class YunHours extends BaseEntity {

    private String hours;
    private String wea;
    private String wea_img;
    private String tem;
    private String win;
    private String win_speed;
    private String aqinum;

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
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

    public String getAqinum() {
        return aqinum;
    }

    public void setAqinum(String aqinum) {
        this.aqinum = aqinum;
    }
}
