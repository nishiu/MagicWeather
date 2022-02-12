package com.frostsowner.magic.weather.domain.weather;

import com.fm.commons.domain.BaseEntity;

public class YunLiveIndexItem extends BaseEntity {

    private String level;
    private String tips;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }
}
