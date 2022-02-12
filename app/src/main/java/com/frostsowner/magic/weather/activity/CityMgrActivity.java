package com.frostsowner.magic.weather.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fm.commons.base.PermissionCallBack;
import com.fm.commons.logic.BeanFactory;
import com.frostsowner.magic.weather.Constant;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.adapter.CityMgrAdapter;
import com.frostsowner.magic.weather.adapter.drag.OnStartDragListener;
import com.frostsowner.magic.weather.adapter.drag.SimpleItemTouchHelperCallback;
import com.frostsowner.magic.weather.base.BaseActivity;
import com.frostsowner.magic.weather.domain.weather.YunWeather;
import com.frostsowner.magic.weather.event.ActionEvent;
import com.frostsowner.magic.weather.event.EventType;
import com.frostsowner.magic.weather.logic.CityManager;
import com.frostsowner.magic.weather.utils.StringUtils;
import com.frostsowner.magic.weather.utils.SubscribeUtils;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CityMgrActivity extends BaseActivity implements OnStartDragListener {

    private final String[] storage_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private final int CITY_DATA_SIZE_MAX = 6;

    @BindView(R.id.status_bar)
    LinearLayout statusBarView;

    @BindView(R.id.city_list_view)
    RecyclerView cityListView;

    @BindView(R.id.btn_back)
    LinearLayout btnBack;
    @BindView(R.id.btn_city_mgr)
    LinearLayout btnCityMgr;
    @BindView(R.id.btn_check)
    LinearLayout btnCheck;
    @BindView(R.id.btn_city_add)
    LinearLayout btnCityAdd;
    @BindView(R.id.banner_container)
    FrameLayout bannerContainer;

    private CityMgrAdapter cityMgrAdapter;
    private CityManager cityManager;
    private ItemTouchHelper itemTouchHelper;
    private SimpleItemTouchHelperCallback simpleItemTouchHelperCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_mgr);
        StatusBarUtil.setTransparentForImageView(this,null);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)statusBarView.getLayoutParams();
        layoutParams.height = getStatusBarHeight(this);
        statusBarView.setLayoutParams(layoutParams);
        cityManager = BeanFactory.getBean(CityManager.class);
        init();
    }

    private void init(){
        cityMgrAdapter = new CityMgrAdapter(this,this);
        cityListView.setAdapter(cityMgrAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        cityListView.setLayoutManager(linearLayoutManager);
        cityMgrAdapter.setNewData(new LinkedList<>(cityManager.getCityWeatherList()));
        simpleItemTouchHelperCallback = new SimpleItemTouchHelperCallback(cityMgrAdapter);
        itemTouchHelper = new ItemTouchHelper(simpleItemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(cityListView);
        simpleItemTouchHelperCallback.setLongPressDragEnabled(false);
        simpleItemTouchHelperCallback.setSwipeEnabled(false);
        cityMgrAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            YunWeather cityWeather = cityManager.getTarget(position);
            if(cityWeather == null)return;
            String cityName = cityWeather.getCityName();
            ActionEvent event = new ActionEvent(EventType.CHANGE_CURRENT_CITY);
            event.setAttr("cityName",cityName);
            EventBus.getDefault().post(event);
            finish();
        });
//        int taskType = getIntent().getIntExtra("taskType",-1);
//        if(taskType == TaskType.AJ_TASK_TYPE_ADD_CITY){
//            ViewAnimator.animate(btnCityAdd)
//                    .startDelay(500)
//                    .duration(1000)
//                    .repeatMode(ViewAnimator.RESTART)
//                    .wave()
//                    .repeatCount(6)
//                    .start();
//        }
        String params = getIntent().getStringExtra("params");
        if(!StringUtils.isEmpty(params)&&params.equals("auto_add")){
            startActivity(new Intent(this,CityPickActivity.class));
        }
        showStoragePermissionAlert();
    }

    private void showStoragePermissionAlert(){
        if(Build.VERSION.SDK_INT < 23 || checkPermissionGranted(storage_permissions))return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("友情提示");
        builder.setMessage("需要手机“存储权限”用于能管理并缓存多个城市天气数据。如拒绝则可能会在未知情况下丢失数据。");
        builder.setPositiveButton("去授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addPermission(storage_permissions, 103, new PermissionCallBack() {
                    @Override
                    public void result(boolean isGranted, List<String> permission) {
                        if(!isGranted){
                            showNotice("权限申请失败");
                        }else{

                        }
                    }
                });
                checkPermissions();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    @OnClick({R.id.btn_city_mgr, R.id.btn_city_add, R.id.btn_check})
    public void onViewClick(View v){
        switch (v.getId()){
            case R.id.btn_check:
                showProgress("正在保存");
                cityMgrAdapter.setManagerStatus(false);
                updateMgrStatus(false);
                cityManager.setCityWeatherList(cityMgrAdapter.getData());
                addSubscription(SubscribeUtils.doOnUIThreadDelayed(() -> {
                    ActionEvent actionEvent = new ActionEvent(EventType.CITY_UPDATE);
                    YunWeather cityWeather = cityManager.getTarget(cityManager.getCityWeatherList().size()-1);
                    if(cityWeather != null){
                        actionEvent.setAttr("cityName",cityWeather.getCityName());
                    }
                    fire(actionEvent);
                    showNotice("更新成功");
                    dismissProgress();
                },1000));
                break;
            case R.id.btn_city_mgr:
                cityMgrAdapter.setManagerStatus(true);
                updateMgrStatus(true);
                break;
            case R.id.btn_city_add:
                if(Constant.isEmulator){
                    showNotice("检测到该设备为模拟器，暂时无法使用该功能");
                    return;
                }
                startActivity(new Intent(this,CityPickActivity.class));
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWeatherEvent(ActionEvent event){
        if(event.getType() == EventType.WEATHER_REQUEST_SUCCESS){
            String cityName = event.getAttrStr("cityName");
            YunWeather cityWeather = cityManager.getTarget(cityName);
            cityMgrAdapter.updateItem(cityWeather);
        }
        else if(event.getType() == EventType.CITY_UPDATE){
//            cityMgrAdapter.setNewData(cityManager.getCityWeatherList());
            finish();
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder){
        itemTouchHelper.startDrag(viewHolder);
    }

    private void updateMgrStatus(boolean isManagerStatus){
        btnCityMgr.setVisibility(isManagerStatus?View.INVISIBLE:View.VISIBLE);
        btnCheck.setVisibility(isManagerStatus?View.VISIBLE:View.INVISIBLE);
        btnCityAdd.setVisibility(isManagerStatus?View.INVISIBLE:View.VISIBLE);
        simpleItemTouchHelperCallback.setSwipeEnabled(isManagerStatus);
    }

    @Override
    public void onBackPressed() {
        if(cityMgrAdapter.getManagerStatus()){
            cityMgrAdapter.setManagerStatus(false);
            updateMgrStatus(false);
            cityMgrAdapter.resetFromCache();
            return;
        }
        super.onBackPressed();
    }
}
