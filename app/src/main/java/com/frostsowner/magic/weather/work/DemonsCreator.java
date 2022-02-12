package com.frostsowner.magic.weather.work;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class DemonsCreator implements JobCreator {

    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag){
//            case WeatherJob.TAG:
//                return new WeatherJob();
            case SignNotificationJob.TAG:
                return new SignNotificationJob();
        }
        return null;
    }
}
