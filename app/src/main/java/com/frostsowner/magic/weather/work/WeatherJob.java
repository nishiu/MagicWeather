package com.frostsowner.magic.weather.work;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.fm.commons.logic.BeanFactory;
import com.frostsowner.magic.weather.domain.weather.YunWeather;
import com.frostsowner.magic.weather.logic.CityManager;
import com.frostsowner.magic.weather.service.WeatherService;

public class WeatherJob extends Job {

    public static final String TAG = "weather_job";

    private CityManager cityManager;
    private JobManager jobManager;

    public WeatherJob(){
        cityManager = BeanFactory.getBean(CityManager.class);
        jobManager = JobManager.instance();
    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        int index = cityManager.getCurrentCityIndex();
        YunWeather cityWeather = cityManager.getTarget(index);
        Intent intent = new Intent(getContext(), WeatherService.class);
        if(cityWeather != null){
            String cityName = cityWeather.getCityName();
            intent.putExtra("cityName",cityName);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            getContext().startForegroundService(intent);
        }else{
            getContext().startService(intent);
        }
        return Result.SUCCESS;
    }
}
