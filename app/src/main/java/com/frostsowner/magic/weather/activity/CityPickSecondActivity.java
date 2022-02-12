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

import java.util.List;

import butterknife.BindView;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class CityPickSecondActivity extends BaseActivity {

    private static final int REQUEST_CODE = 1002;

    @BindView(R.id.status_bar)
    LinearLayout statusBarView;

    @BindView(R.id.city_list)
    TagContainerLayout cityListView;
    @BindView(R.id.c_p_header_city_title_tv)
    TextView tvCityTitle;
    @BindView(R.id.scape_group)
    View scapeGroup;

    private YunCityIdManager yunCityIdManager;

    private String province;

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
        cityListView.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String city) {
                Intent intent = new Intent(getContext(),CityPickThirdActivity.class);
                intent.putExtra("city",city);
                intent.putExtra("province",province);
                startActivityForResult(intent,REQUEST_CODE);
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
        scapeGroup.setVisibility(View.INVISIBLE);
    }

    private void initData(){
        province = getIntent().getStringExtra("province");
        if(StringUtils.isEmpty(province)){
            showNotice("数据异常");
            setResult(RESULT_CANCELED);
            finish();
        }
        tvCityTitle.setText(province);
        List<String> cityList = yunCityIdManager.getCityList(province);
        cityListView.setTags(cityList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){
            setResult(RESULT_OK,data);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
