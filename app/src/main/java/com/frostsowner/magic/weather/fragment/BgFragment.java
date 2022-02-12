package com.frostsowner.magic.weather.fragment;

import android.animation.Animator;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.base.BaseFragment;
import com.frostsowner.magic.weather.domain.weather.YunCondition;
import com.frostsowner.magic.weather.domain.weather.YunWeather;
import com.github.matteobattilana.weather.PrecipType;
import com.github.matteobattilana.weather.WeatherView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class BgFragment extends BaseFragment {

    private final int RAIN_S = 100;
    private final int RAIN_M = 101;
    private final int RAIN_L = 102;
    private final int SNOW_S = 103;
    private final int SNOW_L = 104;
    private final int NONE = 999;

    protected ImageView imgBg1;
    protected ImageView imgBg2;
    private WeatherView weatherEffectView;

    protected boolean isReverse;
    protected int index;
    protected boolean isAnimation;
    private SimpleDateFormat simpleDateFormat;
    private Date date;
    private Date nightDate1;
    private Date nightDate2;

    private int weatherRes;

    protected void initBg(ViewGroup bgGroup){
        imgBg1 = bgGroup.findViewById(R.id.img_bg_1);
        imgBg2 = bgGroup.findViewById(R.id.img_bg_2);
        weatherEffectView = bgGroup.findViewById(R.id.weather_effect_view);
        isReverse = false;
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        date = new Date();
        try {
            nightDate1 = simpleDateFormat.parse("18:00:00");
            nightDate2 = simpleDateFormat.parse("6:00:00");
        }catch (ParseException e){
            e.printStackTrace();
        }
        if(isNight()){
            imgBg1.setBackgroundResource(R.drawable.bg_weather_day);
            imgBg2.setBackgroundResource(R.drawable.bg_weather_night);
            weatherRes = R.drawable.bg_weather_night;
        }else{
            imgBg1.setBackgroundResource(R.drawable.bg_weather_night);
            imgBg2.setBackgroundResource(R.drawable.bg_weather_day);
            weatherRes = R.drawable.bg_weather_day;
        }
        index = 0;
    }

    protected void changeBg(){
        changeBg(null);
    }

    protected void changeBg(YunWeather cityWeather){
        if(isAnimation)return;
        int weatherCode = NONE;
        boolean needChanged = false;
        if(cityWeather != null && cityWeather.getCondition() != null){
            YunCondition baseCondition = cityWeather.getCondition();
            if(baseCondition.getWea().contains("雨")){
                if(baseCondition.getWea().contains("大")){
                    weatherCode = RAIN_L;
                }
                else if(baseCondition.getWea().contains("中")){
                    weatherCode = RAIN_M;
                }
                else{
                    weatherCode = RAIN_S;
                }
                needChanged = weatherRes != R.drawable.bg_weather_rain;
                if(needChanged)weatherRes = R.drawable.bg_weather_rain;
            }
            else if(baseCondition.getWea().contains("雪")){
                if(baseCondition.getWea().contains("大")){
                    weatherCode = SNOW_L;
                }else{
                    weatherCode = SNOW_S;
                }
                needChanged = weatherRes != R.drawable.bg_weather_rain;
                if(needChanged)weatherRes = R.drawable.bg_weather_rain;
            }
            else if(baseCondition.getWea().contains("雾霾")){
                needChanged = weatherRes != R.drawable.bg_weather_smog;
                if(needChanged)weatherRes = R.drawable.bg_weather_smog;
            }
            else if(baseCondition.getWea().contains("沙尘")){
                needChanged = weatherRes != R.drawable.bg_weather_dust;
                if(needChanged)weatherRes = R.drawable.bg_weather_dust;
            }
            logInfo("Bg baseCondition : "+baseCondition.getWea());
        }
        if(weatherCode == NONE){
            if(isNight()){
                needChanged = weatherRes != R.drawable.bg_weather_night;
                if(needChanged)weatherRes = R.drawable.bg_weather_night;
            }else{
                needChanged = weatherRes != R.drawable.bg_weather_day;
                if(needChanged)weatherRes = R.drawable.bg_weather_day;
            }
        }
        logInfo("weatherCode : "+weatherCode+" , needChanged : "+needChanged);
        if(needChanged){
            if(isReverse){
                imgBg2.setBackgroundResource(weatherRes);
            }else{
                imgBg1.setBackgroundResource(weatherRes);
            }
            YoYo.with(isReverse? Techniques.FadeIn:Techniques.FadeOut)
                    .duration(1000)
                    .playOn(imgBg2);
            YoYo.with(isReverse?Techniques.FadeOut:Techniques.FadeIn)
                    .duration(1000)
                    .withListener(animatorListener)
                    .playOn(imgBg1);
            showWeatherEffect(weatherCode);
            logError("改变背景");
        }
    }

    protected Animator.AnimatorListener animatorListener = new Animator.AnimatorListener(){

        @Override
        public void onAnimationStart(Animator animation){
            isAnimation = true;
        }

        @Override
        public void onAnimationEnd(Animator animation){
            if(checkActivity())return;
//            index = (index+1)%(bgRes.length);
//            if(isReverse){
//                imgBg1.setBackgroundResource(bgRes[index]);
//            }else{
//                imgBg2.setBackgroundResource(bgRes[index]);
//            }
//            weatherRes = bgRes[index];
            isReverse = !isReverse;
            isAnimation = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private boolean checkActivity(){
        return getContext() == null || getActivity().isDestroyed();
    }

    private boolean isNight(){
        try {
            date.setTime(System.currentTimeMillis());
            String timeString = simpleDateFormat.format(date);
            Log.e("ijimu"," , timeString : "+timeString);
            Date targetTime = simpleDateFormat.parse(timeString);
	        Log.e("ijimu","是否是晚上 : "+(targetTime.getTime() < nightDate2.getTime() || targetTime.getTime() > nightDate1.getTime()));
            return (targetTime.getTime() < nightDate2.getTime() || targetTime.getTime() > nightDate1.getTime());
        }catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }

    private void showWeatherEffect(int weatherCode){
        switch (weatherCode){
            case NONE:
                weatherEffectView.setWeatherData(PrecipType.CLEAR);
                logInfo("晴空");
                break;
            case RAIN_S:
                weatherEffectView.setWeatherData(PrecipType.RAIN);
                weatherEffectView.setScaleFactor(1.9f);
                weatherEffectView.setSpeed(1600);
                weatherEffectView.setFadeOutPercent(0.7f);
                weatherEffectView.setAngle(-21);
                weatherEffectView.setEmissionRate(120);
                logInfo("小雨");
                break;
            case RAIN_M:
                weatherEffectView.setWeatherData(PrecipType.RAIN);
                weatherEffectView.setScaleFactor(2.8f);
                weatherEffectView.setSpeed(1800);
                weatherEffectView.setFadeOutPercent(0.7f);
                weatherEffectView.setAngle(-21);
                weatherEffectView.setEmissionRate(160);
                logInfo("中雨");
                break;
            case RAIN_L:
                weatherEffectView.setWeatherData(PrecipType.RAIN);
                weatherEffectView.setScaleFactor(3.0f);
                weatherEffectView.setSpeed(2000);
                weatherEffectView.setFadeOutPercent(0.9f);
                weatherEffectView.setAngle(-21);
                weatherEffectView.setEmissionRate(248);
                logInfo("大雨");
                break;
            case SNOW_S:
                weatherEffectView.setWeatherData(PrecipType.SNOW);
                weatherEffectView.setScaleFactor(2.1f);
                weatherEffectView.setSpeed(300);
                weatherEffectView.setFadeOutPercent(0.8f);
                weatherEffectView.setAngle(-23);
                weatherEffectView.setEmissionRate(10);
                logInfo("小雪");
                break;
            case SNOW_L:
                weatherEffectView.setWeatherData(PrecipType.SNOW);
                weatherEffectView.setScaleFactor(2.5f);
                weatherEffectView.setSpeed(350);
                weatherEffectView.setFadeOutPercent(0.8f);
                weatherEffectView.setAngle(-33);
                weatherEffectView.setEmissionRate(30);
                logInfo("大雪");
                break;
        }
    }

    /**
     * 大雨
     * fadeOutPercent：0.9
     * scaleFactor : 4.2799997
     * emissionRate : 248.0
     * speed : 1992
     * angle : -21
     *
     * 中雨
     * fadeOutPercent：0.9f
     * scaleFactor : 2.8f
     * emissionRate : 130
     * speed : 2000
     * angle : -26
     *
     * 小雨
     * fadeOutPercent：0.733
     * scaleFactor : 1.89
     * emissionRate : 169.0
     * speed : 1644
     * angle : -25
     *
     * 小雪
     * fadeOutPercent : 0.858
     * angle : -23
     * speed : 300
     * emissionRate : 10.0
     * scaleFactor : 2.12
     *
     * 大雪
     * fadeOutPercent : 0.851
     * angle : -40
     * speed : 501
     * emissionRate : 77.0
     * scaleFactor : 2.4699998
     */
}
