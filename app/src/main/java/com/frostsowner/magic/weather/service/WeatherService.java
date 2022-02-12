package com.frostsowner.magic.weather.service;

import static com.frostsowner.magic.weather.Constant.ACTION_WIDGET;
import static com.frostsowner.magic.weather.Constant.CONDITION_UPDATE_INTERVAL;
import static com.frostsowner.magic.weather.Constant.SHORT_FORECAST_UPDATE_INTERVAL;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.fm.commons.logic.BeanFactory;
import com.frostsowner.magic.weather.Constant;
import com.frostsowner.magic.weather.TrackPlugin;
import com.frostsowner.magic.weather.domain.weather.YunCondition;
import com.frostsowner.magic.weather.domain.weather.YunForecast;
import com.frostsowner.magic.weather.domain.weather.YunWeather;
import com.frostsowner.magic.weather.event.ActionEvent;
import com.frostsowner.magic.weather.event.EventType;
import com.frostsowner.magic.weather.logic.CityManager;
import com.frostsowner.magic.weather.logic.CrashHandler;
import com.frostsowner.magic.weather.logic.WeatherLocationClient;
import com.frostsowner.magic.weather.net.WeatherRequestManager;
import com.frostsowner.magic.weather.utils.ResultUtil;
import com.frostsowner.magic.weather.utils.StringUtils;
import com.frostsowner.magic.weather.utils.SubscribeUtils;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WeatherService extends BaseWeatherService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private WeatherRequestManager requestManager;
    private CityManager cityManager;

    private boolean isRequesting = false;
    private PowerManager pm;

    @Override
    public void onCreate() {
        super.onCreate();
        requestManager = BeanFactory.getBean(WeatherRequestManager.class);
        cityManager = BeanFactory.getBean(CityManager.class);
        pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground();
        if(intent != null){
            String cityName = intent.getStringExtra("cityName");
            String action = intent.getStringExtra("notification");
            String widget = intent.getStringExtra("action");
            logInfo("get widget action : "+widget);
            boolean showNoticeAlert = TextUtils.equals(widget,ACTION_WIDGET);
            if(StringUtils.isEmpty(cityName)){
                location(showNoticeAlert);
            }else{
                YunWeather cityWeather = cityManager.getTarget(cityName);
                if(cityWeather == null){
                    cityWeather = cityManager.addData(cityName);
                    requestMJData(cityWeather,showNoticeAlert);
                }else{
                    if(cityWeather.isLocate()){
                        location(showNoticeAlert);
                    }else{
                        requestMJData(cityWeather,showNoticeAlert);
                    }
                }
            }
        }
        addSubscription(SubscribeUtils.doOnUIThreadDelayed(new Action0(){
            @Override
            public void call() {
                isRequesting = false;
            }
        },1000*30));
        return super.onStartCommand(intent, flags, startId);
    }

    private void location(boolean showNoticeAlert){
        WeatherLocationClient.get().locate(new WeatherLocationClient.BDLocationCallBack(){
            @Override
            public void locate(BDLocation bdLocation){
                if(bdLocation == null|| Constant.isEmulator){
                    YunWeather cityWeather = cityManager.getTarget(0);
                    if(cityWeather == null){
                        cityWeather = new YunWeather();
                        cityWeather.setCity("北京");
                        cityWeather.setLocate(true);
                        cityManager.addData("北京",true);
                    }
                    requestMJData(cityWeather,showNoticeAlert);
                    return;
                }
                String city = bdLocation.getCity();
                String district = bdLocation.getDistrict();
                String address = bdLocation.getAddrStr();
                String semaAptag = bdLocation.getSemaAptag();
                logInfo("locate log cityName : "+city+district);
                YunWeather cityWeather = cityManager.getTarget(city+district);
                if(cityWeather == null){
                    if(StringUtils.isEmpty(city)){
                        cityWeather = cityManager.addData("北京",true);
                    }else{
                        cityWeather = cityManager.addData(city,district,address,semaAptag,true);
                    }
                }else{
                    if(cityWeather.getCity().equals(city)&&cityWeather.getDistrict().equals(district)){
                        cityWeather.setCity(city);
                        cityWeather.setDistrict(district);
                        cityWeather.setAddress(address);
                        cityWeather.setSemaAptag(semaAptag);
                    }else{
                        cityWeather = cityManager.addData(city,district,address,semaAptag,true);
                    }
                }
                requestMJData(cityWeather,showNoticeAlert);
            }

            @Override
            public void failed(){
                YunWeather cityWeather = cityManager.getTarget(0);
                if(cityWeather == null){
                    cityWeather = cityManager.addData("北京",true);
                }
                ActionEvent actionEvent = new ActionEvent(EventType.WEATHER_REQUEST_FAILED);
                EventBus.getDefault().post(actionEvent);
                requestMJData(cityWeather,showNoticeAlert);
            }
        });
    }

    protected void requestMJData(YunWeather cityWeather,final boolean showNoticeAlert){
        if(isRequesting){
            logger.info("正在更新,无需重复请求");
            return;
        }
        isRequesting = true;
        logInfo("请求天气数据 : "+cityWeather.getCity()+" , "+cityWeather.getDistrict()+" , "+cityWeather.getAddress()+" , cityId : "+cityWeather.getCityId());
        Subscription subscription = Observable.defer(new Func0<Observable<YunWeather>>() {
            @Override
            public Observable<YunWeather> call() {
                return Observable.just(cityWeather);
            }
        })
        .subscribeOn(Schedulers.io())
        .flatMap(new Func1<YunWeather, Observable<YunWeather>>(){
            @Override
            public Observable<YunWeather> call(YunWeather mjWeather) {
                return requestCondition(mjWeather);
            }
        })
        .flatMap(new Func1<YunWeather, Observable<YunWeather>>(){
            @Override
            public Observable<YunWeather> call(YunWeather mjWeather){
                return requestForecast(mjWeather);
            }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<YunWeather>(){
            @Override
            public void call(YunWeather cityData) {
                cityManager.updateData(cityData);
                updateWeatherNotification();
                if(pm != null && pm.isInteractive()){
                    ActionEvent actionEvent = new ActionEvent(EventType.WEATHER_REQUEST_SUCCESS);
                    actionEvent.setAttr("cityName", cityWeather.getCityName());
                    actionEvent.setAttr("action",showNoticeAlert);
                    EventBus.getDefault().post(actionEvent);
                    logger.info("刷新天气");
                }
                logger.info("成功、 天气");
                isRequesting = false;
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                ActionEvent actionEvent = new ActionEvent(EventType.WEATHER_REQUEST_FAILED);
                actionEvent.setAttr("cityName", cityWeather.getCityName());
                EventBus.getDefault().post(actionEvent);
                logger.info("失败、天气");
                throwable.printStackTrace();
                CrashHandler.getInstance().handleException(throwable);
                isRequesting = false;
            }
        });
        addSubscription(subscription);
    }

    private Observable<YunWeather> requestCondition(YunWeather cityWeather){
        YunCondition yunCondition = cityWeather.getCondition();
        if(yunCondition !=null && System.currentTimeMillis() - yunCondition.getLastRequestTime() < CONDITION_UPDATE_INTERVAL){
            logger.info("do not need update conditionData data");
            return Observable.just(cityWeather);
        }
        String result = requestManager.getYunYiDaWeather("v62",cityWeather.getCityId());
        logger.info("update condition data :  "+result);
//        logger.info("update conditionData data2 :  "+result2);
        Type type = new TypeToken<YunCondition>(){}.getType();
        YunCondition data = ResultUtil.getDomain(result, type);
        if (data != null) {
            data.setLastRequestTime(System.currentTimeMillis());
            cityWeather.setCondition(data);
            cityWeather.setUpdateTime(System.currentTimeMillis());
            TrackPlugin.track(this,"weatherApi",null);
        }
        return Observable.just(cityWeather);
    }

    private Observable<YunWeather> requestForecast(YunWeather cityWeather){
        YunForecast yunForecast = cityWeather.getForecast();
        if(yunForecast !=null && System.currentTimeMillis() - yunForecast.getLastRequestTime() < SHORT_FORECAST_UPDATE_INTERVAL){
            logger.info("do not need update forecast data");
            return Observable.just(cityWeather);
        }
        String result = requestManager.getYunYiDaWeather("v3",cityWeather.getCityId());
        logger.info("update forecast data : "+result);
        Type type = new TypeToken<YunForecast>(){}.getType();
        YunForecast data = ResultUtil.getDomain(result, type);
        if (data != null) {
            data.setLastRequestTime(System.currentTimeMillis());
            cityWeather.setForecast(data);
            cityWeather.setUpdateTime(System.currentTimeMillis());
            TrackPlugin.track(this,"weatherApi",null);
        }
        return Observable.just(cityWeather);
    }
}
