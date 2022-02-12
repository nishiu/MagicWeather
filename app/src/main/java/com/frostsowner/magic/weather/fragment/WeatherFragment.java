package com.frostsowner.magic.weather.fragment;

import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.fm.commons.logic.BeanFactory;
import com.fm.commons.logic.LocalStorage;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.activity.CityMgrActivity;
import com.frostsowner.magic.weather.adapter.CityWeatherPagerAdapter;
import com.frostsowner.magic.weather.domain.weather.YunWeather;
import com.frostsowner.magic.weather.event.ActionEvent;
import com.frostsowner.magic.weather.event.EventType;
import com.frostsowner.magic.weather.impl.RefreshStatusListener;
import com.frostsowner.magic.weather.logic.CityManager;
import com.frostsowner.magic.weather.utils.AddressUtils;
import com.frostsowner.magic.weather.utils.LocationUtils;
import com.frostsowner.magic.weather.utils.StringUtils;
import com.frostsowner.magic.weather.utils.SubscribeUtils;
import com.frostsowner.magic.weather.view.NoSwipeViewPager;
import com.frostsowner.magic.weather.widget.PageBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action0;

public class WeatherFragment extends BgFragment {

    @BindView(R.id.status_bar)
    LinearLayout statusBarView;
    @BindView(R.id.title_bg)
    LinearLayout titleBg;
    @BindView(R.id.title_group)
    View titleGroup;
    @BindView(R.id.news_group)
    View newsGroup;
    @BindView(R.id.label_location_alert)
    LinearLayout labelLocationAlert;
    @BindView(R.id.bg_group)
    ViewGroup bgGroup;
    @BindView(R.id.group_location)
    View groupLocation;

    @BindView(R.id.group_refresh)
    View groupRefresh;
    @BindView(R.id.tv_city_name)
    TextView tvCityName;
    @BindView(R.id.city_page_bar)
    LinearLayout cityPageBarView;
    @BindView(R.id.img_location)
    ImageView imgLocation;

    @BindView(R.id.city_weather_pager)
    NoSwipeViewPager cityWeatherPager;

    private CityManager cityManager;
    private LocalStorage localStorage;

    private CityWeatherPagerAdapter cityWeatherPagerAdapter;
    private PageBar cityPageBar;

    private boolean isVisibleToUser = false;

    @Override
    protected void initWidget(View root){
        super.initWidget(root);
        cityManager = BeanFactory.getBean(CityManager.class);
        localStorage = BeanFactory.getBean(LocalStorage.class);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)statusBarView.getLayoutParams();
        layoutParams.height = getStatusBarHeight(getContext());
        statusBarView.setLayoutParams(layoutParams);
        initBg(bgGroup);
        cityWeatherPagerAdapter = new CityWeatherPagerAdapter(getChildFragmentManager());
        cityWeatherPager.setAdapter(cityWeatherPagerAdapter);
        cityWeatherPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateCityWeatherData(position,false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        cityWeatherPager.setScrollFlag(true);
        updateCityWeatherData(0,false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainWeatherEvent(ActionEvent event){
        if(event.getType() == EventType.WEATHER_REQUEST_SUCCESS){
            String cityName = event.getAttrStr("cityName");
            int currentIndex = cityManager.getTargetIndex(cityName);
            updateCityWeatherData(currentIndex,false);
//            showRefreshStatusView(RefreshStatusListener.REFRESH_SUCCESS);
            showRefreshStatusView(RefreshStatusListener.REFRESHING);
            addSubscription(SubscribeUtils.doOnUIThreadDelayed(new Action0() {
                @Override
                public void call() {
                    showRefreshStatusView(RefreshStatusListener.REFRESH_SUCCESS);
                }
            },500));
        }
        else if(event.getType() == EventType.CITY_UPDATE){
            String cityName = event.getAttrStr("cityName");
            int index = cityManager.getTargetIndex(cityName);
            if(StringUtils.isEmpty(cityName)){
                index = 0;
            }
            updateCityWeatherData(index,false);
        }
        else if(event.getType() == EventType.CHANGE_CURRENT_CITY){
            String cityName = event.getAttrStr("cityName");
            int position = cityManager.getTargetIndex(cityName);
            if(cityWeatherPager.getCurrentItem() != position){
                cityWeatherPager.setCurrentItem(position,true);
                changeBg(cityManager.getTarget(cityName));
            }
        }
        else if(event.getType() == EventType.WEATHER_VIEW_SCROLLING){
            float alpha = event.getAttrFloat("alpha");
            titleBg.setAlpha(alpha);
            statusBarView.setAlpha(alpha);
            float specialAlpha = 1 - alpha;
            if(specialAlpha <= 0.6f)specialAlpha=0;
            groupLocation.setAlpha(specialAlpha);
        }
        else if(event.getType() == EventType.WEATHER_REQUEST_FAILED){
            showRefreshStatusView(RefreshStatusListener.REFRESH_FAILED);
        }
        else if(event.getType() == EventType.WEATHER_REFRESHING){
            showRefreshStatusView(RefreshStatusListener.REFRESHING);
        }
        else if(event.getType() == EventType.WIDGET_ENTRY){
            logInfo("widget entry");
            updateCityWeatherData(0,false);
        }
    }

    private void showLocationAlertPopup(){
        if(labelLocationAlert== null)return;
        YunWeather cityWeather = cityManager.getCurrentCityWeather();
        if(cityWeather != null && cityWeather.isLocate()){
            labelLocationAlert.setVisibility(View.INVISIBLE);
            return;
        }
        if(!isVisibleToUser)return;
        if(LocationUtils.isLocServiceEnable(getContext())){
            labelLocationAlert.setVisibility(View.INVISIBLE);
        }else{
            labelLocationAlert.setVisibility(View.VISIBLE);
        }
    }

    private Subscription failedSubscription;
    private Subscription successSubscription;

    private void showRefreshStatusView(int status){
        ProgressBar progressBar = groupRefresh.findViewById(R.id.progress_bar);
        TextView tvProgress = groupRefresh.findViewById(R.id.tv_progress);
        labelLocationAlert.setVisibility(View.INVISIBLE);
        switch (status){
            case RefreshStatusListener.REFRESHING:
                progressBar.setVisibility(View.VISIBLE);
                tvProgress.setText("正在加载");
                groupRefresh.setVisibility(View.VISIBLE);
                break;
            case RefreshStatusListener.REFRESH_SUCCESS:
                if(successSubscription != null || failedSubscription != null)return;
                progressBar.setVisibility(View.GONE);
                tvProgress.setText("更新成功,刚刚发布");
                groupRefresh.setVisibility(View.VISIBLE);
                successSubscription = SubscribeUtils.doOnUIThreadDelayed(new Action0() {
                    @Override
                    public void call() {
                        if(groupRefresh!=null)groupRefresh.setVisibility(View.INVISIBLE);
                        showLocationAlertPopup();
                        if(successSubscription != null){
                            successSubscription.unsubscribe();
                            successSubscription = null;
                        }
                    }
                },1000);
                break;
            case RefreshStatusListener.REFRESH_FAILED:
                if(successSubscription != null || failedSubscription != null)return;
                progressBar.setVisibility(View.GONE);
                tvProgress.setText("更新失败");
                groupRefresh.setVisibility(View.VISIBLE);
                failedSubscription = SubscribeUtils.doOnUIThreadDelayed(() -> {
                    if(groupRefresh!=null)groupRefresh.setVisibility(View.INVISIBLE);
                    showLocationAlertPopup();
                    if(failedSubscription != null){
                        failedSubscription.unsubscribe();
                        failedSubscription = null;
                    }
                },1000);
                break;
        }
    }

    private void updateCityWeatherData(int position,boolean forceReload){
        List<YunWeather> cityWeatherList = cityManager.getCityWeatherList();
        if(cityWeatherList == null|| cityWeatherList.size() == 0)return;
        if(cityWeatherPagerAdapter == null || cityWeatherPagerAdapter.getCount() <= 0 || cityWeatherList.size() != cityWeatherPagerAdapter.getCount() || forceReload){
            List<Fragment> cityWeatherFragments = new LinkedList<>();
            for(YunWeather cityWeather:cityWeatherList){
                logInfo("updateCityWeatherData cityName : "+cityWeather.getCityName());
                cityWeatherFragments.add(CityWeatherFragment.newInstance(cityWeather.getCityName()));
            }
            cityWeatherPagerAdapter = new CityWeatherPagerAdapter(getChildFragmentManager());
            cityWeatherPagerAdapter.setFragments(cityWeatherFragments);
            cityWeatherPager.setOffscreenPageLimit(cityWeatherFragments.size());
            cityWeatherPager.setAdapter(cityWeatherPagerAdapter);
            cityPageBar = new PageBar(getContext());
            cityPageBar.setBgResId(R.drawable.bg_city_page_default, R.drawable.bg_city_page_selected);
            cityPageBarView.removeAllViews();
            cityPageBar.setView(cityPageBarView);
            cityPageBar.setPageCount(cityWeatherPagerAdapter.getCount());
        }
        YunWeather cityWeather = cityManager.getTarget(position);
        if(cityWeather != null){
            if(cityWeather.isLocate()){
                String detailsAddress = AddressUtils.detailsAddress(cityWeather.getAddress(),cityWeather.getCity());
                tvCityName.setText(detailsAddress);
                tvCityName.setSelected(true);
                imgLocation.setVisibility(View.VISIBLE);
                if(LocationUtils.isLocServiceEnable(getContext())){
                    labelLocationAlert.setVisibility(View.INVISIBLE);
                }else{
                    labelLocationAlert.setVisibility(View.VISIBLE);
                }
//            cacheLocate = true;
            }else{
                tvCityName.setText(cityWeather.getCityName());
                imgLocation.setVisibility(View.INVISIBLE);
                labelLocationAlert.setVisibility(View.INVISIBLE);
//            cacheLocate = false;
            }
        }
        cityPageBar.setPageIndex(position);
        cityWeatherPager.setCurrentItem(position,true);
        cityWeatherPagerAdapter.setCurrentPosition(position);
        cityManager.setCurrentCityIndex(position);
        changeBg(cityWeather);
    }


    @Override
    public void onResume() {
        super.onResume();
        isVisibleToUser = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isVisibleToUser = false;
    }

    @OnClick({R.id.btn_city_add, R.id.btn_back_weather,
              R.id.label_location_alert, R.id.tv_city_name, R.id.btn_news_type})
    public void onFunctionClick(View v){
        switch (v.getId()){
            case R.id.tv_city_name:
            case R.id.btn_city_add:
                startActivity(new Intent(getContext(), CityMgrActivity.class));
                break;
            case R.id.label_location_alert:
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                ActionEvent event = new ActionEvent(EventType.LOCATION_START);
                fire(event);
                break;
        }
    }

    @Override
    protected int getLayout(){
        return R.layout.fragment_weather;
    }
}
