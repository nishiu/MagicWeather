package com.frostsowner.magic.weather.activity;

import static com.frostsowner.magic.weather.event.EventType.CITY_UPDATE;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.baidu.location.BDLocation;
import com.fm.commons.logic.BeanFactory;
import com.fm.commons.util.StringUtils;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.adapter.CitySearchAdapter;
import com.frostsowner.magic.weather.base.BaseActivity;
import com.frostsowner.magic.weather.event.ActionEvent;
import com.frostsowner.magic.weather.logic.CityManager;
import com.frostsowner.magic.weather.logic.WeatherLocationClient;
import com.frostsowner.magic.weather.logic.YunCityIdManager;
import com.frostsowner.magic.weather.service.WeatherService;
import com.jaeger.library.StatusBarUtil;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class CityPickActivity extends BaseActivity{

    private final int REQUEST_CODE = 101;

    private final String[] hotCity = {
            "北京","成都","重庆","天津","杭州","深圳","武汉","广州","上海","南京"
    };

    private final String[] cityZhi = {
            "北京","天津","澳门","上海","重庆"
    };

    @BindView(R.id.status_bar)
    LinearLayout statusBarView;
    @BindView(R.id.tv_gps_city)
    TextView tvGpsCity;
    @BindView(R.id.province_list)
    TagContainerLayout provinceListView;
    @BindView(R.id.hot_city_list)
    TagContainerLayout hotCityListView;
    @BindView(R.id.et_search)
    AutoCompleteTextView etSearch;

    private CitySearchAdapter citySearchAdapter;
    private CityManager cityManager;
    private YunCityIdManager yunCityIdManager;
    private List<String> cityZhiList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_pick);
        StatusBarUtil.setTransparentForImageView(this,null);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)statusBarView.getLayoutParams();
        layoutParams.height = getStatusBarHeight(this);
        statusBarView.setLayoutParams(layoutParams);
        cityManager = BeanFactory.getBean(CityManager.class);
        yunCityIdManager = BeanFactory.getBean(YunCityIdManager.class);
        initView();
        initData();
    }

    private void initView(){
        cityZhiList = Arrays.asList(cityZhi);
        etSearch.setOnItemClickListener((parent, view, position, id) -> {
            String cityName = (String)parent.getAdapter().getItem(position);
            select(cityName);
        });
        citySearchAdapter = new CitySearchAdapter(this);
        etSearch.setAdapter(citySearchAdapter);
        WeatherLocationClient.get().locate(new WeatherLocationClient.BDLocationCallBack(){
            @Override
            public void locate(BDLocation bdLocation) {
                StringBuffer buffer = new StringBuffer();
                if(bdLocation == null){
                    buffer.append("定位失败");
                }else{
                    buffer.append(bdLocation.getCity());
                    buffer.append(bdLocation.getDistrict());
                }
                tvGpsCity.setText(buffer.toString());
            }

            @Override
            public void failed(){
                tvGpsCity.setText("定位失败");
            }
        });
        hotCityListView.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                select(text);
            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onSelectedTagDrag(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });
        provinceListView.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String province) {
                if(cityZhiList.contains(province)){
                    Intent intent = new Intent(getContext(),CityPickThirdActivity.class);
                    intent.putExtra("province",province);
                    intent.putExtra("city",province);
                    startActivityForResult(intent,REQUEST_CODE);
                }else{
                    Intent intent = new Intent(getContext(),CityPickSecondActivity.class);
                    intent.putExtra("province",province);
                    startActivityForResult(intent,REQUEST_CODE);
                }
            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onSelectedTagDrag(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });
    }

    private void initData(){
        hotCityListView.setTags(Arrays.asList(hotCity));
        provinceListView.setTags(yunCityIdManager.getProvinceList());
    }

    private void select(String cityName){
        logInfo("select cityName : "+cityName);
        if(cityManager.isEmpty(cityName)){
            cityManager.addData(cityName);
            Intent intent = new Intent(getContext(), WeatherService.class);
            intent.putExtra("cityName",cityName);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                startForegroundService(intent);
            }else{
                startService(intent);
            }
            ActionEvent actionEvent = new ActionEvent(CITY_UPDATE);
            actionEvent.setAttr("cityName",cityName);
            fire(actionEvent);
            finish();
        }else{
            showNotice("已存在该城市,无需添加");
        }
    }

    @OnClick({R.id.btn_search_cancel, R.id.tv_gps_city})
    public void onViewClick(View v){
        switch (v.getId()){
            case R.id.btn_search_cancel:
                etSearch.setText("");
                break;
            case R.id.tv_gps_city:
                if(!TextUtils.equals(tvGpsCity.getText(),"定位失败") &&
                !TextUtils.equals(tvGpsCity.getText(),getString(R.string.location_city_lodding))){
                    select(tvGpsCity.getText().toString());
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){
            String cityName = data.getStringExtra("cityName");
            if(!StringUtils.isEmpty(cityName)){
                select(cityName);
            }
        }
    }
}
