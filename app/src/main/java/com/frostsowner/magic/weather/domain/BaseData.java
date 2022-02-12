package com.frostsowner.magic.weather.domain;

import com.fm.commons.domain.BaseEntity;

public class BaseData extends BaseEntity {

    private long lastRequestTime;

    public long getLastRequestTime() {
        return lastRequestTime;
    }

    public void setLastRequestTime(long lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }
}
