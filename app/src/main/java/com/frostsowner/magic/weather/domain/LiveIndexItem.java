package com.frostsowner.magic.weather.domain;


import com.frostsowner.magic.weather.domain.weather.YunLiveIndex;

public class LiveIndexItem extends BaseWeather {

    private YunLiveIndex yunLiveIndex;

    public YunLiveIndex getYunLiveIndex() {
        return yunLiveIndex;
    }

    public void setYunLiveIndex(YunLiveIndex yunLiveIndex) {
        this.yunLiveIndex = yunLiveIndex;
    }

    @Override
    public int getItemType() {
        return TYPE_LIVE;
    }
}
