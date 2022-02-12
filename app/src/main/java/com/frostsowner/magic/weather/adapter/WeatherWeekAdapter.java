package com.frostsowner.magic.weather.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fm.commons.logic.BeanFactory;
import com.fm.commons.util.PixelUtils;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.domain.weather.YunForecastItem;
import com.frostsowner.magic.weather.event.ActionEvent;
import com.frostsowner.magic.weather.event.EventType;
import com.frostsowner.magic.weather.logic.SimpleDateManager;
import com.frostsowner.magic.weather.utils.StringUtils;
import com.frostsowner.magic.weather.utils.WeatherResUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherWeekAdapter extends BaseQuickAdapter<YunForecastItem, BaseViewHolder> {

    private Context context;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat itemDayFormat;
    private Date today;
    private SimpleDateManager simpleDateManager;
    private int todayPosition = -1;

    public WeatherWeekAdapter(Context context){
        super(R.layout.item_weather_week);
        this.context = context;
        simpleDateManager = BeanFactory.getBean(SimpleDateManager.class);
        dateFormat = simpleDateManager.getFormatMap("yyyy-MM-dd");
        itemDayFormat = simpleDateManager.getFormatMap("MM/dd");
        today = simpleDateManager.getDate();
    }

    @Override
    public void setNewData(@Nullable List<YunForecastItem> data) {
        super.setNewData(data);
        if(data == null)return;
        for(int i = 0; i < data.size();i++){
            YunForecastItem forecastItem = data.get(i);
            if(dateFormat.format(today).equals(forecastItem.getDate())){
                todayPosition = i;
                return;
            }
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, YunForecastItem baseForecast) {
        updateLayoutWidth(helper,baseForecast);
        updateWeekLayout(helper,baseForecast);
        updateDayWeather(helper,baseForecast);
        updateNightWeather(helper,baseForecast);
    }

    private void updateWeekLayout(BaseViewHolder helper, YunForecastItem forecastItem){
        if(dateFormat.format(today).equals(forecastItem.getDate())){
            helper.setText(R.id.tv_week_name,"今天");
        }else{
            helper.setText(R.id.tv_week_name,StringUtils.getWeek(forecastItem.getDate(),dateFormat));
        }
        helper.setAlpha(R.id.item_mask,helper.getLayoutPosition() < todayPosition?0.4f:1f);
        helper.setText(R.id.tv_date,StringUtils.formatTransformStyle(forecastItem.getDate(),dateFormat,itemDayFormat));
    }

    private void updateDayWeather(BaseViewHolder helper,YunForecastItem forecastItem){
        helper.setBackgroundRes(R.id.icon_day_weather, WeatherResUtils.getYunWeatherDayIcon(forecastItem.getWea_day_img()));
        helper.setText(R.id.tv_day_weather,forecastItem.getWea_day());
    }

    private void updateNightWeather(BaseViewHolder helper,YunForecastItem forecastItem){
        helper.setBackgroundRes(R.id.icon_night_weather, WeatherResUtils.getYunWeatherNightIcon(forecastItem.getWea_night_img()));
        helper.setText(R.id.tv_night_weather,forecastItem.getWea_night());
    }

    private void updateLayoutWidth(BaseViewHolder helper,YunForecastItem forecastItem){
        LinearLayout rootView = (LinearLayout)helper.itemView;
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)rootView.getLayoutParams();
        layoutParams.width = getItemWidth();
        rootView.setLayoutParams(layoutParams);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionEvent event = new ActionEvent(EventType.CHANGE_SECEN_FORECAST);
                event.setAttr("time",forecastItem.getDate());
                EventBus.getDefault().post(event);
                Map<String,String> params = new HashMap<>();
                params.put("position","天气点击-"+helper.getLayoutPosition());
            }
        });
    }

    private int getItemWidth(){
        int screenW = getWidthHeight()[0] - PixelUtils.dip2px(context,20);
        return (int)(screenW/5f);
    }

    private int[] getWidthHeight(){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        return new int[]{width,height};
    }
}
