package com.frostsowner.magic.weather.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fm.commons.logic.BeanFactory;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.adapter.LiveIndexAdapter;
import com.frostsowner.magic.weather.base.BaseFragment;
import com.frostsowner.magic.weather.domain.AdLiveItem;
import com.frostsowner.magic.weather.domain.weather.YunCondition;
import com.frostsowner.magic.weather.domain.weather.YunLiveIndex;
import com.frostsowner.magic.weather.domain.weather.YunLiveIndexItem;
import com.frostsowner.magic.weather.domain.weather.YunWeather;
import com.frostsowner.magic.weather.logic.CityManager;
import com.frostsowner.magic.weather.logic.SimpleDateManager;
import com.frostsowner.magic.weather.utils.StringUtils;
import com.frostsowner.magic.weather.utils.WeatherResUtils;
import com.frostsowner.magic.weather.utils.WeatherUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;

public class ConditionFragment extends BaseFragment implements OnRefreshListener{

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_update_time)
    TextView tvUpdateTime;
    @BindView(R.id.tv_temp)
    TextView tvTemp;
    @BindView(R.id.tv_condition)
    TextView tvCondition;
    @BindView(R.id.img_condition)
    ImageView imgCondition;
    @BindView(R.id.tv_aqi)
    TextView tvAqi;

    @BindView(R.id.item_real_feel)
    View itemRealFell;
    @BindView(R.id.item_humidity)
    View itemHumidity;
    @BindView(R.id.item_wind_d)
    View itemWindD;
    @BindView(R.id.item_uvi)
    View itemUvi;
    @BindView(R.id.item_pressure)
    View itemPressure;
    @BindView(R.id.item_vis)
    View itemVis;
    @BindView(R.id.live_index_recycler_view)
    RecyclerView liveIndexRecyclerView;

    @BindView(R.id.native_ad_container)
    FrameLayout bannerContainer;

    private CityManager cityManager;
    private Typeface typeface;
    private SimpleDateManager simpleDateManager;

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        cityManager = BeanFactory.getBean(CityManager.class);
        simpleDateManager = BeanFactory.getBean(SimpleDateManager.class);
        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setEnableRefresh(false);
        typeface = Typeface.createFromAsset(getActivity().getAssets(),"fonts/pf_tc_tiny.ttf");
        updateDetailsData();
    }

    private void updateDetailsData(){
        YunWeather cityWeather = cityManager.getCurrentCityWeather();
        if(cityWeather == null){
            showNotice("数据异常加载失败");
            return;
        }
        updateBaseData(cityWeather);
        updateDetailsData(cityWeather);
        updateLiveIndex(cityWeather);
    }

    private void updateBaseData(YunWeather cityWeather){
        if(cityWeather.getCondition()!= null){
            YunCondition baseCondition = cityWeather.getCondition();
            tvUpdateTime.setText(baseCondition.getDate()+" "+baseCondition.getUpdate_time()+" 发布");
            tvTemp.setText(baseCondition.getTem()+"°");
            tvTemp.setTypeface(typeface);
            tvCondition.setText(baseCondition.getWea());
            tvAqi.setText("空气质量 "+ WeatherUtils.getAqiValue(baseCondition.getAir())+" "+baseCondition.getAir());
            boolean isNight = StringUtils.isNight(System.currentTimeMillis());
            imgCondition.setImageResource(isNight?WeatherResUtils.getYunWeatherNightIcon(baseCondition.getWea_night_img()):WeatherResUtils.getYunWeatherDayIcon(baseCondition.getWea_day_img()));
        }else{
            tvUpdateTime.setText("--");
            tvTemp.setText("--");
            tvCondition.setText("--");
            tvAqi.setText("空气质量 --");
        }
    }

    private void updateDetailsData(YunWeather cityWeather){
        if(cityWeather.getCondition() != null){
            YunCondition baseCondition = cityWeather.getCondition();
            updateDetailsItem(itemRealFell,"体感温度",baseCondition.getTem2()+"°");
            updateDetailsItem(itemHumidity,"湿度",baseCondition.getHumidity());
            updateDetailsItem(itemWindD,"风力风向",baseCondition.getWin()+baseCondition.getWin_speed());
            updateDetailsItem(itemUvi,"PM2.5",baseCondition.getAir_pm25());
            updateDetailsItem(itemPressure,"气压",baseCondition.getPressure()+"hPa");
            updateDetailsItem(itemVis,"能见度",baseCondition.getVisibility());
        }
    }

    private void updateLiveIndex(YunWeather cityWeather){
        if(cityWeather.getCondition() != null && cityWeather.getCondition().getZhishu() != null){
            YunLiveIndex yunLiveIndex = cityWeather.getCondition().getZhishu();
            List<AdLiveItem> adLiveItemList = new LinkedList<>();
            if(canShowLiveIndexItem(yunLiveIndex.getXiche())){
                adLiveItemList.add(createAdLiveItem(AdLiveItem.TYPE_LIVE, yunLiveIndex.getXiche().getLevel(), "洗车"));
            }
            if(canShowLiveIndexItem(yunLiveIndex.getChuanyi())){
                adLiveItemList.add(createAdLiveItem(AdLiveItem.TYPE_LIVE,yunLiveIndex.getChuanyi().getLevel(), "穿衣"));
            }
            if(canShowLiveIndexItem(yunLiveIndex.getZiwaixian())){
                adLiveItemList.add(createAdLiveItem(AdLiveItem.TYPE_LIVE,yunLiveIndex.getZiwaixian().getLevel(), "紫外线"));
            }
            if(canShowLiveIndexItem(yunLiveIndex.getGanmao())){
                adLiveItemList.add(createAdLiveItem(AdLiveItem.TYPE_LIVE,yunLiveIndex.getGanmao().getLevel(), "感冒"));
            }
            if(canShowLiveIndexItem(yunLiveIndex.getChenlian())){
                adLiveItemList.add(createAdLiveItem(AdLiveItem.TYPE_LIVE,yunLiveIndex.getChenlian().getLevel(), "运动"));
            }
            LiveIndexAdapter liveIndexAdapter = new LiveIndexAdapter(getContext(),LiveIndexAdapter.ACTIVITY, R.layout.item_live_index_2);
            liveIndexRecyclerView.setAdapter(liveIndexAdapter);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),4);
            liveIndexRecyclerView.setLayoutManager(gridLayoutManager);
            liveIndexAdapter.setNewData(adLiveItemList);
        }
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

    private boolean canShowLiveIndexItem(YunLiveIndexItem indexItem){
        if(indexItem == null)return false;
        return !StringUtils.isEmpty(indexItem.getLevel())&&!StringUtils.isEmpty(indexItem.getTips());
    }

    private void updateDetailsItem(View root,String title,String content){
        TextView tvTitle = root.findViewById(R.id.item_title_label);
        tvTitle.setText(title);
        TextView tvContent = root.findViewById(R.id.item_content);
        tvContent.setText(StringUtils.isEmpty(content)?"--":content);
    }
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout){
//        addSubscription(SubscribeUtils.doOnUIThreadDelayed(new Action0() {
//            @Override
//            public void call() {
//                refreshLayout.finishRefresh();
//            }
//        },1500));
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_condition;
    }
}
