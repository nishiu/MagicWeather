package com.frostsowner.magic.weather.domain;


import com.frostsowner.magic.weather.domain.weather.YunCondition;

public class ConditionItem extends BaseWeather {

    private YunCondition yunCondition;

    public YunCondition getYunCondition() {
        return yunCondition;
    }

    public void setYunCondition(YunCondition yunCondition) {
        this.yunCondition = yunCondition;
    }

    @Override
    public int getItemType() {
        return TYPE_CONDITION;
    }
}
