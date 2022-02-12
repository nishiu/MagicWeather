package com.frostsowner.magic.weather.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fm.commons.logic.BeanFactory;
import com.fm.commons.util.PixelUtils;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.domain.weather.YunHours;
import com.frostsowner.magic.weather.logic.SimpleDateManager;
import com.frostsowner.magic.weather.utils.StringUtils;
import com.frostsowner.magic.weather.utils.WeatherResUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WeatherHourlyAdapter extends BaseQuickAdapter<YunHours, BaseViewHolder> {

    private Context context;

    public WeatherHourlyAdapter(Context context) {
        super(R.layout.item_weather_hour);
        this.context = context;
    }

    @Override
    public void setNewData(@Nullable List<YunHours> data) {
        super.setNewData(data);
        if(data == null)return;
    }

    @Override
    protected void convert(BaseViewHolder helper, YunHours baseHourly) {
        updateLayoutWidth(helper);
        updateWeekLayout(helper,baseHourly);
        updateDayWeather(helper,baseHourly);
        updateWind(helper,baseHourly);
    }

    private void updateWeekLayout(BaseViewHolder helper, YunHours yunHours){
        helper.setText(R.id.tv_hour_time,yunHours.getHours());
    }

    private void updateDayWeather(BaseViewHolder helper,YunHours yunHours){
        boolean isNight = false;
        if(TextUtils.equals("现在",yunHours.getHours())){
            isNight = StringUtils.isNight(System.currentTimeMillis());
        }else{
            isNight = isNight(yunHours.getHours()+":00");
        }
        helper.setBackgroundRes(R.id.icon_weather, isNight?WeatherResUtils.getYunWeatherNightIcon(yunHours.getWea_img()):WeatherResUtils.getYunWeatherDayIcon(yunHours.getWea_img()));
        helper.setText(R.id.tv_weather,yunHours.getWea());
    }

    private void updateWind(BaseViewHolder holder,YunHours yunHours){
        holder.setText(R.id.tv_wind_direction, yunHours.getWin());
        holder.setText(R.id.tv_wind_level,yunHours.getWin_speed());
    }

    private void updateLayoutWidth(BaseViewHolder helper){
        RelativeLayout rootView = (RelativeLayout)helper.itemView;
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)rootView.getLayoutParams();
        layoutParams.width = getItemWidth();
        rootView.setLayoutParams(layoutParams);
    }

    public final boolean isNight(String timeString){
        if(timeString.contains("/"))return false;
        try {
            SimpleDateManager simpleDateManager = BeanFactory.getBean(SimpleDateManager.class);
            SimpleDateFormat dateFormat = simpleDateManager.getFormatMap("HH:mm:ss");
//            Log.d("ijimu","update time : "+timeString+" , length : "+timeString.length());
            Date nightDate = dateFormat.parse("18:00:00");
            Date nightDate2 = dateFormat.parse("6:00:00");
            Date targetTime = dateFormat.parse(timeString);
//	        Log.d("ijimu","是否是晚上 : "+(targetTime.getTime() < nightDate2.getTime() || targetTime.getTime() > nightDate.getTime()));
            return (targetTime.getTime() < nightDate2.getTime() || targetTime.getTime() > nightDate.getTime());
        }catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }

    private int getItemWidth(){
        int screenW = getWidthHeight()[0]- PixelUtils.dip2px(context,20);
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
