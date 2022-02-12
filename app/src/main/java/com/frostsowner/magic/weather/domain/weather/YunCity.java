package com.frostsowner.magic.weather.domain.weather;

import android.text.TextUtils;

import com.fm.commons.domain.BaseEntity;

public class YunCity extends BaseEntity {

    private String id;
    private String cityEn;
    private String cityZh;
    private String provinceEn;
    private String provinceZh;
    private String leaderEn;
    private String leaderZh;
    private String lat;
    private String lon;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCityEn() {
        return cityEn;
    }

    public void setCityEn(String cityEn) {
        this.cityEn = cityEn;
    }

    public String getCityZh() {
        return cityZh;
    }

    public void setCityZh(String cityZh) {
        this.cityZh = cityZh;
    }

    public String getProvinceEn() {
        return provinceEn;
    }

    public void setProvinceEn(String provinceEn) {
        this.provinceEn = provinceEn;
    }

    public String getProvinceZh() {
        return provinceZh;
    }

    public void setProvinceZh(String provinceZh) {
        this.provinceZh = provinceZh;
    }

    public String getLeaderEn() {
        return leaderEn;
    }

    public void setLeaderEn(String leaderEn) {
        this.leaderEn = leaderEn;
    }

    public String getLeaderZh() {
        return leaderZh;
    }

    public void setLeaderZh(String leaderZh) {
        this.leaderZh = leaderZh;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (!getClass().isInstance(obj))
            return false;
        YunCity yunCity = (YunCity) obj;
        return TextUtils.equals(yunCity.getCityEn(),this.cityEn)&&
                TextUtils.equals(yunCity.getLeaderEn(),this.leaderEn)&&
                TextUtils.equals(yunCity.getProvinceEn(),this.provinceEn)&&
                TextUtils.equals(yunCity.getId(),this.id);
    }

}
