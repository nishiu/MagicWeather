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

    private void update24Hourly(BaseViewHolder helper, MultiItemEntity item){
        boolean force = forceUpdate.get(item.getItemType());
        if(!force)return;
        HourlyItem hourlyItem = (HourlyItem)item;
        View hourlyView = helper.getView(R.id.group_hourly);
        if(hourlyItem.getHours() != null && hourlyItem.getHours() != null){
            hourlyView.setVisibility(View.VISIBLE);
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
        }else{
            hourlyView.setVisibility(View.GONE);
        }
        forceUpdate.put(item.getItemType(),false);
    }

    private void updateForecast(BaseViewHolder helper, MultiItemEntity item){
        boolean force = forceUpdate.get(item.getItemType());
        if(!force)return;
        ForecastItem forecastItem = (ForecastItem)item;
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
            List<YunForecastItem> forecastItems = forecastItem.getForecastItems();
            forecastItems = forecastItems.subList(0,Math.min(forecastItems.size(),7));
            RecyclerView recyclerView = helper.getView(R.id.weather_week_recyclerview);
            WeatherWeekAdapter adapter = forecastAdapterArray.get(helper.getLayoutPosition());
            if(adapter == null){
                adapter = new WeatherWeekAdapter(activity);
                recyclerView.setAdapter(adapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                adapter.setNewData(forecastItems);
                forecastAdapterArray.put(helper.getLayoutPosition(),adapter);
            }
            adapter.setNewData(forecastItems);

            if(forecastItems == null)return;
        }else{

        }
        forceUpdate.put(item.getItemType(),false);
    }

    private void updateAqi(BaseViewHolder helper, MultiItemEntity item){
        boolean force = forceUpdate.get(item.getItemType());
        if(!force)return;
        AqiItem aqiItem = (AqiItem)item;
        LinearLayout aqiParent = helper.getView(R.id.item_root);
        WaveView waveView = helper.getView(R.id.aqi_capsule);
        waveView.setShapeType(WaveView.ShapeType.CIRCLE);
        if(aqiItem.getYunAqi() != null){
            aqiParent.setVisibility(View.VISIBLE);
            YunAqi baseAqi = aqiItem.getYunAqi();
            String quality = WeatherUtils.getAqiValue(baseAqi.getAir());
            helper.setText(R.id.tv_aqi_capsule_value,baseAqi.getAir());
            helper.setText(R.id.tv_aqi_capsule_quality,quality);
            helper.setVisible(R.id.btn_more,true);
            helper.setText(R.id.tv_aqi_content,baseAqi.getAir_tips());
            if(quality.equals("优")){
                waveView.setWaveColor(Color.parseColor("#2021CA49"),
                        Color.parseColor("#4021CA49"),
                        Color.parseColor("#8021CA49"));
            }
            else if(quality.equals("良")){
                waveView.setWaveColor(Color.parseColor("#20FEDD00"),
                        Color.parseColor("#40FEDD00"),
                        Color.parseColor("#80FEDD00"));
            }
            else if(quality.equals("轻度")){
                waveView.setWaveColor(Color.parseColor("#20FFA200"),
                        Color.parseColor("#40FFA200"),
                        Color.parseColor("#80FFA200"));
            }
            else if(quality.equals("中度")){
                waveView.setWaveColor(Color.parseColor("#20FE1E1E"),
                        Color.parseColor("#40FE1E1E"),
                        Color.parseColor("#80FE1E1E"));
            }
            else if(quality.equals("重度")){
                waveView.setWaveColor(Color.parseColor("#20A20050"),
                        Color.parseColor("#40A20050"),
                        Color.parseColor("#80A20050"));
            }
            else if(quality.equals("严重")){
                waveView.setWaveColor(Color.parseColor("#2081022B"),
                        Color.parseColor("#4081022B"),
                        Color.parseColor("#8081022B"));
            }
            WaveHelper waveHelper = new WaveHelper(waveView,WaveHelper.AQI);
            waveHelper.start();

            setAqiItem(helper.getView(R.id.item_pm25),"PM2.5",baseAqi.getPm25(),baseAqi.getAir_level());
            setAqiItem(helper.getView(R.id.item_pm10),"可吸入颗粒物",baseAqi.getPm10(),baseAqi.getAir_level());
            setAqiItem(helper.getView(R.id.item_no2),"二氧化氮",baseAqi.getNo2(),baseAqi.getAir_level());
            setAqiItem(helper.getView(R.id.item_so2),"二氧化硫",baseAqi.getSo2(),baseAqi.getAir_level());
            setAqiItem(helper.getView(R.id.item_co),"一氧化碳",baseAqi.getCo(),baseAqi.getAir_level());
            setAqiItem(helper.getView(R.id.item_o3),"臭氧指数",baseAqi.getO3(),baseAqi.getAir_level());

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
            setAqiItem(helper.getView(R.id.item_pm25),"PM2.5","--","");
            setAqiItem(helper.getView(R.id.item_pm10),"可吸入颗粒物","--","");
            setAqiItem(helper.getView(R.id.item_no2),"二氧化氮","--","");
            setAqiItem(helper.getView(R.id.item_so2),"二氧化硫","--","");
            setAqiItem(helper.getView(R.id.item_co),"一氧化碳","--","");
            setAqiItem(helper.getView(R.id.item_o3),"臭氧指数","--","");
        }
        forceUpdate.put(item.getItemType(),false);
    }

    private void setAqiItem(View itemView,String name,String value,String level){
        TextView tvName = itemView.findViewById(R.id.item_name);
        tvName.setText(name);
        TextView tvValue = itemView.findViewById(R.id.item_value);
        tvValue.setText(value);
        View bg = itemView.findViewById(R.id.item_level_color);
        if(StringUtils.isEmpty(level)){
            bg.setBackgroundColor(Color.parseColor("#9a9a9a"));
        }
        if(level.equals("优")){
            bg.setBackgroundColor(Color.parseColor("#21CA49"));
        }
        else if(level.equals("良")){
            bg.setBackgroundColor(Color.parseColor("#FEDD00"));
        }
        else if(level.equals("轻度")){
            bg.setBackgroundColor(Color.parseColor("#FFA200"));
        }
        else if(level.equals("中度")){
            bg.setBackgroundColor(Color.parseColor("#FE1E1E"));
        }
        else if(level.equals("重度")){
            bg.setBackgroundColor(Color.parseColor("#A20050"));
        }
        else if(level.equals("严重")){
            bg.setBackgroundColor(Color.parseColor("#81022B"));
        }
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
