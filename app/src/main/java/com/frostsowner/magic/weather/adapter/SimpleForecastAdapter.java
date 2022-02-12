package com.frostsowner.magic.weather.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fm.commons.logic.BeanFactory;
import com.fm.commons.util.PixelUtils;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.domain.weather.YunForecastItem;
import com.frostsowner.magic.weather.logic.SimpleDateManager;
import com.frostsowner.magic.weather.utils.StringUtils;
import com.frostsowner.magic.weather.utils.WeatherResUtils;

import java.text.SimpleDateFormat;

public class SimpleForecastAdapter extends BaseQuickAdapter<YunForecastItem, BaseViewHolder> {

    private Context context;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat itemDayFormat;

    private SimpleDateManager simpleDateManager;
    private YunForecastItem selected;

    public SimpleForecastAdapter(Context context) {
        super(R.layout.item_simple_forecast);
        this.context = context;
        simpleDateManager = BeanFactory.getBean(SimpleDateManager.class);
        dateFormat = simpleDateManager.getFormatMap("yyyy-MM-dd");
        itemDayFormat = simpleDateManager.getFormatMap("MM/dd");
    }

    public void setSelected(YunForecastItem baseForecast){
        this.selected = baseForecast;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, YunForecastItem baseForecast) {
        helper.addOnClickListener(R.id.item_root);
        updateLayoutWidth(helper);
        updateWeekLayout(helper,baseForecast);
        updateDayWeather(helper,baseForecast);
        updateDate(helper,baseForecast);
    }

    private void updateWeekLayout(BaseViewHolder helper, YunForecastItem baseForecast){
        if(simpleDateManager.transformTime("yyyy-MM-dd").equals(baseForecast.getDate())){
            helper.setText(R.id.tv_week_name,"今天");
        }else{
            helper.setText(R.id.tv_week_name,baseForecast.getWeek());
        }
        if(selected != null && baseForecast.getDate().equals(selected.getDate())){
            helper.setTextColor(R.id.tv_week_name, Color.parseColor("#ffffff"));
        }else{
            helper.setTextColor(R.id.tv_week_name, Color.parseColor("#7fffffff"));
        }
    }

    private void updateDayWeather(BaseViewHolder helper,YunForecastItem baseForecast){
        ImageView iconImageView = helper.getView(R.id.icon_day_weather);
        if(selected != null && baseForecast.getDate().equals(selected.getDate())){
            iconImageView.setAlpha(1f);
        }else{
            iconImageView.setAlpha(0.5f);
        }
        iconImageView.setImageResource(WeatherResUtils.getYunWeatherDayIcon(baseForecast.getWea_img()));
    }

    private void updateDate(BaseViewHolder helper,YunForecastItem baseForecast){
        if(selected != null && baseForecast.getDate().equals(selected.getDate())){
            helper.setTextColor(R.id.tv_date, Color.parseColor("#ffffff"));
        }else{
            helper.setTextColor(R.id.tv_date, Color.parseColor("#7fffffff"));
        }
        helper.setText(R.id.tv_date,StringUtils.formatTransformStyle(baseForecast.getDate(),dateFormat,itemDayFormat));
    }

    private void updateLayoutWidth(BaseViewHolder helper){
        LinearLayout rootView = (LinearLayout)helper.itemView;
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)rootView.getLayoutParams();
        layoutParams.width = getItemWidth();
        rootView.setLayoutParams(layoutParams);
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
