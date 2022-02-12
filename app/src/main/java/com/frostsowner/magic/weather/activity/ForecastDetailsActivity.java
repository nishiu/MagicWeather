package com.frostsowner.magic.weather.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fm.commons.logic.BeanFactory;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.adapter.LiveIndexAdapter;
import com.frostsowner.magic.weather.adapter.SimpleForecastAdapter;
import com.frostsowner.magic.weather.domain.AdLiveItem;
import com.frostsowner.magic.weather.domain.weather.YunForecastItem;
import com.frostsowner.magic.weather.domain.weather.YunLiveIndex;
import com.frostsowner.magic.weather.domain.weather.YunLiveIndexItem;
import com.frostsowner.magic.weather.domain.weather.YunWeather;
import com.frostsowner.magic.weather.logic.CityManager;
import com.frostsowner.magic.weather.logic.SimpleDateManager;
import com.frostsowner.magic.weather.utils.AddressUtils;
import com.frostsowner.magic.weather.utils.StringUtils;
import com.jaeger.library.StatusBarUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

public class ForecastDetailsActivity extends BgActivity{

    @BindView(R.id.status_bar)
    LinearLayout statusBarView;

    @BindView(R.id.forecast_recycler_view)
    RecyclerView forecastRecyclerView;

    @BindView(R.id.tv_max_min_temp)
    TextView tvTempMaxMin;
    @BindView(R.id.tv_condition)
    TextView tvCondition;
    @BindView(R.id.bg_group)
    ViewGroup bgGroup;

    @BindView(R.id.item_day_wind)
    View itemDayWind;
    @BindView(R.id.item_night_wind)
    View itemNightWind;
    @BindView(R.id.tv_city_name)
    TextView tvCityName;
    @BindView(R.id.img_location)
    ImageView imgLocation;

    @BindView(R.id.item_today_live_index)
    View groupLiveIndex;
    @BindView(R.id.live_index_recycler_view)
    RecyclerView liveIndexRecyclerView;

    @BindView(R.id.ad_group)
    ViewGroup adGroup;
    @BindView(R.id.native_ad_container)
    FrameLayout adContainer;

    private CityManager cityManager;
    private SimpleDateManager simpleDateManager;
    private SimpleForecastAdapter simpleForecastAdapter;
    private Typeface typeface;

    private boolean isSlip;
    private long stayTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_details);
        cityManager = BeanFactory.getBean(CityManager.class);
        simpleDateManager = BeanFactory.getBean(SimpleDateManager.class);
        StatusBarUtil.setTransparentForImageView(this,null);
        StatusBarUtil.setDarkMode(this);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)statusBarView.getLayoutParams();
        layoutParams.height = getStatusBarHeight(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        simpleForecastAdapter = new SimpleForecastAdapter(this);
        forecastRecyclerView.setAdapter(simpleForecastAdapter);
        forecastRecyclerView.setLayoutManager(linearLayoutManager);
        simpleForecastAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                YunForecastItem baseForecast = (YunForecastItem) adapter.getItem(position);
                if(baseForecast == null){
                    showNotice("数据异常,请稍后尝试");
                }else{
                    simpleForecastAdapter.setSelected(baseForecast);
                    updateForecastItemData(baseForecast);
                }
            }
        });
        forecastRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dx > 0 && !isSlip){
                    isSlip = true;
                    Log.e("ijimu","forecast slip : "+dx);
                }
            }
        });
        typeface = Typeface.createFromAsset(getAssets(),"fonts/pf_tc_tiny.ttf");
        initBg(bgGroup);
        initData();
        isSlip = false;
        stayTime = System.currentTimeMillis();
        logInfo("user visible hint");
    }

    private void initData(){
        YunWeather cityWeather = cityManager.getCurrentCityWeather();
        if(cityWeather == null){
            showNotice("数据加载错误");
            finish();
            return;
        }
        updateBaseData(cityWeather);
        updateSimpleForecastData(cityWeather);
    }

    private void updateBaseData(YunWeather cityWeather){
        tvCityName.setText(cityWeather.getCityName());
        if(cityWeather.isLocate()){
            String detailsAddress = AddressUtils.detailsAddress(cityWeather.getAddress(),cityWeather.getCity());
            tvCityName.setText(detailsAddress);
            tvCityName.setSelected(true);
            imgLocation.setVisibility(View.VISIBLE);
        }else{
            tvCityName.setText(cityWeather.getCityName());
            imgLocation.setVisibility(View.INVISIBLE);
        }
    }

    private void setTodayForecast(List<YunForecastItem> baseForecastList){
        String time = getIntent().getStringExtra("time");
        if(StringUtils.isEmpty(time)){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
            time = dateFormat.format(new Date());
        }
        for(int i = 0; i < baseForecastList.size();i++){
            YunForecastItem baseForecast = baseForecastList.get(i);
            if(baseForecast.getDate().equals(time)){
                simpleForecastAdapter.setSelected(baseForecast);
                updateForecastItemData(baseForecast);
                int index = Math.min(Math.max(i-1,0),baseForecastList.size()-1);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) forecastRecyclerView.getLayoutManager();
                if(linearLayoutManager!= null)linearLayoutManager.scrollToPositionWithOffset(index,0);
                return;
            }
        }
    }

    private void updateSimpleForecastData(YunWeather cityWeather){
        if(cityWeather.getForecast() != null &&
            cityWeather.getForecast().getData() != null &&
            cityWeather.getForecast().getData().size() > 0){
            List<YunForecastItem> forecastList = cityWeather.getForecast().getData();
            simpleForecastAdapter.setNewData(forecastList);
            setTodayForecast(forecastList);
        }
    }

    private void updateForecastItemData(YunForecastItem baseForecast){
        tvCondition.setText(baseForecast.getWea());
        tvTempMaxMin.setText(baseForecast.getTem1()+"°/"+baseForecast.getTem2()+"°");
        tvTempMaxMin.setTypeface(typeface);
        updateDetailsItem(itemDayWind,"白天风级",baseForecast.getWin_day());
        updateDetailsItem(itemNightWind,"夜间风级",baseForecast.getWin_night());
        YunWeather cityWeather = cityManager.getCurrentCityWeather();
        String date = baseForecast.getDate();
        boolean visible = cityWeather.getCondition()!=null && cityWeather.getCondition().getDate().equals(date);
        updateTodayLiveIndex(visible);
    }


    private void updateDetailsItem(View root,String title,String content){
        TextView tvTitle = root.findViewById(R.id.item_title_label);
        tvTitle.setText(title);
        TextView tvContent = root.findViewById(R.id.item_content);
        tvContent.setText(StringUtils.isEmpty(content)?"--":content);
    }

    private void updateTodayLiveIndex(boolean visible){
        YunWeather cityWeather = cityManager.getCurrentCityWeather();
        groupLiveIndex.setVisibility(visible?View.VISIBLE:View.GONE);
        if(visible){
            List<AdLiveItem> adLiveItemList = new LinkedList<>();
            YunLiveIndex yunLiveIndex = cityWeather.getCondition().getZhishu();
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
            LiveIndexAdapter liveIndexAdapter = new LiveIndexAdapter(getContext(),LiveIndexAdapter.ACTIVITY, R.layout.item_live_index_2);
            liveIndexRecyclerView.setAdapter(liveIndexAdapter);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),4);
            liveIndexRecyclerView.setLayoutManager(gridLayoutManager);
            liveIndexAdapter.setNewData(adLiveItemList);
        }
    }
    private boolean canShowLiveIndexItem(YunLiveIndexItem indexItem){
        if(indexItem == null)return false;
        return !StringUtils.isEmpty(indexItem.getLevel())&&!StringUtils.isEmpty(indexItem.getTips());
    }

    private AdLiveItem createAdLiveItem(int type,String content,String label){
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
}
