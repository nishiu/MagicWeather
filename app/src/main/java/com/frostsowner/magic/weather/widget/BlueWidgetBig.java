package com.frostsowner.magic.weather.widget;

import com.frostsowner.magic.weather.base.BaseAppWidgetProvider;
import com.frostsowner.magic.weather.domain.WidgetStyle;

/**
 * Implementation of App Widget functionality.
 */
public class BlueWidgetBig extends BaseAppWidgetProvider {

    @Override
    public int update() {
        return WidgetStyle.BLUE_BIG;
    }
}

