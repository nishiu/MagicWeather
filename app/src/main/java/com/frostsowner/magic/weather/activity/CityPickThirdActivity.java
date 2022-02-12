package com.frostsowner.magic.weather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.fm.commons.logic.BeanFactory;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.base.BaseActivity;
import com.frostsowner.magic.weather.logic.YunCityIdManager;
import com.frostsowner.magic.weather.utils.StringUtils;
import com.jaeger.library.StatusBarUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class CityPickThirdActivity extends BaseActivity implements TagView.OnTagClickListener {

    private final String[] SCAPE_WORDS = {
            "景区","园区","风景区","保护区","公园","旅游区","动物园","游览区","名胜区","A区","B区","C区","D区","度假区",
            "古镇"
    };

    private final String[] ZONE_WORDS = {
            "市","区","县","镇"
    };

    @BindView(R.id.status_bar)
    LinearLayout statusBarView;

    @BindView(R.id.c_p_header_city_title_tv)
    TextView tvCityTitle;
    @BindView(R.id.city_list)
    TagContainerLayout cityListView;
    @BindView(R.id.scape_list)
    TagContainerLayout scapeListView;
    @BindView(R.id.scape_group)
    View scapeGroup;

    private YunCityIdManager yunCityIdManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_pick_second);
        StatusBarUtil.setTransparentForImageView(this,null);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)statusBarView.getLayoutParams();
        layoutParams.height = getStatusBarHeight(this);
        statusBarView.setLayoutParams(layoutParams);
        yunCityIdManager = BeanFactory.getBean(YunCityIdManager.class);
        initView();
        initData();
    }

    private void initView(){
        scapeGroup.setVisibility(View.VISIBLE);
        cityListView.setOnTagClickListener(this);
        scapeListView.setOnTagClickListener(this);
    }

    private void initData(){
        String province = getIntent().getStringExtra("province");
        String city = getIntent().getStringExtra("city");
        if(StringUtils.isEmpty(province)|| StringUtils.isEmpty(city)){
            showNotice("数据异常");
            setResult(RESULT_CANCELED);
            finish();
        }
        tvCityTitle.setText(city);
        List<String> dataList = yunCityIdManager.getZoneList(province,city);
        List<String> scapeList = new LinkedList<>();
        List<String> zoneList = new LinkedList<>();
        List<String> zoneWords = Arrays.asList(ZONE_WORDS);
        List<String> scapeWords = Arrays.asList(SCAPE_WORDS);
        for(String data : dataList){
            boolean contains = false;
            for(String word1 : zoneWords){
                String temp = data.replaceAll( "[\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]" , "");
                if(temp.contains(word1)&& temp.lastIndexOf(word1)== temp.length()-1){
                    contains = true;
                    for(String word2 : scapeWords){
                        if(data.contains(word2)){
                            contains = false;
                        }
                    }
                }
            }
            if(contains){
                zoneList.add(data);
            }else{
                scapeList.add(data);
            }
        }
        cityListView.setTags(zoneList);
        scapeListView.setTags(scapeList);
    }

    @Override
    public void onTagClick(int position, String city) {
        Intent intent = new Intent();
        intent.putExtra("cityName",city);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onTagLongClick(int position, String text){

    }

    @Override
    public void onSelectedTagDrag(int position, String text) {

    }

    @Override
    public void onTagCrossClick(int position) {

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
