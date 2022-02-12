package com.frostsowner.magic.weather.logic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SimpleDateManager {

    private Map<String, SimpleDateFormat> formatMap;
    private Date date;

    public SimpleDateManager() {
        formatMap = new HashMap<>();
        date = new Date();
    }

    public String transformTime(long time,String format){
        SimpleDateFormat dateFormat = getFormatMap(format);
        return dateFormat.format(getDate(time));
    }

    public String transformTime(String format){
        return transformTime(System.currentTimeMillis(),format);
    }

    public boolean isToDay(long time,String format){
        String today = transformTime(format);
        String targetDay = transformTime(time,format);
        return today.equals(targetDay);
    }

    public SimpleDateFormat getFormatMap(String format){
        SimpleDateFormat dateFormat = formatMap.get(format);
        if(dateFormat == null){
            dateFormat = new SimpleDateFormat(format, Locale.getDefault());
            formatMap.put(format,dateFormat);
        }
        return dateFormat;
    }

    public Date getDate(long time){
        date.setTime(time);
        return date;
    }

    public Date getDate(){
        return getDate(System.currentTimeMillis());
    }

}
