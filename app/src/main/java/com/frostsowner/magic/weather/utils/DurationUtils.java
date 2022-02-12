package com.frostsowner.magic.weather.utils;

public class DurationUtils {

    private static long startTime;
    private static long duration;

    public static long getTime(){
        return System.currentTimeMillis();
    }

    public static long getDuration(){
        if(duration == -1)return 0;
        return duration;
    }

    public static long getStartTime(){
        return startTime;
    }

    public static void reset(){
        startTime = -1;
        duration = -1;
    }

    public static void recordStart(){
        startTime = getTime();
    }

    public static void recordDuration(){
        if(startTime == -1)duration = -1;
        duration = getTime() - startTime;
    }
}
