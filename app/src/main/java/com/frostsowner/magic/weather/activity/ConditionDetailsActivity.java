package com.frostsowner.magic.weather.activity;


import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.fm.commons.logic.BeanFactory;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.adapter.ConditionDetailsPagerAdapter;
import com.frostsowner.magic.weather.domain.weather.YunWeather;
import com.frostsowner.magic.weather.logic.CityManager;
import com.frostsowner.magic.weather.utils.AddressUtils;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class ConditionDetailsActivity extends BgActivity {

    @BindView(R.id.status_bar)
    LinearLayout statusBarView;

    @BindView(R.id.img_location)
    ImageView imgLocation;
    @BindView(R.id.tv_city_name)
    TextView tvCityName;
    @BindView(R.id.bg_group)
    ViewGroup bgGroup;

    private ConditionDetailsPagerAdapter detailsPagerAdapter;
    private CityManager cityManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition_details);
        StatusBarUtil.setTransparentForImageView(this,null);
        StatusBarUtil.setDarkMode(this);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)statusBarView.getLayoutParams();
        layoutParams.height = getStatusBarHeight(this);
        cityManager = BeanFactory.getBean(CityManager.class);
        detailsPagerAdapter = new ConditionDetailsPagerAdapter(getSupportFragmentManager(),this);
        initBg(bgGroup);
        initData();
    }

    private void initData(){
        YunWeather cityWeather = cityManager.getCurrentCityWeather();
        if(cityWeather == null){
            showNotice("加载异常");
            finish();
            return;
        }
        if(cityWeather.isLocate()){
            String detailsAddress = AddressUtils.detailsAddress(cityWeather.getAddress(),cityWeather.getCity());
            tvCityName.setText(detailsAddress);
        }else{
            tvCityName.setText(cityWeather.getCityName());
        }
        imgLocation.setVisibility(cityWeather.isLocate()? View.VISIBLE:View.INVISIBLE);
        changeBg(cityWeather);
    }

    @OnClick(R.id.btn_more)
    public void onViewClick(){
//        YunWeather cityWeather = cityManager.getCurrentCityWeather();
//        if (cityWeather == null)return;
//        Intent intent = new Intent();
//        intent.putExtra("currentWeather",cityWeather);
//        intent.setClass(getContext(), ShareActivity.class);
//        startActivity(intent);
    }
}
