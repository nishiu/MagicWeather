package com.frostsowner.magic.weather.activity;

import static com.frostsowner.magic.weather.Constant.CHANNEL;
import static com.frostsowner.magic.weather.Constant.HOME_HAS_DEAD;
import static com.frostsowner.magic.weather.Constant.NEED_REQUEST_AD_FLAG;
import static com.frostsowner.magic.weather.Constant.UM_AD_FLAG_KEY;
import static com.frostsowner.magic.weather.event.EventType.CHECK_UPGRADE;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.evernote.android.job.JobConfig;
import com.evernote.android.job.JobManager;
import com.fm.commons.logic.BeanFactory;
import com.fm.commons.logic.LocalStorage;
import com.frostsowner.magic.weather.Constant;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.UMConfig;
import com.frostsowner.magic.weather.base.BaseActivity;
import com.frostsowner.magic.weather.dialog.PrivacyDialog;
import com.frostsowner.magic.weather.domain.weather.YunWeather;
import com.frostsowner.magic.weather.event.ActionEvent;
import com.frostsowner.magic.weather.event.EventType;
import com.frostsowner.magic.weather.logic.CityManager;
import com.frostsowner.magic.weather.logic.CrashHandler;
import com.frostsowner.magic.weather.logic.LogicModule;
import com.frostsowner.magic.weather.service.NotificationHandleService;
import com.frostsowner.magic.weather.service.WeatherService;
import com.frostsowner.magic.weather.service.WidgetHandleService;
import com.frostsowner.magic.weather.utils.ConnectionUtils;
import com.frostsowner.magic.weather.utils.StringUtils;
import com.frostsowner.magic.weather.work.DemonsCreator;
import com.umeng.cconfig.UMRemoteConfig;

public class SplashActivity extends BaseActivity {

    private LocalStorage localStorage;
    private CityManager cityManager;

    private boolean showGuide;

    private PrivacyDialog privacyDialog;

    public boolean canJump = false;
    private boolean isLifeShowAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (!this.isTaskRoot()) { // 判断当前activity是不是所在任务栈的根
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    Constant.SHOW_SPLASH_AD = false;
                    finish();
                    return;
                }
            }
        }
        setContentView(R.layout.activity_splash);
        localStorage = BeanFactory.getBean(LocalStorage.class);
        cityManager = BeanFactory.getBean(CityManager.class);
        isLifeShowAd = getIntent().getBooleanExtra("isLifeShowAd",false);
        logger.info("init life show ad from onCreate, isLifeShowAd = "+isLifeShowAd);
        permissionPrepare();
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        isLifeShowAd = getIntent().getBooleanExtra("isLifeShowAd",false);
        logger.info("init life show ad from onNewIntent, isLifeShowAd = "+isLifeShowAd);
        permissionPrepare();
    }

    @Override
    protected void permissionPrepare(){
//        super.permissionPrepare();
        Constant.SHOW_SPLASH_AD = true;
        initViews();
    }

    private void initViews(){
        showGuide = !isProperty("privacy");
        if(showGuide){
            if(privacyDialog == null){
                privacyDialog = new PrivacyDialog(this, v -> {
                    setProperty("privacy","true");
                    UMConfig.init(getApplication());
                    CrashHandler.getInstance().init(this);
                    LogicModule.init();
                    JobManager.create(this).addJobCreator(new DemonsCreator());
                    JobConfig.setLogcatEnabled(true);
                    initRelaxFlag();
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.exit(0);
                    }
                });
            }
            if(!privacyDialog.isShowing()){
                privacyDialog.show();
                logInfo("privacyDialog show");
            }
        }else{
            UMConfig.init(getApplication());
            initRelaxFlag();
        }
    }

    private void initRelaxFlag(){
        if(localStorage == null)localStorage = BeanFactory.getBean(LocalStorage.class);
        boolean isNeedRequestAdFlag = localStorage.get(NEED_REQUEST_AD_FLAG,true);
//        loginFromServer();
        if(!isNeedRequestAdFlag){
            logInfo("no need request ad flag");
            Constant.SHOW_AD = true;
            execute();
            return;
        }
        if(!ConnectionUtils.isConnectionAvailable(this)){
            execute();
            return;
        }
        String adFlag = UMRemoteConfig.getInstance().getConfigValue(UM_AD_FLAG_KEY);
        Log.d("ijimu","splash um remote config value : "+adFlag+" , channel : "+CHANNEL);
        if(StringUtils.isEmpty(adFlag)){
            Constant.SHOW_AD = false;
            execute();
        }else{
            Constant.SHOW_AD = TextUtils.equals("开",adFlag);
            execute();
        }
    }


    private void execute(){
        toMainView();
    }

    @Override
    protected void onPause(){
        super.onPause();
        canJump = false;
    }

    @Override
    protected void onResume(){
        super.onResume();
        toMainView();
    }

    public void toMainView(){
        if (canJump) {
            if(!isLifeShowAd||HOME_HAS_DEAD){
                startActivity(new Intent(this, HomeTabActivity.class));
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(getContext(), NotificationHandleService.class));
                    startForegroundService(new Intent(getContext(), WeatherService.class));
                    startForegroundService(new Intent(getContext(), WidgetHandleService.class));
                }else{
                    startService(new Intent(getContext(), NotificationHandleService.class));
                    startService(new Intent(getContext(), WeatherService.class));
                    startService(new Intent(getContext(), WidgetHandleService.class));
                }
            }else{
                YunWeather cityWeather = cityManager.getCurrentCityWeather();
                ActionEvent event = new ActionEvent(EventType.WEATHER_REQUEST_SUCCESS);
                if(cityWeather != null && !StringUtils.isEmpty(cityWeather.getCityName())){
                    event.setAttr("cityName",cityWeather.getCityName());
                    event.setAttr("fake","fake");
                }
                event.setAttr("reset","reset");
                fire(event);
            }
            this.finish();
        } else {
            canJump = true;
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(privacyDialog!=null&&privacyDialog.isShowing()){
            privacyDialog.dismiss();
            privacyDialog = null;
        }
        Constant.SHOW_SPLASH_AD = false;
        fire(new ActionEvent(CHECK_UPGRADE));
    }
}
