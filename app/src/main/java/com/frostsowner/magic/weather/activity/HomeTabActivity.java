package com.frostsowner.magic.weather.activity;

import static com.frostsowner.magic.weather.Constant.ACTION_15FORECAST;
import static com.frostsowner.magic.weather.Constant.ACTION_CONDITION;
import static com.frostsowner.magic.weather.Constant.ACTION_HOME;
import static com.frostsowner.magic.weather.Constant.NEED_REQUEST_AD_FLAG;
import static com.frostsowner.magic.weather.Constant.SHOW_AD;
import static com.frostsowner.magic.weather.event.EventType.CHANGE_SECEN_CALENDAR;
import static com.frostsowner.magic.weather.event.EventType.CHANGE_SECEN_WEATHER;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;

import com.fm.commons.logic.BeanFactory;
import com.fm.commons.logic.LocalStorage;
import com.fm.commons.util.StringUtils;
import com.frostsowner.magic.weather.Constant;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.adapter.HomeAdapter;
import com.frostsowner.magic.weather.base.BaseActivity;
import com.frostsowner.magic.weather.event.ActionEvent;
import com.frostsowner.magic.weather.event.EventType;
import com.frostsowner.magic.weather.logic.CityManager;
import com.frostsowner.magic.weather.logic.WeatherConfig;
import com.frostsowner.magic.weather.logic.WeatherItemExposure;
import com.frostsowner.magic.weather.service.WidgetHandleService;
import com.frostsowner.magic.weather.utils.LocationUtils;
import com.frostsowner.magic.weather.utils.NotificationUtils;
import com.frostsowner.magic.weather.utils.SubscribeUtils;
import com.frostsowner.magic.weather.view.NoSwipeViewPager;
import com.frostsowner.magic.weather.work.SignNotificationJob;
import com.jaeger.library.StatusBarUtil;
import com.tencent.bugly.beta.Beta;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import rx.functions.Action0;

public class HomeTabActivity extends BaseActivity {

    private final String[] location_permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private final long BACK_PRESS_DELAYED = 2000;

    @BindView(R.id.view_pager)
    NoSwipeViewPager homeViewPager;

    private HomeAdapter homeAdapter;
    private LocalStorage localStorage;
    private CityManager cityManager;
    private long backPressTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tab);
        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        localStorage = BeanFactory.getBean(LocalStorage.class);
        cityManager = BeanFactory.getBean(CityManager.class);
        boolean isNeedRequestAdFlag = localStorage.get(NEED_REQUEST_AD_FLAG, true);
        if (!isNeedRequestAdFlag && !SHOW_AD){
            SHOW_AD = true;
            Intent intent = new Intent(this, WidgetHandleService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }
        initViews();
        handleAction();
        checkLocationSet();
        Constant.HOME_HAS_DEAD = false;
        if (WeatherConfig.isNotificationSignAlertKey()) {
            SignNotificationJob.schedule();
        }
        if(cityManager.getCityWeatherList() == null || cityManager.getCityWeatherList().size() <= 0){
            startActivity(new Intent(this,CityPickActivity.class));
            return;
        }
        checkUpgrade();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleAction();
        logInfo("onNewIntent");
        Constant.HOME_HAS_DEAD = false;
    }

    private void handleAction() {
        if (getIntent() == null) return;
        String action = getIntent().getStringExtra("action");
        logInfo("home action : " + action);
        if (StringUtils.isEmpty(action)) {
            return;
        }
        if (action.equals(ACTION_CONDITION)) {
            Intent intent = new Intent();
            intent.setClass(this, ConditionDetailsActivity.class);
            startActivity(intent);
        } else if (action.equals(ACTION_15FORECAST)) {
            Intent intent = new Intent();
            intent.setClass(this, ForecastDetailsActivity.class);
            startActivity(intent);
        } else if (action.equals(ACTION_HOME)) {
        }
    }

    private void initViews() {
        homeViewPager.setReMeasure(false);
        homeAdapter = new HomeAdapter(getSupportFragmentManager());
        homeViewPager.setAdapter(homeAdapter);
        homeViewPager.setOffscreenPageLimit(homeAdapter.getCount());
        homeViewPager.setScrollFlag(false);
    }

    private void checkLocationSet() {
        if ((Build.VERSION.SDK_INT < 23 || checkPermissionGranted(location_permissions)) && LocationUtils.isLocServiceEnable(this))
            return;
        boolean locationAlert = isProperty("tt_weather_location_guide_alert");
        if (locationAlert) return;
//        LocationGuideDialog dialog = new LocationGuideDialog(this, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addPermission(location_permissions, 102, new PermissionCallBack() {
//                    @Override
//                    public void result(boolean isGranted, List<String> permission) {
//                        if (!isGranted) {
//                            showNotice("申请失败");
//                        }
//                    }
//                });
//                checkPermissions();
//            }
//        });
//        dialog.show();
        setProperty("tt_weather_location_guide_alert", "true");
    }

    private void checkNotificationSet() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        if (NotificationUtils.isNotificationEnable(this)) return;
        boolean isShow = localStorage.get("tt_weather_notification_set_alert", false);
        if (isShow) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示");
        builder.setMessage("检测到您没有开启通知服务,可能会影响您的天气功能使用,是否跳转至设置页面");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("立即前往", (dialog, which) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent intent = new Intent();
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", getPackageName());
                intent.putExtra("app_uid", getApplicationInfo().uid);
                startActivity(intent);
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        });
        builder.show();
        localStorage.put("tt_weather_notification_set_alert", true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSceneChangeEvent(ActionEvent event) {
        if (event.getType() == CHANGE_SECEN_CALENDAR) {
//            String time = event.getAttrStr("time");
//            Intent intent = new Intent(this, ForecastDetailsActivity.class);
//            intent.putExtra("time", time);
//            startActivity(intent);
            homeViewPager.setCurrentItem(1, false);
        }
        else if (event.getType() == CHANGE_SECEN_WEATHER){
            homeViewPager.setCurrentItem(0, false);
        }
//        else if (event.getType() == CHECK_CALENDAR_PERMISSION) {
//            final boolean isChecked = event.getAttrBoolean("checked");
//            addPermission(CALENDAR_PERMISSION, 102, (isGranted, permissions) -> {
//                ActionEvent permissionEvent = new ActionEvent(GRANTED_CALENDAR_PERMISSION);
//                permissionEvent.setAttr("checked", isChecked);
//                permissionEvent.setAttr("isGranted", (isGranted));
//                fire(permissionEvent);
//            });
//            checkPermissions();
//        }
        else if (event.getType() == EventType.CHECK_UPGRADE){
            checkUpgrade();
        }
    }

    private boolean isChecked = false;

    private void checkUpgrade() {
//        if (isChecked) return;
//        addSubscription(SubscribeUtils.doOnUIThreadDelayed(new Action0() {
//            @Override
//            public void call() {
//                logInfo("检测自动更新");
//                Beta.checkUpgrade(true, false);
//                isChecked = false;
//            }
//        }, 5000));
//        isChecked = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constant.HOME_HAS_DEAD = true;
        BeanFactory.getBean(WeatherItemExposure.class).release();
    }

    @Override
    public void onBackPressed(){
        if (System.currentTimeMillis() - backPressTime > BACK_PRESS_DELAYED) {
            showNotice("请再按一次确认退出");
            backPressTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }
}
