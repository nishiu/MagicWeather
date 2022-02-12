package com.frostsowner.magic.weather.adapter;

import static com.frostsowner.magic.weather.domain.BaseWeather.TYPE_24_HOURLY;
import static com.frostsowner.magic.weather.domain.BaseWeather.TYPE_AQI;
import static com.frostsowner.magic.weather.domain.BaseWeather.TYPE_CONDITION;
import static com.frostsowner.magic.weather.domain.BaseWeather.TYPE_FORECAST;
import static com.frostsowner.magic.weather.domain.BaseWeather.TYPE_LIVE;
import static com.frostsowner.magic.weather.domain.BaseWeather.TYPE_NEWS;
import static com.frostsowner.magic.weather.domain.BaseWeather.TYPE_TOP_ALERT;
import static com.frostsowner.magic.weather.domain.BaseWeather.TYPE_TWO_DAYS;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.fm.commons.logic.BeanFactory;
import com.fm.commons.util.PixelUtils;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.activity.ConditionDetailsActivity;
import com.frostsowner.magic.weather.domain.AdLiveItem;
import com.frostsowner.magic.weather.domain.AqiItem;
import com.frostsowner.magic.weather.domain.ConditionItem;
import com.frostsowner.magic.weather.domain.ForecastItem;
import com.frostsowner.magic.weather.domain.HourlyItem;
import com.frostsowner.magic.weather.domain.LiveIndexItem;
import com.frostsowner.magic.weather.domain.TopAlertItem;
import com.frostsowner.magic.weather.domain.TwoDaysItem;
import com.frostsowner.magic.weather.domain.weather.YunAqi;
import com.frostsowner.magic.weather.domain.weather.YunCondition;
import com.frostsowner.magic.weather.domain.weather.YunForecastItem;
import com.frostsowner.magic.weather.domain.weather.YunHours;
import com.frostsowner.magic.weather.domain.weather.YunLiveIndex;
import com.frostsowner.magic.weather.domain.weather.YunLiveIndexItem;
import com.frostsowner.magic.weather.event.ActionEvent;
import com.frostsowner.magic.weather.event.EventType;
import com.frostsowner.magic.weather.impl.AdapterNotifyListener;
import com.frostsowner.magic.weather.impl.DebugLogger;
import com.frostsowner.magic.weather.logic.SimpleDateManager;
import com.frostsowner.magic.weather.logic.WaveHelper;
import com.frostsowner.magic.weather.utils.StringUtils;
import com.frostsowner.magic.weather.utils.WeatherResUtils;
import com.frostsowner.magic.weather.utils.WeatherUtils;
import com.frostsowner.magic.weather.view.WaveView;
import com.github.florent37.viewanimator.ViewAnimator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.oushangfeng.pinnedsectionitemdecoration.utils.FullSpanUtil;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.xfans.lib.voicewaveview.VoiceWaveView;
import me.xfans.lib.voicewaveview.WaveMode;

public class CityWeatherAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> implements DebugLogger {

    private SparseArray<WeatherHourlyAdapter> hourlyAdapterArray;
    private SparseArray<LiveIndexAdapter> liveIndexAdapterArray;
    private SparseArray<WeatherWeekAdapter> forecastAdapterArray;
    private SparseBooleanArray forceUpdate;

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat itemDayFormat;

    private boolean hourlySlip = false;
    private boolean forecastSlip = false;

    protected FragmentActivity activity;
    private AdapterNotifyListener adapterNotifyListener;
    private SimpleDateManager simpleDateManager;

    public CityWeatherAdapter(FragmentActivity activity){
        super(null);
        this.activity = activity;
        addItemType(TYPE_24_HOURLY, R.layout.item_24_hourly_view);
        addItemType(TYPE_FORECAST, R.layout.item_forecast_view);
        addItemType(TYPE_AQI, R.layout.item_aqi_view);
        addItemType(TYPE_LIVE, R.layout.item_live_index_view);
        hourlyAdapterArray = new SparseArray<>();
        liveIndexAdapterArray = new SparseArray<>();
        forecastAdapterArray = new SparseArray<>();
        forceUpdate = new SparseBooleanArray();
        simpleDateManager = BeanFactory.getBean(SimpleDateManager.class);
        dateFormat = simpleDateManager.getFormatMap("yyyy-MM-dd");
        itemDayFormat = simpleDateManager.getFormatMap("MM月dd日");
    }

    public void setAdapterNotifyListener(AdapterNotifyListener adapterNotifyListener){
        this.adapterNotifyListener = adapterNotifyListener;
    }

    @Override
    public void setNewData(@Nullable List<MultiItemEntity> data) {
        forceUpdate.clear();
        for(int i = 0; i < data.size();i++){
            int itemType = data.get(i).getItemType();
            forceUpdate.put(itemType,true);
        }
        super.setNewData(data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item){
//        Log.d("ijimu","convert, type : "+helper.getItemViewType());
        switch (helper.getItemViewType()){
            case TYPE_24_HOURLY:
                update24Hourly(helper,item);
                break;
            case TYPE_FORECAST:
                updateForecast(helper,item);
                break;
            case TYPE_AQI:
                updateAqi(helper,item);
                break;
            case TYPE_LIVE:
                updateLiveIndex(helper,item);
                break;
        }
        if(adapterNotifyListener != null)adapterNotifyListener.onNotify();
    }

    private void updateTwoDays(BaseViewHolder helper, MultiItemEntity item){
        boolean force = forceUpdate.get(item.getItemType());
        if(!force)return;
        TwoDaysItem twoDaysItem = (TwoDaysItem)item;
        View todayPanel = helper.getView(R.id.item_today_panel);
        View tomorrowPanel = helper.getView(R.id.item_tomorrow_panel);
        List<YunForecastItem> forecastData = twoDaysItem.getForecastItems();
        YunForecastItem todayForecast = null;
        YunForecastItem tomorrowForecast = null;
        for(int i = 0; i < forecastData.size();i++){
            YunForecastItem forecastItem = forecastData.get(i);
            String todayTime = simpleDateManager.transformTime("yyyy-MM-dd");
            String targetTime = forecastItem.getDate();
            if(todayTime.equals(targetTime)){
                todayForecast = forecastItem;
                if(i+1 < forecastData.size())tomorrowForecast = forecastData.get(i+1);
                break;
            }
        }
        updateBottomWeather(todayPanel,"今天",todayForecast);
        updateBottomWeather(tomorrowPanel,"明天",tomorrowForecast);
        todayPanel.setOnClickListener(v -> activity.startActivity(new Intent(activity,ConditionDetailsActivity.class)));
        final YunForecastItem finalTomorrowForecast = tomorrowForecast;
        tomorrowPanel.setOnClickListener(v -> {
            ActionEvent event = new ActionEvent(EventType.CHANGE_SECEN_FORECAST);
            if(finalTomorrowForecast != null)event.setAttr("time",finalTomorrowForecast.getDate());
            EventBus.getDefault().post(event);
        });
        forceUpdate.put(item.getItemType(),false);
    }

    private void updateBottomWeather(View root, String day, YunForecastItem baseForecast){
        if(baseForecast == null){
            root.setVisibility(View.INVISIBLE);
            return;
        }
        root.setVisibility(View.VISIBLE);
        TextView tvWeek = root.findViewById(R.id.tv_week);
        TextView tvTempMinMax = root.findViewById(R.id.tv_temp_min_max);
        ImageView iconWeather = root.findViewById(R.id.icon_weather);
        tvWeek.setText(day);

        TextView tvDay = root.findViewById(R.id.tv_day);
        tvDay.setText(StringUtils.formatTransformStyle(baseForecast.getDate(),dateFormat,itemDayFormat));

        tvTempMinMax.setText(baseForecast.getTem1()+"°/"+baseForecast.getTem2()+"°");
        iconWeather.setImageResource(WeatherResUtils.getYunWeatherDayIcon(baseForecast.getWea_img()));

        TextView tvWeather = root.findViewById(R.id.tv_weather);
        tvWeather.setVisibility(View.VISIBLE);
        tvWeather.setText(baseForecast.getWea());
    }

    private void update24Hourly(BaseViewHolder helper, MultiItemEntity item){
        boolean force = forceUpdate.get(item.getItemType());
        if(!force)return;
        HourlyItem hourlyItem = (HourlyItem)item;
        HorizontalScrollView hourlyListView = helper.getView(R.id.hourly_list_view);
        hourlyListView.scrollTo(PixelUtils.dp2px(activity,20),0);
//        hourlyView = root.findViewById(R.id.group_hourly);
        if(Build.VERSION.SDK_INT >= 23){
            hourlyListView.setOnScrollChangeListener(new View.OnScrollChangeListener(){
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if(hourlyListView.getScrollX() > 0 && !hourlySlip){
                        hourlySlip = true;
                        Log.e("ijimu","hourly slip : "+hourlyListView.getScrollX());
                    }
                }
            });
        }
        if(hourlyItem.getHours() != null && hourlyItem.getHours() != null){
            hourlyListView.setVisibility(View.VISIBLE);
            List<YunHours> baseHourlyList = hourlyItem.getHours();
            RecyclerView recyclerView = helper.getView(R.id.weather_hourly_recyclerview);
            WeatherHourlyAdapter adapter = hourlyAdapterArray.get(helper.getLayoutPosition());
//            Log.d("ijimu","hourly update, isForceUpdate : "+forceUpdate);
            if(adapter == null){
                adapter = new WeatherHourlyAdapter(activity);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(linearLayoutManager);
                hourlyAdapterArray.put(helper.getLayoutPosition(),adapter);
//                Log.d("ijimu","hourly create");
            }
            adapter.setNewData(hourlyItem.getHours());
//            Log.d("ijimu","forceUpdate hourly");
            forceUpdate.put(item.getItemType(),false);

            LineChart lineChart = helper.getView(R.id.weather_hourly_chart_1);

            if(baseHourlyList == null)return;
            //chartviewdouble
            int size = baseHourlyList.size();
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)lineChart.getLayoutParams();
            int width = getItemWidth();
            layoutParams.leftMargin = width/20;
            layoutParams.rightMargin = width/20;
            lineChart.setLayoutParams(layoutParams);

            ArrayList<Entry> tempData = new ArrayList<>();
            for(int i = 0; i < size;i++){
                YunHours yunHours = baseHourlyList.get(i);
                Entry entry = new Entry();
                float tempY = StringUtils.isEmpty(yunHours.getTem())?-1000:Integer.parseInt(yunHours.getTem());
                entry.setX(i);
                entry.setY(tempY);
                tempData.add(entry);
            }
            setLineChart(lineChart,tempData,Color.parseColor("#ffffff"),Color.parseColor("#1E70FF"), R.drawable.bg_blue_transform_vertical);
        }else{
            hourlyListView.setVisibility(View.GONE);
        }
        TextView riseInfoTv = helper.getView(R.id.rise_info);
        if(!StringUtils.isEmpty(hourlyItem.getSunrise())&&
           !StringUtils.isEmpty(hourlyItem.getSunset())){StringBuffer buffer = new StringBuffer();
            buffer.append("日出\t");
            buffer.append(hourlyItem.getSunrise()+"/");
            buffer.append("日落\t");
            buffer.append(hourlyItem.getSunset());
            riseInfoTv.setText(buffer.toString());
            riseInfoTv.setVisibility(View.VISIBLE);
        }else{
            riseInfoTv.setVisibility(View.INVISIBLE);
        }
        forceUpdate.put(item.getItemType(),false);
    }

    private void setLineChart(LineChart lineChart,ArrayList<Entry> data,int circleColor,int circleHoleColor,int filterDrawable){
        lineChart.getLegend().setEnabled(false);
        lineChart.setDrawBorders(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setScaleEnabled(false);
        lineChart.getXAxis().setDrawLabels(false);
        lineChart.getXAxis().setDrawAxisLine(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getAxisRight().setDrawAxisLine(false);
        lineChart.getXAxis().setGranularity(1f);
        lineChart.getXAxis().setLabelCount(data.size(),true);
        lineChart.setTouchEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setAvoidFirstLastClipping(true);

        LineDataSet set1 = new LineDataSet(data,"");
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set1.setCircleColor(circleColor);
        set1.setCircleHoleColor(circleHoleColor);
        set1.setColor(circleHoleColor);
        set1.setValueTextColor(circleHoleColor);
        set1.setCircleRadius(4f);
        set1.setCircleHoleRadius(3f);
        set1.setDrawCircleHole(true);
        set1.setDrawCircles(true);
        set1.setLineWidth(2);
        set1.setFormLineWidth(1f);
        set1.setValueTextSize(15f);
        set1.setDrawFilled(true);
        set1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value){
                return (int)value+"°";
            }
        });
        Drawable drawable = ContextCompat.getDrawable(activity, filterDrawable);
        set1.setFillDrawable(drawable);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        lineChart.setData(new LineData(dataSets));
        lineChart.invalidate();
    }

    private void updateForecast(BaseViewHolder helper, MultiItemEntity item){
        boolean force = forceUpdate.get(item.getItemType());
        if(!force)return;
        ForecastItem forecastItem = (ForecastItem)item;
        HorizontalScrollView forecastListView = helper.getView(R.id.forecast_list_view);
        forecastListView.post(new Runnable() {
            @Override
            public void run() {
                forecastListView.scrollTo(PixelUtils.dp2px(activity,20),0);
            }
        });
        if(Build.VERSION.SDK_INT >= 23){
            forecastListView.setOnScrollChangeListener(new View.OnScrollChangeListener(){
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if(forecastListView.getScrollX() > 0 && !forecastSlip){
                        forecastSlip = true;
                        Log.e("ijimu","forecast slip : "+forecastListView.getScrollX());
                    }
                }
            });
        }
        TextView btnMore = helper.getView(R.id.btn_more);
        btnMore.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ActionEvent event = new ActionEvent(EventType.CHANGE_SECEN_FORECAST);
                EventBus.getDefault().post(event);
                Map<String,String> params = new HashMap<>();
                params.put("position","查看更多");
            }
        });
        if(forecastItem.getForecastItems() != null && forecastItem.getForecastItems().size() > 0){
            forecastListView.setVisibility(View.VISIBLE);
            List<YunForecastItem> forecastItems = forecastItem.getForecastItems();
            forecastItems = forecastItems.subList(0,Math.min(forecastItems.size(),36));
            RecyclerView recyclerView = helper.getView(R.id.weather_week_recyclerview);
            WeatherWeekAdapter adapter = forecastAdapterArray.get(helper.getLayoutPosition());
            if(adapter == null){
                adapter = new WeatherWeekAdapter(activity);
                recyclerView.setAdapter(adapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                adapter.setNewData(forecastItems);
                forecastAdapterArray.put(helper.getLayoutPosition(),adapter);
            }
            adapter.setNewData(forecastItems);

            if(forecastItems == null)return;
            int size = forecastItems.size();

            LinearLayout groupWeatherChart = helper.getView(R.id.group_weather_chart);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)groupWeatherChart.getLayoutParams();
            int width = getItemWidth();
            layoutParams.leftMargin = width/20;
            layoutParams.rightMargin = width/20;
            groupWeatherChart.setLayoutParams(layoutParams);

            LineChart lineChartMax = helper.getView(R.id.weather_chart_max);
            LineChart lineChartMin = helper.getView(R.id.weather_chart_min);

            ArrayList<Entry> dayTempData = new ArrayList<>();
            ArrayList<Entry> nightTempData = new ArrayList<>();
            for(int i = 0; i < size;i++){
                YunForecastItem baseForecast = forecastItems.get(i);
                dayTempData.add(new Entry(i,StringUtils.isEmpty(baseForecast.getTem1())?0:Integer.parseInt(baseForecast.getTem1())));
                nightTempData.add(new Entry(i,StringUtils.isEmpty(baseForecast.getTem2())?0:Integer.parseInt(baseForecast.getTem2())));
            }
            setLineChart(lineChartMax,dayTempData,Color.parseColor("#ffffff"),Color.parseColor("#FFB41E"), R.drawable.bg_orange_transform_vertical);
            setLineChart(lineChartMin,nightTempData,Color.parseColor("#ffffff"),Color.parseColor("#1E70FF"), R.drawable.bg_blue_transform_vertical);
        }else{
            forecastListView.setVisibility(View.GONE);
        }
        forceUpdate.put(item.getItemType(),false);
    }

    private void updateAqi(BaseViewHolder helper, MultiItemEntity item){
        boolean force = forceUpdate.get(item.getItemType());
        if(!force)return;
        AqiItem aqiItem = (AqiItem)item;
        LinearLayout aqiParent = helper.getView(R.id.item_root);
        WaveView waveView = helper.getView(R.id.aqi_capsule);
        waveView.setShapeType(WaveView.ShapeType.SQUARE);
        if(aqiItem.getYunAqi() != null){
            aqiParent.setVisibility(View.VISIBLE);
            YunAqi baseAqi = aqiItem.getYunAqi();
            String quality = WeatherUtils.getAqiValue(baseAqi.getAir());
            helper.setText(R.id.tv_aqi_capsule_value,baseAqi.getAir());
            helper.setText(R.id.tv_aqi_capsule_quality,quality);
            helper.setVisible(R.id.btn_more,true);
            if(quality.equals("优")){
                waveView.setWaveColor(Color.parseColor("#2021CA49"),
                        Color.parseColor("#4021CA49"),
                        Color.parseColor("#8021CA49"));
                helper.setTextColor(R.id.tv_aqi_capsule_name,activity.getResources().getColor(R.color.color_quality_1));
            }
            else if(quality.equals("良")){
                waveView.setWaveColor(Color.parseColor("#20FEDD00"),
                        Color.parseColor("#40FEDD00"),
                        Color.parseColor("#80FEDD00"));
                helper.setTextColor(R.id.tv_aqi_capsule_name,activity.getResources().getColor(R.color.color_quality_2));
            }
            else if(quality.equals("轻度")){
                waveView.setWaveColor(Color.parseColor("#20FFA200"),
                        Color.parseColor("#40FFA200"),
                        Color.parseColor("#80FFA200"));
                helper.setTextColor(R.id.tv_aqi_capsule_name,activity.getResources().getColor(R.color.color_quality_3));
            }
            else if(quality.equals("中度")){
                waveView.setWaveColor(Color.parseColor("#20FE1E1E"),
                        Color.parseColor("#40FE1E1E"),
                        Color.parseColor("#80FE1E1E"));
                helper.setTextColor(R.id.tv_aqi_capsule_name,activity.getResources().getColor(R.color.color_quality_4));
            }
            else if(quality.equals("重度")){
                waveView.setWaveColor(Color.parseColor("#20A20050"),
                        Color.parseColor("#40A20050"),
                        Color.parseColor("#80A20050"));
                helper.setTextColor(R.id.tv_aqi_capsule_name,activity.getResources().getColor(R.color.color_quality_5));
            }
            else if(quality.equals("严重")){
                waveView.setWaveColor(Color.parseColor("#2081022B"),
                        Color.parseColor("#4081022B"),
                        Color.parseColor("#8081022B"));
                helper.setTextColor(R.id.tv_aqi_capsule_name,activity.getResources().getColor(R.color.color_quality_6));
            }
            WaveHelper waveHelper = new WaveHelper(waveView);
            waveHelper.start();

            setAqiItem(helper.getView(R.id.item_pm25),"PM2.5(细颗粒物)",baseAqi.getPm25());
            setAqiItem(helper.getView(R.id.item_pm10),"PM10(可吸入颗粒物)",baseAqi.getPm10());
            setAqiItem(helper.getView(R.id.item_no2),"NO2(二氧化氮)",baseAqi.getNo2());
            setAqiItem(helper.getView(R.id.item_so2),"S02(二氧化硫)",baseAqi.getSo2());
            setAqiItem(helper.getView(R.id.item_co),"CO(一氧化碳)",baseAqi.getCo());
            setAqiItem(helper.getView(R.id.item_o3),"O3(臭氧指数)",baseAqi.getO3());

//            aqiParent.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(activity, AqiDetailsActivity.class);
//                    activity.startActivity(intent);
//                    Map<String,String> params = new HashMap<>();
//                    params.put("position","查看更多");
//                }
//            });
        }
        else{
            waveView.setWaveColor(Color.parseColor("#20b4b4b4"),
            Color.parseColor("#40b4b4b4"),
            Color.parseColor("#80b4b4b4"));
            helper.setVisible(R.id.btn_more,false);
            helper.setText(R.id.tv_aqi_capsule_value,"0");
            helper.setText(R.id.tv_aqi_capsule_quality,"无");
            helper.setTextColor(R.id.tv_aqi_capsule_name,Color.parseColor("#ffffff"));
            setAqiItem(helper.getView(R.id.item_pm25),"PM2.5(细颗粒物)","--");
            setAqiItem(helper.getView(R.id.item_pm10),"PM10(可吸入颗粒物)","--");
            setAqiItem(helper.getView(R.id.item_no2),"NO2(二氧化氮)","--");
            setAqiItem(helper.getView(R.id.item_so2),"S02(二氧化硫)","--");
            setAqiItem(helper.getView(R.id.item_co),"CO(一氧化碳)","--");
            setAqiItem(helper.getView(R.id.item_o3),"O3(臭氧指数)","--");
        }
        forceUpdate.put(item.getItemType(),false);
    }

    private void setAqiItem(View itemView,String name,String value){
        TextView tvName = itemView.findViewById(R.id.item_name);
        tvName.setText(name);
        TextView tvValue = itemView.findViewById(R.id.item_value);
        tvValue.setText(value);
    }

    private void updateLiveIndex(BaseViewHolder helper,MultiItemEntity item){
        boolean force = forceUpdate.get(item.getItemType());
        if(!force)return;
        LiveIndexItem liveIndexItem = (LiveIndexItem)item;
        if(liveIndexItem.getYunLiveIndex() != null){
            RecyclerView recyclerView = helper.getView(R.id.live_index_recycler_view);
            LiveIndexAdapter liveIndexAdapter = liveIndexAdapterArray.get(helper.getLayoutPosition());
            if(liveIndexAdapter == null){
                liveIndexAdapter = new LiveIndexAdapter(activity,LiveIndexAdapter.FRAGMENT, R.layout.item_live_index_1);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(activity,4);
                recyclerView.setAdapter(liveIndexAdapter);
                recyclerView.setLayoutManager(gridLayoutManager);
                liveIndexAdapterArray.put(helper.getLayoutPosition(),liveIndexAdapter);
            }
            List<AdLiveItem> adLiveItemList = new LinkedList<>();
            YunLiveIndex yunLiveIndex = liveIndexItem.getYunLiveIndex();
            if(canShowLiveIndexItem(yunLiveIndex.getXiche())){
                adLiveItemList.add(createAdLiveItem(AdLiveItem.TYPE_LIVE, yunLiveIndex.getXiche().getLevel(), "洗车"));
            }
            if(canShowLiveIndexItem(yunLiveIndex.getChuanyi())){
                adLiveItemList.add(createAdLiveItem(AdLiveItem.TYPE_LIVE, yunLiveIndex.getChuanyi().getLevel(), "穿衣"));
            }
            if(canShowLiveIndexItem(yunLiveIndex.getZiwaixian())){
                adLiveItemList.add(createAdLiveItem(AdLiveItem.TYPE_LIVE, yunLiveIndex.getZiwaixian().getLevel(), "紫外线"));
            }
            if(canShowLiveIndexItem(yunLiveIndex.getGanmao())){
                adLiveItemList.add(createAdLiveItem(AdLiveItem.TYPE_LIVE, yunLiveIndex.getGanmao().getLevel(), "感冒"));
            }
            if(canShowLiveIndexItem(yunLiveIndex.getChenlian())){
                adLiveItemList.add(createAdLiveItem(AdLiveItem.TYPE_LIVE, yunLiveIndex.getChenlian().getLevel(), "运动"));
            }
            liveIndexAdapter.setNewData(adLiveItemList);
        }
        forceUpdate.put(item.getItemType(),false);
    }

    private AdLiveItem createAdLiveItem(int type, String content, String label){
        return createAdLiveItem(type,content,label,"");
    }

    private AdLiveItem createAdLiveItem(int type, String content, String label,String contentColor){
        AdLiveItem adLiveItem = new AdLiveItem();
        adLiveItem.setType(type);
        adLiveItem.setContent(content);
        adLiveItem.setLabel(label);
        adLiveItem.setContentColor(StringUtils.isEmpty(contentColor)? Color.parseColor("#333333"):Color.parseColor(contentColor));
        return adLiveItem;
    }

    private boolean canShowLiveIndexItem(YunLiveIndexItem indexItem){
        if(indexItem == null)return false;
        return !StringUtils.isEmpty(indexItem.getLevel())&&!StringUtils.isEmpty(indexItem.getTips());
    }

    @Override
    public void logInfo(String msg){

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        FullSpanUtil.onAttachedToRecyclerView(recyclerView, this, TYPE_NEWS);
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        FullSpanUtil.onViewAttachedToWindow(holder, this, TYPE_NEWS);
    }

    private int getItemWidth(){
        int screenW = getWidthHeight()[0] - PixelUtils.dip2px(activity,20);
        return (screenW);
    }

    private int[] getWidthHeight(){
        Resources resources = activity.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        return new int[]{width,height};
    }
}
