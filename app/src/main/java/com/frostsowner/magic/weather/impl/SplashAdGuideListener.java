package com.frostsowner.magic.weather.impl;

public interface SplashAdGuideListener {

    /**
     * @param isChecked 用户协议开关状态
     */
    void checkDeal(boolean isChecked);

    /**
     * @param isChecked 广告协议按钮
     */
    void checkAd(boolean isChecked, String apkUrl, String packageName);
}
