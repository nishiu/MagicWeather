package com.frostsowner.magic.weather.work;

import com.evernote.android.job.JobRequest;

public class WorkUtils {

    public int startWeatherRefresh(){
        JobRequest.Builder builder = new JobRequest.Builder(WeatherJob.TAG);
        builder.setExecutionWindow(1_000,5_000);
        return builder.build().schedule();
    }
}
